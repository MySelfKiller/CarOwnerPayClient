package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.login.LoginAutoActivity;
import com.kayu.car_owner_pay.data_parser.ActvInfoParse;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.LoginDataParse;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.car_owner_pay.model.ActivationCard;
import com.kayu.car_owner_pay.model.LoginInfo;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validator.PhoneValidator;
import com.kayu.utils.Constants;
import com.kayu.utils.DeviceIdUtils;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.SMSCountDownTimer;
import com.kayu.utils.StringUtil;
import com.kongzue.dialog.v3.TipGifDialog;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ActivationActivity extends BaseActivity {

    private AppCompatButton ask_btn;
    private TextView send_ver_code;
    private EditText ver_code_et;
    private EditText card_num_et;
    private EditText phone_et;
    private EditText code_et;
    private SMSCountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("激活"+getResources().getString(R.string.app_name)+"特权卡");

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

        phone_et = findViewById(R.id.activation_phone_et);
        phone_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        card_num_et = findViewById(R.id.activation_card_num_et);
        code_et = findViewById(R.id.activation_code_et);
        card_num_et.setClickable(false);
        card_num_et.setFocusable(false);
        code_et.setClickable(false);
        code_et.setFocusable(false);
        ver_code_et = findViewById(R.id.activation_ver_code_et);
        ver_code_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        send_ver_code = findViewById(R.id.activation_send_ver_code);
        timer = new SMSCountDownTimer(send_ver_code,60*1000*2,1000);
        send_ver_code.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_et);
                phoneValiv.addValidator(new PhoneValidator(ActivationActivity.this));
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


        ask_btn = findViewById(R.id.activation_ask_btn);
        ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_et);
                phoneValiv.addValidator(new PhoneValidator(ActivationActivity.this));
                form.addValidates(phoneValiv);
                Validate smsValiv = new Validate(ver_code_et);
                form.addValidates(smsValiv);

                boolean isOk = form.validate();
                if (isOk){
                    sendSubRequest();
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern pattern= Pattern.compile("^1[0-9]{10}$");
                if (pattern.matcher(s).matches()){
                    Form form = new Form();
                    Validate phoneValiv = new Validate(phone_et);
                    phoneValiv.addValidator(new PhoneValidator(ActivationActivity.this));
                    form.addValidates(phoneValiv);
                    boolean isOk = form.validate();
                    if (isOk){
                        getActivInfo();
                    }
                }else {
                    card_num_et.setText("");
                    code_et.setText("");
                }
            }

        });
    }

    @SuppressLint("HandlerLeak")
    private void sendSmsRequest() {
        TipGifDialog.show(ActivationActivity.this, "发送验证码...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = ActivationActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_VERIFICATION_CODE;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("",phone_et.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TipGifDialog.dismiss();
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


    @SuppressLint("HandlerLeak")
    private void sendSubRequest() {
        TipGifDialog.show(ActivationActivity.this, "确认中...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = ActivationActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_LOGIN;
        reqInfo.parser = new LoginDataParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("phone",phone_et.getText().toString().trim());
        reqDateMap.put("code",ver_code_et.getText().toString().trim());
        String imei = DeviceIdUtils.getIMEI(getApplicationContext());
        if (!StringUtil.isEmpty(imei))
            reqDateMap.put("imei", imei);

//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TipGifDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    LoginInfo user = (LoginInfo) resInfo.responseData;
                    if (null != user){
                        SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(Constants.isLogin,true);
                        editor.putString(Constants.token,user.token);
                        editor.putBoolean(Constants.isSetPsd,true);
                        editor.putString(Constants.login_info, GsonHelper.toJsonString(user));
                        editor.apply();
                        editor.commit();
                        KWApplication.getInstance().token = user.token;
                        AppManager.getAppManager().finishAllActivity();
                        Intent intent = new Intent(ActivationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }else {
                    Toast.makeText(ActivationActivity.this,resInfo.msg,Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);

    }


    @SuppressLint("HandlerLeak")
    private void getActivInfo() {
        TipGifDialog.show(ActivationActivity.this, "查询中...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = ActivationActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_ACTVINFO;
        reqInfo.parser = new ActvInfoParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("phone",phone_et.getText().toString().trim());
//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TipGifDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    ActivationCard actvInfo = (ActivationCard) resInfo.responseData;
                    if (null != actvInfo){
                        String no = actvInfo.no;
                        String hidNO = no.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                        String hidCode = actvInfo.code;
                        card_num_et.setText(hidNO);
                        code_et.setText(hidCode.replaceAll(actvInfo.code.substring(3),"***"));

                    }
                }
                ToastUtils.show(resInfo.msg);
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);

    }
}