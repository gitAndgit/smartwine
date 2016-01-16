package com.sicao.smartwine.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.WineLibraryEntity;


/***
 * 美酒库首页适配器
 * 
 * @author mingqi'li
 * 
 */
public class WineLibraryIndexGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private WineLibraryEntity[] mList;
	private WineLibraryEntity mEntity;

	public WineLibraryIndexGridViewAdapter(Context context,
			WineLibraryEntity entity) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		mEntity = entity;
		this.mList = entity.getSub();
	}

	public void setmList(WineLibraryEntity entity) {
		mEntity = entity;
		this.mList = entity.getSub();
	}

	public void update(WineLibraryEntity entity) {
		setmList(entity);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.length;
	}

	@Override
	public WineLibraryEntity getItem(int position) {
		// TODO Auto-generated method stub
		return mList[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mList[position].hashCode();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.wine_library_index_list_gridview_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.textView1);
			holder.desc = (TextView) convertView.findViewById(R.id.textView2);
			holder.icon = (SimpleDraweeView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final WineLibraryEntity entity = mList[position];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				AppContext.metrics.widthPixels / 8,
				AppContext.metrics.widthPixels / 8);
		params.rightMargin = 10;
		holder.icon.setLayoutParams(params);
		holder.name.setText(entity.getTitle());
		if (!"".equals(entity.getDescription())) {
			holder.desc.setText(entity.getDescription());
		}
		if (!"".equals(entity.getPath())) {
			holder.icon.setImageURI(Uri.parse(entity.getPath()));
		}
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != listener) {
					entity.setDescription(mEntity.getTitle());
					listener.item(entity);
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView name, desc;
		SimpleDraweeView icon;
	}

	public interface ItemClickListener {
		public void item(WineLibraryEntity entity);
	}

	private ItemClickListener listener;

	public void setListener(ItemClickListener listener) {
		this.listener = listener;
	}
}
