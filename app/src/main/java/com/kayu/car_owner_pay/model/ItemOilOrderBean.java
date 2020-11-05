package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

public class ItemOilOrderBean {
    /**
     * orderId : ZF2231103562009251EXx01
     * paySn : CZBH582788473319401
     * phone : 176****9252
     * orderTime : 2020-09-25 14:37:08
     * payTime : 2020-09-25 14:37:26
     * refundTime : null
     * gasName : 众诚连锁卫星加油站
     * province : 吉林省
     * city : 长春市
     * county : 朝阳区
     * gunNo : 2
     * oilNo : 0#
     * amountPay : 0.10
     * amountGun : 0.10
     * amountDiscounts : 0.00
     * orderStatusName : 已支付
     * couponMoney : 0.00
     * couponId : -1
     * couponCode : null
     * litre : 0.02
     * payType : 微信支付
     * priceUnit : 5.89
     * priceOfficial : 6.88
     * priceGun : 5.99
     * orderSource : 车友团
     * qrCode4PetroChina : null
     * amountServiceCharge : 0.00
     */

    @SerializedName("orderId")
    public String orderId;
    @SerializedName("paySn")
    public String paySn;
    @SerializedName("phone")
    public String phone;
    @SerializedName("orderTime")
    public String orderTime;
    @SerializedName("payTime")
    public String payTime;
    @SerializedName("refundTime")
    public String refundTime;
    @SerializedName("gasName")
    public String gasName;
    @SerializedName("province")
    public String province;
    @SerializedName("city")
    public String city;
    @SerializedName("county")
    public String county;
    @SerializedName("gunNo")
    public Integer gunNo;
    @SerializedName("oilNo")
    public String oilNo;
    @SerializedName("amountPay")
    public String amountPay;
    @SerializedName("amountGun")
    public String amountGun;
    @SerializedName("amountDiscounts")
    public String amountDiscounts;
    @SerializedName("orderStatusName")
    public String orderStatusName;
    @SerializedName("couponMoney")
    public String couponMoney;
    @SerializedName("couponId")
    public Integer couponId;
    @SerializedName("couponCode")
    public String couponCode;
    @SerializedName("litre")
    public String litre;
    @SerializedName("payType")
    public String payType;
    @SerializedName("priceUnit")
    public String priceUnit;
    @SerializedName("priceOfficial")
    public String priceOfficial;
    @SerializedName("priceGun")
    public String priceGun;
    @SerializedName("orderSource")
    public String orderSource;
    @SerializedName("qrCode4PetroChina")
    public String qrCode4PetroChina;
    @SerializedName("amountServiceCharge")
    public String amountServiceCharge;


}
