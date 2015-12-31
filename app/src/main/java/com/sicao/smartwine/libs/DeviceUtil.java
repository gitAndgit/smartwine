package com.sicao.smartwine.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.sicao.smartwine.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 15/3/6.
 */
public final class DeviceUtil {

	public static final String PROTOCOL_IOT = "iot://";
	public static final String PROTOCOL_HTTP = "http://";
	public static final String PROTOCOL_SSID = "ssid://";

	private static final List<Bundle> mModelInfos = new ArrayList<Bundle>();
	private static final List<Bundle> mTypeInfos = new ArrayList<Bundle>();

	public static String getUDID(String ssid) {
		if (isNewDevice(ssid)) {
			return stringToMD5(ssid);
		}
		return null;
	}

	public static String getMacAddress(String ssid) {
		if (isNewDevice(ssid)) {
			String[] result = ssid.substring(PROTOCOL_IOT.length()).split("/");
			if (result.length > 2) {
				String mac = result[2];
				String mac1 = mac.substring(0, 2);
				String mac2 = mac.substring(2, 4);
				String mac3 = mac.substring(4, 6);
				String mac4 = mac.substring(6, 8);
				String mac5 = mac.substring(8, 10);
				String mac6 = mac.substring(10, 12);
				String macAddress = mac1 + ":" + mac2 + ":" + mac3 + ":" + mac4
						+ ":" + mac5 + ":" + mac6;
				return macAddress;
			}
		}
		return null;
	}

	public static String getModel(String ssid) {
		if (isNewDevice(ssid)) {
			String[] result = ssid.substring(PROTOCOL_IOT.length()).split("/");
			if (result.length > 2) {
				return result[1];
			}
		}
		return null;
	}

	public static String getMan(String ssid) {
		if (isNewDevice(ssid)) {
			String[] result = ssid.substring(PROTOCOL_IOT.length()).split("/");
			if (result.length > 2) {
				return result[0];
			}
		}
		return null;
	}

	public static String getIoTUri(String man, String model, String mac) {
		String[] m = mac.split(":");
		return PROTOCOL_IOT + man.toLowerCase() + "/" + model.toLowerCase()
				+ "/" + m[0] + m[1] + m[2] + m[3] + m[4] + m[5];
	}

	public static String getSSID(String man, String model, String mac) {
		String[] macs = mac.split(":");
		return PROTOCOL_IOT + man + "/" + model + "/" + macs[0] + macs[1]
				+ macs[2] + macs[3] + macs[4] + macs[5];
	}

