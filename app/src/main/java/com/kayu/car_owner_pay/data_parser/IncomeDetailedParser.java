package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.ui.income.IncomeDetailedData;
import com.kayu.utils.GsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IncomeDetailedParser extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        int status = jsonObject.optInt("status");
        String msg = jsonObject.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
//        PersionalNotice notice = GsonHelper.fromJson(jsonStr,PersionalNotice.class);
        JSONArray noticeArr = jsonObject.optJSONArray("data");
        if (null != noticeArr &&noticeArr.length()>0){
            List<IncomeDetailedData> dataList= new ArrayList<>();
            for (int x = 0; x<noticeArr.length(); x++){
                JSONObject obj = (JSONObject) noticeArr.get(x);
                IncomeDetailedData noticeData = GsonHelper.fromJson(obj.toString(),IncomeDetailedData.class);
                dataList.add(noticeData);
            }
            responseInfo.responseData = dataList;
        }
        return responseInfo;
    }
}
