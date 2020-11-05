package com.kayu.car_owner_pay.wxapi;

import com.google.gson.annotations.SerializedName;

public class WxPayBean {

    /**
     * orderNo : CW20201023163639183SLZHSSXWVPXTN
     * orderId : 266
     * body : {"package":"sign=WXPay","appid":"wxa0cc46db3c2c4aa4","sign":"13EB52A0DD91422FAFF4BA317ACA48C9","partnerid":"1603374952","noncestr":"AF1B5ACBE0CE40E388FEF7B33BC9618F","perpayid":"wx23163639424749427f8340e28ff9f80000","timestamp":1603442199}
     */

    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("orderId")
    public Long orderId;
    @SerializedName("body")
    public BodyDTO body;

    public static class BodyDTO {
        /**
         * package : sign=WXPay
         * appid : wxa0cc46db3c2c4aa4
         * sign : 13EB52A0DD91422FAFF4BA317ACA48C9
         * partnerid : 1603374952
         * noncestr : AF1B5ACBE0CE40E388FEF7B33BC9618F
         * perpayid : wx23163639424749427f8340e28ff9f80000
         * timestamp : 1603442199
         */

        @SerializedName("package")
        public String packageX;
        @SerializedName("appid")
        public String appid;
        @SerializedName("sign")
        public String sign;
        @SerializedName("partnerid")
        public String partnerid;
        @SerializedName("noncestr")
        public String noncestr;
        @SerializedName("perpayid")
        public String perpayid;
        @SerializedName("timestamp")
        public Long timestamp;
    }
}
