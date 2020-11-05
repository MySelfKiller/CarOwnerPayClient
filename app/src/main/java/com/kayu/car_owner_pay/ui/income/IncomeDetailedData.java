package com.kayu.car_owner_pay.ui.income;

import com.google.gson.annotations.SerializedName;

public class IncomeDetailedData {

    /**
     * id : 1
     * orderNo : ZF2231103562009251CdQ01
     * amount : 0.0
     * explain : 加油优惠
     * type : 0
     * createTime : 2020-09-27 10:14:55
     */

    @SerializedName("id")
    public Integer id;
    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("amount")
    public Double amount;
    @SerializedName("explain")
    public String explain;
    @SerializedName("type")
    public Integer type;
    @SerializedName("createTime")
    public String createTime;
}
