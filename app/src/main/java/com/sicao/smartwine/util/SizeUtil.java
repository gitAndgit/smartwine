package com.sicao.smartwine.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/***
 * <a>屏幕适配工具</br> <a>1,设置textView字体的大小
 * {@link #setTextSize(TextView, DisplayMetrics)} </br> <a>2,设置Button字体的大小
 * {@link #setTextSize(Button, DisplayMetrics)}</br> <a>3,设置ImageView图片平铺大小
 * {@link #setImageViewSize(ImageView, DisplayMetrics)}</br>
 * <a>4,设置LinearLayout布局的高度
 * {@link #setLinearLayoutHeight(LinearLayout, DisplayMetrics)}</br>
 * <a>5,设置RelativeLayout布局的高度 (保证其父控件为RelativeLayout,否则抛出类型转换异常)
 * {@link #setRelativeLayoutHeight(RelativeLayout, DisplayMetrics)}</br>
 * <a>6,设置RelativeLayout布局的高度 (保证其父控件为LinearLayout,否则抛出类型转换异常)
 * {@link #setRLayoutHeight(RelativeLayout, DisplayMetrics)}</br>
 * <a>7,设置顶部TextView字体的大小 {@link #setTopTextSize(TextView, DisplayMetrics)}</br>
 * 
 * @author li'mingqi
 */
public class SizeUtil {

	private static int normalFontSize(int screenWidth, int screenHeight) {

		if (screenWidth <= 240) { // 240X320 屏幕

			return 15;

		} else if (screenWidth <= 320) { // 320X480 屏幕

			return 15;

		} else if (screenWidth <= 480) { // 480X800

			return 17;

		} else if (screenWidth <= 540) { // 540X960 屏幕

			return 20;

		} else if (screenWidth <= 800) { // 800X1280 屏幕

			return 22;

		} else { // 大于 800X1280

			return 24;

		}
	}

	/***
	 * set textView text font
	 * 
	 * @param v
	 *            TextView控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setTextSize(TextView v, DisplayMetrics metrics) {
		// 获取px
		int size = normalFontSize(metrics.widthPixels, metrics.heightPixels);
		// 转换sp
		int sp = px2sp(metrics, size);
		v.setTextSize(sp);
	}

	/***
	 * set button text font
	 * 
	 * @param v
	 *            Button控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setTextSize(Button v, DisplayMetrics metrics) {
		// 获取px
		int size = normalFontSize(metrics.widthPixels, metrics.heightPixels);
		// 转换sp
		int sp = px2sp(metrics, size);
		v.setTextSize(sp);
	}

	/***
	 * set topView text font
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	private static int normalTopViewFontSize(int screenWidth, int screenHeight) {

		if (screenWidth <= 240) { // 240X320 屏幕

			return 17;

		} else if (screenWidth <= 320) { // 320X480 屏幕

			return 20;

		} else if (screenWidth <= 480) { // 480X800

			return 22;

		} else if (screenWidth <= 540) { // 540X960 屏幕

			return 24;

		} else if (screenWidth <= 800) { // 800X1280 屏幕

			return 28;

		} else { // 大于 800X1280

			return 36;

		}
	}

	/***
	 * set topTextView font
	 * 
	 * @param v
	 *            TextView控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setTopTextSize(TextView v, DisplayMetrics metrics) {
		int size = normalTopViewFontSize(metrics.widthPixels,
				metrics.heightPixels);
		int sp = px2sp(metrics, size);
		v.setTextSize(sp);
	}

	/***
	 * set layout height
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	private static int setLayoutheightSize(int screenWidth, int screenHeight) {

		if (screenWidth <= 240) { // 240X320 屏幕

			return 40;

		} else if (screenWidth <= 320) { // 320X480 屏幕

			return 50;

		} else if (screenWidth <= 480) { // 480X800

			return 75;

		} else if (screenWidth <= 540) { // 540X960 屏幕

			return 80;

		} else if (screenWidth <= 800) { // 800X1280 屏幕

			return 100;

		} else { // 大于 800X1280

			return 150;

		}
	}

	/**
	 * set RelativeLayout height 父控件为RelativeLayout
	 * 
	 * @param v
	 *            RelativeLayout控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setRelativeLayoutHeight(RelativeLayout v,
			DisplayMetrics metrics) {
		LayoutParams params = (LayoutParams) v.getLayoutParams();
		// 转换高度px---dp
		int height = setLayoutheightSize(metrics.widthPixels,
				metrics.heightPixels);
		params.height = px2dip(metrics, height);
		params.width = metrics.widthPixels;
		v.invalidate();
	}

	/**
	 * set RelativeLayout height 父控件为LinearLayout
	 * 
	 * @param v
	 *            RelativeLayout控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setRLayoutHeight(RelativeLayout v, DisplayMetrics metrics) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v
				.getLayoutParams();
		int height = setLayoutheightSize(metrics.widthPixels,
				metrics.heightPixels);
		params.height = px2dip(metrics, height);
		v.invalidate();
	}

	/**
	 * set LinearLayout height 父控件为LinearLayout
	 * 
	 * @param v
	 *            LinearLayout控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setLinearLayoutHeight(LinearLayout v,
			DisplayMetrics metrics) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v
				.getLayoutParams();
		int height = setLayoutheightSize(metrics.widthPixels,
				metrics.heightPixels);
		params.height = px2dip(metrics, height);
		v.invalidate();
	}

	private static int setImageSize(int screenWidth, int screenHeight) {

		if (screenWidth <= 240) { // 240X320 屏幕

			return 45;

		} else if (screenWidth <= 320) { // 320X480 屏幕

			return 48;

		} else if (screenWidth <= 480) { // 480X800

			return 85;

		} else if (screenWidth <= 540) { // 540X960 屏幕

			return 160;

		} else if (screenWidth <= 800) { // 800X1280 屏幕

			return 200;

		} else { // 大于 800X1280

			return 300;

		}
	}

	/***
	 * set imageView size
	 * 
	 * @param v
	 *            ImageView控件
	 * @param display
	 *            屏幕参数
	 */
	public static void setImageViewSize(ImageView v, DisplayMetrics metrics) {
		ViewGroup.LayoutParams params = v.getLayoutParams();
		v.setScaleType(ScaleType.FIT_XY);
		params.width = setImageSize(metrics.widthPixels, metrics.heightPixels);
		params.height = setImageSize(metrics.widthPixels, metrics.heightPixels);
		v.setLayoutParams(params);

	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(DisplayMetrics metrics, float pxValue) {
		final float scale = metrics.density;
		return (int) (pxValue / scale + 0.5f);
	}
	  /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 
	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(DisplayMetrics metrics, float dipValue) {
		final float scale = metrics.density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(DisplayMetrics metrics, float pxValue) {
		final float fontScale = metrics.scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(DisplayMetrics metrics, float spValue) {
		final float fontScale = metrics.scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	@SuppressWarnings("deprecation")
	public static void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
				params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 动态设置ListView的高度(Item布局必须为LinearLayout)
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
