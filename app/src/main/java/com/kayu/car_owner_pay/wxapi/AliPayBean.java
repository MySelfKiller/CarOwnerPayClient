package com.kayu.car_owner_pay.wxapi;

import com.google.gson.annotations.SerializedName;

public class AliPayBean {

    /**
     * orderId : 272
     * orderNo : CW20201023165642711XHWHNSMXMOEED
     * body : alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2021001199681089&biz_content=%7B%22out_trade_no%22%3A%22CW20201023165642711XHWHNSMXMOEED%22%2C%22total_amount%22%3A23%2C%22subject%22%3A%22%E8%BD%A6%E5%8F%8B%E5%9B%A2-%E6%B4%97%E8%BD%A6%E4%BB%98%E6%AC%BE%22%2C%22timeout_express%22%3A%225m%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fwww.ky808.cn%2Fcarfriend%2Fapi%2Fnotify%2Falipay&sign=gzVf6hOhX4lT3V5lp72T59yNt1hNJPe0GG8ZIWWHMo1iw6Uv5tzKYG7BWbSWYkl5ZWKP5q5GcEQx3yC2GtNgiysU193pm7goNxdW2%2F335edyhaQD5Kx%2F%2Fc4SbFLzdDvzcE6jL4Qo9tQY23%2FRPIlccuJ7glelqGunhk68xlY%2BmPlqpPjcgsanAMkBIwgBU9Lghz4dpEtpr5DazpCvySp1%2FbRR9Jh5qFdJQ3z7geejLPP0JTwGfzfKBd9sSod%2B1S0vAoFU48rvjoIxM%2FPlU9%2FI52Fr1UBmNe1ZSzmGLPgNIf3hXzzNO8RXzg4kFFk2LaOsI2SvejZFOyQp%2FJVMg2h%2FVw%3D%3D&sign_type=RSA2&timestamp=2020-10-23+16%3A56%3A42&version=1.0
     */

    @SerializedName("orderId")
    public Long orderId;
    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("body")
    public String body;
}
