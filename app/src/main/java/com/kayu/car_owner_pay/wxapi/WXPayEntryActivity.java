package com.kayu.car_owner_pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kayu.utils.LogUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("hm","WXPayEntryActivity");
        WXShare share = new WXShare(this);
        api = share
//                                .register()
                .getApi();

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            if (!api.handleIntent(getIntent(), this)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        LogUtil.e("hm","onNewIntent");
        setIntent(intent);
        if (!api.handleIntent(intent, this)) {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {

        Intent intent = new Intent(WXShare.ACTION_SHARE_RESPONSE);
//        if(Build.VERSION.SDK_INT >= 26){
//            intent.addFlags(0x01000000);
//        }
        if(baseResp instanceof SendAuth.Resp){
            final String code = ((SendAuth.Resp) baseResp).code;//需要转换一下才可以
            intent.setAction(WXShare.TYPE_LOGIN);
            intent.putExtra(WXShare.EXTRA_RESULT, code);
        }else {
            if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                intent.setAction(WXShare.TYPE_SHARE);

            } else if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                intent.setAction(WXShare.TYPE_PAY);
            }
            intent.putExtra(WXShare.EXTRA_RESULT, new WXShare.Response(baseResp));
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }
}
