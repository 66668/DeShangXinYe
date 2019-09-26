package com.deshangxinye.app.utils;

import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.bean.OrederSendInfo;
import com.thoughtworks.xstream.XStream;

/**
 * Created by xmg on 2016/12/5.
 */

public class NetWorkFactory {
    public interface Listerner {
        void Success(String data);

        void Faiulre(String data);
    }

    /**
     * 本地模拟 统一下单 生成微信预支付Id 这一步放在服务器端生成
     *
     * @param orederSendInfo
     * @param listerner
     */
    public static void UnfiedOrder(OrederSendInfo orederSendInfo, final Listerner listerner) {
        //生成sign签名
        String sign = WXpayUtils.genSign(orederSendInfo);

        //生成所需参数，为xml格式
        orederSendInfo.setSign(sign.toUpperCase());
        XStream xstream = new XStream();
        xstream.alias("xml", OrederSendInfo.class);
        final String xml = xstream.toXML(orederSendInfo).replaceAll("__", "_");
        MLog.d("xml源字符串--" + xml);
        //调起接口，获取预支付ID
        WxOkHttp.ResultCallback<String> resultCallback = new WxOkHttp.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                String data = response;
                data = data.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");
                listerner.Success(data);
            }

            @Override
            public void onFailure(Exception e) {
                listerner.Faiulre(e.toString());
            }
        };

        WxOkHttp.post(BaseConst.UNIFIED_ORDER, resultCallback, xml);
    }
}
