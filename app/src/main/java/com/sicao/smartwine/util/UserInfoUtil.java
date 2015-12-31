package com.sicao.smartwine.util;

import android.content.Context;

/***
 * 用户个人帐号信息
 *
 * @author mingqi'li
 */
public class UserInfoUtil {
    /*
     * 用户是否已经登录
     */
    public static String LOGIN = "LOGIN";

    /*
     * 设置用户是否已经登录
     */
    public static boolean getLogin(Context context) {
        return SICAOSharePreferences.getBoolean(context, LOGIN, LOGIN);
    }

    /*
     * 获取用户登录状态
     */
    public static void setLogin(Context context, boolean islogin) {
        SICAOSharePreferences.putBoolean(context, LOGIN, LOGIN, islogin);
    }

    /*
     * 用户帐号信息
     */
    public static String USERNAME = "USERNAME";
    public static String PASSWORD = "PASSWORD";

    /*
     * 获取用户信息
     */
    public static String[] getUserInfo(Context context) {
        String username = SICAOSharePreferences.getString(context, USERNAME, USERNAME);
        String password = SICAOSharePreferences.getString(context, PASSWORD, PASSWORD);
        return new String[]{username, password};
    }

    /*
     * 保存用户信息
     */
    public static void saveUserInfo(Context context, String username,
                                    String password) {
        SICAOSharePreferences.putString(context, USERNAME, USERNAME, username);
        SICAOSharePreferences.putString(context, PASSWORD, PASSWORD, password);
    }

    /*
     * 用户会话令牌信息
     */
    public static String TOKEN = "TOKEN";

    /*
     * 获取用户Token信息
     */
    public static String getToken(Context context) {
        return SICAOSharePreferences.getString(context, TOKEN, TOKEN);
    }

    /*
     * 保存用户Token信息
     */
    public static void saveToken(Context context, String token) {
        SICAOSharePreferences.putString(context, TOKEN, TOKEN, token);
    }

    /*
     * 用户UID
     */
    public static String UID = "UID";

    /*
     * 获取用户Token信息
     */
    public static String getUID(Context context) {
        return SICAOSharePreferences.getString(context, UID, UID);
    }

    /*
     * 保存用户Token信息
     */
    public static void saveUID(Context context, String uid) {
        SICAOSharePreferences.putString(context, UID, UID, uid);
    }

    /*
     * 用户USER_TYPE
     */
    public static String USER_TYPE = "USER_TYPE";

    /*
     * 获取用户USER_TYPE信息
     */
    public static String getUSER_TYPE(Context context) {
        return SICAOSharePreferences.getString(context, USER_TYPE, USER_TYPE);
    }

    /*
     * 保存用户USER_TYPE信息
     */
    public static void saveUSER_TYPE(Context context, String usertype) {
        SICAOSharePreferences.putString(context, USER_TYPE, USER_TYPE, usertype);
    }

    /*
     * 用户USER_TYPE
     */
    public static String USER_ADDRESS = "USER_ADDRESS";

    /*
     * 获取用户USER_TYPE信息
     */
    public static String getUSER_ADDRESS(Context context) {
        return SICAOSharePreferences.getString(context, USER_ADDRESS, USER_ADDRESS);
    }

    /*
     * 保存用户USER_TYPE信息
     */
    public static void saveUSER_ADDRESS(Context context, String address) {
        SICAOSharePreferences.putString(context, USER_ADDRESS, USER_ADDRESS, address);
    }

    public static String USER_DEFLAULTNAME = "USER_DEFLAULTNAME";

    /*
     * 保存用户的地址名字信息
     */
    public static void saveDefaultName(Context context, String name) {
        SICAOSharePreferences.putString(context, USER_DEFLAULTNAME, USER_DEFLAULTNAME, name);
    }

    /*
     * 得到用户的地址名字信息
     */
    public static String getDefaultName(Context context) {
        return SICAOSharePreferences.getString(context, USER_DEFLAULTNAME, USER_DEFLAULTNAME);
    }

    public static String USER_DEFLAULTPHONE = "USER_DEFLAULTPHONE";

    /*
     * 保存用户的地址名字信息
     */
    public static void saveDefaultPhone(Context context, String phone) {
        SICAOSharePreferences.putString(context, USER_DEFLAULTPHONE, USER_DEFLAULTPHONE, phone);
    }

    /*
     * 得到用户的地址名字信息
     */
    public static String getDefaultPhone(Context context) {
        return SICAOSharePreferences.getString(context, USER_DEFLAULTPHONE, USER_DEFLAULTPHONE);
    }

    /*
     * 是否有新消息
     */
    public static String HAS_MSG = "HAS_MSG";

    /*
     * 获取是否有新消息
     */
    public static String hasMsg(Context context) {
        return SICAOSharePreferences.getString(context, HAS_MSG, HAS_MSG);
    }

    /*
     * 保存是否有新消息
     */
    public static void saveMsg(Context context, String msg) {
        SICAOSharePreferences.putString(context, HAS_MSG, HAS_MSG, msg);
    }

    /*
     * 是否打开消息通知
     */
    public static String OPEN_MSG_NOTIFICATION = "OPEN_MSG_NOTIFICATION";

