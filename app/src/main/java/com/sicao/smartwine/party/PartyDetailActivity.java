package com.sicao.smartwine.party;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.party.adapter.EmoViewPagerAdapter;
import com.sicao.smartwine.party.adapter.EmoteAdapter;
import com.sicao.smartwine.shop.ShopDetailActivity;
import com.sicao.smartwine.shop.adapter.SnsCommentAdapter;
import com.sicao.smartwine.shop.adapter.SupportUserAdapter;
import com.sicao.smartwine.shop.entity.Comment;
import com.sicao.smartwine.shop.entity.CommentList;
import com.sicao.smartwine.shop.entity.MyWine;
import com.sicao.smartwine.shop.entity.Sns;
import com.sicao.smartwine.shop.entity.TopicDetail;
import com.sicao.smartwine.shop.entity.User;
import com.sicao.smartwine.util.ApiCallBack;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListAndObjectCallBack;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.util.BMapUtil;
import com.sicao.smartwine.util.QiNiuUpLoadQueue;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.BottomPopupWindow;
import com.sicao.smartwine.widget.CropImage;
import com.sicao.smartwine.widget.NoScrollGridView;
import com.sicao.smartwine.widget.P2RefreshListView;
import com.sicao.smartwine.widget.emoj.FaceText;
import com.sicao.smartwine.widget.emoj.FaceTextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 * 品酒活动详情
 *
 * @author techssd
 * @version 1.0.0
 */
public class PartyDetailActivity extends BaseActivity implements View.OnClickListener {
    // 用户发起活动布局 发起活动酒款
    LinearLayout ll_activitywines;
    // 活动时间 活动时间 活动费用 活动参加人数
    TextView tv_activi_time, tv_activi_address, tv_activi_fee,
            tv_person_number;
    // 我的点赞列表适配器
    SupportUserAdapter grideAdapter;
    NoScrollGridView gv;
    // 上层传入品酒活动ID
    String partyID = "";
    // 网络获取品酒活动详情
    TopicDetail topicDetail;
    // webview
    WebView webView;
    // 回复内容列表
    P2RefreshListView lv_comment;
    // 表情和回复输入框部分
    EditText et_content;
    //打开表情的按钮
    TextView btn_emo;
    //发送按钮
    TextView btn_send;
    View layout_emo;
    ViewPager pager_emo;
    boolean isCommentShown;
    // 品酒活动回复数据
    ArrayList<Comment> list_post = new ArrayList<Comment>();
    // 品酒活动回复适配器
    SnsCommentAdapter adapter;
    ImageView iv_support;
    View layout_more;
    View ll_mjk;
    ImageView iv_mjk;
    TextView tv_mjk;
    TextView btn_more;
    Sns mjk;
    View ll_add;
    int page = 1;
    // 回复布局
    View ll_comment_bar;
    // 表情分页图标
    ImageView mPoint1, mPoint2;
    // 标签数据
    List<FaceText> emos;
    ImageView iv_supportnew;
    TextView tv_supportnew;
    TextView tv_commentnew;
    LinearLayout ll_bottom2;// 三大功能键汇总
    Handler mHandler;
    // headview
    View headView;
    // 参加人数
    ArrayList<User> mListUser = new ArrayList<User>();
    // 品酒活动创建时间
    TextView tv_time;
    // 品酒活动创建人的昵称
    TextView tv_nickname;
    // 品酒活动创建者的头像
    ImageView iv_photo;
    // 品酒活动标题
    TextView tv_topic_title;
    // 关注按钮
    TextView mAddFocus;
    // 报名试饮按钮
    TextView free_drink_btn;
    // 集品酒会参与人数
    TextView joinPerson;
    // 活动地址时间及人数信息
    LinearLayout addresslayout;
    // 活动酒款布局
    RelativeLayout partylayout;
    // 报名成员下的那条线
    ImageView line1;
    //活动酒款下的那条线
    TextView tv_wine_drive, tv_jion_drive;
    // 报名成员
    RelativeLayout joinpersionlayout;
    // 标记是否正在发送回复
    boolean isSending = false;
    // 是否正在点赞
    boolean isAddSupport = false;

