package com.kayu.car_owner_pay.wxapi;

public interface OnResponseListener {
    void onSuccess();

    void onCancel();

    void onFail(String message);
}