    /*
     * 获取消息通知的打开状态
     */
    public static boolean getMsgON(Context context) {
        return SICAOSharePreferences.getBoolean(context, OPEN_MSG_NOTIFICATION,
                OPEN_MSG_NOTIFICATION);
    }

    /*
     * 设置消息通知的开关
     */
    public static void setMsgON(Context context, boolean on) {
        SICAOSharePreferences.putBoolean(context, OPEN_MSG_NOTIFICATION, OPEN_MSG_NOTIFICATION,
                on);
    }

    /*
     * 保存话题下帖子的最大ID
     */
    public static String TALK_MAX_ID = "TALK_MAX_ID";

    /*
     * 获取话题下帖子的最大ID
     */
    public static int getTalkMaxId(Context context, String key) {
        return SICAOSharePreferences.getInt(context, TALK_MAX_ID, key);
    }

    /*
     * 设置话题下帖子的最大ID
     */
    public static void saveTalkMaxId(Context context, String key, int values) {
        SICAOSharePreferences.putInt(context, TALK_MAX_ID, key, values);
    }

    /*
     * 是否有保存该话题下最大ID的KEY值
     */
    public static boolean containsTalkKey(Context context, String key) {
        try {
            return SICAOSharePreferences.contains(context, TALK_MAX_ID, key);
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * 引导页是否打开过
     */
    public static String INDEX_PAGE_OPEN = "INDEX_PAGE_OPEN";

    /*
     * 引导页是否打开过
     */
    public static boolean getIndexOpen(Context context) {
        return SICAOSharePreferences.getBoolean(context, INDEX_PAGE_OPEN, INDEX_PAGE_OPEN);
    }

    /*
     * 引导页是否打开过
     */
    public static void saveIndexOpen(Context context, boolean open) {
        SICAOSharePreferences.putBoolean(context, INDEX_PAGE_OPEN, INDEX_PAGE_OPEN, open);
    }

    // 保存索搜数据
    public static String SEARCH_HISTORY = "SEARCH_HISTORY";

    // 保存索搜数据
    public static String getSearchHistory(Context context) {
        return SICAOSharePreferences.getString(context, SEARCH_HISTORY, SEARCH_HISTORY);
    }

    // 保存索搜数据
    public static void saveSearchHistory(Context context, String Msg) {
        SICAOSharePreferences.putString(context, SEARCH_HISTORY, SEARCH_HISTORY, Msg);
    }

    // 保存用户头像
    public static String USER_AVATAR = "USER_AVATAR";

    // 保存用户头像
    public static String getUserAvatar(Context context) {
        return SICAOSharePreferences.getString(context, USER_AVATAR, USER_AVATAR);
    }

    // 保存用户头像
    public static void saveUserAvatar(Context context, String avatar) {
        SICAOSharePreferences.putString(context, USER_AVATAR, USER_AVATAR, avatar);
    }

    // 保存用户昵称
    public static String USER_NICKNAME = "USER_NICKNAME";

    // 保存用户昵称
    public static String getUserNickName(Context context) {
        return SICAOSharePreferences.getString(context, USER_NICKNAME, USER_NICKNAME);
    }

    // 保存用户昵称
    public static void saveUserNickName(Context context, String avatar) {
        SICAOSharePreferences.putString(context, USER_NICKNAME, USER_NICKNAME, avatar);
    }

    // 获取支付宝信息
    public static String PAY_PAY = "PAY_PAY";

    public static String getPay(Context context) {
        return SICAOSharePreferences.getString(context, PAY_PAY, PAY_PAY);
    }

    // 保存支付宝信息
    public static void savePay(Context context, String pay) {
        SICAOSharePreferences.putString(context, PAY_PAY, PAY_PAY, pay);
    }

    // 获取银行信息
    public static String BANK_BANK = "BANK_BANK";

    public static String getBank(Context context) {
        return SICAOSharePreferences.getString(context, BANK_BANK, BANK_BANK);
    }

    // 保存银行信息
    public static void saveBank(Context context, String bank) {
        SICAOSharePreferences.putString(context, BANK_BANK, BANK_BANK, bank);
    }

    // 获取当前设备ID
    public static String SMART_WINE_DEVICE_ID = "SMART_WINE_DEVICE_ID";

    public static String getDeviceID(Context context) {
        return SICAOSharePreferences.getString(context, SMART_WINE_DEVICE_ID, SMART_WINE_DEVICE_ID);
    }

    // 保存当前设备ID
    public static void saveDeviceID(Context context, String deviceID) {
        SICAOSharePreferences.putString(context, SMART_WINE_DEVICE_ID, SMART_WINE_DEVICE_ID, deviceID);
    }

    // 获取当前连接ID
    public static String SMART_WINE_CONNECT_ID = "SMART_WINE_CONNECT_ID";

    public static String getConnectID(Context context) {
        return SICAOSharePreferences.getString(context, SMART_WINE_CONNECT_ID, SMART_WINE_CONNECT_ID);
    }

    // 保存当前设备ID
    public static void saveConnectID(Context context, String connectID) {
        SICAOSharePreferences.putString(context, SMART_WINE_CONNECT_ID, SMART_WINE_CONNECT_ID, connectID);
    }
}
