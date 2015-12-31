package com.sicao.smartwine.libs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import java.net.URISyntaxException;

/**
 * Created by tom on 15/4/3.
 */
public class ShortcutUtil {


    public static boolean hasShortcut(Context context,Uri uri){
        boolean isInstallShortcut = false;
        String AUTHORITY ="com.android.launcher.settings";
        Uri CONTENT_URI = Uri.parse("content://" +AUTHORITY + "/favorites?notify=true");
        Cursor c = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        while (c.moveToNext()){
            try {
                int intentIndex = c.getColumnIndexOrThrow("intent");
                Intent intent = Intent.parseUri(c.getString(intentIndex), 0);
                Uri u =  intent.getData();
                if (u != null){
                    if (u.compareTo(uri) == 0){
                        isInstallShortcut = true;
                    }
                }
            } catch (URISyntaxException e) {
                continue;
            }
        }
        c.close();

        return isInstallShortcut ;
    }

}
