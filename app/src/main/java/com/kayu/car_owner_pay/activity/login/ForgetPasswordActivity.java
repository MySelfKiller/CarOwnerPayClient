package com.kayu.car_owner_pay.activity.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.BaseActivity;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validate.ConfirmValidate;
import com.kayu.form_verify.validator.NotEmptyValidator;
import com.kayu.form_verify.validator.PhoneValidator;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.SMSCountDownTimer;
import com.kongzue.dialog.v3.TipGifDialog;

import java.util.HashMap;

public class ForgetPasswordActivity extends BaseActivity {

    private EditText code_edt;
    private EditText phone_edt;
    private EditText new_password_edt;
    private EditText new_password2_edt;
    private AppCompatButton ask_btn;
    private TextView send_sms_tv;
    private SMSCountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //标题栏
        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("忘记密码");

        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = findViewById(R.id.title_back_tv);
        back_tv.setText("返回");

        code_edt = findViewById(R.id.forget_code);
        phone_edt = findViewById(R.id.forget_number_edt);
        new_password_edt = findViewById(R.id.forget_new_password);
        new_password2_edt = findViewById(R.id.forget_new_password2);
        send_sms_tv = findViewById(R.id.forget_send_sms_tv);
        timer = new SMSCountDownTimer(send_sms_tv,60*1000,1000);
        send_sms_tv.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_edt);
                phoneValiv.addValidator(new PhoneValidator(ForgetPasswordActivity.this));
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
        ask_btn = findViewById(R.id.forget_ask_btn);
        ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValidate = new Validate(phone_edt);
                phoneValidate.addValidator(new PhoneValidator(ForgetPasswordActivity.this));
                form.addValidates(phoneValidate);

                Validate codeValidate = new Validate(code_edt);
                codeValidate.addValidator(new NotEmptyValidator(ForgetPasswordActivity.this));
                form.addValidates(codeValidate);

                ConfirmValidate newPassValidate = new ConfirmValidate(new_password_edt,new_password2_edt);
                newPassValidate.addValidator(new NotEmptyValidator(ForgetPasswordActivity.this));
                form.addValidates(newPassValidate);
                if (form.validate()){
                    reqReSetPasswrod();
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void reqReSetPasswrod() {
        TipGifDialog.show(ForgetPasswordActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = ForgetPasswordActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+ HttpConfig.INTERFACE_RESET_PASSWORD;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("phone",phone_edt.getText().toString().trim());
        reqDateMap.put("code",code_edt.getText().toString().trim());
        reqDateMap.put("newPwd",new_password2_edt.getText().toString().trim());
//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TipGifDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    ToastUtils.show("密码已经重置成功！");
                    finish();
                }else {
                    ToastUtils.show(resInfo.msg);
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

    @SuppressLint("HandlerLeak")
    private void sendSmsRequest() {
        TipGifDialog.show(ForgetPasswordActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = ForgetPasswordActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_VERIFICATION_CODE;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("",phone_edt.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TipGifDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    ToastUtils.show("验证码发送成功");
                }else {
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
