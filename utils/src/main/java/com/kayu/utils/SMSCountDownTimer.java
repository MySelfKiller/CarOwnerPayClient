package com.kayu.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

public class SMSCountDownTimer extends CountDownTimer {
    private View mView;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SMSCountDownTimer(TextView view, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mView = view;
    }
    public SMSCountDownTimer(AppCompatButton view, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mView = view;
    }

    @Override
    public void onTick(long millisUntilFinished) {
//防止计时过程中重复点击
        mView.setClickable(false);
        if (mView instanceof TextView){
            ((TextView)mView).setText(millisUntilFinished/1000+"秒");
        }else if (mView instanceof AppCompatButton){
            ((AppCompatButton)mView).setText(millisUntilFinished/1000+"秒");
        }
    }

    @Override
    public void onFinish() {
        //重新给Button设置文字
        if (mView instanceof TextView){
            ((TextView)mView).setText("重新获取");
        }else if (mView instanceof AppCompatButton){
            ((AppCompatButton)mView).setText("重新获取");
        }
        //设置可点击
        mView.setClickable(true);
    }

    public void clear(){
        this.cancel();
        //重新给Button设置文字
        if (mView instanceof TextView){
            ((TextView)mView).setText("重新获取");
        }else if (mView instanceof AppCompatButton){
            ((AppCompatButton)mView).setText("重新获取");
        }
        //设置可点击
        mView.setClickable(true);
    }
}
