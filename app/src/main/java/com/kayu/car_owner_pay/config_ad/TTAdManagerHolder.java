package com.kayu.car_owner_pay.config_ad;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.http.OkHttpManager;
import com.kayu.utils.StringUtil;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static final String TAG = "TTAdManagerHolder";
//    private static final String appID = "5138603";//应用ID
//    public static final String videoID = "945853775";//激励视屏ID
//    public static final String splashID = "887428694";//闪屏广告ID

    private static String appID = "5144457";//应用ID
    public static final String videoID = "945900466";//激励视屏ID
    public static final String splashID = "887446448";//闪屏广告ID

    private static boolean sInit;


    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(final Context context) {
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig(context), new TTAdSdk.InitCallback() {
                @Override
                public void success() {

                    Log.i(TAG, "success: "+TTAdSdk.isInitSuccess());
                }

                @Override
                public void fail(int code, String msg) {
                    Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
                }
            });
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context) {

        if (null != KWApplication.getInstance().systemArgs && !StringUtil.isEmpty(KWApplication.getInstance().systemArgs.android.csjAppid)) {
            appID = KWApplication.getInstance().systemArgs.android.csjAppid;
        }
        return new TTAdConfig.Builder()
                .appId(appID)
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) // 锁屏下穿山甲SDK不会再出落地页，此API已废弃，调用没有任何效果
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(
                        TTAdConstant.NETWORK_STATE_WIFI,
                        TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                .supportMultiProcess(true)//是否支持多进程
                .needClearTaskReset()
                .build();
    }
}
