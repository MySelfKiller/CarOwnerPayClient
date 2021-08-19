package com.kayu.car_owner_pay;

import android.webkit.JavascriptInterface;

import com.kayu.utils.DesCoderUtil;

import java.nio.charset.StandardCharsets;

/**
 * 配置淘油宝的js调用信息
 */
public class SettingInterface {
    private String data;
    private String gasId;
    public SettingInterface(String data,String gasId) {
        this.data = data;
        this.gasId = gasId;

    }
    String json = "{\"app_id\": \"2a10fa39e3546d256bf993f546b6d73b\", \"secret\":\"fdbab8561f7138914179b773a732e1aa\"}";
    @JavascriptInterface
    public String tybRegisterData() {
        try {
            return DesCoderUtil.decryptDES(data,gasId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
