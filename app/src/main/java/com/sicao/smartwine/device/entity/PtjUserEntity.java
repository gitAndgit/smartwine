package com.sicao.smartwine.device.entity;

import java.io.Serializable;

/**
 * Created by techssd on 2015/12/31.
 */
public class PtjUserEntity implements Serializable {
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    /***
     * {
     "status": true,
     "error_code": 0,
     "info": {
     "uid": "1231915",
     "avatar": "http:\/\/www.putaoji.com\/Uploads\/Avatar\/qqAvatar\/555c9c41abb81_128_128.jpg",
     "nickname": "niti",
     "signature": "爱美酒.",
     "email": "",
     "mobile": "18818689897",
     "score": "10",
     "sex": "f",
     "birthday": "0000-00-00",
     "title": "Lv1 实习"
     "auth_type": "0",
     }
     }
     */
    String uid;
    String avatar;
    String nickname;
    String signature;

    String email;
    String mobile;
    String score;
    String sex;
    String birthday;
    String title;
    String auth_type;

}
