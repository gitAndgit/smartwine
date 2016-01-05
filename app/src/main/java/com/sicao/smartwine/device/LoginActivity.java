package com.sicao.smartwine.device;

import android.content.Intent;
import android.os.Bundle;
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
import com.sicao.smartwine.util.UserInfoUtil;

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
    //忘记密码
    TextView forgetPassword;
    //立即注册
    TextView register;
    //手机号输入框
    EditText mPhoneView;

    @Override
    public String setTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public int setView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //隐藏返回键
        toolbar.setNavigationIcon(null);
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
        forgetPassword = (TextView) findViewById(R.id.textView4);
        register = (TextView) findViewById(R.id.textView5);
        login.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        register.setOnClickListener(this);
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
                attemptLogin();
                break;
            case R.id.textView4://忘记密码
                startActivity(new Intent(this, DeviceInfoActivity.class));
                break;
            case R.id.textView5://立即注册
                startActivity(new Intent(this, RegisterActivity.class));
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
}
