package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.model.OilStationBean;
import com.kayu.car_owner_pay.model.OilsParam;
import com.kayu.car_owner_pay.model.OilsTypeParam;
import com.kayu.utils.GsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
public class StationDetailDataParser extends BaseParse {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject responseJson = new JSONObject(jsonStr);
        int state = responseJson.optInt("status");
        String message = responseJson.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(state,message);
        if (state == 1) {
            JSONObject dataJson = responseJson.optJSONObject("data");
            if (null != dataJson){
                OilStationBean stationBean = GsonHelper.fromJson(dataJson.toString(), OilStationBean.class);
                JSONArray oilTypeJsonArr = dataJson.optJSONArray("oilTypes");
                if (null != stationBean && null != oilTypeJsonArr && oilTypeJsonArr.length() > 0) {
                    ArrayList<OilsTypeParam> listArray = new ArrayList<OilsTypeParam>();
                    for (int x = 0; x < oilTypeJsonArr.length(); x++) {
                        OilsTypeParam oilsTypeParam = GsonHelper.fromJson(oilTypeJsonArr.get(x).toString(), OilsTypeParam.class);
                        if (null != oilsTypeParam) {
                            JSONObject ibj = (JSONObject) oilTypeJsonArr.get(x);
                            JSONArray oilJsonArr = ibj.optJSONArray("list");
                            if (null != oilJsonArr && oilJsonArr.length() > 0) {
                                ArrayList<OilsParam> oilsParamList = new ArrayList<>();
                                for (int y = 0; y < oilJsonArr.length(); y++) {
                                    OilsParam oilsParam = GsonHelper.fromJson(oilJsonArr.get(y).toString(), OilsParam.class);
                                    oilsParamList.add(oilsParam);
                                }
                                oilsTypeParam.oilsParamList = oilsParamList;
                            }
                        }
                        listArray.add(oilsTypeParam);
                    }
                    stationBean.oilsTypeList  = listArray;
                }
                responseInfo.responseData = stationBean;
            }
        }
        return responseInfo;
    }
}
