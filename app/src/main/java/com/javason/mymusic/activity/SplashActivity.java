package com.javason.mymusic.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.javason.mymusic.MainActivity;
import com.javason.mymusic.R;
import com.javason.mymusic.util.PackageUtil;

public class SplashActivity extends BaseCommonActivity {

    //这样创建有内存泄漏，在性能优化我们具体讲解
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            next();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        //延时3秒，在企业中通常会有很多逻辑处理，所以延时时间最好是用3-消耗的的时间
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(-1);
            }
        }, 3000);

    }

    private void next() {
        if (isShowGuide()) {
            startActivityAfterFinishThis(GuideActivity.class);
        } else if (sp.isLogin()) {
            startActivityAfterFinishThis(MainActivity.class);
        } else {
            startActivityAfterFinishThis(LoginActivity.class);
        }
    }

    /**
     * 根据当前版本号判断是否需要引导页
     * @return
     */
    private boolean isShowGuide() {
        return sp.getBoolean(String.valueOf(PackageUtil.getVersionCode(getApplicationContext())),true);
    }
}
