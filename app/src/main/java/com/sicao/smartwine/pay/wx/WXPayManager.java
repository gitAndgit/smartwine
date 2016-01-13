package com.sicao.smartwine.pay.wx;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.sicao.smartwine.pay.OrderEntity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/***
 * 微信支付管理
 * 
 * @author mingqi'li
 * 
 */
public class WXPayManager {

	// 上下文对象
	private Context mContext;
	// 请求调用微信支付
	PayReq req;
	// 微信API
	IWXAPI msgApi = null;
	// 生成订单数据使用
	Map<String, String> resultunifiedorder;
	StringBuffer sb;
	//
	private Handler mHandler;
	// 订单信息
	private OrderEntity mOrder;

	@SuppressLint("HandlerLeak")
	public WXPayManager(Context context) {
		this.mContext = context;
		msgApi = WXAPIFactory.createWXAPI(mContext, null);
		// 微信部分
		req = new PayReq();
		sb = new StringBuffer();
		msgApi.registerApp(Constants.APP_ID);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 10086:
					// 调动微信支付
					genPayReq();
					msgApi.sendReq(req);
					break;
				default:
					break;
				}

			}
		};
	}

	/**
	 * 发起请求，获取签名及订单信息
	 */
	public void goPay(OrderEntity order) {
		this.mOrder = order;
		msgApi.registerApp(Constants.APP_ID);
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}

	private void genPayReq() {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = this.mOrder.getOrderNumber();
		req.packageValue = "prepay_id=" + this.mOrder.getOrderNumber();
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
	}

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

	private class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(mContext, null, "正在启动微信...");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			resultunifiedorder = result;
			Message msg = mHandler.obtainMessage();
			msg.what = 10086;
			mHandler.sendMessage(msg);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Message msg = mHandler.obtainMessage();
			msg.what = 10087;
			mHandler.sendMessage(msg);
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs(mOrder);
			byte[] buf = Util.httpPost(url, entity);
			String content = new String(buf);
			Map<String, String> xml = decodeXml(content);
			return xml;
		}
	}

	/***
	 * 
	 * @param content
	 * @return
	 */
	public Map<String, String> decodeXml(String content) {
		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}
			return xml;
		} catch (Exception e) {
		}
		return null;

	}

	private String genProductArgs(OrderEntity order) {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams
					.add(new BasicNameValuePair("appid", Constants.APP_ID));
			// 描述
			packageParams.add(new BasicNameValuePair("body", order.getGoods().getWineName()));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			// 支付结果通知
			packageParams.add(new BasicNameValuePair("notify_url", order
					.getNotifyurl()));
			// 商户订单号
			packageParams.add(new BasicNameValuePair("out_trade_no", order
					.getOrderNumber()));
			// 订单生成机器IP
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", order
					.getOrderPrice()));// 总金额
			packageParams.add(new BasicNameValuePair("trade_type",
					"深圳市葡萄集科技有限公司"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);
			Log.i("huahua", "字符串----\n" + xmlstring);
			return xmlstring;

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成签名
	 */

	@SuppressLint("DefaultLocale")
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");
			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");
		Log.e("orion", sb.toString());
		return sb.toString();
	}

	// private String genOutTradNo() {
	// Random random = new Random();
	// return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
	// .getBytes());
	// }
	public AlipayonClick mclick;

	public interface AlipayonClick {
		public void setOnClick();
	}
	public void setClick(AlipayonClick click) {
		this.mclick = click;
	}
}
