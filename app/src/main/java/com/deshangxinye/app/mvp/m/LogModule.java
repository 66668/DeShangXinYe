package com.deshangxinye.app.mvp.m;

import android.content.Context;

import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.base.MySettings;
import com.deshangxinye.app.bean.CommonBean;
import com.deshangxinye.app.bean.LoginBean;
import com.deshangxinye.app.bean.WchatInfoBean;
import com.deshangxinye.app.bean.WchatTokenBean;
import com.deshangxinye.app.mvp.http.url_main.MyHttpService;
import com.deshangxinye.app.mvp.listener.v.OnLogListener;
import com.deshangxinye.app.mvp.listener.v.OnUserinfoListener;
import com.deshangxinye.app.mvp.listener.v.OnWchatListener;
import com.deshangxinye.app.utils.MLog;
import com.deshangxinye.app.utils.SPUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * 登录 m层 具体实现
 */
public class LogModule {

    private Context context;
    private static final String TAG = "SJY";

    public LogModule(Context context) {
        this.context = context;
    }


    public void loginNomal(final OnLogListener listener, String name, String ps) {
        MyHttpService.Builder.getHttpServer()//固定样式，可自定义其他网络
                .loginNormal(name, ps)//接口方法
                .subscribeOn(Schedulers.io())//固定样式
                .unsubscribeOn(Schedulers.io())//固定样式
                .observeOn(AndroidSchedulers.mainThread())//固定样式
                .subscribe(new Observer<LoginBean>() {//固定样式，可自定义其他处理
                    @Override
                    public void onComplete() {
                        MLog.d(TAG, "登录--onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        MLog.e(TAG, "登录--登录失败异常onError:" + e.toString());
                        listener.onLoginNormalFailed("异常", new Exception("接口异常！"));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginBean bean) {
                        MLog.d(TAG, "登录-onNext");

                        //处理返回结果
                        if (bean.getCode() == 0) {
                            MLog.d(TAG, "登录-成功");
                            listener.onLoginNormalSuccess(bean);
                        } else {
                            listener.onLoginNormalFailed(bean.getMessage(), null);
                        }
                    }

                });
    }


    /**
     * 微信登陆（1）先获取token
     *
     * @param code
     * @param listener
     */
    public void getWchatToken(String code, final OnWchatListener listener) {
        /*
         * 将你前面得到的AppID、AppSecret、code，拼接成URL 获取access_token等等的信息(微信)
         */
        String url = getWchatCodeRequest(code);

        MyHttpService.Builder.getHttpServer()//固定样式，可自定义其他网络
                .getWchatToken(url)//接口方法
                .subscribeOn(Schedulers.io())//固定样式
                .observeOn(AndroidSchedulers.mainThread())//固定样式
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        MLog.e("getWchatToken--获取微信token失败--onError");
                        listener.onWchatFailed("获取微信token失败", null);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String jsonStr = new String(responseBody.bytes());
                            MLog.d("getWchatToken--onNext--" + jsonStr);
                            if (jsonStr.contains("access_token")) {
                                WchatTokenBean bean = new Gson().fromJson(jsonStr, WchatTokenBean.class);
                                String access_token = bean.getAccess_token();
                                String openid = bean.getOpenid();
                                String refresh_token = bean.getRefresh_token();
                                String get_user_info_url = getWchatUserInfo(access_token, openid);
                                //
                                getWchatInfo(get_user_info_url, openid, access_token, refresh_token, listener);
                            } else {
                                MLog.e("getWchatToken--重复获取");
                                //大概率重复登陆
                                listener.onWchatFailed("重复获取", null);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            MLog.e("getWchatToken--" + e.toString());
                            listener.onWchatFailed("获取微信token失败", null);
                        }
                    }
                });

    }

    /**
     * 微信登陆（2）获取info
     *
     * @param url
     * @param access_token
     * @param refresh_token
     * @param listener
     */
    public void getWchatInfo(String url, final String openid, final String access_token, final String refresh_token, final OnWchatListener listener) {
        MyHttpService.Builder.getHttpServer()//固定样式，可自定义其他网络
                .getWchatInfo(url)//接口方法
                .subscribeOn(Schedulers.io())//固定样式
                .observeOn(AndroidSchedulers.mainThread())//固定样式
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onWchatFailed("获取微信Info失败", null);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String jsonStr = new String(responseBody.bytes());
                            MLog.e("getWchatInfo--" + jsonStr);
                            if (jsonStr.contains("unionid")) {
                                WchatInfoBean bean = new Gson().fromJson(jsonStr, WchatInfoBean.class);
                                String unionid = bean.getUnionid();
                                String nickName = bean.getNickname();
                                //
                                //数据返回,登录界面处理,无法使用 intent值跳转
                                SPUtil.putString("unionid", unionid);
                                SPUtil.putString("openid", openid);
                                SPUtil.putString("access_token", access_token);
                                SPUtil.putString("refresh_token", refresh_token);
                                SPUtil.putString("nickName", nickName);
                                SPUtil.putString("avatar", bean.getHeadimgurl());
                                listener.onWchatSuccess();
                            } else {
                                MLog.e("getWchatInfo--获取微信Info失败");
                                listener.onWchatFailed("获取微信Info失败", null);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            MLog.d("getWchatInfo--获取微信Info" + e.toString());
                            listener.onWchatFailed("获取微信Info失败", null);
                        }
                    }
                });

    }

    public void loginThird(final OnLogListener listener, final int btype, final String bid, final long stamp, String sign, final String accessToken, final String refreshToken, final String name) {
        MySettings settings = MySettings.getInstance();
        MyHttpService.Builder.getHttpServer()//固定样式，可自定义其他网络
                .loginThird(btype, bid, stamp, sign, settings.token)//接口方法
                .subscribeOn(Schedulers.io())//固定样式
                .unsubscribeOn(Schedulers.io())//固定样式
                .observeOn(AndroidSchedulers.mainThread())//固定样式
                .subscribe(new Observer<LoginBean>() {//固定样式，可自定义其他处理
                    @Override
                    public void onComplete() {
                        MLog.d(TAG, "登录--onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        MLog.e(TAG, "登录--登录失败异常onError:" + e.toString());
                        listener.onLoginThirdFailed("异常", new Exception("接口异常！"), bid, btype, stamp, accessToken, refreshToken, name);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginBean bean) {
                        MLog.d(TAG, "登录-onNext");

                        //处理返回结果
                        if (bean.getCode() == 0) {
                            MLog.d(TAG, "登录-成功");
                            listener.onLoginThirdSuccess(bean);
                        } else {
                            listener.onLoginThirdFailed(bean.getMessage(), null, bid, btype, stamp, accessToken, refreshToken, name);
                        }
                    }

                });
    }

    public void mLogout(final OnUserinfoListener listener) {
        MySettings settings = MySettings.getInstance();
        MyHttpService.Builder.getHttpServer()//固定样式，可自定义其他网络
                .logout(settings.token)//接口方法
                .subscribeOn(Schedulers.io())//固定样式
                .unsubscribeOn(Schedulers.io())//固定样式
                .observeOn(AndroidSchedulers.mainThread())//固定样式
                .subscribe(new Observer<CommonBean>() {//固定样式，可自定义其他处理
                    @Override
                    public void onComplete() {
                        MLog.d("验证码--onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        MLog.e("验证码--异常onError:" + e.toString());
                        listener.onLogoutFailed("异常", new Exception("接口异常！"));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonBean bean) {
                        MLog.d("验证码-onNext");

                        //处理返回结果
                        if (bean.getCode() == 0) {
                            MLog.d("验证码-成功");
                            listener.onLogoutSuccess(bean);
                        } else {
                            MLog.e("验证码-成功");
                            listener.onLogoutFailed(bean.getMessage(), null);
                        }
                    }

                });
    }


    /**
     * 微信登陆相关处理
     */
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

    private String getWchatCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID",
                urlEnodeUTF8(BaseConst.WX_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET",
                urlEnodeUTF8(BaseConst.WX_APP_SECRET));
        GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
        result = GetCodeRequest;
        return result;
    }

    private String getWchatUserInfo(String access_token, String openid) {
        String result = null;
        GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN",
                urlEnodeUTF8(access_token));
        GetUserInfo = GetUserInfo.replace("OPENID", urlEnodeUTF8(openid));
        result = GetUserInfo;
        return result;
    }

    private String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
