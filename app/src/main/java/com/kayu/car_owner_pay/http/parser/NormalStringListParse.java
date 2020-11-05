package com.kayu.car_owner_pay.http.parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NormalStringListParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        int status = jsonObject.optInt("status");
        String msg = jsonObject.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
        if (status == 1) {
            JSONArray data = jsonObject.optJSONArray("data");
            if (null != data && data.length() > 0) {
                List<String> strList = new ArrayList<>();
                for (int x = 0; x < data.length(); x++) {
                    strList.add((String) data.get(x));
                    responseInfo.responseData = strList;
                }
            }
        }
        return responseInfo;
    }
}
