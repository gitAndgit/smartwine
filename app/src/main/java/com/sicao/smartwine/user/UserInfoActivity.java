package com.sicao.smartwine.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.activity.ChangePhone;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.device.entity.PtjUserEntity;
import com.sicao.smartwine.user.entity.DateTimePickDialogUtil;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.LToastUtil;
import com.sicao.smartwine.util.StringUtil;
import com.sicao.smartwine.util.UploadFileUtil;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 个人信息页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class UserInfoActivity extends BaseActivity {


    Handler mHandler;
    /**
     * 昵称,性别,生日,个性签名,收货地址,手机号，未绑定手机号 我的余额
     */
    TextView mNickname, mSex, mBirthday, mSign, mAddress, mPhone, Amount,tv_change;//更改手机号
    RelativeLayout rr_bing_phone;
    ImageView cb_notify;
    /**
     * 头像
     */
    SimpleDraweeView mAvatar;
    /***
     * 个人信息实体类
     */
    PtjUserEntity mUserEntity;
    /**
     * 头像上传是文件地址
     */
    String mImagePath = "";
    TextView startDateTime; // 开始时间
    DateTimePickDialogUtil dateTimePicKDialog;
    ProgressDialog progressDialog;

    String initStartDateTime = "1990年9月3日 "; // 初始化开始时间
    String initEndDateTime = "1980年1月1日 "; // 初始化结束时间

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_user_userinfo_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case UploadFileUtil.UPLOAD_FILE_SUCCESS:
                        String content = (String) msg.obj;
                        // 1,将头像地址反馈给服务器
                        try {
                            JSONObject object = new JSONObject(content);
                            if (object.getBoolean("status")) {
                                // 2，将图片显示出来
                                Bitmap bm = CropImage.optimizeBitmap(mImagePath,
                                        AppContext.metrics.widthPixels / 3,
                                        AppContext.metrics.widthPixels / 3);
                                mAvatar.setImageBitmap(bm);
                                UserInfoUtil.saveUserAvatar(
                                        UserInfoActivity.this,
                                        object.getJSONObject("info").getString(
                                                "path"));
                                Toast.makeText(UserInfoActivity.this, "头像更改OK", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserInfoActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                        }
                        break;
                }
            }
        };
        progressDialog=new ProgressDialog(this);
        getUserInfo();
        getMyDefaultAddress();
        boolean is_received = UserInfoUtil.getMsgON(this);
        /*
		 * 设置推送开关
		 */
        if (is_received) {
            cb_notify.setImageResource(R.drawable.open);
        } else {
            cb_notify.setImageResource(R.drawable.close);
        }
        cb_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is = UserInfoUtil.getMsgON(UserInfoActivity.this);
                if (is) {
                    cb_notify.setImageResource(R.drawable.close);
                    UserInfoUtil.setMsgON(UserInfoActivity.this, false);
                } else {
                    cb_notify.setImageResource(R.drawable.open);
                    UserInfoUtil.setMsgON(UserInfoActivity.this, true);
                }
            }
        });
    }

    /**
     * 控件初始化
     */
    public void init() {
        mBirthday = (TextView) findViewById(R.id.birthday);// 生日
        mBirthday.setText(initStartDateTime);
        mNickname = (TextView) findViewById(R.id.nick_name);
        mSex = (TextView) findViewById(R.id.sex);
        mSign = (TextView) findViewById(R.id.sign);
        mAddress = (TextView) findViewById(R.id.address);
        mAvatar = (SimpleDraweeView) findViewById(R.id.avatar);
        mPhone = (TextView) findViewById(R.id.user_phone);
        tv_change=(TextView)findViewById(R.id.tv_change);//更改手机号
        rr_bing_phone = (RelativeLayout) findViewById(R.id.rr_bing_phone);
        cb_notify = (ImageView) findViewById(R.id.cb_notify);
    }

    /***
     * 控件的点击事件
     *
     * @param v
     */
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.nick_name:// 昵称
            case R.id.rr_name:
//                startActivityForResult(new Intent(this, NickNameActivity.class),
//                        Constants.UPDATE_USER_NICKNAME);
                break;
            case R.id.sex:// 性别
            case R.id.rr_sex_select:
//                startActivityForResult(new Intent(this, SexActivity.class),
//                        Constants.UPDATE_USER_SEX);
                break;
            case R.id.birthday:// 生日
            case R.id.rr_birsty:
