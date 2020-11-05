package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

public class UserBean {

    /**
     * username : 张三
     * wxName : Spring
     * wxNo : null
     * phone : 1883****000
     * idNo : 1300**************000
     * headPic : https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIwq9KbChx4lfQ3F96EYpWzJiah0W4yxykS6WW7qicobIZX6RnMZbTKM9vkVaXa8YibREKHALE1P3P3Q/132
     * balance : 9999.26
     * lastLoginTime : 2020-09-27 10:37:24
     * expAmt : 0.74
     */

    @SerializedName("username")
    public String username;
    @SerializedName("wxName")
    public String wxName;
    @SerializedName("wxNo")
    public String wxNo;
    @SerializedName("phone")
    public String phone;
    @SerializedName("idNo")
    public String idNo;
    @SerializedName("headPic")
    public String headPic;
    @SerializedName("balance")
    public Double balance;//单位（元）
    @SerializedName("lastLoginTime")
    public String lastLoginTime;
    @SerializedName("expAmt")
    public Double expAmt;
}
