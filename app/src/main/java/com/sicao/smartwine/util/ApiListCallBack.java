package com.sicao.smartwine.util;

import java.util.ArrayList;

/*
 * 接口回调
 */
public interface ApiListCallBack {
	// 执行成功
	public <T> void response(ArrayList<T> list);
}
