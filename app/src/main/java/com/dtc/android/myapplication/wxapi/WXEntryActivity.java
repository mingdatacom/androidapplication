package com.dtc.android.myapplication.wxapi;


import com.dtc.android.myapplication.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private Button gotoBtn, regBtn, launchBtn, checkBtn;

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxa86249ef7e0f3da8";
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        Log.d(TAG, "send req to weixin req.openid " + req.openId);
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                Log.d(TAG, "got command message from weixin");
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                Log.d(TAG, "got show message from weixin");
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        Log.d(TAG, "got response from weixin code " + resp.errCode);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp authResponse = (SendAuth.Resp) resp;
            Log.d(TAG, "auth code " + authResponse.code);
        }
    }

}