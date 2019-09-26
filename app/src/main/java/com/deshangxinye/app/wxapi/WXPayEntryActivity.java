package com.deshangxinye.app.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.deshangxinye.app.base.BaseAct;
import com.deshangxinye.app.base.BaseConst;
import com.deshangxinye.app.utils.MLog;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信登录 设置
 */
public class WXPayEntryActivity extends BaseAct implements IWXAPIEventHandler {
    private IWXAPI api;

    //回调中errCode值
// 0    成功  展示成功页面
//-1    错误  可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//-2    用户取消    无需处理。发生场景：用户不支付了，点击取消，返回APP。

    // 获取第一步的code后，请求以下链接获取access_token
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("支付结果");
        setContentView(tv);
        api = WXAPIFactory.createWXAPI(this, BaseConst.WX_APP_ID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        MLog.d(TAG, "支付onResp--" + resp.errCode + resp.errStr);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("提示");
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();
            //这里肯定不能是像上面的DEMO一样弹出对话框了，而是通知我们发起支付调用的页面
            //然后及时finish掉这个页面，贴个伪代码：
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
