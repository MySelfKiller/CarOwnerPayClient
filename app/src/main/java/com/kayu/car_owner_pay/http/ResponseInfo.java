package com.kayu.car_owner_pay.http;

/**
 * Created by Killer on 2018/2/11.
 */

public class ResponseInfo {
    public int status = -1;
    public String msg = "";
    public String url;
    public ResponseInfo(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    public Object responseData = null;

}
