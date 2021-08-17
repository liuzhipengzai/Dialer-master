package com.ctao.dialer.http;

import com.lzy.okgo.model.HttpParams;

import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;

/**
 * Created by liuwenzhuo on 2019/5/31.
 */

public class LoginHttp {
    private final OkGoWrapper mOkGoWrapper;
    private static String token;

    private LoginHttp() {
        mOkGoWrapper = OkGoWrapper.instance();
    }

    private static class InstanceHolder {
        private static final LoginHttp sInstance = new LoginHttp();
    }

    public static LoginHttp get() {
        return InstanceHolder.sInstance;
    }

    //登录
    public void login(String username, String password, Bean01Callback<LoginBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("username", username);
        httpParams.put("password", password);
        mOkGoWrapper.post(LoginUrL.URL, null, httpParams, LoginBean.class,
                callback);
    }

    //拨号
    public void makeCall(String mobile, Bean01Callback<CallBean> callback) {
      HttpParams httpParams = new HttpParams();
      httpParams.put("mobile", mobile);
      mOkGoWrapper.post(LoginUrL.makeCall, null, httpParams, CallBean.class,
        callback);
    }
    //通话记录
  public void callRecord(Bean01Callback<CallRecordBean> callback){
    HttpParams httpParams = new HttpParams();
    httpParams.put("limit", 50);
    httpParams.put("page", 1);
    mOkGoWrapper.post(LoginUrL.callRecord, null, httpParams, CallRecordBean.class,
      callback);
  }
  //退出登录
  public void exit(Bean01Callback<CallRecordBean> callback){
    HttpParams httpParams = new HttpParams();
    mOkGoWrapper.post(LoginUrL.exit, null, httpParams, CallRecordBean.class,
      callback);
  }
}
