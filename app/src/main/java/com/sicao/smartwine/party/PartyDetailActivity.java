package com.sicao.smartwine.party;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.shop.ShopDetailActivity;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter;
import com.sicao.smartwine.shop.adapter.SupportUserAdapter;
import com.sicao.smartwine.shop.entity.Comment;
import com.sicao.smartwine.shop.entity.MyWine;
import com.sicao.smartwine.shop.entity.Sns;
import com.sicao.smartwine.shop.entity.TopicDetail;
import com.sicao.smartwine.shop.entity.User;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListAndObjectCallBack;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.NoScrollGridView;
import com.sicao.smartwine.widget.P2RefreshListView;

import java.util.ArrayList;

/***
 * 品酒活动详情
 *
 * @author techssd
 * @version 1.0.0
 */
public class PartyDetailActivity extends BaseActivity implements View.OnClickListener {
    // 用户发起活动布局 发起活动酒款
    private LinearLayout ll_activitywines;
    // 活动时间 活动时间 活动费用 活动参加人数
    private TextView tv_activi_time, tv_activi_address, tv_activi_fee,
            tv_person_number;
    // 我的点赞列表适配器
    private SupportUserAdapter grideAdapter;
    private NoScrollGridView gv;
    // 上层传入品酒活动ID
    private String partyID = "";
    // 网络获取品酒活动详情
    private TopicDetail topicDetail;
    // webview
    private WebView webView;
    // 回复内容列表
    private P2RefreshListView lv_comment;
    // 表情和回复输入框部分
    private EditText et_content;
    private TextView btn_emo;
    private TextView btn_send;
    private View layout_emo;
    private ViewPager pager_emo;
    private boolean isCommentShown;
    // 品酒活动回复数据
    private ArrayList<Comment> list_post = new ArrayList<Comment>();
    // 品酒活动回复适配器
    private SnsCommentAdapter adapter;
    private ImageView iv_support;
    private View layout_more;
    private View ll_mjk;
    private ImageView iv_mjk;
    private TextView tv_mjk;
    private TextView btn_more;
    private Sns mjk;
    private View ll_add;
    private int page = 1;
    // 回复布局
    private View ll_comment_bar;
    private ImageView iv_supportnew;
    private TextView tv_supportnew;
    private TextView tv_commentnew;
    private LinearLayout ll_bottom2;// 三大功能键汇总
    private Handler mHandler;
    // headview
    private View headView;
    // 参加人数
    private ArrayList<User> mListUser = new ArrayList<User>();
    // 品酒活动创建时间
    private TextView tv_time;
    // 品酒活动创建人的昵称
    private TextView tv_nickname;
    // 品酒活动创建者的头像
    private ImageView iv_photo;
    // 品酒活动标题
    private TextView tv_topic_title;
    // 关注按钮
    private TextView mAddFocus;
    // 报名试饮按钮
    private TextView free_drink_btn;
    // 集品酒会参与人数
    private TextView joinPerson;
    // 活动地址时间及人数信息
    private LinearLayout addresslayout;
    // 活动酒款布局
    private RelativeLayout partylayout;
    // 报名成员下的那条线
    ImageView line1;
    //活动酒款下的那条线
    private TextView tv_wine_drive, tv_jion_drive;
    // 报名成员
    private RelativeLayout joinpersionlayout;
    // 标记是否正在发送回复
    private boolean isSending = false;
    // 是否正在点赞
    private boolean isAddSupport = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        partyID = (String) getIntent().getExtras().get("partyID");
        //获取活动详情
        ApiClient.getPartyDetail(this, partyID, new ApiCallBack() {
            @Override
            public void response(Object object) {
                try {
                    topicDetail = (TopicDetail) object;
                    // 图片堵塞加载
                    webView.getSettings().setLoadsImagesAutomatically(false);
                    webView.loadUrl(topicDetail.getContent_url());
                    // 设置点赞数
                    tv_supportnew.setText(topicDetail.getPraisecount() + "");
                    // 设置评论回复数
                    tv_commentnew.setText(topicDetail.getReplycount() + "");

                    if (null != topicDetail.getTitle()
                            && !"".equals(topicDetail.getTitle())) {

                        tv_topic_title.setVisibility(View.VISIBLE);
                        tv_topic_title.setText(topicDetail.getTitle());
                    } else {
                        tv_topic_title.setVisibility(View.GONE);
                    }
                    // 报名地址信息
                    if ("".equals(topicDetail.getAddress())
                            || null == topicDetail.getAddress()) {
                        addresslayout.setVisibility(View.GONE);
                    } else {
                        addresslayout.setVisibility(View.VISIBLE);
                        String endString = topicDetail.getEnd_time();
                        String endtime = endString.substring(
                                endString.length() - 5, endString.length());
                        tv_activi_time.setText(topicDetail.getStart_time()
                                + "~" + endtime);
                        tv_activi_address.setText(topicDetail.getAddress());// 活动地址
                        tv_activi_fee.setText("活动费用" + topicDetail.getFee()
                                + "元/人");// 活动费用
                        if (!topicDetail.getPeople().equals("")
                                && !topicDetail.getPeople().equals("0")) {
                            tv_jion_drive.setVisibility(View.VISIBLE);
                            tv_person_number.setText("活动人数"
                                    + topicDetail.getPeople() + "人");// 活动人数
                        }
                    }

                    // 设置点赞的背景
                    if (topicDetail.isIs_support()) {
                        iv_supportnew.setImageResource(R.drawable.ic_support_p);
                        iv_supportnew.setTag(true);//已点赞
                    } else {
                        iv_supportnew.setImageResource(R.drawable.ic_support_n);
                        iv_supportnew.setTag(false);//没有点过赞
                    }
                    // 更新用户信息
                    if (topicDetail.getUser() != null) {
                        tv_topic_title.setText(topicDetail.getTitle());
                        tv_nickname
                                .setText(topicDetail.getUser().getNickname());
                        AppContext.imageLoader.displayImage(topicDetail.getUser()
                                .getAvatar(), iv_photo, AppContext.gallery);
                    }
                    tv_time.setText(topicDetail.getAddtime());
                    // 增加酒款
                    if (null != topicDetail.getLink_id()
                            && !"".equals(topicDetail.getLink_id())
                            && topicDetail.getLink_id().split(",").length > 0
                            && !topicDetail.getLink_id().equals("0")) {
                        partylayout.setVisibility(View.VISIBLE);
                        tv_wine_drive.setVisibility(View.VISIBLE);
                        final String[] wineId = topicDetail.getLink_id().split(
                                ",");
                        for (int i = 0; i < wineId.length; i++) {
                            ApiClient.getGoodsDetail(PartyDetailActivity.this,
                                    wineId[i], new ApiCallBack() {
                                        @Override
                                        public void response(Object object) {
                                            View wineview = LayoutInflater
                                                    .from(PartyDetailActivity.this)
                                                    .inflate(
                                                            R.layout.freedrink_info_wine_info,
                                                            null);
                                            ImageView iv_mycellarimage = (ImageView) wineview
                                                    .findViewById(R.id.iv_mycellarimage);// 图片
                                            TextView tv_mywinename = (TextView) wineview
                                                    .findViewById(R.id.tv_mywinename);// 名字
                                            TextView textView1 = (TextView) wineview
                                                    .findViewById(R.id.textView1);// 价格
                                            final MyWine m = (MyWine) object;
                                            if (m.getImage() != null
                                                    && !m.getImage().equals("")) {
                                                AppContext.imageLoader.displayImage(
                                                        m.getImage(),
                                                        iv_mycellarimage,
                                                        AppContext.gallery);
                                            }
                                            if (m.getPrice() != null
                                                    && !m.getPrice().equals("")) {
                                                textView1.setText(m.getPrice());
                                            } else {
                                                textView1.setText("暂无价格");
                                            }
                                            tv_mywinename.setText(m.getName());
                                            ll_activitywines.addView(wineview);
                                            wineview.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(
                                                            PartyDetailActivity.this,
                                                            ShopDetailActivity.class);
                                                    intent.putExtra("windid",
                                                            m.getId());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }, null);

                        }
                    } else {
                        // 没有活动酒款
                        tv_wine_drive.setVisibility(View.GONE);
                        partylayout.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                }
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
            }
        });
    }

    @Override

    public String setTitle() {
        return getString(R.string.title_activity_party_detail_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_party_detail;
    }

    public void init() {
        headView = LayoutInflater.from(this).inflate(
                R.layout.party_headview_xml, null);
        headView.setVisibility(View.GONE);
        webView = (WebView) headView.findViewById(R.id.webView);
        iv_photo = (ImageView) headView.findViewById(R.id.iv_photo);
        tv_nickname = (TextView) headView.findViewById(R.id.tv_nickname);
        tv_time = (TextView) headView.findViewById(R.id.tv_time);
        tv_topic_title = (TextView) headView.findViewById(R.id.tv_topic_title);
        ll_activitywines = (LinearLayout) headView
                .findViewById(R.id.ll_activitywines);// 活动酒款
        tv_activi_time = (TextView) headView.findViewById(R.id.time);// 活动时间
        tv_activi_address = (TextView) headView.findViewById(R.id.address);// 活动地址
        tv_activi_fee = (TextView) headView.findViewById(R.id.money);// 活动费用
        tv_person_number = (TextView) headView.findViewById(R.id.person);// 活动人数
        joinPerson = (TextView) headView.findViewById(R.id.party_person);// 报名人数
        addresslayout = (LinearLayout) headView
                .findViewById(R.id.address_layout);// 报名人数以及地址信息等
        partylayout = (RelativeLayout) headView.findViewById(R.id.party_wine);// 活动酒款
        // /三条线
        line1 = (ImageView) headView.findViewById(R.id.tv_hanyonghzong);
        tv_wine_drive = (TextView) headView.findViewById(R.id.tv_wine_drive);
        tv_jion_drive = (TextView) headView.findViewById(R.id.tv_jion_drive);
        //
        joinpersionlayout = (RelativeLayout) headView
                .findViewById(R.id.party_person_layout);// 报名成员上方的头部
        ll_bottom2 = (LinearLayout) findViewById(R.id.ll_bottom2);
        ll_bottom2.setVisibility(View.GONE);
        iv_supportnew = (ImageView) ll_bottom2.findViewById(R.id.iv_supportnew);
        tv_supportnew = (TextView) ll_bottom2.findViewById(R.id.tv_supportnew);
        tv_commentnew = (TextView) ll_bottom2.findViewById(R.id.tv_commentnew);
        free_drink_btn = (TextView) ll_bottom2
                .findViewById(R.id.free_drink_btn);
        // 参加活动人员头像列表
        gv = (NoScrollGridView) headView.findViewById(R.id.GridView1);
        mListUser = new ArrayList<User>();
        grideAdapter = new SupportUserAdapter(this, mListUser, "");
        gv.setAdapter(grideAdapter);
        /* 设置webView */
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new JavaScriptInterface(), "Android");

		/* 初始化评论列表 */
        lv_comment = (P2RefreshListView) findViewById(R.id.lv_comment);
        lv_comment.setVisibility(View.GONE);
        lv_comment.addHeaderView(headView);
        adapter = new SnsCommentAdapter(list_post, this);
        lv_comment.setAdapter(adapter);

        // webview加载结束事件
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 关闭图片堵塞加载
                // webView.getSettings().setBlockNetworkImage(false);
                webView.getSettings().setLoadsImagesAutomatically(true);
                // 显示底部评论布局
                ll_bottom2.setVisibility(View.VISIBLE);
                // 头部显示
                headView.setVisibility(View.VISIBLE);
                // listview显示
                lv_comment.setVisibility(View.VISIBLE);
                // 获取评论列表
                webView.setVisibility(View.VISIBLE);
                // 获取参加人数据
                getPartyJoinUsers(partyID, 1, 100);
                getCommentList(partyID, page, 10);// 获取品酒活动评论列表
                // 判断是否显示参加/报名
                String cyte = topicDetail.getCtype();
                if ("3".equals(cyte)) {
                    // 用户自己发起的活动
                    if (topicDetail.getUid().equals(
                            UserInfoUtil.getUID(PartyDetailActivity.this))) {
                        // 获取时间字符串的下表
                        String start = topicDetail.getStart_time();
                        String end = topicDetail.getEnd_time();
                        start.indexOf(" ");
                        String startnew = start.substring(0, start.indexOf(" "));
                        String endnew = end.substring(0, end.indexOf(" "));
                        String endtime = end.substring(end.indexOf(" "),
                                end.length());
                        if (startnew.equals(endnew)) {
                            tv_activi_time.setText(topicDetail.getStart_time()
                                    + " 至" + endtime);
                        } else {
                            // 如果不是同一天
                            tv_activi_time.setText(topicDetail.getStart_time()
                                    + " 至" + topicDetail.getEnd_time());
                        }
                        tv_activi_address.setText(topicDetail.getAddress());
                        // 报名费用 String转成int 首先转成double
                        tv_activi_fee.setText("活动费用" + topicDetail.getFee()
                                + "元/人");
                        tv_person_number.setText("活动人数"
                                + topicDetail.getPeople() + "人");
                        free_drink_btn.setClickable(true);
                        if (!topicDetail.getList().isEmpty()) {
                            addWine();
                        }
                        free_drink_btn.setVisibility(View.VISIBLE);
                        free_drink_btn.setText("查看报名信息");
                        free_drink_btn
                                .setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Intent intent = new Intent(
//                                                PartyDetailActivity.this,
//                                                ActivitytUserActivity.class);
//                                        intent.putExtra("id",
//                                                topicDetail.getId());
//                                        intent.putExtra("uid", topicDetail
//                                                .getUser().getUid());
//                                        startActivity(intent);

                                    }
                                });

                    } else {

                        // 监测是否已经过期
                        long currentTime = System.currentTimeMillis() / 1000;
                        long deadTime = Long.parseLong(topicDetail
                                .getDeadline());
                        if (currentTime - deadTime > 0) {
                            free_drink_btn.setVisibility(View.VISIBLE);
                            free_drink_btn
                                    .setBackgroundResource(R.drawable.shape_appcolor);
                            free_drink_btn.setText("该活动已结束");
                            free_drink_btn.setTextColor(getResources()
                                    .getColor(R.color.baseColor));
                            free_drink_btn.setClickable(false);
                            return;
                        }

                        free_drink_btn.setVisibility(View.VISIBLE);
                        if (topicDetail.isIs_signup()) {
                            free_drink_btn.setText("已报名参加");
                            free_drink_btn
                                    .setBackgroundResource(R.drawable.shape_gaycolor);
                            free_drink_btn.setClickable(false);
                            free_drink_btn.setTextColor(getResources()
                                    .getColor(R.color.baseColor));
                        } else {
                            if (topicDetail.getIs_Full().equals("1")) {
                                // 已满员
                                free_drink_btn.setText("人员已满");
                                free_drink_btn
                                        .setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                            } else {
                                // 未满员
                                free_drink_btn.setText("报名参加");
                                // 最新活动
                                free_drink_btn
                                        .setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // 判断是否已经登录了
                                                if (!UserInfoUtil
                                                        .getLogin(PartyDetailActivity.this)) {
                                                    return;
                                                }
                                                //报名
//                                                actionFeelDrink(partyID);
                                                Toast.makeText(PartyDetailActivity.this, "报名", Toast.LENGTH_SHORT).show();
                                            }

                                        });
                            }
                        }
                    }
                }
            }

        });
    }

    /***
     * 获取活动参加人信息
     *
     * @param partyid 活动ID
     * @param page    页码
     * @param row     每页数据
     */
    public void getPartyJoinUsers(final String partyid, int page, int row) {
        ApiClient.getPartyJoinUsers(this, partyid, page, row, new ApiListAndObjectCallBack() {
            @Override
            public <T> void response(ArrayList<T> list, Object object) {
                //参加人员信息
                mListUser = (ArrayList<User>) list;
                //参加的总人数
                String personNumber = (String) object;
                gv.setVisibility(View.VISIBLE);
                joinpersionlayout
                        .setVisibility(View.VISIBLE);
                joinPerson.setText(personNumber + "人");
                grideAdapter.update(mListUser,
                        personNumber);
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ll_support:// 活动点赞操作
                if (null != iv_supportnew.getTag() && !isAddSupport) {
                    if ((boolean) iv_supportnew.getTag()) {
                        //用户已经点过赞了，此次操作为取消点赞的动作
                        isAddSupport = true;//标记正在处理点赞动作
                        ApiClient.ThumbsUp(this, partyID, "1", false, new ApiCallBack() {
                            @Override
                            public void response(Object object) {
                                isAddSupport = false;
                                //刷新点赞量
                                int count = Integer.parseInt(tv_supportnew.getText().toString().trim());
                                count--;
                                tv_supportnew.setText(count + "");
                                //重新标记点赞提示
                                iv_supportnew.setImageResource(R.drawable.ic_support_n);
                                iv_supportnew.setTag(false);
                            }
                        }, new ApiException() {
                            @Override
                            public void error(String error) {
                                isAddSupport = false;
                            }
                        });
                    } else {
                        //用户还没有点过赞，此次操作为点赞操作
                        ApiClient.ThumbsUp(this, partyID, "1", true, new ApiCallBack() {
                            @Override
                            public void response(Object object) {
                                isAddSupport = false;
                                //刷新点赞量
                                int count = Integer.parseInt(tv_supportnew.getText().toString().trim());
                                count++;
                                tv_supportnew.setText(count + "");
                                //重新标记点赞提示
                                iv_supportnew.setImageResource(R.drawable.ic_support_p);
                                iv_supportnew.setTag(true);

                            }
                        }, new ApiException() {
                            @Override
                            public void error(String error) {
                                isAddSupport = false;
                            }
                        });
                    }

                }
                break;
            case R.id.iv_photo:
                break;
            case R.id.ll_mjk:
                break;
            case R.id.ll_commentnew:// 评论布局
                break;
            case R.id.address:// 地图
                break;
        }
    }

    class JavaScriptInterface {
        @JavascriptInterface
        public void callJsOnAndroid(String url) {
            //
            if (url.contains("http://") || url.contains("https://")) {
//                Intent intent = new Intent(PartyDetailActivity.this,
//                        BannerDetailActivity.class);
//                intent.putExtra("url", url + "");
//                startActivity(intent);
            } else {
                Intent intent = new Intent(PartyDetailActivity.this,
                        ShopDetailActivity.class);
                intent.putExtra("windid", url);
                startActivity(intent);
            }

        }

        @JavascriptInterface
        @Override
        public String toString() {
            return "Android";
        }
    }

    /***
     * 获取活动的评论列表数据
     *
     * @param partyID 活动ID
     * @param page    数据页码数
     * @param row     数据每页显示条目数
     */
    public void getCommentList(final String partyID, final int page,
                               final int row) {
        ApiClient.getPartyDetailComments(this, partyID, page, row, new ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                lv_comment.onLoadMoreComplete();
                lv_comment.onRefreshComplete();
                ArrayList<Comment>mlist=(ArrayList<Comment>)list;
                if (page==1){
                    list_post=mlist;
                }else{
                    list_post.addAll(mlist);
                }
                adapter.update(list_post);
            }
        }, new ApiException() {
            @Override
            public void error(String error) {
                lv_comment.onLoadMoreComplete();
                lv_comment.onRefreshComplete();
            }
        });
    }

    @SuppressLint("InflateParams")
    public void addWine() {
        ll_activitywines.removeAllViews();
        int size = topicDetail.getList().size();
        for (int i = 0; i < size; i++) {
            View wineview = LayoutInflater.from(PartyDetailActivity.this)
                    .inflate(R.layout.freedrink_info_wine_info, null);
            ImageView iv_mycellarimage = (ImageView) wineview
                    .findViewById(R.id.iv_mycellarimage);// 图片
            TextView tv_mywinename = (TextView) wineview
                    .findViewById(R.id.tv_mywinename);// 名字
            TextView textView1 = (TextView) wineview
                    .findViewById(R.id.textView1);// 价格
            final MyWine m = topicDetail.getList().get(i);
            if (m.getImage() != null && !m.getImage().equals("")) {
                AppContext.imageLoader.displayImage(m.getImage(),
                        iv_mycellarimage, AppContext.gallery);
            }
            if (m.getPrice() != null && !m.getPrice().equals("")) {
                textView1.setText(m.getPrice());
            } else {
                textView1.setText("暂无价格");
            }
            tv_mywinename.setText(m.getName());
            ll_activitywines.addView(wineview);
            wineview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PartyDetailActivity.this,
                            ShopDetailActivity.class);
                    intent.putExtra("windid", m.getId());
                    startActivity(intent);
                }
            });
        }
    }
}
