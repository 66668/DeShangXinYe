package com.deshangxinye.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.deshangxinye.app.base.BaseAct;

/**
 * 启动页/欢迎页
 */
public class SplashAct extends BaseAct {
    //权限申请
    // Class members
    //-------------------------------------------------------------------------------

    private boolean isRunning = false;
    private Bundle extraBundle = null;
    private String passWord;


    // Activity methods
    //-------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决首次安装按home键置入后台，从桌面图标点击重新启动的问题
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.act_splash);
        initViews();
    }

    private void initViews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle b = new Bundle();
                startActivity(IntroAct.class, b);
                finish();
            }
        }, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 屏蔽任何按键
        return true;
    }

}