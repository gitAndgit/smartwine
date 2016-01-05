package com.sicao.smartwine.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.ClassTypeEntity;
/**
 * 查找酒款筛选适配器
 * 
 * @author yongzhong'han
 * 
 */
public class WineFilterAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ClassTypeEntity[] mlist;// 商品筛选类

	public WineFilterAdapter(Context context, ClassTypeEntity[] list) {
		this.mContext = context;
		this.mlist = list;
		mInflater = LayoutInflater.from(mContext);
	}

	public void upDataAdapter(ClassTypeEntity[] list) {
		this.mlist = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mlist.length;
	}

	@Override
	public ClassTypeEntity getItem(int position) {
		return mlist[position];
	}

	@Override
	public long getItemId(int position) {
		return mlist[position].hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final HoldView mView;
		if (null == convertView) {
			mView = new HoldView();
			convertView = mInflater.inflate(R.layout.wine_fitler_item, null);
			mView.tv_two = (TextView) convertView.findViewById(R.id.tv_two);
			convertView.setTag(mView);
		} else {
			mView = (HoldView) convertView.getTag();
		}
		try {
			final int index = position;
			final ClassTypeEntity deal = mlist[position];
			mView.tv_two.setText(deal.getName());
			if (deal.isSelect()) {
				mView.tv_two.setTextColor(mContext.getResources().getColor(
						R.color.write));
				mView.tv_two
						.setBackgroundResource(R.drawable.shape_appcolor);
			} else {
				mView.tv_two.setTextColor(mContext.getResources().getColor(
						R.color.C7C7C7));
				mView.tv_two
						.setBackgroundResource(R.drawable.shape_gaycolor);
			}
			mView.tv_two.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != mIsOnClick) {
						mIsOnClick.setMyOnclick(deal, index);
					}
				}
			});
		} catch (Exception e) {
		}
		return convertView;
	}

	// 口袋
	class HoldView {
		TextView tv_two;
	}

	// 是否点击接口回掉
	public interface IsOnClick {
		void setMyOnclick(ClassTypeEntity mDeal, int index);
	}

	public IsOnClick mIsOnClick;

	public void setIsOnClick(IsOnClick IsOnClick) {
		this.mIsOnClick = IsOnClick;
	}
}
