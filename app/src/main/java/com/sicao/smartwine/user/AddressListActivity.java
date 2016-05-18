package com.sicao.smartwine.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.pay.Constants;
import com.sicao.smartwine.user.adapter.AddressAdapter;
import com.sicao.smartwine.user.entity.Address;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.P2RefreshListView;

import java.util.ArrayList;

/***
 * 地址管理页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class AddressListActivity extends BaseActivity implements View.OnClickListener {
    /***
     * 没有收货地址时显示的布局
     */
    private LinearLayout mNullLayout;

    /***
     * 收货地址列表
     */
    private P2RefreshListView mAddress;
    /***
     * 收货地址适配器
     */
    private ArrayList<Address> mList;
    /***
     * 收货地址数据
     */
    private AddressAdapter mAdapter;
    @SuppressWarnings("unused")
    private TextView add_address_btn;
    private StringBuilder builder;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_user_address_list_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        rightText.setVisibility(View.VISIBLE);
        rightText.setText("添加");
        rightText.setOnClickListener(this);

        mList = new ArrayList<Address>();
        mAdapter = new AddressAdapter(this, mList);
        mAddress.setAdapter(mAdapter);
        /***
         *
         */
        mAdapter.setSetListener(new AddressAdapter.SetListener() {
            @Override
            public void setPosition(final int position) {
                Address add = mList.get(position);
                // 保存默认地址
                UserInfoUtil.saveUSER_ADDRESS(AddressListActivity.this,
                        add.toString());
                ApiClient.defaultConfigAddress(AddressListActivity.this, add.getId(), new ApiCallBack() {
                    @Override
                    public void response(Object object) {
                        for (int i = 0; i < mList.size(); i++) {
                            mList.get(i).setIsdefault(false);
                        }
                        mList.get(position).setIsdefault(true);
                        mAdapter.update(mList);
                    }
                }, null);
            }
        });
        mAdapter.setdeleteListener(new AddressAdapter.DeleteListener() {
            @Override
            public void setPosition(int position) {
                Address address = mList.get(position);
                deleteAddress(address.getId());
                builder.append(address.getId());
                builder.append(",");
                String addressId = builder.toString();
                Intent intent = new Intent(Constants.CANCLE_ADDRESS_ID);
                intent.putExtra("addressId", addressId);
                sendBroadcast(intent);
                setResult(RESULT_CANCELED);
            }
        });
        mAdapter.setResiveAdress(new AddressAdapter.ResiveAddressListener() {
            @Override
            public void resiveAdress(int position) {
                Address add = mList.get(position);
                // 设置回调值
                Intent intent = new Intent();
                // 把返回数据存入Intent
                intent.putExtra("address", add);
                intent.putExtra("add", add.getAddress());
                // 设置返回数据
                setResult(RESULT_OK, intent);
                // 关闭Activity
                finish();
            }
        });
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //添加收货地址信息
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.add_address_btn:

            case R.id.right_text:// 添加地址
                  Intent intent = new Intent(this, AddAddressActivity.class);
//                startActivityForResult(intent, Constants.ADD_USER_ADDRESS);
                break;
        }

    }

    /***
     * 初始化控件
     */
    private void init() {
        mAddress = (P2RefreshListView) findViewById(R.id.address_listview);
        mNullLayout = (LinearLayout) findViewById(R.id.address_layout);
        add_address_btn = (TextView) findViewById(R.id.add_address_btn);
        builder = new StringBuilder();//初始化builde
        // 获取地址
        getMyAddressList();
    }

    /***
     * 获取我的地址列表数据
     */
    public void getMyAddressList() {
        ApiClient.getAddressList(this, new ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                mList = (ArrayList<Address>) list;
                mAdapter.update(mList);
            }
        }, null);
    }

    /***
     * 删除某一个地址
     *
     * @param ID
     */
    public void deleteAddress(String ID) {
        ApiClient.deleteAddressByID(this, ID, new ApiCallBack() {
            @Override
            public void response(Object object) {
                getMyAddressList();
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                Toast.makeText(AddressListActivity.this, "网络异常,请重试!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ADD_USER_ADDRESS) {
                // 添加收货地址成功-----刷新
                getMyAddressList();
            }
        }
    }
}
