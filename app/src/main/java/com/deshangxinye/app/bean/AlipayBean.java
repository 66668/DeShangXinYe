package com.deshangxinye.app.bean;

import java.io.Serializable;

/**
 * 支付结果返回
 * 格式{code msg data {}}
 */

public class AlipayBean implements Serializable {
    String signed_str;

    public String getSigned_str() {
        return signed_str;
    }

    public void setSigned_str(String signed_str) {
        this.signed_str = signed_str;
    }

}
