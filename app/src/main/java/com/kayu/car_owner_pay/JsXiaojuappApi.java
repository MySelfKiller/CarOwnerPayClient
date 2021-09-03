package com.kayu.car_owner_pay;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.amap.api.location.AMapLocation;
import com.kayu.car_owner_pay.activity.WashStationActivity;
import com.kayu.utils.AppUtil;
import com.kayu.utils.DesCoderUtil;
import com.kayu.utils.LogUtil;
import com.kayu.utils.location.LocationManagerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.CompletionHandler;
/**
 * 配置青桔js调用信息
 */
public class JsXiaojuappApi {
    private Context mContext;
    private Handler mHandler;
    public JsXiaojuappApi(Context context,  Handler handler){
        this.mContext = context;

        this.mHandler = handler;

    }
//
//    @JavascriptInterface
//    public void getLocation(String args, CompletionHandler callBack) {
////        callBack.completed();
//
//    }
//
//    @JavascriptInterface
//    public void launchNav(String args) {
//    }
//
//    @JavascriptInterface
//    public void setReferer(String args) {
//        LogUtil.e("qingju", "JsXiaojuappApi setReferer==" + args);
//        try {
//            JSONObject jsonObject = new JSONObject(args);
//            String referer = jsonObject.getString("Referer");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @JavascriptInterface
    public void getLocation(Object args,CompletionHandler handler){
        // handler.complete("");
        AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.evaluateJavascript("window.locationCallback(" + location.getLongitude() + "," + location.getLatitude() + ")", null);
//            }
//        });
        LogUtil.e("qingju","JsXiaojuappApi getLocation==" + location.getLongitude() + "," + location.getLatitude());
    }

    @JavascriptInterface
    public void launchNav(Object args){
        // handler
        LogUtil.e("qingju", "JsXiaojuappApi launchNav==" + args);
//        mHandler.post(() -> {
//            try {
//                JSONObject jsonObject = new JSONObject((String) args);
////                AppUtil.openUrl(mContext, "http://uri.amap.com/marker?position=" + jsonObject.getLong("toLng") + "," + jsonObject.getLong("toLat"));
////                KWApplication.getInstance().toNavi(mContext, jsonObject.getLong("toLat"), jsonObject.getLong("toLng"),"","GCJ02");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });
    }

    @JavascriptInterface
    public void getLocation(Object args){
        // handler
        AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.evaluateJavascript("window.locationCallback(" + location.getLongitude() + "," + location.getLatitude() + ")", null);
//            }
//        });
        LogUtil.e("qingju","JsXiaojuappApi getLocation==" + location.getLongitude() + "," + location.getLatitude());
    }

    @JavascriptInterface
    public void openGMap(String args) {
        LogUtil.e("qingju","JsXiaojuappApi-------"+ args.toString());
        mHandler.post(() ->{
            //{"fromLng":118.180237,"fromLat":39.623863,"toLng":"118.02162","toLat":"39.7285","toName":"红利加油站"}
            try {
                JSONObject jsonObject = new JSONObject(args);
                KWApplication.getInstance().toNavi(mContext,
                        jsonObject.getString("toLat"),
                        jsonObject.getString("toLng"),
                        jsonObject.getString("toName"),"GCJ02");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });


    }
}
