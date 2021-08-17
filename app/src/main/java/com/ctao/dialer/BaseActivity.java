package com.ctao.dialer;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import cn.dlc.commonlibrary.ui.base.BaseCommonActivity;
import cn.dlc.commonlibrary.ui.dialog.WaitingDialog;

public abstract class BaseActivity extends BaseCommonActivity {
    WaitingDialog mWaitingDialog;
    //需要处理全屏逻辑之类的操作,在子类重写beforeSetContentView方法做处理
    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        //setTranslucentStatus();
    }

    //需要在每个Activity处理逻辑,直接在onCreate方法处理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initEven();
    }

    //处理每个Activity都执行的逻辑
    private void initEven() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));//设置状态栏颜色
//            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏，并且不显示字体

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏文字颜色为暗色
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color));
//            int vis = getWindow().getDecorView().getSystemUiVisibility();
//            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
//            getWindow().getDecorView().setSystemUiVisibility(vis);
        }
    }

    /*****************************************************************************************/
    /**
     * 显示等待对话框
     *
     * @param text
     * @param cancelable
     */
    public void showWaitingDialog(String text, boolean cancelable) {
        if (mWaitingDialog == null) {
            mWaitingDialog = WaitingDialog.newDialog(this).setMessage(text);
        }
        if (mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
        mWaitingDialog.setCancelable(cancelable);
        mWaitingDialog.show();
    }

    public void dismissWaitingDialog() {
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }

}
