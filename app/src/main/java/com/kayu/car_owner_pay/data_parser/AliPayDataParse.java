package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.wxapi.AliPayBean;
import com.kayu.utils.GsonHelper;

import org.json.JSONObject;

public class AliPayDataParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject obj = new JSONObject(jsonStr);
        int status = obj.optInt("status");
        String msg = obj.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
        if (status==1){
            JSONObject dataObj = obj.optJSONObject("data");
            if (null != dataObj && dataObj.length()>0){
                AliPayBean subItem = GsonHelper.fromJson(dataObj.toString(), AliPayBean.class);
                responseInfo.responseData = subItem;
            }
        }
        return responseInfo;
    }
}
