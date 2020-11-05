package com.kayu.car_owner_pay.update;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.utils.GsonHelper;

import org.json.JSONObject;

/**
 * Created by hm on 2018/10/31.
 */

public class UpdateInfoParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
//        LogUtil.e("hm","UpdateInfoParse: jsonStr="+jsonStr);
        JSONObject obj = new JSONObject(jsonStr);
        int status = obj.getInt("status");
        String msg = obj.getString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
        if (status ==0){
            responseInfo.responseData = null;
        }
        if (status==1){
            UpdateInfo updateInfo = GsonHelper.fromJson(obj.getString("data").toString(), UpdateInfo.class);
            if (updateInfo != null){
                responseInfo.responseData = updateInfo;
            }
        }
        return responseInfo;
    }
}
