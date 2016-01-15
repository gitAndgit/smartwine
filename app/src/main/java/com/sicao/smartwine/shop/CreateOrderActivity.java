package com.sicao.smartwine.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.pay.Constants;
import com.sicao.smartwine.pay.OrderEntity;
import com.sicao.smartwine.pay.alipay.AlipayManager;
import com.sicao.smartwine.pay.wx.WXPayManager;
import com.sicao.smartwine.shop.entity.WineEntity;
import com.sicao.smartwine.user.entity.Address;
import com.sicao.smartwine.user.entity.InviteEntity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.MD5;
import com.sicao.smartwine.util.StringUtil;
import com.sicao.smartwine.util.UserInfoUtil;

import org.apache.http.Header;
import org.json.JSONObject;

/***
 * 下订单页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class CreateOrderActivity extends BaseActivity {
    /**
     * 订单
     **/
    private OrderEntity oentity;

    // 收货地址信息
    private Address mAddress;
    // 收货地址显示控件
    private TextView mName, mAddressText;
    // 商品简介
    private WineEntity wine;
    // 商品图片控件
    private ImageView mGood;
    // 商品名称，价格和简介控件
    private TextView mGoodName, mMealDesc;
    // 套餐类型
    private WineEntity meal;
    // 主人寄语
    private EditText mFuck;
    // 底部显示总价控件
    private TextView mAllMoney;
    // 当没有默认地址信息时的显示控件
    private TextView mAddAddress;
    // 当有默认地址信息时显示的布局
    private RelativeLayout mLinearlayout;
    // 优惠券信息显示控件
    private TextView mInvite;
    // 选择的优惠券
    private InviteEntity entity = null;
    private LinearLayout sr_content;
    /**
     * 支付宝管理
     **/
    private AlipayManager mAlipayManager;
    /**
     * 微信支付管理
     **/
    private WXPayManager mWXpayManager;
    // 是否使用的是支付宝
    private boolean isXaplay = true;
    // 接受微信支付的结果广播
    private WxReturnReceiver receiver;
    private Handler mHandler;
    // 单价金额
    private double money = 1;
    // 商品购买数量
    private int joinNumber = 1;
    // 总金额
    private TextView allMoney;
    // 商品数量
    private EditText goodNumber;
    // 支付宝和微信的选择标记
    private ImageView zhifubao_icon, weixin_icon;
    // 总计多少钱
    private double allmoney = 0;
    private TextView tv_order_xieyi;
    private ImageView iv_order_iv;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_shop_createorder_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_create_order;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        // 商品信息
        wine = (WineEntity) getIntent().getExtras().get("wine");
        // 套餐信息
        meal = (WineEntity) getIntent().getExtras().get("meal");
        goodNumber.setText(meal.getOldPrice());// 选填了几瓶酒
        // 商品简介部分赋值
        set();
        // 获取默认地址
        getMyDefaultAddress();
        // 标记下订单支付流程页面
        AppContext.pay.add(this);
        // 注册广播,监控微信支付的结果
        receiver = new WxReturnReceiver();
        IntentFilter filter = new IntentFilter(Constants.WX_PAY_RETURN);
        registerReceiver(receiver, filter);
    }

    /*
     * 控件初始化
	 */
    @SuppressLint("HandlerLeak")
    public void init() {
        mName = (TextView) findViewById(R.id.name);
        mAddressText = (TextView) findViewById(R.id.address);
        mAddAddress = (TextView) findViewById(R.id.address_text);
        mGood = (ImageView) findViewById(R.id.imageView1);
        mMealDesc = (TextView) findViewById(R.id.textView2);
        mGoodName = (TextView) findViewById(R.id.textView1);
        mFuck = (EditText) findViewById(R.id.editText1);
        mAllMoney = (TextView) findViewById(R.id.textView6);
        allMoney = (TextView) findViewById(R.id.all_money);
        mLinearlayout = (RelativeLayout) findViewById(R.id.address_layout);
        goodNumber = (EditText) findViewById(R.id.tv_number);
        zhifubao_icon = (ImageView) findViewById(R.id.zhifubao_icon);
        weixin_icon = (ImageView) findViewById(R.id.weixin_icon);
        mInvite = (TextView) findViewById(R.id.invite_use_text);
        //
        sr_content = (LinearLayout) findViewById(R.id.sr_content);
        tv_order_xieyi = (TextView) findViewById(R.id.tv_order_xieyi);// 支付协议
        iv_order_iv = (ImageView) findViewById(R.id.iv_order_iv);// 支付状态
        iv_order_iv.setTag("true");
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.OPEN_ALIPAY_CLIENT:// 支付宝
                        mAlipayManager.goPay(oentity, "CreateOrderAcivity");
                        break;
                    case Constants.OPEN_WX_CLIENT:// 微信
                        mWXpayManager.goPay(oentity);
                        break;
                }
            }

            ;
        };
        // 购买数量
        goodNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!"".equals(s.toString().trim())) {
                    joinNumber = Integer.parseInt(s.toString().trim());
                    meal.setOldPrice(joinNumber + "");
                    allMoney.setText("￥"
                            + StringUtil.double2(money * joinNumber) + "");
                    mAllMoney.setText("总计:￥"
                            + StringUtil.double2(money * joinNumber) + "");
                    allmoney = Double.parseDouble(StringUtil.double2(money
                            * joinNumber));
                } else {
                    goodNumber.setText("1");
                    meal.setOldPrice("1");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        tv_order_xieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(CreateOrderAcivity.this,
//                        AplayMentActivity.class), 10099);
            }
        });
    }

    /*
     * 简介部分赋值
     */
    public void set() {
        mGoodName.setText(wine.getWineName());
        mMealDesc.setText("￥" + meal.getPrice());// 商品单价
        money = Double.parseDouble(meal.getPrice());
        mAllMoney.setText("总计 :￥" + money + "");
        allMoney.setText("￥" + money);
        allmoney = Double.parseDouble(StringUtil.double2(money * joinNumber));

        AppContext.imageLoader.displayImage(wine.getWineImg(), mGood,
                AppContext.gallery);
        // 支付宝
        mAlipayManager = new AlipayManager(this);
        // 微信支付
        mWXpayManager = new WXPayManager(this);
        // 支付宝支付结果监听
        mAlipayManager.setAlipayListener(new AlipayManager.AlipayInterface() {
            @Override
            public void alipaySupport(boolean support) {
                Toast.makeText(CreateOrderActivity.this, "客户端不支持支付宝插件...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void alipaySuccess(OrderEntity order) {
                Toast.makeText(CreateOrderActivity.this, "支付成功...", Toast.LENGTH_SHORT).show();
                closeOtherActivity();
            }

            @Override
            public void alipayFail(OrderEntity order) {
                Toast.makeText(CreateOrderActivity.this, "支付失败...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * 控件点击事件
     */
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img1:
            case R.id.create_order_top_layout:
//                if (null == mAddress) {
//                    startActivityForResult(new Intent(this,
//                            AddAddressActivity.class), Constants.REVISE_ADDRESS);
//                } else {
//                    startActivityForResult(
//                            new Intent(this, MyAddressActivity.class),
//                            Constants.REVISE_ADDRESS);
//                }
                break;
            case R.id.textView5:// 提交订单

                if (iv_order_iv.getTag().equals("false")) {
                    Toast.makeText(CreateOrderActivity.this, "要先同意葡萄集支付协议哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null != mAddress) {
                    createOrder();
                } else {
                    Toast.makeText(CreateOrderActivity.this, "请填写收货地址信息", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.invite_layout:// 选择代金券
//                Intent intent = new Intent(this, SelectCashcouponActivity.class);
//                intent.putExtra("use", true);
//                startActivityForResult(intent, Constants.SELECT_INVITE);
                break;

            case R.id.jian:// 减人数
                joinNumber = Integer.parseInt(goodNumber.getText().toString()
                        .trim().equals("") ? (1 + "") : goodNumber.getText()
                        .toString().trim());
                if (joinNumber - 1 > 0) {
                    goodNumber.setText(joinNumber - 1 + "");
                    meal.setOldPrice(goodNumber.getText().toString().trim() + "");
                    allMoney.setText("￥"
                            + StringUtil.double2(money
                            * Integer
                            .parseInt(goodNumber.getText()
                                    .toString().trim().equals("") ? (1 + "")
                                    : goodNumber.getText()
                                    .toString().trim()))
                            + "");
                    mAllMoney
                            .setText("总计:￥"
                                    + StringUtil.double2(money
                                    * Integer
                                    .parseInt(goodNumber.getText()
                                            .toString().trim()
                                            .equals("") ? (1 + "")
                                            : goodNumber.getText()
                                            .toString()
                                            .trim())) + "");
                    allmoney = Double.parseDouble(StringUtil.double2(money
                            * (joinNumber - 1)));
                }
                break;

            case R.id.jia:// 加人数
                joinNumber = Integer.parseInt(goodNumber.getText().toString()
                        .trim().equals("") ? (1 + "") : goodNumber.getText()
                        .toString().trim());
                if (joinNumber + 1 <= Integer.MAX_VALUE) {
                    goodNumber.setText(joinNumber + 1 + "");
                    meal.setOldPrice(goodNumber.getText().toString().trim() + "");
                    allMoney.setText("￥"
                            + StringUtil.double2(money
                            * Integer
                            .parseInt(goodNumber.getText()
                                    .toString().trim().equals("") ? (1 + "")
                                    : goodNumber.getText()
                                    .toString().trim()))
                            + "");
                    mAllMoney
                            .setText("总计:￥"
                                    + StringUtil.double2(money
                                    * Integer
                                    .parseInt(goodNumber.getText()
                                            .toString().trim()
                                            .equals("") ? (1 + "")
                                            : goodNumber.getText()
                                            .toString()
                                            .trim())) + "");
                    allmoney = Double.parseDouble(StringUtil.double2(money
                            * (joinNumber + 1)));
                }
                break;
            case R.id.zhifubao:// 支付宝
                isXaplay = true;
                zhifubao_icon.setImageResource(R.drawable.pay_select);
                weixin_icon.setImageResource(R.drawable.pay_default);
                break;
            case R.id.weixin:// 微信
                isXaplay = false;
                zhifubao_icon.setImageResource(R.drawable.pay_default);
                weixin_icon.setImageResource(R.drawable.pay_select);
                break;
        }
    }

    @Override
    protected void onResume() {
        getMyDefaultAddress();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && null != data) {
            // 选择地址信息的返回回调
            if (requestCode == Constants.REVISE_ADDRESS) {
                mLinearlayout.setVisibility(View.VISIBLE);
                mAddAddress.setVisibility(View.GONE);
                getMyDefaultAddress();
            }
            // 选择代金券信息的返回回调
            else if (requestCode == Constants.SELECT_INVITE) {
                entity = (InviteEntity) data.getExtras().getSerializable(
                        "invite");
                mInvite.setText("-￥" + entity.getPrice());
                // 修改底部价格
                if (Double.parseDouble(entity.getPrice()) > allmoney) {
                    // 如果代金券的金额大于要支付的金额，fuck
                    entity = null;
                    // mInvite.setText("点击查看我的代金券");
                } else {
                    mAllMoney.setText("总计:￥"
                            + StringUtil.double2((allmoney - Double
                            .parseDouble(entity.getPrice()))));
                    mAllMoney.setText("总计:￥"
                            + StringUtil.double2((allmoney - Double
                            .parseDouble(entity.getPrice()))) + "");
                    allmoney = Double.parseDouble(StringUtil.double2(allmoney
                            - Double.parseDouble(entity.getPrice())));
                }
            }
        }
        // 关于支付协议部分
        if (requestCode == 10099) {
            if (resultCode == RESULT_OK) {
                // 同意了支付协议
                iv_order_iv.setImageResource(R.drawable.expert_treaty_p);
                iv_order_iv.setTag("true");
            } else {
                // 没同意支付协议
                iv_order_iv.setImageResource(R.drawable.expert_treaty);
                iv_order_iv.setTag("false");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 下订单
     */
    public void createOrder() {
        AsyncHttpClient httpClient = ApiClient.getHttpClient();
        String url = ApiClient.URL + "App/saveOrderInfo";
        RequestParams params = new RequestParams();
        params.put("deal_id", wine.getId());// 商品id
        params.put("amount", meal.getPrice());// 套餐价格
        params.put("cost", "0");// 运费
        params.put("address_id", mAddress.getId());// 收货地址id
        params.put("sid", meal.getId());// 套餐id
        params.put("remark", mFuck.getText().toString() + "");
        if (null == entity) {
            params.put("denomination", "");// 优惠券价格
            params.put("voucher_id", "");// 优惠券id
        } else {
            params.put("denomination", entity.getPrice() + "");// 优惠券价格
            params.put("voucher_id", entity.getId() + "");// 优惠券id
        }
        params.put("userToken",
                UserInfoUtil.getToken(CreateOrderActivity.this));
        params.put("num",

                meal.getOldPrice() + "");// 商品数量
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject object = new JSONObject(new String(bytes));
                    if (object.getBoolean("status")) {
                        String orderId = object.getString("info");
                        getOrderInfo(orderId);
                    } else {
                        Toast.makeText(CreateOrderActivity.this, "" + object.getString("info"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    /***
     * 获取默认地址
     */
    private void getMyDefaultAddress() {
        ApiClient.getDefaultAddress(this, new ApiCallBack() {
            @Override
            public void response(Object object) {
                if (null == object) {
                    mLinearlayout.setVisibility(View.GONE);
                    mAddAddress.setVisibility(View.VISIBLE);
                } else {
                    mAddress = (Address) object;
                    mAddressText.setText("收货地址:"
                            + mAddress.getAddress() + "");
                    mName.setText("收货人:" + mAddress.getName() + "   "
                            + mAddress.getPhone());
                    mLinearlayout.setVisibility(View.VISIBLE);
                    mAddAddress.setVisibility(View.GONE);
                    sr_content.setVisibility(View.VISIBLE);
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

    /***
     * 获取订单信息
     */
    public void getOrderInfo(String id) {
        ApiClient.getOrderDetail(this, id, new ApiCallBack() {
            @Override
            public void response(Object object) {
                oentity = (OrderEntity) object;
                zhifu();
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                Toast.makeText(CreateOrderActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // 去支付
    public void zhifu() {
        if (!isXaplay) {
            // 微信支付
            // 签名验证
            String sign = MD5.Encode(
                    com.sicao.smartwine.pay.wx.Constants.API_KEY + oentity.getGoods().getWineName()
                            + oentity.getOrderNumber()
                            + oentity.getOrderPrice() + oentity.getState()
                            + com.sicao.smartwine.pay.wx.Constants.MCH_ID
                            + UserInfoUtil.getUID(this)
                            + Constants.KEY, "UTF-8");
            checkSign(Integer.parseInt(oentity.getOrderId()), sign, "2");
        } else {
            // 支付宝支付
            // 签名验证
            String sign = MD5.Encode(
                    com.sicao.smartwine.pay.alipay.Constants.RSA_PUBLIC
                            + oentity.getGoods().getWineName()
                            + oentity.getOrderNumber()
                            + oentity.getOrderPrice() + oentity.getState()
                            + com.sicao.smartwine.pay.alipay.Constants.PARTNER
                            + UserInfoUtil.getUID(this)
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
                OrderEntity e = (OrderEntity) object;
                oentity.setNotifyurl(e.getNotifyurl());
                if (e.getState().equals("1")) {
                    //支付宝
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.OPEN_ALIPAY_CLIENT;
                    mHandler.sendMessage(msg);
                } else {
                    //微信
                    oentity.setOrderNumber(e.getOrderNumber());
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.OPEN_WX_CLIENT;
                    mHandler.sendMessage(msg);
                }
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                Toast.makeText(CreateOrderActivity.this, "订单校验异常", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void onBackPressed() {
        finish();
    }

    ;

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
                    Toast.makeText(CreateOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    closeOtherActivity();
                } else {
                    // 支付失败
                    Toast.makeText(CreateOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*
     * 下订单支付流程结束,关闭之前标记的页面并启动订单列表页面
     */
    public void closeOtherActivity() {
        for (Activity activity : AppContext.pay) {
            activity.finish();
        }
//        startActivity(new Intent(this, MyOrderActivity.class));
        finish();
    }
}
