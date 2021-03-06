package com.sicao.smartwine.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sicao.smartwine.device.entity.ModelEntity;
import com.sicao.smartwine.device.entity.PtjUserEntity;
import com.sicao.smartwine.device.entity.RegisterEntity;
import com.sicao.smartwine.device.entity.ZjtUserEntity;
import com.sicao.smartwine.pay.OrderEntity;
import com.sicao.smartwine.shop.entity.Banner;
import com.sicao.smartwine.shop.entity.ClassTypeEntity;
import com.sicao.smartwine.shop.entity.Comment;
import com.sicao.smartwine.shop.entity.CommentList;
import com.sicao.smartwine.shop.entity.GoodsEntity;
import com.sicao.smartwine.shop.entity.InterEntity;
import com.sicao.smartwine.shop.entity.MyWine;
import com.sicao.smartwine.shop.entity.ShareEntity;
import com.sicao.smartwine.shop.entity.Sns;
import com.sicao.smartwine.shop.entity.TopicDetail;
import com.sicao.smartwine.shop.entity.User;
import com.sicao.smartwine.shop.entity.WineEntity;
import com.sicao.smartwine.shop.entity.WineLibraryEntity;
import com.sicao.smartwine.user.entity.Address;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListAndObjectCallBack;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.AppManager;
import com.sicao.smartwine.util.MD5;
import com.sicao.smartwine.util.UserInfoUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用于执行网络请求部分
 * Created by mingqi'li on 2015/12/21.
 */
public class ApiClient {
    public static String URL = "http://www.putaoji.com/Apiv5/";
    //请求帮助类
    private static AsyncHttpClient mHttp;

    public static synchronized AsyncHttpClient getHttpClient() {

        if (null == mHttp) {
            mHttp = new AsyncHttpClient();
        }
        return mHttp;
    }

    public static boolean status(JSONObject object) {
        try {
            return object.getBoolean("status");
        } catch (Exception e) {
            return false;
        }
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
                /**
                 * {
                 "status": true,
                 "error_code": 0,
                 "info": "短信发送成功"
                 }
                 */
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response("success");
                        }
                        return;
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
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

