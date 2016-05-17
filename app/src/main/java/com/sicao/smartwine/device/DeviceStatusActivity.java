package com.sicao.smartwine.device;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.LifeClient;
import com.sicao.smartwine.device.adapter.DeviceAdapter;
import com.smartline.life.device.Device;

import java.util.ArrayList;

/***
 * 我的酒柜列表数据
 *
 * @author techssd
 * @version 1.0.0
 */
public class DeviceStatusActivity extends BaseActivity {
    //设备列表
    //P2RefreshListView mDeviceListView;
    //设备列表数据源
    ArrayList<Device> mListData = new ArrayList<Device>();
    //数据列表适配器
    DeviceAdapter mAdapter;
    ImageView tv_connect_icon;
    LinearLayout ll_status;
    TextView tv_status,tv_status_prompt,tv_complete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public String setTitle() {
        return "连接成功";
    }

    @Override
    protected int setView() {
        return R.layout.activity_device_status;
    }

    public void init() {
        // mDeviceListView=(P2RefreshListView)findViewById(R.id.view4);
        // mAdapter=new DeviceAdapter(this,mListData);
        //mDeviceListView.setAdapter(mAdapter);
        tv_connect_icon= (ImageView) findViewById(R.id.tv_connect_icon);
        ll_status=(LinearLayout) findViewById(R.id.ll_status);
        tv_status=(TextView)findViewById(R.id.tv_status);
        tv_status_prompt=(TextView)findViewById(R.id.tv_status_prompt);
        tv_complete=(TextView) findViewById(R.id.tv_complete);
        LifeClient.getDeviceList(this, new LifeClient.ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> arrayList) {
                mListData = (ArrayList<Device>) arrayList;
                if(mListData.size()<=0){
                    tv_connect_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fail));
                    tv_status.setText("连接失败");
                    tv_status_prompt.setText("请检查设备是否运行正常，指示灯是否闪烁");
                    tv_complete.setText("确定");
                    ll_status.setVisibility(View.VISIBLE);
                }else{
                    tv_status.setText("连接成功");
                    tv_complete.setText("完成");
                    tv_status_prompt.setText("现在您可以使用手机控制您的设备");
                    tv_connect_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_success));
                    ll_status.setVisibility(View.VISIBLE);
                }
            }
        }, new LifeClient.ApiException() {
            @Override
            public void error(String s) {

            }
        });
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }
}
