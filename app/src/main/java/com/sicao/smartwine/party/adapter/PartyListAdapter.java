package com.sicao.smartwine.party.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.R;
import com.sicao.smartwine.party.PartyDetailActivity;
import com.sicao.smartwine.shop.entity.Sns;

import java.util.ArrayList;

/*
 * 集品酒会适配器
 * 
 * @author mingqi'li
 * 
 */
public class PartyListAdapter extends BaseAdapter {
    ArrayList<Sns> list;
    Context context;
    FrameLayout.LayoutParams params;

    /*
     * 此构造方法用于普通文章列表适配器 如:SnsListActivity
     *
     * @param context 上下文
     *
     * @param list 文章列表
     */
    public PartyListAdapter(Context context, ArrayList<Sns> list) {
        super();
        this.context = context;
        this.list = list;
        params = new FrameLayout.LayoutParams(
                AppContext.metrics.widthPixels / 3,
                AppContext.metrics.widthPixels / 3);
    }

    public void setList(ArrayList<Sns> list) {
        this.list = list;
    }

    public void update(ArrayList<Sns> list) {
        setList(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Sns getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.list.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final int position2 = position;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.talent_party_list_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.iv_pic = (ImageView) convertView
                    .findViewById(R.id.iv_pic);
            viewHolder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            viewHolder.tv_address = (TextView) convertView
                    .findViewById(R.id.address_text);
            viewHolder.tv_people = (TextView) convertView
                    .findViewById(R.id.people_text);
            viewHolder.tv_time = (TextView) convertView
                    .findViewById(R.id.time);
            viewHolder.state = (TextView) convertView
                    .findViewById(R.id.party_state);
            viewHolder.tv_text_status = (LinearLayout) convertView
                    .findViewById(R.id.party_statue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //保证不重复设置布局大小
        if (null == viewHolder.iv_pic.getTag()) {
            viewHolder.iv_pic.setLayoutParams(params);
            viewHolder.iv_pic.setTag(new Object());
        }
        final Sns sns = list.get(position2);
        viewHolder.tv_title.setText(sns.getTitle());
        viewHolder.iv_pic.setTag(sns.getCover());
        if (null != sns.getStatus() && !sns.getStatus().equals("")) {
            if (sns.getStatus().equals("2")) {
                viewHolder.tv_text_status.setVisibility(View.VISIBLE);
            } else if (sns.getStatus().equals("1")) {
                viewHolder.tv_text_status.setVisibility(View.GONE);
            }
        } else {
            viewHolder.tv_text_status.setVisibility(View.GONE);
        }
        AppContext.imageLoader.displayImage(sns.getApp_cover(),
                viewHolder.iv_pic, AppContext.gallery);
        if (sns.isEnd()) {
            viewHolder.state.setText("已结束");
        } else {
            viewHolder.state.setText("进行中");
        }
        try {
            viewHolder.tv_address
                    .setText(sns.getAddress().equals("") ? "深圳市新朝酒窖体验馆"
                            : sns.getAddress());
            viewHolder.tv_time
                    .setText(sns.getStart_time().equals("") ? "星期一  14:00"
                            : sns.getStart_time() + "");
            viewHolder.tv_people.setText(sns.getPeople().equals("") ? "20人"
                    : sns.getPeople() + "人");
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //已发布的最新活动
                    Intent intent = new Intent(context,
                            PartyDetailActivity.class);
                    intent.putExtra("partyID", sns.getCid());
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv_pic;
        TextView tv_title;
        TextView tv_time;
        TextView tv_address;
        TextView tv_people;
        LinearLayout tv_text_status;
        //活动状态
        TextView state;
    }
}
