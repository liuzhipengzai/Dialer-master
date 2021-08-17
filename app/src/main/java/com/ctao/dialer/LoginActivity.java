package com.ctao.dialer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;


import com.ctao.dialer.http.LoginBean;
import com.ctao.dialer.http.LoginHttp;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.utils.PrefUtil;
import cn.dlc.commonlibrary.utils.ToastUtil;

//import android.support.design.widget.TabLayout;

public class LoginActivity extends BaseActivity {


  @BindView(R.id.et_account)
  EditText etAccount;
  @BindView(R.id.et_password)
  EditText etPassword;
  @BindView(R.id.tv_login)
  TextView tvLogin;

  @Override
  protected int getLayoutId() {
    return R.layout.activity_login;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PrefUtil.init(this);
    String username = PrefUtil.getDefault().getString(Constants.USERNAME, "");
    String password = PrefUtil.getDefault().getString(Constants.PASSWORD, "");
    int type = getIntent().getIntExtra(Constants.TYPE, 1);
    if (type == 1){
      if (!TextUtils.isEmpty(username)) {
        etAccount.setText(username);
        etPassword.setText(password);
        Login();
      }
    }
  }

  public static void startAct(Activity context, int type) {
    //type==1正常2异常
    Intent intent = new Intent(context, LoginActivity.class);
    intent.setFlags(
      Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(Constants.TYPE, type);
    context.startActivity(intent);
  }

  private void Login() {
    final String username = etAccount.getText().toString().trim();
    final String password = etPassword.getText().toString().trim();
    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
      ToastUtil.showOne(this, "请输入账号或密码");
    } else {
      showWaitingDialog("登录中", false);
      LoginHttp.get().login(username, password, new Bean01Callback<LoginBean>() {
        @Override
        public void onSuccess(LoginBean loginBean) {
          dismissWaitingDialog();
          PrefUtil.getDefault().saveString(Constants.USERNAME, username);
          PrefUtil.getDefault().saveString(Constants.PASSWORD, password);
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
        }

        @Override
        public void onFailure(String message, Throwable tr) {
          dismissWaitingDialog();
          showToast(message);
        }
      });
    }

  }


  @OnClick(R.id.tv_login)
  public void onViewClicked() {
    Login();
  }
}
