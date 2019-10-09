package com.deshangxinye.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.deshangxinye.app.base.BaseAct;
import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.bean.OrederSendInfo;
import com.deshangxinye.app.bean.PrepayIdInfo;
import com.deshangxinye.app.mvp.http.rxbus.RxBus;
import com.deshangxinye.app.mvp.http.rxbus.RxBusBaseMessage;
import com.deshangxinye.app.mvp.http.rxbus.RxCodeConstants;
import com.deshangxinye.app.utils.CommonUtils;
import com.deshangxinye.app.utils.MLog;
import com.deshangxinye.app.utils.NetWorkFactory;
import com.deshangxinye.app.utils.SPUtil;
import com.deshangxinye.app.utils.UiUtils;
import com.deshangxinye.app.utils.WXpayUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.thoughtworks.xstream.XStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseAct {
    private WebView webView;
    //添加header
    Map<String, String> header = new HashMap<>();

    //微信
    private static IWXAPI WXapi;

    PrepayIdInfo bean;//预支付

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        initRxBus();
        //加载路径
        initWebView();

        webView.loadUrl(BaseConst.BASE_URL, header);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //去除Rxbus的监听
        if (null != compositeDisposable && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MLog.d("登录返回结果处理" + requestCode + "--" + resultCode);

    }

    private void initWebView() {
        //添加header
        header.put("dcapp", "android");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);                       //可执行js

        webSettings.setDefaultTextEncodingName("UTF-8");              //设置默认的文本编码名称，以便在解码html页面时使用
        webSettings.setAllowContentAccess(true);                      //启动或禁用WebView内的内容URL访问
        webSettings.setAppCacheEnabled(false);                        //设置是否应该启用应用程序缓存api
        webSettings.setBuiltInZoomControls(false);                    //设置WebView是否应该使用其内置的缩放机制
        webSettings.setUseWideViewPort(true);                         //设置WebView是否应该支持viewport 调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);                    //设置WebView是否使用预览模式加载界面。
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);          //重写缓存的使用方式
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);   //告知js自动打开窗口
        webSettings.setLoadsImagesAutomatically(true);                //设置WebView是否应该载入图像资源
        webSettings.setAllowFileAccess(true);                         //启用或禁用WebView内的文件访问
        webSettings.setDomStorageEnabled(true);                       //设置是否启用了DOM存储API,默认为false
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);//开启硬件加速
        //使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                MLog.d("shouldOverrideUrlLoading--url=" + url);
                view.loadUrl(url, header);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