//                dateTimePicKDialog = new DateTimePickDialogUtil(UserActivity.this,
//                        initEndDateTime, false);
//                dateTimePicKDialog.dateTimePicKDialog(mBirthday);
//                dateTimePicKDialog.setMakeSure(new MakeSureListener() {
//                    @Override
//                    public void makeSure(String content) {
//                        // Api接口
//                        updateBirthday(content);
//                    }
//                });

                break;
            case R.id.sign:// 个性签名
//                startActivityForResult(new Intent(this, SignActivity.class),
//                        Constants.UPDATE_USER_SIGN);
                break;
            case R.id.address:// 地址
                LToastUtil.show(this,"正在开发");
                startActivity(new Intent(this,AddressListActivity.class));
//                startActivityForResult(new Intent(this, MyAddressActivity.class),
//                        Constants.REVISE_ADDRESS);
                break;
            case R.id.avatar:// 头像
//                Intent intentPhoto = new Intent(UserActivity.this, CropImage.class);
//                intentPhoto.putExtra(CropImage.outputX, 500);
//                intentPhoto.putExtra(CropImage.outputY, 500);
//                intentPhoto.putExtra(CropImage.aspectX, 1);
//                intentPhoto.putExtra(CropImage.aspectY, 1);
//                intentPhoto.putExtra("ifTailorFlag", true);// 标志位启动裁剪
//                this.startActivityForResult(intentPhoto,
//                        Constants.OPEN_OR_CREATE_PICTURE);
                break;
            case R.id.tv_change:// 绑定手机号
            case R.id.rr_bing_phone:
                startActivityForResult(new Intent(this, ChangePhone.class),
                        10086);

                break;
            case R.id.Amount:// 跳到我的余额
//                startActivity(new Intent(UserActivity.this, MyRemaining.class));
                break;
            case R.id.tv_change_password:
                LToastUtil.show(this,"正在开发");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
