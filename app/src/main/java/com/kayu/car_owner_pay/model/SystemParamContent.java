package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/28.
 * PS: Not easy to write code, please indicate.
 */

public class SystemParamContent {
    @SerializedName("gas")
    public Integer gas;
    @SerializedName("carwash")
    public Integer carwash;
    @SerializedName("android")
    public AndroidDTO android;

    public static class AndroidDTO {
        @SerializedName("csj_appId")
        public String csjAppid;
        @SerializedName("csj_placementId")
        public String csjPlacementid;
        @SerializedName("ylh_appId")
        public String ylhAppid;
        @SerializedName("ylh_splashID")
        public String ylhSplashid;
        @SerializedName("showAd")
        public String showAd;
    }

    /**
     * {"gas":1,"carwash":1,
     * "ios":{"csj_appId":"5144458","csj_placementId":"887439766","ylh_appId":"1105344611","ylh_splashID":"9040714184494018","showAd":"csj"},
     * "android":{"csj_appId":"5144457","csj_placementId":"887446448","ylh_appId":"1105344611","ylh_splashID":"9040714184494018","showAd":"csj"}}
     */


}
