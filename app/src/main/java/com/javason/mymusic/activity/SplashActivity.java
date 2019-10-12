package com.javason.mymusic.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.javason.mymusic.MainActivity;
import com.javason.mymusic.R;
import com.javason.mymusic.util.Consts;
import com.javason.mymusic.util.PackageUtil;

import awty.enr.pweu.AdManager;
import awty.enr.pweu.nm.sp.SplashViewSettings;
import awty.enr.pweu.nm.sp.SpotListener;
import awty.enr.pweu.nm.sp.SpotManager;
import awty.enr.pweu.nm.sp.SpotRequestListener;
import butterknife.BindView;

public class SplashActivity extends BaseCommonActivity {

    private static final String TAG = "TAG";
    private static final long DEFAULT_DELAY_TIME = 3000;
    public static final int MSG_GUIDE = 100;
    private static final int MSG_HOME = 110;

    //这样创建有内存泄漏，在性能优化我们具体讲解
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GUIDE:
                    //跳转到引导页
                    startActivityAfterFinishThis(GuideActivity.class);
                    break;

                case MSG_HOME:
                    //跳转到首页
                    startActivityAfterFinishThis(MainActivity.class);
                    break;
            }
            //next();
        }
    };

    @BindView(R.id.ad_container)
    ViewGroup ad_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
//        //得到当前界面的装饰视图
//        if(Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            //设置让应用主题内容占据状态栏和导航栏
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            //设置状态栏和导航栏颜色为透明
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }
//        //隐藏标题栏
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        setContentView(R.layout.activity_splash);
        Log.i(TAG,"setContentView");

        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        if (isShowGuide()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(MSG_GUIDE);
                }
            }, DEFAULT_DELAY_TIME);
        } else {
            //开始获取广告
            startGetAd();
            //startActivityAfterFinishThis(HomeActivity.class);
        }

        //延时3秒，在企业中通常会有很多逻辑处理，所以延时时间最好是用3-消耗的的时间
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(-1);
//            }
//        }, 3000);

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
  //广告页
    private void startGetAd() {
        AdManager.getInstance(this).init(Consts.AK, Consts.AS, false);
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d(TAG, "onRequestSuccess");
                //广告获取成功，显示广告
                showA();
            }

            @Override
            public void onRequestFailed(int i) {
                Log.d(TAG, "onRequestFailed:" + i);
                toHome();

            }
        });
    }

    private void showA() {
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        splashViewSettings.setAutoJumpToTargetWhenShowFailed(true);
        splashViewSettings.setTargetClass(MainActivity.class);
        splashViewSettings.setSplashViewContainer(ad_container);
        SpotManager.getInstance(this).showSplash(this,
                splashViewSettings, new SpotListener() {
                    @Override
                    public void onShowSuccess() {
                        Log.d(TAG, "onShowSuccess");
                    }

                    @Override
                    public void onShowFailed(int i) {
                        Log.d(TAG, "onShowFailed: " + i);

                        //如果显示的时候失败，还是进入主界面
                        //toHome();
                    }

                    @Override
                    public void onSpotClosed() {
                        Log.d(TAG, "onSpotClosed");
                    }

                    @Override
                    public void onSpotClicked(boolean b) {
                        Log.d(TAG, "onSpotClicked");
                    }
                });
    }

    private void toHome() {
        //获取失败，延时3秒进入主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_HOME);
            }
        }, DEFAULT_DELAY_TIME);
    }
}