//            @Nullable
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                MLog.d("shouldInterceptRequest");
//                String url = request.getUrl().toString();
//                return getNewResponse(url, request.getRequestHeaders());
//            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        //硬件加速
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //处理js
        webView.addJavascriptInterface(new JSInterface(), "android");//设置js接口 h5的统一name

    }

    /**
     * js调用android代码
     * android4.2以后，任何为JS暴露的接口，都需要加@JavascriptInterface
     */
    private class JSInterface {

        //微信登陆
        @JavascriptInterface
        public void wxLogin() {
            if (CommonUtils.isNetWork()) {
                MLog.d("wxLogin");
                WXapi = WXAPIFactory.createWXAPI(MainActivity.this, BaseConst.WX_APP_ID, true);
                WXapi.registerApp(BaseConst.WX_APP_ID);
                if (!WXapi.isWXAppInstalled()) {
                    UiUtils.showToast("请先安装微信app");
                } else {
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_demo";
                    WXapi.sendReq(req);
                }

            } else {
                UiUtils.showToast(R.string.alert_Net_NotWork);
            }

        }

        /**
         * 微信分享
         */
        @JavascriptInterface
        public void appNativeShare(int type, String wx_share_title, String wx_share_url, String wx_share_imgurl, String wx_share_desc) {
            try {
                MLog.d("type=" + type + "--wx_share_desc=" + wx_share_desc + "--wx_share_imgurl=" + wx_share_imgurl + "--wx_share_title=" + wx_share_title + "--wx_share_url=" + wx_share_url);
                //
                WXapi = WXAPIFactory.createWXAPI(MainActivity.this, BaseConst.WX_APP_ID, true);
                WXapi.registerApp(BaseConst.WX_APP_ID);

                shareUrlToWx(wx_share_url, wx_share_title, wx_share_desc, wx_share_imgurl, type);

            } catch (Exception e) {
                MLog.d("微信分享" + e.toString());
                e.printStackTrace();
            }
        }

        /**
         * 微信支付
         *
         * @param order_type   订单类型 1-帖子，2-其他充
         * @param type         支付类型 1-微信，2-支付宝
         * @param out_trade_no 订单号
         * @param total_fee    金额元
         */
        @JavascriptInterface
        public void appPayment(int order_type, int type, String out_trade_no, String total_fee) {
            if (type == 1) {
                bean = null;
                //生成签名参数
                OrederSendInfo sendInfo = new OrederSendInfo(
                        BaseConst.WX_APP_ID,
                        BaseConst.MCH_ID,
                        WXpayUtils.genNonceStr(),
                        "德尚微信支付",
                        out_trade_no,//订单号
                        WXpayUtils.getMoney(total_fee),//金额元
                        order_type);

                NetWorkFactory.UnfiedOrder(sendInfo, new NetWorkFactory.Listerner() {
                    @Override
                    public void Success(String data) {
                        MLog.d("生成预支付Id成功--" + data);
                        /**
                         * data返回值
                         * <xml><return_code>SUCCESS</return_code>
                         *     <return_msg>OK</return_msg>
                         *     <appid>wx8a8a1773f1c76165</appid>
                         *     <mch_id>1511698191</mch_id>
                         *     <nonce_str>ETjtvvYVHHXeh0dU</nonce_str>
                         *     <sign>1EAFC356D57CDA882EC2E57800CC498C</sign>
                         *     <result_code>SUCCESS</result_code>
                         *     <prepay_id>wx2516265864510329f9d06cd11848148800</prepay_id>
                         *     <trade_type>APP</trade_type>
                         *     </xml>
                         */
                        if (data.contains("SUCCESS")) {
                            XStream stream = new XStream();
                            stream.processAnnotations(PrepayIdInfo.class);
                            bean = (PrepayIdInfo) stream.fromXML(data);
                            //调起支付界面
                            WXpayUtils.Pay(bean);
                        }
                    }

                    @Override
                    public void Faiulre(String data) {
                    }
                });
            } else {
                UiUtils.showToast("app暂不支持支付宝");
            }
        }

    }

    //==================================================================================

    //==================================================================================

    /**
     * 分享链接
     *
     * @param url
     * @param title
     * @param desc
     * @param iconUrl
     * @param sceneType
     */
    public void shareUrlToWx(String url, String title, String desc, final String iconUrl, int sceneType) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = desc;
        Bitmap thumbBmp = GetLocalOrNetBitmap(iconUrl);
        //小图标
        msg.thumbData = bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //分享类型：会话 WXSceneSession /朋友圈 WXSceneTimeline
        if (sceneType == 1) {
            req.scene = SendMessageToWX.Req.WXSceneSession;//
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;//
        }
        WXapi.sendReq(req);

    }

    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 把网络资源图片转化成bitmap
     *
     * @param url 网络资源图片
     * @return Bitmap
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }


    public static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }


    //点击回退按钮不是退出应用程序，而是返回上一个页面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //
    /**
     * 使用RxBus 实现微信登录信息返回
     * home页跳转到new页
     */
    /**
     * 使用RxBus 实现fragment之间的跳转
     * home页跳转到new页
     */
    CompositeDisposable compositeDisposable;

    private void initRxBus() {
        compositeDisposable = new CompositeDisposable();
        RxBus.getInstance().toObservable(RxCodeConstants.WEChat_BACK_LOG, RxBusBaseMessage.class)
                .subscribe(new Observer<RxBusBaseMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(RxBusBaseMessage rxBusBaseMessage) {
                        final String unionid = SPUtil.getString("unionid", "");
                        final String openid = SPUtil.getString("openid", "");
                        final String access_token = SPUtil.getString("access_token", "");
                        final String refresh_token = SPUtil.getString("refresh_token", "");
                        final String nickName = SPUtil.getString("nickName", "");
                        final String avatar = SPUtil.getString("avatar", "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //android调用js
//                                String params = openid + " , " + nickName + " , " + avatar;
                                String params = '"' + openid + '"' + "," + '"' + nickName + '"' + "," + '"' + avatar + '"';

                                MLog.d("android调用js--wxLoginSuccess--" + params);
                                //方式1
                                webView.loadUrl("javascript:wxLoginSuccess(" + params + ")");

                                //方式2
//                                webView.evaluateJavascript("javascript:wxLoginSuccess(" + params + ")", new ValueCallback<String>() {
////                                    @Override
////                                    public void onReceiveValue(String value) {
////                                    }
////                                });
                                //清空登陆缓存
                                SPUtil.putString("unionid", "");
                                SPUtil.putString("openid", "");
                                SPUtil.putString("access_token", "");
                                SPUtil.putString("refresh_token", "");
                                SPUtil.putString("nickName", "");
                                SPUtil.putString("avatar", "");
                            }
                        });

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


//    private WebResourceResponse getNewResponse(String url, Map<String, String> headers) {
//
//        try {
//            OkHttpClient httpClient = new OkHttpClient();
//
//            Request.Builder builder = new Request.Builder()
//                    .url(url.trim())
//                    .addHeader("dcapp", "android");
//
//            Set<String> keySet = headers.keySet();
//            for (String key : keySet) {
//                builder.addHeader(key, headers.get(key));
//            }
//
//            Request request = builder.build();
//
//            final Response response = httpClient.newCall(request).execute();
//
//            String conentType = response.header("Content-Type", response.body().contentType().type());
//            String temp = conentType.toLowerCase();
//            if (temp.contains("charset=utf-8")) {
//                conentType = conentType.replaceAll("(?i)" + "charset=utf-8", "");//不区分大小写的替换
//            }
//            if (conentType.contains(";")) {
//                conentType = conentType.replaceAll(";", "");
//                conentType = conentType.trim();
//            }
//
//            return new WebResourceResponse(
//                    conentType,
//                    response.header("Content-Encoding", "utf-8"),
//                    response.body().byteStream()
//            );
//
//        } catch (Exception e) {
//            return null;
//        }
//
//    }


}
