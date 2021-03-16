package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

public class UserBean {

    /**
     * username : 黄敏
     * wxName : 痞子哥哥
     * wxNo : null
     * phone : 151******46
     * idNo : null
     * headPic : https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83er0yGmJvZZJ0vYIkA7arNh8fDpCxyGWadnEGBxydPEAwibiaLoH5FhOLZu1Rn4ARBhcGV6UF3mXP48g/132
     * balance : 0
     * rewardAmt : 0
     * lastLoginTime : 2020-11-23 16:23:36
     * inviteNo : 1328608206898941954
     * expAmt : 0
     * activateTime : 2020-11-17 15:57:42
     * type : 2
     * "busTitle":"已为您节省0.0"
     */

    @SerializedName("username")
    public String username;//姓名(暂定)
    @SerializedName("wxName")
    public String wxName;//微信昵称
    @SerializedName("wxNo")
    public String wxNo;//微信号(暂定)
    @SerializedName("phone")
    public String phone;//手机号
    @SerializedName("idNo")
    public String idNo;//证件号(暂定)
    @SerializedName("headPic")
    public String headPic;//微信头像
    @SerializedName("balance")
    public Double balance;//账户余额/元
    @SerializedName("rewardAmt")
    public Double rewardAmt;//累计收益/元
    @SerializedName("lastLoginTime")
    public String lastLoginTime;//最后一次登陆时间
    @SerializedName("inviteNo")
    public String inviteNo="";//激活卡号码
    @SerializedName("expAmt")
    public Double expAmt;//累计节省金额
    @SerializedName("activateTime")
    public String activateTime;//激活时间
    @SerializedName("type")
    public Integer type;//账号类型 1:普通用户,2:经销商,3:运营商,-2 游客
    @SerializedName("busTitle")
    public String busTitle;//已为你节省xxx元
}
