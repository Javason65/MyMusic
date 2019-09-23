package com.javason.mymusic.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.javason.mymusic.MainActivity;
import com.javason.mymusic.R;
import com.javason.mymusic.api.Api;
import com.javason.mymusic.domain.Session;
import com.javason.mymusic.domain.User;
import com.javason.mymusic.domain.event.LoginSuccessEvent;
import com.javason.mymusic.domain.response.DetailResponse;
import com.javason.mymusic.reactivex.HttpListener;
import com.javason.mymusic.util.LogUtil;
import com.javason.mymusic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseCommonActivity {

    private static final int QQ_LOGIN_FLAG_SUCCESS = 100;
    private static final int WECHAT_LOGIN_FLAG_SUCCESS = 101;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QQ_LOGIN_FLAG_SUCCESS: {
                    processLoginUserInfo(ShareSDK.getPlatform(QQ.NAME), User.TYPE_QQ);
                    break;
                }
                case WECHAT_LOGIN_FLAG_SUCCESS: {
                    //processLoginUserInfo(ShareSDK.getPlatform(WEIBO),User.TYPE_WEIBO);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到当前界面的装饰视图
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
        //隐藏标题栏
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        EventBus.getDefault().register(this);
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
    public void iv_login_qq() {
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.SSOSetting(false);  //设置false表示使用SSO授权方式
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        qq.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                loginFailed();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                LogUtil.d("qq qqLogin onComplete:" + (Looper.myLooper() == Looper.getMainLooper()));
                //该方法回调不是在主线程
                handler.sendEmptyMessage(QQ_LOGIN_FLAG_SUCCESS);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        //authorize与showUser单独调用一个即可
        qq.showUser(null);//授权并获取用户信息
    }

    private void loginFailed() {
        ToastUtil.showSortToast(getActivity(), "登陆失败,请稍后再试!");
    }

    private void processLoginUserInfo(Platform p,int type) {
        showLoading();

        PlatformDb db = p.getDb();

        //这里的逻辑是QQ不需要在调用注册，直接调用登陆，如果有相同的OpenId表示登陆成功
        //在QQ这里的逻辑是每个AppKey对应的OpenId都不一样
        // 但可以发邮件让他们将两个应用的OpenId打通，但有一些限制
        User user = new User();
        user.setNickname(db.getUserName());

        //建议将前面的http去掉
        user.setAvatar(db.getUserIcon());
        user.setOpen_id(db.getUserId());
        user.setType(type);

        Api.getInstance().login(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Session>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<Session> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSuccessEvent(LoginSuccessEvent event) {
        //连接融云服务器
//        ((AppContext)getApplication()).imConnect();
        finish();
    }

    private void next(Session data) {
        sp.setToken(data.getToken());
        sp.setIMToken(data.getIm_token());
        sp.setUserId(data.getId());
        startActivity(MainActivity.class);

        loginSuccessEvent(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
