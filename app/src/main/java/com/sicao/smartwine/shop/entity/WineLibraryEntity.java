package com.sicao.smartwine.shop.entity;

import java.io.Serializable;
import java.util.Arrays;

public class WineLibraryEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7567459465720931574L;
	/*
	 * id 筛选id title 筛选名称 description 描述 path 图片路径 sub 子筛选，字段同上
	 */
	private String id;
	private String title;
	private String description;
	private String path;
	private WineLibraryEntity[] sub;
	private boolean ischeck;
	

	public boolean isIscheck() {
		return ischeck;
	}

	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public WineLibraryEntity[] getSub() {
		return sub;
	}

	public void setSub(WineLibraryEntity[] sub) {
		this.sub = sub;
	}

	@Override
	public String toString() {
		return "WineLibraryEntity [id=" + id + ", title=" + title
				+ ", description=" + description + ", path=" + path + ", sub="
				+ Arrays.toString(sub) + "]";
	}

}
