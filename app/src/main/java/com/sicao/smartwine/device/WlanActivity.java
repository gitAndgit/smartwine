package com.sicao.smartwine.device;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.adapter.WifiAdapter;

import java.util.ArrayList;

/***
 * WIFI列表
 */
public class WlanActivity extends BaseActivity {
    private ArrayList<ScanResult> mList;
    private WifiAdapter mAdapter;
    private ListView mListview;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_WIFI);
    }

    @Override
    public int setView() {
        return R.layout.activity_wlan;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListview = (ListView) findViewById(R.id.listView);
        mList = new ArrayList<ScanResult>();
        mAdapter = new WifiAdapter(this, mList);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult scan = mList.get(position);
                Intent intent = new Intent();
                intent.putExtra("wifi", scan);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        // 获取WIFI列表数据
        LifeClient.getWifiList(getApplicationContext(), new com.sicao.smartwine.api.LifeClient.ApiListCallBack() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> void response(ArrayList<T> list) {
                mList = (ArrayList<ScanResult>) list;
                mAdapter.update(mList);
            }
        }, new com.sicao.smartwine.api.LifeClient.ApiException() {
            @Override
            public void error(String error) {
                Toast.makeText(getApplicationContext(), error + "",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
