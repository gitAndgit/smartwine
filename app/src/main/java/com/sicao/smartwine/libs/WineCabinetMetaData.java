package com.sicao.smartwine.libs;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tom on 15/7/17.
 */
public class WineCabinetMetaData implements BaseColumns{

    public static final String TABLE_NAME = WineCabinetService.SERVICE_NAEME;

    public static final String JID = "jid";

    public static final String ON = "_on";

    public static final String LIGHT = "light";

    public static final String COMPRESSOR = "compressor";

    public static final String TEMP = "temp";

    public static final String REAL_TEMP = "realTemp";

    public static final String TIMESTAMP = "timestamp";

    public static final String AUTHORITY = "com.sicao.smartwine.libs.WineCabinetProvider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
}
