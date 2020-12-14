package com.kayu.car_owner_pay.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validator.PhoneValidator;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.SMSCountDownTimer;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;

public class OrderTrackingActivity extends AppCompatActivity {

    private EditText ver_code_et;
    private EditText phone_et;
    private TextView send_ver_code;
    private AppCompatButton ask_btn;
    private SMSCountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("查询订单");

        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
//        TextView back_tv = view.findViewById(R.id.title_back_tv);
//        back_tv.setText("我的");
        phone_et = findViewById(R.id.tracking_phone_et);
        ver_code_et = findViewById(R.id.tracking_ver_code_et);
        send_ver_code = findViewById(R.id.tracking_send_ver_code);
        ask_btn = findViewById(R.id.tracking_ask_btn);

        phone_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        ver_code_et.setInputType(InputType.TYPE_CLASS_NUMBER);

        timer = new SMSCountDownTimer(send_ver_code,60*1000*2,1000);
        send_ver_code.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_et);
                phoneValiv.addValidator(new PhoneValidator(OrderTrackingActivity.this));
                form.addValidates(phoneValiv);
                boolean isOk = form.validate();
                if (isOk){

                    timer.start();
                    sendSmsRequest();
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });


        ask_btn = findViewById(R.id.tracking_ask_btn);
        ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_et);
                phoneValiv.addValidator(new PhoneValidator(OrderTrackingActivity.this));
                form.addValidates(phoneValiv);
                Validate smsValiv = new Validate(ver_code_et);
                form.addValidates(smsValiv);

                boolean isOk = form.validate();
                if (isOk){
//                    sendSubRequest();
                    Intent intent = new Intent(OrderTrackingActivity.this, OrderDetailsActivity.class);
                    intent.putExtra("phone",phone_et.getText().toString().trim());
                    intent.putExtra("code",ver_code_et.getText().toString().trim());
                    startActivity(intent);
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void sendSmsRequest() {
        WaitDialog.show(OrderTrackingActivity.this,"发送验证码...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = OrderTrackingActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_ORDER_CODE;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("",phone_et.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    ToastUtils.show("验证码发送成功");
                }else {
                    timer.clear();
                    ToastUtils.show(resInfo.msg);
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestGetJSON(callback);
    }

}