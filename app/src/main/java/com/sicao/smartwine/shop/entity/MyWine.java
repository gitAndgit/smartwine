package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/****
 * 我的酒窖构造体
 * 
 * @author tech
 */
public class MyWine implements Serializable{

	/**
	 * 序列码
	 */
	private static final long serialVersionUID = 1L;
	private String name;// 酒款名字
	private String image;// 酒款图片
	private String type;// 酒款产地
	private String appraise;// 酒款评价
	private String id;//葡萄酒ID
	
	private boolean isonClick;//默认为没有选中状态
	private boolean isediting;//是否编辑状态  默认为false/当酒款大于9是锁死 true代表锁死
	private String bid;//收藏的id
	private boolean isMycellect;//是否是收藏列表
	//v3新增
	private String price;
	private boolean isaddwine=false;//是否是从添加列表进入  默认不是
	//为适配帖子详情添加字段
	private String buy_type;//购买类型

	
	

	public String getBuy_type() {
		return buy_type;
	}

	public void setBuy_type(String buy_type) {
		this.buy_type = buy_type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public boolean isIsaddwine() {
		return isaddwine;
	}

	public void setIsaddwine(boolean isaddwine) {
		this.isaddwine = isaddwine;
	}

	public boolean isMycellect() {
		return isMycellect;
	}

	public void setMycellect(boolean isMycellect) {
		this.isMycellect = isMycellect;
	}

	public boolean isIsonClick() {
		return isonClick;
	}

	public void setIsonClick(boolean isonClick) {
		this.isonClick = isonClick;
	}

	public boolean isIsediting() {
		return isediting;
	}

	public void setIsediting(boolean isediting) {
		this.isediting = isediting;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAppraise() {
		return appraise;
	}

	public void setAppraise(String appraise) {
		this.appraise = appraise;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MyWine [name=" + name + ", image=" + image + ", type=" + type
				+ ", appraise=" + appraise + ", id=" + id + ", isonClick="
				+ isonClick + ", isediting=" + isediting + ", bid=" + bid
				+ ", isMycellect=" + isMycellect + ", price=" + price
				+ ", isaddwine=" + isaddwine + ", buy_type=" + buy_type
				+ "]";
	}
}
