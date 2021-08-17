package com.ctao.dialer.update;

public interface INetCallBack {
    void success(String response);

    void failed(Throwable throwable);
}
