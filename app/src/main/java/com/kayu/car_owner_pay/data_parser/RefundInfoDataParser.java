package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.model.RefundInfo;
import com.kayu.utils.GsonHelper;

import org.json.JSONObject;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
public class RefundInfoDataParser extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject responseJson = new JSONObject(jsonStr);
        int state = responseJson.optInt("status");
        String message = responseJson.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(state, message);
        if (state == 1) {
            JSONObject dataJson = responseJson.optJSONObject("data");
            if (null != dataJson && dataJson.length() > 0) {
//                ArrayList<WashStationBean> listArray = new ArrayList<WashStationBean>();
                responseInfo.responseData = GsonHelper.fromJson(dataJson.toString(), RefundInfo.class);
            }
        }
        return responseInfo;
    }
}
