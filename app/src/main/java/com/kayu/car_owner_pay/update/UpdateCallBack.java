package com.kayu.car_owner_pay.update;

import android.os.Handler;

import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.utils.Constants;
import com.kayu.utils.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hm on 2018/3/14.
 */

public class UpdateCallBack implements Callback {
    private RequestInfo reqInfo = null;
    private Handler handler = null;

    public UpdateCallBack(RequestInfo info){
        reqInfo = info;
        handler = reqInfo.handler;
    }
    @Override
    public void onFailure(Call call, IOException e) {
        Handler handler = reqInfo.handler;
        ResponseInfo responseInfo = new ResponseInfo(-1,"网络异常");
        handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS,responseInfo));
        LogUtil.e("network req","IOException: "+e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String result = response.body().string();
        LogUtil.e("network req","errorcode:"+response.code());
        LogUtil.e("network req","返回的数据: "+result);

        ResponseInfo obj = null;
        if (null == result){
            obj = new ResponseInfo(-1,"网络异常");
            handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS,obj));
            return;
        }
        try {
            obj = (ResponseInfo) reqInfo.parser.parseJSON(reqInfo.handler,result,0);
        } catch (Exception e) {
            e.printStackTrace();
            obj = new ResponseInfo(-1,"服务器出错，稍后重试");
            handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS,obj));
        }
        handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS,obj));
    }
}
