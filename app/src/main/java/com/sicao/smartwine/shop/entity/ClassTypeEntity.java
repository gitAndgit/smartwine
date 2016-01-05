package com.sicao.smartwine.shop.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 美酒库顶部筛选数据
 * 
 * @author yongzhong'han
 * 
 */
public class ClassTypeEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4412824374894729341L;

	private String name;
	private ClassTypeEntity[] subs;
	private boolean isSelect;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassTypeEntity[] getSubs() {
		return subs;
	}

	public void setSubs(ClassTypeEntity[] subs) {
		this.subs = subs;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	@Override
	public String toString() {
		return "ClassTypeEntity [name=" + name + ", subs="
				+ Arrays.toString(subs) + ", isSelect=" + isSelect + "]";
	}

	

}
