package com.sicao.smartwine.device;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
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
import com.sicao.smartwine.activity.DeviceListActivity;
import com.sicao.smartwine.activity.SettingsActivity;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.entity.ModelEntity;
import com.sicao.smartwine.libs.DeviceMetaData;
import com.sicao.smartwine.libs.WineCabinetMetaData;
import com.sicao.smartwine.libs.WineCabinetService;
import com.sicao.smartwine.shop.IndexActivity;
import com.sicao.smartwine.user.UserInfoActivity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.UserInfoUtil;
import com.smartline.life.device.Device;

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
    ModelEntity entity;

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
        setStatus(true);
        leftIcon.setVisibility(View.GONE);
        //右侧头像
        //rightIcon.setVisibility(View.VISIBLE);
        //
        wineSetting = (TextView) findViewById(R.id.textView13);
        wineSetting.setOnClickListener(this);
        wineLight = (ImageView) findViewById(R.id.imageView3);
        wineLight.setOnClickListener(this);
        settingConnect = (RelativeLayout) findViewById(R.id.setting_connect);
        settingConnect.setOnClickListener(this);
        wineShop = (RelativeLayout) findViewById(R.id.wineShop);
        wineShop.setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        findViewById(R.id.my_message).setOnClickListener(this);
        findViewById(R.id.find).setOnClickListener(this);//发现
        findViewById(R.id.my_wines).setOnClickListener(this);
        findViewById(R.id.management_order).setOnClickListener(this);

        //
        mIsonline = (TextView) findViewById(R.id.online);
        mRealTemp = (TextView) findViewById(R.id.textView11);
        mSetTemp = (TextView) findViewById(R.id.set_temp);
        //
        mWorkModel_1 = (TextView) findViewById(R.id.wine_mode);
        //获取用户信息
       /* ApiClient.getUserInfo(this, UserInfoUtil.getUID(this), UserInfoUtil.getToken(this), new ApiCallBack() {
            @Override
            public void response(Object object) {
                rightIcon.setImageURI(Uri.parse(((PtjUserEntity) object).getAvatar()));
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
        //头像点击事件
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceInfoActivity.this, UserInfoActivity.class));
            }
        });*/
        //登录到智捷通
        if(UserInfoUtil.getLogin(this)){
            LifeClient.login(DeviceInfoActivity.this, getString(R.string.xmpp_host), getResources().getInteger(R.integer.xmpp_port),
                    getString(R.string.service), getString(R.string.source), "sicao-" + UserInfoUtil.getUID(this),
                    "sicao12345678", new com.sicao.smartwine.api.LifeClient.ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            Log.i("huahua", "智捷通登录OK-----");
                            UserInfoUtil.setLogin(DeviceInfoActivity.this, true);
                        }
                    }, new com.sicao.smartwine.api.LifeClient.ApiException() {
                        @Override
                        public void error(String error) {
                            Log.i("huahua", "智捷通登录失败-----" + error);
                            Toast.makeText(DeviceInfoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
        mCabinet = new WineCabinetService(mDeviceID, LifeClient.getConnection());
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
        Log.i("huahua", "uri----->" + mCabinetUri);
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
                                entity = (ModelEntity) object;
                                if (null != mCabinet && null != entity && !"".equals(entity.getWork_model_name())) {
                                    mWorkModel_1.setText(entity
                                            .getWork_model_name());
                                    // 酒柜的温度跟后台设置的模式温度不一致,此处以需以酒柜服务器为准，即将设备的工作模式修改为手动模式,并调整该模式的温度(调整葡萄集服务器)
                                    if (null != mCabinet
                                            && mCabinet.getTemp() != Integer.parseInt(entity
                                            .getWork_model_demp())) {
                                        Log.i("huahua", "real--" + mCabinet.getRealTemp() + "---temp--" + mCabinet.getTemp() + "---model--" + entity.getWork_model_demp());
                                        ApiClient.configWorkMode(DeviceInfoActivity.this, UserInfoUtil.getUID(DeviceInfoActivity.this),
                                                mDeviceID, "手动", mCabinet.getTemp() + "", "update", null, null);
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
            LifeClient.getDeviceList(getApplicationContext(),
                    new com.sicao.smartwine.api.LifeClient.ApiListCallBack() {
                        @Override
                        public <T> void response(ArrayList<T> list) {
                            @SuppressWarnings("unchecked")
                            ArrayList<Device> mList = (ArrayList<Device>) list;
                            if (!mList.isEmpty()) {
                                //已经有设备
                                if ("".equals(getDeviceID())) {
                                    //有设备信息存储，但是未选择设备
                                    mDevice = mList.get(0);
                                    selectDevice(mDevice);
                                } else {
                                    //当前有设备在展示
                                    for (Device device : mList) {
                                        if (getDeviceID().equals(device.getJid())) {
                                            mDevice = device;
                                            selectDevice(mDevice);
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                    }, new com.sicao.smartwine.api.LifeClient.ApiException() {
                        @Override
                        public void error(String error) {
                            Log.i("huahua", "设备列表---错误" + error);
                        }
                    });
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.textView13://酒柜设置
                if (LifeClient.getConnectionId() != -1 && !"".equals(mDeviceID)) {
                    String smartWineMode = "";
                    if (null == entity) {
                        smartWineMode = "";
                    } else {
                        if (null == entity.getWork_model_name()) {
                            smartWineMode = "";
                        } else {
                            smartWineMode = entity.getWork_model_name();
                        }
                    }
                    startActivityForResult(new Intent(this, SmartSetActivity.class).
                            putExtra("smartWineName", mDevice.getName()).
                            putExtra("smartWineMode", smartWineMode).
                            putExtra("smartModeTemp", realTemp + "").
                            putExtra("CURRENT_DEVICE_ID", mDevice.getJid()), 10088);
                }
                break;

            case R.id.imageView3://酒柜灯开关
                if (LifeClient.getConnectionId() != -1 && !"".equals(getDeviceID())) {
                    Cursor c = getContentResolver().query(mCabinetUri, null, null, null,
                            null);
                    if (c.moveToNext()) {
                        light = c.getInt(c.getColumnIndex(WineCabinetMetaData.LIGHT)) == 1;
                        Log.i("huahua","酒柜灯---->"+light);
                        if (null != mCabinet) {
                            if (light) {
                                Toast.makeText(DeviceInfoActivity.this, "正在关闭设备灯", Toast.LENGTH_LONG).show();
                                mCabinet.setLight(false);
                            } else {
                                Toast.makeText(DeviceInfoActivity.this, "正在启动设备灯", Toast.LENGTH_LONG).show();
                                mCabinet.setLight(true);
                            }
                            mCabinet.update();
                        }
                    }
                    c.close();
                }
                break;
            case R.id.setting_connect://设置连接
                //如果wifi没有连接
                if(getWifistatus()){
                    if (null != mDevice) {
                        startActivityForResult(new Intent(this, DeviceListActivity.class), 10090);
                    } else if (LifeClient.getConnectionId() != -1) {
                        WifiManager wifi= (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        String wifiName=wifi.getConnectionInfo().getSSID();
                        startActivityForResult(new Intent(this, ConfigActivity.class).putExtra("wifiName",wifiName), 10089);
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("请求打开wifi");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WifiManager wifi= (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(true);
                            dialog.dismiss();;
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();;
                        }
                    });
                    builder.create().show();;
                }
                break;
            case R.id.wineShop://美酒商城
                startActivity(new Intent(this, IndexActivity.class));
                break;
            case R.id.setting://设置
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.my_message:
                startActivity(new Intent(DeviceInfoActivity.this, UserInfoActivity.class));
                break;
            case R.id.find:
                Toast.makeText(this, "正在开发", Toast.LENGTH_SHORT).show();
                break;
            case R.id.management_order:
                Toast.makeText(this, "正在开发", Toast.LENGTH_SHORT).show();
                break;
            case R.id.my_wines:
                Toast.makeText(this, "正在开发", Toast.LENGTH_SHORT).show();
                break;
            /*case R.id.tiXing://美酒提醒
                startActivity(new Intent(this, ScrollingActivity.class));
                break;*/
            /*case R.id.yijian://意见反馈
                startActivity(new Intent(this, FeedBackActivity.class));
                break;*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10088) {//修改设备配置
                updateCabinetView();
                // 获取该设备的工作模式
                if (!"".equals(mDeviceID)) {
                    ApiClient.configWorkMode(this, UserInfoUtil.getUID(this), mDeviceID,
                            "", "", "select", new ApiCallBack() {
                                @Override
                                public void response(Object object) {
                                    try {
                                        entity = (ModelEntity) object;
                                        if (null != mCabinet && null != entity && !"".equals(entity.getWork_model_name())) {
                                            mWorkModel_1.setText(entity
                                                    .getWork_model_name());
                                            // 酒柜的温度跟后台设置的模式温度不一致,此处以需以酒柜服务器为准，即将设备的工作模式修改为手动模式,并调整该模式的温度(调整葡萄集服务器)
                                            if (null != mCabinet
                                                    && mCabinet.getTemp() != Integer.parseInt(entity
                                                    .getWork_model_demp())) {
                                                Log.i("huahua", "real--" + mCabinet.getRealTemp() + "---temp--" + mCabinet.getTemp() + "---model--" + entity.getWork_model_demp());
                                                ApiClient.configWorkMode(DeviceInfoActivity.this, UserInfoUtil.getUID(DeviceInfoActivity.this),
                                                        mDeviceID, "手动", mCabinet.getTemp() + "", "update", null, null);
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }, null);
                }
            } else if (requestCode == 10089 || requestCode == 10090) {//配置新设备或者用户从设备列表选取了新设备
                final String jid = data.getExtras().getString("new_device_id");
                //获取设备列表,取设备ID相同的设备信息
                LifeClient.getDeviceList(DeviceInfoActivity.this, new com.sicao.smartwine.api.LifeClient.ApiListCallBack() {
                    @Override
                    public <T> void response(ArrayList<T> list) {
                        if (list.size() <= 0) {
                            return;
                        }
                        ArrayList<Device> mList = (ArrayList<Device>) list;
                        for (Device device : mList) {
                            if (jid.equals(device.getJid())) {
                                mDevice = device;
                                selectDevice(device);
                            }
                        }
                    }
                }, new com.sicao.smartwine.api.LifeClient.ApiException() {
                    @Override
                    public void error(String error) {
                        Log.i("huahua", "获取设备列表" +
                                "失败-----" + error);
                        Toast.makeText(DeviceInfoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (resultCode == RESULT_CANCELED) {//设备重置
            if (null != data && null != data.getExtras() && data.getExtras().containsKey("reset_device")) {
                // 没有设备
                mDeviceID = "";
                setDeviceID("");
                mIsonline.setText("未连接");
                wineLight.setImageResource(R.drawable.light_icon_close);
                mRealTemp.setText("0℃");
                mSetTemp.setText("0℃");
                mWorkModel_1.setText("未设置");
                Toast.makeText(getApplicationContext(), "设备已重置",
                        Toast.LENGTH_LONG).show();
                // 抓取数据库中的设备列表,默认选择打开第一台设备
                LifeClient.getDeviceList(getApplicationContext(),
                        new com.sicao.smartwine.api.LifeClient.ApiListCallBack() {
                            @Override
                            public <T> void response(ArrayList<T> list) {
                                @SuppressWarnings("unchecked")
                                ArrayList<Device> mList = (ArrayList<Device>) list;
                                if ("".equals(mDeviceID)) {
                                    if (!mList.isEmpty()) {
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
                        }, new com.sicao.smartwine.api.LifeClient.ApiException() {
                            @Override
                            public void error(String error) {
                            }
                        });
            }
        }
    }

    int realTemp = 0;

    /**
     * 更新UI
     */
    private void updateCabinetView() {
        Cursor c = getContentResolver().query(mCabinetUri, null, null, null,
                null);
        wineLight.setVisibility(View.VISIBLE);
        if (c.moveToNext()) {
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
            realTemp = c.getInt(c.getColumnIndex(WineCabinetMetaData.TEMP));
            mRealTemp.setText(c.getInt(c.getColumnIndex(WineCabinetMetaData.REAL_TEMP))
                    + "℃");
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
            getContentResolver().delete(mCabinetUri, null, null);
            getContentResolver().delete(DeviceMetaData.CONTENT_URI, null, null);
        } catch (Exception e) {
        }
    }
    //获取wifi状态
    protected boolean getWifistatus(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiStatus=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(wifiStatus == NetworkInfo.State.CONNECTED||wifiStatus== NetworkInfo.State.CONNECTING){
            return true;
        }else{
            return false;
        }
    }
}
