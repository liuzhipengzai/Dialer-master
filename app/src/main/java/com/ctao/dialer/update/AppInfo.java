package com.ctao.dialer.update;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 * "title": "4.5.0更新啦！",
 * "content": "1. 优化了阅读体验；\n2. 上线了 hyman 的课程；\n3. 修复了一些已知问题。",
 * "url": "http://59.110.162.30/v450_imooc_updater.apk",
 * "md5": "14480fc08932105d55b9217c6d2fb90b",
 * "versionCode": "450"
 * }
 */
public class AppInfo implements Parcelable {

    private int code;
    private String msg;
    private DataBean data;

    public static class DataBean{
        private String oldversion;
        private String newversion;
        private String downloadurl;
        private String content;
        private int enforce;//强制更新
    }

    private String title;
    private String content;
    private String url;
    private String md5;
    private String versionCode = "2";


    public String oldversion;
    public String newversion;
    public String downloadurl;
    public int enforce;//强制更新
    public AppInfo() {
    }
    public static AppInfo parse1(String response) {
        JSONObject object = null;
        AppInfo appInfo = null;
        try {
            object = new JSONObject(response);
            /**
             * 为什么要使用jsonObject.optString， 不使用jsonObject.getString
             * 因为jsonObject.optString获取null不会报错
             */
            String data = object.optString("data", null);
            if (data != null || data.length() > 0) {
                JSONObject jsonObject = new JSONObject(data);
                String newversion = jsonObject.optString("newversion", null);
                String content = jsonObject.optString("content", null);
                int enforce = jsonObject.optInt("enforce", 0);
                String downloadurl = jsonObject.optString("downloadurl", null);
                // 日志打印结果：
                Log.e("TAG", "解析的结果：newversion:" + newversion + " content:" + content + " enforce:" + enforce + "downloadurl:" + downloadurl);
                appInfo = new AppInfo("",content,"","","",newversion,downloadurl,enforce);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    public static AppInfo parse(String response) {
        JSONObject object = null;
        AppInfo appInfo = null;
        try {
            object = new JSONObject(response);
            String title = object.optString("title", "");
            String content = object.optString("content", "");
            String url = object.optString("url", "");
            String md5 = object.optString("md5", "");
            String versionCode = object.optString("versionCode", "");
            appInfo = new AppInfo(title, content, url, md5, versionCode,"","",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    public AppInfo(String title, String content, String url, String md5, String versionCode,
                   String newversion,String downloadurl,int enforce) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.md5 = md5;
        this.versionCode = versionCode;
        this.oldversion = oldversion;
        this.newversion = newversion;
        this.downloadurl = downloadurl;
        this.enforce = enforce;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", versionCode='" + versionCode + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
