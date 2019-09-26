package com.deshangxinye.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.deshangxinye.app.BuildConfig;
import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.base.MyApplication;
import com.deshangxinye.app.base.MySettings;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Intent.CATEGORY_DEFAULT;

public class UiUtils {
    private static final String TAG = "TNUtilsUi";

    public static long getMonth(long milliseconds) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTimeInMillis(milliseconds);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        c.set(year, month, 0, 0, 0, 0);
        return c.getTimeInMillis() / 1000;
    }

    public static ProgressDialog progressDialog(Context aContext, int msgId) {
        ProgressDialog dialog = new ProgressDialog(aContext);
        dialog.setTitle("");
        dialog.setIndeterminate(true);
        dialog.setMessage(MyApplication.getInstance().getApplicationContext().getString(msgId));
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)// Search键
                {
                    return true;
                    // 如果true，说明此事件已被处理，将不会冒泡提交OnDismissListener ，这时将不会看到google
                    // search 界面
                    // return false;
                    // 如果是这样，如果注册的有OnDismissListener ，可以看到onDismiss事件会被触发
                }
                return false;
            }
        });
        return dialog;
    }

    public static void showToast(Object msg) {
        if (Integer.class.isInstance(msg)) {
            Toast t1 = Toast.makeText(MyApplication.getInstance().getApplicationContext(), (Integer) msg,
                    Toast.LENGTH_LONG);
            t1.show();
        } else if (String.class.isInstance(msg)) {
            Toast t1 = Toast.makeText(MyApplication.getInstance().getApplicationContext(), (String) msg,
                    Toast.LENGTH_LONG);
            t1.show();
        }
    }

    public static void showShortToast(Object msg) {
        if (Integer.class.isInstance(msg)) {
            Toast t1 = Toast.makeText(MyApplication.getInstance().getApplicationContext(), (Integer) msg,
                    Toast.LENGTH_SHORT);
            t1.show();
        } else if (String.class.isInstance(msg)) {
            Toast t1 = Toast.makeText(MyApplication.getInstance().getApplicationContext(), (String) msg,
                    Toast.LENGTH_SHORT);
            t1.show();
        }
    }


    /**
     * 分享到微信好友和朋友圈
     *
     * @param act
     * @param note
     * @param isCycle true表示朋友圈，false表示好友
     */
    public static void sendToWX(Activity act, String note, boolean isCycle) {
        IWXAPI api = WXAPIFactory.createWXAPI(act, BaseConst.WX_APP_ID, true);
        api.registerApp(BaseConst.WX_APP_ID);

        String text = note;
        text = TextUtils.isEmpty(text) ? "笔记无内容" : text;
        if (text.length() > 140) {
            text = text.substring(0, 140);
        }

        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = isCycle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }



    public static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }


    public static void hideKeyboard(Activity act, int viewId) {
        InputMethodManager imm = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(act.findViewById(viewId).getWindowToken(),
                0); // 隐藏软键盘
    }

    public static void showKeyBoard(Activity act, int viewId) {
        InputMethodManager imm = ((InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.showSoftInput(act.findViewById(viewId), 0);
    }

    public static void showKeyBoard(Activity act, View v, int viewId) {
        InputMethodManager imm = ((InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.showSoftInput(v.findViewById(viewId), 0);
    }


    public static void openFile(String filePath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        /* 设置intent的file与MimeType */
        Uri contentUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0+版本安全设置
            contentUri = FileProvider.getUriForFile(MyApplication.getInstance().getApplicationContext(), BuildConfig.APPLICATION_ID + ".FileProvider", new File(filePath));
        } else {//7.0-正常调用
            contentUri = Uri.fromFile(new File(filePath));
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        MySettings.getInstance().topAct.startActivity(intent);
    }


    public static void openFile(Context context, File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /* 设置intent的file与MimeType */
        Uri contentUri = null;
        //兼容7.0 (兼容8.0的设置在具体的act中)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0+版本安全设置
            contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file);
        } else {//7.0以下--正常调用
            contentUri = Uri.fromFile(file);
        }

        String type = getMIMEType(file);
        //添加读取权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        MLog.e("打开文件");
        //
        intent.setDataAndType(contentUri, type);//type
        context.startActivity(intent);

    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getInstance().startActivity(intent);
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };


    public static void setEnabledViews(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (Button.class.isInstance(view) || ImageButton.class.isInstance(view))
            view.setFocusable(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                setEnabledViews(group.getChildAt(idx), enabled);
            }
        }
    }

}
