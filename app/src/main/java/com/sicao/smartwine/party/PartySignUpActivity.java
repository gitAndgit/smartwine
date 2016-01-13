package com.sicao.smartwine.party;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.pay.Constants;
import com.sicao.smartwine.pay.OrderEntity;
import com.sicao.smartwine.pay.alipay.AlipayManager;
import com.sicao.smartwine.pay.wx.WXPayManager;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.MD5;
import com.sicao.smartwine.util.UserInfoUtil;

/***
 * 活动报名
 *
 * @author techssd
 * @version 1.0.0
 */
public class PartySignUpActivity extends BaseActivity {
    // 参加人数控件和需要支付的金额控件
    private TextView joinPerson, payMoney;
    // 联系人手机号码控件
    private EditText phoneText;
    // 手机号
    private String phone = "";
    // 支付选中事件标志
    private ImageView zhifubao_icon, weixin_icon;
    // 报名人数
    private int joinNumber = 1;
    // 活动的最高人数
    private int maxNumber = 1;
    // 单人金额
    private double money = 0;
    // 活动id
    private String topic_id = "";
    // 活动名称
    private String name = "";
    // 下面的这是用于直接支付的控制流程
    private int type = 2;
    /**
     * 支付宝管理
     **/
    private AlipayManager mAlipayManager;
    /**
     * 微信支付管理
     **/
    private WXPayManager mWXpayManager;
    // 接受微信支付的结果广播
    private WxReturnReceiver receiver;
    //
    private OrderEntity oentity;
    private Handler mHandler;
    private EditText ev_editview;
    private ImageView iv_order_iv;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_party_signup_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_party_sign_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxNumber = Integer.parseInt(getIntent().getExtras().getString(
                "max_person_number"));
        money = Double.parseDouble(getIntent().getExtras().getString(
                "one_person_pay_money"));
        topic_id = getIntent().getExtras().getString("topic_id");
        name = getIntent().getExtras().getString("name");
        ev_editview = (EditText) findViewById(R.id.ev_editview);// 人数输入框
        init();
        if (maxNumber == 1) {
            finish();
        }
        // 支付宝
        mAlipayManager = new AlipayManager(this);
        // 微信支付
        mWXpayManager = new WXPayManager(this);
        // 支付宝支付结果监听
        mAlipayManager.setAlipayListener(new AlipayManager.AlipayInterface() {
            @Override
            public void alipaySupport(boolean support) {
                Toast.makeText(PartySignUpActivity.this, "客户端不支持支付宝插件...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void alipaySuccess(OrderEntity order) {
                Toast.makeText(PartySignUpActivity.this, "支付成功...", Toast.LENGTH_SHORT).show();
                // 通知后台
                makesure(oentity.getGoods().getId(), oentity.getOrderNumber());
            }

            @Override
            public void alipayFail(OrderEntity order) {
                Toast.makeText(PartySignUpActivity.this, "支付失败...", Toast.LENGTH_SHORT).show();
            }
        });
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.OPEN_ALIPAY_CLIENT:// 支付宝
                        mAlipayManager.goPay(oentity, "PartySignUpActivity");
                        break;
                    case Constants.OPEN_WX_CLIENT:// 微信
                        mWXpayManager.goPay(oentity);
                        break;
                }
            }

            ;
        };
        // 注册广播,监控微信支付的结果
        receiver = new WxReturnReceiver();
        IntentFilter filter = new IntentFilter(Constants.WX_PAY_RETURN);
        registerReceiver(receiver, filter);
    }

    private void init() {
        joinPerson = (TextView) findViewById(R.id.textView2);
        joinPerson.setText(joinNumber + "");
        ev_editview.setText(joinNumber + "");
        payMoney = (TextView) findViewById(R.id.money);
        payMoney.setText(money + "");
        phoneText = (EditText) findViewById(R.id.editText1);
        zhifubao_icon = (ImageView) findViewById(R.id.zhifubao_icon);
        weixin_icon = (ImageView) findViewById(R.id.weixin_icon);
        iv_order_iv = (ImageView) findViewById(R.id.iv_order_iv);// 支付状态
        iv_order_iv.setTag("true");
        if (money == 0.00 || money == 0) {
            findViewById(R.id.ll_money).setVisibility(View.GONE);
            findViewById(R.id.zhifubao).setVisibility(View.GONE);
            findViewById(R.id.weixin).setVisibility(View.GONE);
            findViewById(R.id.ll_select_pay).setVisibility(View.GONE);
        }
        iv_order_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_order_iv.getTag().equals("true")) {
                    iv_order_iv.setImageResource(R.drawable.expert_treaty);
                    iv_order_iv.setTag("false");
                } else {
                    iv_order_iv.setImageResource(R.drawable.expert_treaty_p);
                    iv_order_iv.setTag("true");
                }
            }
        });
        // 查看协议页面
        findViewById(R.id.tv_order_xieyi).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        startActivityForResult(new Intent(
//                                PartySignUpActivity.this,
//                                AplayMentActivity.class), 10099);
                    }
                });
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.textView1:// 减人数
                joinNumber = Integer.parseInt(ev_editview.getText().toString()
                        .trim().equals("") ? (1 + "") : ev_editview.getText()
                        .toString().trim());
                if (joinNumber - 1 > 0) {
                    ev_editview.setText(joinNumber - 1 + "");
                    payMoney.setText(money * (joinNumber - 1) + "");
                }
                break;

            case R.id.textView3:// 加人数
                joinNumber = Integer.parseInt(ev_editview.getText().toString()
                        .trim().equals("") ? (0 + "") : ev_editview.getText()
                        .toString().trim());
                if (joinNumber + 1 <= maxNumber) {
                    ev_editview.setText(joinNumber + 1 + "");
                    payMoney.setText(money * (joinNumber + 1) + "");
                }
                break;
            case R.id.zhifubao:// 选中支付宝
                type = 2;
                zhifubao_icon.setImageResource(R.drawable.pay_select);
                weixin_icon.setImageResource(R.drawable.pay_default);
                break;
            case R.id.weixin:// 选中微信
                type = 1;
                zhifubao_icon.setImageResource(R.drawable.pay_default);
                weixin_icon.setImageResource(R.drawable.pay_select);
                break;
            case R.id.join_btn:// 提交
                phone = phoneText.getText().toString().trim();
                if (iv_order_iv.getTag().equals("false")) {
                    Toast.makeText(PartySignUpActivity.this, "要先同意葡萄集支付协议哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.length() == 11) {
                    if (Integer.parseInt(ev_editview.getText().toString().trim()
                            .equals("") ? "0" : ev_editview.getText().toString()
                            .trim()) > 0) {
                        // 下订单
                        commit(topic_id, phone, ev_editview.getText().toString()
                                .trim(), payMoney.getText().toString().trim(), name);
                    } else {
                        Toast.makeText(PartySignUpActivity.this, "人数必须大于0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PartySignUpActivity.this, "请检查您填写的联系号码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /***
     * 生成临时报名信息
     *
     * @param row_id
     * @param tel
     * @param sign_num
     * @param price
     * @param name
     */
    public void commit(final String row_id, final String tel,
                       final String sign_num, final String price, final String name) {
        ApiClient.createTemporarySignUp(this, row_id, tel, sign_num, price, name, new ApiCallBack() {
            @Override
            public void response(Object object) {
                oentity = (OrderEntity) object;
                if (money == 0.00 || money == 0) {
                    makesure(oentity.getGoods().getId(),
                            oentity.getOrderNumber());
                } else {
                    zhifu();
                }
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
    }

    // 去支付
    public void zhifu() {
        if (type == 1) {
            // 微信支付
            // 签名验证

            String sign = MD5.Encode(
                    com.sicao.smartwine.pay.wx.Constants.API_KEY + oentity.getGoods().getWineName()
                            + oentity.getOrderNumber()
                            + oentity.getOrderPrice() + oentity.getState()
                            + com.sicao.smartwine.pay.wx.Constants.MCH_ID
                            + UserInfoUtil.getUID(PartySignUpActivity.this)
                            + Constants.KEY, "UTF-8");
            checkSign(Integer.parseInt(oentity.getOrderId()), sign, "2");
        } else if (type == 2) {
            // 支付宝支付
            // 签名验证
            String sign = MD5.Encode(
                    com.sicao.smartwine.pay.alipay.Constants.RSA_PUBLIC
                            + oentity.getGoods().getWineName()
                            + oentity.getOrderNumber()
                            + oentity.getOrderPrice() + oentity.getState()
                            + com.sicao.smartwine.pay.alipay.Constants.PARTNER
                            + UserInfoUtil.getUID(PartySignUpActivity.this)
                            + Constants.KEY, "UTF-8");
            checkSign(Integer.parseInt(oentity.getOrderId()), sign, "1");
        }
    }

    /***
     * 调用第三方支付之前的订单信息校验功能
     */
    public void checkSign(final int orderId, final String sign,
                          final String type) {
        ApiClient.checkSign(this, orderId + "", sign, type, new ApiCallBack() {
            @Override
            public void response(Object object) {
                OrderEntity entity = (OrderEntity) object;
                oentity.setNotifyurl(entity.getNotifyurl());
                oentity.setOrderNumber(entity.getOrderNumber());
                if ("1".equals(entity.getState())) {
                    //支付宝
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.OPEN_ALIPAY_CLIENT;
                    mHandler.sendMessage(msg);
                } else if ("2".equals(type)) {
                    //微信
                    entity.setOrderNumber(oentity.getOrderNumber());
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.OPEN_WX_CLIENT;
                    mHandler.sendMessage(msg);
                }
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(receiver);
        System.gc();
    }

    /**
     * 接受微信支付的结果回调
     *
     * @author puser
     */
    class WxReturnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.WX_PAY_RETURN.equals(action)) {
                // 微信结果广播
                boolean is = intent.getExtras().getBoolean("wxreturn");
                if (is) {
                    // 支付OK
                    Toast.makeText(PartySignUpActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    makesure(oentity.getGoods().getId(),
                            oentity.getOrderNumber());
                } else {
                    // 支付失败
                    Toast.makeText(PartySignUpActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /***
     * 通知后台确认报名已成功
     */
    public void makesure(final String enroid, String ordernumber) {
        ApiClient.makesurePaySuccess(this, enroid, ordernumber, new ApiCallBack() {
            @Override
            public void response(Object object) {
                sendBroadcast(new Intent(Constants.JOIN_PARTY_SUCCESS));
                finish();
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                Toast.makeText(PartySignUpActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
