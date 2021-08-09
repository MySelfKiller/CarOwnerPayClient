package com.kayu.car_owner_pay.http;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.activity.AppManager;
import com.kayu.car_owner_pay.activity.login.LoginAutoActivity;
import com.kayu.car_owner_pay.http.cookie.PersistentCookieStore;
import com.kayu.utils.Constants;
import com.kayu.utils.LogUtil;
import com.kayu.utils.location.LocationManagerUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hm on 2018/10/31.
 */

public class ResponseCallback implements Callback {
    private RequestInfo reqInfo = null;
    private Handler handler = null;
    public ResponseCallback(RequestInfo requestInfo){
        reqInfo = requestInfo;
        handler = reqInfo.handler;
    }
    @Override
    public void onFailure(Call call, IOException e) {
//        Handler handler = reqInfo.handler;
        ResponseInfo responseInfo = new ResponseInfo(-1,"网络异常");
        handler.sendMessage(handler.obtainMessage(Constants.REQ_NETWORK_ERROR,responseInfo));
        LogUtil.e("network req","IOException: "+e.toString());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String result = response.body().string();
        LogUtil.e("network req","errorcode:"+response.code());
        LogUtil.e("network req","返回的数据: "+result);

        ResponseInfo obj = null;
        if (response.code()==200){
            if (null == result){
                obj = new ResponseInfo(-1,"网络异常");
                handler.sendMessage(handler.obtainMessage(Constants.REQ_NETWORK_ERROR,obj));
                return;
            }
            try {
                obj = (ResponseInfo) reqInfo.parser.parseJSON(reqInfo.handler,result,0);
                if (obj.status == Constants.response_code_10101 || obj.status == Constants.response_code_10102){
                    handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_ERROR,obj));
                    Intent intent = new Intent("com.kayu.broadcasttest.JUMP");
                    KWApplication.getInstance().localBroadcastManager.sendBroadcast(intent); // 发送本地广播

                } else if (obj.status == Constants.response_code_1) {
                    handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS, obj));
                } else {
                    handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_ERROR,obj));
                }
            } catch (Exception e) {
                e.printStackTrace();
                obj = new ResponseInfo(-1,"服务器出错，稍后重试");
                handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_ERROR,obj));
            }
        }else {
            obj = new ResponseInfo(-1,"网络异常");
            handler.sendMessage(handler.obtainMessage(Constants.REQ_NETWORK_ERROR,obj));
        }
    }
}
