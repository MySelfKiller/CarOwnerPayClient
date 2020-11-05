package com.kayu.car_owner_pay.http.parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.model.LoginInfo;
import com.kayu.utils.GsonHelper;

import org.json.JSONObject;

public class LoginDataParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject obj = new JSONObject(jsonStr);
        int status = obj.optInt("status");
        String msg = obj.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
        if (status==1){
            JSONObject dataObj = obj.optJSONObject("data");
            if (null != dataObj) {
                LoginInfo userBean = GsonHelper.fromJson(obj.optJSONObject("data").toString(), LoginInfo.class);
                if (userBean != null){
                    responseInfo.responseData = userBean;
                }
            }
        }
        return responseInfo;
    }
}
