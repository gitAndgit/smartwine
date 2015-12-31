package com.sicao.smartwine.device.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sicao.smartwine.R;

import java.util.ArrayList;

/**
 * Created by techssd on 2015/12/30.
 */
public class WifiAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ScanResult> mlist;
    private LayoutInflater mInflater;

    public void setMlist(ArrayList<ScanResult> mlist) {
        this.mlist = mlist;
    }

    public void update(ArrayList<ScanResult> list) {
        setMlist(list);
        this.notifyDataSetChanged();
    }

    public WifiAdapter(Context context, ArrayList<ScanResult> list) {
        this.mContext = context;
        this.mlist = list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public ScanResult getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mlist.get(position).hashCode();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.wifi_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult scan = mlist.get(position);
        holder.name.setText(scan.SSID + "");
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}
