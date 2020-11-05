package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WashStationDetailBean {

    /**
     * shopCode : 130204001
     * shopName : 金洁士汽车清洗美容服务
     * address : 唐山市路南区国防道凯旋铭居底商2号
     * startTime : 2019-09-29
     * endTime : 2019-09-29
     * openTimeStart : 08:00
     * openTimeEnd : 18:00
     * isStatus : 4
     * doorPhotoUrl : http://150.242.239.250:8131/group1/M00/01/F8/wKhkEVs-ESGESv9tAAAAAJTx4PY377.jpg
     * longitude : 118.167631
     * latitude : 39.625703
     * rating : A
     * score : 5.0
     * totalNum : 0
     * telephone : 15614582498
     * isOpen : 1
     * proNumber : 130000
     * proName : 河北
     * cityNumber : 130200
     * cityName : 唐山市
     * imgList : [{"shopCode":"130204001","shopImgUrl":"http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AD17RAAIFD18b7GA651.jpg"},{"shopCode":"130204001","shopImgUrl":"http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AeajjAAHKUX8zAk4633.jpg"},{"shopCode":"130204001","shopImgUrl":"http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AVyTMAAHTt7hvWkM376.jpg"}]
     * serviceList : [{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"30.00","finalPrice":"23.00","carModel":1},{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"35.00","finalPrice":"28.00","carModel":2}]
     * services : [{"washType":1,"name":"普通洗车","list":[{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"30.00","finalPrice":"23.00","carModel":1},{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"35.00","finalPrice":"28.00","carModel":2}]}]
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
    @SerializedName("rating")
    public String rating;
    @SerializedName("score")
    public String score;
    @SerializedName("totalNum")
    public String totalNum;
    @SerializedName("telephone")
    public String telephone;
    @SerializedName("isOpen")
    public String isOpen;
    @SerializedName("proNumber")
    public String proNumber;
    @SerializedName("proName")
    public String proName;
    @SerializedName("cityNumber")
    public String cityNumber;
    @SerializedName("cityName")
    public String cityName;
    @SerializedName("imgList")
    public List<ImgListDTO> imgList;
    @SerializedName("serviceList")
    public List<ServiceListDTO> serviceList;
    @SerializedName("services")
    public List<ServicesDTO> services;

    public static class ImgListDTO {
        /**
         * shopCode : 130204001
         * shopImgUrl : http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AD17RAAIFD18b7GA651.jpg
         */

        @SerializedName("shopCode")
        public String shopCode;
        @SerializedName("shopImgUrl")
        public String shopImgUrl;
    }

    public static class ServiceListDTO {
        /**
         * serviceCode : 81
         * serviceName : 标准洗车-五座轿车
         * serviceType : 1
         * price : 30.00
         * finalPrice : 23.00
         * carModel : 1
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
        @SerializedName("carModel")
        public Integer carModel;
    }

    public static class ServicesDTO {
        /**
         * washType : 1
         * name : 普通洗车
         * list : [{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"30.00","finalPrice":"23.00","carModel":1},{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"35.00","finalPrice":"28.00","carModel":2}]
         */

        @SerializedName("washType")
        public Integer washType;//清洗类型 1:标准洗车 2:精致洗车
        @SerializedName("name")
        public String name;
        @SerializedName("list")
        public List<ListDTO> list;

        public static class ListDTO {
            /**
             * serviceCode : 81
             * serviceName : 标准洗车-五座轿车
             * serviceType : 1
             * price : 30.00
             * finalPrice : 23.00
             * carModel : 1
             */

            @SerializedName("serviceCode")//服务编码
            public String serviceCode;
            @SerializedName("serviceName")//服务名称
            public String serviceName;
            @SerializedName("serviceType")
            public String serviceType;
            @SerializedName("price")
            public String price;//门店市场价
            @SerializedName("finalPrice")
            public String finalPrice;//最终价
            @SerializedName("carModel")
            public Integer carModel;//车型 1：小轿车 2:大车 3:全部车型
        }
    }
}
