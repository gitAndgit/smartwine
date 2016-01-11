package com.sicao.smartwine.party;

import android.os.Bundle;
import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.party.adapter.PartyListAdapter;
import com.sicao.smartwine.shop.entity.Sns;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.widget.P2RefreshListView;
import com.sicao.smartwine.widget.P2RefreshListView.OnLoadMoreListener;
import com.sicao.smartwine.widget.P2RefreshListView.OnRefreshListener;
import java.util.ArrayList;

/***
 * 品酒活动列表
 *
 * @author techssd
 * @version 1.0.0
 */
public class PartyListActivity extends BaseActivity {
    // 品酒活动数据
    ArrayList<Sns> list = new ArrayList<Sns>();
    // 品酒活动列表适配器
    PartyListAdapter adapter;
    // 品酒活动
    P2RefreshListView lv_sns;
    int type = 3;//集品酒会
    int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getSnsList(page, type, 0);
    }

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_party_list_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_party_list;
    }

    private void init() {
        lv_sns = (P2RefreshListView) findViewById(R.id.view3);
        adapter = new PartyListAdapter(this, list);
        lv_sns.setAdapter(adapter);
        lv_sns.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {// 下拉刷新监听
                if (!AppContext.isNetworkAvailable()) {
                    lv_sns.onRefreshComplete();
                } else {
                    page = 1;
                    lv_sns.setCanLoadMore(true);
                    getSnsList(page, type, 0);
                }
            }
        });
        lv_sns.setOnLoadListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {// 上拉加载更多监听
                page++;
                getSnsList(page, type, 0);
            }
        });
    }

    /***
     * 获取活动列表
     *
     * @param page
     * @param cType
     * @param mark
     */
    public void getSnsList(final int page, final int cType, int mark) {
        ApiClient.getPartyList(this, cType, page, mark, new ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> lists) {
                ArrayList<Sns> mlist = (ArrayList<Sns>) lists;
                if (page == 1) {
                    list = mlist;
                } else {
                    list.addAll(mlist);
                }
                lv_sns.onRefreshComplete();
                lv_sns.onLoadMoreComplete();
                adapter.update(list);
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
    }
}
