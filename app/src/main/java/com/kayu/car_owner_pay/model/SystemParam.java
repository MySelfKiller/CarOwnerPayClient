package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/28.
 * PS: Not easy to write code, please indicate.
 */
public class SystemParam {

    /**
     * id : 55
     * title : 加油洗车功能
     * content : {"gas":1,"carwash":1}
     * url : null
     * state : 1
     * type : 10
     * pathMd5 : null
     * pathLength : 0
     * force : null
     * blank1 : null
     * blank2 : null
     * blank3 : null
     * blank4 : null
     * blank5 : null
     * blank6 : null
     * blank7 : null
     * blank8 : null
     * blank9 : null
     */

    @SerializedName("id")
    public Long id;
    @SerializedName("title")
    public String title;
    @SerializedName("content")
    public String content;
    @SerializedName("url")
    public String url;
    @SerializedName("state")
    public Integer state;
    @SerializedName("type")
    public Integer type;
    @SerializedName("pathMd5")
    public String pathMd5;
    @SerializedName("pathLength")
    public Integer pathLength;
    @SerializedName("force")
    public String force;
    @SerializedName("blank1")
    public String blank1;
    @SerializedName("blank2")
    public String blank2;
    @SerializedName("blank3")
    public String blank3;
    @SerializedName("blank4")
    public String blank4;
    @SerializedName("blank5")
    public String blank5;
    @SerializedName("blank6")
    public String blank6;
    @SerializedName("blank7")
    public String blank7;
    @SerializedName("blank8")
    public String blank8;
    @SerializedName("blank9")
    public String blank9;
}
