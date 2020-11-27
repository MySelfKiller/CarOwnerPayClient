package com.kayu.car_owner_pay.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.model.WXSharedBean;
import com.kayu.car_owner_pay.wxapi.WXShare;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.ImageUtil;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.ImageCallback;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;

public class LocalJavascriptInterface {
    private Context mContext;
    public LocalJavascriptInterface(Context context){
        this.mContext = context;
    }

    @JavascriptInterface
    public void saveImage(String s){
//        MessageDialog.show((AppCompatActivity) mContext, "保存图片", "确定保存图片到相册吗？\n"+s, "确定", "取消")
//                .setCancelable(false)
//                .setOkButton(new OnDialogButtonClickListener() {
//            @Override
//            public boolean onClick(BaseDialog baseDialog, View v) {
//                baseDialog.doDismiss();
//
//                return false;
//            }
//        });
        if (StringUtil.isEmpty(s)) {
            TipDialog.show((AppCompatActivity) mContext, "数据错误，无法保存！", TipDialog.TYPE.ERROR);
            return;
        }
        KWApplication.getInstance().loadImg(s,null,new ImageCallback() {
            @Override
            public void onSuccess(Bitmap resource) {
                String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
                boolean isSaveSuccess = ImageUtil.saveImageToGallery(mContext, resource,fileName);
                if (isSaveSuccess) {
                    TipDialog.show((AppCompatActivity) mContext, "保存成功", TipDialog.TYPE.SUCCESS);
                } else {
//                    Toast.makeText(mContext, "保存失败", Toast.LENGTH_LONG).show();
                    TipDialog.show((AppCompatActivity) mContext, "保存失败", TipDialog.TYPE.ERROR);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @JavascriptInterface
    public void sharedWechat(String jsonStr){
        if (null == jsonStr) {
            TipDialog.show((AppCompatActivity) mContext, "分享数据错误！", TipDialog.TYPE.ERROR);
            return;
        }
//        TipDialog.show((AppCompatActivity) mContext, jsonStr, TipDialog.TYPE.ERROR);
//        Toast.makeText(mContext, jsonStr, Toast.LENGTH_LONG).show();
//        jsonStr
        WXSharedBean sharedBean = GsonHelper.fromJson(jsonStr, WXSharedBean.class);
        if (null == sharedBean) {
            TipDialog.show((AppCompatActivity) mContext, "分享数据错误！", TipDialog.TYPE.ERROR);
            return;
        }

        WXShare wxShare = new WXShare(mContext);
        wxShare.register();

        switch (sharedBean.object) {
            case 0://0图片，1音乐，2视频，3网页
                if (StringUtil.isEmpty(sharedBean.qrCode)) {
                    TipDialog.show((AppCompatActivity) mContext, "分享的图片地址不存在！", TipDialog.TYPE.ERROR);
                    break;
                }
                KWApplication.getInstance().loadImg(sharedBean.qrCode,null,new ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap resource) {
                        Bitmap sss = resource;
                        wxShare.shareImg(sharedBean.type,sss, sharedBean.title, sharedBean.desc);
                    }

                    @Override
                    public void onError() {

                    }
                });
                break;
            case 1:
                if (StringUtil.isEmpty(sharedBean.url)) {
                    TipDialog.show((AppCompatActivity) mContext, "分享的音频地址不存在！", TipDialog.TYPE.ERROR);
                    break;
                }
                wxShare.shareMusic(sharedBean.type,sharedBean.url, sharedBean.title, sharedBean.desc);
                break;
            case 2:
                if (StringUtil.isEmpty(sharedBean.url)) {
                    TipDialog.show((AppCompatActivity) mContext, "分享的视频地址不存在！", TipDialog.TYPE.ERROR);
                    break;
                }

                wxShare.shareVideo(sharedBean.type,sharedBean.url, sharedBean.title, sharedBean.desc);
                break;
            case 3:
                if (StringUtil.isEmpty(sharedBean.url)) {
                    TipDialog.show((AppCompatActivity) mContext, "分享的网页地址不存在！", TipDialog.TYPE.ERROR);
                    break;
                }
                wxShare.shareUrl(sharedBean.type,sharedBean.url, sharedBean.title, sharedBean.desc,null);
                break;
        }
    }
}
