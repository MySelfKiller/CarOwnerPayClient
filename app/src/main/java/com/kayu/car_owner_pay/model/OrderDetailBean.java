package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

public class OrderDetailBean {

    /**
     * orderNo : UAC20201207162148128GXAOYQROJWCO
     * username : 金章冀
     * phone : 13717699831
     * cardNo : 000000001
     * cardCode : 218T84
     * waybillNo : null
     */

    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("username")
    public String username;
    @SerializedName("phone")
    public String phone;
    @SerializedName("cardNo")
    public String cardNo;
    @SerializedName("cardCode")
    public String cardCode;
    @SerializedName("waybillNo")
    public String waybillNo;
}
