package com.sicao.smartwine.shop.entity;

/**
 * 商品详情
 * 
 * @author mingqi'li
 * 
 */
public class GoodsEntity extends WineEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8657689348698317005L;
	/*
	 * id：商品id name：商品名称 description：商品描述 current_price: 现价 origin_price: 原价
	 * gallery：商品图片组 brief：简介 buy_type：购买类型 4可购买 url: 酒款点评wap url
	 */
	String name;// 名字
	String current_price;// 当前价格
	String avg_point;// 评分
	String icon;// 图标
	String place;// 产地
	String description;
	String origin_price;
	String brief;
	String buy_type;
	String url;
	String [] cover;//商品图片组
	String support;
	String uid;
	boolean is_support;
	
	public boolean isIs_support() {
		return is_support;
	}
	public void setIs_support(boolean is_support) {
		this.is_support = is_support;
	}
	public String getSupport() {
		return support;
	}
	public void setSupport(String support) {
		this.support = support;
	}
	public String[] getCover() {
		return cover;
	}
	public void setCover(String[] cover) {
		this.cover = cover;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurrent_price() {
		return current_price;
	}
	public void setCurrent_price(String current_price) {
		this.current_price = current_price;
	}
	public String getAvg_point() {
		return avg_point;
	}
	public void setAvg_point(String avg_point) {
		this.avg_point = avg_point;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrigin_price() {
		return origin_price;
	}
	public void setOrigin_price(String origin_price) {
		this.origin_price = origin_price;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public String getBuy_type() {
		return buy_type;
	}
	public void setBuy_type(String buy_type) {
		this.buy_type = buy_type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	@Override
	public String toString() {
		return "GoodsEntity [ name=" + name + ", current_price="
				+ current_price + ", avg_point=" + avg_point + ", icon=" + icon
				+ ", place=" + place + ", description=" + description
				+ ", origin_price=" + origin_price + ", brief=" + brief
				+ ", buy_type=" + buy_type + ", url=" + url +",support"+support+ ", uid="+uid+"]";
	}
	

}
