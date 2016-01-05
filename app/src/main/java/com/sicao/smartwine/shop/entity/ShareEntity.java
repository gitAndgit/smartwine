package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/**
 * 分享
 * 
 * @author mingqi'li
 * 
 */
public class ShareEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8996385599818101458L;
	/*
	 * "cover":
	 * "http://img1.putaoji.com/Uploads/Picture/2015-06-02/556d12944f5c8.jpg",
	 * "title": "衣服上滴有红酒要怎么清洗", "content":
	 * "偶尔饭局，喝一杯红酒，但是最尴尬的问题莫过于不小心洒了一身红酒，红酒渍弄到衣服上...", "url":
	 * "http://www.putaoji.com/Wap/Sns/article/id/14000.html"
	 */
	private String cover;//图标地址
	private String url;//分享出去的链接
	private String title;//分享的标题
	private String content;//分享的内容
	private String sharePicUrl;
	private String qrcode;//分享出去的二维码的图片
	
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getQrcode() {
		return qrcode;
	}
	public String getSharePicUrl() {
		return sharePicUrl;
	}
	public void setSharePicUrl(String sharePicUrl) {
		this.sharePicUrl = sharePicUrl;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "ShareEntity [cover=" + cover + ", url=" + url + ", title="
				+ title + ", content=" + content +",sharePicUrl"+sharePicUrl+"]";
	}

}
