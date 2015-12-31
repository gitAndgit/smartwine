package com.sicao.smartwine.libs;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tom on 14/12/26.
 */
public  class Contracts {

    public static String AUTHORITY = "com.sicao.smartwine.libs.RosterProvider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Rosters implements RosterColumns{
        public static final String TABLE_NAME = "Rosters";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public enum  Type{
            DEVICE,
            CONTACT
        }
    }

    public interface RosterColumns extends BaseColumns{
        public static final String JID = "jid";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String STATUS = "status";
        public static final String ONLINE = "online";
    }

    public static final class RosterGroups implements RosterGroupColumns{
        public static final String TABLE_NAME = "RosterGroups";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
    }

    public interface RosterGroupColumns extends BaseColumns{
        public static final String JID = "jid";
        public static final String NAME = "groupName";
    }

    public static final class VCards implements VCardColumns{
        public static final String TABLE_NAME = "VCards";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
    }

    public interface VCardColumns extends BaseColumns{
        public static final String JID = "jid";
        public static final String NAME = "name";
        public static final String NICKNAME = "nickname";
        public static final String FIRSTNAME = "firstname";
        public static final String LASTNAME = "lastname";
        public static final String MIDDLENAME = "middlename";
        public static final String AVATAR  = "avatar";
        public static final String EMAIL_HOME = "emailhome";
        public static final String EMAIL_WORK  = "emailwork";
        /**
         * F:女 M:男
         */
        public static final String MALE = "male";
        /**
         * 格式 2000-01-01
         */
        public static final String BIRTHDAY = "birthday";
        public static final String TIMESTAMP = "timestamp";
    }

}
