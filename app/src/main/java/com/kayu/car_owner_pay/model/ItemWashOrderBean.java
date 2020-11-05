package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

public class ItemWashOrderBean {

    /**
     * shopCode : 310113003
     * realAmount : 0.01
     * address : 上海上海市宝山区上海上海市宝山区上海市宝山区蕰川路289号101室
     * latitude : 31.352045
     * shopName : 上海景邦汽车技术服务有限公司
     * telephone : 021-3456789
     * id : 53
     * busTime : 09:00-23:00
     * serviceName : 标准洗车-五座轿车
     * doorPhotoUrl : http://150.242.239.250:8131/group1/M00/02/03/wKhkEVs-EXKEY8D_AAAAALM0KfQ765.jpg
     * longitude : 121.438553
     */

    @SerializedName("shopCode")
    public String shopCode;
    @SerializedName("realAmount")
    public Double realAmount;
    @SerializedName("address")
    public String address;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("shopName")
    public String shopName;
    @SerializedName("state")
    public Integer state;//状态筛选(暂定) 0:待支付 1:已支付待使用 2:已取消 3:已使用 4:退款中 5:已退款 6:支付失败、7:退款失败
    @SerializedName("telephone")
    public String telephone;
    @SerializedName("id")
    public Long id;
    @SerializedName("busTime")
    public String busTime;
    @SerializedName("serviceName")
    public String serviceName;
    @SerializedName("doorPhotoUrl")
    public String doorPhotoUrl;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("surplusDay")
    public Integer surplusDay;
}
