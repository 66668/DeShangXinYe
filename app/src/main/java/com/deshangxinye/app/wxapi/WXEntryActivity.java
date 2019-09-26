package com.deshangxinye.app.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.deshangxinye.app.base.BaseAct;
import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.mvp.http.rxbus.RxBus;
import com.deshangxinye.app.mvp.http.rxbus.RxBusBaseMessage;
import com.deshangxinye.app.mvp.http.rxbus.RxCodeConstants;
import com.deshangxinye.app.mvp.listener.v.OnWchatListener;
import com.deshangxinye.app.mvp.p.LogPresenter;
import com.deshangxinye.app.utils.MLog;
import com.deshangxinye.app.utils.UiUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信登录 设置
 */
public class WXEntryActivity extends BaseAct implements IWXAPIEventHandler, OnWchatListener {
    private IWXAPI api;
    LogPresenter presenter;

    // 获取第一步的code后，请求以下链接获取access_token
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new LogPresenter(this, this);
        //接收到分享以及登录的intent传递handleIntent方法，处理结果

        api = WXAPIFactory.createWXAPI(this, BaseConst.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                MLog.e("微信--参数不合法，未被SDK处理，退出");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.transaction != null) {
                    finish();
                    Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                } else {
                    String code = ((SendAuth.Resp) resp).code;
                    /*
                     * 将你前面得到的AppID、AppSecret、code，拼接成URL 获取access_token等等的信息(微信)
                     */
                    //
                    presenter.getWchatToken(code);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //====================================微信登陆回调====================================

    @Override
    public void onWchatSuccess() {
        //需要回调登录界面 RxBus
        MLog.d("WXEntryActivity--onWchatSuccess");
        RxBus.getInstance().post(RxCodeConstants.WEChat_BACK_LOG, new RxBusBaseMessage());
        this.finish();
    }

    @Override
    public void onWchatFailed(String msg, Exception e) {
//        UiUtils.showToast(msg);
        this.finish();
    }
}
