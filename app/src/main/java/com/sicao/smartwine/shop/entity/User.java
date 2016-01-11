package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/**
 * 用户类基本信息
 * <ol>
 * <li>用戶名 {@link User#nickname}
 * <li>用戶id、 {@link User#uid}
 * <li>用戶頭像url {@link User#avatar}
 * </ol>
 * 
 * @author mingqi'li
 * 
 */
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 用戶名
	private String nickname;
	// 用户id、
	private String uid;
	// 用户头像
	private String avatar;
	// 用户级别
	private String identity;
	// 是否可点击
	private boolean clickable;

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public boolean isClickable() {
		return clickable;
	}

	@Override
	public String toString() {
		return "User [nickname=" + nickname + ", uid=" + uid + ", avatar="
				+ avatar + ",identity=" + identity + "]";
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
