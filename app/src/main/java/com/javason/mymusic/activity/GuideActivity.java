package com.javason.mymusic.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.javason.mymusic.MainActivity;
import com.javason.mymusic.R;
import com.javason.mymusic.adapter.GuideAdapter;
import com.javason.mymusic.util.PackageUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class GuideActivity extends BaseCommonActivity {
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    private GuideAdapter adapter;

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

        setContentView(R.layout.activity_guide);


    }

    @Override
    protected void initDatas() {
        Log.i("TAG","initDatas in GuideActivity");
        super.initDatas();
        adapter = new GuideAdapter(getActivity(), getSupportFragmentManager());
        vp.setAdapter(adapter);
        //indicator
        indicator.setViewPager(vp);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());


        ArrayList<Integer> datas = new ArrayList<>();
        datas.add(R.drawable.guide1);
        datas.add(R.drawable.guide2);
        datas.add(R.drawable.guide3);
        adapter.setDatas(datas);

    }

    @OnClick(R.id.bt_login_or_register)
    public void bt_login_or_register() {
        setFirst();
        startActivityAfterFinishThis(LoginActivity.class);
    }

    @OnClick(R.id.bt_enter)
    public void bt_enter() {
        setFirst();
        startActivityAfterFinishThis(MainActivity.class);
    }

    private void setFirst() {
        sp.putBoolean(String.valueOf(PackageUtil.getVersionCode(getApplicationContext())), false);
    }

    /**
     * 不调用父类方法，用户按返回键就不能关闭当前页面了
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();将这行代码注释掉，用户没法退出返回，必须点击其中一个按钮，才能结束引导界面
    }
}
