package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

public class ItemMessageBean {

    @SerializedName("title")
    public String title;
    @SerializedName("url")
    public String url;
    @SerializedName("content")
    public String content;
    @SerializedName("createTime")
    public String createTime;
}
