package com.kayu.car_owner_pay.http.parser;

import android.os.Handler;

import org.json.JSONException;

/**
 * Created by HuangMin on 2016/7/10.
 */
public abstract class BaseParse<ResponseInfo> {

    /**
     * json数据解析
     * @param jsonStr json数据串
     * @param handler
     * @return 解析后的Object
     * @throws JSONException
     */
    public abstract ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception;

//	 public String checkResponse(String paramString) throws JSONException{
//		if(paramString==null || "".equals(paramString.trim())){
//			return null;
//		}else{
//			JSONObject jsonObject = new JSONObject(paramString);
//
//			if(jsonObject!=null && !jsonObject.equals("error")){
//				return result;
//			}else{
//				return null;
//			}
//
//		}
//	 }
}
