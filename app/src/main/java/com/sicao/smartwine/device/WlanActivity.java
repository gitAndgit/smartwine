package com.sicao.smartwine.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.adapter.WifiAdapter;
import com.sicao.smartwine.util.mydialog.WifiStatusDialog;

import org.jivesoftware.smackx.muc.packet.Destroy;

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
        initView();
    }
    //初始化控件
    protected void initView(){
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
        //wifi已经打开
        if (getWifistatus()){
            //弹出对话框


        }else{
            //wifi已关闭
//            WifiStatusDialog mydialog=new WifiStatusDialog(this,R.style.myDialogTheme1);
//            mydialog.show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("请求打开wifi");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   WifiManager wifi= (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(true);
                    sendBroadcast(new Intent("WIFI"));
                    dialog.dismiss();;
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();;
                }
            });
            builder.create().show();;


        }
        myReceiver=new myReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("WIFI");
        registerReceiver(myReceiver,intentFilter);
    }
    //获取wifi状态
    protected boolean getWifistatus(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiStatus=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(wifiStatus == NetworkInfo.State.CONNECTED||wifiStatus== NetworkInfo.State.CONNECTING){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private myReceiver myReceiver;
    class myReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("WIFI")){
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
                unregisterReceiver(myReceiver);
            }
        }
    }
}
