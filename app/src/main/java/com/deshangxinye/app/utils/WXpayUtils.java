package com.deshangxinye.app.utils;


import android.widget.Toast;

import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.base.MyApplication;
import com.deshangxinye.app.bean.OrederSendInfo;
import com.deshangxinye.app.bean.PrepayIdInfo;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 微信支付封装
 */

public class WXpayUtils {

    private static IWXAPI iwxapi;
    private static PayReq req;

    public static IWXAPI getWXAPI() {
        if (iwxapi == null) {
            //通过WXAPIFactory创建IWAPI实例
            iwxapi = WXAPIFactory.createWXAPI(MyApplication.getInstance().getApplicationContext(), null);
            //将应用的appid注册到微信
            iwxapi.registerApp(BaseConst.WX_APP_ID);
            req = new PayReq();
        }
        return iwxapi;
    }

    //生成随机字符串 Nonce Str
    public static String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }


    //获得时间戳
    private static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    //生成预支付随机签名
    public static String genSign(OrederSendInfo info) {
        StringBuffer sb = new StringBuffer(info.toString());
        //拼接密钥
        sb.append("key=");
        sb.append(BaseConst.API_KEY);
        MLog.d("StringA=" + sb.toString());
//        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        String appSign = MD5.MD5Encode(sb.toString(), "UTF-8");
        return appSign;
    }

    //生成支付随机签名
    private static String genAppSign(List<WxOkHttp.Param> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).key);
            sb.append('=');
            sb.append(params.get(i).value);
            sb.append('&');
        }
        //拼接密钥
        sb.append("key=");
        sb.append(BaseConst.API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign.toUpperCase();
    }

    //生成支付参数
    private static void genPayReq(String prepayid) {
        req.appId = BaseConst.WX_APP_ID;
        req.partnerId = BaseConst.MCH_ID;
        req.prepayId = prepayid;
        req.packageValue = "Sign=" + prepayid;
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<WxOkHttp.Param> signParams = new LinkedList<WxOkHttp.Param>();
        signParams.add(new WxOkHttp.Param("appid", req.appId));
        signParams.add(new WxOkHttp.Param("noncestr", req.nonceStr));
        signParams.add(new WxOkHttp.Param("package", req.packageValue));
        signParams.add(new WxOkHttp.Param("partnerid", req.partnerId));
        signParams.add(new WxOkHttp.Param("prepayid", req.prepayId));
        signParams.add(new WxOkHttp.Param("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);
    }

    public static void Pay(PrepayIdInfo bean) {
        if (judgeCanGo()) {
            MLog.d("Pay--" + bean.toString());
            genPayReq(bean.getPrepay_id());
            iwxapi.sendReq(req);
        }
    }

    private static boolean judgeCanGo() {
        getWXAPI();//初始化
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "请先安装微信应用", Toast.LENGTH_SHORT).show();
            return false;
        }
//        else if (!iwxapi.isWXAppSupportAPI()) {
//            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "请先更新微信应用", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    /**
     * 支付金额 元转换成分
     *
     * @param amount
     * @return
     */
    public static String getMoney(String amount) {
        if (amount == null) {
            return "";
        }
        // 金额转化为分为单位
        // 处理包含, ￥ 或者$的金额
        String currency = amount.replaceAll("\\$|\\￥|\\,", "");
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }
}