                /**
                 * {
                 "status": true,
                 "error_code": 0,
                 "info": "验证成功"
                 }
                 */
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        if (null != callback) {
                            callback.response("success");
                        }
                        return;
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
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
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        JSONObject info = object.getJSONObject("info");
                        ZjtUserEntity user = new ZjtUserEntity();
                        user.setUid(info.getString("uid"));
                        user.setToken(info.getString("userToken"));
                        if (null != callback) {
                            callback.response(user);
                        }
                        return;
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
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

                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        JSONObject info = object.getJSONObject("info");
                        ZjtUserEntity user = new ZjtUserEntity();
                        user.setUid(info.getString("uid"));
                        user.setToken(info.getString("userToken"));
                        if (null != callback) {
                            callback.response(user);
                        }
                        return;
                    } else {
                        Toast.makeText(context, object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
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

                /**
                 * { “code”: 200, “message”:”注册成功” }
                 */
                Gson gson = new Gson();
                RegisterEntity entity = gson.fromJson(new String(bytes), RegisterEntity.class);
                if (null != callback) {
                    callback.response(entity);
                }
                return;
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
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
    public static void configWorkMode(final Context context, String uid,
                                      String deviceId, String model_name,
                                      String model_temp, final String action,
                                      final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = "http://www.putaoji.com/Apiv5/App/smartWine";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("act", action);
        params.put("device_id", deviceId);
        params.put("work_model_name", model_name);
        params.put("work_model_demp", model_temp);
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
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
                            return;
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));

                }
                return;
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
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        PtjUserEntity entity = new Gson().fromJson(object.getJSONObject("info").toString(), PtjUserEntity.class);
                        if (null != callBack) {
                            callBack.response(entity);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
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

                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
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
                        if (null != callBack) {
                            callBack.response(mList);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException exception) {
                    Log.i("ApiClient", "sicao-" + exception.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
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
                    if (status(object)) {
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
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
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
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
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
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取美酒商城内的商品详情
     *
     * @param context   上下文对象
     * @param goodid    商品ID
     * @param callback  执行OK 回调对象
     * @param exception 执行失败回调对象
     */
    public static void getGoodsInfo(final Context context, final String goodid,
                                    final ApiCallBack callback, final ApiException exception) {

        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getDealDetailFromReviewArticle?userToken=" + UserInfoUtil.getToken(context) + "&deal_id=" + goodid;
        Log.i("huahua", "商品详情--" + url);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        GoodsEntity entity = new Gson().fromJson(
                                info.toString(), GoodsEntity.class);
                        ShareEntity share = new Gson().fromJson(object
                                        .getJSONObject("share_info").toString(),
                                ShareEntity.class);
                        entity.setShare(share);
                        if (null != callback) {
                            callback.response(entity);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });

    }

    /***
     * 获取文章(包含商品详情信息)的回复列表(葡萄集)
     *
     * @param topic_id  文章/帖子  ID
     * @param page      页码数
     * @param row       每页条目数
     * @param callBack  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getCommentList(final Context context, String topic_id, int page,
                                      int row, final ApiListCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/listDealComment?userToken="
                + UserInfoUtil.getToken(context) + "&deal_id=" + topic_id
                + "&page=" + page + "&row=" + row;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONArray array = object.getJSONArray("info");
                        ArrayList<Comment> list = new ArrayList<Comment>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject comment = array.getJSONObject(i);
                            Comment c = new Comment();
                            // 一级回复内容
                            c.setContent(comment.getString("content"));
                            // 一级回复id
                            c.setId(comment.getString("id"));
                            // 一级回复图片地址
                            JSONArray imgs = comment.getJSONArray("img");
                            if (imgs.length() > 0) {
                                c.setImgUrl(imgs.getString(0));
                            }
                            // 一级回复类型(纯图+文字/纯文字/美酒刊/话题/美酒推荐)
                            c.setType(Integer.parseInt(comment.getString("type")));
                            // 评论时间
                            c.setCreate_time(comment.getString("create_time"));
                            // 点赞数
                            c.setSupport(comment.getString("support"));
                            // 是否已点赞
                            c.setSupport(comment.getBoolean("is_support"));
                            // 一级回复者
                            User u = new User();
                            // 一级回复者的头像
                            u.setAvatar(comment.getString("avatar"));
                            // 一级回复者的昵称
                            u.setNickname(comment.getString("nickname"));
                            // 一级回复者的uid
                            u.setUid(comment.getString("uid"));
                            c.setGoods(true);
                            c.setStar(comment.getString("star"));
                            c.setUser(u);
                            // 二级回复内容
                            ArrayList<CommentList> list2 = new ArrayList<CommentList>();
                            if (!comment.isNull("list")) {
                                JSONArray array2 = comment.getJSONArray("list");
                                // 解析二级回复列表
                                for (int j = 0; j < array2.length(); j++) {
                                    JSONObject comment2 = array2.getJSONObject(j);
                                    CommentList l = new CommentList(
                                            comment2.getString("uid"),
                                            comment2.getString("nickname"),
                                            comment2.getString("content"),
                                            comment2.getString("sup_uid"),
                                            comment2.getString("sup_nickname"),
                                            comment2.getString("create_time"));
                                    l.setCommentListid(Integer.parseInt(comment2
                                            .getString("id")));
                                    // 将二级回复数据加入到数组中
                                    list2.add(l);
                                    // 解析N+层
                                    list2.addAll(fors(comment2));
                                }
                            }
                            // 将二级回复配置到一级回复中
                            c.setmLists(list2);
                            // 将一级回复加入到回复列表数组
                            list.add(c);
                        }
                        if (null != callBack) {
                            callBack.response(list);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    public static ArrayList<CommentList> fors(JSONObject object) {
        ArrayList<CommentList> list = new ArrayList<CommentList>();
        try {
            if (object.getJSONArray("list").length() > 0) {
                JSONArray array3 = object.getJSONArray("list");
                for (int k = 0; k < array3.length(); k++) {
                    JSONObject comment3 = array3.getJSONObject(k);
                    CommentList ll = new CommentList(comment3.getString("uid"),
                            comment3.getString("nickname"),
                            comment3.getString("content"),
                            comment3.getString("pid"),
                            comment3.getString("create_time"), "");
                    ll.setCommentListid(Integer.parseInt(comment3
                            .getString("id")));
                    // 将三层回复数据加入到二层回复下
                    list.add(ll);
                    list.addAll(fors(comment3));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /***
     * 获取品酒活动列表
     *
     * @param context   上下文对象
     * @param cType
     * @param page
     * @param mark
     * @param callback
     * @param exception
     */
    public static void getPartyList(final Context context, int cType, int page, int mark,
                                    final ApiListCallBack callback,
                                    final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/listActivity?userToken=" + UserInfoUtil.getToken(context)
                + "&page=" + page + "&row=10";
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        ArrayList<Sns> list = new ArrayList<Sns>();
                        if (!TextUtils.isEmpty(object.getJSONObject("info")
                                .getString("list"))) {// 内容不为空
                            JSONArray jsonArray = object.getJSONObject("info")
                                    .getJSONArray("list");
                            int length = jsonArray.length();
                            Gson gson = new Gson();
                            for (int i = 0; i < length; i++) {
                                JSONObject json = (JSONObject) jsonArray.get(i);
                                if (json != null) {
                                    Sns sns = gson.fromJson(json.toString(),
                                            Sns.class);
                                    list.add(sns);
                                }
                            }
                        }
                        if (null != callback) {
                            callback.response(list);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取品酒活动详情信息
     *
     * @param context   上下文对象
     * @param partyID   活动ID
     * @param callback  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getPartyDetail(final Context context, final String partyID,
                                      final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getNewTopicDetail?userToken="
                + UserInfoUtil.getToken(context) + "&topic_id=" + partyID;
        Log.i("huahua", "活动详情-" + url);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    boolean status = object.getBoolean("status");
                    String error_code = object.getString("error_code");
                    if (status == true && "0".equals(error_code)) {
                        JSONObject jsonObject = object.getJSONObject("info")
                                .getJSONObject("topic");
                        Gson gson = new Gson();
                        // 帖子的具体内容
                        TopicDetail topicDetail = gson.fromJson(
                                jsonObject.toString(), TopicDetail.class);
                        JSONObject insert = jsonObject
                                .getJSONObject("relation");
                        // 帖子中插入的文章或者美酒刊等等嘎七噶八的东东
                        InterEntity entity = gson.fromJson(insert.toString(),
                                InterEntity.class);
                        topicDetail.setRelation(entity);
                        // v3 新加酒款
                        if (jsonObject.has("third")
                                && !jsonObject.isNull("third")
                                && jsonObject.get("third") instanceof JSONArray) {
                            JSONArray array = jsonObject.getJSONArray("third");
                            ArrayList<MyWine> mlist = new ArrayList<MyWine>();
                            int a = array.length();
                            for (int i = 0; i < a; i++) {
                                JSONObject object2 = array.getJSONObject(i);
                                MyWine wine = new MyWine();
                                wine.setId(object2.getString("id"));
                                wine.setName(object2.getString("name"));
                                wine.setImage(object2.getString("icon"));
                                wine.setPrice(object2
                                        .getString("current_price"));
                                wine.setBuy_type(object2.getString("buy_type"));
                                wine.setType(object2.getString("type"));
                                mlist.add(wine);
                            }
                            topicDetail.setList(mlist);
                        }
                        // 活动试饮啥的是不是已经报名了呀
                        topicDetail.setIs_signup(jsonObject
                                .getBoolean("is_signup"));
                        // 用户自己创建帖子时发表的图片数组,后台无法排版，妹的，前端自己排版。组合吧。
                        JSONArray imgJson = jsonObject.getJSONArray("image");
                        // banner有两个字段，src暂用代表big图，small暂用代表small图，自己看着办啊！
                        ArrayList<Banner> images = new ArrayList<Banner>();
                        for (int i = 0; i < imgJson.length(); i++) {
                            JSONObject img = imgJson.getJSONObject(i);
                            Banner b = new Banner();
                            b.setSrc(img.getString("big"));
                            b.setUrl(img.getString("small"));
                            // 把数据塞进数组中
                            images.add(b);
                        }
                        // 把数组塞进帖子的具体内容中，唉，再来一层吧。
                        topicDetail.setImages(images);
                        // 帖子分享的标题内容
                        ShareEntity share = new Gson().fromJson(object
                                        .getJSONObject("share_info").toString(),
                                ShareEntity.class);
                        //
                        topicDetail.setShare(share);
                        // 发通知吧，数据已OK啦。
                        if (null != callback) {
                            callback.response(topicDetail);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                    Log.i("ApiClient", e.getMessage() + "");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取品酒活动中插入的酒款信息
     *
     * @param context   上下文对象
     * @param goodID    商品ID
     * @param callback  执行OK回调对象
     * @param exception 执行失败回调对象
     */
    public static void getGoodsDetail(final Context context, final String goodID,
                                      final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getGoodDetail?userToken="
                + UserInfoUtil.getToken(context) + "&deal_id=" + goodID;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        JSONArray array = info.getJSONArray("list");
                        MyWine wine = new MyWine();
                        if (array.length() > 0) {
                            JSONObject wineJson = array
                                    .getJSONObject(0);
                            wine.setId(wineJson.getString("id"));
                            wine.setName(wineJson.getString("name"));
                            wine.setPrice(wineJson
                                    .getString("current_price"));
                            String icon = wineJson.getString("icon");
                            wine.setImage(icon);
                            wine.setType(wineJson
                                    .getString("buy_address"));
                        }
                        if (null != callback) {
                            callback.response(wine);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /**
     * 获取品酒活动参加人员的信息
     *
     * @param partyID   活动ID
     * @param page      数据页码
     * @param row       每页数据量
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void getPartyJoinUsers(final Context context, String partyID, int page, int row,
                                         final ApiListAndObjectCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getTopicSignList?topic_id=" + partyID
                + "&userToken=" + UserInfoUtil.getToken(context);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject objec = new JSONObject(new String(bytes));
                    if (objec.getBoolean("status")) {
                        JSONObject info = objec.getJSONObject("info");
                        String number = info.getJSONObject("topic")
                                .getString("sum");// 参与人数
                        JSONArray signinfo = info
                                .getJSONObject("topic").getJSONArray(
                                        "signinfo");// 参与人信息
                        ArrayList<User> list = new ArrayList<User>();
                        for (int i = 0; i < signinfo.length(); i++) {
                            User user = new User();
                            JSONObject jsonObject = (JSONObject) signinfo
                                    .get(i);
                            String uid = jsonObject.getString("uid");
                            String avatar = jsonObject
                                    .getString("signcover");
                            user.setUid(uid);
                            user.setAvatar(avatar);
                            list.add(user);
                        }
                        if (null != callBack) {
                            callBack.response(list, number);
                        }
                        return;
                    } else {
                        if ("401".equals(objec.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 点赞/取消赞的操作
     *
     * @param context   上下文对象
     * @param sourceID  点赞资源ID (文章/帖子/评论ID)
     * @param type      对什么执行点赞/取消赞 (1帖子 2 文章 3帖子评论 4文章评论 5商品评论)
     * @param ThumbsUp  是点赞还是取消赞
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void ThumbsUp(final Context context, String sourceID, String type, final boolean ThumbsUp,
                                final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = "";
        if (ThumbsUp) {
            url = URL + "App/support?userToken="
                    + UserInfoUtil.getToken(context) + "&id=" + sourceID + "&type="
                    + type + "&act=1";
        } else {
            url = URL + "App/support?userToken="
                    + UserInfoUtil.getToken(context) + "&id=" + sourceID + "&type="
                    + type + "&act=0";
        }
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    Log.i("huahua", "点赞--" + ThumbsUp + "结果---" + new String(bytes));
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取活动详情下的评论列表数据信息
     *
     * @param context   上下文对象
     * @param partyID   活动ID
     * @param page      数据拉去页码数
     * @param row       每页数控量
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void getPartyDetailComments(final Context context, String partyID, int page, int row, final int maxComment,
                                              final ApiListCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/listPost?userToken="
                + UserInfoUtil.getToken(context) + "&topic_id=" + partyID
                + "&page=" + page + "&row=" + row;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        JSONObject info = object.getJSONObject("info");
                        JSONArray array = info.getJSONArray("list");
                        ArrayList<Comment> list = new ArrayList<Comment>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject comment = array.getJSONObject(i);
                            Comment c = new Comment();
                            c.setPost_num(maxComment + "");
                            // 一级回复内容
                            c.setContent(comment.getString("content"));
                            // 一级回复id
                            c.setId(comment.getString("id"));
                            // 一级回复图片地址
                            c.setImgUrl(comment.getString("img"));
                            // 一级回复类型(纯图+文字/纯文字/美酒刊/话题/美酒推荐)
                            c.setType(Integer.parseInt(comment.getString("type")));
                            // 回复时间
                            c.setCreate_time(comment.getString("create_time"));
                            // 点赞数
                            c.setSupport(comment.getString("support"));
                            // 是否已点赞
                            c.setSupport(comment.getBoolean("is_support"));
                            // 一级回复者
                            User u = new User();
                            // 一级回复者的头像
                            u.setAvatar(comment.getString("avatar"));
                            // 一级回复者的昵称
                            u.setNickname(comment.getString("nickname"));
                            // 一级回复者的uid
                            u.setUid(comment.getString("uid"));
                            c.setUser(u);
                            // 二级回复内容
                            ArrayList<CommentList> list2 = new ArrayList<CommentList>();
                            if (!comment.isNull("list")) {
                                JSONArray array2 = comment.getJSONArray("list");
                                // 解析二级回复列表
                                for (int j = 0; j < array2.length(); j++) {
                                    JSONObject comment2 = array2.getJSONObject(j);
                                    CommentList l = new CommentList(
                                            comment2.getString("uid"),
                                            comment2.getString("nickname"),
                                            comment2.getString("content"),
                                            comment2.getString("sup_uid"),
                                            comment2.getString("sup_nickname"),
                                            comment2.getString("create_time"));
                                    l.setCommentListid(Integer.parseInt(comment2
                                            .getString("id")));
                                    // 将二级回复数据加入到数组中
                                    list2.add(l);
                                    // 解析三++层回复
                                    list2.addAll(fors(comment2));
                                }
                            }
                            // 将二级回复配置到一级回复中
                            c.setmLists(list2);
                            // 将一级回复加入到回复列表数组
                            list.add(c);
                        }
                        if (null != callBack) {
                            callBack.response(list);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 酒会活动添加评论(一级/二级)
     *
     * @param context    上下文对象
     * @param partyID    活动ID
     * @param pid        被评论者ID
     * @param type       回复的文本类型( -1图片，0纯文字，1美酒刊，2话题，3葡萄酒)
     * @param attach_ids 图片ID组
     * @param img_paths  图片路径组
     * @param content    回复内容
     * @param callBack   执行OK回调对象
     * @param exception  执行失败回调对象
     */
    public static void sendCommentFromPartyDetail(final Context context, String partyID, String pid, String type,
                                                  String attach_ids, String img_paths, String content,
                                                  final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/newPost?userToken="
                + UserInfoUtil.getToken(context);
        RequestParams params = new RequestParams();
        params.put("topic_id", partyID);
        params.put("content", content);
        params.put("attach_ids", attach_ids);// 图片id或者文章id
        params.put("type", type + "");// -1图片，0纯文字，1美酒刊，2话题，3葡萄酒
        params.put("img_paths", img_paths);// 七牛云存储图片
        params.put("pid", pid + "");// 被评论者id
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                    return;
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 删除评论
     *
     * @param context   上下文对象
     * @param commentID 评论ID
     * @param type      评论类型(帖子/文章)
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void deleteComment(final Context context, final String commentID,
                                     final String type, final ApiCallBack callBack,
                                     final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/delComment?userToken="
                + UserInfoUtil.getToken(context) + "&id=" + commentID + "&type="
                + type;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    Log.i("huahua", new String(bytes));
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                    return;
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取订单详情
     *
     * @param context   上下文对象
     * @param orderID   订单ID
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void getOrderDetail(final Context context, String orderID,
                                      final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getMyOrderDetail?userToken="
                + UserInfoUtil.getToken(context) + "&order_id=" + orderID;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        // 订单
                        OrderEntity entity = new OrderEntity();
                        entity.setOrderId(info.getString("id"));// 订单id
                        entity.setOrderNumber(info.getString("order_sn"));// 订单号
                        entity.setOrderPrice(info.getString("total_price"));// 订单价格
                        entity.setState(info.getString("current_status"));// 订单状态
                        // 商品信息
                        WineEntity wine = new WineEntity();
                        wine.setWineName(info.getString("name"));
                        wine.setPrice(info.getString("unit_price"));
                        wine.setOldPrice(info.getString("origin_price"));
                        wine.setWineImg(info.getString("icon"));
                        wine.setDetail(info.getString("description"));
                        // 规格信息
                        WineEntity meal = new WineEntity();
                        meal.setWineName(info.getString("specify_name") + "");
                        // 收货人地址栏信息
                        Address address = new Address();
                        address.setAddress(info.getString("address"));
                        address.setName(info.getString("consignee"));
                        address.setPhone(info.getString("mobile"));
                        // 1，设置地址信息
                        entity.setAddress(address);
                        // 2，设置商品信息
                        entity.setGoods(wine);
                        // 3，设置规格信息
                        entity.setMeal(meal);
                        // 4，底部显示要支付的金额
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 支付前对订单进行校验
     *
     * @param context   上下文对象
     * @param orderID   订单ID
     * @param sign      待校验的字符串
     * @param payType   支付类型(支付宝/微信)
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void checkSign(final Context context, String orderID, String sign, final String payType,
                                 final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/verfityAndReturnOrderId?userToken="
                + UserInfoUtil.getToken(context) + "&order_id=" + orderID
                + "&order_key=" + sign + "&pay_type=" + payType;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        JSONObject info = object.getJSONObject("info");
                        // 赋值支付回调web的url
                        OrderEntity entity = new OrderEntity();
                        entity.setNotifyurl(info.getString("notify_url"));
                        entity.setState(payType);
                        if ("2".equals(payType)) {
                            // 如果是调用微信，则获取微信预支付订单号
                            entity.setOrderNumber(info.getString("prepay_id"));
                        }
                        if (null != callBack) {
                            callBack.response(entity);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /**
     * 品酒活动生成临时报名信息
     *
     * @param context   上下文对象
     * @param row_id
     * @param tel
     * @param sign_num
     * @param price
     * @param name
     * @param callBack
     * @param exception
     */
    public static void createTemporarySignUp(final Context context, String row_id, String tel,
                                             String sign_num, String price, final String name,
                                             final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/enrollment";
        RequestParams params = new RequestParams();
        params.put("userToken",
                UserInfoUtil.getToken(context));
        params.put("row_id", row_id);
        params.put("tel", tel);
        params.put("sign_num", sign_num);
        params.put("total_price", price);
        params.put("name", name);
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        OrderEntity entity = new OrderEntity();
                        JSONObject list = object.getJSONObject("info")
                                .getJSONObject("list");
                        WineEntity wine = new WineEntity();
                        wine.setId(list.getString("enroid"));
                        wine.setWineName(name);
                        entity.setGoods(wine);
                        entity.setOrderId(list.getString("id"));
                        entity.setOrderNumber(list
                                .getString("order_sn"));
                        entity.setOrderTime(list
                                .getString("create_time"));
                        entity.setOrderPrice(list
                                .getString("total_price"));
                        entity.setState(list.getString("order_status"));
                        if (null != callBack) {
                            callBack.response(entity);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 支付成功后告知后台支付OK
     *
     * @param context
     * @param enroid
     * @param ordernumber
     * @param callBack
     * @param exception
     */
    public static void makesurePaySuccess(final Context context, String enroid, String ordernumber,
                                          final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/enrollStatus?enroid=" + enroid
                + "&order_sn=" + ordernumber + "&userToken=" + UserInfoUtil.getToken(context);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 增加商品评价(一级,二级)
     *
     * @param context      上下文对象
     * @param deal_id      商品ID
     * @param content      评价内容
     * @param attach_ids   评价内容中的图片ID组
     * @param type         评价类型(-1,没有图片为0)
     * @param pid          被评价者ID
     * @param star         评价的星级
     * @param bitmap_paths 图片的路径url拼接起来的字符串(七牛云存储图片)
     * @param callBack     接口执行OK回调的对象
     * @param exception    接口执行失败回调的对象
     */
    public static void sendCommentForShop(final Context context, String deal_id, String content,
                                          String attach_ids, String type, String pid,
                                          String star, String bitmap_paths, final ApiCallBack callBack,
                                          final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/newDealComment?userToken=" + UserInfoUtil.getToken(context);
        RequestParams params = new RequestParams();
        params.put("deal_id", deal_id);
        params.put("content", content);
        params.put("attach_ids", attach_ids);
        params.put("type", type);
        params.put("pid", pid);
        params.put("star", star);
        params.put("img_paths", bitmap_paths);// 七牛云存储图片
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                        return;
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取我的默认地址
     *
     * @param context   上下文对象
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void getDefaultAddress(final Context context, final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getDefaultAddress?userToken=" + UserInfoUtil.getToken(context);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        Address entity = null;
                        JSONObject info = object.getJSONObject("info");
                        JSONArray array = info.getJSONArray("list");
                        if (array.length() > 0) {
                            entity = new Address();
                            JSONObject address = array.getJSONObject(0);
                            entity.setAddress(address.getString("address"));
                            entity.setPhone(address.getString("tel"));
                            entity.setName(address.getString("realName"));
                            entity.setId(address.getString("id"));
                        }
                        if (null != callBack) {
                            callBack.response(entity);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 获取订单详情信息
     *
     * @param context   上下文对象
     * @param orderID   订单ID
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void getOrderInfo(final Context context, String orderID, final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getMyOrderDetail?userToken=" + UserInfoUtil.getToken(context) + "&order_id=" + orderID;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        JSONObject info = object.getJSONObject("info");
                        // 订单
                        OrderEntity oentity = new OrderEntity();
                        oentity.setOrderId(info.getString("id"));// 订单id
                        oentity.setOrderNumber(info.getString("order_sn"));// 订单号
                        oentity.setOrderPrice(info.getString("total_price"));// 订单价格
                        oentity.setState(info.getString("current_status"));// 订单状态
                        // 商品信息
                        WineEntity wine = new WineEntity();
                        wine.setWineName(info.getString("name"));
                        wine.setPrice(info.getString("unit_price"));
                        wine.setOldPrice(info.getString("origin_price"));
                        wine.setWineImg(info.getString("icon"));
                        wine.setDetail(info.getString("description"));
                        // 规格信息
                        WineEntity meal = new WineEntity();
                        meal.setWineName(info.getString("specify_name") + "");
                        // 收货人地址栏信息
                        Address address = new Address();
                        address.setAddress(info.getString("address"));
                        address.setName(info.getString("consignee"));
                        address.setPhone(info.getString("mobile"));
                        // 1，设置地址信息
                        oentity.setAddress(address);
                        // 2，设置商品信息
                        oentity.setGoods(wine);
                        // 3，设置规格信息
                        oentity.setMeal(meal);
                        if (null != callBack) {
                            callBack.response(oentity);
                        }
                        return;

                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));

                }
                return;
            }
        });
    }

    /***
     * 用户反馈接口/帖子举报接口
     *
     * @param context   上下文对象
     * @param userToken 用户登录标识
     * @param type      1用户反馈 2帖子举报
     * @param remark    type为1时传递
     * @param tid       type为2时传递
     */
    public static void feedBack(final Context context, final String userToken,
                                final int type, final String remark, final String tid,
                                final ApiCallBack callback, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/recordUserAction?userToken=" + userToken;
        RequestParams params = new RequestParams();
        params.put("type", type + "");
        if (type == 1) {
            // 用户反馈
            params.put("remark", remark + "");
        } else if (type == 2) {
            // 帖子举报
            params.put("tid", tid + "");
        }
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        if (null != callback) {
                            callback.response(object);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));

                }
                return;
            }
        });
    }

    /***
     * 获取我的地址列表信息
     *
     * @param context   上下文对象
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void getAddressList(final Context context,
                                      final ApiListCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/getAddress?userToken=" + UserInfoUtil.getToken(context);
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int k, Header[] headers, byte[] bytes) {
                try {
                    JSONObject objec = new JSONObject(new String(bytes));
                    if (status(objec)) {
                        ArrayList<Address> list = new ArrayList<Address>();
                        JSONObject info = objec.getJSONObject("info");
                        JSONArray array = info.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            Address add = new Address();
                            JSONObject address = array.getJSONObject(i);
                            add.setAddress(address.getString("address"));
                            add.setPhone(address.getString("tel"));
                            add.setName(address.getString("realName"));
                            add.setId(address.getString("id"));
                            String defaults = address.getString("default");
                            if ("1".equals(defaults)) {
                                add.setIsdefault(true);
                            } else {
                                add.setIsdefault(false);
                            }
                            list.add(add);
                        }
                        if (null != callBack) {
                            callBack.response(list);
                        }
                    } else {
                        if ("401".equals(objec.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 根据某一个地址的地址ID删除某一个地址
     *
     * @param context   上下文对象
     * @param id        地址ID
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void deleteAddressByID(final Context context, String id,
                                         final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/deleteAddress?userToken=" + UserInfoUtil.getToken(context) + "&id=" + id;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject objec = new JSONObject(new String(bytes));
                    if (status(objec)) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                    } else {
                        if ("401".equals(objec.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    /***
     * 设置某一个地址为yoghurt的默认地址
     *
     * @param context   上下文对象
     * @param id        地址ID
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public static void defaultConfigAddress(final Context context, String id,
                                            final ApiCallBack callBack, final ApiException exception) {
        AsyncHttpClient httpClient = getHttpClient();
        String url = URL + "App/setDefaultAddress?userToken=" + UserInfoUtil.getToken(context)
                + "&id=" + id;
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(true);
                        }
                    } else {
                        if ("401".equals(object.getString("error_code"))) {
                            //退出账号，重启应用
                            UserInfoUtil.setLogin(context, false);
                            AppManager.stopApp(AppManager.getPackageName(context), context);
                            AppManager.startApp(AppManager.getPackageName(context), context);
                            return;
                        }
                    }
                    return;

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (null != exception) {
                    exception.error(new String(bytes));
                }
                return;
            }
        });
    }

    //获取验证码
    public static void getLoginCode(String phoneNumber, final ApiCallBack callBack, final ApiException exception) {
        String url = ApiClient.URL + "App/verifyMobile?mobile=" + phoneNumber
                + "&type=getcode";
        AsyncHttpClient httpClient = getHttpClient();
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (null != callBack) {
                        callBack.response(object);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    //登陆接口
    public static void getLogin(String username, String password, final ApiCallBack callBack, final ApiException exception) {
        String url = ApiClient.URL + "App/quickLogin?mobile="
                + username + "&verifyCode=" + password;
        AsyncHttpClient httpClient = getHttpClient();
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    if (null != callBack) {
                        callBack.response(new String(bytes));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    /**
     * 注销操作
     *
     * @param context 上下文
     *                用户标识
     *                通知主线程更新界面
     */
    public static void logout(Context context) {
        // 删除保存的用户token信息
        UserInfoUtil.saveToken(context, "");
        UserInfoUtil.saveUID(context, "");
        UserInfoUtil.saveUserInfo(context, "", "");
        UserInfoUtil.setLogin(context, false);
    }

    //get请求的通用方法
    public static void get(String url, Context context, final ApiCallBack apiCallBack, ApiException apiexception) {
        AsyncHttpClient httpClient = getHttpClient();
        httpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (null != apiCallBack) {
                    apiCallBack.response(new String(bytes));
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
    //post通用请求方式
    public static void post(String url, RequestParams params, final ApiCallBack callBack){
        AsyncHttpClient httpClient=getHttpClient();
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if(null!=callBack){
                    callBack.response(new String(bytes));
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
