package com.kayu.car_owner_pay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kayu.car_owner_pay.data_parser.AliPayDataParse;
import com.kayu.car_owner_pay.data_parser.WxPayDataParse;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.NormalIntParse;
import com.kayu.car_owner_pay.wxapi.AliPayBean;
import com.kayu.car_owner_pay.wxapi.WxPayBean;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;

public class PayOrderViewModel extends ViewModel {

    private MutableLiveData<WxPayBean> wxPayLiveData;
    private MutableLiveData<AliPayBean> alipayLiveData;


    public LiveData<AliPayBean> getAliPayInfo(Context context, String shopCode,String serviceCode) {
        alipayLiveData = new MutableLiveData<>();
        loadPayInfo(context,shopCode,serviceCode,2);
        return alipayLiveData;
    }
    public LiveData<WxPayBean> getWeChatPayInfo(Context context, String shopCode,String serviceCode) {
        wxPayLiveData = new MutableLiveData<>();
        loadPayInfo(context,shopCode,serviceCode,1);
        return wxPayLiveData;
    }
    public void cancelPay(Context context, long orderId) {
        cancelPayInfo(context,orderId);
    }

    @SuppressLint("HandlerLeak")
    private void loadPayInfo(Context context, String shopCode,String serviceCode, int payFlag) {
        WaitDialog.show((AppCompatActivity) context,"获取支付信息。。。");
        RequestInfo reques = new RequestInfo();
        reques.context = context;
//        if (orderFlag == 1) {
//            reques.reqUrl = HttpConfig.HOST + HttpConfig.interface_get_pay_course;
//        } else {
//        }
            reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_PAY;
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("shopCode", shopCode);
        reqDateMap.put("serviceCode", serviceCode);
        reqDateMap.put("payWay", payFlag);
        reques.reqDataMap = reqDateMap;
        if (payFlag == 1) {
            reques.parser = new WxPayDataParse();
        } else {
            reques.parser = new AliPayDataParse();

        }

        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                WaitDialog.dismiss();
                if (resInfo.status == 1) {
                    if (payFlag ==1) {//微信订单
                        WxPayBean wxPayBean = (WxPayBean) resInfo.responseData;
                        wxPayLiveData.setValue(wxPayBean);
                    } else {//支付宝订单信息
                        AliPayBean wxPayBean = (AliPayBean) resInfo.responseData;
                        alipayLiveData.setValue(wxPayBean);
                    }



                } else {
                    Toast.makeText(context, resInfo.msg, Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

    @SuppressLint("HandlerLeak")
    private void cancelPayInfo(Context context, long orderId) {
        RequestInfo reques = new RequestInfo();
        reques.context = context;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_PAY_CANCEL;
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("id", orderId);
        reques.reqDataMap = reqDateMap;
        reques.parser = new NormalIntParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestPostJSON(callback);
    }
}
