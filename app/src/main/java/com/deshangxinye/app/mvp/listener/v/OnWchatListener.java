package com.deshangxinye.app.mvp.listener.v;

/**
 * 微信登录 v层
 */
public interface OnWchatListener {
    void onWchatSuccess();

    void onWchatFailed(String msg, Exception e);


}
