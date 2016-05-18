package com.sicao.smartwine.util;

import android.content.Context;

/**
 * Created by android on 2016/5/14.
 */
public class LToastUtil {
    public static void show(Context context, String hintText){
        LToast toast=LToast.getInstance(context);
        toast.setHint(hintText);
        toast.show();
    }
    public static void show(Context context,String hintText,int time){
        LToast toast=LToast.getInstance(context);
        toast.setHint(hintText);
        toast.show();
        toast.setDuration(time);
    }
}
