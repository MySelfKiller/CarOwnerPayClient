package com.kayu.car_owner_pay.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.R;
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
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
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
//    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;

    public WXShare(Context context) {
        api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, false);
        this.context = context;
    }

    public WXShare register() {
        // ????????????
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
     * ????????????????????????
     *
     * @param wxPayBean ?????????????????????
     * @param callback  ??????????????????
     */
    public void getWXPay(WxPayBean wxPayBean, ItemCallback callback) {
        if (!api.isWXAppInstalled()) {
            ToastUtils.show("????????????????????????????????????");
        } else {
            PayReq payRequest = new PayReq();
            payRequest.appId = Constants.WX_APP_ID;
            payRequest.partnerId = wxPayBean.body.partnerid;
            payRequest.prepayId = wxPayBean.body.perpayid;
            payRequest.packageValue = wxPayBean.body.packageX;//?????????
            payRequest.nonceStr = wxPayBean.body.noncestr;
            payRequest.timeStamp = String.valueOf(wxPayBean.body.timestamp);
            payRequest.sign = wxPayBean.body.sign;
            api.sendReq(payRequest);
            this.callback = callback;
        }
    }

    /**
     * ????????????????????????code
     *
     * @return
     */
    public WXShare getAuth(ItemCallback callback) {
        //??????????????????

        if (!api.isWXAppInstalled()) {
            ToastUtils.show("????????????????????????????????????");
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "com.kayu.car_owner_pay";
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
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        boolean result = api.sendReq(req);
        LogUtil.e("hm", "text shared: " + result);
        return this;
    }

    public WXShare shareImg(int type, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            String tip = "???????????????";
            ToastUtils.show(tip + " path = " + filePath);
            return this;
        }

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(filePath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 150, true);
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        return this;
    }

    public WXShare shareImg(int type, Bitmap bitmap,String title, String descroption) {
        WXImageObject imgObj = new WXImageObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        imgObj.imageData = datas;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        msg.title = title;
        msg.description = descroption;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 150, true);
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        return this;
    }

    public WXShare shareMusic(int type, String url, String title, String descroption) {
//???????????????WXMusicObject?????????url
        WXMusicObject music = new WXMusicObject();
        music.musicUrl = url;

//??? WXMusicObject ????????????????????? WXMediaMessage ??????
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = descroption;
        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//?????????????????????
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);
//????????????Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
//        req.userOpenId = getOpenId();
//??????api??????????????????????????????
        api.sendReq(req);
        return this;
    }

    public WXShare shareVideo(int type, String url, String title, String descroption){
//???????????????WXVideoObject?????????url
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = url;

//??? WXVideoObject ????????????????????? WXMediaMessage ??????
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = title;
        msg.description = descroption;
        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        msg.thumbData =FileUtil.bmpToByteArray(thumbBmp,true);
//        msg.setThumbImage(null);
//????????????Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message =msg;
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
//        req.userOpenId = getOpenId();

//??????api??????????????????????????????
        api.sendReq(req);
        return this;
    }


    public WXShare shareUrl(int type, String url, String title, String descroption, String filePath) {//???????????????WXWebpageObject??????url?? ?? ?? ???? ??
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;
        //???WXWebpageObject?????????????????????WXMediaMessage????????????????????????
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = descroption;
        //???????????????????????????????????????????????????????????????????????????????????????????????????
        if (!StringUtil.isEmpty(filePath)) {
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);
            msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);
        }else {
            Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        return this;
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
            LogUtil.e("hm", "???????????????");
            if (intent.getAction().equals(WXShare.TYPE_SHARE)) {
                Response response = intent.getParcelableExtra(EXTRA_RESULT);
                LogUtil.e("hm", "type: " + response.getType());
                LogUtil.e("hm", "errCode: " + response.errCode);
                String result;
                if (listener != null) {
                    if (response.errCode == BaseResp.ErrCode.ERR_OK) {
                        listener.onSuccess();
                    } else if (response.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                        listener.onCancel();
                    } else {
                        switch (response.errCode) {
                            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                result = "???????????????";
                                break;
                            case BaseResp.ErrCode.ERR_UNSUPPORT:
                                result = "???????????????";
                                break;
                            default:
                                result = "????????????";
                                break;
                        }
                        listener.onFail(result);
                    }
                }
            } else if (intent.getAction().equals(WXShare.TYPE_LOGIN)) {
                String code = intent.getStringExtra(WXShare.EXTRA_RESULT);
                if (null != callback) {
                    callback.onItemCallback(0, code);
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
                        result = "????????????" + response.errCode;
                        listener.onFail(result);
                    }
                }
                if (null != callback) {
                    callback.onItemCallback(0, null);
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
