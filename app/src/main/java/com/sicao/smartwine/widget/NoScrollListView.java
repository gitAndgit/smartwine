package com.sicao.smartwine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 可以嵌套在ScrollView中得ListView
 * 
 * @author putaoji
 */
public class NoScrollListView extends ListView {

	public NoScrollListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NoScrollListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 3,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

//	/**
//	 * 屏蔽触摸事件
//	 */
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// 下面这句话是关键
//		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//			return true;
//		}
//		return super.dispatchTouchEvent(ev);// 也有所不同哦
//	}

}
