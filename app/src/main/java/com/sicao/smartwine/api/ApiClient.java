package com.sicao.smartwine.api;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sicao.smartwine.device.entity.ModelEntity;
import com.sicao.smartwine.device.entity.PtjUserEntity;
import com.sicao.smartwine.device.entity.RegisterEntity;
import com.sicao.smartwine.device.entity.ZjtUserEntity;
import com.sicao.smartwine.libs.DeviceMetaData;
import com.sicao.smartwine.libs.DeviceUtil;
import com.sicao.smartwine.shop.entity.ClassTypeEntity;
import com.sicao.smartwine.shop.entity.WineEntity;
import com.sicao.smartwine.shop.entity.WineLibraryEntity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.MD5;
import com.sicao.smartwine.util.UserInfoUtil;
import com.smartline.life.core.LANServiceManager;
import com.smartline.life.core.NetServiceManager;
import com.smartline.life.core.XMPPManager;
import com.smartline.life.device.Device;

import org.apache.http.Header;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于执行网络请求部分
 * Created by mingqi'li on 2015/12/21.
 */
public class ApiClient {
    private static String URL = "http://www.putaoji.com/Apiv5/";
    //请求帮助类
    private static AsyncHttpClient mHttp;

    public static synchronized AsyncHttpClient getHttpClient() {

        if (null == mHttp) {
            mHttp = new AsyncHttpClient();
            mHttp.setResponseTimeout(2000);
        }
        return mHttp;
    }

