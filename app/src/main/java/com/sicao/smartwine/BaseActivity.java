package com.sicao.smartwine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.CircleImageView;

public abstract class BaseActivity extends AppCompatActivity {
    //内容容器参数配置
    protected FrameLayout.LayoutParams params;
    //内容容器
    protected FrameLayout content;
    //Toolbar右侧图标
    protected CircleImageView rightIcon;
    //Toolbar左侧图标
    protected ImageView leftIcon;
    //当前连接ID
    protected int mConnectID = -1;
    //当前设备ID
    protected String mDeviceID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        content = (FrameLayout) findViewById(R.id.content);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(setTitle() == null ? getString(R.string.app_name) : setTitle());
        rightIcon = (CircleImageView) findViewById(R.id.view);
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
        View view = View.inflate(this, setView(), null);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        content.addView(view);
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


    public void setConnectID(int mConnectID) {
        this.mConnectID = mConnectID;
        UserInfoUtil.saveConnectID(this, mConnectID + "");
    }

    public int getConnectID() {
        return Integer.parseInt(UserInfoUtil.getConnectID(this));
    }

    public void setDeviceID(String mDeviceID) {
        this.mDeviceID = mDeviceID;
        UserInfoUtil.saveDeviceID(this, mDeviceID);
    }

    public String getDeviceID() {
        return UserInfoUtil.getDeviceID(this);
    }
}
