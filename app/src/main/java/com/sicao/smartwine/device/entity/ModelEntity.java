package com.sicao.smartwine.device.entity;

import java.io.Serializable;

public class ModelEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4382638320833400812L;
	/*
	 * "id": "1", "uid": "1212121", "device_id":
	 * "9687c8867e535e08dae006e759862d75@life.com", "work_model_name": "sdsd",
	 * "work_model_demp": "12"
	 */
	private int id;
	private String uid;
	private String device_id;
	private String work_model_name;
	private String work_model_demp;
	private boolean select;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getWork_model_name() {
		return work_model_name;
	}

	public void setWork_model_name(String work_model_name) {
		this.work_model_name = work_model_name;
	}

	public String getWork_model_demp() {
		return work_model_demp;
	}

	public void setWork_model_demp(String work_model_demp) {
		this.work_model_demp = work_model_demp;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	@Override
	public String toString() {
		return "ModelEntity [id=" + id + ", uid=" + uid + ", device_id="
				+ device_id + ", work_model_name=" + work_model_name
				+ ", work_model_demp=" + work_model_demp + ", select=" + select
				+ "]";
	}

}
