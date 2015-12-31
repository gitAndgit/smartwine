package com.sicao.smartwine.device;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.libs.WineCabinetMetaData;
import com.sicao.smartwine.libs.WineCabinetService;
import com.sicao.smartwine.widget.BottomPopupWindow;
import com.smartline.life.core.XMPPManager;

import org.jivesoftware.smack.XMPPConnection;

/***
 * 酒柜设置
 */
public class SmartSetActivity extends BaseActivity implements View.OnClickListener {
    //酒柜名称
    EditText wineName;
    String smartWineName = "";
    //工作模式名称
    TextView mWorkName;
    String workName = "";
    //设置温度
    TextView mWorkTemp;
    //工作模式
    BottomPopupWindow workMode;
    //设置温度
    BottomPopupWindow workTemp;
    // 设备信息
    WineCabinetService mCabinet;
    Uri mCabinetUri;
     //保存按钮
    Button commit;
    //酒柜的名称
    String getSmartWineName="";
    //酒柜的模式名称
    String smartWineMode="";
    //酒柜的模式温度
    String smartModeTemp="";

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.work_mode_name://工作模式
                workMode = new BottomPopupWindow(SmartSetActivity.this);
                workMode.update(getResources().getStringArray(R.array.device_model));
                workMode.showAtLocation(content, Gravity.BOTTOM,
                        0, 0);
                workMode.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                    @Override
                    public void onClick(String value) {
                        mWorkName.setText(value);
                        workMode.dismiss();
                        mCabinet.setLight(false);
                        mCabinet.update();
                    }
                });
                break;
            case R.id.work_mode_temp_name://设置的温度
                workTemp = new BottomPopupWindow(SmartSetActivity.this);
                workTemp.update(getResources().getStringArray(R.array.device_temp));
                workTemp.showAtLocation(content, Gravity.BOTTOM,
                        0, 0);
                workTemp.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                    @Override
                    public void onClick(String value) {
                        mWorkTemp.setText(value);
                        workTemp.dismiss();
                        ;
                        mCabinet.setTemp(Integer.parseInt(value));
                        mCabinet.setLight(true);
                        mCabinet.update();
                    }
                });
                break;
            case R.id.button2://提交
                 //检测酒柜名称是否已经做了修改

                break;
        }
    }

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_smart_set);
    }

    @Override
    protected int setView() {
        return R.layout.activity_smart_set;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        // 设备监控
        if (null != mCabinet) {
            mCabinet = null;
        }
        Cursor c = getContentResolver().query(WineCabinetMetaData.CONTENT_URI,
                null, WineCabinetMetaData.JID + "=?",
                new String[]{getDeviceID()}, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(WineCabinetMetaData._ID));
            mCabinetUri = ContentUris.withAppendedId(
                    WineCabinetMetaData.CONTENT_URI, id);
        } else {
            ContentValues values = new ContentValues();
            values.put(WineCabinetMetaData.JID, getDeviceID());
            mCabinetUri = getContentResolver().insert(
                    WineCabinetMetaData.CONTENT_URI, values);
        }
        Log.i("huahua", "connectID=" + getConnectID() + ";deviceID=" + getDeviceID() + ";uri=" + mCabinetUri);
        // 监听数据库中该行数据的变化
        getContentResolver().registerContentObserver(mCabinetUri, true,
                mContentObservera);
        XMPPConnection mConnection = ((XMPPManager) getApplicationContext()
                .getSystemService(
                        XMPPManager.XMPP_SERVICE))
                .getXMPPConnection(getConnectID());
        mCabinet = new WineCabinetService(getDeviceID(), mConnection);
    }

    public void init() {
        wineName = (EditText) findViewById(R.id.editText);
        mWorkName = (TextView) findViewById(R.id.work_mode_name);
        mWorkTemp = (TextView) findViewById(R.id.work_mode_temp_name);
        mWorkName.setOnClickListener(this);
        mWorkTemp.setOnClickListener(this);
        commit=(Button)findViewById(R.id.button2);
        commit.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObservera);
        getContentResolver().delete(mCabinetUri, null, null);
    }

    /***
     * 监控设备行信息的变化
     */
    private ContentObserver mContentObservera = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
        }
    };
}

