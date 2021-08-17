package com.ctao.dialer.http;

/**
 * Created by liuwenzhuo on 2019/5/31.
 */

public interface LoginUrL {
  String URL = "http://caller.hbosw.com/api/user/login";
  String makeCall = "http://caller.hbosw.com/api/hbcall/makeCall";
  String callRecord = " http://caller.hbosw.com/api/hbcall/getHistoryList";
  String exit = " http://caller.hbosw.com/api/user/logout";
}
