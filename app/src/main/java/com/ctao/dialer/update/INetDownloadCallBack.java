package com.ctao.dialer.update;

import java.io.File;

public interface INetDownloadCallBack {
    void success(File apkFile);

    void progress(int progress);

    void failed(Throwable throwable);
}
