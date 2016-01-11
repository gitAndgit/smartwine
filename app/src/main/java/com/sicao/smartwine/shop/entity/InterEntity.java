package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/***
 * 插入文章实体
 * 
 * @author mingqi'li
 * 
 */
public class InterEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3892988114672080560L;
	
	
	/***
	 *   "relation": {
                "type": "",
                "cover_id": "http://www.putaoji.com",
                "rid": "",
                "title": ""
            },
	 */
	//插入类型(美酒推荐/话题/葡萄酒)
	String type;
	//插入内容的图片地址
	String cover_id;
	//插入内容的id
	String rid;
	//插入内容的标题
	String title;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCover_id() {
		return cover_id;
	}
	public void setCover_id(String cover_id) {
		this.cover_id = cover_id;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
   
}
