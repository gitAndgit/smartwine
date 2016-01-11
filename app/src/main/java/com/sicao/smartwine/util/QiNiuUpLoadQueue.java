/**
 * Administrator
 * 2015-1-27
 */
package com.sicao.smartwine.util;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

import com.sicao.smartwine.AppContext;
import java.util.ArrayList;

/**
 * 图片文件队列上传 ;可针对单个文件的失败实现重新单独上传; 针对七牛云做的调整
 * 
 * @author li'mingqi
 * 
 */
public class QiNiuUpLoadQueue {
	/** 上下文对象 **/
	Context mContext;
	/** Handler **/
	Handler mHandler;
	/** 队列开始执行 **/
	final int START = 001;
	/** 队列正在执行 **/
	final int LOADING = 002;
	/** 队列执行中任务一项失败 **/
	final int FAIL = 003;
	/** 队列执行中任务一项成功 **/
	final int SUCCESS = 004;
	/** 针对队列中失败的一项任务开始重新执行 **/
	final int SINGLE_SUCCESS = 005;
	/** 针对队列中失败的一项任务正在执行 **/
	final int SINGLE_LOADING = 006;
	/** 针对队列中失败的一项任务执行失败 **/
	final int SINGLE_FAIL = 007;
	/** 队列执行的结果 **/
	ArrayList<String> mHit = new ArrayList<String>();
	/** 用于保存队列要执行任务集合 **/
	ArrayList<Uri> mData = new ArrayList<Uri>();
	/** 队列执行的当前的任务项 **/
	int mCurrentIndex = 0;
	/** 队列执行结果 **/
	QiNiuUploadListener uploadListener;
	/** 是否正在执行队列中的一项 **/
	boolean isUpLoading = false;
	/** 队列执行的UI提示适配器数据 **/
	ArrayList<UploadImageEntity> imgs = new ArrayList<UploadImageEntity>();


	public QiNiuUpLoadQueue(Context context) {
		this.mContext = context;
		try {
			this.mHandler = new Handler(new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					int what = msg.what;
					switch (what) {
					case START:// 队列开始执行
						if (hasNext()) {
							// mDialog.showText("正在上传图片");
							// 队列开始执行
							upload();
						} else {
							// 结束
							if (null != uploadListener) {
								uploadListener.result(mHit);
							}
						}
						break;

					case LOADING:// 队列任务执行过程中,更新文件的上传进度;
						// int progress = msg.arg2;
						// imgs.get(imgs.size() - 1).setProgress(progress);
						break;
					case SUCCESS:
						// 任务执行成功;
						int index = msg.arg1;// 用于确定是哪一张图片 如"门店图片"
						String url = (String) msg.obj;// 上传的结果url
						mHit.set(index, url);// 将执行结果放置返回内容中
						isUpLoading = false;// 单次任务结束,复位标志位;
						// 继续下一张图片的上传
						mCurrentIndex++;
						start();
						break;

					case FAIL:
						// 任务执行失败;
						int indexf = msg.arg1;// 用于标记是哪一张上传失败 ，如"门店图片"
						mHit.set(indexf, "-1");// 设置执行的结果到返回内容中
						// 单次任务结束,复位标志位
						isUpLoading = false;
						// 继续下一张图片的上传
						mCurrentIndex++;
						start();
						break;
					}
					return true;
				}
			});
		} catch (Exception e) {
		}
	}

	/**
	 * 设置队列执行的返回结果
	 * 
	 * @param uploadListener
	 *            the uploadListener to set
	 */
	public void setQiNiuUploadListener(QiNiuUploadListener uploadListener) {
		this.uploadListener = uploadListener;
	}

	/***
	 * 设置队列要执行的任务
	 * 
	 * @param data
	 *            li'mingqi 2015-1-29
	 */
	public void setData(Uri[] data) {
		if (null != data && data.length > 0) {
			for (int i = 0; i < data.length; i++) {
				mData.add(data[i]);
				mHit.add(i, "");
			}
		}
	}

	/**
	 * 任务队列开始执行
	 * 
	 * li'mingqi 2015-1-29
	 */
	public void start() {
		Message msg = new Message();
		msg.what = START;
		mHandler.sendMessage(msg);
	}

	/***
	 * 判断是否还有任务需要执行
	 * 
	 * @return li'mingqi 2015-1-29
	 */

	public boolean hasNext() {
		try {
			if (mCurrentIndex < mData.size()) {
				if (null != mData.get(mCurrentIndex)
						&& !"".equals(mData.get(mCurrentIndex))) {
					return true;

				} else {
					mCurrentIndex++;
					if (mCurrentIndex == mData.size()) {
						mCurrentIndex--;
						return false;
					} else {
						return hasNext();
					}
				}
			} else {
				mCurrentIndex--;
				return false;
			}
		} catch (Exception e) {
		}
		return false;

	}

	/***
	 * 上传文件的方法
	 * 
	 * li'mingqi 2015-1-29
	 */
	public void upload() {
		final Message message = new Message();
		try {
			if (!AppContext.isNetworkAvailable()) {
				message.what = FAIL;
				message.arg1 = mCurrentIndex;
				mHandler.sendMessage(message);
				Toast.makeText(mContext, "网络不可用,请检查您的网络设置", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			QiNiuManager.uploadFile(mData.get(mCurrentIndex).getPath(),
					System.currentTimeMillis() + ".jpg", new ApiCallBack() {

						@Override
						public void response(Object object) {
							message.what = SUCCESS;
							message.arg1 = mCurrentIndex;
							message.obj = (String) object;
							mHandler.sendMessage(message);

						}
					}, new ApiException() {

						@Override
						public void error(String error) {
							message.what = FAIL;
							message.arg1 = mCurrentIndex;
							mHandler.sendMessage(message);

						}
					});

		} catch (Exception e) {
			message.what = FAIL;
			message.arg1 = mCurrentIndex;
			mHandler.sendMessage(message);
		}

	}

	public interface QiNiuUploadListener {
		public void result(ArrayList<String> result);
	}

	/***
	 * 根据下标位置得到要传文件的名称
	 * 
	 * @param index
	 * @return li'mingqi 2015-1-29
	 */
	public String getCurrentImgName(int index) {

		return "正在上传第  " + (index + 1) + " 张图片...";
	}
}
