package com.sicao.smartwine.user.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 代金券
 * 
 * @author mingqi'li
 * 
 */
public class InviteEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3294445634275572741L;
	
	//id
	private String id;
	//代金券总额
	private String allMoney;
	//该代金券的领取人
	private String phone;
	//该代金券的金额
	private String price;
	//该代金券的领取人是否已经注册
	private String isreg;
	
	private ArrayList<InviteEntity>list;
    
	
	public InviteEntity() {
		// TODO Auto-generated constructor stub
	}
	public InviteEntity(String id, String allMoney, String phone, String price,
			String isreg, ArrayList<InviteEntity> list) {
		super();
		this.id = id;
		this.allMoney = allMoney;
		this.phone = phone;
		this.price = price;
		this.isreg = isreg;
		this.list = list;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAllMoney() {
		return allMoney;
	}

	public void setAllMoney(String allMoney) {
		this.allMoney = allMoney;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getIsreg() {
		return isreg;
	}

	public void setIsreg(String isreg) {
		this.isreg = isreg;
	}

	public ArrayList<InviteEntity> getList() {
		return list;
	}

	public void setList(ArrayList<InviteEntity> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "InviteEntity [id=" + id + ", allMoney=" + allMoney + ", phone="
				+ phone + ", price=" + price + ", isreg=" + isreg + ", list="
				+ list + "]";
	}
	
    
}
