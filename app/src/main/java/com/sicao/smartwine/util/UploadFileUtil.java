package com.sicao.smartwine.util;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.sicao.smartwine.api.ApiClient;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/***
 * 
 * @author mingqi'li
 * 
 */
public class UploadFileUtil {
	
	public static final int UPLOAD_FILE_SUCCESS=1;
	public static final int UPLOAD_FILE_FAIL=-1;
	
	public static void upload(final Handler handler,final Uri uri) {
		final Message msg=handler.obtainMessage();
	   new Thread(){
		   public void run() {
			   String end = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				String newName = "image.jpg";
				try {
					URL url = new URL(ApiClient.URL+"App/uploadPicture");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					/* 允许Input、Output，不使用Cache */
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					/* 设置传送的method=POST */
					conn.setRequestMethod("POST");
					/* setRequestProperty */
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("Charset", "UTF-8");
					conn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					/* 设置DataOutputStream */
					DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
					ds.writeBytes(twoHyphens + boundary + end);
					ds.writeBytes("Content-Disposition: form-data; "
							+ "name=\"file1\";filename=\"" + newName + "\"" + end);
					ds.writeBytes(end);
					/* 取得文件的FileInputStream */
					File f = new File(uri.getPath());
					FileInputStream fStream = new FileInputStream(f);
					/* 设置每次写入1024bytes */
					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int length = -1;
					/* 从文件读取数据至缓冲区 */
					while ((length = fStream.read(buffer)) != -1) {
						/* 将资料写入DataOutputStream中 */
						ds.write(buffer, 0, length);
					}
					ds.writeBytes(end);
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
					/* close streams */
					fStream.close();
					ds.flush();
					/* 关闭DataOutputStream */
					ds.close();
					/* 取得Response内容 */
					int res = conn.getResponseCode();
					if (res == 200) {// 上传成功
						System.setProperty("http.keepAlive", "false");
						InputStream is = conn.getInputStream();
						int ch;
						StringBuffer b = new StringBuffer();
						while ((ch = is.read()) != -1) {
							b.append((char) ch);
						}
						is.close();
						/***
						 * 解析上传的结果并提示主线程
						 */
						Log.i("huahua",new String(b.toString()
								.getBytes("ISO-8859-1"), "UTF-8"));
						msg.what=UPLOAD_FILE_SUCCESS;
						msg.obj=new String(b.toString()
								.getBytes("ISO-8859-1"), "UTF-8");
						handler.sendMessage(msg);
					} else if (res == 500) {// 请求超时,中断
						msg.what=UPLOAD_FILE_FAIL;
						handler.sendMessage(msg);
					} else {// 其他
						msg.what=UPLOAD_FILE_FAIL;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					msg.what=UPLOAD_FILE_FAIL;
					handler.sendMessage(msg);
				}
 
		   };
	   }.start();
		
	}
	
	public static void upload(final Handler handler,final Uri uri,final String token,final String type) {
		final Message msg=handler.obtainMessage();
	   new Thread(){
		   public void run() {
			   String end = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				String newName = "image.jpg";
				try {
					URL url = new URL(ApiClient.URL+"App/modifyAvatar?userToken="+token+"&avatarType="+type);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					/* 允许Input、Output，不使用Cache */
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					/* 设置传送的method=POST */
					conn.setRequestMethod("POST");
					/* setRequestProperty */
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("Charset", "UTF-8");
					conn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					/* 设置DataOutputStream */
					DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
					ds.writeBytes(twoHyphens + boundary + end);
					ds.writeBytes("Content-Disposition: form-data; "
							+ "name=\"file1\";filename=\"" + newName + "\"" +";type=\"image/jpg\""+ end);
					ds.writeBytes(end);
					/* 取得文件的FileInputStream */
					File f = new File(uri.getPath());
					FileInputStream fStream = new FileInputStream(f);
					/* 设置每次写入1024bytes */
					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int length = -1;
					/* 从文件读取数据至缓冲区 */
					while ((length = fStream.read(buffer)) != -1) {
						/* 将资料写入DataOutputStream中 */
						ds.write(buffer, 0, length);
					}
					ds.writeBytes(end);
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
					/* close streams */
					fStream.close();
					ds.flush();
					/* 关闭DataOutputStream */
					ds.close();
					/* 取得Response内容 */
					int res = conn.getResponseCode();
					if (res == 200) {// 上传成功
						System.setProperty("http.keepAlive", "false");
						InputStream is = conn.getInputStream();
						int ch;
						StringBuffer b = new StringBuffer();
						while ((ch = is.read()) != -1) {
							b.append((char) ch);
						}
						is.close();
						/***
						 * 解析上传的结果并提示主线程
						 */
						Log.i("huahua",new String(b.toString()
								.getBytes("ISO-8859-1"), "UTF-8"));
						msg.what=UPLOAD_FILE_SUCCESS;
						msg.obj=new String(b.toString()
								.getBytes("ISO-8859-1"), "UTF-8");
						handler.sendMessage(msg);
					} else if (res == 500) {// 请求超时,中断
						msg.what=UPLOAD_FILE_FAIL;
						handler.sendMessage(msg);
					} else {// 其他
						msg.what=UPLOAD_FILE_FAIL;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					msg.what=UPLOAD_FILE_FAIL;
					handler.sendMessage(msg);
				}
 
		   };
	   }.start();
		
	}
}
