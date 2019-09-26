package com.deshangxinye.app.mvp.http.fileprogress;

/**
 * 查看文件上传下载进度 下载进度回调
 */
public interface FileProgressListener {

    void onFileProgressing(int progress);

}
