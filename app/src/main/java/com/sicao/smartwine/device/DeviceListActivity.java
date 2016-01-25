package com.sicao.smartwine.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.adapter.DeviceAdapter;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.P2RefreshListView;
import com.smartline.life.device.Device;

import java.util.ArrayList;

/***
 * 我的酒柜列表数据
 * @author techssd
 * @version  1.0.0
 */
public class DeviceListActivity extends BaseActivity {
    //设备列表
    P2RefreshListView mDeviceListView;
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
        mDeviceListView=(P2RefreshListView)findViewById(R.id.view4);
        mAdapter=new DeviceAdapter(this,mListData);
        mDeviceListView.setAdapter(mAdapter);
        rightText.setVisibility(View.VISIBLE);
        rightText.setText("添加新设备");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceListActivity.this, ConfigActivity.class));
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
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 关闭页面，进入设备列表页面
                UserInfoUtil.saveDeviceID(DeviceListActivity.this, mListData.get(position-1).getJid());
                Intent intent = new Intent();
                intent.putExtra("new_device_id", mListData.get(position-1).getJid());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
