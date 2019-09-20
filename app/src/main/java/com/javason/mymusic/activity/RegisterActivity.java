package com.javason.mymusic.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.javason.mymusic.R;
import com.javason.mymusic.domain.User;
import com.javason.mymusic.util.StringUtil;
import com.javason.mymusic.util.ToastUtil;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseCommonActivity {
    @BindView(R.id.et_nickname)
    EditText et_nickname;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_phone)
    EditText et_phone;

    private String phone;
    private String nickname;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
    }

    @OnClick(R.id.bt_register)
    public void bt_register() {
        nickname = et_nickname.getText().toString();
        if (StringUtils.isBlank(nickname)  ) {
            ToastUtil.showSortToast(getActivity(), R.string.enter_nickname);
            return;
        }

        if (nickname.contains(" ")) {
            //更复杂的，建议用正则表达式
            ToastUtil.showSortToast(getActivity(), R.string.nickname_space);
            return;
        }

        phone = et_phone.getText().toString();
        if (StringUtils.isBlank(phone)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_phone);
            return;
        }

        if (!StringUtil.isPhone(phone)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_phone);
            return;
        }

        password = et_password.getText().toString();
        if (StringUtils.isBlank(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_password);
            return;
        }

        if (!StringUtil.isPassword(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_password_format);
            return;
        }

        User user = new User();
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setPassword(password);
        user.setType(User.TYPE_PHONE);

        Api.getInstance().register(user)
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
}
