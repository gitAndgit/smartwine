package com.sicao.smartwine.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.WineLibraryEntity;
import com.sicao.smartwine.widget.NoScrollGridView;

import java.util.ArrayList;


/***
 * 美酒库首页listview适配器
 *
 * @author mingqi'li
 */
public class WineLibraryIndexListviewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<WineLibraryEntity> mList;

    public WineLibraryIndexListviewAdapter(Context context,
                                           ArrayList<WineLibraryEntity> list) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mList = list;
    }

    public void setmList(ArrayList<WineLibraryEntity> mList) {
        this.mList = mList;
    }

    public void update(ArrayList<WineLibraryEntity> list) {
        setmList(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public WineLibraryEntity getItem(int position) {
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
            convertView = mInflater.inflate(
                    R.layout.wine_library_index_list_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.textView1);
            holder.gridview = (NoScrollGridView) convertView
                    .findViewById(R.id.noScrollGridView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            WineLibraryEntity entity = mList.get(position);
            holder.title.setText(entity.getTitle());
            WineLibraryIndexGridViewAdapter adapter = new WineLibraryIndexGridViewAdapter(
                    mContext, entity);
            adapter.setListener(new WineLibraryIndexGridViewAdapter.ItemClickListener() {
                @Override
                public void item(WineLibraryEntity entity) {
                    if (null != listener) {
                        listener.item(entity);
                    }
                }
            });
            holder.gridview.setAdapter(adapter);
        } catch (Exception e) {
        }
        return convertView;
    }

    class ViewHolder {
        TextView title;
        NoScrollGridView gridview;
    }

    public interface ItemListener {
        public void item(WineLibraryEntity entity);
    }

    private ItemListener listener;

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }
}
