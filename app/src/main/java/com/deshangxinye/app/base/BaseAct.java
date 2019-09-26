package com.deshangxinye.app.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.deshangxinye.app.utils.MLog;

import java.lang.ref.WeakReference;
import java.util.Vector;

public class BaseAct extends Activity {

    protected final String TAG = getClass().getSimpleName();
    protected boolean isInFront;
    protected int createStatus; // 0 firstCreate, 1 resume, 2 reCreate
    private Vector<Dialog> dialogs;
    public final Handler handler = new WeakRefHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySettings.getInstance().topAct = this;

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        createStatus = 2;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    //===================================新版 跳转 --开始==========================================

    public void startToMain(Class clz) {
        Intent i = new Intent(this, clz);//推荐显示调用
        startActivity(i);
    }

    public void startActivity(Class clz) {
        Intent i = new Intent(this, clz);//推荐显示调用
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * android5.0要求第一个参数是具体的类
     *
     * @param act
     * @param clz
     */
    public void startActivity(Activity act, Class clz) {
        Intent i = new Intent(act, clz);//推荐显示调用
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void startActivity(Class clz, Bundle aBundle) {
        Intent i = new Intent(this, clz);//推荐显示调用
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (aBundle != null) {
            i.putExtras(aBundle);
        }
        startActivity(i);

    }

    public void startToMain(Class clz, Bundle aBundle) {
        Intent i = new Intent(this, clz);//推荐显示调用
        if (aBundle != null) {
            i.putExtras(aBundle);
        }
        startActivity(i);

    }

    /**
     * android5.0要求第一个参数是具体的类
     *
     * @param act
     * @param clz
     */
    public void startActivity(Activity act, Class clz, Bundle aBundle) {
        Intent i = new Intent(act, clz);//推荐显示调用
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (aBundle != null) {
            i.putExtras(aBundle);
        }
        startActivity(i);

    }

    public void startActForResult(Class aActName, Bundle aBundle, int requestCode) {
        Intent i = new Intent(this, aActName);//推荐显示调用
        if (aBundle != null)
            i.putExtras(aBundle);
        startActivityForResult(i, requestCode);
    }
    //===================================新版 跳转 --结束==========================================


    //===================================handler软引用 --开始==========================================


    public class WeakRefHandler extends Handler {

        private final WeakReference<BaseAct> mFragmentReference;

        public WeakRefHandler(BaseAct activity) {
            mFragmentReference = new WeakReference<BaseAct>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseAct activity = mFragmentReference.get();
            if (activity != null) {
                try {
                    activity.handleMessage(msg);
                } catch (Exception e) {
                    MLog.e("SJY", e.toString());
                }

            }
        }
    }

    /**
     * @param msg
     */
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            default:
                break;
        }
    }
    //===================================handler软引用 --结束==========================================

}
