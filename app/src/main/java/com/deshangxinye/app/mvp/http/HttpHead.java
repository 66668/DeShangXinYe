package com.deshangxinye.app.mvp.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * 初始化后 貌似没用到
 */

public class HttpHead {
    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    private static final int NETWORK_TYPE_WIFI = -101;
    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /** Unknown network class. */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. */
    private static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. */
    private static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. */
    private static final int NETWORK_CLASS_4_G = 3;
    // 适配低版本手机
    /** Network type is unknown */
    private static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    private static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    private static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    private static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B */
    private static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0 */
    private static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A */
    private static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT */
    private static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    private static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    private static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    private static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    private static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B */
    private static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    private static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    private static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    private static final int NETWORK_TYPE_HSPAP = 15;

    private static Context context;

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context) {
        HttpHead.context = context;
    }


    public static String getHeader() {
        return "a/" + getVersionName() + "/" + getSystemCode() + "/" + getDeviceName() + "/" + getCurrentNetworkType();
    }

    /**
     * * 获取版本号
     * * @return 当前应用的版本号
     */
    private static String getVersionName() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return info.versionName;
    }

    /**
     * 获取系统版本号
     * @return
     */
    public static int getSystemCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备名
     * @return
     */
    @SuppressWarnings("static-access")
    public static String getDeviceName() {
        return new Build().MODEL;
    }

    private static String getUuid() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice = tm.getDeviceId().toString();
        tmSerial = "ANDROID_ID";
        androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID).toString();
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;

    }
    /**
     * 获取当前的网络情况
     * @return
     */
    public static String getCurrentNetworkType() {
        int networkClass = getNetworkClass();
        String type = "未知";
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = "无";
                break;
            case NETWORK_CLASS_WIFI:
                type = "Wi-Fi";
                break;
            case NETWORK_CLASS_2_G:
                type = "2G";
                break;
            case NETWORK_CLASS_3_G:
                type = "3G";
                break;
            case NETWORK_CLASS_4_G:
                type = "4G";
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = "未知";
                break;
        }
        return type;
    }

    private static int getNetworkClass() {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext()
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);

    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }
}
