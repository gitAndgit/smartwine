package com.sicao.smartwine.user.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.user.entity.Address;

import java.util.ArrayList;

/***
 * 收货地址适配器
 * 
 * @author mingqi'li
 * 
 */
public class AddressAdapter extends BaseAdapter {

	ArrayList<Address> mList;
	Context mContext;
	LayoutInflater mInflater;

	public AddressAdapter(Context context, ArrayList<Address> list) {
		this.mList = list;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
	}

	public void setmList(ArrayList<Address> mList) {
		this.mList = mList;
	}

	public void update(ArrayList<Address> list) {
		setmList(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Address getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mList.get(position).hashCode();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.address_item, null);
			mViewHolder.name = (TextView) convertView.findViewById(R.id.name);
			mViewHolder.setaddress = (TextView) convertView
					.findViewById(R.id.user_set_address);
			mViewHolder.delete = (TextView) convertView
					.findViewById(R.id.user_delete);
			mViewHolder.address = (TextView) convertView
					.findViewById(R.id.user_address);
			mViewHolder.phone = (TextView) convertView
					.findViewById(R.id.user_phone);
			mViewHolder.select = (ImageView) convertView
					.findViewById(R.id.set_default_img);
			mViewHolder.selectlayout=(LinearLayout) convertView.findViewById(R.id.linearLayout1);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		Address address = mList.get(position);
		mViewHolder.name.setText(address.getName());
		mViewHolder.phone.setText(address.getPhone());
		mViewHolder.address.setText(address.getAddress());
		if (address.isIsdefault()) {
			mViewHolder.setaddress.setTextColor(mContext.getResources().getColor(R.color.baseColor));
			mViewHolder.select
					.setImageResource(R.drawable.skyblue_platform_checked);
		} else {
			mViewHolder.setaddress.setTextColor(Color.parseColor("#000000"));
			mViewHolder.select
					.setImageResource(R.drawable.skyblue_platform_checked_disabled);
		}
		final int index=position;
		mViewHolder.selectlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null!=setListener){
					setListener.setPosition(index);
				}
			}
		});
		mViewHolder.delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
              if(null!=deleteListener){
            	  deleteListener.setPosition(index);
              }
				
			}
		});
		//申请试饮修改地址回调
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addressListener.resiveAdress(index);
				
			}
		});
		return convertView;
	}

	ViewHolder mViewHolder;

	class ViewHolder {
		TextView name, phone, address, setaddress, delete;
		ImageView select;
		LinearLayout selectlayout;
	}

	public interface SetListener {

		public void setPosition(int position);

	}

	private SetListener setListener;

	public void setSetListener(SetListener setListener) {
		this.setListener = setListener;
	}

	public interface DeleteListener {

		public void setPosition(int position);

	}
	private DeleteListener deleteListener;

	public void setdeleteListener(DeleteListener dListener) {
		this.deleteListener = dListener;
	}
	//修改地址的接口回调
	public interface ResiveAddressListener{
		public void resiveAdress(int position);
	}
	public ResiveAddressListener addressListener;
	public void setResiveAdress(ResiveAddressListener addressListener){
		this.addressListener=addressListener;
	}
}
