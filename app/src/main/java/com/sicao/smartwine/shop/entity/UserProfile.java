package com.sicao.smartwine.shop.entity;

import java.io.Serializable;

/**
 * 用户詳細资料
 * <ol>
 * <li>性別 {@link UserProfile#sex}
 * <li>郵箱 {@link UserProfile#email}
 * <li>手機號碼 {@link UserProfile#mobile}
 * <li>個性簽名 {@link UserProfile#signature}
 * <li>生日 {@link UserProfile#birthday}
 * <li>積分 {@link UserProfile#score}
 * <li>積分等級名稱 {@link UserProfile#title}
 * </ol>
 * 
 * @author mingqi'li
 * 
 */
public class UserProfile extends User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6987446144802224476L;
	private String signature;// 个性签名
	private String email;// 邮箱
	private String mobile;// 手机号码
	private String score;// 积分
	private String sex;// 性别,m|f
	private String birthday;// 生日
	private String title; // 积分等级名
	private String auth_type;// 用户账号角色（0普通用户 1品酒师 2卖家 3达人 4品牌）

	public void setAuth_type(String auth_type) {
		this.auth_type = auth_type;
	}

	public String getAuth_type() {
		return auth_type;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "UserProfile [signature=" + signature + ", email=" + email
				+ ", mobile=" + mobile + ", score=" + score + ", sex=" + sex
				+ ", birthday=" + birthday + ", title=" + title + "]";
	}

}
