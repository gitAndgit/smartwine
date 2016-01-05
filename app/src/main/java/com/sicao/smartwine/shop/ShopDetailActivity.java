package com.sicao.smartwine.shop;

import android.os.Bundle;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;

/***
 * 美酒详情页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class ShopDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_shop_detail_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_shop_detail;
    }

    private void init() {

    }
}
