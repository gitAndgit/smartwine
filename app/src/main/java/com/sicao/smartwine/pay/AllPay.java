package com.sicao.smartwine.pay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.pay.alipay.AlipayManager;
import com.sicao.smartwine.pay.wx.WXPayManager;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.MD5;
import com.sicao.smartwine.util.UserInfoUtil;

/**
 * 支付选择
 * @author yongzhong'han
 *
 */
public class AllPay {

	/** 订单 **/
	private OrderEntity entity;
	/** 支付宝管理 **/
	private AlipayManager mAlipayManager;
	/** 微信支付管理 **/
	private WXPayManager mWXpayManager;
	// 当前选择的是哪一种支付
	private int payType = 2;
	// 第三方支付Handler
	private Handler mHandler;
	// 订单id
	private String orderId = "";
	private int type;
	// 接受微信支付的结果广播
	private WxReturnReceiver receiver;
	private boolean isclickpay = false;
	private Context mContext;

	// 构造方法
	/**
	 * @param context 上下文 
	 * @param v v按钮控件
	 * @param npayType 支付类型  1代表微信 2代表支付宝   默认支付宝支付
	 * @param oentity 支付订单详情 
	 */
	public AllPay(Context context, View v, int npayType, OrderEntity oentity) {
		this.mContext=context;
		this.entity = oentity;
		this.payType = npayType;
		getOrderInfo(orderId);
		init(context, v);
		// 用于区分调起哪个支付
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int what = msg.what;
				switch (what) {
				case Constants.OPEN_ALIPAY_CLIENT:// 支付宝
					mAlipayManager.goPay(entity, "PayActivity");
					isclickpay = false;
					break;
				case Constants.OPEN_WX_CLIENT:// 微信
					mWXpayManager.goPay(entity);
					isclickpay = false;
					break;
				}
			}
		};
		// 注册广播,监控微信支付的结果
		receiver = new WxReturnReceiver();
		IntentFilter filter = new IntentFilter(Constants.WX_PAY_RETURN);
		mContext.registerReceiver(receiver, filter);
	}

	//
	public void init(Context context, View v) {
		this.mContext = context;
		// 支付宝
		mAlipayManager = new AlipayManager(mContext);
		// 微信支付
		mWXpayManager = new WXPayManager(mContext);
		// 支付宝支付结果监听
		mAlipayManager.setAlipayListener(new AlipayManager.AlipayInterface() {
			@Override
			public void alipaySuccess(OrderEntity order) {
				Toast.makeText(mContext, "支付成功...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void alipayFail(OrderEntity order) {
				Toast.makeText(mContext, "支付失败...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void alipaySupport(boolean support) {
				Toast.makeText(mContext, "客户端不支持支付宝插件...", Toast.LENGTH_SHORT).show();
			}
		});
		gozhifu(v);
	}

	//
	public void gozhifu(View v) {
		if (!isclickpay) {
			isclickpay = true;
			if (payType == 1) {
				// 微信支付
				// 签名验证
				String sign = MD5.Encode(
						com.sicao.smartwine.pay.wx.Constants.API_KEY
								+ entity.getGoods().getWineName()
								+ entity.getOrderNumber()
								+ entity.getOrderPrice()
								+ entity.getState()
								+ com.sicao.smartwine.pay.wx.Constants.MCH_ID
								+ UserInfoUtil.getUID(mContext)
								+ Constants.KEY, "UTF-8");
				checkSign(entity.getOrderId(), sign,
						"2");
			} else if (payType == 2) {
				// 支付宝支付
				// 签名验证
				String sign = MD5.Encode(
						com.sicao.smartwine.pay.alipay.Constants.RSA_PUBLIC
								+ entity.getGoods().getWineName()
								+ entity.getOrderNumber()
								+ entity.getOrderPrice()
								+ entity.getState()
								+ com.sicao.smartwine.pay.alipay.Constants.PARTNER
								+ UserInfoUtil.getUID(mContext)
								+ Constants.KEY, "UTF-8");
				checkSign(entity.getOrderId(), sign,
						"1");
			}
		} else {
			Toast.makeText(mContext, "请勿重复点击，以免造成多次支付", Toast.LENGTH_SHORT).show();
		}
	}

	// 下订单支付流程结束,关闭之前标记的页面并启动订单列表页面

	public void closeOtherActivity() {
		for (Activity activity : AppContext.pay) {
			mContext.unregisterReceiver(receiver);
			activity.finish();
		}
	}

	/**
	 * 接受微信支付的结果回调
	 * 
	 * @author puser
	 * 
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
					Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
					closeOtherActivity();
				} else {
					// 支付失败
					Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/***
	 * 调用第三方支付之前的订单信息校验功能
	 * 
	 */
	public void checkSign(final String orderId, final String sign,
			final String type) {
		ApiClient.checkSign(mContext, orderId, sign, type, new ApiCallBack() {
			@Override
			public void response(Object object) {
				OrderEntity e = (OrderEntity) object;
				entity.setNotifyurl(e.getNotifyurl());
				if ("1".equals(type)) {
					//支付宝
					Message msg = mHandler.obtainMessage();
					msg.what = Constants.OPEN_ALIPAY_CLIENT;
					mHandler.sendMessage(msg);
				} else if ("2".equals(type)) {
					//微信
					entity.setOrderNumber(e.getOrderNumber());
					Message msg = mHandler.obtainMessage();
					msg.what = Constants.OPEN_WX_CLIENT;
					mHandler.sendMessage(msg);
				}
			}
		}, new ApiException() {
			@Override
			public void error(String error) {
				Toast.makeText(mContext, "校验失败-"+error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/***
	 * 获取订单信息
	 * 
	 */
	public void getOrderInfo(String id) {
		ApiClient.getOrderDetail(mContext, id, new ApiCallBack() {
			@Override
			public void response(Object object) {
                   entity=(OrderEntity)object;
			}
		}, new ApiException() {
			@Override
			public void error(String error) {
				Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void onBackPressed() {
		((Activity) mContext).finish();
		mContext.unregisterReceiver(receiver);
	};

}
