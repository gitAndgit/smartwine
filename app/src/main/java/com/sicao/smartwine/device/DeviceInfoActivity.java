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

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.entity.ModelEntity;
import com.sicao.smartwine.device.entity.PtjUserEntity;
import com.sicao.smartwine.libs.DeviceMetaData;
import com.sicao.smartwine.libs.WineCabinetMetaData;
import com.sicao.smartwine.libs.WineCabinetService;
import com.sicao.smartwine.party.PartyListActivity;
import com.sicao.smartwine.shop.IndexActivity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
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
        leftIcon.setVisibility(View.GONE);
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
        //获取用户信息
        ApiClient.getUserInfo(this, UserInfoUtil.getUID(this), UserInfoUtil.getToken(this), new ApiCallBack() {
            @Override
            public void response(Object object) {
                AppContext.imageLoader.displayImage(((PtjUserEntity) object).getAvatar(), rightIcon, AppContext.gallery);
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
        //登录到智捷通
        LifeClient.login(DeviceInfoActivity.this, getString(R.string.xmpp_host), getResources().getInteger(R.integer.xmpp_port),
                getString(R.string.service), getString(R.string.source), "sicao-" + UserInfoUtil.getUID(this),
                "sicao12345678", new com.sicao.smartwine.api.LifeClient.ApiCallBack() {
                    @Override
                    public void response(Object object) {
                        Log.i("huahua", "智捷通登录OK-----");
                        UserInfoUtil.setLogin(DeviceInfoActivity.this, true);
                        //获取设备列表,取第一个为默认勾选设备
                        LifeClient.getDeviceList(getApplicationContext(), new com.sicao.smartwine.api.LifeClient.ApiListCallBack() {
                            @Override
                            public <T> void response(ArrayList<T> list) {
                                ArrayList<Device> mList = (ArrayList<Device>) list;
                                if (mList.size() <= 0) {
                                    Toast.makeText(DeviceInfoActivity.this, "您还没有添加酒柜设备", Toast.LENGTH_SHORT).show();
                                    return;
                                }
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
                                Log.i("huahua", "获取设备列表" +
                                        "失败-----" + error);
                                Toast.makeText(DeviceInfoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }, new com.sicao.smartwine.api.LifeClient.ApiException() {
                    @Override
                    public void error(String error) {
                        Log.i("huahua", "智捷通登录失败-----" + error);
                        Toast.makeText(DeviceInfoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });
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
                    String smartModeTemp = "";
                    if (null == entity) {
                        smartWineMode = "";
                        smartModeTemp = "";
                    } else {
                        if (null == entity.getWork_model_name()) {
                            smartWineMode = "";
                            smartModeTemp = "";
                        } else {
                            smartWineMode = entity.getWork_model_name();
                            smartModeTemp = entity.getWork_model_demp();
                        }
                    }
                    startActivityForResult(new Intent(this, SmartSetActivity.class).
                            putExtra("smartWineName", mDevice.getName()).
                            putExtra("smartWineMode", smartWineMode).
                            putExtra("smartModeTemp", smartModeTemp), 10088);
                }
                break;
            case R.id.imageView3://酒柜灯开关
                if (LifeClient.getConnectionId() != -1 && !"".equals(mDeviceID)) {
                    if (null != mCabinet) {
                        if (mCabinet.isLight()) {
                            Toast.makeText(DeviceInfoActivity.this, "正在关闭设备灯", Toast.LENGTH_LONG).show();
                            mCabinet.setLight(false);
                        } else {
                            Toast.makeText(DeviceInfoActivity.this, "正在启动设备灯", Toast.LENGTH_LONG).show();
                            mCabinet.setLight(true);
                        }
                        mCabinet.update();
                    }
                }
                break;
            case R.id.setting_connect://设置连接
                if (LifeClient.getConnectionId() != -1) {
                    startActivityForResult(new Intent(this, ConfigActivity.class).putExtra("connectid", LifeClient.getConnectionId()), 10089);
                }
                break;
            case R.id.wineShop://美酒商城
                startActivity(new Intent(this, IndexActivity.class));
                break;
            case R.id.drinkWine://品酒行动
                startActivity(new Intent(this, PartyListActivity.class));
                break;
            case R.id.tiXing://美酒提醒
                Toast.makeText(DeviceInfoActivity.this, "美酒提醒", Toast.LENGTH_SHORT).show();
                break;
            case R.id.yijian://意见反馈
                Toast.makeText(DeviceInfoActivity.this, "意见反馈", Toast.LENGTH_SHORT).show();
                break;
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
            } else if (requestCode == 10089) {//配置新设备
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
}
