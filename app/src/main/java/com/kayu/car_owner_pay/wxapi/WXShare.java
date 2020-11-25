package com.kayu.car_owner_pay.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kayu.utils.Constants;
import com.kayu.utils.FileUtil;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.StringUtil;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;

public class WXShare {

    public static final String ACTION_SHARE_RESPONSE = "action_wx_share_response";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_TYPE = "type";
    public static final String TYPE_LOGIN = "login";
    public static final String TYPE_SHARE = "share";
    public static final String TYPE_PAY = "pay";

    private final Context context;
    private final IWXAPI api;
    private OnResponseListener listener;
    private ResponseReceiver receiver;
    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;

    public WXShare(Context context) {
        api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID,false);
        this.context = context;
    }

    public WXShare register() {
        // 微信分享
        api.registerApp(Constants.WX_APP_ID);
        receiver = new ResponseReceiver();
        IntentFilter filter = new IntentFilter(ACTION_SHARE_RESPONSE);
        filter.addAction(WXShare.TYPE_LOGIN);
        filter.addAction(WXShare.TYPE_PAY);
        filter.addAction(WXShare.TYPE_SHARE);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
//        context.registerReceiver(receiver, filter);
        return this;
    }

    public void unregister() {
        try {
            api.unregisterApp();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ItemCallback callback;

    /**
     * 发起微信支付请求
     * @param wxPayBean 支付请求实体类
     * @param callback  支付结果回调
     */
    public void getWXPay(WxPayBean wxPayBean, ItemCallback callback) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
        } else {
            PayReq payRequest = new PayReq();
            payRequest.appId = Constants.WX_APP_ID;
            payRequest.partnerId = wxPayBean.body.partnerid;
            payRequest.prepayId = wxPayBean.body.perpayid;
            payRequest.packageValue = wxPayBean.body.packageX;//固定值
            payRequest.nonceStr = wxPayBean.body.noncestr;
            payRequest.timeStamp = String.valueOf(wxPayBean.body.timestamp);
            payRequest.sign = wxPayBean.body.sign;
            api.sendReq(payRequest);
            this.callback = callback;
        }
    }
    /**
     * 获取微信登录授权code
     * @return
     */
    public WXShare getAuth(ItemCallback callback){
        //发起登录请求

        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "com.kayu.courseapp";
            api.sendReq(req);
            this.callback = callback;
        }
        return this;
    }

    public WXShare shareText(int type, String text) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        //        msg.title = "Will be ignored";
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = mTargetScene;

        boolean result = api.sendReq(req);
        LogUtil.e("hm","text shared: " + result);
        return this;
    }

    public WXShare shareImg(int type, String filePath) {
        if (type == Constants.SHARED_TYPE1){
            mTargetScene = SendMessageToWX.Req.WXSceneSession;
        }else if (type == Constants.SHARED_TYPE2){
            mTargetScene = SendMessageToWX.Req.WXSceneTimeline;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            String tip = "文件不存在";
            Toast.makeText(context, tip + " path = " + filePath, Toast.LENGTH_LONG).show();
            return this;
        }

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(filePath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 200, true);
        bmp.recycle();
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);
        return this;
    }
    public WXShare shareImg(int type, Bitmap bitmap) {
        if (type == Constants.SHARED_TYPE1){
            mTargetScene = SendMessageToWX.Req.WXSceneSession;
        }else if (type == Constants.SHARED_TYPE2){
            mTargetScene = SendMessageToWX.Req.WXSceneTimeline;
        }

        WXImageObject imgObj = new WXImageObject();

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        bitmap.recycle();
        msg.thumbData = FileUtil.bmpToByteArray(scaledBitmap, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);
        return this;
    }


    public WXShare shareUrl(int flag, String url, String title, String descroption, String filePath){//初始化一个WXWebpageObject填写url          
        WXWebpageObject webpageObject =new WXWebpageObject();
        webpageObject.webpageUrl =url;
        //用WXWebpageObject对象初始化一个WXMediaMessage，天下标题，描述
        WXMediaMessage msg =new WXMediaMessage(webpageObject);
        msg.title =title;
        msg.description =descroption;
        //这块需要注意，图片的像素千万不要太大，不然的话会调不起来微信分享，
        if (!StringUtil.isEmpty(filePath)){
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);
            bmp.recycle();
            msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);
        }
        SendMessageToWX.Req req =new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message =msg;
        req.scene=flag==1? SendMessageToWX.Req.WXSceneSession: SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);return this;
    }



    public IWXAPI getApi() {
        return api;
    }

    public void setListener(OnResponseListener listener) {
        this.listener = listener;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e("hm","接收到广播");
            if (intent.getAction().equals(WXShare.TYPE_SHARE)){
                Response response = intent.getParcelableExtra(EXTRA_RESULT);
                LogUtil.e("hm","type: " + response.getType());
                LogUtil.e("hm","errCode: " + response.errCode);
                String result;
                if (listener != null) {
                    if (response.errCode == BaseResp.ErrCode.ERR_OK) {
                        listener.onSuccess();
                    } else if (response.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                        listener.onCancel();
                    } else {
                        switch (response.errCode) {
                            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                result = "发送被拒绝";
                                break;
                            case BaseResp.ErrCode.ERR_UNSUPPORT:
                                result = "不支持错误";
                                break;
                            default:
                                result = "发送返回";
                                break;
                        }
                        listener.onFail(result);
                    }
                }
            }else if (intent.getAction().equals(WXShare.TYPE_LOGIN)){
                String code = intent.getStringExtra(WXShare.EXTRA_RESULT);
                if (null != callback){
                    callback.onItemCallback(0,code);
                }
            } else if (intent.getAction().equals(WXShare.TYPE_PAY)) {
                Response response = intent.getParcelableExtra(EXTRA_RESULT);
                String result;
                if (listener != null) {
                    if (response.errCode == BaseResp.ErrCode.ERR_OK) {
                        listener.onSuccess();
                    } else if (response.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                        listener.onCancel();
                    } else {
                        result = "支付错误" + response.errCode;
                        listener.onFail(result);
                    }
                }
                if (null != callback) {
                    callback.onItemCallback(0,null);
                }
            }

        }
    }

    public static class Response extends BaseResp implements Parcelable {

        public int errCode;
        public String errStr;
        public String transaction;
        public String openId;

        private int type;
        private boolean checkResult;

        public Response(BaseResp baseResp) {
            errCode = baseResp.errCode;
            errStr = baseResp.errStr;
            transaction = baseResp.transaction;
            openId = baseResp.openId;
            type = baseResp.getType();
            checkResult = baseResp.checkArgs();
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public boolean checkArgs() {
            return checkResult;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.errCode);
            dest.writeString(this.errStr);
            dest.writeString(this.transaction);
            dest.writeString(this.openId);
            dest.writeInt(this.type);
            dest.writeByte(this.checkResult ? (byte) 1 : (byte) 0);
        }

        protected Response(Parcel in) {
            this.errCode = in.readInt();
            this.errStr = in.readString();
            this.transaction = in.readString();
            this.openId = in.readString();
            this.type = in.readInt();
            this.checkResult = in.readByte() != 0;
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel source) {
                return new Response(source);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };
    }
}
