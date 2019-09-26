package com.deshangxinye.app.mvp.p;

import android.content.Context;

import com.deshangxinye.app.mvp.listener.v.OnLogListener;
import com.deshangxinye.app.mvp.listener.v.OnWchatListener;
import com.deshangxinye.app.mvp.m.LogModule;


/**
 * 登录 p层 具体实现
 */
public class LogPresenter implements OnLogListener, OnWchatListener {
    private Context context;
    private OnLogListener onLogView;
    private OnWchatListener onWchatView;
    //p层调用M层方法
    private LogModule logModule;

    public LogPresenter(Context context, OnLogListener logListener) {
        this.context = context;
        this.onLogView = logListener;
        logModule = new LogModule(context);
    }

    public LogPresenter(Context context, OnWchatListener logListener) {
        this.context = context;
        this.onWchatView = logListener;
        logModule = new LogModule(context);
    }

    //============================p层重写，用于调用m层方法============================
    public void loginNormal(String name, String ps) {
        logModule.loginNomal(this, name, ps);
    }

    public void loginThird(int aArray, String unionId, long currentTime, String accessToken, String refreshToken, String name) {
        String sign = "bid=" + unionId + "&btype=" + aArray + "&stamp=" + currentTime + "qingbiji";
        logModule.loginThird(this
                , aArray
                , unionId
                , currentTime
                , ""
                , accessToken
                , refreshToken
                , name);
    }


    /**
     * module有两个连续调用的接口
     *
     * @param url
     */
    public void getWchatToken(String url) {
        logModule.getWchatToken(url, this);
    }

    //==========================结果回调==============================
    @Override
    public void onLoginNormalSuccess(Object obj) {
        onLogView.onLoginNormalSuccess(obj);
    }

    @Override
    public void onLoginNormalFailed(String msg, Exception e) {
        onLogView.onLoginNormalFailed(msg, e);
    }

    @Override
    public void onQQUnionIdSuccess(Object obj, String accessToken, String refreshToken) {
        onLogView.onQQUnionIdSuccess(obj, accessToken, refreshToken);
    }

    @Override
    public void onQQUnionIdFailed(String msg, Exception e) {
        onLogView.onQQUnionIdFailed(msg, e);
    }

    @Override
    public void onLoginThirdSuccess(Object obj) {
        onLogView.onLoginThirdSuccess(obj);
    }

    @Override
    public void onLoginThirdFailed(String msg, Exception e, String bid, int btype, long currentTime, String accessToken, String refreshToken, String name) {
        onLogView.onLoginThirdFailed(msg, e, bid, btype, currentTime, accessToken, refreshToken, name);
    }

    @Override
    public void onLogProfileSuccess(Object obj) {
        onLogView.onLogProfileSuccess(obj);
    }

    @Override
    public void onLogProfileFailed(String msg, Exception e) {
        onLogView.onLogProfileFailed(msg, e);
    }


    //============================微信登陆相关============================
    //第一次接口，获取token

    @Override
    public void onWchatSuccess() {
        onWchatView.onWchatSuccess();
    }

    @Override
    public void onWchatFailed(String msg, Exception e) {
        onWchatView.onWchatFailed(msg, e);
    }
}
