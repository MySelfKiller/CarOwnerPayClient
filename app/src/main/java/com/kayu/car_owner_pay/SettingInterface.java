package com.kayu.car_owner_pay;

import android.webkit.JavascriptInterface;

import com.kayu.utils.DesCoderUtil;
import com.kayu.utils.LogUtil;
import com.kayu.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * 配置淘油宝的js调用信息
 */
public class SettingInterface {
    private String data;
    private String gasId;
    private String jsonData = "";
    public SettingInterface(String data,String gasId) {
        this.data = data;
        this.gasId = gasId;
        if (!StringUtil.isEmpty(data)) {
            try {
                jsonData = DesCoderUtil.decryptDES(data,gasId);
            } catch (Exception e) {
                e.printStackTrace();//https://tyb-qa-api.nucarf.cn/pay/#/toPay?order_sn=2021082017440048559749&amount=3
            }
        }
    }
//    String json = "{\"app_id\": \"2a10fa39e3546d256bf993f546b6d73b\", \"secret\":\"fdbab8561f7138914179b773a732e1aa\"}";
    @JavascriptInterface
    public String tybRegisterData() {
        return jsonData;
    }

    @JavascriptInterface
    public String openMiniProgram(String  data)  {
        JSONObject sssd= null;
        LogUtil.e("---支付返回数据---",data);
        try {
            sssd = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  "{\"app_id\": "+sssd.optString("app_id")+", \"type\":\"1\",\"url\":\"https://tyb-qa-api.nucarf.cn/pay/#/toPay?order_sn=2021082017440048559749&amount=3\"}";
    }
}
