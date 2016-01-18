package com.sicao.smartwine.user;

import android.os.Bundle;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;

/***
 * 地址管理页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class AddressListActivity extends BaseActivity {

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
        setContentView(R.layout.activity_address_list);
    }
}