//            if (requestCode == Constants.OPEN_OR_CREATE_PICTURE) {
//                // 图片信息返回
//                Boolean have_image = data.getBooleanExtra(CropImage.have_image,
//                        false);
//                if (have_image) {// 取出图片
//                    mImagePath = data.getStringExtra(CropImage.image_path);
//                    // 1，上传该图片
//                    UploadFileUtil.upload(mHandler, Uri.parse(mImagePath),
//
//                            UserInfoUtil.getToken(this),
//                            UserInfoUtil.getUSER_TYPE(this));
//
//
//                }
//            } else if (requestCode == Constants.UPDATE_USER_SIGN) {
//                // 修改个性签名OK
//                mSign.setText(data.getExtras().getString("updatesign"));
//            } else if (requestCode == Constants.UPDATE_USER_NICKNAME) {
//                // 修改昵称
//                mNickname.setText(data.getExtras().getString("updatenickname"));
//            } else if (requestCode == Constants.UPDATE_USER_SEX) {
//                // 修改昵称
//                mSex.setText(data.getExtras().getString("updatesex"));
//            } else if (requestCode == Constants.BIND_PHONE) {
//                mPhone.setText(StringUtil.patternPhoneNum(data.getExtras()
//                        .getString("bindPhone")));
//            } else if (requestCode == Constants.REVISE_ADDRESS) {
//                mAddress.setText(data.getExtras().getString("add"));
//            }
            if(requestCode==10086){
                String phone=data.getExtras().getString("bindPhone");
                mPhone.setText(StringUtil
                        .patternPhoneNum(phone));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /***
     * 获取用户信息
     */
    public void getUserInfo() {
        ApiClient.getUserInfo(this, UserInfoUtil.getUID(this), UserInfoUtil.getToken(this), new ApiCallBack() {
            @Override
            public void response(Object object) {
                mUserEntity = (PtjUserEntity) object;
                // 显示用户信息
                mNickname.setText(mUserEntity.getNickname());
                mSign.setText(TextUtils.isEmpty(mUserEntity
                        .getSignature()) ? "\"这个人很懒，什么也没说\""
                        : mUserEntity.getSignature());
                mAvatar.setImageURI(Uri.parse(mUserEntity.getAvatar()));
                if ("".equals(StringUtil.patternPhoneNum(mUserEntity
                        .getMobile()))) {
                    // 手机号等于空时
                    mPhone.setClickable(true);
                    mPhone.setText("未绑定");
                } else {
                    // 手机号不等于空 设置手机号
                    mPhone.setClickable(false);
                    rr_bing_phone.setClickable(false);
                    mPhone.setText(StringUtil
                            .patternPhoneNum(mUserEntity.getMobile()));
                }
                if (mUserEntity.getSex().equals("f")) {
                    mSex.setText("女");
                } else {
                    mSex.setText("男");
                }
                mBirthday.setText(mUserEntity.getBirthday());
                progressDialog.dismiss();
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        });
    }

    /***
     * 保存上传的图片
     *
     * @param id
     */
    public void savePicture(final String id) {

//        RequestQueue queue = ApiClient.getVolley(this);
//        String url = ApiClient.URL + "App/savePicture?userToken="
//                + UserInfoUtil.getToken(this) + "&id=" + id;
//        StringRequest request = new StringRequest(url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            if (object.getBoolean("status")) {
//                                dismissProgress();
//                                LToastUtil.show(UserActivity.this, response);
//                            } else {
//                                if ("401".equals(object.getString("error_code"))) {
//                                    ApiClient.resetLogin(UserActivity.this);
//                                    return;
//                                }
//                                LToastUtil.show(UserActivity.this,
//                                        object.getString("info"));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if ("".equals(error) || null == null) {
//                    LToastUtil.show(UserActivity.this, "亲，网络不好哦~~~");
//                    return;
//                }
//                LToastUtil.show(UserActivity.this, error.getMessage());
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("ptj-stat-json",ApiClient.getHttpHeaderVersin(context));
//                return map;
//            }
//        };
//        queue.add(request);
    }

    /**
     * 修改生日
     *
     * @param
     */
    public void updateBirthday(final String birthday) {
//        RequestQueue queue = ApiClient.getVolley(this);
//        String url = ApiClient.URL + "App/setBirthday?userToken="
//                + UserInfoUtil.getToken(this);
//        StringRequest request = new StringRequest(Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject objec = new JSONObject(response);
//                            dismissProgress();
//                            if (objec.getBoolean("status")) {
//                                LToastUtil.show(UserActivity.this, "年龄修改OK");
//                                mBirthday.setText(birthday);
//                            } else {
//                                if ("401".equals(objec.getString("error_code"))) {
//                                    ApiClient.resetLogin(UserActivity.this);
//                                    return;
//                                }
//                                LToastUtil.show(UserActivity.this,
//                                        objec.getString("info"));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if ("".equals(error) || null == null) {
//                    LToastUtil.show(UserActivity.this, "亲，网络不好哦~~~");
//                    return;
//                }
//                dismissProgress();
//                LToastUtil.show(UserActivity.this, error.getMessage());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("birthday", birthday);
//                return map;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("ptj-stat-json",ApiClient.getHttpHeaderVersin(context));
//                return map;
//            }
//        };
//        queue.add(request);
    }

    /***
     * 获取默认地址
     */
    private void getMyDefaultAddress() {
//        RequestQueue queue = ApiClient.getVolley(this);
//        StringRequest request = new StringRequest(ApiClient.URL
//                + "App/getDefaultAddress?userToken="
//                + UserInfoUtil.getToken(this), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject objec = new JSONObject(response);
//                    dismissProgress();
//                    if (objec.getBoolean("status")) {
//                        // 更新UI
//                        JSONObject info = objec.getJSONObject("info");
//                        JSONArray array = info.getJSONArray("list");
//                        if (array.length() > 0) {
//                            JSONObject address = array.getJSONObject(0);
//                            mAddress.setText(address.getString("address"));
//                        }
//                    } else {
//                        if ("401".equals(objec.getString("error_code"))) {
//                            ApiClient.resetLogin(UserActivity.this);
//                            return;
//                        }
//                        LToastUtil.show(UserActivity.this,
//                                objec.getString("info"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if ("".equals(error) || null == null) {
//                    LToastUtil.show(UserActivity.this, "亲，网络不好哦~~~");
//                    return;
//                }
//                dismissProgress();
//                LToastUtil.show(UserActivity.this, error.getMessage());
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("ptj-stat-json",ApiClient.getHttpHeaderVersin(context));
//                return map;
//            }
//        };
//        queue.add(request);
//    }
    }
}
