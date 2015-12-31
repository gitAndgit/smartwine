package com.sicao.smartwine.libs;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartline.life.core.DeviceServiceProvider;
import com.smartline.life.iot.IoTService;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 15/7/17.
 */
public class WineCabinetProvider extends DeviceServiceProvider {

    @Override
    public List<String> getSupportModel() {
        List<String> models = new ArrayList<String>();
        models.add("rwc1");
        return models;
    }

    @Override
    public void onDeviceOnline(final XMPPConnection connection, final String bareJid, final String model, final String version) {
        WineCabinetService cabinetService = new WineCabinetService(bareJid,connection);
        cabinetService.load(new IoTService.Callback() {
            @Override
            public void complete(IoTService service) {
                onDeviceStatusChange(connection,bareJid,service,model,version);
            }

            @Override
            public void exception(Exception exception) {

            }
        });
    }

    @Override
    public void onDeviceStatusChange(XMPPConnection connection, String bareJid, IoTService service, String model, String version) {
        if (WineCabinetService.SERVICE_NAEME.equals(service.getServiceName())){
            WineCabinetService cabinet = new WineCabinetService(service);
            ContentValues values = new ContentValues();
            values.put(WineCabinetMetaData.ON,cabinet.isOn());
            values.put(WineCabinetMetaData.LIGHT,cabinet.isLight());
            values.put(WineCabinetMetaData.COMPRESSOR,cabinet.isCompressorWorking());
            values.put(WineCabinetMetaData.TEMP,cabinet.getTemp());
            values.put(WineCabinetMetaData.REAL_TEMP,cabinet.getRealTemp());
            values.put(WineCabinetMetaData.TIMESTAMP,System.currentTimeMillis());

            Cursor c = query(WineCabinetMetaData.CONTENT_URI,null,WineCabinetMetaData.JID+"=?",new String[]{bareJid},null);
            if (c.moveToFirst()){
                long id = c.getLong(c.getColumnIndex(WineCabinetMetaData._ID));
                update(ContentUris.withAppendedId(WineCabinetMetaData.CONTENT_URI,id),values,null,null);
            }else {
                values.put(WineCabinetMetaData.JID,bareJid);
                insert(WineCabinetMetaData.CONTENT_URI,values);
            }
            c.close();
        }
    }

    @Override
    public void onDeviceOffline(XMPPConnection connection, String bareJid, String model, String version) {

    }

    @Override
    protected SQLiteOpenHelper onCreateSQLiteOpenHelper() {
        return new WineCabinetSQLiteOpenHelper(getContext());
    }

    class WineCabinetSQLiteOpenHelper extends SQLiteOpenHelper{

        private static final String DATABASE_NAME = WineCabinetMetaData.TABLE_NAME+".db";
        private static final int DATABASE_VERSION = 2;

        public WineCabinetSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + WineCabinetMetaData.TABLE_NAME
                    +"("
                    + WineCabinetMetaData._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + WineCabinetMetaData.JID +" TEXT UNIQUE,"
                    + WineCabinetMetaData.ON + " INTEGER,"
                    + WineCabinetMetaData.LIGHT + " INTEGER,"
                    + WineCabinetMetaData.COMPRESSOR + " INTEGER,"
                    + WineCabinetMetaData.TEMP + " INTEGER,"
                    + WineCabinetMetaData.REAL_TEMP + " INTEGER,"
                    + WineCabinetMetaData.TIMESTAMP + "  INTEGER"
                    +")";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists "+ WineCabinetMetaData.TABLE_NAME + ";");
            onCreate(db);
        }
    }
}
