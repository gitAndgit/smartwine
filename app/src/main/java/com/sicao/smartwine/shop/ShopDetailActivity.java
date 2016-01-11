package com.sicao.smartwine.shop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter.CancleSupportListener;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter.CommentItemChildClickListener;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter.CommentItemClickListener;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter.SupportListener;
import com.sicao.smartwine.shop.entity.Comment;
import com.sicao.smartwine.shop.entity.CommentList;
import com.sicao.smartwine.shop.entity.GoodsEntity;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.SizeUtil;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.BottomPopupWindow;
import com.sicao.smartwine.widget.CircularBannerView;
import com.sicao.smartwine.widget.NoScrollListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/***
 * 美酒详情页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class ShopDetailActivity extends BaseActivity {
    // 商品图片介绍 点赞 收藏
    private ImageView mGoodImg, iv_support_ig, iv_collect_ig;
    // 商品名称和价格以及介绍 购买文字
    private TextView mGoodname, mGoodprice, mGooddetail, tv_to_buys;
    // 商品之图文简介和酒款介绍展示形式webview
    private WebView mWebdetail, web_view2;
    // 评论列表
    private NoScrollListView lv_comment;
    // 商品id
    private String id = "", url = "";
    // 商品详情
    private GoodsEntity mGoods;
    // 下划线
    private TextView mLine1, mLine2, mLine3;
    // 文章评论内容集合
    private ArrayList<Comment> list_comment;
    // 文章评论列表适配器
    private SnsCommentAdapter adapter;
    // 用来控制是一级回复还是二级回复
    private int pid = 0;// 默认是一级回复 0
    private boolean isLoading = false;// 是否正在加载
    private int page = 1;
    private Handler mHandler;
    // 对评论执行点赞的index
    private int index = 0;
    private CircularBannerView my_banner;// 版蓝图
    private LinearLayout ll_support, ll_shares, ll_collects, ll_to_buys;
    private boolean isload = false;// 是否是去登陆了，如果中间过程登陆了为真 没有登陆为假
    private ArrayList<String> images = new ArrayList<String>();
    //酒窖信息
    private FrameLayout jiujiaoLayout;
    private TextView shopName, shopTitle;
    private ImageView shopIcon;
    private String uid = "";//酒窖用户的uid
    private RelativeLayout rr_promise;
    private TextView tv_add_shop_car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getExtras().getString("windid");
        init();
        ApiClient.getGoodsInfo(this, id, new ApiCallBack() {
            @Override
            public void response(Object object) {
                mGoods = (GoodsEntity) object;
                if (null != mGoods && null != mGoods.getCover()
                        && mGoods.getCover().length > 0) {
                    mGoodImg.setVisibility(View.GONE);
                    String[] subimgs = mGoods.getCover();
                    my_banner.setImageResouce(subimgs, null, null);
                    my_banner.adapter.notifyDataSetChanged();
                    my_banner.setVisibility(View.VISIBLE);
                }
                mGoodname.setText(mGoods.getName());
                mGoodprice.setText("￥" + mGoods.getCurrent_price());
                mGooddetail.setText(Html.fromHtml(mGoods.getDescription()
                        .toString()));
                if (mGoods != null) {
                    String content = mGoods.getBrief();
                    if (content != null) {
                        Document document = Jsoup.parse(content);
                        Elements elements = document.getElementsByTag("img");
                        for (Element element : elements) {
                            String oldattr = element.attr("src");
                            String newattr = oldattr;
                            if (!oldattr.contains("http://")) {
                                newattr = "http://www.putaoji.com" + oldattr;
                            }
                            element.attr("src", newattr);
                            element.attr(
                                    "width",
                                    SizeUtil.px2dip(AppContext.metrics,
                                            AppContext.metrics.widthPixels - 50)
                                            + "");
                            element.attr(
                                    "height",
                                    SizeUtil.px2dip(
                                            AppContext.metrics,
                                            (AppContext.metrics.widthPixels - 50) * 2 / 3)
                                            + "");
                            images.add(newattr);
                        }
                        mWebdetail.loadDataWithBaseURL(null,
                                document.toString(), "text/html", "utf-8", null);

                    }
                }
                if (mGoods.isIs_support()) {
                    ll_support.setTag("true");
                    iv_support_ig.setImageResource(R.drawable.ic_support_p);
                } else {
                    ll_support.setTag("false");
                    iv_support_ig.setImageResource(R.drawable.ic_support_gray);
                }
                url=mGoods.getBuy_address();
                tv_to_buys.setVisibility(View.VISIBLE);
                if (url.equals("")) {
                    tv_to_buys.setText("购买");
                    tv_add_shop_car.setVisibility(View.VISIBLE);
                } else if (url.contains("tmall")) {
                    tv_to_buys.setText("前往 天猫 购买");
                } else if (url.contains("taobao")) {
                    tv_to_buys.setText("前往 淘宝 购买");
                } else {
                    tv_to_buys.setText("前往 京东 购买");
                }
                getCommentList(id,page,10);
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                finish();
            }
        });
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
        my_banner = (CircularBannerView) findViewById(R.id.my_banner);// 我的bann图
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AppContext.metrics.widthPixels, AppContext.metrics.widthPixels);
        my_banner.setLayoutParams(params);
        mGoodImg = (ImageView) findViewById(R.id.good_img);
        mGoodImg.setLayoutParams(params);
        mGoodname = (TextView) findViewById(R.id.good_name);
        mGoodprice = (TextView) findViewById(R.id.good_price);
        mGooddetail = (TextView) findViewById(R.id.good_destail);
        mWebdetail = (WebView) findViewById(R.id.web_view);
        web_view2 = (WebView) findViewById(R.id.web_view2);
        lv_comment = (NoScrollListView) findViewById(R.id.lv_used_wine_listview);
        mLine1 = (TextView) findViewById(R.id.line1);
        mLine2 = (TextView) findViewById(R.id.line2);
        mLine3 = (TextView) findViewById(R.id.line3);
        // 底部四个按钮
        ll_support = (LinearLayout) findViewById(R.id.ll_support);// 点赞
        ll_shares = (LinearLayout) findViewById(R.id.ll_shares);// 分享 更改成收藏
        ll_collects = (LinearLayout) findViewById(R.id.ll_collects);// 收藏 更改成点评
        ll_to_buys = (LinearLayout) findViewById(R.id.ll_to_buys);// 购买布局
        tv_to_buys = (TextView) findViewById(R.id.tv_to_buys);// 购买要显示的文字
        iv_support_ig = (ImageView) findViewById(R.id.iv_support_ig);// 点赞图像
        iv_collect_ig = (ImageView) findViewById(R.id.iv_collect_ig);// 收藏图像
        //酒窖信息部分的布局
        jiujiaoLayout = (FrameLayout) findViewById(R.id.jiujiao_layout);
        shopName = (TextView) findViewById(R.id.hotel_name);
        shopTitle = (TextView) findViewById(R.id.textView3);
        shopIcon = (ImageView) findViewById(R.id.hotel_icon);
        rr_promise = (RelativeLayout) findViewById(R.id.rr_promise);//我们的承诺
        tv_add_shop_car = (TextView) findViewById(R.id.tv_add_shop_car);
        jiujiaoLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(uid)) {
                    //美酒屋
//                    startActivity(new Intent(ShopDetailActivity.this,
//                            WineHourseActivity.class).putExtra("uid", uid));
                }
            }
        });
        rr_promise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //我们的承诺
//                startActivity(new Intent(ShopDetailActivity.this, PromiseActivity.class));

            }
        });
        //
        list_comment = new ArrayList<Comment>();
        adapter = new SnsCommentAdapter(list_comment, this);
        lv_comment.setAdapter(adapter);
        //
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 10035:
                        // 1， 获取回复列表OK
                        @SuppressWarnings("unchecked")
                        ArrayList<Comment> list = (ArrayList<Comment>) msg.obj;
                        if (1 == page) {
                            list_comment = list;
                        } else {
                            list_comment.addAll(list);
                        }
                        // 2，布局回复列表 (异步线程)
                        adapter.update(list_comment);
                        break;
                }

            }
        };
        /***
         * 快速回复入口
         */
        adapter.setCommentItemClickListener(new CommentItemClickListener() {

            @Override
            public void onClick(int index, final Comment comment) {
                if (!UserInfoUtil.getLogin(ShopDetailActivity.this)) {
                    //重新登陆
//                    UIHelper.startLoginActivity(ShopDetailActivity.this,
//                            false);
                    isload = true;
                    return;
                }

                if (comment
                        .getUser()
                        .getUid()
                        .equals(UserInfoUtil
                                .getUID(ShopDetailActivity.this))) {
                    // 一层回复删除事件
                    final BottomPopupWindow removeWindow = new BottomPopupWindow(ShopDetailActivity.this);
                    removeWindow.showAtLocation(lv_comment, Gravity.BOTTOM, 0,
                            0);
                    removeWindow.update(new String[]{"删除评论", "取消"});
                    removeWindow.showAtLocation(content, Gravity.BOTTOM,
                            0, 0);
                    removeWindow.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                        @Override
                        public void onClick(String value) {
                            if ("删除评论".equals(value)) {
//                                getDelComment(id);// 删除接口
                            }
                            removeWindow.dismiss();
                        }
                    });
                    return;
                }
                // 二层回复的上层回复id
                pid = Integer.parseInt(comment.getId());
//                Intent intent = new Intent(ShopDetailActivity.this,
//                        WinComment.class);
//                intent.putExtra("pid", pid);
//                intent.putExtra("deal_id", id + "");
//                startActivityForResult(intent, 202);

            }
        });
        // 二层回复点击事件
        adapter.setChildItemChildClickListener(new CommentItemChildClickListener() {
            @Override
            public void onClick(int position, final CommentList comment) {
                if (!UserInfoUtil.getLogin(ShopDetailActivity.this)) {
                    //重新登陆
//                    UIHelper.startLoginActivity(ShopDetailActivity.this,
//                            false);
                    isload = true;
                    return;
                }
                // 二层回复删除事件
                if (comment.getUida().equals(
                        UserInfoUtil.getUID(ShopDetailActivity.this))) {
                    final BottomPopupWindow removeWindow = new BottomPopupWindow(ShopDetailActivity.this);
                    removeWindow.showAtLocation(lv_comment, Gravity.BOTTOM, 0,
                            0);
                    removeWindow.update(new String[]{"删除评论", "取消"});
                    removeWindow.showAtLocation(content, Gravity.BOTTOM,
                            0, 0);
                    removeWindow.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                        @Override
                        public void onClick(String value) {
                            if ("删除评论".equals(value)) {
//                                getDelComment(id);// 删除接口
                            }
                            removeWindow.dismiss();
                        }
                    });
                    return;
                }
                // 二层回复的上层回复id
                pid = comment.getCommentListid();
//                Intent intent = new Intent(ShopDetailActivity.this,
//                        WinComment.class);
//                intent.putExtra("pid", pid);
//                intent.putExtra("deal_id", id + "");
//                startActivityForResult(intent, 202);
            }
        });
        // 对一层评论添加点赞
        adapter.setSupportListener(new SupportListener() {
            @Override
            public void addSupport(int index) {
            }
        });
        // 对一层评论取消点赞
        adapter.setCancleSupport(new CancleSupportListener() {
            @Override
            public void cancleSupport(int position) {
            }
        });
        tv_to_buys.setVisibility(View.GONE);
    }
    @SuppressLint("SetJavaScriptEnabled")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twjs:// 图文介绍
                mLine1.setVisibility(View.VISIBLE);
                mLine2.setVisibility(View.INVISIBLE);
                mLine3.setVisibility(View.INVISIBLE);
                lv_comment.setVisibility(View.GONE);
                web_view2.setVisibility(View.GONE);
                mWebdetail.setFocusable(false);// 设置不可聚焦 去除后有聚焦现象更新画面
                if (mGoods != null) {
                    String content = mGoods.getBrief();
                    if (content != null) {
                        Document document = Jsoup.parse(content);
                        Elements elements = document.getElementsByTag("img");
                        for (Element element : elements) {
                            String oldattr = element.attr("src");
                            String newattr = oldattr;
                            if (!oldattr.contains("http://")) {
                                newattr = "http://www.putaoji.com" + oldattr;
                            }
                            element.attr("src", newattr);
                            element.attr(
                                    "width",
                                    SizeUtil.px2dip(AppContext.metrics,
                                            AppContext.metrics.widthPixels - 50)
                                            + "");
                            element.attr(
                                    "height",
                                    SizeUtil.px2dip(
                                            AppContext.metrics,
                                            (AppContext.metrics.widthPixels - 50) * 2 / 3)
                                            + "");
                            images.add(newattr);
                        }
                        mWebdetail.loadDataWithBaseURL(null, document.toString(),
                                "text/html", "utf-8", null);
                    }
                }
                // 开启硬件加速
                mWebdetail.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                mWebdetail.setVisibility(View.VISIBLE);
                mWebdetail.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebdetail.clearHistory();
                    }
                }, 1000);
                break;

            case R.id.jkdp:// 酒款点评
                mLine1.setVisibility(View.INVISIBLE);
                mLine2.setVisibility(View.VISIBLE);
                mLine3.setVisibility(View.INVISIBLE);
                mWebdetail.setVisibility(View.GONE);
                lv_comment.setVisibility(View.GONE);
                web_view2.setFocusable(false);// 设置不可聚焦 去除后有聚焦现象更新画面
                web_view2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                web_view2.getSettings().setJavaScriptEnabled(true);
                web_view2.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        // Activity和Webview根据加载程度决定进度条的进度大小
                        // 当加载到100%的时候 进度条自动消失
                        setTitle("Loading...");
                        setProgress(progress * 100);
                    }
                });
                web_view2.loadUrl(mGoods.getUrl());
                web_view2.setVisibility(View.VISIBLE);

                break;
            case R.id.yhpl:// 用户评论
                mLine1.setVisibility(View.INVISIBLE);
                mLine2.setVisibility(View.INVISIBLE);
                mLine3.setVisibility(View.VISIBLE);
                mWebdetail.setVisibility(View.GONE);
                web_view2.setVisibility(View.GONE);
                lv_comment.setVisibility(View.VISIBLE);
                lv_comment.setFocusable(false);// 设置不可聚焦
                //
                getCommentList(id, page, 10);
                break;
            case R.id.img:
            case R.id.ll_shares:
                // 点评
