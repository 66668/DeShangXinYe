package com.deshangxinye.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.deshangxinye.app.utils.SPUtil;

import java.util.Queue;

/**
 * 全局用户信息类
 */
public class MySettings {

    private static final String TAG = "MySettings";
    private static final String PREF_NAME = "sp_Deshangxinye";//sp包名

    private static MySettings singleton = new MySettings();

    // app status
    public boolean hasDbError;

    public String skinName = "default";

    //devkey 注册渠道key，用于记录注册量来自哪个推广程序
    public String devKey;

    // user login status
    public String loginname;
    public String username;
    public String password;
    public String token;
    public long expertTime;
    public long userId;
    public String phone;
    public String email;
    public int emailVerify;
    public long defaultCatId;
    public int totalCount;
    public boolean highVersion;
    public boolean syncOldDb;//是否同步老数据库
    public String version;
    public int phoneDialogShowCount = 0;

    public int isAutoLogin; // 0 NO; 1 YES
    public long projectLocalId;
    public boolean needShowLock;//锁屏参数（1）

    public boolean remindLockGroup = true;
    public boolean remindLockNote = true;

    //Auth
    public String accessToken;    //sina、QQ、google没有refreshToken直接使用accessToken
    public String sinaUid;
    public String uniqueId;
    public String refreshToken; //百度、360的使用refreshToken拿accessToken
    public int userType; // 0    轻笔记; 1   百度; 2  新浪微博;  3 qq;  4  谷歌;  5 360; 6   人人网; 8 天翼189

    public int tokenStatus = 0;    //0  无效；1 有效;(轻笔记token是否有效)
    public String tnAccessToken;    //轻笔记开放API的accessToken

    public long originalSyncTime = (long) 0;
    public long originalSyncShareNotesTime = (long) 0;
    public long originalSyncProjectTime = (long) 0;

    public int userStatus = 0; //	0 、老用户  	1、新用户
    public int appStartCount = 0; //用于统计程序的启动次数
    public int pictureCompressionMode = 1; //暂为：0,不压缩  1,压缩(默认). 为-1时,是第一次添加图片附件用于弹窗询问用户是否使用压缩模式
    public int bootViewSeen = 0x00; //做二进制数使用，1表示该界面已显示过引导，0表示否
    //第0位表示mian界面，第1位表示标签界面，第2位表示文件夹列表界面，第3位表示笔记列表界面，第4位表示混排界面,
    //第5位表示noteview界面;

    // settings for user
    public int sync; // 0,auto 1,wifi 2,manual
    public int noteListOrder; // 0 lastUpdate, 1 createTime, 2 title
    public int catListOrder;    //0 Default, 1 createTime, 2 name
    public String voice; // xiaoyan, xiaoyu
    public int speed; // 50
    public int volume; // 50
    public String searchWord;
    public boolean firstLaunch;
    public int showDialogType = 0x00;//第0位为bindingDialog，1表示不再提醒，0表示提醒, 第1位为首页的同步dialog

    public Queue<Integer> lockPattern;

    // below not save
    public Context appContext;
    public Activity topAct;

    public boolean isLogout = false;

    public boolean serviceRuning = false;

    private SharedPreferences sp = null;

    private MySettings() {
    }

    public static MySettings getInstance() {
        return singleton;
    }

    public boolean isInProject() {
        return projectLocalId > 0;
    }

    public boolean isCanLogin() {
        if (expertTime * 1000 > System.currentTimeMillis()) {
            return false;
        } else if (username.length() == 0
                || password.length() != 32 || token.length() == 0) {
            return false;
        }
        return true;
    }

    public boolean isLogin() {
        //7天 设置重新登陆
        if (expertTime * 1000 - System.currentTimeMillis() > 0 && loginname.length() != 0
                && token.length() != 0 && !isLogout) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTNTokenActivie() {
        if (tokenStatus == 1 && tnAccessToken != null && tnAccessToken.length() > 0) {
            if (userType > 0) {
                if (uniqueId != null && uniqueId.length() > 0)
                    return true;
            } else {
                return true;
            }
        }
        return false;
    }

    public void readPref() {
        hasDbError = SPUtil.getBoolean("hasDbError", false);
        originalSyncTime = SPUtil.getLong("originalSyncTime", 0);
    }

    public void savePref() {
        SPUtil.putLong("originalSyncTime",originalSyncTime);

    }

}
