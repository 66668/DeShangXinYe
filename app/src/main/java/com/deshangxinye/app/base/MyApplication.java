package com.deshangxinye.app.base;

import android.app.Application;

import com.deshangxinye.app.mvp.http.url_main.HttpUtils;
import com.deshangxinye.app.utils.MLog;


/**
 * sjy 0607
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication application;

    public static MyApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //log初始化
        MLog.init(true, "SJY");
        initialize();
        //新网络框架 初始化
        HttpUtils.getInstance().init(this, MLog.DEBUG);
    }

    // private methods
    //-------------------------------------------------------------------------------
    private void initialize() {
        MySettings settings = MySettings.getInstance();
        settings.appContext = this;
        settings.readPref();

    }

}
