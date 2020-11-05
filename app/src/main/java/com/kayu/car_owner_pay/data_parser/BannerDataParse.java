package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.model.BannerBean;
import com.kayu.utils.GsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BannerDataParse extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject obj = new JSONObject(jsonStr);
        int status = obj.optInt("status");
        String msg = obj.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
        if (status==1){
            JSONArray dataArr = obj.optJSONArray("data");
            if (null != dataArr && dataArr.length()>0){
                List<BannerBean> itemDataList = new ArrayList<>();
                for (int x = 0; x<dataArr.length(); x++){
                    BannerBean itemData = GsonHelper.fromJson(dataArr.get(x).toString(), BannerBean.class);
                    itemDataList.add(itemData);
                }
                responseInfo.responseData = itemDataList;
            }
        }
        return responseInfo;
    }
}
