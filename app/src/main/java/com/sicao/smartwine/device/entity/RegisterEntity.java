package com.sicao.smartwine.device.entity;

import java.io.Serializable;

/**
 * Created by techssd on 2015/12/24.
 */
public class RegisterEntity implements Serializable {

    int code;
    String message;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
