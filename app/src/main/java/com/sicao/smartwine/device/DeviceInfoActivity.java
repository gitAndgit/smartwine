package com.sicao.smartwine.device;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.libs.DeviceMetaData;
import com.sicao.smartwine.libs.WineCabinetMetaData;
import com.sicao.smartwine.libs.WineCabinetService;
import com.sicao.smartwine.device.entity.ModelEntity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.UserInfoUtil;
import com.smartline.life.core.XMPPManager;
import com.smartline.life.device.Device;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StreamError;

import java.util.ArrayList;

/**
 * 酒柜信息管理页面
 */
public class DeviceInfoActivity extends BaseActivity implements View.OnClickListener {
    //酒柜设置
    TextView wineSetting;
    //酒柜灯光
    ImageView wineLight;
    //设置连接
    RelativeLayout settingConnect;
    //美酒商城
    RelativeLayout wineShop;
    //品酒行动
    RelativeLayout drinkWine;
    //美酒提醒
    RelativeLayout tiXing;
    //意见反馈
    RelativeLayout yijian;
    // 酒柜内温度,工作模式,设置温度,连接状态,设备名称,酒柜电灯是否已经打开,工作模式.
    TextView mRealTemp, mWorkModel_1, mSetTemp, mIsonline, mDeviceName;
    // 设备信息
    WineCabinetService mCabinet;
    Uri mCabinetUri;
    // 设置的温度
    int setTemp = -1;
    // 设备灯是否已经点亮
    boolean light = false;
    // 设备
    Device mDevice;
    // XMPP链接
    XMPPConnection mConnection;
    // 链接监听
    AbstractConnectionListener mConnectionListener;


    @Override
    public String setTitle() {
        return getString(R.string.title_activity_device_info);
    }

