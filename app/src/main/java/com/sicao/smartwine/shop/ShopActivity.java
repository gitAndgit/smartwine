package com.sicao.smartwine.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.shop.adapter.WineFilterAdapter;
import com.sicao.smartwine.shop.adapter.WineLibraryAdpter;
import com.sicao.smartwine.shop.adapter.WineLibraryAdpter.IsHit;
import com.sicao.smartwine.shop.entity.ClassTypeEntity;
import com.sicao.smartwine.shop.entity.WineEntity;
import com.sicao.smartwine.util.ApiException;
import com.sicao.smartwine.util.ApiListCallBack;
import com.sicao.smartwine.widget.NoScrollGridView;
import com.sicao.smartwine.widget.P2RefreshListView;
import com.sicao.smartwine.widget.P2RefreshListView.OnLoadMoreListener;
import com.sicao.smartwine.widget.P2RefreshListView.OnRefreshListener;

import java.util.ArrayList;

/***
 * 美酒商城下级美酒列表页面
 *
 * @author techssd
 * @version 1.0.0
 */
public class ShopActivity extends BaseActivity {
    private WineLibraryAdpter mLibraryAdpter;// 我的适配器
    private ArrayList<WineEntity> mlist;// 底部适配器
    private WineFilterAdapter mfilteradpter;// 我选项适配器
    private ArrayList<ClassTypeEntity> mpostlist;// 选项按钮

    // 对象 场合 价格 品类 产地
    private ImageView class1_1, class2_2, class3_3;
    private NoScrollGridView gd_item;
    private P2RefreshListView lv_wine_library;
    private int page = 1;
    // 标记顶部选项下标
    private int sub = 0;
    private String attr = "";

    // 筛选菜单是否已经打开
    private boolean menuIsopen = false;
    // 大类别
    private TextView class1, class2, class3;
    // 场合/对象
    private String name = "";
    // 场合/对象id
    private String id = "";

