package com.sicao.smartwine.util;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/**
	 *       * 验证邮箱地址是否正确
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		flag = matcher.matches();
		return flag;
	}

	/*
	 * 将手机号的中间4位变为****
	 */
	public static String patternPhoneNum(String phonenumber) {
		if (phonenumber.length() == 11) {
			String first = phonenumber.substring(0, 3);
			String last = phonenumber.substring(7, phonenumber.length());
			return first + "****" + last;
		} else {
			return phonenumber;
		}
	}

	/****
	 * 将字符串内容复制到剪切板
	 * 
	 * @param c
	 *            上下文对�?
	 * @param copyContent
	 *            复制的内�?
	 */
	@SuppressWarnings("deprecation")
	public static void copyToClipBoard(Context c, CharSequence copyContent) {
		android.text.ClipboardManager manager = (ClipboardManager) c
				.getSystemService(Context.CLIPBOARD_SERVICE);
		manager.setText(copyContent);
	}

	/***
	 * 获取缓存图片的文件名
	 * 
	 * @param imageUri
	 * @return
	 */
	public static String getBitmapFileName(String imageUri) {
		byte[] md5 = getMD5(imageUri.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(36);
	}

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
		}
		return hash;
	}

	/***
	 * 将long类型的时间格式化成"2015-1-4"字符串类型
	 * 
	 * @param dataSource
	 *            long类型的时间数据
	 * @return 格式化后的时间字符串
	 * 
	 *         li'mingqi 2015-1-4
	 */
	@SuppressLint("SimpleDateFormat")
	public static String dataFormatToyyyyMMdd(long dataSource) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date(dataSource);
		return format.format(date);
	}

	@SuppressLint("SimpleDateFormat")
	public static String dataFormatToyyyyMMdds(long dataSource) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(dataSource);
		return format.format(date);
	}

	/**
	 * 保留两位小数
	 * 
	 * @param money
	 * @return
	 */
	public static String double2(double money) {
		DecimalFormat df = new DecimalFormat("###.00");
		return df.format(money);
	}
}
