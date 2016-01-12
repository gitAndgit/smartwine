package com.sicao.smartwine.pay;

import com.sicao.smartwine.shop.entity.WineEntity;
import com.sicao.smartwine.user.entity.Address;
import java.io.Serializable;

/**
 * 订单
 * 
 * @author mingqi'li
 * 
 */
public class OrderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3035335910394789215L;

	// 订单号
	private String orderNumber;
	// 订单价格
	private String orderPrice;
	// 订单id
	private String orderId;
	// 商品详细信息
	private WineEntity goods;
	//订单状态
	private String state;
	//收货地址信息
	private Address address;
	//商品套餐规格
	private WineEntity meal;
	//下单时间
	private String orderTime;
	//服务器异步通知页面
	private String notifyurl;
	
	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}
	public String getNotifyurl() {
		return notifyurl;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public WineEntity getMeal() {
		return meal;
	}
	public void setMeal(WineEntity meal) {
		this.meal = meal;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}

	public OrderEntity() {
		// TODO Auto-generated constructor stub
	}

	public OrderEntity(String orderNumber, String orderPrice, String orderId,
			 WineEntity goods) {
		super();
		this.orderNumber = orderNumber;
		this.orderPrice = orderPrice;
		this.orderId = orderId;
		this.goods = goods;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}

	public WineEntity getGoods() {
		return goods;
	}

	public void setGoods(WineEntity goods) {
		this.goods = goods;
	}
	@Override
	public String toString() {
		return "OrderEntity [orderNumber=" + orderNumber + ", orderPrice="
				+ orderPrice + ", orderId=" + orderId + ", goods=" + goods
				+ ", state=" + state + ", address=" + address + ", meal="
				+ meal + ", orderTime=" + orderTime + ", notifyurl="
				+ notifyurl + "]";
	}

	

}
