package com.sicao.smartwine.util;

import java.util.ArrayList;

/**
 * Created by techssd on 2016/1/8.
 */
public interface ApiListAndObjectCallBack {
    // 执行成功
    public <T> void response(ArrayList<T> list, Object object);
}