    /***
     * 注册时获取手机验证码(葡萄集)
     *
     * @param phone     手机号
     * @param callBack  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void registerGetCode(final Context context, final String phone, final ApiCallBack callBack,
                                       final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/verifyMobile?mobile=" + phone + "&type=getcode";
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                /**
                 * {
                 "status": true,
                 "error_code": 0,
                 "info": "短信发送成功"
                 }
                 */
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        if (null != callBack) {
                            callBack.response("success");
                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 验证用户的验证码是否正确(葡萄集)
     *
     * @param context   上下文对象
     * @param phone     手机号
     * @param code      手机验证码
     * @param callback  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void checkPhoneCode(final Context context, final String phone, final String code,
                                      final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/verifyMobile?mobile=" + phone + "&code=" + code + "&type=verifycode";
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                /**
                 * {
                 "status": true,
                 "error_code": 0,
                 "info": "验证成功"
                 }
                 */
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        if (null != callback) {
                            callback.response("success");

                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 注册接口(葡萄集)
     *
     * @param context   上下文对象
     * @param username  用户登录名(手机号,或者第三方登录)
     * @param password  用户密码
     * @param callback  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void register(final Context context, final String username, final String password,
                                final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/registerForSmartWine?username=" + username + "&password="
                + MD5.Encode(password, "utf-8") + "&nickname=" + "&type=3";
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                /**
                 * {
                 "status": true,
                 "error_code": 0,
                 "info": {
                 "uid": "1111",
                 "userToken": "YuMjAC0wlZqK1i3EOyxTmFkr8QBsaNX2tgpb6cfD"
                 }
                 }
                 */
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        ZjtUserEntity user = new ZjtUserEntity();
                        user.setUid(info.getString("uid"));
                        user.setToken(info.getString("userToken"));
                        if (null != callback) {
                            callback.response(user);
                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 用户账号登陆(葡萄集)
     *
     * @param context   上下文对象
     * @param username  用户名
     * @param password  用户密码
     * @param callback  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void login(final Context context, final String username, final String password,
                             final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/login?username=" + username + "&password=" + MD5.Encode(password, "utf-8");
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        ZjtUserEntity user = new ZjtUserEntity();
                        user.setUid(info.getString("uid"));
                        user.setToken(info.getString("userToken"));
                        if (null != callback) {
                            callback.response(user);
                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
            }
        });

    }


    /***
     * 验证用户帐号存在性，不存在则生成一个帐号,存在则提示用户已存在(外包检测是否存在该账号的接口)(智捷通)
     *
     * @param context   上下文对象
     * @param username  用户名
     * @param password  用户密码
     * @param callback  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void checkAccountInfo(final Context context, final String username, final String password,
                                        final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = "http://112.124.50.143:9090/plugins/thirdparty/user/register?username="
                + username + "&password=" + "sicao12345678" + "&accessToken=5ddd2bd0-b30a-4af6-a4a6-c9383afe1592";
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                /**
                 * { “code”: 200, “message”:”注册成功” }
                 */
                Gson gson = new Gson();
                RegisterEntity entity = gson.fromJson(new String(bytes), RegisterEntity.class);
                if (null != callback) {
                    callback.response(entity);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 登录接口(智捷通),执行OK返回设备连接ID(ConnectID)(智捷通)
     *
     * @param context   上下文对象
     * @param host      登录IP地址(112.124.50.143)
     * @param port      登录IP端口(5222)
     * @param service   接口服务名(life.com)
     * @param resource  登录账号类型(mobile)
     * @param username  用户名(sicao-123456789)
     * @param password  密码
     * @param callBack  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void login(final Context context, final String host, final int port, final String service,
                             final String resource, final String username, final String password,
                             final ApiCallBack callBack, final ApiException exception) {
        new Thread() {
            @Override
            public void run() {
                XMPPManager xmppManager = (XMPPManager) context
                        .getApplicationContext().getSystemService(
                                XMPPManager.XMPP_SERVICE);
                XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration
                        .builder();
                builder.setHost(host);
                builder.setPort(port);
                builder.setServiceName(service);
                builder.setResource(resource);
                builder.setSendPresence(true);
                builder.setUsernameAndPassword(username, password);
                builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                final int connId = xmppManager.addXMPPTCPConnection(builder
                        .build());
                xmppManager.connection(connId,
                        new AbstractConnectionListener() {
                            @Override
                            public void authenticated(
                                    XMPPConnection connection, boolean resumed) {
                                if (null != callBack) {
                                    callBack.response(connId);
                                }
                            }

                            @Override
                            public void connectionClosedOnError(Exception e) {
                                if (null != exception && null != e
                                        && null != e.getMessage()) {
                                    exception.error(e.getMessage());
                                }
                            }
                        });
            }
        }.start();
    }

    /***
     * 获取所有的设备列表信息(智捷通)
     *
     * @param context   上下文对象
     * @param callback  接口执行OK回调
     * @param exception 接口执行失败回调
     */
    public static void getDeviceList(final Context context,
                                     final ApiListCallBack callback, final ApiException exception) {
        try {
            ContentResolver mResolver = context.getApplicationContext()
                    .getContentResolver();
            Cursor cursor = mResolver.query(DeviceMetaData.CONTENT_URI, null,
                    null, null, null);
            ArrayList<Device> mList = new ArrayList<Device>();
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    Device device = new Device();
                    // 数据库ID字段
                    device.setId(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData._ID))));
                    // 设备id
                    device.setJid(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.JID)));
                    // 设备分组
                    device.setGroup(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.GROUP)));
                    // 设备MAC地址
                    device.setMac(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.MAC)));
                    //
                    device.setManufacturer(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.MAN)));
                    // 设备模式
                    device.setModel(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.MODEL)));
                    // 设备的名称
                    device.setName(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.NAME)));
                    // 设备是否在线
                    device.setOnline("1".equals(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.ONLINE))) ? true
                            : false);
                    device.setWifiSSID(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.SSID)));
                    // 设备系统---电板
                    device.setOs(cursor.getString(cursor
                            .getColumnIndex(DeviceMetaData.OS)));
                    mList.add(device);
                } while (cursor.moveToNext());
            }
            cursor.close();
            if (null != callback) {
                callback.response(mList);
            }
        } catch (Exception e) {
            if (null != e && null != exception) {
                exception.error(e.getMessage() + "");
            }
        }
    }

    /***
     * 获取WIFI列表数据(智捷通)
     *
     * @param context   上下文对象
     * @param callback  执行OK时回调对象
     * @param exception 执行失败时回调对象
     */
    public static void getWifiList(final Context context,
                                   final ApiListCallBack callback, final ApiException exception) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            manager.startScan();
            List<ScanResult> results = manager.getScanResults();
            ArrayList<ScanResult> data = new ArrayList<ScanResult>();
            for (ScanResult result : results) {
                if (!DeviceUtil.isNewDevice(result.SSID) && !"".equals(result.SSID)) {
                    data.add(result);
                }
            }
            if (null != callback) {
                callback.response(data);
            }
        } catch (Exception e) {
            if (null != exception && null != e && null != e.getMessage()) {
                exception.error(e.getMessage());
            }
        }
    }

    /**
     * 获取发现设备列表数据(智捷通)
     *
     * @param context   上下文对象
     * @param callback  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getNativeConfigDeviceList(final Context context,
                                                 final ApiListCallBack callback, final ApiException exception) {
        try {
            ContentResolver mResolver = context.getApplicationContext()
                    .getContentResolver();
            //
            Cursor cursor = mResolver.query(DeviceMetaData.FIND_CONTENT_URI,
                    null, null, null, null);
            ArrayList<Device> mlist = new ArrayList<Device>();
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    // 该状态表示该设备在网络服务器上已经配置完毕，但是还没有添加到我的设备列表里面
                    if (!DeviceMetaData.Status.WAN.equals(cursor
                            .getString(cursor
                                    .getColumnIndex(DeviceMetaData.STATUS)))) {
                        Device device = new Device();
                        // 数据库ID字段
                        int id = Integer.parseInt(cursor.getString(cursor
                                .getColumnIndex(DeviceMetaData._ID)));
                        device.setId(id);
                        // 设备id
                        String jid = cursor.getString(cursor
                                .getColumnIndex(DeviceMetaData.JID));
                        device.setJid(jid);
                        // 设备MAC地址
                        String mac = cursor.getString(cursor
                                .getColumnIndex(DeviceMetaData.MAC));
                        device.setMac(mac);
                        // 设备模式
                        String model = cursor.getString(cursor
                                .getColumnIndex(DeviceMetaData.MODEL));
                        device.setModel(model);
                        // 设备的名称
                        String name = cursor.getString(cursor
                                .getColumnIndex(DeviceMetaData.NAME));
                        device.setName(name);
                        mlist.add(device);
                    }
                } while (cursor.moveToNext());

            }
            cursor.close();
            if (null != callback){
                callback.response(mlist);
            }
        } catch (Exception e) {
            if (null != e && null != exception){
                exception.error(e.getMessage() + "");
            }
        }
    }

    /**
     * 将刚配置好的设备添加到我的设备列表里面(智捷通)
     *
     * @param context   上下文对象
     * @param connectid XMPPConnection连接对象id
     * @param device    设备对象
     */
    public static void addDevice(final Context context, final int connectid,
                                 final Device device, final String saveName,
                                 final ApiCallBack callback, final ApiException exception) {
        XMPPManager mXMPPManager = (XMPPManager) context
                .getApplicationContext().getSystemService(
                        XMPPManager.XMPP_SERVICE);
        final XMPPConnection mXMPPConnection = mXMPPManager
                .getXMPPConnection(connectid);
        if (mXMPPConnection.isConnected() && mXMPPConnection.isAuthenticated()) {
            String bareJid = XmppStringUtils.parseBareJid(device.getJid());
            Roster roster = Roster.getInstanceFor(mXMPPConnection);
            try {
                roster.createEntry(bareJid, saveName, new String[]{"我的设备"});
                if (null != callback){
                    callback.response(true);
                }
            } catch (Exception e) {
                if (null != e && null != exception)
                {
                    exception.equals(e.getMessage());
                }
            }
        } else {
            if (null != exception){
                exception.equals("连接异常");
            }
        }

    }

    /***
     * 向设备发送UDP数据包(无结果返回)40s(智捷通)
     *
     * @param context       上下文对象
     * @param wifi_ssid     WIFI名称
     * @param wifi_password WIFI密码
     */
    public static void configWIFIToDevice(final Context context,
                                          final String wifi_ssid, final String wifi_password) {
        new Thread() {
            @Override
            public void run() {
                EsptouchTask mEsptouchTask = new EsptouchTask(wifi_ssid,
                        wifi_password, context);
                try {
                    if (null != mEsptouchTask) {
                        mEsptouchTask.execute();
                        // 执行10秒10次的轮询数据库表
                        int max = 10;
                        for (int i = 0; i < max; i++) {
                            LANServiceManager serviceManager = (LANServiceManager) context
                                    .getSystemService(LANServiceManager.LAN_SERVICE);
                            serviceManager.discoveryLanServices();
                            NetServiceManager nsm = (NetServiceManager) context
                                    .getSystemService(NetServiceManager.NSM_SERVICE);
                            nsm.refreshSearchService();
                            Thread.currentThread();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (i == 9 && null != mEsptouchTask) {
                                mEsptouchTask.interrupt();
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }.start();
    }

    /***
     * 配置设备的工作模式(葡萄集)
     *
     * @param context    上下文对象
     * @param uid        用户uid
     * @param deviceId   设备id
     * @param model_name 模式名称
     * @param model_temp 模式对应的温度
     * @param action     select|insert|update   -------查询,添加,更新
     * @param callback   执行OK回调对象
     * @param exception  执行失败回调对象
     */
    public static void configWorkMode(final Context context, final String uid,
                                      final String deviceId, final String model_name,
                                      final String model_temp, final String action,
                                      final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = "http://www.putaoji.com/Apiv5/App/smartWine";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("act", action);
        params.put("device_id", uid);
        params.put("work_model_name", model_name);
        params.put("work_model_demp", model_temp);
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        if ("insert".equals(action) || "update".equals(action)) {
                            //插入/更新
                            if (null != callback) {
                                callback.response(true);
                            }
                        } else if ("select".equals(action)) {
                            //查询
                            JSONObject info = object
                                    .getJSONObject("info");
                            ModelEntity entity = new Gson().fromJson(
                                    info.toString(), ModelEntity.class);
                            if (null != callback) {
                                callback.response(entity);
                            }
                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /**
     * 获取用户的个人信息接口(葡萄集)
     *
     * @param context   上下文对象
     * @param uid       用户uid
     * @param token     用户token
     * @param callBack  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getUserInfo(final Context context, final String uid, final String token,
                                   final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getProfileInfo?userToken=" + token + "&uid=" + uid;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                /**
                 * {
                 "status": true,
                 "error_code": 0,
                 "info": {
                 "uid": "1231915",
                 "avatar": "http:\/\/www.putaoji.com\/Uploads\/Avatar\/qqAvatar\/555c9c41abb81_128_128.jpg",
                 "nickname": "niti",
                 "signature": "爱美酒.",
                 "email": "",
                 "mobile": "18818689897",
                 "score": "10",
                 "sex": "f",
                 "birthday": "0000-00-00",
                 "title": "Lv1 实习"
                 "auth_type": "0",
                 }
                 }
                 */
                Log.i("huahua", new String(bytes));
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        PtjUserEntity entity = new Gson().fromJson(object.getJSONObject("info").toString(), PtjUserEntity.class);
                        if (null != callBack){
                            callBack.response(entity);
                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception){
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 获取美酒商城首页展示数据(葡萄集)
     *
     * @param context   上下文对象
     * @param callBack  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getShopIndex(final Context context, final ApiListCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getClassify?userToken=" + UserInfoUtil.getToken(context);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("huahua", new String(bytes));
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        ArrayList<WineLibraryEntity> mList = new ArrayList<WineLibraryEntity>();
                        JSONArray array = object
                                .getJSONArray("info");
                        for (int j = 0; j < array.length(); j++) {
                            WineLibraryEntity entity = new Gson()
                                    .fromJson(array
                                                    .getJSONObject(j)
                                                    .toString(),
                                            WineLibraryEntity.class);
                            mList.add(entity);
                        }
                        if (null != callBack){
                            callBack.response(mList);
                        }
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception){
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 获取美酒商城下级页面的美酒列表数据(葡萄集)
     *
     * @param page       分页页码数
     * @param row        每页条目数
     * @param context    上下文对象
     * @param pos        位置  值为(0默认值 1美酒库)
     * @param name       商品名称,搜索时该字段生效
     * @param cat_id     分类  值为(美酒套餐 4 其他 0)
     * @param classify   分类ID组  (1,23  [注意：搜索传0])
     * @param attr       商品属性  (红葡萄酒,白葡萄酒,起泡酒,其他【多个用逗号隔开】)
     * @param priceScope 价格筛选
     * @param originArea 地区筛选
     * @param wineType   品类筛选
     * @param callBack   执行OK回调对象
     * @param exception  执行失败回调对象
     */
    public static void getShopList(final int page, final int row,
                                   final Context context, final String pos, final String name,
                                   final String cat_id, final String classify, final String attr,
                                   final String priceScope, final String originArea,
                                   final String wineType, final ApiListCallBack callBack,
                                   final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getGoodsList";
        RequestParams params = new RequestParams();
        params.put("userToken", UserInfoUtil.getToken(context));
        params.put("page", page + "");
        params.put("row", row + "");
        params.put("cat_id", cat_id);
        params.put("poso", pos);
        params.put("name", "");
        params.put("classify", classify);
        params.put("attr", attr);
        params.put("priceScope", priceScope);
        params.put("originArea", originArea);
        params.put("wineType", wineType);
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        JSONArray array = info.getJSONArray("list");
                        ArrayList<WineEntity> list = new ArrayList<WineEntity>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject wine = array.getJSONObject(i);
                            WineEntity entity = new WineEntity();
                            entity.setId(wine.getString("id"));
                            entity.setWineName(wine.getString("name"));
                            entity.setWineImg(wine.getString("icon"));
                            entity.setPrice(wine
                                    .getString("current_price"));
                            entity.setBuy_address(wine
                                    .getString("buy_address"));
                            list.add(entity);
                        }
                        if (null != callBack) {
                            callBack.response(list);
                        }
                    } else {
                        Log.e("putaoji",
                                "wine list error:"
                                        + object.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception){
                    exception.error(new String(bytes));
                }
            }
        });
    }

    /***
     * 获取美酒列表的筛选条件数据(葡萄集)
     *
     * @param name
     * @param sub       子筛选
     * @param callBack  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getClassify(final Context context, final String name, final String sub,
                                   final ApiListCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getDealAttr?userToken="
                + UserInfoUtil.getToken(context) + "&name=" + name + "&sub=" + sub;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    /***
                     * {"status":true,"error_code":0,"info":[{"name":"价格","sub":
                     * ["全部","50以下","51-99","100-199","200-499","500-999",
                     * "1000+"
                     * ]},{"name":"品类","sub":["全部","红葡萄酒","白葡萄酒","起泡酒","其他"
                     * ]},{"name"
                     * :"产地","sub":["全部","法国","意大利","西班牙","德国","美国","澳大利亚"
                     * ,"智利","阿根廷","其他"]}]}
                     */
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONArray array = object.getJSONArray("info");
                        ArrayList<ClassTypeEntity> list = new ArrayList<ClassTypeEntity>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject subs = array.getJSONObject(i);
                            ClassTypeEntity entity = new ClassTypeEntity();
                            entity.setName(subs.getString("name"));
                            // sub
                            JSONArray sub = subs.getJSONArray("sub");
                            ClassTypeEntity[] sublist = new ClassTypeEntity[sub
                                    .length()];
                            for (int j = 0; j < sub.length(); j++) {
                                ClassTypeEntity entity1 = new ClassTypeEntity();
                                entity1.setName(sub.getString(j));
                                sublist[j] = entity1;
                            }
                            entity.setSubs(sublist);
                            list.add(entity);
                        }
                        if (null != callBack) {
                            callBack.response(list);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception){
                    exception.error(new String(bytes));
                }
            }
        });
    }
}
