package com.sicao.smartwine.shop.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 帖子详情
 * 
 * @author putaoji "topic": { "id": "4028", "gid": "1", "uid": "2", "title":
 *         "烧烤+葡萄酒", "viewcount": "0", "replycount": "10", "addtime": "今天11:26",
 *         "user": { "nickname": "小萄", "avatar":
 *         "http://www.putaoji.com/Uploads/Avatar/2014-10-14/543cd08197858-05505543_64_64.jpg"
 *         }, "content": "
 *         <p>
 *         <span></span><span style=\"line-height:1.5;\"></span><span style=\
 *         "line-height:1.5;\">冬天到，吃烧烤暖心呀，但一次完美的烧烤怎能没有美酒相伴呢?本文用一张图向大家展示5种比较流行的葡萄酒如何与烧烤美
 *         食 相 互 搭 配 。 < / s p a n >
 *         </p>
 *         <p>
 *         <span style=\"line-height:1.5;\"><br />
 *         ... ", "postlist": [ { "id": "11819", "tid": "4028", "uid":
 *         "1227786", "content": "嗯 暖心窝子  ", "ctime": "今天13:34", "user": {
 *         "nickname": "极道车神", "uid": "1227786", "avatar":
 *         "http://www.putaoji.com/Uploads/Avatar/2014-11-07/545c65f3ccbea-ba05f090_64_64.jpg"
 *         } } … ] }
 */
public class TopicDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5303501717743720468L;
	private String id;// 帖子id
	private String gid;// 葡吧id
	private String uid;// 用户id
	private String title;// 标题
	private String viewcount;// 浏览数
	private String replycount;// 回复数
	private String addtime;// 发布时间
	private User user;// 发布人信息
	private String content;// 内容html 格式
	private InterEntity relation;// 插入内容
	private String praisecount;// 点赞数
	private boolean is_support;// 是否已经点赞
	private boolean is_collect;// 是否已经收藏
	private String ctype;// 文章ctype的类型
	private boolean is_signup;
	private String deadline;// 过期时间
	// 内容的url
	private String content_url;
	private ArrayList<WineEntity> mywinelist;
	// 新加字段
	// address: 地址
	// fee：费用
	// people：人数
	// start_time：开始日期
	// end_time：结束日期
	// lat：x坐标
	// lng：y坐标
	private String address;
	private String fee;
	private String people;
	private String start_time;
	private String end_time;
	private String lat;
	private String lng;
	private ArrayList<MyWine> list;
	private String map_detail_url;
	private String link_id;
	private String img_paths;
	private String status;
	private String is_Full;//是否满员
	
	public void setIs_Full(String is_Full) {
		this.is_Full = is_Full;
	}
	public String getIs_Full() {
		return is_Full;
	}
	
	public ArrayList<WineEntity> getMywinelist() {
		return mywinelist;
	}

	public void setMywinelist(ArrayList<WineEntity> mywinelist) {
		this.mywinelist = mywinelist;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImg_paths() {
		return img_paths;
	}

	public void setImg_paths(String img_paths) {
		this.img_paths = img_paths;
	}

	public void setLink_id(String link_id) {
		this.link_id = link_id;
	}

	public String getLink_id() {
		return link_id;
	}

	public String getMap_detail_url() {
		return map_detail_url;
	}

	public void setMap_detail_url(String map_detail_url) {
		this.map_detail_url = map_detail_url;
	}

	public ArrayList<MyWine> getList() {
		return list;
	}

	public void setList(ArrayList<MyWine> list) {
		this.list = list;
	}

	// v3新加字段
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

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public ArrayList<Banner> getmImages() {
		return mImages;
	}

	public void setmImages(ArrayList<Banner> mImages) {
		this.mImages = mImages;
	}

	// 分享的内容
	private ShareEntity share;

	public void setShare(ShareEntity share) {
		this.share = share;
	}

	public ShareEntity getShare() {
		return share;
	}

	public void setContent_url(String content_url) {
		this.content_url = content_url;
	}

	public String getContent_url() {
		return content_url;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setIs_signup(boolean is_signup) {
		this.is_signup = is_signup;
	}

	public boolean isIs_signup() {
		return is_signup;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getCtype() {
		return ctype;
	}

	/*
	 * 用户自己创建的贴息的图片组 </暂用src代表big图地址，url代表small图>
	 */
	private ArrayList<Banner> mImages;

	public void setImages(ArrayList<Banner> mImages) {
		this.mImages = mImages;
	}

	public ArrayList<Banner> getImages() {
		return mImages;
	}

	public void setIs_collect(boolean is_collect) {
		this.is_collect = is_collect;
	}

	public boolean isIs_collect() {
		return is_collect;
	}

	public void setIs_support(boolean is_support) {
		this.is_support = is_support;
	}

	public boolean isIs_support() {
		return is_support;
	}

	public void setPraisecount(String praisecount) {
		this.praisecount = praisecount;
	}

	public String getPraisecount() {
		return praisecount;
	}

	// private List<Post> postlist; //评论列表
	// public List<Post> getPostlist() {
	// return postlist;
	// }

	public InterEntity getRelation() {
		return relation;
	}

	public void setRelation(InterEntity relation) {
		this.relation = relation;
	}

	@Override
	public String toString() {
		return "TopicDetail [id=" + id + ", gid=" + gid + ", uid=" + uid
				+ ", title=" + title + ", viewcount=" + viewcount
				+ ", replycount=" + replycount + ", addtime=" + addtime
				+ ", user=" + user + ", content=" + content + ", relation="
				+ relation + ", praisecount=" + praisecount + ", is_support="
				+ is_support + ", is_collect=" + is_collect + ", ctype="
				+ ctype + ", is_signup=" + is_signup + ", deadline=" + deadline
				+ ", content_url=" + content_url + ", address=" + address
				+ ", fee=" + fee + ", people=" + people + ", start_time="
				+ start_time + ", end_time=" + end_time + ", lat=" + lat
				+ ", lng=" + lng + ", share=" + share + ", mImages=" + mImages
				+ ",map_detail_url" + map_detail_url + ",img_paths"+img_paths+",status"+status
				+",mywinelist"+mywinelist+"]";
	}

	// public void setPostlist(List<Post> postlist) {
	// this.postlist = postlist;
	// }
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getViewcount() {
		return viewcount;
	}

	public void setViewcount(String viewcount) {
		this.viewcount = viewcount;
	}

	public String getReplycount() {
		return replycount;
	}

	public void setReplycount(String replycount) {
		this.replycount = replycount;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
