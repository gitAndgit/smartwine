package com.sicao.smartwine.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sicao.smartwine.R;
import com.sicao.smartwine.pay.wx.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/***
 * 微信支付回调
 * 
 * @author mingqi'li
 * 
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	/** 微信API **/
	private IWXAPI api;
	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);

	}

	@Override
	public void onReq(BaseReq req) {
		Log.e("log", "onPayFinish, errCode = "
				+ "+++++++++++++++++++++++++++++");
	}

	@Override
	public void onResp(BaseResp resp) {
		/* WXSuccess = 0, *//** < 成功 */
		/*
		 * WXErrCodeCommon = -1,
		 *//** < 普通错误类型 */
		/*
		 * WXErrCodeUserCancel = -2,
		 *//** < 用户点击取消并返回 */
		/*
		 * WXErrCodeSentFail = -3,
		 *//** < 发送失败 */
		/*
		 * WXErrCodeAuthDeny = -4,
		 *//** < 授权失败 */
		/*
		 * WXErrCodeUnsupport = -5,
		 *//** < 微信不支持 */
		/*
		 * };
		 */
		Intent intent = new Intent(com.sicao.smartwine.pay.Constants.WX_PAY_RETURN);
		switch (resp.errCode) {
		case 0:
			// mDialog.showText("付款成功，正在生成订单，请稍后...");
			// mResult.setText("支付成功");
			intent.putExtra("wxreturn", true);
			break;
		case 1:
		case -1:
			// mResult.setText("支付失败");
			// mDialog.showText("支付失败");
			intent.putExtra("wxreturn", false);
			break;
		case 2:
		case -2:
			// mDialog.showText("退出微信支付");
			// mResult.setText("支付失败");
			intent.putExtra("wxreturn", false);
			break;
		case 3:
		case -3:
			// mDialog.showText("发送失败");
			// mResult.setText("支付失败");
			intent.putExtra("wxreturn", false);
			break;
		case 4:
		case -4:
			// mDialog.showText("授权失败");
			// mResult.setText("支付失败");
			intent.putExtra("wxreturn", false);
			break;
		case 5:
		case -5:
			// mDialog.showText("微信不支持");
			// mResult.setText("支付失败");
			intent.putExtra("wxreturn", false);
			break;
		}
		this.sendBroadcast(intent);
		finish();
	}
}