	public static String stringToMD5(String str) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}



	public static List<Bundle> getTypes() {
		return mTypeInfos;
	}

	public static List<Bundle> getModelInfos() {
		return mModelInfos;
	}

	public static Bundle getDeviceDescription(String model) {
		if (model == null)
			return null;
		for (Bundle info : mModelInfos) {
			if (model.equalsIgnoreCase(info.getString("model"))) {
				return info;
			}
		}
		return null;
	}

	private static final void beginDocument(XmlPullParser parser,
			String firstElementName) throws XmlPullParserException, IOException {
		int type;
		while ((type = parser.next()) != XmlPullParser.START_TAG
				&& type != XmlPullParser.END_DOCUMENT) {
			;
		}

		if (type != XmlPullParser.START_TAG) {
			throw new XmlPullParserException("No start tag found");
		}

		if (!parser.getName().equals(firstElementName)) {
			throw new XmlPullParserException("Unexpected start tag: found "
					+ parser.getName() + ", expected " + firstElementName);
		}
	}

	public static String getDeviceName(String model) {
		if (model == null) {
			return null;
		}
		for (Bundle info : mModelInfos) {
			if (model.equalsIgnoreCase(info.getString("model"))) {
				return info.getString("name");
			}
		}
		return "智能设备";
	}

	public static String getDeviceType(String model) {
		if (model == null) {
			return null;
		}
		for (Bundle info : mModelInfos) {
			if (model.equalsIgnoreCase(info.getString("model"))) {
				return info.getString("type");
			}
		}
		return null;
	}
	public static boolean isNewDevice(String ssid) {
		if (ssid != null && ssid.startsWith(PROTOCOL_IOT)
				&& ssid.length() > (PROTOCOL_IOT.length() + 12)) {
			String[] result = ssid.substring(PROTOCOL_IOT.length()).split("/");
			Log.d("isNewDevice", "size=" + result.length);
			if (result.length > 2) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNewDevice(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null && wifiInfo.getSSID().length() > 18) {
			String ssid = wifiInfo.getSSID();
			if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
				ssid = ssid.substring(1, ssid.length() - 1);
			}
			return isNewDevice(ssid);
		}
		return false;
	}

	public static boolean isSupportDevice(String model) {
		if (model == null || mModelInfos == null) {
			return false;
		}
		for (Bundle info : mModelInfos) {
			if (model.equalsIgnoreCase(info.getString("model"))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSupportSmartconfig(String model) {
		if (model == null || mModelInfos == null) {
			return false;
		}
		for (Bundle info : mModelInfos) {
			if (model.equalsIgnoreCase(info.getString("model"))) {
				return info.getBoolean("smartconfig");
			}
		}
		return false;
	}

	public static boolean loadConfig(Context context, int resId) {
		mModelInfos.clear();
		XmlResourceParser parser = context.getResources().getXml(resId);
		AttributeSet attrs = Xml.asAttributeSet(parser);
		try {
			beginDocument(parser, "devices");
			final int depth = parser.getDepth();
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser
					.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

				if (type != XmlPullParser.START_TAG) {
					continue;
				}
				final String tagName = parser.getName();
				TypedArray a = context.obtainStyledAttributes(attrs,
						R.styleable.Device);

				Bundle info = new Bundle();
				info.putString("model", a.getString(R.styleable.Device_model));
				info.putString("name", a.getString(R.styleable.Device_name));
				info.putString("man",
						a.getString(R.styleable.Device_manufacturer));
				info.putString("type", a.getString(R.styleable.Device_type));
				info.putInt("onlineIcon",
						a.getResourceId(R.styleable.Device_onlineIcon, 0));
				info.putInt("offlineIcon",
						a.getResourceId(R.styleable.Device_offlineIcon, 0));

				info.putInt("timerActions",
						a.getResourceId(R.styleable.Device_timerActions, 0));
				info.putInt("timerLabels",
						a.getResourceId(R.styleable.Device_timerLabels, 0));

				info.putString("uri", a.getString(R.styleable.Device_uri));
				info.putString("entity", a.getString(R.styleable.Device_entity));
				info.putString("service",
						a.getString(R.styleable.Device_service));
				info.putString("provider",
						a.getString(R.styleable.Device_provider));
				info.putString("controlActivity",
						a.getString(R.styleable.Device_controlActivity));
				info.putBoolean("p2p",
						a.getBoolean(R.styleable.Device_p2p, false));

				info.putBoolean("smartconfig",
						a.getBoolean(R.styleable.Device_smartconfig, false));
				info.putInt("smartconfigTip",
						a.getResourceId(R.styleable.Device_smartconfigTip, 0));
				info.putString("smartconfigVersion",
						a.getString(R.styleable.Device_smartconfigVersion));

				info.putBoolean("directconfig",
						a.getBoolean(R.styleable.Device_directconfig, false));
				info.putInt("directconfigTip",
						a.getResourceId(R.styleable.Device_directconfigTip, 0));

				Log.e("Home", info.toString());
				a.recycle();
				mModelInfos.add(info);
			}
			return true;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean loadConfigType(Context context, int resId) {
		mTypeInfos.clear();
		XmlResourceParser parser = context.getResources().getXml(resId);
		AttributeSet attrs = Xml.asAttributeSet(parser);
		try {
			beginDocument(parser, "types");
			final int depth = parser.getDepth();
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser
					.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

				if (type != XmlPullParser.START_TAG) {
					continue;
				}
				final String tagName = parser.getName();
				TypedArray a = context.obtainStyledAttributes(attrs,
						R.styleable.Device);
				Bundle typeInfo = new Bundle();
				typeInfo.putString("type", a.getString(R.styleable.Device_type));
				typeInfo.putString("name", a.getString(R.styleable.Device_name));
				typeInfo.putInt("typeIcon",
						a.getResourceId(R.styleable.Device_typeIcon, 0));
				a.recycle();
				mTypeInfos.add(typeInfo);
			}
			return true;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
