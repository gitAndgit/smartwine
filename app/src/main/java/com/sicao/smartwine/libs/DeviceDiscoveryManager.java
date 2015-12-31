package com.sicao.smartwine.libs;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.smartline.life.core.DapManager;
import com.smartline.life.core.LANServiceManager;
import com.smartline.life.core.LifeApplication;
import com.smartline.life.core.NetServiceManager;
import com.smartline.life.util.WiFiUtil;

import org.jxmpp.util.XmppStringUtils;

import java.util.Map;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

/**
 * Created by tom on 15/6/24.
 */
public class DeviceDiscoveryManager implements DapManager.DapDiscoveryListener,
        NetServiceManager.DiscoveryServiceListener,
        LANServiceManager.LanDiscoveryListener{

    private static final boolean DEBUG = false;
    private static final String TAG = "==discovery==";

    private Context mContext;
    private ContentResolver mResolver;

    private NetServiceManager mServiceManager;
    private DapManager mDapManager;
    private LANServiceManager mLANServiceManager;


    private Handler mHandler = new Handler();

    public DeviceDiscoveryManager(Context context){
        mContext = context;
        mResolver = mContext.getContentResolver();
        LifeApplication application = (LifeApplication)mContext.getApplicationContext();
        mDapManager = (DapManager)application.getSystemService(DapManager.DAP_SERVICE);
        mServiceManager = (NetServiceManager)application.getSystemService(NetServiceManager.NSM_SERVICE);
        mLANServiceManager = (LANServiceManager)application.getSystemService(LANServiceManager.LAN_SERVICE);
    }

    public void startDiscovery(){
        mDapManager.discoverServices("iot://life/.*/.*", this);
        mServiceManager.discoveryService("_presence._tcp.local.", this);
        mLANServiceManager.addListener(this);
        mLANServiceManager.asyncDiscoveryServices();
    }

    public void stopDiscovery(){
        mLANServiceManager.removeListener(this);
        mLANServiceManager.stopDiscovery();
        mServiceManager.stopDiscoveryService(this);
        mServiceManager.stop();
        mDapManager.stopServiceDiscovery(this);
    }

    /**
     * DapDiscoveryListener
     * @param info
     */
    @Override
    public void onDapDiscoveryStarted(DapManager.DAPInfo info) {

    }

    /**
     * DapDiscoveryListener
     * @param info
     */
    @Override
    public void onDapDiscoveryStopped(DapManager.DAPInfo info) {
        mResolver.delete(DeviceMetaData.FIND_CONTENT_URI, DeviceMetaData.STATUS + "=?", new String[]{DeviceMetaData.Status.ACCESSPOINT.toString()});
    }

    /**
     * DapDiscoveryListener
     * @param info
     */
    @Override
    public void onDapInfoFound(DapManager.DAPInfo info) {
        String ssid = info.ssid;
        String jid = XmppStringUtils.completeJidFrom(DeviceUtil.getUDID(ssid), "life.com");
        Cursor c = mResolver.query(DeviceMetaData.FIND_CONTENT_URI, null, DeviceMetaData.JID + "=?", new String[]{jid}, null);
        if (c.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put(DeviceMetaData.SSID, ssid);
            long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
            mResolver.update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI, id),values,null,null);
        }else {
            String model = DeviceUtil.getModel(ssid);
            if (model != null && DeviceUtil.isSupportDevice(model)){
                ContentValues values = new ContentValues();
                values.put(DeviceMetaData.JID,jid);
                values.put(DeviceMetaData.NAME, DeviceUtil.getDeviceName(model));
                values.put(DeviceMetaData.MODEL, model);
                values.put(DeviceMetaData.TYPE, DeviceUtil.getDeviceType(model));
                values.put(DeviceMetaData.SSID, ssid);
                values.put(DeviceMetaData.STATUS, DeviceMetaData.Status.ACCESSPOINT.toString());
                mResolver.insert(DeviceMetaData.FIND_CONTENT_URI, values);
            }
        }
        c.close();
    }

    /**
     * DapDiscoveryListener
     * @param info
     */
    @Override
    public void onDapInfoLost(DapManager.DAPInfo info) {
        String jid = XmppStringUtils.completeJidFrom(DeviceUtil.getUDID(info.ssid), "life.com");
        mResolver.delete(DeviceMetaData.FIND_CONTENT_URI, DeviceMetaData.JID + "=? and " + DeviceMetaData.STATUS + "=?", new String[]{jid, DeviceMetaData.Status.ACCESSPOINT.toString()});
    }


    /**
     * DiscoveryServiceListener
     * @param serviceType
     */
    @Override
    public void onDiscoveryStarted(String serviceType) {
        if (DEBUG)
         Log.e(TAG, "onDiscoveryStarted");
    }

    /**
     * DiscoveryServiceListener
     * @param serviceType
     */
    @Override
    public void onDiscoveryStop(String serviceType) {
        //Log.e(TAG,"=========onDiscoveryStop========");
        mResolver.delete(DeviceMetaData.FIND_CONTENT_URI, DeviceMetaData.STATUS + "=?", new String[]{DeviceMetaData.Status.LAN.toString()});
    }

    /**
     * DiscoveryServiceListener
     * @param serviceEvent
     */
    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {
        if (DEBUG)
            Log.e(TAG, "serviceAdded::" + serviceEvent.getName());
        String jid = XmppStringUtils.completeJidFrom(serviceEvent.getName(), "life.com");
        Cursor c = mResolver.query(DeviceMetaData.FIND_CONTENT_URI, null, DeviceMetaData.JID + "=? and " + DeviceMetaData.STATUS + "=?", new String[]{jid, DeviceMetaData.Status.LAN.toString()}, null);
        if (c.moveToFirst()){
            long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
            mHandler.removeCallbacksAndMessages(id);
            if (DEBUG)
                Log.e(TAG, "TEST service update id=" + id);
        }else {
            if (DEBUG)
                Log.e(TAG, "serviceAdded resolveService::" + serviceEvent.getName());
            mServiceManager.resolveService(serviceEvent.getType(), serviceEvent.getName());
        }
        c.close();
    }

    /**
     * DiscoveryServiceListener
     * @param serviceEvent
     */
    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        if (DEBUG)
            Log.d(TAG, "serviceRemoved::" + serviceEvent.getName());
        String jid = XmppStringUtils.completeJidFrom(serviceEvent.getName(), "life.com");
        Cursor c = mResolver.query(DeviceMetaData.FIND_CONTENT_URI, null, DeviceMetaData.JID + "=?", new String[]{jid}, null);
        if (c.moveToFirst()){
            final long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
            final String ssid = c.getString(c.getColumnIndex(DeviceMetaData.SSID));
            final String host = c.getString(c.getColumnIndex(DeviceMetaData.IPV4));
            if (DEBUG)
                Log.e(TAG, "TEST will remove service id=" + id);
            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    runOnBackgroundThead(new Runnable() {
                        @Override
                        public void run() {
                            if (!Util.isReachableHost(host,3000)) {
                                if (ssid != null && WiFiUtil.hasWiFi(mContext, ssid)) {
                                    ContentValues values = new ContentValues();
                                    values.put(DeviceMetaData.STATUS, DeviceMetaData.Status.ACCESSPOINT.toString());
                                    mResolver.update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI, id), values, null, null);
                                } else {
                                    mResolver.delete(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI, id), null, null);
                                }
                            }
                        }
                    });
                    if (DEBUG)
                        Log.e(TAG, "TEST removed service id=" + id);
                }
            }, id, SystemClock.uptimeMillis() + 3 * 1000);
        }
        c.close();
    }

    /**
     * DiscoveryServiceListener
     * @param serviceEvent
     */
    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {
        if (DEBUG)
            Log.e(TAG,"serviceResolved::"+serviceEvent.getName());
        ServiceInfo info = serviceEvent.getInfo();
        if (info != null && info.hasData()) {
            Log.d(TAG, "serviceResolved::getNiceTextString=" + info.getNiceTextString());
            resolvedDevice(info);
        }
    }

    public void resolvedDevice(ServiceInfo info){
        String model = info.getPropertyString("model");
        if (DEBUG)
            Log.e(TAG,"resolve.model="+model);
        if (model != null && DeviceUtil.isSupportDevice(model)){
            String jid = XmppStringUtils.parseBareJid(info.getPropertyString("jid"));
            ContentValues values = new ContentValues();
            values.put(DeviceMetaData.MODEL,model);
            values.put(DeviceMetaData.TYPE, DeviceUtil.getDeviceType(model));
            values.put(DeviceMetaData.IPV4, info.getInetAddress().getHostAddress());
            if (info.getInet6Address() != null){
                values.put(DeviceMetaData.IPV6, info.getInet6Address().getHostAddress());
            }
            values.put(DeviceMetaData.PORT,info.getPort());

            Cursor c = mResolver.query(DeviceMetaData.CONTENT_URI,null,DeviceMetaData.JID + "=? and "+DeviceMetaData.ONLINE+"=1", new String[]{jid},null);
            if (c.moveToFirst()){
                values.put(DeviceMetaData.NAME, c.getString(c.getColumnIndex(DeviceMetaData.NAME)));
                values.put(DeviceMetaData.STATUS, DeviceMetaData.Status.WAN.toString());
            }else {
                values.put(DeviceMetaData.NAME, DeviceUtil.getDeviceName(model));
                values.put(DeviceMetaData.STATUS, DeviceMetaData.Status.LAN.toString());
            }
            c.close();


            c = mResolver.query(DeviceMetaData.FIND_CONTENT_URI,null,DeviceMetaData.JID + "=?", new String[]{jid},null);
            if (c.moveToFirst()){
                long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
                mResolver.update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI,id),values,null,null);
            }else {
                values.put(DeviceMetaData.JID,jid);
                mResolver.insert(DeviceMetaData.FIND_CONTENT_URI, values);
            }
            c.close();
            Log.e(TAG, values.toString());
        }
    }

    /**
     * LanDiscoveryListener
     * @param info
     */
    @Override
    public void onLanInfoFound(Map<String, String> info) {
        if (DEBUG)
            Log.e(TAG,"onLanInfoFound::"+info.get("jid"));
        String model = info.get("model");
        if (DeviceUtil.isSupportDevice(model)){
            String jid = XmppStringUtils.parseBareJid(info.get("jid"));
            ContentValues values = new ContentValues();
            values.put(DeviceMetaData.MODEL,model);
            values.put(DeviceMetaData.RESOURCE, "IoT");
            values.put(DeviceMetaData.IPV4, info.get("host"));
            values.put(DeviceMetaData.PORT, 5222);
            values.put(DeviceMetaData.TYPE, DeviceUtil.getDeviceType(model));

            Cursor c = mResolver.query(DeviceMetaData.CONTENT_URI,null,DeviceMetaData.JID + "=? and "+DeviceMetaData.ONLINE+"=1", new String[]{jid},null);
            if (c.moveToFirst()){
                values.put(DeviceMetaData.NAME, c.getString(c.getColumnIndex(DeviceMetaData.NAME)));
                values.put(DeviceMetaData.STATUS, DeviceMetaData.Status.WAN.toString());
            }else {
                values.put(DeviceMetaData.NAME, DeviceUtil.getDeviceName(model));
                values.put(DeviceMetaData.STATUS, DeviceMetaData.Status.LAN.toString());
            }
            c.close();

            c = mResolver.query(DeviceMetaData.FIND_CONTENT_URI,null,DeviceMetaData.JID + "=?", new String[]{jid},null);
            if (c.moveToFirst()){
                long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
                mResolver.update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI,id),values,null,null);
            }else {
                values.put(DeviceMetaData.JID,jid);
                mResolver.insert(DeviceMetaData.FIND_CONTENT_URI, values);
            }
            c.close();
            if (DEBUG)
                Log.e(TAG, values.toString());
        }
    }

    /**
     * LanDiscoveryListener
     * @param info
     */
    @Override
    public void onLanInfoLost(Map<String, String> info) {
        String jid = XmppStringUtils.parseBareJid(info.get("jid"));
        if (DEBUG)
            Log.e(TAG,"onLanInfoLost::"+info.get("jid"));
        Cursor c = mResolver.query(DeviceMetaData.FIND_CONTENT_URI, null, DeviceMetaData.JID + "=?", new String[]{jid}, null);
        if (c.moveToFirst()){
            long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
            String ssid = c.getString(c.getColumnIndex(DeviceMetaData.SSID));
            if (ssid != null && WiFiUtil.hasWiFi(mContext, ssid)){
                ContentValues values = new ContentValues();
                values.put(DeviceMetaData.STATUS,DeviceMetaData.Status.ACCESSPOINT.toString());
                mResolver.update(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI, id), values, null, null);
            }else {
                mResolver.delete(ContentUris.withAppendedId(DeviceMetaData.FIND_CONTENT_URI,id),null,null);
            }
        }
        c.close();
    }

    public void runOnBackgroundThead(Runnable runnable){
        new Thread(runnable).start();
    }

}
