package com.sicao.smartwine.libs;


import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.smartline.life.core.BaseContentProvider;
import com.smartline.life.iot.IoTService;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by tom on 15/6/13.
 */
public class DeviceProvider extends BaseContentProvider {

    private static final boolean DEBUG = false;
    private static final String TAG = "DeviceProvider";

    public static final String FIND_DEVICE = "findDevice";

    private ConnectionCreationListener mConnectionCreationListener = new ConnectionCreationListener() {
        @Override
        public void connectionCreated(XMPPConnection connection) {
            Roster roster = Roster.getInstanceFor(connection);
            roster.addRosterListener(new DeviceRosterListener(roster));
        }
    };

    private Context mContext;
    private WifiManager mWiFiManager;

    private List<ScanResult> mAPCache = new ArrayList<ScanResult>();
    @Override
    public boolean onCreate() {
        super.onCreate();
        mContext = getContext();
        XMPPConnectionRegistry.removeConnectionCreationListener(mConnectionCreationListener);
        XMPPConnectionRegistry.addConnectionCreationListener(mConnectionCreationListener);
        return true;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (FIND_DEVICE.equals(method)){
            mWiFiManager.startScan();

        }
        return null;
    }

    @Override
    protected SQLiteOpenHelper onCreateSQLiteOpenHelper() {
        return new DeviceSQLiteOpenHelper(getContext());
    }

    private class DeviceRosterListener implements RosterListener {

        private Roster mRoster;

        public DeviceRosterListener(Roster roster){
            mRoster = roster;
        }

        @Override
        public void entriesAdded(Collection<String> addresses) {
//            Log.e(TAG,"entriesAdded");
//            for (String user : addresses){
//                Log.e(TAG,"entriesAdded "+user);
//            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
//            Log.e(TAG,"entriesUpdated");
//            for (String user : addresses){
//                Log.e(TAG,"entriesUpdated "+user);
//            }
        }