    // 用来控制是一级回复还是二级回复
    int pid = 0;// 默认是一级回复 0
    // 用来控制回复中插入数据的类型
    int type = 0;// 默认是纯文本 0
    String imgId = "";// 图片Id、文章id
    // 待上传的图片路径
    String mImagePath = "";

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
        //回复部分布局
        ll_comment_bar = findViewById(R.id.ll_comment_bar);
        // 表情分页指示点
        mPoint1 = (ImageView) ll_comment_bar.findViewById(R.id.page1);
        mPoint2 = (ImageView) ll_comment_bar.findViewById(R.id.page2);
        initEmoj();
        layout_emo = findViewById(R.id.layout_emo);
        layout_more = findViewById(R.id.layout_more);
        ll_add = findViewById(R.id.ll_add);
        ll_mjk = findViewById(R.id.ll_mjk);
        ll_mjk.setOnClickListener(this);
        btn_emo = (TextView) findViewById(R.id.action_emo);
        btn_send = (TextView) findViewById(R.id.btn_send);
        btn_more = (TextView) findViewById(R.id.btn_more);
        btn_emo.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_more.setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);
        et_content.setOnClickListener(this);
        iv_mjk = (ImageView) findViewById(R.id.iv_mjk);
        /***
         * 快速回复入口
         */
        adapter.setCommentItemClickListener(new SnsCommentAdapter.CommentItemClickListener() {
            @Override
            public void onClick(final int index, final Comment comment) {
                if (comment.getUser().getUid()
                        .equals(UserInfoUtil.getUID(PartyDetailActivity.this))) {
                    // 一层回复删除事件
                    final BottomPopupWindow removeWindow = new BottomPopupWindow(PartyDetailActivity.this);
                    removeWindow.update(new String[]{"删除评论", "取消"});
                    removeWindow.showAtLocation(content, Gravity.BOTTOM,
                            0, 0);
                    removeWindow.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                        @Override
                        public void onClick(String value) {
                            if ("删除评论".equals(value)) {
                                String id = comment.getId();
                                deleteComment(id, true);// 删除该评论

                            }
                            removeWindow.dismiss();
                        }
                    });
                    return;
                }
                isCommentShown = true;
                // 二层回复类型 0，纯文本
                type = 0;
                // 二层回复的上层回复id
                pid = Integer.parseInt(comment.getId());
                showSoftInputView();
                btn_more.setVisibility(View.GONE);
                layout_emo.setVisibility(View.GONE);
                et_content.setFocusable(true);
                et_content.setFocusableInTouchMode(true);
                et_content.requestFocus();
                ll_comment_bar.setVisibility(View.VISIBLE);
                ll_bottom2.setVisibility(View.GONE);
                et_content.setHint("回复:" + comment.getUser().getNickname());
            }
        });
        // 二层回复点击事件
        adapter.setChildItemChildClickListener(new SnsCommentAdapter.CommentItemChildClickListener() {
            @Override
            public void onClick(int index, final CommentList comment) {
                if (comment.getUida().equals(
                        UserInfoUtil.getUID(PartyDetailActivity.this))) {
                    // 二层回复删除事件
                    final BottomPopupWindow removeWindow = new BottomPopupWindow(PartyDetailActivity.this);
                    removeWindow.update(new String[]{"删除评论", "取消"});
                    removeWindow.showAtLocation(content, Gravity.BOTTOM,
                            0, 0);
                    removeWindow.setMenuItemClickListener(new BottomPopupWindow.MenuItemClickListener() {
                        @Override
                        public void onClick(String value) {
                            if ("删除评论".equals(value)) {
                                int id = comment.getCommentListid();
                                deleteComment(id + "", false);// 删除该评论
                            }
                            removeWindow.dismiss();
                        }
                    });
                    return;
                }
                Log.i("huahua", "卧槽");
                isCommentShown = true;
                // 二层回复类型 0，纯文本
                type = 0;
                // 二层回复的上层回复id
                pid = comment.getCommentListid();
                showSoftInputView();
                btn_more.setVisibility(View.GONE);
                layout_emo.setVisibility(View.GONE);
                et_content.setFocusable(true);
                et_content.setFocusableInTouchMode(true);
                et_content.requestFocus();
                ll_comment_bar.setVisibility(View.VISIBLE);
                ll_bottom2.setVisibility(View.GONE);
                et_content.setHint("回复:" + comment.getUnamea());
            }
        });

        // 刷新
        lv_comment.setOnRefreshListener(new P2RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getCommentList(partyID, page, 10);
            }
        });
        // 加载更多
        lv_comment.setOnLoadListener(new P2RefreshListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getCommentList(partyID, page, 10);
            }
        });
    }

    /**
     * 更新表情分页的指示点
     *
     * @param point
     */
    public void update(int point) {
        if (0 == point) {
            mPoint2.setImageResource(R.drawable.light_blue_point);
            mPoint1.setImageResource(R.drawable.blue_point);
        } else if (1 == point) {
            mPoint1.setImageResource(R.drawable.light_blue_point);
            mPoint2.setImageResource(R.drawable.blue_point);

        }

    }

    /***
     * 初始化Emoj布局
     */
    private void initEmoj() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
        pager_emo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                update(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /***
     * Emoj的GridView布局
     *
     * @param i
     * @return
     */
    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (et_content != null && !TextUtils.isEmpty(key)) {
                        int start = et_content.getSelectionStart();
                        CharSequence content = et_content.getText().insert(
                                start, key);
                        et_content.setText(content);
                        // 定位光标位置
                        CharSequence info = et_content.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
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
            case R.id.upload_img:
                // 标记一级评论插入数据的类型
                type = -1;// -1 图片
                //
                Intent intentPhoto = new Intent(PartyDetailActivity.this,
                        CropImage.class);
                intentPhoto.putExtra(CropImage.outputX, 500);
                intentPhoto.putExtra(CropImage.outputY, 500);
                intentPhoto.putExtra(CropImage.aspectX, 1);
                intentPhoto.putExtra(CropImage.aspectY, 1);
                intentPhoto.putExtra("ifTailorFlag", false);// 标志位启动裁剪
                startActivityForResult(intentPhoto, 103);
                break;
            case R.id.ll_commentnew:// 评论按钮
                showSoftInputView();//开启软键盘
                ll_comment_bar.setVisibility(View.VISIBLE);//显示评论框输入栏
                ll_bottom2.setVisibility(View.GONE);//隐藏顶部点赞数目栏

                break;
            case R.id.action_emo:// emoj按钮
                if (layout_emo.getVisibility() == View.VISIBLE) {
                    layout_emo.setVisibility(View.GONE);
                    et_content.setFocusable(true);
                    et_content.setFocusableInTouchMode(true);
                    et_content.requestFocus();
                    ll_comment_bar.setVisibility(View.VISIBLE);
                    ll_bottom2.setVisibility(View.GONE);
                    btn_emo.setBackgroundResource(R.drawable.jiahaotwo);
                    showSoftInputView();
                } else {
                    layout_more.setVisibility(View.GONE);
                    ll_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.VISIBLE);
                    btn_emo.setBackgroundResource(R.drawable.newkeyboad);
                    hideSoftInputView();
                }
                break;
            case R.id.btn_more:// 更多按钮
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_more.setVisibility(View.GONE);
                    ll_add.setVisibility(View.GONE);
                    et_content.setFocusable(true);
                    et_content.setFocusableInTouchMode(true);
                    et_content.requestFocus();
                    ll_comment_bar.setVisibility(View.VISIBLE);
                    ll_bottom2.setVisibility(View.GONE);
                    showSoftInputView();
                } else {
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.VISIBLE);
                    ll_add.setVisibility(View.VISIBLE);
                    btn_emo.setBackgroundResource(R.drawable.jiahaotwo);
                    hideSoftInputView();
                }
                break;

            case R.id.btn_send:// 提交评论
                // 1,上传图片
                if (!"".equals(mImagePath)) {
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
                                sendComment(
                                        list_post.size() - 1,
                                        partyID,
                                        et_content.getText().toString().trim() + "",
                                        imgId, type, pid, result.get(0));
                                         /* 关闭底部 */
                                // ll_noname.setVisibility(View.VISIBLE);
                                layout_emo.setVisibility(View.GONE);
                                layout_more.setVisibility(View.GONE);
                                hideSoftInputView();
                                isCommentShown = false;
                            } else {
                                Toast.makeText(PartyDetailActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    if (type == 0) {
                        imgId = "";
                        if ("".equals(et_content.getText().toString().trim())) {
                            Toast.makeText(PartyDetailActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (pid == 0) {
                        // 一级回复

                        sendComment(list_post.size() - 1, partyID, et_content
                                .getText().toString().trim()
                                + "", imgId, type, pid, "");
                    } else {
                        // 二级回复
                        sendComment(list_post.size() - 1, partyID, et_content.getText()
                                .toString().trim()
                                + "", imgId, type, pid, "");
                    }
                }
                break;
            case R.id.address:// 地图
                break;
            case R.id.ll_share:// 分享按钮
                AppContext.share(PartyDetailActivity.this,
                        topicDetail.getShare(), null);
                break;
        }
    }

    public void sendComment(final int position, final String partyID,
                            final String content, final String attach_ids, final int ctype,
                            final int pidS, final String img_paths) {
        if (!isSending) {//正在发送回复
            isSending = true;
            ApiClient.sendCommentFromPartyDetail(this, partyID, pidS + "", ctype + "", attach_ids, img_paths, content, new ApiCallBack() {
                @Override
                public void response(Object object) {
                    isSending = false;
                    ll_comment_bar.setVisibility(View.GONE);
                    ll_bottom2.setVisibility(View.VISIBLE);
                    hideSoftInputView();
                    isCommentShown = false;
                    type = 0;// 格式复位---纯文本
                    imgId = "0";// 图片Id复位无图模式
                    pid = 0;// 被评论的评论id复位
                    et_content.setText("");
                    et_content.setHint("回复:" + "文章");
                    ll_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                    //刷新评论，并滚动到当前的位置
                    page = 1;
                    getCommentList(partyID, page, 10);
                    lv_comment.setSelection(position);
                    //评论数增加
                    int count = Integer.parseInt(tv_commentnew.getText().toString().trim());
                    count++;
                    tv_commentnew.setText(count + "");
                }
            }, new ApiException() {
                @Override
                public void error(String error) {
                    isSending = false;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
//            mjk = (Sns) data.getSerializableExtra("sns");
//            ll_add.setVisibility(View.GONE);
//            ll_mjk.setVisibility(View.VISIBLE);
//            UIHelper.imageLoader.displayImage(mjk.getCover(), iv_mjk,
//                    UIHelper.banneroption);
//            tv_mjk.setText("" + mjk.getTitle());
//        } else
        if (resultCode == RESULT_OK && data != null
                && requestCode == 103) {
            // 图片信息返回
            Boolean have_image = data.getBooleanExtra(CropImage.have_image,
                    false);
            if (have_image) {// 取出图片
                mImagePath = data.getStringExtra(CropImage.image_path);
                // 1，将图片显示出来
                ll_add.setVisibility(View.GONE);
                ll_mjk.setVisibility(View.VISIBLE);
                Bitmap bm = BMapUtil.getImageThumbnail(mImagePath,
                        AppContext.metrics.widthPixels / 4,
                        AppContext.metrics.widthPixels / 4);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        bm.getWidth(), bm.getHeight());
                iv_mjk.setLayoutParams(params);
                iv_mjk.setImageBitmap(bm);
                // 2，上传该图片-------移交【发送】按钮处理
                // 3，获取图片在服务器上的地址--------移交【发送】按钮处理
            }
        }
//        else if (resultCode == RESULT_OK && data != null
//                && requestCode == Constants.TOPIC_JOIN_FEEL_DRINK_TO_BINGPHONE) {
//            // 帖子页面参加免费试饮动作引发的绑定手机逻辑,使用该字符串标记动作类型----执行参加免费试饮
//            showProgress("正在提交报名信息...");
//            ApiClient.actionFeelDrink(PartyDetailActivity.this,
//                    topicDetail.getId(), new ApiCallBack() {
//                        @Override
//                        public void response(Object object) {
//                            dismissProgress();
//                            // 参加成功-----执行分享
//                            UIHelper.share(PartyDetailActivity.this,
//                                    topicDetail.getShare(), null);
//                        }
//                    }, new ApiException() {
//                        @Override
//                        public void error(String error) {
//                            dismissProgress();
//                        }
//                    });
//        } else if (resultCode == RESULT_OK
//                && data != null
//                && requestCode == Constants.TOPIC_JOIN_FEEL_DRINK_TO_EDIT_USERINFO) {
//            // 活动以及个人地址信息参加完毕----执行分享
//            UIHelper.share(PartyDetailActivity.this, topicDetail.getShare(),
//                    null);
//
//        }
        super.onActivityResult(requestCode, resultCode, data);
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
                ArrayList<Comment> mlist = (ArrayList<Comment>) list;
                if (page == 1) {
                    list_post = mlist;
                } else {
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

    // 返回键的监听
    @Override
    public void onBackPressed() {
        hideSoftInputView();
        if (webView.canGoBack()) {// 如果webview可以返回，则处理webview的返回事件
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }


    // 显示软键盘
    public void showSoftInputView() {
        try {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .showSoftInput(et_content, 0);
        } catch (Exception e) {
        }

    }

    /**
     * 隐藏软键盘 hideSoftInputView
     */
    public void hideSoftInputView() {

        try {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(et_content.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }

    }

    // 调用回复删除的方法

    /***
     * 删除评论
     *
     * @param id    评论ID
     * @param isOne 是否是一层回复
     */
    public void deleteComment(String id, final boolean isOne) {
        ApiClient.deleteComment(this, id, "1", new ApiCallBack() {
            @Override
            public void response(Object object) {
                if (isOne) {
                    //评论量减少
                    int count = Integer.parseInt(tv_commentnew.getText().toString().trim());
                    count--;
                    tv_commentnew.setText(count + "");
                }
                page = 1;
                getCommentList(partyID, page, 10);
            }
        }, null);
    }
}
