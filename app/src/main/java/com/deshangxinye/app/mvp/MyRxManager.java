package com.deshangxinye.app.mvp;


/**
 * 请求管理，主动关闭网络请求
 */
public class MyRxManager {
    private static String TAG = "Rx";
    private static MyRxManager sInstance = new MyRxManager();

    private boolean isSyncing;//是否在主页同步中
    private boolean isFolderSyncing;//是否在文件夹同步中

    public static MyRxManager getInstance() {
        return sInstance;
    }

    private MyRxManager() {

    }

    /**
     * 是否在请求中（目前只用于主页同步中，单个接口不调用）
     *
     * @return
     */
    public boolean isSyncing() {
        return isSyncing;
    }

    /**
     * 同步中
     */
    public void setSyncing(boolean b) {
        this.isSyncing = b;
    }

    /**
     * 文件夹是否在同步
     *
     * @return
     */
    public boolean isFolderSyncing() {
        return isFolderSyncing;
    }

    public void setFolderSyncing(boolean folderSyncing) {
        isFolderSyncing = folderSyncing;
    }
}
