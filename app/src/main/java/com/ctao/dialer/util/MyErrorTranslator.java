package com.ctao.dialer.util;

import com.google.gson.JsonParseException;
import com.lzy.okgo.exception.HttpException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import cn.dlc.commonlibrary.okgo.exception.ApiException;
import cn.dlc.commonlibrary.okgo.translator.ErrorTranslator;

/**
 * Created by yinqingxiang  on  2018/8/4.
 */
public class MyErrorTranslator implements ErrorTranslator {

    @Override
    public String translate(Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof HttpException) {
            return "服务器异常";
        } else if (throwable instanceof JsonParseException) {
            // json解析错误，一般就是接口改了
            return "网络接口异常";
        } else if (throwable instanceof ApiException) {
            return throwable.getMessage();
        } else if (throwable instanceof UnknownHostException) {
            return "请检查网络是否打开!";
        } else if (throwable instanceof SocketTimeoutException) {
            return "请求超时";
        }else if (throwable instanceof TimeoutException) {
            return "请求超时";
        } else {
            return throwable.getMessage();
        }
    }

}
