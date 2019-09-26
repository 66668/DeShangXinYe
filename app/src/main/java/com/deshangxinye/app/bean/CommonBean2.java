package com.deshangxinye.app.bean;

import java.io.Serializable;

/**
 * 统一接收数据的bean
 */

public class CommonBean2<T> implements Serializable {
    int code;
    String msg;
    T profile;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getProfile() {
        return profile;
    }

    public void setProfile(T profile) {
        this.profile = profile;
    }

    public CommonBean2(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CommonBean{" +
                "code=" + code +
                ", message='" + msg + '\'' +
                '}';
    }
}
