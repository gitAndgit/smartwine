package com.sicao.smartwine.shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.QiNiuUpLoadQueue;
import com.sicao.smartwine.util.UploadFileUtil;
import com.sicao.smartwine.widget.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/****
 * 商品点评
 *
 * @author techssd
 * @version 1.0.0
 */
public class ShopReviewsActivity extends BaseActivity implements View.OnClickListener {
    ImageView iv_prisent_no5, iv_prisent_no4, iv_prisent_no3,
            iv_prisent_no2, iv_prisent_no1;// 星级控件
    ImageView iv_addphone;// 添加图片按钮
    ImageView iv_phonecontent;// 图片内容
    EditText et_commentcontent;

    String iv_prisent_no = 3 + "";// 评星数
    String contents;// 评论内容
    String mImagePath;
    Handler mHandler;
    String imgId;// 图片Id
    int pid;// 被评论者iD
    String deal_id;// 商品Id
    LinearLayout ll_star;
    TextView tv_coloc_bg;
    // 是否正在回复
    boolean isSending = false;

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_shop_reviews_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_shop_reviews;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pid = bundle.getInt("pid");
        deal_id = bundle.getString("deal_id");

        init();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UploadFileUtil.UPLOAD_FILE_SUCCESS) {
                    String content = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(content);
                        JSONObject info = object.getJSONObject("info");
                        JSONObject file1 = info.getJSONObject("file1");
                        imgId = file1.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contents = et_commentcontent.getText().toString().trim();
                    if (contents.equals("") || null == contents) {
                        Toast.makeText(ShopReviewsActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                    } else {
                        // 有图片时
                        newDealComment(deal_id, contents, imgId, -1 + "", pid
                                + "", iv_prisent_no, "");

                    }

                } else if (msg.what == UploadFileUtil.UPLOAD_FILE_FAIL) {
                    Toast.makeText(ShopReviewsActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        // 提交按钮
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSending) {
                    isSending = true;
                    contents = et_commentcontent.getText().toString().trim();
                    if ("".equals(contents) || null == contents) {
                        Toast.makeText(ShopReviewsActivity.this, "请输入您的评价", Toast.LENGTH_SHORT).show();

                    } else {
                        if (pid != 0) {
                            newDealComment(deal_id, contents, " ", 0 + "", pid
                                    + "", " ", "");
                        } else {
                            if ("".equals(mImagePath) || null == mImagePath) {
                                newDealComment(deal_id, contents, " ", 0 + "",
                                        pid + "", iv_prisent_no, "");
                            } else {
                                QiNiuUpLoadQueue queue = new QiNiuUpLoadQueue(
                                        getApplicationContext());
                                Uri[] uris = new Uri[1];
                                uris[0] = Uri.fromFile(new File(mImagePath));
                                queue.setData(uris);
                                queue.start();
                                queue.setQiNiuUploadListener(new QiNiuUpLoadQueue.QiNiuUploadListener() {
                                    @Override
                                    public void result(ArrayList<String> result) {
                                        if (!"-1".equals(result.get(0))) {
                                            // 3,发表一级回复
                                            newDealComment(deal_id, contents,
                                                    " ", -1 + "", pid + "",
                                                    iv_prisent_no,
                                                    result.get(0));
                                        } else {
                                            Toast.makeText(ShopReviewsActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                                            isSending = false;
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        iv_phonecontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(ShopReviewsActivity.this);
                b.setMessage("您真的要移除吗？");
                b.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                iv_addphone.setVisibility(View.VISIBLE);
                                iv_phonecontent.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        });
                b.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                b.show();
            }
        });

    }

    private void init() {
        //显示右侧发表按钮
        rightText.setVisibility(View.VISIBLE);
        // 星级评价
        iv_prisent_no5 = (ImageView) findViewById(R.id.iv_prisent_no5);
        iv_prisent_no4 = (ImageView) findViewById(R.id.iv_prisent_no4);
        iv_prisent_no3 = (ImageView) findViewById(R.id.iv_prisent_no3);
        iv_prisent_no2 = (ImageView) findViewById(R.id.iv_prisent_no2);
        iv_prisent_no1 = (ImageView) findViewById(R.id.iv_prisent_no1);

        iv_addphone = (ImageView) findViewById(R.id.iv_addphone);// 添加图片按钮
        iv_phonecontent = (ImageView) findViewById(R.id.iv_phonecontent);// 添加照片内容
        ll_star = (LinearLayout) findViewById(R.id.ll_star);// 隐藏评星
        tv_coloc_bg = (TextView) findViewById(R.id.tv_coloc_bg);// 灰线
        if (pid == 0) {
            iv_addphone.setVisibility(View.VISIBLE);
        } else {
            ll_star.setVisibility(View.GONE);
            tv_coloc_bg.setVisibility(View.GONE);
            iv_addphone.setVisibility(View.GONE);
        }
        et_commentcontent = (EditText) findViewById(R.id.et_commentcontent);// 评论内容
        iv_prisent_no1.setOnClickListener(this);
        iv_prisent_no2.setOnClickListener(this);
        iv_prisent_no3.setOnClickListener(this);
        iv_prisent_no4.setOnClickListener(this);
        iv_prisent_no5.setOnClickListener(this);
        iv_addphone.setOnClickListener(this);
        iv_phonecontent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_prisent_no1:
                iv_prisent_no1.setImageResource(R.drawable.start_red);
                iv_prisent_no2.setImageResource(R.drawable.prisent_no);
                iv_prisent_no3.setImageResource(R.drawable.prisent_no);
                iv_prisent_no4.setImageResource(R.drawable.prisent_no);
                iv_prisent_no5.setImageResource(R.drawable.prisent_no);
                iv_prisent_no = 1 + "";
                break;
            case R.id.iv_prisent_no2:
                iv_prisent_no1.setImageResource(R.drawable.start_red);
                iv_prisent_no2.setImageResource(R.drawable.start_red);
                iv_prisent_no3.setImageResource(R.drawable.prisent_no);
                iv_prisent_no4.setImageResource(R.drawable.prisent_no);
                iv_prisent_no5.setImageResource(R.drawable.prisent_no);
                iv_prisent_no = 2 + "";
                break;
            case R.id.iv_prisent_no3:
                iv_prisent_no1.setImageResource(R.drawable.start_red);
                iv_prisent_no2.setImageResource(R.drawable.start_red);
                iv_prisent_no3.setImageResource(R.drawable.start_red);
                iv_prisent_no4.setImageResource(R.drawable.prisent_no);
                iv_prisent_no5.setImageResource(R.drawable.prisent_no);
                iv_prisent_no = 3 + "";
                break;
            case R.id.iv_prisent_no4:
                iv_prisent_no1.setImageResource(R.drawable.start_red);
                iv_prisent_no2.setImageResource(R.drawable.start_red);
                iv_prisent_no3.setImageResource(R.drawable.start_red);
                iv_prisent_no4.setImageResource(R.drawable.start_red);
                iv_prisent_no5.setImageResource(R.drawable.prisent_no);
                iv_prisent_no = 4 + "";
                break;
            case R.id.iv_prisent_no5:
                iv_prisent_no1.setImageResource(R.drawable.start_red);
                iv_prisent_no2.setImageResource(R.drawable.start_red);
                iv_prisent_no3.setImageResource(R.drawable.start_red);
                iv_prisent_no4.setImageResource(R.drawable.start_red);
                iv_prisent_no5.setImageResource(R.drawable.start_red);
                iv_prisent_no = 5 + "";
                break;
            case R.id.iv_addphone:
                Intent intentPhoto = new Intent(ShopReviewsActivity.this, CropImage.class);
                intentPhoto.putExtra(CropImage.outputX, 500);
                intentPhoto.putExtra(CropImage.outputY, 500);
                intentPhoto.putExtra(CropImage.aspectX, 1);
                intentPhoto.putExtra(CropImage.aspectY, 1);
                intentPhoto.putExtra("ifTailorFlag", false);// 标志位不启动裁剪
                startActivityForResult(intentPhoto, 101);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == 101) {
                // 图片信息返回
                Boolean have_image = data.getBooleanExtra(CropImage.have_image,
                        false);
                if (have_image) {// 取出图片
                    mImagePath = data.getStringExtra(CropImage.image_path);
                    // 1，将图片显示出来
                    iv_addphone.setVisibility(View.GONE);
                    iv_phonecontent.setVisibility(View.VISIBLE);
                    Bitmap bm = CropImage.optimizeBitmap(mImagePath,
                            AppContext.metrics.widthPixels / 3,
                            AppContext.metrics.widthPixels / 3);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            AppContext.metrics.widthPixels / 3,
                            AppContext.metrics.widthPixels / 3);
                    params.leftMargin=10;
                    params.rightMargin=10;
                    params.topMargin=10;
                    params.bottomMargin=10;
                    iv_phonecontent.setLayoutParams(params);
                    iv_phonecontent.setImageBitmap(bm);
                }
            }
        }
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     */
    public void hideSoftInputView() {
        try {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            et_commentcontent.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }

    }

    /***
     * 添加点评
     */
    public void newDealComment(final String deal_id, final String content,
                               final String attach_ids, final String type, final String pid,
                               final String star, final String bitmap_paths) {
        ApiClient.sendCommentForShop(this, deal_id, content, attach_ids, type, pid, star, bitmap_paths, new ApiCallBack() {
            @Override
            public void response(Object object) {
                Toast.makeText(ShopReviewsActivity.this, "已点评", Toast.LENGTH_SHORT).show();
                hideSoftInputView();
                setResult(RESULT_OK);
                finish();
            }
        }, null);

    }
}
