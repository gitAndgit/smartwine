package com.sicao.smartwine.party;

import android.os.Bundle;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;

/***
 * 活动报名
 *
 * @author techssd
 * @version 1.0.0
 */
public class PartySignUpActivity extends BaseActivity {

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_party_signup_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_party_sign_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
