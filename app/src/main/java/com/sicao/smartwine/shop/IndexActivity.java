package com.sicao.smartwine.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.shop.adapter.WineLibraryIndexListviewAdapter;
import com.sicao.smartwine.shop.adapter.WineLibraryIndexListviewAdapter.ItemListener;
import com.sicao.smartwine.shop.entity.WineLibraryEntity;
import com.sicao.smartwine.util.ApiListCallBack;

import java.util.ArrayList;

/***
 * 美酒商城首页
 *
 * @author techssd
 * @version 1.0.0
 */
public class IndexActivity extends BaseActivity {
    // 美酒库首页数据列表
    private ListView mListview;
    // 美酒库首页数据适配器
    private WineLibraryIndexListviewAdapter mAdapter;
    // 美酒库首页数据
    private ArrayList<WineLibraryEntity> mList;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_shop_index_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_shop_index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        ApiClient.getShopIndex(this, new ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                mList = (ArrayList<WineLibraryEntity>) list;
                mAdapter.update(mList);
            }
        }, null);
        // item点击事件
        mAdapter.setListener(new ItemListener() {
            @Override
            public void item(WineLibraryEntity entity) {
//                startActivity(new Intent(IndexActivity.this,
//                        WineLibraryActivity.class).putExtra("name",
//                        entity.getTitle()).putExtra("id", entity.getId()));
            }
        });
    }

    /***
     * 初始化试图
     */
    private void init() {
        mListview = (ListView)findViewById(R.id.listView2);
        //魅族去除下拉悬停结构
        mListview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mList = new ArrayList<WineLibraryEntity>();
        mAdapter = new WineLibraryIndexListviewAdapter(this, mList);
        mListview.setAdapter(mAdapter);
    }
}
