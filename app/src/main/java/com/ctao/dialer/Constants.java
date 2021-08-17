package com.ctao.dialer;


import com.yanzhenjie.permission.Permission;

public class Constants {
  //腾讯Bugly
  public static String BuglyAppId = "3bb9cd84b8"; //Bugly的AppId
  //权限
  public static String[] Permissions = {Permission.CALL_PHONE,Permission.WRITE_EXTERNAL_STORAGE};
  // 存储
  public static final String USERINFO = "userInfo";
  public static final String ACCOUNT = "account";
  public static final String PWD = "password";
  public static final String ROOM = "room";
  public static final String AUTO_LOGIN = "auto_login";
  public static final String LOGOUT = "logout";
  public static final String ICON_URL = "icon_url";

  public static final String CHAT_INFO = "chatInfo";
  public static final String LOGIN_USER = "LOGIN_USER";

  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String APP_VERSION_URL = "http://hbcall.hbv5.net/api/apppublish/checkversion";
  public static final String TYPE = "type";
}