        @SuppressLint("NewApi") @Override
        public void entriesDeleted(Collection<String> addresses) {
            //Log.e(TAG,"entriesDeleted");
            for (String bareJid : addresses){
                Log.e(TAG,"entriesDeleted "+bareJid);
                delete(DeviceMetaData.CONTENT_URI, DeviceMetaData.JID + "=?", new String[]{bareJid});
                Cursor c = query(DeviceMetaData.FIND_CONTENT_URI,null,DeviceMetaData.JID+"=? and "+DeviceMetaData.STATUS+" =? ",new String[]{bareJid,DeviceMetaData.Status.WAN.toString()},null,null);
                if (c.moveToFirst()){
                    long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
                    ContentValues value = new ContentValues();
                    value.put(DeviceMetaData.STATUS,DeviceMetaData.Status.LAN.toString());
                    update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI,id),value,null,null);
                }
                c.close();
            }
        }

        @SuppressLint("NewApi") @Override
        public void presenceChanged(Presence presence) {
            //Log.e(TAG,"presenceChanged="+presence.toString());
            final String jid = XmppStringUtils.parseBareJid(presence.getFrom());
            IoTService upgradeService = presence.getExtension(IoTService.ELEMENT_NAME, IoTService.NAMESPACE);
            if (upgradeService != null && "upgrade".equals(upgradeService.getServiceName())){
                String type = upgradeService.getString("type");
                boolean status = upgradeService.getBoolean("status");
                if ("finish".equals(type) && status){
                    String version = upgradeService.getString("upgrade_version");
                    ContentValues value = new ContentValues();
                    value.put(DeviceMetaData.VERSION,version);
                    update(DeviceMetaData.CONTENT_URI, value, DeviceMetaData.JID + "=?", new String[]{jid});
                }
            }else {
                ContentValues value = new ContentValues();
                value.put(DeviceMetaData.ONLINE,presence.isAvailable());
                update(DeviceMetaData.CONTENT_URI,value,DeviceMetaData.JID+"=?",new String[]{jid});
            }
            if (presence.isAvailable()){
                Cursor c = query(DeviceMetaData.FIND_CONTENT_URI,null,DeviceMetaData.JID+"=? and "+DeviceMetaData.STATUS+" is not ? ",new String[]{jid,DeviceMetaData.Status.WAN.toString()},null,null);
                if (c.moveToFirst()){
                    long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));

                    ContentValues value = new ContentValues();

                    Cursor cursor = query(DeviceMetaData.CONTENT_URI,null,DeviceMetaData.JID+"=?", new String[]{jid},null);
                    if (cursor.moveToFirst()){
                        value.put(DeviceMetaData.NAME,c.getString(c.getColumnIndex(DeviceMetaData.NAME)));
                    }
                    cursor.close();
                    value.put(DeviceMetaData.STATUS,DeviceMetaData.Status.WAN.toString());

                    update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI,id),value,null,null);
                }
                c.close();
            }else {
                //Log.e(TAG,"delete for new device jid="+jid);
                Cursor c = query(DeviceMetaData.FIND_CONTENT_URI,null,DeviceMetaData.JID+"=? and "+DeviceMetaData.STATUS+" is not ? ",new String[]{jid,DeviceMetaData.Status.ACCESSPOINT.toString()},null,null);
                if (c.moveToFirst()){
                    //long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
                    final String host = c.getString(c.getColumnIndex(DeviceMetaData.IPV4));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!Util.isReachableHost(host,6000)){
                                delete(DeviceMetaData.FIND_CONTENT_URI, DeviceMetaData.JID + "=? and " + DeviceMetaData.STATUS + " is not ?", new String[]{jid, DeviceMetaData.Status.ACCESSPOINT.toString()});
                            }
                        }
                    }).start();
                }
                c.close();
                //delete(DeviceMetaData.FIND_CONTENT_URI, DeviceMetaData.JID + "=? and " + DeviceMetaData.STATUS + " is not ?", new String[]{jid, DeviceMetaData.Status.ACCESSPOINT.toString()});
            }
        }
    }
    private class DeviceSQLiteOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = DeviceMetaData.TABLE_NAME+".db";
        private static final int DATABASE_VERSION = 2;

        public DeviceSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String deviceSql = "CREATE TABLE IF NOT EXISTS "
                    + DeviceMetaData.TABLE_NAME
                    +"("
                    + DeviceMetaData._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DeviceMetaData.JID +" TEXT UNIQUE,"
                    + DeviceMetaData.TYPE + " TEXT,"
                    + DeviceMetaData.NAME + " TEXT,"
                    + DeviceMetaData.NICK + " TEXT,"
                    + DeviceMetaData.MODEL + " TEXT,"
                    + DeviceMetaData.ONLINE + " INTEGER,"
                    + DeviceMetaData.VERSION + " TEXT,"
                    + DeviceMetaData.RESOURCE + " TEXT,"
                    + DeviceMetaData.MAN + " TEXT,"
                    + DeviceMetaData.CATEGORY + " TEXT,"
                    + DeviceMetaData.GROUP + " TEXT,"
                    + DeviceMetaData.MAC + " TEXT,"
                    + DeviceMetaData.IPV4 + " TEXT,"
                    + DeviceMetaData.IPV6 + " TEXT,"
                    + DeviceMetaData.PORT + " INTEGER,"
                    + DeviceMetaData.SSID + " TEXT,"
                    + DeviceMetaData.BSSID + " TEXT,"
                    + DeviceMetaData.WIFIPASSWD + " TEXT,"
                    + DeviceMetaData.STATUS + " TEXT,"
                    + DeviceMetaData.AVATAR + " TEXT,"
                    + DeviceMetaData.OS + " TEXT"
                    +")";
            db.execSQL(deviceSql);

            String localDeviceSql = "CREATE TABLE IF NOT EXISTS "
                    + DeviceMetaData.FIND_TABLE_NAME
                    +"("
                    + DeviceMetaData._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DeviceMetaData.JID +" TEXT UNIQUE,"
                    + DeviceMetaData.NAME + " TEXT,"
                    + DeviceMetaData.MODEL + " TEXT,"
                    + DeviceMetaData.MAN + " TEXT,"
                    + DeviceMetaData.TYPE + "  TEXT,"
                    + DeviceMetaData.STATUS + "  TEXT,"
                    + DeviceMetaData.MAC + "  TEXT,"
                    + DeviceMetaData.SSID + "  TEXT,"
                    + DeviceMetaData.BSSID + "  TEXT,"
                    + DeviceMetaData.IPV4 + "  TEXT,"
                    + DeviceMetaData.IPV6 + "  TEXT,"
                    + DeviceMetaData.PORT + "  INTEGER,"
                    + DeviceMetaData.RESOURCE + "  TEXT"
                    +")";
            db.execSQL(localDeviceSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists "+ DeviceMetaData.TABLE_NAME + ";");
            db.execSQL("drop table if exists "+ DeviceMetaData.FIND_TABLE_NAME + ";");
            onCreate(db);
        }
    }

}
