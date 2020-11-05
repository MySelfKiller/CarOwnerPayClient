package com.kayu.car_owner_pay.http.parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;

import org.json.JSONObject;

public class NormalParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        int status = jsonObject.optInt("status");
        String msg = jsonObject.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);

        return responseInfo;
    }
}
