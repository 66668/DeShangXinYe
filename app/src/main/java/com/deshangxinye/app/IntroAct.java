package com.deshangxinye.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deshangxinye.app.utils.MLog;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import java.lang.ref.WeakReference;


/**
 * Created by andrew on 11/17/16.
 */

public class IntroAct extends AppIntro {
    public final Handler handler = new WeakRefHandler(this);

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            startToMain();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setBgDrawable(R.mipmap.ad_01);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setBgDrawable(R.mipmap.ad_02);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setBgDrawable(R.mipmap.ad_03);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        handler.postDelayed(task, 5000);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startToMain();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startToMain();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        MLog.d("onSlideChanged");
        handler.removeCallbacks(task);
        handler.postDelayed(task, 5000);
    }

    private void startToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class WeakRefHandler extends Handler {

        private final WeakReference<IntroAct> mFragmentReference;

        public WeakRefHandler(IntroAct activity) {
            mFragmentReference = new WeakReference<IntroAct>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final IntroAct activity = mFragmentReference.get();
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
}
