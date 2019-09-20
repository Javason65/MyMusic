package com.javason.mymusic.activity;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.javason.mymusic.MainActivity;
import com.javason.mymusic.R;

import butterknife.OnClick;

public class LoginActivity extends BaseCommonActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到当前界面的装饰视图
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    @OnClick(R.id.bt_login)
    public void bt_login(){
        startActivity(LoginPhoneActivity.class);
    }

    @OnClick(R.id.bt_register)
    public void bt_register(){
        startActivity(RegisterActivity.class);
    }

    @OnClick(R.id.tv_enter)
    public void tv_enter(){
        startActivity(MainActivity.class);
    }

    @OnClick(R.id.iv_login_qq)
    public void iv_login_qq(){

    }
}
