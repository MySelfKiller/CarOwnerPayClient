package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/27.
 * PS: Not easy to write code, please indicate.
 */
public class RefundInfo {

    /**
     * id : 53
     * amount : 0.01
     * refundWayResults : [{"way":1,"content":"原路退回(1-3个工作日内退回到原支付方)"}]
     * reasons : ["计划有变,没消费时间","选错门店","店里价格更优惠","无法联系到商家预约","商家不接待"]
     */

    @SerializedName("id")
    public Integer id;
    @SerializedName("amount")
    public Double amount;
    @SerializedName("refundWayResults")
    public List<RefundWayResultsDTO> refundWayResults;
    @SerializedName("reasons")
    public List<String> reasons;

    public static class RefundWayResultsDTO {
        /**
         * way : 1
         * content : 原路退回(1-3个工作日内退回到原支付方)
         */

        @SerializedName("way")
        public Integer way;
        @SerializedName("content")
        public String content;
    }
}
