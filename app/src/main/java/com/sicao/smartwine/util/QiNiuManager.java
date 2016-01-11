package com.sicao.smartwine.util;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.Zone;
import com.qiniu.android.utils.UrlSafeBase64;

import org.json.JSONObject;

import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/***
 * 七牛云存储文件上传管理
 * 
 * @author mingqi'li
 * 
 */
public class QiNiuManager {

	private static String AccessKey = "LK2vJ9I9Pw_64CVjGHkw767C4Wp2QD7C28U3pZYx";
	private static String SecretKey = "fqMygyyfPOLAW1LJ4vVD27LScVMAXR3mcLLKLNL5";

	public static void uploadFile(String filePath,
			String saveName, final ApiCallBack callback,
			final ApiException exception) {
		try {
			saveName = "Uploads/Picture/"
					+ StringUtil.dataFormatToyyyyMMdds(System
							.currentTimeMillis()) + "/" + getUUID() + "-"
					+ saveName;
			Configuration config = new Configuration.Builder()
					.chunkSize(256 * 1024) // 分片上传时，每片的大小。 默认 256K
					.putThreshhold(512 * 1024) // 启用分片上传阀值。默认 512K
					.connectTimeout(10) // 链接超时。默认 10秒
					.responseTimeout(60) // 服务器响应超时。默认 60秒
					.zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认
										// Zone.zone0
					.build();
			// 1 构造上传策略
			JSONObject _json = new JSONObject();
			long _dataline = System.currentTimeMillis() / 1000 + 3600;
			_json.put("deadline", _dataline);// 有效时间为一个小时
			_json.put("scope", "img1");

			String _encodedPutPolicy = UrlSafeBase64.encodeToString(_json
					.toString().getBytes());
			byte[] _sign = HmacSHA1Encrypt(_encodedPutPolicy, SecretKey);
			String _encodedSign = UrlSafeBase64.encodeToString(_sign);
			String _uploadToken = AccessKey + ':' + _encodedSign + ':'
					+ _encodedPutPolicy;
			// 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
			UploadManager uploadManager = new UploadManager(config);
			uploadManager.put(filePath, saveName, _uploadToken,
					new UpCompletionHandler() {
						@Override
						public void complete(String key, ResponseInfo info,
								JSONObject res) {
							if (info.isOK()) {
								if (null != callback) {
									callback.response(key);
								}
							} else {
								if (null != callback) {
									callback.response("-1");
								}
							}
						}
					}, null);
		} catch (Exception e) {
			if (null != exception) {
				exception.error("-1");
			}
		}
	}

	/***
	 * 生成数字签名认证
	 */
	private static final String MAC_NAME = "HmacSHA1";
	private static final String ENCODING = "UTF-8";

	/**
	 * 
	 * 这个签名方法找了半天 一个个对出来的、、、、程序猿辛苦啊、、、 使用 HMAC-SHA1 签名方法对对encryptText进行签名
	 * 
	 * @param encryptText
	 *            被签名的字符串
	 * @param encryptKey
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey)
			throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		// 生成一个指定 Mac 算法 的 Mac 对象
		Mac mac = Mac.getInstance(MAC_NAME);
		// 用给定密钥初始化 Mac 对象
		mac.init(secretKey);
		byte[] text = encryptText.getBytes(ENCODING);
		// 完成 Mac 操作
		return mac.doFinal(text);
	}

	/***
	 * 随机UUID
	 * 
	 * @return
	 */
	private static String getUUID() {
		String uuidStr = UUID.randomUUID().toString();
		uuidStr = uuidStr.substring(0, 8) + uuidStr.substring(9, 13)
				+ uuidStr.substring(14, 18) + uuidStr.substring(19, 23)
				+ uuidStr.substring(24);
		return uuidStr;
	}
}
