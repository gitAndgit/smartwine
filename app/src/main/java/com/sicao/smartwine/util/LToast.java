package com.sicao.smartwine.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.R;

/**
 * Created by android on 2016/5/14.
 * 自定义弹出框
 */
public class LToast {
    // 提示的文字
    private String hint;

    // 上下文对象
    private Context mContext;

    // 钩子
    private LayoutInflater mInflater;

    // Toast布局
    private View mContent;

    // textView控件
    private TextView mText;

    // 系统提示
    private Toast mToast;

    private static LToast instance;

    public static synchronized LToast getInstance(Context context) {
        if (null != instance) {
            return instance;
        } else {
            return new LToast(context);
        }
    }


    private LToast(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mContent = mInflater.inflate(R.layout.ltoast, null);
        mText = (TextView) mContent.findViewById(R.id.ltoast_textview);
        mToast = new Toast(mContext.getApplicationContext());
        mToast.setGravity(Gravity.BOTTOM, 0, 100);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mContent);
    }

    /***
     *
     * @param t
     */
    public void setDuration(int time) {
        mToast.setDuration(time);
    }

    /***
     * 设置提示文字
     *
     * @param hint
     */
    public void setHint(String hint) {
        this.hint = hint;
        if (null != mText)
            mText.setText(this.hint);
    }

    public void show() {
        if (null != mToast)
            mToast.show();
    }
}
