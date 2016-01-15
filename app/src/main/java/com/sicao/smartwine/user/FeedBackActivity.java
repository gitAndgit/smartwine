package com.sicao.smartwine.user;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.pay.Constants;
import com.sicao.smartwine.util.UserInfoUtil;

/***
 * 意见反馈
 *
 * @author techssd
 * @version 1.0.0
 */
public class FeedBackActivity extends BaseActivity {
    // 意见反馈控件
    EditText sug;
    String content = "";

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_user_feedback_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sug = (EditText) findViewById(R.id.et_input);
        rightText.setVisibility(View.VISIBLE);
        rightText.setText("提交");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = sug.getText().toString().trim();
                if (!"".equals(content)) {
                    ApiClient.feedBack(FeedBackActivity.this,
                            UserInfoUtil.getToken(FeedBackActivity.this),
                            Constants.FEED_BACK_USER, content, "",
                            new com.sicao.smartwine.util.ApiCallBack() {
                                @Override
                                public void response(Object object) {
                                    sug.setText("");
                                    finish();
                                    Toast.makeText(FeedBackActivity.this, "谢谢你的建议,我们将尽快做出修改", Toast.LENGTH_SHORT).show();
                                }
                            }, null);
                } else {
                    Toast.makeText(FeedBackActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
