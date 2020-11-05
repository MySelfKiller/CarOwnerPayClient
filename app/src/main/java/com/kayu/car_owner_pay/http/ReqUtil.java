package com.kayu.car_owner_pay.http;

import android.text.TextUtils;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.utils.Constants;
import com.kayu.utils.DesCoderUtil;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.LogUtil;
import com.kayu.utils.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 网路请求公共入口
 * Created by Huangmin on 2018/1/17.
 */

public class ReqUtil {

    private static ReqUtil mInstance;
    private OkHttpClient httpClient;

    Map<String, String> headerMap = new HashMap<>();
//    public static final MediaType PNG=MediaType.parse("image/jpeg");
    private RequestInfo reqInfo;
    private boolean fileDownload = false;
    private ReqUtil(){
//        OkHttpClient. = new OkHttpClient.Builder();
        //请求超时设置
        headerMap.put("terminal","app");
        headerMap.put("Referer",HttpConfig.HOST);
        Referer: http://192.168.0.112:8081/
        httpClient = OkHttpManager.getInstance().getHttpClient();
    }
    public static ReqUtil getInstance(){
        if (mInstance == null) {
            synchronized (ReqUtil.class) {
                if (mInstance == null) {
                    mInstance = new ReqUtil();
                }
            }
        }
//        else {
//            if (null != reqInfo){
//                reqInfo =null;//确保每次调用网路请求的时候都置空上一次请求的数据
//            }
//        }
        return mInstance;
    }

    public OkHttpClient getHttpClient(){
        return httpClient;
    }

    public void setReqInfo(RequestInfo reqInfo){
        this.reqInfo = reqInfo;
    }



    public void requestPostJSON(Callback callback) {

        if (null == reqInfo){
            LogUtil.e("request","Req NetWork:reqInfo is null");
            return;
        }

        if (!NetUtil.isNetworkAvailable(reqInfo.context)){
            ResponseInfo responseInfo = new ResponseInfo(-1,"无可用网络");
            reqInfo.handler.sendMessage(reqInfo.handler.obtainMessage(Constants.REQ_NETWORK_ERROR,responseInfo));
            return;
        }

        String url = reqInfo.reqUrl;

//        FormBody.Builder formBody = new FormBody.Builder();

//        StringBuffer buffer = new StringBuffer();
        String jsonParam ="";
        if (null != reqInfo.reqDataMap && !reqInfo.reqDataMap.isEmpty()){

            jsonParam = GsonHelper.toJsonString(reqInfo.reqDataMap);
//            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
//            while (map1it.hasNext()){
//                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
//                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
//                formBody.add(entry.getKey(),(String)entry.getValue());
//            }
        }

        if (null != reqInfo.reqDataMap && !reqInfo.reqDataMap.isEmpty()){
            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
            while (map1it.hasNext()){
                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
                if (StringUtil.isEmpty(entry.getKey())){
                    url = url+entry.getValue();
                }else {
//                    urlBuilder.addQueryParameter(entry.getKey(),(String) entry.getValue());
                }
            }
        }
        LogUtil.e("hm","request param："+jsonParam);
        RequestBody JsonBody= RequestBody.create(HttpConfig.JSON,jsonParam);
        LogUtil.e("request","url="+url);
        headerMap.put("authorization", KWApplication.getInstance().token);
        Headers headers = Headers.of(headerMap);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(JsonBody)
                .build();
        httpClient.newCall(request).enqueue(callback);
    }

