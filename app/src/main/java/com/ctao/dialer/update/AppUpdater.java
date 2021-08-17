package com.ctao.dialer.update;



public class AppUpdater {

    private static AppUpdater appUpdater = null;

    //默认为OkHttpManager
    private static INetManager netManager = new OkHttpNetManager();

    private AppUpdater() {
    }

    public static AppUpdater getInstance() {
        if (appUpdater == null) {
            appUpdater = new AppUpdater();
        }
        return appUpdater;
    }

    public INetManager getNetManager() {
        return netManager;
    }

    public void setNetManager(INetManager netManager) {
        AppUpdater.netManager = netManager;
    }
}
