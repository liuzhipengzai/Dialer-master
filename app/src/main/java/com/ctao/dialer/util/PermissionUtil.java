package com.ctao.dialer.util;

import android.content.Context;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;


public class PermissionUtil {

  private static PermissionUtil inistance;

  public static PermissionUtil getInstance() {
    if (inistance == null) {
      synchronized (PermissionUtil.class) {
        if (inistance == null) {
          inistance = new PermissionUtil();
        }
      }
    }
    return inistance;
  }

  /**
   * 检查权限
   *
   * @param canSkip    是否能跳过
   * @param permission 要申请的权限
   */
  public void checkPermission(final Context context, final boolean canSkip, final PermissionCallBack permissionCallBack, final String... permission) {
    AndPermission.with(context)
      .runtime()
      .permission(permission)
      .onGranted(new Action<List<String>>() {
        @Override
        public void onAction(List<String> data) {
          permissionCallBack.onSuccess();
        }
      })
      .onDenied(new Action<List<String>>() {
        @Override
        public void onAction(List<String> data) {

          //ToastUtil.show(context,"拒绝应用的权限有可能影响到相关用户体验");
          permissionCallBack.onFail();
//                        if (canSkip) {
//                            permissionCallBack.onSuccess();
//                            return;
//                        }
          // 这些权限被用户总是拒绝。AndPermission.hasAlwaysDeniedPermission(BaseActivity.this, data)
          //showSettingDialog(context, permissionCallBack,permission);
        }
      })
      .start();
  }


  public interface PermissionCallBack {
    void onSuccess();

    void onFail();
  }


}
