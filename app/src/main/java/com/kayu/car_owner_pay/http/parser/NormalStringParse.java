package com.kayu.car_owner_pay.http.parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;

import org.json.JSONObject;

public class NormalStringParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        int status = jsonObject.optInt("status");
        String msg = jsonObject.optString("message");
        String data = jsonObject.optString("data");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
        responseInfo.responseData = data;

        return responseInfo;
    }
}
