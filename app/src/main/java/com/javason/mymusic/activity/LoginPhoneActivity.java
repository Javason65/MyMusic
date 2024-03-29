package com.javason.mymusic.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.javason.mymusic.MainActivity;
import com.javason.mymusic.R;
import com.javason.mymusic.api.Api;
import com.javason.mymusic.domain.Session;
import com.javason.mymusic.domain.User;
import com.javason.mymusic.domain.event.LoginSuccessEvent;
import com.javason.mymusic.domain.response.DetailResponse;
import com.javason.mymusic.reactivex.HttpListener;
import com.javason.mymusic.util.StringUtil;
import com.javason.mymusic.util.ToastUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginPhoneActivity extends BaseTitleActivity {

    @BindView(R.id.et_phone)
    EditText et_phone;

    @BindView(R.id.et_password)
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
    }

    @OnClick(R.id.bt_login)
    public void bt_login() {
        String phone = et_phone.getText().toString();
        if (StringUtils.isBlank(phone)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_phone);
            return;
        }

        if (!StringUtil.isPhone(phone)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_phone);
            return;
        }

        String password = et_password.getText().toString();
        if (StringUtils.isBlank(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_password);
            return;
        }

        if (!StringUtil.isPassword(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_password_format);
            return;
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setType(User.TYPE_PHONE);

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

    /**
     * 登陆完成后，保存相关信息，并跳转到主界面
     *
     * @param data
     */
    private void next(Session data) {
        sp.setToken(data.getToken());
        sp.setUserId(data.getId());
        sp.setIMToken(data.getIm_token());
        startActivityAfterFinishThis(MainActivity.class);

        EventBus.getDefault().post(new LoginSuccessEvent());
    }

}
