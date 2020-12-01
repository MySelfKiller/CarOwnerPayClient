package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.model.ItemMessageBean;
import com.kayu.car_owner_pay.ui.income.IncomeDetailedData;
import com.kayu.utils.GsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageListParser extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        int status = jsonObject.optInt("status");
        String msg = jsonObject.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(status,msg);
//        PersionalNotice notice = GsonHelper.fromJson(jsonStr,PersionalNotice.class);
        JSONObject dataObj = jsonObject.optJSONObject("data");
        if (null != dataObj) {
            JSONArray noticeArr = dataObj.optJSONArray("list");
            if (null != noticeArr &&noticeArr.length()>0){
                List<ItemMessageBean> dataList= new ArrayList<>();
                for (int x = 0; x<noticeArr.length(); x++){
                    JSONObject obj = (JSONObject) noticeArr.get(x);
                    ItemMessageBean noticeData = GsonHelper.fromJson(obj.toString(), ItemMessageBean.class);
                    dataList.add(noticeData);
                }
                responseInfo.responseData = dataList;
            }
        }
        return responseInfo;
    }
}
