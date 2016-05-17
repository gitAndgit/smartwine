package com.sicao.smartwine;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.util.UserInfoUtil;

public abstract class BaseActivity extends AppCompatActivity {
    //内容容器参数配置
    protected FrameLayout.LayoutParams params;
    //内容容器
    protected FrameLayout content;
    //Toolbar右侧图标
    protected SimpleDraweeView rightIcon;
    //Toolbar左侧图标
    protected ImageView leftIcon;
    //当前设备ID
    protected String mDeviceID = "";
    //顶部标题
    TextView title;
    //右侧按钮
    protected TextView rightText;
    public AppContext appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        content = (FrameLayout) findViewById(R.id.content);
        title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(setTitle() == null ? getString(R.string.app_name) : setTitle());
        rightIcon = (SimpleDraweeView) findViewById(R.id.view);
        RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(55,55);
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        rightParams.rightMargin=20;
        rightParams.leftMargin=10;
        rightParams.topMargin=10;
        rightParams.bottomMargin=10;
        rightIcon.setLayoutParams(rightParams);
        leftIcon = (ImageView) findViewById(R.id.left_icon);
        //返回键
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        //右侧按钮
        rightText=(TextView)findViewById(R.id.right_text);
        View view = View.inflate(this, setView(), null);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        content.addView(view);
        appContext=new AppContext();
    }

    /**
     * 设置标题
     *
     * @return
     */
    public abstract String setTitle();

    /***
     * 设置内容layout
     *
     * @return
     */
    protected abstract int setView();

    /***
     * 返回键事件
     */
    public void onBack() {
        finish();
    }

    public void setDeviceID(String mDeviceID) {
        this.mDeviceID = mDeviceID;
        UserInfoUtil.saveDeviceID(this, this.mDeviceID);
    }

    public String getDeviceID() {
        return UserInfoUtil.getDeviceID(this);
    }
    public void setStatus(boolean isStatus){//状态栏是否透明 true时状态栏透明  默认为true
        if(isStatus){
            findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.tv_status).setVisibility(View.GONE);
        }
    }
    public void closeInput(View view){
        /*
		 * 隐藏软键盘 hideSoftInputView
		 */
        try {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

}