    @Override
    public int setView() {
        return R.layout.activity_device_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setNavigationIcon(null);
        //右侧头像
        rightIcon.setImageResource(R.drawable.ic_back);
        //
        wineSetting = (TextView) findViewById(R.id.textView13);
        wineSetting.setOnClickListener(this);
        wineLight = (ImageView) findViewById(R.id.imageView3);
        wineLight.setOnClickListener(this);
        settingConnect = (RelativeLayout) findViewById(R.id.setting_connect);
        settingConnect.setOnClickListener(this);
        wineShop = (RelativeLayout) findViewById(R.id.wineShop);
        wineShop.setOnClickListener(this);
        drinkWine = (RelativeLayout) findViewById(R.id.drinkWine);
        drinkWine.setOnClickListener(this);
        tiXing = (RelativeLayout) findViewById(R.id.tiXing);
        tiXing.setOnClickListener(this);
        yijian = (RelativeLayout) findViewById(R.id.yijian);
        yijian.setOnClickListener(this);
        //
        mIsonline = (TextView) findViewById(R.id.online);
        mRealTemp = (TextView) findViewById(R.id.textView11);
        mSetTemp = (TextView) findViewById(R.id.set_temp);
        //
        mWorkModel_1 = (TextView) findViewById(R.id.wine_mode);
        //登录到智捷通
        ApiClient.login(DeviceInfoActivity.this, getString(R.string.xmpp_host), getResources().getInteger(R.integer.xmpp_port),
                getString(R.string.service), getString(R.string.source), "sicao-" + UserInfoUtil.getUID(this),
                "sicao12345678", new ApiCallBack() {
                    @Override
                    public void response(Object object) {
                        Log.i("huahua", "智捷通登录OK-----");
                        UserInfoUtil.setLogin(DeviceInfoActivity.this, true);
                        //设备连接ID
                        setConnectID((Integer) object);
                        // XMPP链接
                        mConnection = ((XMPPManager) getApplicationContext()
                                .getSystemService(
                                        XMPPManager.XMPP_SERVICE))
                                .getXMPPConnection(mConnectID);
                        //
                        mConnection
                                .addConnectionListener(mConnectionListener);
                        //获取设备列表,取第一个为默认勾选设备
                        ApiClient.getDeviceList(DeviceInfoActivity.this, new ApiListCallBack() {
                            @Override
                            public <T> void response(ArrayList<T> list) {
                                if (list.size() <= 0) {
                                    Toast.makeText(DeviceInfoActivity.this, "您还没有添加酒柜设备", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                selectDevice((Device) list.get(0));
                            }
                        }, new ApiException() {
                            @Override
                            public void error(String error) {
                                Log.i("huahua", "获取设备列表" +
                                        "失败-----" + error);
                                Toast.makeText(DeviceInfoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }, new ApiException() {
                    @Override
                    public void error(String error) {
                        Log.i("huahua", "智捷通登录失败-----" + error);
                        Toast.makeText(DeviceInfoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });
        // 监听连接的状态
        mConnectionListener = new AbstractConnectionListener() {
            @Override
            public void connectionClosed() {
                super.connectionClosed();

            }

            @Override
            public void reconnectionFailed(Exception e) {
                super.reconnectionFailed(e);
                if (e instanceof XMPPException.StreamErrorException) {
                    XMPPException.StreamErrorException xmppEx = (XMPPException.StreamErrorException) e;
                    StreamError error = xmppEx.getStreamError();
                    if (StreamError.Condition.conflict == error
                            .getCondition()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (-1 != mConnectID) {
                                    ((XMPPManager) getApplicationContext()
                                            .getSystemService(
                                                    XMPPManager.XMPP_SERVICE))
                                            .connect(mConnectID);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                // 当链接发生错误关闭时会调用次方法
                if (e instanceof XMPPException.StreamErrorException) {
                    XMPPException.StreamErrorException xmppEx = (XMPPException.StreamErrorException) e;
                    StreamError error = xmppEx.getStreamError();
                    if (StreamError.Condition.conflict == error
                            .getCondition()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (-1 != mConnectID) {
                                    ((XMPPManager) getApplicationContext()
                                            .getSystemService(
                                                    XMPPManager.XMPP_SERVICE))
                                            .connect(mConnectID);
                                }
                            }
                        });
                    }
                }
            }
        };
        // 监听数据库中设备列表的数据变化
        getContentResolver().registerContentObserver(
                DeviceMetaData.CONTENT_URI, true,
                mContentObserver);
    }

    /**
     * 选择某一台设备进行监控
     *
     * @param device
     */
    public void selectDevice(Device device) {
        getContentResolver().unregisterContentObserver(mContentObservera);
        // 设备后台编号
        mDeviceID = mDevice.getJid();
        setDeviceID(mDeviceID);
        // 设备名称
        String name = mDevice.getName();
        // 设备监控
        if (null != mCabinet) {
            mCabinet = null;
        }
        mCabinet = new WineCabinetService(mDeviceID, mConnection);
        Cursor c = getContentResolver().query(WineCabinetMetaData.CONTENT_URI,
                null, WineCabinetMetaData.JID + "=?",
                new String[]{mDeviceID}, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(WineCabinetMetaData._ID));
            mCabinetUri = ContentUris.withAppendedId(
                    WineCabinetMetaData.CONTENT_URI, id);
        } else {
            ContentValues values = new ContentValues();
            values.put(WineCabinetMetaData.JID, mDeviceID);
            mCabinetUri = getContentResolver().insert(
                    WineCabinetMetaData.CONTENT_URI, values);
        }
        // 监听数据库中该行数据的变化
        getContentResolver().registerContentObserver(mCabinetUri, true,
                mContentObservera);
        updateCabinetView();
        // 获取该设备的工作模式
        if (!"".equals(mDeviceID)) {
            ApiClient.configWorkMode(this, UserInfoUtil.getUID(this), mDeviceID,
                    "", "", "select", new ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            try {
                                ModelEntity entity = (ModelEntity) object;
                                if (null != mCabinet) {
                                    mWorkModel_1.setText(entity
                                            .getWork_model_name());
                                    // 酒柜的温度跟后台设置的模式温度不一致,此处以服务器为准
                                    if (null != mCabinet
                                            && mCabinet.getTemp() != Integer.parseInt(entity
                                            .getWork_model_demp())) {
                                        mCabinet.setTemp(Integer.parseInt(entity
                                                .getWork_model_demp()));
                                        mCabinet.update();
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }, null);
        }
    }

    /***
     * 监控设备行信息的变化
     */
    private ContentObserver mContentObservera = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updateCabinetView();
        }
    };
    /***
     * 监控设备列表的变化
     */
    private ContentObserver mContentObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 2,抓取数据库中的设备列表,默认选择打开第一台设备
            ApiClient.getDeviceList(getApplicationContext(),
                    new ApiListCallBack() {
                        @Override
                        public <T> void response(ArrayList<T> list) {
                            @SuppressWarnings("unchecked")
                            ArrayList<Device> mList = (ArrayList<Device>) list;
                            if ("".equals(mDeviceID)) {
                                if (mList.isEmpty()) {
                                    // 未添加设备
                                } else {
                                    // 选取第一台设备作为默认
                                    mDevice = mList.get(0);
                                    // 3,设置该台设备的数据库监听,注意切换设备时的需要切换监控
                                    selectDevice(mDevice);
                                }
                            } else {
                                for (int i = 0; i < mList.size(); i++) {
                                    if (mList.get(i).getJid()
                                            .equals(mDeviceID)) {
                                        mDevice = mList.get(i);
                                        selectDevice(mDevice);
                                    }
                                }
                            }
                        }
                    }, new ApiException() {
                        @Override
                        public void error(String error) {
                        }
                    });
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.textView13://酒柜设置
                if (mConnectID != -1)
                    startActivity(new Intent(this, SmartSetActivity.class));
                break;
            case R.id.imageView3://酒柜灯开关
                if (mConnectID != -1 && !"".equals(mDeviceID)) {
                    if (null != mCabinet)
                        if (mCabinet.isLight())
                            mCabinet.setLight(false);
                        else
                            mCabinet.setLight(true);
                    mCabinet.update();
                }
                break;
            case R.id.setting_connect://设置连接
                if (mConnectID != -1) {
                    startActivity(new Intent(this, ConfigActivity.class).putExtra("connectid", mConnectID));
                }
                break;
            case R.id.wineShop://美酒商城
                Toast.makeText(DeviceInfoActivity.this, "美酒商城", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drinkWine://品酒行动
                Toast.makeText(DeviceInfoActivity.this, "品酒行动", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tiXing://美酒提醒
                Toast.makeText(DeviceInfoActivity.this, "美酒提醒", Toast.LENGTH_SHORT).show();
                break;
            case R.id.yijian://意见反馈
                Toast.makeText(DeviceInfoActivity.this, "意见反馈", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 更新UI
     */
    private void updateCabinetView() {
        Cursor c = getContentResolver().query(mCabinetUri, null, null, null,
                null);
        wineLight.setVisibility(View.VISIBLE);
        if (c.moveToFirst()) {
            boolean on = c.getInt(c.getColumnIndex(WineCabinetMetaData.ON)) == 1;
            if (on) {
                mIsonline.setText("正常");
            } else {
                mIsonline.setText("未连接");
            }
            light = c.getInt(c.getColumnIndex(WineCabinetMetaData.LIGHT)) == 1;
            if (light) {
                wineLight.setImageResource(R.drawable.light_icon);
            } else {
                wineLight.setImageResource(R.drawable.light_icon_close);
            }
            mRealTemp.setText(c.getInt(c.getColumnIndex(WineCabinetMetaData.REAL_TEMP))
                    + "°");
            setTemp = c.getInt(c.getColumnIndex(WineCabinetMetaData.TEMP));
            mSetTemp.setText(setTemp + "℃");
        }
        c.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getContentResolver().unregisterContentObserver(mContentObservera);
            getContentResolver().unregisterContentObserver(mContentObserver);
            if (mConnection != null) {
                mConnection.removeConnectionListener(mConnectionListener);
                ((XMPPManager) getApplicationContext().getSystemService(
                        XMPPManager.XMPP_SERVICE)).disconnect(mConnectID);
            }
            getContentResolver().delete(mCabinetUri, null, null);
            getContentResolver().delete(DeviceMetaData.CONTENT_URI, null, null);
        } catch (Exception e) {
        }
    }
}
