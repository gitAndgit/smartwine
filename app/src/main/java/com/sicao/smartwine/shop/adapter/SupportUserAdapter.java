package com.sicao.smartwine.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.User;
import com.sicao.smartwine.widget.CircleImageView;

import java.util.ArrayList;

/**
 * 支持的用户的头像
 * 
 * @author mingqi'li
 * 
 */
public class SupportUserAdapter extends BaseAdapter {

	private ArrayList<User> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	// 点赞数
	private String mAllSupport = "";

	public SupportUserAdapter(Context context, ArrayList<User> list,
			String support) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mList = list;
		this.mAllSupport = support;
		this.mInflater = LayoutInflater.from(mContext);
	}

	public void setmList(ArrayList<User> mList) {
		this.mList = mList;
	}

	public void update(ArrayList<User> list, String support) {
		setmList(list);
		this.mAllSupport = support;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public User getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mList.get(position).hashCode();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = this.mInflater.inflate(R.layout.support_user_item,
					null);
			holder.icon = (CircleImageView) convertView
					.findViewById(R.id.circleImageView1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final User user = mList.get(position);
		if (user.isClickable()) {
			if (0 == position) {
				// 第一个，不可点击
				holder.icon.setImageResource(R.drawable.support_user_icon);
				holder.icon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			} else if (mList.size() >= 9 && position == 8) {
				// 最后一个，点击展开
				holder.icon.setImageResource(R.drawable.dynamic_icon);
				if (mAllSupport.length() > 3) {
					// 大于1000取0~倒数第三位的数字作为K(千)的单位
					holder.icon.setmContent(mAllSupport.substring(0, mAllSupport.length()-3) + "k+");
				} else {
					// 小与1000
					holder.icon.setmContent(mAllSupport + "");
				}
				holder.icon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// if (null != openListener) {
						// openListener.open();
						// }
					}
				});
			}

		} else {
			AppContext.imageLoader.displayImage(user.getAvatar(), holder.icon,
					AppContext.gallery);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(mContext,
//							UserCenterActivity.class);
//					intent.putExtra("uid", user.getUid());
//					mContext.startActivity(intent);
				}
			});
		}
		return convertView;
	}

	class ViewHolder {
		CircleImageView icon;
	}

	private OpenListener openListener;

	public void setOpenListener(OpenListener openListener) {
		this.openListener = openListener;
	}

	public interface OpenListener {

		public void open();

	}
}
