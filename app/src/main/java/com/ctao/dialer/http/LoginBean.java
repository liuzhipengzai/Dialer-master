package com.ctao.dialer.http;

/**
 * 页面:尹庆祥  on  2017/10/16.
 * 对接口:
 * 作用:
 */

public class LoginBean {


  /**
   * code : 1
   * msg : 登录成功
   * data : {"id":1,"username":"test","realname":"","loginip":"116.4.11.169","prevtime":"2021-07-02 16:33:10","logintime":"2021-07-02 16:39:18","status":"正常"}
   */

  private int code;
  private String msg;
  private DataBean data;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public DataBean getData() {
    return data;
  }

  public void setData(DataBean data) {
    this.data = data;
  }

  public static class DataBean {
    /**
     * id : 1
     * username : test
     * realname :
     * loginip : 116.4.11.169
     * prevtime : 2021-07-02 16:33:10
     * logintime : 2021-07-02 16:39:18
     * status : 正常
     */

    private int id;
    private String username;
    private String realname;
    private String loginip;
    private String prevtime;
    private String logintime;
    private String status;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getRealname() {
      return realname;
    }

    public void setRealname(String realname) {
      this.realname = realname;
    }

    public String getLoginip() {
      return loginip;
    }

    public void setLoginip(String loginip) {
      this.loginip = loginip;
    }

    public String getPrevtime() {
      return prevtime;
    }

    public void setPrevtime(String prevtime) {
      this.prevtime = prevtime;
    }

    public String getLogintime() {
      return logintime;
    }

    public void setLogintime(String logintime) {
      this.logintime = logintime;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
  }
}
