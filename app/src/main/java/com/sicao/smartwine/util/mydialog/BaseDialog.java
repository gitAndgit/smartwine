package com.sicao.smartwine.util.mydialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sicao.smartwine.R;

/**
 * Created by android on 2016/5/16.
 * dialog基础样式
 */
public abstract class BaseDialog extends Dialog {
    private Context mContext;
    public TextView tv_content,tv_confirm,tv_cancel;// 警告信息 /确定/取消

    public BaseDialog(Context context, int theme){
        super(context,R.style.myDialogTheme1);
        mContext=context;
        initView();
    }
    protected BaseDialog(Context context) {
        this(context,0);
    }
    //初始化控件
    protected void initView(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view=LayoutInflater.from(mContext).inflate(R.layout.dialog_base,null);
        tv_cancel=(TextView) view.findViewById(R.id.tv_cancel);
        tv_confirm=(TextView) view.findViewById(R.id.tv_confirm);
        tv_content=(TextView) view.findViewById(R.id.tv_content);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用

        lp.width = (int) (d.widthPixels * 0.8);
        lp.height = (int) (d.heightPixels * 0.3);
        lp.alpha = 0.6f;

        dialogWindow.setAttributes(lp);

    }
}
