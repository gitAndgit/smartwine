package com.sicao.smartwine.pay;

public class Constants {

    /*
     * 支付加密字符串
     */
    public static final String KEY = "app@putaoji.com";
    /*
     * 调用支付宝
     */
    public static final int OPEN_ALIPAY_CLIENT = 10101;
    /*
     * 调用微信
     */
    public static final int OPEN_WX_CLIENT = 10102;
    /*
     * 微信支付的结果
     */
    public static final String WX_PAY_RETURN = "WX_PAY_RETURN";
    /*
     *报名参加集品酒会成功后发送广播修改状态
     */
    public static final String JOIN_PARTY_SUCCESS = "JOIN_PARTY_SUCCESS";


    /*
     * 修改地址
     */
    public static final int REVISE_ADDRESS = 10100;
    /*
     * 选择优惠券
     */
    public static final int SELECT_INVITE = 10103;
    /*
	 * 用户反馈
	 */
    public static final int FEED_BACK_USER = 1;
    /*
     * 添加地址
     *
     */
    public static final int ADD_USER_ADDRESS = 10096;
    /*
     *地址栏没有地址的时候跳转的添加地址的请求码
     */
    public static final int TO_ADD_ADDRESS = 10106;

    /*
     *删除地址信息id广播
     */
    public static final String CANCLE_ADDRESS_ID = "CANCLE_ADDRESS_ID";
    /*
     *如果收货地址是单一的
     */
    public static final String SINGLE_ADDRESS = "SINGLE_ADDRESS";

}
