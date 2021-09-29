package com.kayu.utils;

public class Constants {

  public static final int RC_PERMISSION_BASE = 1000;
  public static final int RC_PERMISSION_PERMISSION_ACTIVITY = RC_PERMISSION_BASE + 1;
  public static final int RC_PERMISSION_PERMISSION_FRAGMENT = RC_PERMISSION_BASE + 2;
  public static final int INSTALL_APP_REQUESTCODE = RC_PERMISSION_BASE + 3;


  public static final String SharedPreferences_name = "login_info";
  public static final String login_info = "login_info";
  public static final String isLogin = "isLogin";
  public static final String token = "token";
  public static final String isSetPsd = "isSetPsd";
  public static final String isShowDialog = "isShowDialog";
  public static final String system_args = "system_args";
  public static final String userInfo = "user_info";

  public static final String authority = "com.kayu.car_owner_pay.provider";
  public static final String PATH_ROOT = "com.kayu.car_owner_pay" ;


    public static String PATH_IMG = "imag/"; // 图片
    public static String PATH_PHOTO = "photo/"; // 图片及其他数据保存文件夹

  //服务端请求数据解析状态
  public static final int REQ_NETWORK_ERROR = -2;
  public static final int PARSE_DATA_ERROR = -1;
  public static final int PARSE_DATA_SUCCESS = 1;
  public static final int PARSE_DATA_REFRESH = 3;
  public static final int PARSE_DATA_END = 4;


  //接口请求响应状态码说明
  public static final int response_code_0 = 0;  // 失败
  public static final int response_code_1 = 1;  // 成功
  public static final int response_code_301 = 301;  // 参数错误
  public static final int response_code_302 = 302;  // 结果不存在
  public static final int response_code_303 = 303;  // 结果已存在
  public static final int response_code_304 = 304;  // 数据错误
  public static final int response_code_305 = 305;  // 数据上传失败
  public static final int response_code_306 = 306;  // 非法操作

  public static final int response_code_401 = 401;  // 无权限

  public static final int response_code_500 = 500;  // 服务错误
  public static final int response_code_10100 = 10100;  // 用户名或密码错误

  public static final int response_code_10101 = 10101;  // 用户未登陆
  public static final int response_code_10102 = 10102;  // 账号禁用
  public static final int response_code_10103 = 10103;  // 密码修改失败
  public static final int response_code_10104 = 10104;  // 验证码错误
  public static final int response_code_10105 = 10105;  //

  public static final String FLAG_CREDIT_CARD = "creditCard";
  public static final String FLAG_LOAN = "loan";
  public static final String FLAG_LOAN_BANNER = "loanBanner";
  public static final String FLAG_ACTIVITY = "activity";

  public static final int BASE_ID = 1100000;

  public static final String WX_APP_ID = "wxa0cc46db3c2c4aa4";
}
