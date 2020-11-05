package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WashStationBean {

    /**
     * shopCode : 131203001
     * shopName : 名都洗车店
     * address : 河北省唐山市曹妃甸区新城大街兴海名都地下停车场D区
     * startTime : 2019-09-29
     * endTime : 2019-09-29
     * openTimeStart : 08:00
     * openTimeEnd : 18:00
     * isStatus : 4
     * doorPhotoUrl : null
     * longitude : 118.45866
     * latitude : 39.283637
     * score : 5.0
     * totalNum : 0
     * distance : 882322
     * isOpen : 1
     * chain : 0
     * serviceList : [{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"33.00","finalPrice":"26.00"},{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"29.00","finalPrice":"22.00"}]
     */

    @SerializedName("shopCode")
    public String shopCode;
    @SerializedName("shopName")
    public String shopName;
    @SerializedName("address")
    public String address;
    @SerializedName("startTime")
    public String startTime;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("openTimeStart")
    public String openTimeStart;
    @SerializedName("openTimeEnd")
    public String openTimeEnd;
    @SerializedName("isStatus")
    public String isStatus;
    @SerializedName("doorPhotoUrl")
    public String doorPhotoUrl;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("score")
    public String score;
    @SerializedName("totalNum")
    public Integer totalNum;
    @SerializedName("distance")
    public String distance;
    @SerializedName("isOpen")
    public String isOpen;
    @SerializedName("chain")
    public String chain;
    @SerializedName("serviceList")
    public List<ServiceListDTO> serviceList;

    public static class ServiceListDTO {
        /**
         * serviceCode : 3000
         * serviceName : 标准洗车-SUV/MPV
         * serviceType : 1
         * price : 33.00
         * finalPrice : 26.00
         */

        @SerializedName("serviceCode")
        public String serviceCode;
        @SerializedName("serviceName")
        public String serviceName;
        @SerializedName("serviceType")
        public String serviceType;
        @SerializedName("price")
        public String price;
        @SerializedName("finalPrice")
        public String finalPrice;

    }
}
