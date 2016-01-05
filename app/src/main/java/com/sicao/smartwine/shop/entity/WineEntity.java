package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/***
 * 葡萄酒
 * 
 * @author mingqi'li
 * 
 */
public class WineEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6752927334686094989L;
	// id
	private String id;
	// 名称
	private String wineName;
	// 介绍
	private String detail;
	// 价格
	private String price;
	// 原价
	private String oldPrice;
	// 生产日期
	private String date;
	// 厂家
	private String manufacturers;
	// 葡萄酒图片
	private String wineImg;
	// 简介轮播图
	private String[] imgs;
	// 是否选中
	private boolean isSelect;
	// 分享
	private ShareEntity share;
	//状态(0下架1上架)
	private String state;
	//max_bought": "12",//库存
//    "buy_count": "0",//已售
	private String max_bought;
	private String buy_count;
	//购买地址
	private String buy_address;
	
	public void setBuy_address(String buy_address) {
		this.buy_address = buy_address;
	}
	public String getBuy_address() {
		return buy_address;
	}
	
	
	public String getMax_bought() {
		return max_bought;
	}
	public void setMax_bought(String max_bought) {
		this.max_bought = max_bought;
	}
	public String getBuy_count() {
		return buy_count;
	}
	public void setBuy_count(String buy_count) {
		this.buy_count = buy_count;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}
	public void setShare(ShareEntity share) {
		this.share = share;
	}

	public ShareEntity getShare() {
		return share;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}

	public String[] getImgs() {
		return imgs;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public String getId() {
		return id;
	}

	public void setWineImg(String wineImg) {
		this.wineImg = wineImg;
	}

	public String getWineImg() {
		return wineImg;
	}

	public WineEntity() {
		// TODO Auto-generated constructor stub
	}

	public WineEntity(String wineName, String detail, String price,
			String date, String manufacturers) {
		super();
		this.wineName = wineName;
		this.detail = detail;
		this.price = price;
		this.date = date;
		this.manufacturers = manufacturers;
	}

	public String getWineName() {
		return wineName;
	}

	public void setWineName(String wineName) {
		this.wineName = wineName;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(String manufacturers) {
		this.manufacturers = manufacturers;
	}

	@Override
	public String toString() {
		return "WineEntity [wineName=" + wineName + ", detail=" + detail
				+ ", price=" + price + ", date=" + date + ", manufacturers="
				+ manufacturers + "]";
	}

}
