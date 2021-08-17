package com.ctao.dialer.http;

public class CallBean {


  /**
   * code : 0
   * msg : AXB小号呼叫失败！
   * data : []
   * sub_msg : 因建党100周年的到来，我司将配合运营商通信业务管理要求，7月1日和7月2日将暂停线路，7月3号恢复使用，造成不便敬请谅解！
   */

  private int code;
  private String msg;
  private String sub_msg;
  private DataBean data;
  public DataBean getData() {
    return data;
  }

  public void setData(DataBean data) {
    this.data = data;
  }
  public static class DataBean {

    private String axb_number;
    private String mobile;

    public String getAxb_number() {
      return axb_number;
    }

    public void setAxb_number(String axb_number) {
      this.axb_number = axb_number;
    }

    public String getMobile() {
      return mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }
  }
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

  public String getSub_msg() {
    return sub_msg;
  }

  public void setSub_msg(String sub_msg) {
    this.sub_msg = sub_msg;
  }

}
