package com.sicao.smartwine.device;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.libs.WineCabinetService;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.BottomPopupWindow;
import com.smartline.life.core.XMPPManager;
import com.smartline.life.device.Device;
import com.smartline.life.iot.CommandService;
import com.smartline.life.iot.IoTService;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.roster.Roster;

/***
 * 酒柜设置
 */
public class SmartSetActivity extends BaseActivity implements View.OnClickListener {
    //酒柜名称
    EditText wineName;
    //工作模式名称
    TextView mWorkName;
    String workName = "";
    //设置温度
    TextView mWorkTemp;
    //工作模式
    BottomPopupWindow workMode;
    //设置温度
    BottomPopupWindow workTemp;
    // 设备信息
    WineCabinetService mCabinet;
    //保存按钮
    Button commit;
    //酒柜的名称
    String smartWineName = "";
    //酒柜的模式名称
    String smartWineMode = "";
    //酒柜的模式温度
    String smartModeTemp = "";

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.work_mode_name://工作模式
                workMode = new BottomPopupWindow(SmartSetActivity.this);
                workMode.update(getResources().getStringArray(R.array.device_model));
                workMode.showAtLocation(content, Gravity.BOTTOM,
                        0, 0);
                workMode.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                    @Override
                    public void onClick(String value) {
                        mWorkName.setText(value);
                        workMode.dismiss();
                    }
                });
                break;
            case R.id.work_mode_temp_name://设置的温度
                workTemp = new BottomPopupWindow(SmartSetActivity.this);
                workTemp.update(getResources().getStringArray(R.array.device_temp));
                workTemp.showAtLocation(content, Gravity.BOTTOM,
                        0, 0);
                workTemp.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                    @Override
                    public void onClick(String value) {
                        mWorkTemp.setText(value);
                        workTemp.dismiss();
                        ;
                    }
                });
                break;
            case R.id.button2://提交
                //检测酒柜名称是否已经做了修改
                if (!"".equals(wineName.getText().toString().trim()) && !smartWineName.equals(wineName.getText().toString().trim())) {
                    //修改酒柜名
                    Device d = new Device();
                    d.setJid(getDeviceID());
                    LifeClient.addDevice(SmartSetActivity.this, LifeClient.getConnectionId(), d, wineName.getText().toString().trim(),
                            new com.sicao.smartwine.api.LifeClient.ApiCallBack() {
                        @Override
                        public void response(Object object) {
                            setResult(RESULT_OK);
                            Toast.makeText(SmartSetActivity.this, "酒柜名称修改成功", Toast.LENGTH_SHORT).show();
                            //修改了酒柜模式
                            if (!smartWineMode.equals(mWorkName.getText().toString().trim())) {
                                //修改酒柜模式对应的温度
                                if (!"".equals(smartModeTemp)) {
                                    //该酒柜已经有工作模式,并且有对应的工作温度
                                    if (Integer.parseInt(smartModeTemp) > Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                        //温度降低
                                        for (int i = Integer.parseInt(smartModeTemp); i < Integer.parseInt(mWorkTemp.getText().toString().trim()); i--) {
                                            if (i >= Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                                mCabinet.setTemp(i);
                                                mCabinet.update();
                                            }
                                        }
                                        ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                                getDeviceID(), mWorkName.getText().toString().trim(),
                                                mWorkTemp.getText().toString().trim() , "update", new ApiCallBack() {
                                                    @Override
                                                    public void response(Object object) {
                                                        setResult(RESULT_OK);
                                                        finish();
                                                        Toast.makeText(SmartSetActivity.this, "酒柜模式修改成功", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new ApiException() {
                                                    @Override
                                                    public void error(String error) {
                                                        Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        //温度升高
                                        for (int i = Integer.parseInt(smartModeTemp); i <= Integer.parseInt(mWorkTemp.getText().toString().trim()); i++) {
                                            if (i <= Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                                mCabinet.setTemp(i);
                                                mCabinet.update();
                                            }
                                        }
                                        ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                                getDeviceID(), mWorkName.getText().toString().trim(),
                                                mWorkTemp.getText().toString().trim(), "update", new ApiCallBack() {
                                                    @Override
                                                    public void response(Object object) {
                                                        setResult(RESULT_OK);
                                                        finish();
                                                        Toast.makeText(SmartSetActivity.this, "酒柜模式修改成功", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new ApiException() {
                                                    @Override
                                                    public void error(String error) {
                                                        Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {//该酒柜没有工作模式或者说是第一次配置该设备,尚未有配置工作模式或者是数据丢失状态(概率较低)
                                    ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                            getDeviceID(), mWorkName.getText().toString().trim(),
                                            mWorkTemp.getText().toString().trim(), "insert", new ApiCallBack() {
                                                @Override
                                                public void response(Object object) {
                                                    setResult(RESULT_OK);
                                                    finish();
                                                    Toast.makeText(SmartSetActivity.this, "酒柜模式修改成功", Toast.LENGTH_SHORT).show();
                                                }
                                            }, new ApiException() {
                                                @Override
                                                public void error(String error) {
                                                    Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }else{
                                //没有修改酒柜模式
                                finish();
                            }
                        }
                    }, new com.sicao.smartwine.api.LifeClient.ApiException() {
                        @Override
                        public void error(String error) {
                            Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if ("".equals(mWorkName.getText().toString().trim())) {
                    Toast.makeText(SmartSetActivity.this, "请设置酒柜模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                //修改了酒柜模式
                if (!smartWineMode.equals(mWorkName.getText().toString().trim())) {
                    //修改酒柜模式对应的温度
                    if (!"".equals(smartModeTemp)) {
                        //该酒柜已经有工作模式,并且有对应的工作温度
                        if (Integer.parseInt(smartModeTemp) > Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                            //温度降低
                            for (int i = Integer.parseInt(smartModeTemp); i < Integer.parseInt(mWorkTemp.getText().toString().trim()); i--) {
                                if (i >= Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                    mCabinet.setTemp(i);
                                    mCabinet.update();
                                }
                            }
                            ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                    getDeviceID(), mWorkName.getText().toString().trim(),
                                    mWorkTemp.getText().toString().trim() , "update", new ApiCallBack() {
                                        @Override
                                        public void response(Object object) {
                                            setResult(RESULT_OK);
                                            finish();
                                            Toast.makeText(SmartSetActivity.this, "酒柜模式修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new ApiException() {
                                        @Override
                                        public void error(String error) {
                                            Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            //温度升高
                            for (int i = Integer.parseInt(smartModeTemp); i <= Integer.parseInt(mWorkTemp.getText().toString().trim()); i++) {
                                if (i <= Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                    mCabinet.setTemp(i);
                                    mCabinet.update();
                                }
                            }
                            ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                    getDeviceID(), mWorkName.getText().toString().trim(),
                                    mWorkTemp.getText().toString().trim(), "update", new ApiCallBack() {
                                        @Override
                                        public void response(Object object) {
                                            setResult(RESULT_OK);
                                            finish();
                                            Toast.makeText(SmartSetActivity.this, "酒柜模式修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new ApiException() {
                                        @Override
                                        public void error(String error) {
                                            Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {//该酒柜没有工作模式或者说是第一次配置该设备,尚未有配置工作模式或者是数据丢失状态(概率较低)
                        ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                getDeviceID(), mWorkName.getText().toString().trim(),
                                mWorkTemp.getText().toString().trim(), "insert", new ApiCallBack() {
                                    @Override
                                    public void response(Object object) {
                                        setResult(RESULT_OK);
                                        finish();
                                        Toast.makeText(SmartSetActivity.this, "酒柜模式修改成功", Toast.LENGTH_SHORT).show();
                                    }
                                }, new ApiException() {
                                    @Override
                                    public void error(String error) {
                                        Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    //酒柜模式没有变化
                    if (!smartModeTemp.equals(mWorkTemp.getText().toString().trim())) {
                        if (Integer.parseInt(smartModeTemp) > Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                            //温度降低
                            for (int i = Integer.parseInt(smartModeTemp); i <=Integer.parseInt(mWorkTemp.getText().toString().trim()); i--) {
                                if (i >= Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                    mCabinet.setTemp(i);
                                    mCabinet.update();
                                }
                            }
                            //修改酒柜模式对应的温度
                            ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                    getDeviceID(), mWorkName.getText().toString().trim(),
                                    mWorkTemp.getText().toString().trim(), "update", new ApiCallBack() {
                                        @Override
                                        public void response(Object object) {
                                            setResult(RESULT_OK);
                                            finish();
                                            Toast.makeText(SmartSetActivity.this, "酒柜温度修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new ApiException() {
                                        @Override
                                        public void error(String error) {
                                            Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            //温度升高
                            for (int i = Integer.parseInt(smartModeTemp); i <= Integer.parseInt(mWorkTemp.getText().toString().trim()); i++) {
                                if (i <= Integer.parseInt(mWorkTemp.getText().toString().trim())) {
                                    mCabinet.setTemp(i);
                                    mCabinet.update();
                                }
                            }
                            //修改酒柜模式对应的温度
                            ApiClient.configWorkMode(SmartSetActivity.this, UserInfoUtil.getUID(SmartSetActivity.this),
                                    getDeviceID(), mWorkName.getText().toString().trim(),
                                    mWorkTemp.getText().toString().trim(), "update", new ApiCallBack() {
                                        @Override
                                        public void response(Object object) {
                                            setResult(RESULT_OK);
                                            finish();
                                            Toast.makeText(SmartSetActivity.this, "酒柜温度修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new ApiException() {
                                        @Override
                                        public void error(String error) {
                                            Toast.makeText(SmartSetActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
                break;
            case R.id.view://重置
                if (-1 != LifeClient.getConnectionId()) {
                    if (LifeClient.getConnection().isConnected()) {
                        CommandService command = new CommandService(getDeviceID(),
                                LifeClient.getConnection());

                        command.restore(new IoTService.Callback() {
                            @Override
                            public void complete(IoTService service) {
                                new Thread() {
                                    public void run() {
                                        Roster roster = Roster
                                                .getInstanceFor(LifeClient.getConnection());
                                        try {
                                            roster.removeEntry(roster
                                                    .getEntry(getDeviceID()));
                                            Intent intent=new Intent();
                                            intent.putExtra("reset_device",true);
                                            setResult(RESULT_CANCELED, intent);
                                            finish();
                                        } catch (NotLoggedInException e) {
                                            e.printStackTrace();
                                        } catch (NoResponseException e) {
                                            e.printStackTrace();
                                        } catch (XMPPErrorException e) {
                                            e.printStackTrace();
                                        } catch (NotConnectedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    ;
                                }.start();
                            }

                            @Override
                            public void exception(Exception exception) {
                                Toast.makeText(getApplicationContext(), "重置失败",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        ((XMPPManager) getApplicationContext().getSystemService(
                                XMPPManager.XMPP_SERVICE)).connect(LifeClient.getConnectionId());
                    }

                }
                break;
        }
    }

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_smart_set);
    }

    @Override
    protected int setView() {
        return R.layout.activity_smart_set;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        // 设备监控
        if (null != mCabinet) {
            mCabinet = null;
        }
        mCabinet = new WineCabinetService(getDeviceID(), LifeClient.getConnection());
        //右侧重置按钮
        rightIcon.setImageResource(R.drawable.ic_launcher);
        rightIcon.setOnClickListener(this);
        //
        smartWineName = getIntent().getExtras().getString("smartWineName");
        wineName.setText(smartWineName);
        //酒柜的模式名称

        smartWineMode = getIntent().getExtras().getString("smartWineMode");
        mWorkName.setText(smartWineMode);
        //酒柜的模式温度
        smartModeTemp = getIntent().getExtras().getString("smartModeTemp");
        mWorkTemp.setText(smartModeTemp);
    }

    public void init() {
        wineName = (EditText) findViewById(R.id.editText);
        mWorkName = (TextView) findViewById(R.id.work_mode_name);
        mWorkTemp = (TextView) findViewById(R.id.work_mode_temp_name);
        mWorkName.setOnClickListener(this);
        mWorkTemp.setOnClickListener(this);
        commit = (Button) findViewById(R.id.button2);
        commit.setOnClickListener(this);
    }
}

