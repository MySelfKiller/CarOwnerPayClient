package com.kayu.car_owner_pay.data_parser;

import android.os.Handler;

import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.BaseParse;
import com.kayu.car_owner_pay.model.ParamWashBean;
import com.kayu.car_owner_pay.model.WashParam;
import com.kayu.utils.GsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
public class ParamWashDataParser extends BaseParse<ResponseInfo> {
    @Override
    public ResponseInfo parseJSON(Handler handler, String jsonStr, double dataVersion) throws Exception {
        JSONObject responseJson = new JSONObject(jsonStr);
        int state = responseJson.optInt("status");
        String message = responseJson.optString("message");
        ResponseInfo responseInfo = new ResponseInfo(state,message);
        if (state == 1) {
            JSONObject dataJson = responseJson.optJSONObject("data");
            ParamWashBean paramSelect = new ParamWashBean();
            if (null != dataJson){
                JSONArray distanceJson = dataJson.optJSONArray("des");
                if (null != distanceJson && distanceJson.length() > 0) {
                    ArrayList<WashParam> distanceList = new ArrayList<WashParam>();
                    for (int x = 0; x < distanceJson.length(); x++) {
                        WashParam stationBean = GsonHelper.fromJson(distanceJson.get(x).toString(), WashParam.class);
                        distanceList.add(stationBean);
                    }
                    paramSelect.desList = distanceList;
                }
                JSONArray sortsJson = dataJson.optJSONArray("types");
                if (null != sortsJson && sortsJson.length() > 0) {
                    ArrayList<WashParam> sortsList = new ArrayList<WashParam>();
                    for (int x = 0; x < sortsJson.length(); x++) {
                        WashParam WashParam = GsonHelper.fromJson(sortsJson.get(x).toString(), WashParam.class);
                        sortsList.add(WashParam);
                    }
                    paramSelect.typesList = sortsList;
                }
//                JSONArray oilsJson = dataJson.optJSONArray("oils");
//                if (null != oilsJson && oilsJson.length() > 0) {
//                    ArrayList<OilsTypeParam> oilsTypeParamList = new ArrayList<OilsTypeParam>();
//                    for (int x = 0; x < oilsJson.length(); x++) {
//                        OilsTypeParam oilsTypeParam = GsonHelper.fromJson(oilsJson.get(x).toString(), OilsTypeParam.class);
//                        if (null != oilsTypeParam) {
//                            JSONObject ibj = (JSONObject) oilsJson.get(x);
//                            JSONArray oilJsonArr = ibj.optJSONArray("list");
//                            if (null != oilJsonArr && oilJsonArr.length() > 0) {
//                                ArrayList<OilsParam> oilsParamList = new ArrayList<>();
//                                for (int y = 0; y < oilJsonArr.length(); y++) {
//                                    OilsParam oilsParam = GsonHelper.fromJson(oilJsonArr.get(y).toString(), OilsParam.class);
//                                    oilsParamList.add(oilsParam);
//                                }
//                                oilsTypeParam.oilsParamList = oilsParamList;
//                            }
//                        }
//                        oilsTypeParamList.add(oilsTypeParam);
//                    }
//                    paramSelect.oilsTypeParamList = oilsTypeParamList;
//                }
                responseInfo.responseData = paramSelect;
            }
        }
        return responseInfo;
    }
}
