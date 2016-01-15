package com.sicao.smartwine.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.WineEntity;

import java.util.ArrayList;

public class WineLibraryAdpter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<WineEntity> mlist;

    public WineLibraryAdpter(Context context, ArrayList<WineEntity> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mlist = list;
    }

    public void upDataAdapter(ArrayList<WineEntity> list) {
        this.mlist = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public WineEntity getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mlist.get(position).hashCode();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoldView mView;
        if (null == convertView) {
            mView = new HoldView();
            convertView = mInflater.inflate(R.layout.wine_library_list, null);
            mView.iv_mycellarimage = (ImageView) convertView
                    .findViewById(R.id.iv_mycellarimage);
            mView.tv_mywinename = (TextView) convertView
                    .findViewById(R.id.tv_mywinename);
            mView.textView1 = (TextView) convertView
                    .findViewById(R.id.textView1);
            mView.rr_jiondetails = (RelativeLayout) convertView.findViewById(R.id.rr_jiondetails);
            convertView.setTag(mView);
        } else {
            mView = (HoldView) convertView.getTag();
        }
        final WineEntity entity = mlist.get(position);
        if (null != entity.getWineImg() && !entity.getWineImg().equals("")) {
            AppContext.imageLoader.displayImage(entity.getWineImg(),
                    mView.iv_mycellarimage, AppContext.gallery);
        } else {
            mView.iv_mycellarimage.setImageResource(R.drawable.ic_launcher);
        }
        mView.tv_mywinename.setText(entity.getWineName());
        mView.textView1.setText("￥ " + entity.getPrice() + "元");
        mView.rr_jiondetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsHit.setMyHitOcclick(entity);
            }
        });
        return convertView;
    }

    // 口袋类
    class HoldView {
        ImageView iv_mycellarimage;
        TextView tv_mywinename, textView1;// 酒名 酒的价格
        RelativeLayout rr_jiondetails;
    }

    //
    public interface IsHit {
        void setMyHitOcclick(WineEntity entity);
    }

    public IsHit mIsHit;

    public void setIsHit(IsHit IsHit) {
        this.mIsHit = IsHit;
    }
}
