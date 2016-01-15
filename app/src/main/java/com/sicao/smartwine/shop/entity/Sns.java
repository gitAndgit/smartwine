package com.sicao.smartwine.shop.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 文章摘要，即文章列表item
 * 
 * @author putaoji
 * 
 */
public class Sns implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 137787714248793813L;
	/**
	 * 
	 */
	private String id;// 文章id

	private String title;// 文章标题
	private String description;// 文章描述
	private String link_id;// 外链id,包括网址链接
	private String view;// 浏览数
	private String comment;// 评论数
	private String create_time;// 创建时间
	private String update_time;// 更新时间
	private String ctype;// 文章类型 1:热门话题2:免费试饮3:最新活动4:美酒推荐等
	private String subtitle;// 副标题
	private String showtype;// 显示类型0:宽图展示1:图文展示2:多图展示
	private String link_type;// 外链类型0:不外链1:话题2:商品3:活动10:其他
	private String cover;// 封面图链接
	private String app_cover;// 新的封面图连接
	private User user;// 用户信息
	private String praisecount;// 支持数量
	private boolean is_support;// 是否已经支持
	private boolean is_follow;// 是否已经关注
	private String cid;// 帖子类型id
	private String mark;// 标签
	private boolean fail;
	private ArrayList<String> images;
	private String deadline;// 结束时间

	// 2.1.8版本修改
	/***
	 * "address": "", "fee": "", "people": "", "start_time": "", "end_time":
	 * ""start_week
	 */
	private String address;
	private String fee;
	private String people;
	private String start_time;
	private String end_time;
	private String start_week;
	//2.1.9版本修改
	private String status;//发布活动的状态
	//活动是否结束
	private boolean isEnd;
	
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	public boolean isEnd() {
		return isEnd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStart_week(String start_week) {
		this.start_week = start_week;
	}

	public String getStart_week() {
		return start_week;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getDeadline() {
		return deadline;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	public boolean isFail() {
		return fail;
	}

	public void setFail(boolean fail) {
		this.fail = fail;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public void setApp_cover(String app_cover) {
		this.app_cover = app_cover;
	}

	public String getApp_cover() {
		return app_cover;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setPraisecount(String praisecount) {
		this.praisecount = praisecount;
	}

	public String getPraisecount() {
		return praisecount;
	}

	public void setIs_follow(boolean is_follow) {
		this.is_follow = is_follow;
	}

	public boolean isIs_follow() {
		return is_follow;
	}

	public void setIs_support(boolean is_support) {
		this.is_support = is_support;
	}

	public boolean isIs_support() {
		return is_support;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink_id() {
		return link_id;
	}

	public void setLink_id(String link_id) {
		this.link_id = link_id;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getShowtype() {
		return showtype;
	}

	public void setShowtype(String showtype) {
		this.showtype = showtype;
	}

	public String getLink_type() {
		return link_type;
	}

	public void setLink_type(String link_type) {
		this.link_type = link_type;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Sns [id=" + id + ",cid=" + cid +", title=" + title + ", description="
				+ description + ", link_id=" + link_id + ", view=" + view
				+ ", comment=" + comment + ", create_time=" + create_time
				+ ", update_time=" + update_time + ", ctype=" + ctype
				+ ", subtitle=" + subtitle + ", showtype=" + showtype
				+ ", link_type=" + link_type + ", cover=" + cover + ", user="
				+ user + ", praisecount=" + praisecount + ", is_support="
				+ is_support + ", is_follow=" + is_follow + "cid=" + cid
				+ ",app_cover=" + app_cover + ",mark=" + mark + "]";
	}

}
