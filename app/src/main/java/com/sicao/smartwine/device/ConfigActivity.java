package com.sicao.smartwine.device;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.libs.DeviceMetaData;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.UserInfoUtil;
import com.smartline.life.device.Device;

import java.util.ArrayList;

/***
 * 配置设备
 */
public class ConfigActivity extends BaseActivity implements View.OnClickListener {
    //WIFI  SSID
    TextView SSID;
    //WIFI  password
    EditText password;
    //pre page
    TextView prePage;
    //next page
    TextView nextPage;
    //look  password
    ImageView lookPassword;
    //密码是否正在显示
    boolean passwordShow = false;
    // WIFI 信息
    ScanResult wifi;
    // 链接的ID
    int mConnectID = -1;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_config);
    }

    @Override
    public int setView() {
        return R.layout.activity_config;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectID = getIntent().getExtras().getInt("connectid");
        toolbar.setNavigationIcon(null);
        SSID = (TextView) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        prePage = (TextView) findViewById(R.id.textView9);
        nextPage = (TextView) findViewById(R.id.textView10);
        lookPassword = (ImageView) findViewById(R.id.open_pwd);
        SSID.setOnClickListener(this);
        prePage.setOnClickListener(this);
        nextPage.setOnClickListener(this);
        lookPassword.setOnClickListener(this);
        //
        getContentResolver().registerContentObserver(
                DeviceMetaData.FIND_CONTENT_URI, true, mContentObserver);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.editText1://选择WIFI
                startActivityForResult(new Intent(ConfigActivity.this, WlanActivity.class), 10086);
                break;
            case R.id.textView9://上一步
                finish();
                break;
            case R.id.textView10://下一步
                if (null != wifi && !TextUtils.isEmpty(SSID.getText().toString().trim()) &&
                        !"SSID:".equals(SSID.getText().toString().trim()) &&
                        !TextUtils.isEmpty(password.getText().toString().trim())) {
                    //执行WIFI配置
                    ApiClient.configWIFIToDevice(getApplicationContext(),
                            wifi.SSID, password.getText().toString()
                                    .trim());

                } else {
                    Toast.makeText(ConfigActivity.this, "请正确配置当前WIFI信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.open_pwd://隐藏和查看密码
                if (!passwordShow) {
                    password
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordShow = true;
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordShow = false;
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && null != data) {
            if (requestCode == 10086) {
                wifi = (ScanResult) data.getExtras().get("wifi");
                SSID.setHint("");
                SSID.setText(wifi.SSID);
            }
        }
    }

    private ContentObserver mContentObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 1,获取发现列表里面的数据
            ApiClient.getNativeConfigDeviceList(getApplicationContext(),
                    new ApiListCallBack() {
                        @Override
                        public <T> void response(ArrayList<T> list) {
                            Log.i("huahua","发现设备列表----"+list.size());
                            @SuppressWarnings("unchecked")
                            ArrayList<Device> mlist = (ArrayList<Device>) list;
                            if (!mlist.isEmpty()) {
                                for (final Device d : mlist) {
                                    Log.i("huahua","发现设备列表----"+d.toString());
                                    if (null != wifi) {
                                        if (-1 != mConnectID) {
                                            Log.i("huahua","发现设备列表--连接ID--"+mConnectID);
                                            ApiClient
                                                    .addDevice(
                                                            getApplicationContext(),
                                                            mConnectID,
                                                            d,
                                                            "新朝智能酒柜",
                                                            new ApiCallBack() {
                                                                @Override
                                                                public void response(
                                                                        Object object) {

                                                                    //设置模式--------------------
                                                                    ApiClient
                                                                            .configWorkMode(
                                                                                    ConfigActivity.this,
                                                                                    UserInfoUtil
                                                                                            .getUID(ConfigActivity.this),
                                                                                    d.getJid(),
                                                                                    "手动模式",
                                                                                    "12",
                                                                                    "insert",
                                                                                    null,
                                                                                    null);
                                                                }
                                                            },
                                                            new ApiException() {
                                                                @Override
                                                                public void error(
                                                                        String error) {
                                                                    Toast.makeText(ConfigActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                        }
                                    }
                                }
                                // 关闭页面，进入设备列表页面
                                finish();
                            }

                        }
                    }, new ApiException() {
                        @Override
                        public void error(String error) {
                            Toast.makeText(getApplicationContext(), "请重新配置设备",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObserver);
    }
}