//                        Intent intent = new Intent(ShopDetailActivity.this,
//                                WinComment.class);
//                        pid = 0;
//                        intent.putExtra("pid", pid);
//                        intent.putExtra("deal_id", id + "");
//                        startActivityForResult(intent, 201);
                break;
            case R.id.textView1:// 购买
                break;
            case R.id.tv_to_buys:
                String textmessage = tv_to_buys.getText().toString().trim();
                if (textmessage.equals("购买")) {
                    //本地购买
                } else {
                    //第三方都买
                }
                break;
            case R.id.tv_add_shop_car:
                //添加到购物车
                break;
            case R.id.ll_support:
                if (!UserInfoUtil.getLogin(this)) {
//                    UIHelper.startLoginActivity(this, false);
                    isload = true;
                    return;
                }
                if (ll_support.getTag().equals("true")) {
                    // 取消点赞
                } else {
                    // 点赞
                }
                break;
        }
    }

    /***
     * 获取商品的回复列表
     * @param topic_id
     * @param page
     * @param row
     */
    public void getCommentList(final String topic_id, final int page,
                               final int row) {
        ApiClient.getCommentList(this, topic_id, page, row, new ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                ArrayList<Comment>mlist=(ArrayList<Comment>)list;
                if (page==1){
                    list_comment=mlist;
                }else{
                    list_comment.addAll(mlist);
                }
                adapter.update(list_comment);
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
    }
}
