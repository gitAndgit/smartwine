package com.sicao.smartwine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.LToastUtil;
import com.sicao.smartwine.util.StringUtil;
import com.sicao.smartwine.util.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android on 2016/5/16.
 */
public class ChangePhone extends BaseActivity {
    private TextView tv_change_phone, tv_code;//更改手机号  /点击获取验证码
    private EditText tv_get_phone, tv_get_code; //获取手机号  获取验证码

    @Override
    public String setTitle() {
        return "手机号码";
    }

    @Override
    protected int setView() {
        return R.layout.activity_changephone;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10020:
                    int count = msg.arg1;
                    tv_code.setText(count + "s");
                    // 释放控制锁
                    if (count == 0) {
                        tv_code.setText("重新获取验证码");
                        tv_code.setClickable(true);
                    }
                    if (count == -100) {
                        tv_code.setText("获取验证码");
                        tv_code.setClickable(true);
                        tv_get_code.setText("");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    //初始化控件
    public void initView() {
        tv_change_phone = (TextView) findViewById(R.id.tv_change_phone);
        tv_code = (TextView) findViewById(R.id.tv_code);
        //
        tv_get_phone = (EditText) findViewById(R.id.tv_get_phone);
        tv_get_code = (EditText) findViewById(R.id.tv_get_code);
        //更改手机号
        tv_change_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mphone = tv_get_phone.getText().toString().trim();
                String code = tv_get_code.getText().toString().trim();
                if (mphone.length() != 11) {
                    LToastUtil.show(ChangePhone.this, "请输入正确的手机号");
                } else {
                    if(code.length()!=4){
                        LToastUtil.show(ChangePhone.this,"请输入正确的手机号");
                    }else{
                        closeInput(tv_get_code);
                        String url = ApiClient.URL + "App/changeMobile?userToken="
                                + UserInfoUtil.getToken(ChangePhone.this) + "&mobile=" + mphone
                                + "&verifyCode=" + code;
                        ApiClient.get(url, ChangePhone.this, new ApiCallBack() {
                            @Override
                            public void response(Object objecte) {
                                try {
                                    JSONObject object = new JSONObject((String) objecte);
                                    if (object.getBoolean("status")) {
                                        LToastUtil.show(ChangePhone.this, "手机修改成功");
                                        Intent intent = new Intent();
                                        intent.putExtra("bindPhone",
                                                mphone);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        LToastUtil.show(ChangePhone.this,
                                                object.getString("info"));
                                        tv_get_code.setText("");
                                    }
                                } catch (JSONException e) {

                                }
                            }
                        }, null);
                    }
                }

            }
        });
        //获取验证码
        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mphone = tv_get_phone.getText().toString().trim();
                if (mphone.length() == 11) {
                    ApiClient.getLoginCode(mphone, new ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            countDown();
                        }
                    }, null);
                } else {
                 LToastUtil.show(ChangePhone.this,"请输入正确的手机号");
                }
            }
        });
    }

    //开启线程进行60s调度
    public void countDown() {
        new Thread() {
            public void run() {

                int count = 60;

                while (count >= 0) {

                    Message msg = mHandler.obtainMessage();

                    msg.what = 10020;

                    msg.arg1 = count;

                    mHandler.sendMessage(msg);

                    count--;

                    Thread.currentThread();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }
}
