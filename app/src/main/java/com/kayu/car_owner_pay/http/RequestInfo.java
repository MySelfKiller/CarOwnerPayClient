package com.kayu.car_owner_pay.http;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.kayu.car_owner_pay.http.parser.BaseParse;

import java.io.File;
import java.util.HashMap;

/**
 * Created by HuangMin on 2016/7/10.
 */
public final class RequestInfo {
    public String key = "dcec4ddf84b6427bb1a05bceca22365a"; // 请求服务端需要用到的验证key
    public String reqUrl;      //请求URL
    public Context context;     //上下文
    public Activity activity;
    public HashMap<String,Object> reqDataMap;//往后台传输请求参数，hashMap携带。
//    public HashMap<String,List<Object>> reqDataListMap;
    public HashMap<String,Object> otherDataMap;	//往后台传输请求参数，hashMap携带。驾车需要的额外数据
    public HashMap<String,Object> fileDataMap;
    public BaseParse<?> parser;		//返回json数据解析成需要的对象
    public Handler handler;
    public File file;
    public long fileSize;
    public String downUrl = null;
//    public boolean authorization = true;
}
