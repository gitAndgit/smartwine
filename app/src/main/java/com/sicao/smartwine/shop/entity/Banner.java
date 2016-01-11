package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/**
 * 轮播广告栏
 * 
 * @author putaoji
 * 
 */
public class Banner implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3787879831317218778L;
	private String url;// 详情链接
	private String src;// 轮播图片地址
	private String ctype;// 广告图类型
	private String cid;// 广告图对应的文章/帖子/商品的id
	private String path;// 广告图的地址--------------2015-5-20号修改
	private String link;//广告地址----2015-08-31号

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCid() {
		return cid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	@Override
	public String toString() {
		return "Banner [url=" + url + ", src=" + src + ", ctype=" + ctype
				+ ", cid=" + cid + ", path=" + path + ", link=" + link+"]";
	}

}
