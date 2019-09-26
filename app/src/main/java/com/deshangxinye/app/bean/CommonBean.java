package com.deshangxinye.app.bean;

import java.io.Serializable;

/**
 * 统一接收数据的bean
 */

public class CommonBean implements Serializable {
    int code;
    String msg;

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

    public CommonBean(int code, String msg) {
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
