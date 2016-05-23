package com.sicao.smartwine.user;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.BaseActivity;

/**
 * Created by android on 2016/5/16.
 */
public class UserActivity extends BaseActivity {
    Handler mHandler;
    /**
     * 昵称,性别,生日,个性签名,收货地址,手机号，未绑定手机号 我的余额
     */
    TextView mNickname, mSex, mBirthday, mSign, mPhone;
    RelativeLayout rr_bing_phone;
    // private ImageView cb_notify;
    /**
     * 头像
     */
    SimpleDraweeView mAvatar;
    /***
     * 个人信息实体类
     */

    /**
     * 头像上传是文件地址
     */
    String mImagePath = "";
    TextView startDateTime; // 开始时间
    // 友盟个人资料事件
    String umenguserinfo = "umenguserinfo";
    String umengresetphone = "umengresetphone";


    String initStartDateTime = "1990年9月3日 "; // 初始化开始时间
    String initEndDateTime = "1980年1月1日 "; // 初始化结束时间
    private TextView change_phone_number_tv;
    private ImageView user_change_iv;

    @Override
    public String setTitle() {
        return "个人中心";
    }

    @Override
    protected int setView() {
        return 0;
    }


}
