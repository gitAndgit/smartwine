package com.sicao.smartwine.libs;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tom on 15/5/5.
 */
public class DeviceMetaData implements BaseColumns{
     //设备ID
    public static final String JID = "jid";
    //类型
    public static final String TYPE = "type";
     //名称
    public static final String NAME = "name";
    //昵称
    public static final String NICK = "nick";
    //模式
    public static final String MODEL = "model";
     //是否在线
    public static final String ONLINE = "online";
     //版本
    public static final String VERSION  = "version";

    public static final String RESOURCE = "resource";

    public static final String MAN = "manufacturer";

    public static final String CATEGORY  = "category";
    //设备分组
    public static final String GROUP = "rgroup";
   //设备地址
    public static final String MAC = "mac";

    public static final String IPV4 = "ipv4";

    public static final String IPV6 = "ipv6";

    public static final String PORT = "port";
    //WIFI名称
    public static final String SSID = "ssid";
     //WIFI地址
    public static final String BSSID = "bssid";
    //WIFI密码
    public static final String WIFIPASSWD = "wifi_passwd";

    public static final String STATUS = "status";

    public static final String AVATAR  = "avatar";

    public static final String OS  = "os";

    public enum Status{
        NONE,
        ACCESSPOINT,
        LAN,
        WAN
    }

    public static final String TABLE_NAME = "devices";
    public static final String FIND_TABLE_NAME = "find_devices";

    public static String AUTHORITY = "com.sicao.smartwine.libs.DeviceProvider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
    public static final Uri FIND_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, FIND_TABLE_NAME);
}
