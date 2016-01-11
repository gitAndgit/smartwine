package com.sicao.smartwine.shop.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 文章详情里边的评论
 * 
 * @author putaoji
 * 
 */
public class Comment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2971710826230493102L;
	private String id;// 评论id
	private String content;// 评论内容
	private String create_time;// 评论时间
	private User user;// 评论用户
	private Sns mjk;//文章
	private String imgUrl;//上传图片的地址
	private ArrayList<CommentList>mLists;//二级回复列表数据
    //该条回复的类型（第一层回复）.-1属于纯图片加文字，0属于纯文字，1属于美酒刊，2属于话题，3属于美酒推荐
	private int type;
	public String title;//美酒刊简介
	private String desc;//美酒刊内容
	private String aid;//美酒刊id
	private String support;//点赞数
	private boolean isSupport;//该一级评论是否已点赞
	private TopicDetail topic;//帖子
	//点评星级
	private String star;
	//是否是商品的点评
	private boolean isGoods;
	
	
	public void setGoods(boolean isGoods) {
		this.isGoods = isGoods;
	}
	public boolean isGoods() {
		return isGoods;
	}
	
	public void setStar(String star) {
		this.star = star;
	}
	public String getStar() {
		return star;
	}
	
	public void setTopic(TopicDetail topic) {
		this.topic = topic;
	}
	public TopicDetail getTopic() {
		return topic;
	}
	
	public String getSupport() {
		return support;
	}
	public void setSupport(String support) {
		this.support = support;
	}
	public boolean isSupport() {
		return isSupport;
	}
	public void setSupport(boolean isSupport) {
		this.isSupport = isSupport;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getAid() {
		return aid;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	
	public void setmLists(ArrayList<CommentList> mLists) {
		this.mLists = mLists;
	}
	public ArrayList<CommentList> getmLists() {
		return mLists;
	}
	
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getImgUrl() {
		return imgUrl;
	}

	public Sns getMjk() {
		return mjk;
	}

	public void setMjk(Sns mjk) {
		this.mjk = mjk;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "Comment [id=" + id + ", content=" + content + ", create_time="
				+ create_time + ", user=" + user + ", mjk=" + mjk + ", imgUrl="
				+ imgUrl + ", mLists=" + mLists + "]";
	}

	
}