    // 分类信息priceScope= 价格区间，originArea=产地，wineType =品类
    private String priceScope = "", wineType = "", originArea = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getIntent().getExtras().getString("name");
        id = getIntent().getExtras().getString("id");
        init();
    }

    @Override
    public String setTitle() {
        return getString(R.string.title_activity_shop_info);
    }

    @Override
    protected int setView() {
        return R.layout.activity_shop;
    }

    private void init() {
        gd_item = (NoScrollGridView) findViewById(R.id.gd_item);
        //
        class1_1 = (ImageView) findViewById(R.id.class1_1);
        class2_2 = (ImageView) findViewById(R.id.class2_2);
        class3_3 = (ImageView) findViewById(R.id.class3_3);
        lv_wine_library = (P2RefreshListView) findViewById(R.id.lv_wine_library);// 加载酒款
        class1 = (TextView) findViewById(R.id.class1);
        class2 = (TextView) findViewById(R.id.class2);
        class3 = (TextView) findViewById(R.id.class3);
        mlist = new ArrayList<WineEntity>();
        mLibraryAdpter = new WineLibraryAdpter(this, mlist);
        lv_wine_library.setAdapter(mLibraryAdpter);
        lv_wine_library.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                // priceScope= 价格区间，originArea=产地，wineType =品类
                getGoodsList(page, 10, "", attr, priceScope, originArea,
                        wineType);
            }
        });
        //
        mpostlist = new ArrayList<ClassTypeEntity>();
        mfilteradpter = new WineFilterAdapter(this, new ClassTypeEntity[]{});
        gd_item.setAdapter(mfilteradpter);
        getClassify("1", getIntent().getExtras().getString("name"));
        getGoodsList(1, 10, "", attr, priceScope, originArea, wineType);

        // 适配器点击事件
        mfilteradpter.setIsOnClick(new WineFilterAdapter.IsOnClick() {
            @Override
            public void setMyOnclick(ClassTypeEntity mDeal, int index) {
                mlist.clear();
                if (!mpostlist.isEmpty()) {// priceScope=
                    // 价格区间，originArea=产地，wineType =品类
                    for (int i = 0; i < mpostlist.get(sub).getSubs().length; i++) {
                        mpostlist.get(sub).getSubs()[i].setSelect(false);
                    }
                    mpostlist.get(sub).getSubs()[index].setSelect(true);
                    mfilteradpter.notifyDataSetChanged();
                    if (sub == 0) {
                        class1.setText(mpostlist.get(sub).getSubs()[index]
                                .getName());
                        class1.setTextColor(getResources().getColor(
                                R.color.black));
                        priceScope = mpostlist.get(sub).getSubs()[index]
                                .getName();
                    } else if (sub == 1) {
                        class2.setText(mpostlist.get(sub).getSubs()[index]
                                .getName());
                        class2.setTextColor(getResources().getColor(
                                R.color.black));
                        wineType = mpostlist.get(sub).getSubs()[index]
                                .getName();
                    } else if (sub == 2) {
                        class3.setText(mpostlist.get(sub).getSubs()[index]
                                .getName());
                        class3.setTextColor(getResources().getColor(
                                R.color.black));
                        originArea = mpostlist.get(sub).getSubs()[index]
                                .getName();
                    }
                    // 筛选按钮
                    getGoodsList(1, 10, sub + "," + index, attr, priceScope,
                            originArea, wineType);
                    gd_item.setVisibility(View.GONE);
                    menuIsopen = false;
                    class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                    class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                    class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);

                }
            }
        });
        mLibraryAdpter.setIsHit(new IsHit() {
            @Override
            public void setMyHitOcclick(WineEntity entity) {
                gd_item.setVisibility(View.GONE);
                menuIsopen = false;
                if (sub == 0 || sub == 1 || sub == 2) {
                    class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                    class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                    class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                }
                Intent intent = new Intent(ShopActivity.this,
                        ShopDetailActivity.class);
                intent.putExtra("windid", entity.getId());
                intent.putExtra("url", entity.getBuy_address());
                startActivity(intent);
            }
        });
        lv_wine_library.setOnLoadListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mlist.size() % 10 != 0) {// 说明已经到最后一页了
                    Toast.makeText(ShopActivity.this, "暂无更多数据",
                            Toast.LENGTH_SHORT).show();
                    lv_wine_library.onLoadMoreComplete();
                } else {// 加载更多
                    page = mlist.size() / 10 + 1;
                    getGoodsList(page, 10, "", attr, priceScope, originArea,
                            wineType);
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_object:
                try {
                    if (sub != 0) {// 没处于该选项
                        sub = 0;
                        menuIsopen = true;
                        class1.setTextColor(getResources().getColor(
                                R.color.baseColor));
                        class2.setTextColor(getResources().getColor(R.color.black));
                        class3.setTextColor(getResources().getColor(R.color.black));
                        class1_1.setImageResource(R.drawable.talent_wine_list_icon_down);
                        class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                        class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                        mfilteradpter.upDataAdapter(mpostlist.get(sub).getSubs());
                        gd_item.setVisibility(View.VISIBLE);
                    } else {
                        if (menuIsopen) {// 已经打开了
                            menuIsopen = false;// 标志关闭
                            gd_item.setVisibility(View.GONE);// 关闭
                            class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class1.setTextColor(getResources().getColor(
                                    R.color.black));
                            class2.setTextColor(getResources().getColor(
                                    R.color.black));
                            class3.setTextColor(getResources().getColor(
                                    R.color.black));
                        } else {
                            menuIsopen = true;
                            class1_1.setImageResource(R.drawable.talent_wine_list_icon_down);
                            class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                            mfilteradpter.upDataAdapter(mpostlist.get(sub)
                                    .getSubs());
                            gd_item.setVisibility(View.VISIBLE);
                            class1.setTextColor(getResources().getColor(
                                    R.color.baseColor));
                            class2.setTextColor(getResources().getColor(
                                    R.color.black));
                            class3.setTextColor(getResources().getColor(
                                    R.color.black));
                        }
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.ll_ocasion:
                try {
                    if (sub != 1) {
                        sub = 1;
                        menuIsopen = true;
                        class2.setTextColor(getResources().getColor(
                                R.color.baseColor));
                        class1.setTextColor(getResources().getColor(R.color.black));
                        class3.setTextColor(getResources().getColor(R.color.black));
                        class2_2.setImageResource(R.drawable.talent_wine_list_icon_down);
                        class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                        class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                        mfilteradpter.upDataAdapter(mpostlist.get(sub).getSubs());
                        gd_item.setVisibility(View.VISIBLE);
                    } else {
                        if (menuIsopen) {
                            class2.setTextColor(getResources().getColor(
                                    R.color.black));
                            class1.setTextColor(getResources().getColor(
                                    R.color.black));
                            class3.setTextColor(getResources().getColor(
                                    R.color.black));
                            class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                            gd_item.setVisibility(View.GONE);
                            menuIsopen = false;
                        } else {
                            menuIsopen = true;
                            class2.setTextColor(getResources().getColor(
                                    R.color.baseColor));
                            class1.setTextColor(getResources().getColor(
                                    R.color.black));
                            class3.setTextColor(getResources().getColor(
                                    R.color.black));
                            class2_2.setImageResource(R.drawable.talent_wine_list_icon_down);
                            class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                            mfilteradpter.upDataAdapter(mpostlist.get(sub)
                                    .getSubs());
                            gd_item.setVisibility(View.VISIBLE);

                        }
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.ll_price:
                try {
                    if (sub != 2) {
                        sub = 2;
                        menuIsopen = true;
                        class2.setTextColor(getResources().getColor(R.color.black));
                        class1.setTextColor(getResources().getColor(R.color.black));
                        class3.setTextColor(getResources().getColor(
                                R.color.baseColor));
                        class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                        class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                        class3_3.setImageResource(R.drawable.talent_wine_list_icon_down);
                        mfilteradpter.upDataAdapter(mpostlist.get(sub).getSubs());
                        gd_item.setVisibility(View.VISIBLE);
                    } else {
                        if (menuIsopen) {
                            class2.setTextColor(getResources().getColor(
                                    R.color.black));
                            class1.setTextColor(getResources().getColor(
                                    R.color.black));
                            class3.setTextColor(getResources().getColor(
                                    R.color.black));
                            class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class3_3.setImageResource(R.drawable.talent_wine_list_icon_up);
                            gd_item.setVisibility(View.GONE);
                            menuIsopen = false;
                        } else {
                            menuIsopen = true;
                            class2.setTextColor(getResources().getColor(
                                    R.color.black));
                            class1.setTextColor(getResources().getColor(
                                    R.color.black));
                            class3.setTextColor(getResources().getColor(
                                    R.color.baseColor));
                            class3_3.setImageResource(R.drawable.talent_wine_list_icon_down);
                            class2_2.setImageResource(R.drawable.talent_wine_list_icon_up);
                            class1_1.setImageResource(R.drawable.talent_wine_list_icon_up);
                            mfilteradpter.upDataAdapter(mpostlist.get(sub)
                                    .getSubs());
                            gd_item.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }

    // 索搜酒 页码 上下文 pos0默认值 1美酒库 商品名字 分类 classify分类id组
	/*
	 * priceScope= 价格区间，originArea=产地，wineType =品类
	 */
    public void getGoodsList(final int page, int row, String allid,
                             String attr, String priceScope, String originArea, String wineType) {
        ApiClient.getShopList(page, row, this, "1", name, "0", id, attr,
                priceScope, originArea, wineType,new ApiListCallBack() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T> void response(ArrayList<T> list) {
                        lv_wine_library.onRefreshComplete();
                        lv_wine_library.onLoadMoreComplete();
                        ArrayList<WineEntity> temp = (ArrayList<WineEntity>) list;
                        if (temp.size() > 0) {
                            if (page == 1) {
                                mlist = temp;
                            } else {
                                mlist.addAll(temp);
                            }
                            mLibraryAdpter.upDataAdapter(mlist);
                        } else {
                            mLibraryAdpter.upDataAdapter(mlist);
                        }
                    }
                }, null);
    }
    // 商品筛选接口
    public void getClassify(String name, String sub) {
        ApiClient.getClassify(this, name, sub, new ApiListCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                if (!list.isEmpty()) {
                    mpostlist = (ArrayList<ClassTypeEntity> )list;
                    mfilteradpter.upDataAdapter(mpostlist.get(0)
                            .getSubs());
                } else {
                    // 设置没有索搜到东西
                }
            }
        }, new ApiException() {
            @Override
            public void error(String error) {

            }
        });
    }

}
