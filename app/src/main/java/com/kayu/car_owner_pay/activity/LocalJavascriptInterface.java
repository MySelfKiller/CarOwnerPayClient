package com.kayu.car_owner_pay.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.WXSharedBean;
import com.kayu.car_owner_pay.wxapi.WXShare;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.ImageUtil;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.callback.ImageCallback;
import com.kayu.utils.permission.EasyPermissions;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipGifDialog;

import java.util.List;

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
//        LogUtil.e("LocalJavascriptInterface","saveImage----path:"+s);
        if (StringUtil.isEmpty(s)) {
            TipGifDialog.show((AppCompatActivity) mContext, "数据错误，无法保存！", TipGifDialog.TYPE.ERROR);
            return;
        }
        permissionsCheck((BaseActivity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string.permiss_write_store, new Callback() {
            @Override
            public void onSuccess() {
                KWApplication.getInstance().loadImg(s,null,new ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap resource) {
                        String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
                        boolean isSaveSuccess = ImageUtil.saveImageToGallery(mContext, resource,fileName);
                        if (isSaveSuccess) {
                            TipGifDialog.show((AppCompatActivity) mContext, "保存成功", TipGifDialog.TYPE.SUCCESS);
                        } else {
                            TipGifDialog.show((AppCompatActivity) mContext, "保存失败", TipGifDialog.TYPE.ERROR);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    @JavascriptInterface
    public void sharedWechat(String jsonStr){
        if (null == jsonStr) {
            TipGifDialog.show((AppCompatActivity) mContext, "分享数据错误！", TipGifDialog.TYPE.ERROR);
            return;
        }
        WXSharedBean sharedBean = GsonHelper.fromJson(jsonStr, WXSharedBean.class);
        if (null == sharedBean) {
            TipGifDialog.show((AppCompatActivity) mContext, "分享数据错误！", TipGifDialog.TYPE.ERROR);
            return;
        }

        WXShare wxShare = new WXShare(mContext);
        wxShare.register();

        switch (sharedBean.object) {
            case 0://0图片，1音乐，2视频，3网页
                if (StringUtil.isEmpty(sharedBean.qrCode)) {
                    TipGifDialog.show((AppCompatActivity) mContext, "分享的图片地址不存在！", TipGifDialog.TYPE.ERROR);
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
                    TipGifDialog.show((AppCompatActivity) mContext, "分享的音频地址不存在！", TipGifDialog.TYPE.ERROR);
                    break;
                }
                wxShare.shareMusic(sharedBean.type,sharedBean.url, sharedBean.title, sharedBean.desc);
                break;
            case 2:
                if (StringUtil.isEmpty(sharedBean.url)) {
                    TipGifDialog.show((AppCompatActivity) mContext, "分享的视频地址不存在！", TipGifDialog.TYPE.ERROR);
                    break;
                }

                wxShare.shareVideo(sharedBean.type,sharedBean.url, sharedBean.title, sharedBean.desc);
                break;
            case 3:
                if (StringUtil.isEmpty(sharedBean.url)) {
                    TipGifDialog.show((AppCompatActivity) mContext, "分享的网页地址不存在！", TipGifDialog.TYPE.ERROR);
                    break;
                }
                wxShare.shareUrl(sharedBean.type,sharedBean.url, sharedBean.title, sharedBean.desc,null);
                break;
        }
    }

    public void permissionsCheck(BaseActivity baseActivity, String[] perms, int resId, @NonNull Callback callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        baseActivity.performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new BaseActivity.PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                callback.onSuccess();
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(baseActivity, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) baseActivity);
                dialog.setTitle(baseActivity.getString(R.string.app_name));
                dialog.setMessage(baseActivity.getString(resId));
                dialog.setOkButton("确定", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
                        return false;
                    }
                });
                dialog.setCancelButton("取消",new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        return false;
                    }
                });
                dialog.setCancelable(false);

                dialog.show();
            }
        });
    }

}
