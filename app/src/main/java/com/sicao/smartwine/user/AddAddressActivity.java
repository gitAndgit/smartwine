package com.sicao.smartwine.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.user.entity.Address;
import com.sicao.smartwine.util.UserInfoUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**添加收货地址页面
 * Created by android on 2016/4/22.
 */
public class AddAddressActivity extends BaseActivity{
    EditText name, phone;
    TextView address;
    private ArrayList<Address> mList;
    private String addresses;
    private EditText et_detail_address;
    @Override
    protected int setView() {
        return 0;
    }

    @Override
    public String setTitle() {
        return "添加收货地址";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void init() {
        RelativeLayout tv_select_address = (RelativeLayout) findViewById(R.id.tv_select_address);
        et_detail_address = (EditText) findViewById(R.id.et_detail_address);// 详细地址信息
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.user_phone);
        address = (TextView) findViewById(R.id.user_address);
        phone.setInputType(InputType.TYPE_CLASS_PHONE);// 控制电话输入
        tv_select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到选择地址页面
                Intent intent = new Intent(AddAddressActivity.this,
                        AddressItemActivity.class);
                startActivityForResult(intent, 110);
            }
        });
    }

    // 回传值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 110 && null != data) {
            addresses = data.getExtras().getString("address");
            address.setText(addresses);
        }

    }



    /**
     * 添加收货地址
     *
     * @param sign
     */
    public void addAddress(final String name, final String phone,
                           final String address) {
        /*String url = ApiClient.URL + "App/addAddress?userToken="
                + UserInfoUtil.getToken(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("realName", name);
        params.put("address", address);
        params.put("tel", phone);
        params.put("userid", UserInfoUtil.getUID(appContext));
        ApiClient.post(this, url, params, new ApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    JSONObject objec = new JSONObject((String) object);
                    dismissProgress();
                    if (objec.getBoolean("status")) {
                        LToastUtil.show(AddAddressActivity.this, "添加地址成功");
                        // 要把这些值带到申请试饮activity
                        Intent intent = new Intent();
                        intent.putExtra("name", name);
                        intent.putExtra("phone", phone);
                        intent.putExtra("address", address);
                        setResult(RESULT_OK, intent);
                        UserInfoUtil.saveDefaultName(AddAddressActivity.this,
                                name);
                        UserInfoUtil.saveDefaultPhone(AddAddressActivity.this,
                                phone);
                        getMyAddressList();
                    } else {
                        LToastUtil.show(AddAddressActivity.this,
                                objec.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ApiException() {

            @Override
            public void error(String error) {

            }
        });*/
    }

    /***
     * 设置默认地址
     *
     */
    private void setMyDefaultAddress(String addressId) {
        /*String url = ApiClient.URL + "App/setDefaultAddress?userToken="
                + UserInfoUtil.getToken(this) + "&id=" + addressId;
        ApiClient.get(this, url, new ApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    JSONObject objec = new JSONObject((String)object);
                    dismissProgress();
                    if (objec.getBoolean("status")) {
                        mList.get(0).setIsdefault(true);
                        AddAddressActivity.this.finish();
                    } else {
                        if ("401".equals(objec.getString("error_code"))) {
                            ApiClient
                                    .resetLogin(AddAddressActivity.this);
                            return;
                        }
                        LToastUtil.show(AddAddressActivity.this,
                                objec.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ApiException() {

            @Override
            public void error(String error) {

            }
        });*/
    }

    /***
     * 获取我的地址
     *
     */
    private void getMyAddressList() {
        /*String url = ApiClient.URL + "App/getAddress?userToken="
                + UserInfoUtil.getToken(this);
        ApiClient.get(this, url, new ApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    JSONObject objec = new JSONObject((String)object);
                    dismissProgress();
                    if (objec.getBoolean("status")) {
                        ArrayList<Address> list = new ArrayList<Address>();
                        JSONObject info = objec.getJSONObject("info");
                        JSONArray array = info.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            Address add = new Address();
                            JSONObject address = array.getJSONObject(i);
                            add.setAddress(address.getString("address"));
                            add.setPhone(address.getString("tel"));
                            add.setName(address.getString("realName"));
                            add.setId(address.getString("id"));
                            String defaults = address
                                    .getString("default");
                            if ("1".equals(defaults)) {
                                add.setIsdefault(true);
                            } else {
                                add.setIsdefault(false);
                            }
                            list.add(add);
                        }
                        if (list.size() == 1) {
                            setMyDefaultAddress(list.get(0).getId());
                            Intent intent = new Intent(
                                    Constants.SINGLE_ADDRESS);
                            intent.putExtra("singleaddress",
                                    list.get(0));
                            sendBroadcast(intent);
                        }else{
                            AddAddressActivity.this.finish();
                        }
                        mList = list;

                    } else {
                        if ("401".equals(objec.getString("error_code"))) {
                            ApiClient
                                    .resetLogin(AddAddressActivity.this);
                            return;
                        }
                        LToastUtil.show(AddAddressActivity.this,
                                objec.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ApiException() {

            @Override
            public void error(String error) {

            }
        });*/

    }
}
