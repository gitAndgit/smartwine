package com.sicao.smartwine.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.device.entity.RegisterEntity;
import com.sicao.smartwine.device.entity.ZjtUserEntity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.AppManager;
import com.sicao.smartwine.util.LToast;
import com.sicao.smartwine.util.LToastUtil;
import com.sicao.smartwine.util.UserInfoUtil;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 登录页面
 *
 * @author mingqi'li
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    //密码输入框
    EditText password;
    //登录按钮
    Button login;
    //手机号输入框
    EditText mPhoneView;
    TextView tv_code;

    @Override
    public String setTitle() {
        return "登录";
    }

    @Override
    public int setView() {
        return R.layout.activity_main;
    }
    protected Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
                        password.setText("");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //隐藏返回键
        leftIcon.setVisibility(View.GONE);
        if (UserInfoUtil.getLogin(this)) {
            finish();
            startActivity(new Intent(LoginActivity.this, DeviceInfoActivity.class));
        }
    }

    /**
     * 控件初始化动作
     */
    public void init() {
        mPhoneView = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText_pwd);
        login = (Button) findViewById(R.id.button);
        tv_code= (TextView) findViewById(R.id.tv_code);//获取验证码
        tv_code.setOnClickListener(this);
        login.setOnClickListener(this);
        rightText.setText("注册");
        rightText.setVisibility(View.VISIBLE);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Store values at the time of the login attempt.
        final String phone = mPhoneView.getText().toString();
        final String passwordstr = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordstr) && !isPasswordValid(passwordstr)) {
            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_password), Toast.LENGTH_SHORT).show();
            focusView = password;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(LoginActivity.this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
            focusView = mPhoneView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
        } else {
            focusView = login;
            focusView.requestFocus();
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //执行葡萄集登录代码
            ApiClient.login(this, phone, passwordstr, new ApiCallBack() {
                @Override
                public void response(Object object) {
                    ZjtUserEntity user = (ZjtUserEntity) object;
                    UserInfoUtil.saveUID(LoginActivity.this, user.getUid());
                    UserInfoUtil.saveToken(LoginActivity.this, user.getToken());
                    UserInfoUtil.saveUserInfo(LoginActivity.this, phone, passwordstr);
                    //检测该账号在智捷通是否存在
                    ApiClient.checkAccountInfo(LoginActivity.this, "sicao-" + user.getUid(), "sicao12345678", new ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            RegisterEntity entity = (RegisterEntity) object;
                            int code = entity.getCode();
                            switch (code) {
                                case 200:// 注册成功
                                case 204:// 用户已存在

                                    //登录智捷通
                                    //进入主页
                                    startActivity(new Intent(LoginActivity.this, DeviceInfoActivity.class));
                                    finish();
                                    break;
                                case 201:// 用户名不合法（用户名必须包含App前缀）
                                    break;
                                case 202:// 密码不合法（密码不能为空）
                                    break;
                                case 203:// accessToken异常
                                    Toast.makeText(LoginActivity.this, "[203]accessToken异常", Toast.LENGTH_SHORT).show();
                                    break;
                                case 205:// 用户不存在
                                    Toast.makeText(LoginActivity.this, "[205]用户不存在", Toast.LENGTH_SHORT).show();
                                    break;
                                case 206:// 设备不存在
                                    break;
                                case 207:// 注册失败，服务器错误
                                    Toast.makeText(LoginActivity.this, "[207]登录失败，服务器错误", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                    }, new ApiException() {
                        @Override
                        public void error(String error) {
                            Toast.makeText(LoginActivity.this, error + "", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }, new ApiException() {
                @Override
                public void error(String error) {
                    Toast.makeText(LoginActivity.this, error + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button://登录
                //attemptLogin();
                final String mphone=mPhoneView.getText().toString().trim();
                final String pw=password.getText().toString().trim();
                if(mphone.length()==11&&pw.length()==4){
                    getLogin(mphone,pw);
                }else{
                    Toast.makeText(this,"验证码或手机号错误",Toast.LENGTH_SHORT);
                }
                break;
            case R.id.textView4://忘记密码
                startActivity(new Intent(this, DeviceInfoActivity.class));
                break;
            case R.id.tv_code://获取验证码
                String mphonee=mPhoneView.getText().toString().trim();
                if(mphonee.length()==11){
                    ApiClient.getLoginCode(mphonee, new ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            try{
                                JSONObject json=(JSONObject) object;
                                LToastUtil.show(LoginActivity.this,json.getString("info"));
                                if(json.getBoolean("status")){
                                    countDown();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    },null);
                }else{
                    LToastUtil.show(this,"请输入正确的手机号");
                }
                break;
            case R.id.textView5://立即注册
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(password);
        if (m.matches()) {
            return false;
        }
        p = Pattern.compile("[a-zA-Z]");
        m = p.matcher(password);
        if (m.matches()) {
            return false;
        }
        return true;
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
            };
        }.start();
    }
    //登陆的方法
    public void getLogin(final String mphone,final String pw){
        AppManager.closeInput_mange(this,login);
        ApiClient.getLogin(mphone, pw, new ApiCallBack() {
            @Override
            public void response(Object object) {
                 try {
                     JSONObject JSONObject=new JSONObject((String) object);
                     if (JSONObject.getBoolean("status")) {
                         JSONObject info = JSONObject.getJSONObject("info");
                         String token = info.getString("userToken");
                         // 保存用户信息
                         UserInfoUtil.saveToken(LoginActivity.this, token);
                         // 记录用户的个人信息
                         UserInfoUtil.saveUserInfo(LoginActivity.this, mphone,
                                 pw);
                         // 记录用户的登录状态
                         UserInfoUtil.setLogin(LoginActivity.this, true);
                         // 用户uid
                         UserInfoUtil.saveUID(LoginActivity.this,
                                 info.getString("uid"));
                         // 记录用户帐号类型
                         UserInfoUtil.saveUSER_TYPE(LoginActivity.this, "normal");
                         // 默认打开消息推送的开关
                         UserInfoUtil.setMsgON(LoginActivity.this, true);
                         // 退出
                         startActivity(new Intent(LoginActivity.this,DeviceInfoActivity.class));
                         finish();
                     }else{
                         Toast.makeText(LoginActivity.this,JSONObject.getJSONObject("info")+"",Toast.LENGTH_SHORT);
                     }
                 }catch (Exception e){
                     e.printStackTrace();
                 }
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                Toast.makeText(LoginActivity.this,"登陆错误，请重试",Toast.LENGTH_SHORT);
            }
        });
    }
}