    public void requestPostMD5JSON(Callback callback) {

        if (null == reqInfo){
            LogUtil.e("request","Req NetWork:reqInfo is null");
            return;
        }

        if (!NetUtil.isNetworkAvailable(reqInfo.context)){
            ResponseInfo responseInfo = new ResponseInfo(-1,"无可用网络");
            reqInfo.handler.sendMessage(reqInfo.handler.obtainMessage(Constants.REQ_NETWORK_ERROR,responseInfo));
            return;
        }

        String url = reqInfo.reqUrl;

//        FormBody.Builder formBody = new FormBody.Builder();

//        StringBuffer buffer = new StringBuffer();
        String jsonParam ="";
        if (null != reqInfo.reqDataMap && !reqInfo.reqDataMap.isEmpty()){

            jsonParam = GsonHelper.toJsonString(reqInfo.reqDataMap);
//            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
//            while (map1it.hasNext()){
//                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
//                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
//                formBody.add(entry.getKey(),(String)entry.getValue());
//            }
        }
        LogUtil.e("hm","request param："+jsonParam);
        HashMap<String,Object> jsonMap = new HashMap<>();
        try {
            String value = DesCoderUtil.encryptDES(jsonParam, NetUtil.token.substring(NetUtil.token.length()-8));
            jsonMap.put("data",value);
            LogUtil.e("hm","request param："+value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody JsonBody= null;
        try {
            JsonBody = RequestBody.create(HttpConfig.JSON, GsonHelper.toJsonString(jsonMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e("request","url="+url);
        Headers headers = Headers.of(headerMap);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(JsonBody)
                .build();
        httpClient.newCall(request).enqueue(callback);
    }

    public void requestGetJSON(Callback callback){
        if (null == reqInfo){
            LogUtil.e("request","Req NetWork:reqInfo is null");
            return;
        }

        if (!NetUtil.isNetworkAvailable(reqInfo.context)){
            ResponseInfo responseInfo = new ResponseInfo(-1,"无可用网络");
            reqInfo.handler.sendMessage(reqInfo.handler.obtainMessage(Constants.REQ_NETWORK_ERROR,responseInfo));
            return;
        }
        String url = reqInfo.reqUrl;

//        HttpUrl.Builder urlBuilder =HttpUrl.parse(url)
//                .newBuilder();

        if (null != reqInfo.reqDataMap && !reqInfo.reqDataMap.isEmpty()){
            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
            while (map1it.hasNext()){
                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
                if (StringUtil.isEmpty(entry.getKey())){
                    url = url+entry.getValue();
                }else {
//                    urlBuilder.addQueryParameter(entry.getKey(),(String) entry.getValue());
                }
            }
        }
        LogUtil.e("request","url="+url);
        headerMap.put("authorization", KWApplication.getInstance().token);
        Headers headers = Headers.of(headerMap);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .get()
                .build();
        httpClient.newCall(request).enqueue(callback);
    }


    public void requestGet(Callback callback){
        if (reqInfo == null){
            LogUtil.e("request","Req NetWork:reqInfo is null");
            return;
        }
        if (!NetUtil.isNetworkAvailable(reqInfo.context)){
            ResponseInfo responseInfo = new ResponseInfo(-1,"无可用网络");
            reqInfo.handler.sendMessage(reqInfo.handler.obtainMessage(Constants.REQ_NETWORK_ERROR,responseInfo));
            return;
        }
        long downfileSize = 0;
        if (reqInfo.file != null && reqInfo.file.exists())
            downfileSize = reqInfo.file.length();

        String url = HttpConfig.HOST+reqInfo.reqUrl;

        if (!TextUtils.isEmpty(reqInfo.downUrl))
            url = reqInfo.downUrl;

//        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Range","bytes="+downfileSize+"-"+reqInfo.fileSize);
        Headers headers = Headers.of(headerMap);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .get()
                .build();
        httpClient.newCall(request).enqueue(callback);
    }
    public void requestGetImage(Callback callback){
        if (reqInfo == null){
            LogUtil.e("hm","Req NetWork:reqInfo is null");
            return;
        }

        if (!NetUtil.isNetworkAvailable(reqInfo.context)){
            ResponseInfo responseInfo = new ResponseInfo(-1,"无可用网络");
            reqInfo.handler.sendMessage(reqInfo.handler.obtainMessage(Constants.REQ_NETWORK_ERROR,responseInfo));
            return;
        }
        String url = reqInfo.downUrl;
        if (null != reqInfo.reqDataMap && !reqInfo.reqDataMap.isEmpty()){
            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
            while (map1it.hasNext()){
                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
                if (StringUtil.isEmpty(entry.getKey())){
                    url = url+entry.getValue();
                }else {
//                    urlBuilder.addQueryParameter(entry.getKey(),(String) entry.getValue());
                }
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        httpClient.newCall(request).enqueue(callback);
    }


    /**
     * post请求，json，带文件上传，暂时不知道是服务端问题，还是本地代码写的有问题
     */
    public void requestForm(Callback callback){
        if (reqInfo == null){
            return;
        }
        String url = reqInfo.reqUrl;

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        String reqJson = "";
        if (null != reqInfo.reqDataMap && !reqInfo.reqDataMap.isEmpty()){

            Iterator iter = reqInfo.reqDataMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
                multipartBodyBuilder.addFormDataPart(String.valueOf(key),String.valueOf(val));
            }
        }


        if (reqInfo.fileDataMap != null && !reqInfo.fileDataMap.isEmpty()){
            Iterator iter = reqInfo.fileDataMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
                File file=new File(String.valueOf(val));
                RequestBody fileBody= RequestBody.create(HttpConfig.FILE,file);
                multipartBodyBuilder.addFormDataPart(String.valueOf(key),String.valueOf(key)+".jpg",fileBody);
            }
        }

        LogUtil.e("hm","url="+url);
//        Map<String, String> headerMap = new HashMap<>();
//        //mt_from=app
//        headerMap.put("mt_from","app");
//        LogUtil.getSelf().i("authorization="+getSkey());
//        headerMap.put("authorization",getSkey());
//        Headers headers = Headers.of(headerMap);
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build())
                .build();
        httpClient.newCall(request).enqueue(callback);
    }

}
