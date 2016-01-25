package com.sicao.smartwine.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.R;
import com.smartline.life.device.Device;

import java.util.ArrayList;

/**
 * Created by techssd on 2016/1/23.
 */
public class DeviceAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Device> mList;
    LayoutInflater mInflater;

    public DeviceAdapter(Context context, ArrayList<Device> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater=LayoutInflater.from(mContext);
    }

    public void setmList(ArrayList<Device> mList) {
        this.mList = mList;
    }

    public void update(ArrayList<Device> mList) {
        setmList(mList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Device getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null==convertView){
            convertView=mInflater.inflate(R.layout.device_list_item,null);
            holder=new ViewHolder();
            holder.icon=(SimpleDraweeView)convertView.findViewById(R.id.device_icon);
            holder.name=(TextView)convertView.findViewById(R.id.textView15);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        Device device = mList.get(position);
        holder.name.setText(device.getName()+"\nUDID="+device.getUDID()+"\n在线="+device.isOnline());
        return convertView;
    }
    class ViewHolder{
        com.facebook.drawee.view.SimpleDraweeView icon;
        TextView name;
    }
}
