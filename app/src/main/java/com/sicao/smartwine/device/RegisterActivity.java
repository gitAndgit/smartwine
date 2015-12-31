package com.sicao.smartwine.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.device.entity.RegisterEntity;
import com.sicao.smartwine.device.entity.UserEntity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.UserInfoUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via phone/password.
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

    // 手机号码输入框
    AutoCompleteTextView mPhoneView;
    //密码输入框
    EditText mPasswordView;
    //验证码输入框
    EditText code;
    //获取验证码按钮
    TextView getCode;
    //获取验证码倒计时120秒
    int mine = 120;
    //是否正在执行倒计时
    boolean loading = false;
    //倒计时处理
    Handler mHandler;
    //服务与隐私协议勾选框
    CheckBox mCheckBox;
    //是否勾选了服务与隐私协议
    boolean isCheck = true;

    @Override
    public String setTitle() {
        return getString(R.string.action_register);
    }

    @Override
    public int setView() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState
        );
        // Set up the login form.
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        code = (EditText) findViewById(R.id.code);
        getCode = (TextView) findViewById(R.id.textView6);
        getCode.setOnClickListener(this);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck = true;
                } else {
                    isCheck = false;
                }
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int arg1 = msg.arg1;
                if (arg1 > 0 && arg1 <= 120) {
                    getCode.setText(arg1 + "s");
                }
                if (arg1 == 0) {
                    getCode.setText("重新获取验证码");
                    loading = false;
                    mine = 120;
                }
            }
        };
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mPhoneView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(email)) {
            mPhoneView.setError(getString(R.string.error_invalid_email));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //对验证码进行校验
            String codeStr = code.getText().toString().trim();
            if (TextUtils.isEmpty(codeStr)) {
                Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiClient.checkPhoneCode(this, email, codeStr, new ApiCallBack() {
                @Override
                public void response(Object object) {
                    //执行注册网络代码
                    ApiClient.register(RegisterActivity.this, email, password, new ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            UserEntity user = (UserEntity) object;
                            UserInfoUtil.saveUID(RegisterActivity.this, user.getUid());
                            UserInfoUtil.saveToken(RegisterActivity.this, user.getToken());
                            UserInfoUtil.saveUserInfo(RegisterActivity.this, email, password);
                            //检测该账号在智捷通是否存在
                            ApiClient.checkAccountInfo(RegisterActivity.this, "sicao-" + user.getUid(), "sicao12345678", new ApiCallBack() {
                                @Override
                                public void response(Object object) {
                                    RegisterEntity entity=(RegisterEntity)object;
                                    int code=entity.getCode();
                                    switch (code){
                                        case 200:// 注册成功
                                        case 204:// 用户已存在

                                            //登录智捷通
                                            //进入主页
                                            startActivity(new Intent(RegisterActivity.this, DeviceInfoActivity.class));
                                            break;
                                        case 201:// 用户名不合法（用户名必须包含App前缀）
                                            break;
                                        case 202:// 密码不合法（密码不能为空）
                                            break;
                                        case 203:// accessToken异常
                                            Toast.makeText(RegisterActivity.this, "[203]accessToken异常", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 205:// 用户不存在
                                            break;
                                        case 206:// 设备不存在
                                            break;
                                        case 207:// 注册失败，服务器错误
                                            Toast.makeText(RegisterActivity.this, "[207]登录失败，服务器错误", Toast.LENGTH_SHORT).show();
                                            break;
                                    }

                                }
                            }, new ApiException() {
                                @Override
                                public void error(String error) {
                                    Toast.makeText(RegisterActivity.this, error+"", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //进入主页
                            startActivity(new Intent(RegisterActivity.this, DeviceInfoActivity.class));
                        }
                    }, new ApiException() {
                        @Override
                        public void error(String error) {
                            Toast.makeText(RegisterActivity.this, error+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, new ApiException() {
                @Override
                public void error(String error) {
                    Toast.makeText(RegisterActivity.this, error+"", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.textView6://获取验证码
                if (!loading) {
                    String phone = mPhoneView.getText().toString();
                    if (!isPhoneValid(phone)) {
                        Toast.makeText(RegisterActivity.this, "请正确输入手机号码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mPhoneView.setFocusable(false);//手机号码编辑框不可编辑

                    //获取验证码

                    ApiClient.registerGetCode(RegisterActivity.this, phone, new ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            Toast.makeText(RegisterActivity.this, "验证码已发送,请注意查收", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
                    loading = true;//正在倒计时
                    new Thread() {
                        @Override
                        public void run() {
                            for (int i = 120; i >= 0; i--) {
                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = mine;
                                mHandler.sendMessage(msg);
                                mine--;
                                try {
                                    Thread.currentThread();
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }.start();
                }
                break;
            case R.id.email_sign_in_button://注册按钮
                if (isCheck)
                    attemptLogin();
                else
                    Toast.makeText(RegisterActivity.this, "请认真阅读并勾选同意服务与隐私协议", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

