package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.model.ItemOilOrderBean;
import com.kayu.utils.GsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
public class OilOrderListDataParser extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject responseJson = new JSONObject(jsonStr);
        int state = responseJson.optInt("status");
        String message = responseJson.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(state,message);
        if (state == 1) {
            JSONArray dataJson = responseJson.optJSONArray("data");
            if (null != dataJson && dataJson.length() > 0){
                ArrayList<ItemOilOrderBean> listArray = new ArrayList<ItemOilOrderBean>();
                for (int x = 0; x < dataJson.length(); x++) {
                    ItemOilOrderBean stationBean = GsonHelper.fromJson(dataJson.get(x).toString(), ItemOilOrderBean.class);
                    listArray.add(stationBean);
                }
                responseInfo.responseData = listArray;
            }
        }
        return responseInfo;
    }
}
