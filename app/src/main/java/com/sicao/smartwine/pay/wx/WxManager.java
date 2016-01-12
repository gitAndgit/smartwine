package com.sicao.smartwine.pay.wx;

import android.content.Context;
import android.util.Log;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/***
 * 微信支付
 * @author mingqi'li
 * 2015-3-30
 */
public class WxManager {
	IWXAPI msgApi =null;
	
	private Context mContext;
	//生成签名信息
	private PayReq req;
	//订单信息字段
	private Map<String, String> resultunifiedorder;
	
	StringBuffer sb;
	public WxManager(Context context) {
      this.mContext=context;
      this.msgApi= WXAPIFactory.createWXAPI(this.mContext, null);
	}
	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}
	
	@SuppressWarnings("deprecation")
	private void genPayReq() {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genAppSign(signParams);
		sb.append("sign\n" + req.sign + "\n\n");
		Log.e("orion", signParams.toString());

	}
	@SuppressWarnings("deprecation")
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		return appSign;
	}
	
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
}
