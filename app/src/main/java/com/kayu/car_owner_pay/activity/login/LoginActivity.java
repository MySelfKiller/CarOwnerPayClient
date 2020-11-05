package com.kayu.car_owner_pay.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.AppManager;
import com.kayu.car_owner_pay.activity.BaseActivity;
import com.kayu.car_owner_pay.activity.MainActivity;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.LoginDataParse;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.car_owner_pay.model.LoginInfo;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validator.PhoneValidator;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.SMSCountDownTimer;
import com.kayu.utils.StringUtil;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity {
    private EditText phone_number;
    private EditText sms_code;
    private AppCompatButton ask_btn;
    private SMSCountDownTimer timer;
    private TextView send_sms;
    private TextView password_target;
    private LinearLayout login_send_sms_lay;
    private LinearLayout password_target_lay;
    private LinearLayout login_sms_target_lay;
    private TextView login_sms_target;
    private TextView login_forget_password;
    private boolean isSMSLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        //标题栏
//        findViewById(R.id.title_menu_btu).setVisibility(View.INVISIBLE);
//        TextView title_name = findViewById(R.id.title_name_tv);
//        title_name.setText(getResources().getString(R.string.title_login));

//        findViewById(R.id.title_menu_btu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        TextView back_tv = findViewById(R.id.title_back_tv);
//        back_tv.setText("微信登录");

        login_send_sms_lay = findViewById(R.id.login_send_sms_lay);
        login_sms_target_lay = findViewById(R.id.login_sms_target_lay);
        password_target_lay = findViewById(R.id.login_password_target_lay);
        password_target = findViewById(R.id.login_password_target);
        login_sms_target = findViewById(R.id.login_sms_target);
        login_sms_target.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                login_sms_target_lay.setVisibility(View.GONE);
                password_target_lay.setVisibility(View.VISIBLE);
                login_send_sms_lay.setVisibility(View.VISIBLE);
                sms_code.setText("");
                sms_code.setHint("请输入验证码");
                sms_code.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputFilter[] filters = {new InputFilter.LengthFilter(4)};
                sms_code.setFilters(filters);
                isSMSLogin = true;
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        login_forget_password = findViewById(R.id.login_forget_password);
        login_forget_password.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        ask_btn = findViewById(R.id.login_ask_btn);
        phone_number = findViewById(R.id.login_number_edt);

        send_sms = findViewById(R.id.login_send_sms_tv);
        sms_code = findViewById(R.id.login_sms_code_edt);
        sms_code.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filters = {new InputFilter.LengthFilter(4)};
        sms_code.setFilters(filters);
        sms_code.setHint("请输入验证码");
        password_target.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                login_send_sms_lay.setVisibility(View.GONE);
                password_target_lay.setVisibility(View.GONE);
                login_sms_target_lay.setVisibility(View.VISIBLE);
                sms_code.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                sms_code.setTypeface(Typeface.DEFAULT);
                sms_code.setTransformationMethod(new PasswordTransformationMethod());
                sms_code.setText("");
                sms_code.setHint("请输入登录密码");

                InputFilter[] filters = {new InputFilter.LengthFilter(25)};
                sms_code.setFilters(filters);
                isSMSLogin = false;
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        ask_btn.setClickable(false);
        ask_btn.setEnabled(false);


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

                if (isSMSLogin){
                    Pattern pasPattern= Pattern.compile("[0-9]{4}$");
                    if (pattern.matcher(s).matches() && pasPattern.matcher(sms_code.getText().toString().trim()).matches()){
                        ask_btn.setClickable(true);
                        ask_btn.setEnabled(true);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
                    }else {
                        ask_btn.setEnabled(false);
                        ask_btn.setClickable(false);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
                    }
                }else {
                    if (pattern.matcher(s).matches() && !StringUtil.isEmpty(sms_code.getText().toString().trim())){
                        ask_btn.setClickable(true);
                        ask_btn.setEnabled(true);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
                    }else {
                        ask_btn.setEnabled(false);
                        ask_btn.setClickable(false);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
                    }
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
                if (isSMSLogin){
                    Pattern pasPattern= Pattern.compile("[0-9]{4}$");
                    if (pattern.matcher(phone_number.getText().toString().trim()).matches() && pasPattern.matcher(s).matches()){
                        ask_btn.setClickable(true);
                        ask_btn.setEnabled(true);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
                    }else {
                        ask_btn.setEnabled(false);
                        ask_btn.setClickable(false);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
                    }
                }else {
                    if (pattern.matcher(phone_number.getText().toString().trim()).matches() && !StringUtil.isEmpty(sms_code.getText().toString().trim())){
                        ask_btn.setClickable(true);
                        ask_btn.setEnabled(true);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
                    }else {
                        ask_btn.setEnabled(false);
                        ask_btn.setClickable(false);
                        ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
                    }
                }
//                if (!StringUtil.isEmpty(s.toString().trim()) && !StringUtil.isEmpty(phone_number.getText().toString().trim())){
//                    ask_btn.setClickable(true);
//                    ask_btn.setEnabled(true);
//                    ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
//                }else {
//                    ask_btn.setClickable(false);
//                    ask_btn.setEnabled(false);
//                    ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
//                }
            }
        });


        timer = new SMSCountDownTimer(send_sms,60*1000*2,1000);
        send_sms.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate phoneValiv = new Validate(phone_number);
                phoneValiv.addValidator(new PhoneValidator(LoginActivity.this));
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
                phoneValiv.addValidator(new PhoneValidator(LoginActivity.this));
                form.addValidates(phoneValiv);
                Validate smsValiv = new Validate(sms_code);
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
    private void sendSubRequest() {
        WaitDialog.show(LoginActivity.this,"确认中...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = LoginActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_LOGIN;
        reqInfo.parser = new LoginDataParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("phone",phone_number.getText().toString().trim());
        if (isSMSLogin){
            reqDateMap.put("code",sms_code.getText().toString().trim());
        }else {

            reqDateMap.put("password",sms_code.getText().toString().trim());
        }
//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    LoginInfo user = (LoginInfo) resInfo.responseData;
                    if (null != user){
                        SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
//                        KWApplication.getInstance().sign = user.sign;
//                        if (user.initPwd == 1){//是否需要设定密码 0:否 1:是
//                            editor.putBoolean(Constants.isLogin,true);
//                            editor.putString(Constants.sign,user.sign);
//                            editor.putBoolean(Constants.isSetPsd,false);
//                            editor.putString(Constants.userInfo, GsonHelper.toJsonString(user));
//                            editor.apply();
//                            editor.commit();
//                            Intent intent = new Intent(LoginActivity.this, SetPasswordActivity.class);
//                            intent.putExtra("title","设置密码");
//                            intent.putExtra("back","");
//                            intent.putExtra("isSetPwd",true);
//                            startActivity(intent);
//                        }else {
                            editor.putBoolean(Constants.isLogin,true);
                            editor.putString(Constants.token,user.token);
                            editor.putBoolean(Constants.isSetPsd,true);
                            editor.putString(Constants.login_info, GsonHelper.toJsonString(user));
                            editor.apply();
                            editor.commit();
                            AppManager.getAppManager().finishAllActivity();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
//                        }

                    }
                }else {
                    Toast.makeText(LoginActivity.this,resInfo.msg,Toast.LENGTH_SHORT).show();
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
        WaitDialog.show(LoginActivity.this,"发送验证码...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = LoginActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_VERIFICATION_CODE;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("",phone_number.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    Toast.makeText(LoginActivity.this,"验证码发送成功",Toast.LENGTH_SHORT).show();
                }else {
                    timer.clear();
                    Toast.makeText(LoginActivity.this,resInfo.msg,Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestGetJSON(callback);
    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            long secondTime=System.currentTimeMillis();
            if(secondTime-firstTime>2000){
                Toast.makeText(LoginActivity.this,"再按一次退出应用",Toast.LENGTH_SHORT).show();
                firstTime=secondTime;
                return true;
            }else{
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
