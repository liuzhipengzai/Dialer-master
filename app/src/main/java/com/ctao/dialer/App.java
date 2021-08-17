package com.ctao.dialer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;


import com.ctao.dialer.util.MyErrorTranslator;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;


import cn.dlc.commonlibrary.BuildConfig;
import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import cn.dlc.commonlibrary.okgo.exception.ApiException;
import cn.dlc.commonlibrary.okgo.logger.JsonRequestLogger;
import cn.dlc.commonlibrary.utils.PrefUtil;
import cn.dlc.commonlibrary.utils.ResUtil;
import cn.dlc.commonlibrary.utils.ScreenUtil;
import cn.dlc.commonlibrary.utils.SystemUtil;
import cn.dlc.commonlibrary.utils.ToastUtil;
import okhttp3.OkHttpClient;

public class App extends Application {
  private static App instance;

  public static App instance() {
    return instance;
  }

  static {
    //设置全局的Header构建器
    SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
      @Override
      public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
        layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
        return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
      }
    });
    //设置全局的Footer构建器
    SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
      @Override
      public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
        //指定为经典Footer，默认是 BallPulseFooter
        return new ClassicsFooter(context).setDrawableSize(20);
      }
    });
  }
  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;

    initUtil();
  }

  private void initUtil() {
    if (SystemUtil.isMainProcess(this)) {
      ScreenUtil.init(this); // 获取屏幕尺寸
      ResUtil.init(this); // 资源
      PrefUtil.init(this); // SharedPreference
      // 网络
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
      OkGoWrapper.initOkGo(this, builder.build());
      OkGoWrapper.instance()
        // 错误信息再格式化
        .setErrorTranslator(new MyErrorTranslator())
        // 拦截网络错误，一般是登录过期啥的
        .setErrorInterceptor(tr -> {
          if (tr instanceof ApiException) {
            ApiException ex = (ApiException) tr;
            if (ex.getCode() == 9) {
              ToastUtil.showOne(getApplicationContext(), "登录信息已过期或已在其它设备登录,请重新登录!");
              // 登录信息过期，请重新登录
//              UserHelper.UserInfo info = UserHelper.get().loadUserInfo();
//              info.reset();
              LoginActivity.startAct((Activity) getApplicationContext(), 2);
              return true;
            }
          }
          return false;
        })
        // 打印网络访问日志的
        .setRequestLogger(new JsonRequestLogger(BuildConfig.DEBUG, 30));
    }
  }
}
