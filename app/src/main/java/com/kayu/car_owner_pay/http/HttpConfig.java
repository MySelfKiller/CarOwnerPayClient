package com.kayu.car_owner_pay.http;


import com.kayu.car_owner_pay.BuildConfig;

import okhttp3.MediaType;

public class HttpConfig {
    public static final String HOST = BuildConfig.BASE_URL;
    public static final String INTERFACE_LOGIN = "api/login"; //登录
    public static final String INTERFACE_VERIFICATION_CODE = "api/getSmsCapt/"; //短信验证码
    public static final String INTERFACE_SET_PASSWORD = ""; //设置密码
    public static final String INTERFACE_RESET_PASSWORD = ""; //重置密码

    public static final String INTERFACE_GET_CATEGORY = "api/v1/nav/list"; //获取项目类别列表
    public static final String INTERFACE_GET_FILTER = "api/v1/gas/getfilter"; //获取加油站条件
    public static final String INTERFACE_GET_BANNER = "api/v1/banner/list"; //获取Banner列表

    public static final String INTERFACE_GET_ACCOUNT_REMINDER = "api/parameter/getAccTitle";        //获取账户提示语

    public static final String INTERFACE_GET_EXCHANGE = "api/v1/user/recharge";        //兑换充值
    public static final String INTERFACE_GET_NOTIFY_LIST = "api/v1/notify/list";        //获取消息列表

    public static final String INTERFACE_STATION_LIST = "api/v1/gas/list"; //获取加油站列表
    public static final String INTERFACE_STATION_DETAIL = "api/v1/gas/getdetail/"; //获取加油站详情
    public static final String INTERFACE_GAS_PAY = "api/v1/gas/buy"; //获取加油站支付信息
    public static final String INTERFACE_GAS_NOTIFIED = "api/v1/gasorder/notified"; //发起加油通知后台

    public static final String INTERFACE_GAS_ORDER_LIST = "api/v1/gasorder/list"; //获取加油站订单列表

    public static final String INTERFACE_GET_WASH_FILTER = "api/v1/carwash/getfilter"; //洗车条件
    public static final String INTERFACE_WASH_STATION_LIST = "api/v1/carwash/list"; //获取洗车站列表
    public static final String INTERFACE_WASH_STATION_DETAIL = "api/v1/carwash/getdetail/"; //获取洗车站详情
    public static final String INTERFACE_WASH_PAY = "api/v1/carwash/buy"; //获取洗车订单购买信息
    public static final String INTERFACE_WASH_PAY_CANCEL = "api/v1/cworder/cancel";//取消洗车订单
    public static final String INTERFACE_WASH_ORDER_LIST = "api/v1/cworder/list"; //获取洗车订单列表
    public static final String INTERFACE_WASH_ORDER_DETAIL = "api/v1/cworder/getdetail/"; //获取洗车订单详情
    public static final String INTERFACE_WASH_ORDER_REFUND_INFO = "api/v1/cworder/rfdinfo/"; //获取洗车订单退款信息
    public static final String INTERFACE_WASH_ORDER_REFUND = "api/v1/cworder/refund"; //洗车订单申请退款

    public static final String INTERFACE_GET_PARAMETER = "api/parameter/getSystemParameter";        //获取系统参数配置
    public static final String INTERFACE_WECHAT = "api/parameter/wechatCustomer";        //微信客服
    public static final String INTERFACE_USER_INFO = "api/v1/user/getdetail";        //用户信息
    public static final String INTERFACE_BALANCE_DEAIL = "api/v1/ioitem/list";        //收入明细
    public static final String INTERFACE_CHECK_UPDAGE = "api/parameter/editionAndroid"; //检查版本更新接口
    public static final String INTERFACE_MESSAGE_LIST = "api/v1/notify/list";        //消息列表
    public static final String INTERFACE_MESSAGE_NUM = "api/v1/notify/getUnreadCnt";        //消息列表

    //    public String authorization = "";
    //    public static final MediaType JSON =MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType FILE= MediaType.parse("application/octet-stream");
    public static final MediaType FORM= MediaType.parse("multipart/form-data; charset=utf-8");
    public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
}
