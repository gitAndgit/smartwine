package com.sicao.smartwine.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.ConfigActivity;
import com.sicao.smartwine.device.adapter.DeviceAdapter;
import com.sicao.smartwine.util.LToastUtil;
import com.sicao.smartwine.util.SwipeMenuListView.SwipeMenu;
import com.sicao.smartwine.util.SwipeMenuListView.SwipeMenuCreator;
import com.sicao.smartwine.util.SwipeMenuListView.SwipeMenuItem;
import com.sicao.smartwine.util.SwipeMenuListView.SwipeMenuListView;
import com.sicao.smartwine.util.UserInfoUtil;
import com.smartline.life.device.Device;

import java.util.ArrayList;

/***
 * 我的酒柜列表数据
 * @author techssd
 * @version  1.0.0
 */
public class DeviceListActivity extends BaseActivity {
    //设备列表
    SwipeMenuListView mDeviceListView;
    //设备列表数据源
    ArrayList<Device>mListData=new ArrayList<Device>();
    //数据列表适配器
    DeviceAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_device_list_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_list;
    }
    public void init(){
        mDeviceListView=(SwipeMenuListView)findViewById(R.id.view4);
        mAdapter=new DeviceAdapter(this,mListData);
        mDeviceListView.setAdapter(mAdapter);
        rightText.setVisibility(View.VISIBLE);
        rightText.setText("添加新设备");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifi= (WifiManager) getSystemService(Context.WIFI_SERVICE);
                String wifiName=wifi.getConnectionInfo().getSSID();
                startActivity(new Intent(DeviceListActivity.this, ConfigActivity.class).putExtra("wifiName",wifiName));
            }
        });
        LifeClient.getDeviceList(this, new LifeClient.ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> arrayList) {
                mListData = (ArrayList<Device>) arrayList;
                mAdapter.update(mListData);
            }
        }, new LifeClient.ApiException() {
            @Override
            public void error(String s) {

            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color
//                        .parseColor("#EDEDED")));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.remove);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mDeviceListView.setMenuCreator(creator);
        mDeviceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                LToastUtil.show(DeviceListActivity.this,"删除设备");
            }
        });
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 关闭页面，进入设备列表页面
                if(!mListData.get(position).isOnline()){
                    UserInfoUtil.saveDeviceID(DeviceListActivity.this, mListData.get(position).getJid());
                    Intent intent = new Intent();
                    intent.putExtra("new_device_id", mListData.get(position).getJid());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    LToastUtil.show(DeviceListActivity.this,"当前设备已连接，请选择其他设备");
                }
            }
        });
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}

