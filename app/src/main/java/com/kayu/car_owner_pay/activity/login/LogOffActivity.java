package com.kayu.car_owner_pay.activity.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.AppManager;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.LoginDataParse;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.car_owner_pay.model.UserBean;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validator.NotEmptyValidator;
import com.kayu.form_verify.validator.PhoneValidator;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.SMSCountDownTimer;
import com.kayu.utils.location.LocationManagerUtil;
import com.kongzue.dialog.v3.TipGifDialog;

import java.util.HashMap;
import java.util.regex.Pattern;

public class LogOffActivity extends AppCompatActivity {

    private EditText phone_number;
    private EditText sms_code;
    private AppCompatButton ask_btn;
    private SMSCountDownTimer timer;
    private TextView send_sms;
    private UserBean useData;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_off);

        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("账号注销");

        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
        String jsonUser = sp.getString(Constants.userInfo,"");
        useData = GsonHelper.fromJson(jsonUser, UserBean.class);

        phone_number = findViewById(R.id.logoff_number_edt);
        send_sms = findViewById(R.id.logoff_send_sms_tv);
        sms_code = findViewById(R.id.logoff_sms_code_edt);
        sms_code.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filters = {new InputFilter.LengthFilter(4)};
        sms_code.setFilters(filters);
        sms_code.setHint("请输入验证码");

        ask_btn = findViewById(R.id.logoff_ask_btn);
        ask_btn.setClickable(false);
        ask_btn.setEnabled(false);

        phone_number.setText(useData.phone);
        phone_number.setClickable(false);
        phone_number.setEnabled(false);


        phone_number.addTextChangedListener(new TextWatcher() {
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
                    send_sms.setClickable(true);
                    send_sms.setTextColor(getResources().getColor(R.color.colorAccent));
                }else {
                    send_sms.setClickable(false);
                    send_sms.setTextColor(getResources().getColor(R.color.grayText));
                }

                Pattern pasPattern= Pattern.compile("[0-9]{4}$");
                if (pattern.matcher(s).matches() && pasPattern.matcher(sms_code.getText().toString().trim()).matches()){
                    ask_btn.setClickable(true);
                    ask_btn.setEnabled(true);
                    ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
                    ask_btn.setTextColor(getResources().getColor(R.color.slight_yellow));
                }else {
                    ask_btn.setEnabled(false);
                    ask_btn.setClickable(false);
                    ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
                    ask_btn.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });

        send_sms.setClickable(false);
        sms_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern pattern= Pattern.compile("^1[0-9]{10}$");
                Pattern pasPattern= Pattern.compile("[0-9]{4}$");
                if ( pasPattern.matcher(s).matches()){
                    ask_btn.setClickable(true);
                    ask_btn.setEnabled(true);
                    ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
                }else {
                    ask_btn.setEnabled(false);
                    ask_btn.setClickable(false);
                    ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
                }
            }
        });


        timer = new SMSCountDownTimer(send_sms,60*1000*2,1000);
        send_sms.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_number);
                phoneValiv.addValidator(new NotEmptyValidator(LogOffActivity.this));
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
        ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_number);
                phoneValiv.addValidator(new NotEmptyValidator(LogOffActivity.this));
                form.addValidates(phoneValiv);
                Validate smsValiv = new Validate(sms_code);
                smsValiv.addValidator(new NotEmptyValidator(LogOffActivity.this));
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
    }

    @SuppressLint("HandlerLeak")
    private void sendSmsRequest() {
        TipGifDialog.show(LogOffActivity.this, "发送验证码...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = LogOffActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_VERIFICATION_CODE;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("","18888888888");
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
        TipGifDialog.show(LogOffActivity.this, "确认中...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = LogOffActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_LOGIN;
        reqInfo.parser = new LoginDataParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("phone","18888888887");
        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TipGifDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constants.isLogin, false);
                    editor.putString(Constants.userInfo, "");
                    editor.apply();
                    editor.commit();
                    AppManager.getAppManager().finishAllActivity();
                    LocationManagerUtil.getSelf().stopLocation();
//                                LocationManagerUtil.getSelf().destroyLocation();
                    startActivity(new Intent(LogOffActivity.this, LoginAutoActivity.class));
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
}