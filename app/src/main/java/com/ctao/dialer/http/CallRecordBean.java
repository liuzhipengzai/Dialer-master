package com.ctao.dialer.http;

import java.util.List;

public class CallRecordBean {

  /**
   * code : 1
   * msg : 操作成功
   * data : [{"id":4182,"called_number":"13622850769","starttime":"2021-07-03 14:39:02"},{"id":4181,"called_number":"13622850769","starttime":"2021-07-03 14:38:29"},{"id":4180,"called_number":"18122836573","starttime":"2021-07-03 14:37:56"},{"id":4179,"called_number":"15220204497","starttime":"2021-07-03 09:34:32"},{"id":4178,"called_number":"15220204497","starttime":"2021-07-03 08:59:27"},{"id":10,"called_number":"18300076978","starttime":"2021-06-10 11:05:08"},{"id":6,"called_number":"19868115646","starttime":"2021-06-10 10:20:23"},{"id":5,"called_number":"18211344468","starttime":"2021-06-10 10:19:41"},{"id":3,"called_number":"19868115646","starttime":"2021-06-10 10:10:05"}]
   * total : 9
   */

  private int code;
  private String msg;
  private int total;
  private List<DataBean> data;

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

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public List<DataBean> getData() {
    return data;
  }

  public void setData(List<DataBean> data) {
    this.data = data;
  }

  public static class DataBean {
    /**
     * id : 4182
     * called_number : 13622850769
     * starttime : 2021-07-03 14:39:02
     */

    private int id;
    private String called_number;
    private String starttime;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getCalled_number() {
      return called_number;
    }

    public void setCalled_number(String called_number) {
      this.called_number = called_number;
    }

    public String getStarttime() {
      return starttime;
    }

    public void setStarttime(String starttime) {
      this.starttime = starttime;
    }
  }
}
