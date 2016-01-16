package com.sicao.smartwine.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.R;

/**
 * 广告轮播
 * 
 * @author putaoji
 */
public class CircularBannerView extends RelativeLayout {
	private Context context;
	private String[] imageUrl = null;
	private String[] imageDesc = null;
	public MyAdapter adapter;
	private TextView image_desc;
	private LinearLayout pointGroup;
	protected int lastPosition;
	private OnItemClickListener listener;
	private LinearLayout ll_bottom;
	private int circle_res = R.drawable.point_bg;
	private ViewPager vp;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				vp.setCurrentItem(vp.getCurrentItem() + 1);
				handler.sendEmptyMessageDelayed(0, 8000);
				break;
			default:
				break;
			}
		};
	};

	public CircularBannerView(Context context) {
		super(context);
		this.context = context;
		initView(context);
	}

	public CircularBannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(context);
	}

	public CircularBannerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView(context);
	}

	public void setImageUrl(String[] imageUrl) {
		if (imageUrl != null && imageUrl.length > 0) {
			vp.removeAllViews();
			pointGroup.removeAllViews();
			vp.invalidate();
			pointGroup.invalidate();
			this.imageUrl = imageUrl;
			setPoints(imageUrl.length);
			adapter.notifyDataSetChanged();
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public void setImageDesc(String[] imageDesc) {
		if (imageDesc != null && imageDesc.length > 0) {
			image_desc.setVisibility(View.VISIBLE);
			this.imageDesc = imageDesc;
			adapter.notifyDataSetChanged();
			image_desc.setText(imageDesc[0]);
		}
	}

	public void setDescTextColor(int color) {
		if (image_desc.getVisibility() == View.VISIBLE) {
			image_desc.setTextColor(color);
		}
	}

	public void setBottomBkColor(int color) {
		ll_bottom.setBackgroundColor(color);
	}

	public void setCircleSelector(int res) {
		circle_res = res;
	}

	public void setImageResouce(String imageurl[], String imagedesc[],
			OnItemClickListener listener) {
		vp.removeAllViews();
		pointGroup.removeAllViews();
		vp.invalidate();
		pointGroup.invalidate();
		setImageUrl(imageurl);
		setImageDesc(imagedesc);
		setOnItemClickListener(listener);
		handler.sendEmptyMessage(0);
	}

	@SuppressLint("InflateParams")
	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.circularbanner, null);
		vp = (ViewPager) view.findViewById(R.id.viewpager);
		image_desc = (TextView) view.findViewById(R.id.image_desc);
		pointGroup = (LinearLayout) view.findViewById(R.id.point_group);
		ll_bottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
		imageUrl = new String[] {};
		imageDesc = new String[] {};
		adapter = new MyAdapter();
		vp.setAdapter(adapter);
		vp.setCurrentItem(Integer.MAX_VALUE / 2);
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (imageUrl.length > 0) {
					position = position % imageUrl.length;
					if (imageDesc.length > 0) {
						image_desc.setText(imageDesc[position]);
					}
					pointGroup.getChildAt(position).setEnabled(true);
					pointGroup.getChildAt(lastPosition).setEnabled(false);
					lastPosition = position;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		addView(view);
	}

	public class MyAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final SimpleDraweeView imageView = new SimpleDraweeView(context);
			if (imageUrl.length > 0) {
				imageView.setImageURI(Uri.parse(imageUrl[position
						% imageUrl.length]));
			}
			container.addView(imageView);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null)
						listener.onclick(position % imageUrl.length);
				}
			});
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			object = null;
		}
	}

	private void setPoints(int count) {
		for (int i = 0; i < count; i++) {
			ImageView point = new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = 5;
			params.leftMargin = 5;
			point.setLayoutParams(params);
			point.setBackgroundResource(circle_res);
			if (i == 0) {
				point.setEnabled(true);
			} else {
				point.setEnabled(false);
			}
			pointGroup.addView(point);
		}
	}

	public interface OnItemClickListener {
		void onclick(int position);
	}
}
