package com.kayu.car_owner_pay.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;

public class SetPasswordActivity extends BaseActivity {
    private String title = "标题";
    private String back = "";
    private EditText set_new_password,set_old_password;
    private EditText set_new_password2;
    private AppCompatButton set_ask_btn;
    private boolean isSetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        back = intent.getStringExtra("back");
        isSetPwd = intent.getBooleanExtra("isSetPwd",false);

        //标题栏
        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText(title);

        LinearLayout back_lay = findViewById(R.id.title_back_btu);
        if (StringUtil.isEmpty(back)){
            back_lay.setVisibility(View.INVISIBLE);
        }else {
            back_lay.setVisibility(View.VISIBLE);
        }
        back_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = findViewById(R.id.title_back_tv);
        back_tv.setText(back);

        LinearLayout old_pwd_lay = findViewById(R.id.set_old_pwd_lay);
        if(isSetPwd){
            old_pwd_lay.setVisibility(View.GONE);
        }else {
            old_pwd_lay.setVisibility(View.VISIBLE);
        }
        set_old_password = findViewById(R.id.set_old_password);
        set_new_password = findViewById(R.id.set_new_password);
        set_new_password2 = findViewById(R.id.set_new_password2);
        set_ask_btn = findViewById(R.id.set_ask_btn);
        set_ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                if(!isSetPwd){
                    Validate codeValidate = new Validate(set_old_password);
                    codeValidate.addValidator(new NotEmptyValidator(SetPasswordActivity.this));
                    form.addValidates(codeValidate);
                }
                ConfirmValidate newPassValidate = new ConfirmValidate(set_new_password,set_new_password2);
                newPassValidate.addValidator(new NotEmptyValidator(SetPasswordActivity.this));
                form.addValidates(newPassValidate);
                if (form.validate()){
                    reqSetPasswrod();
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void reqSetPasswrod() {
        WaitDialog.show(SetPasswordActivity.this,"稍等...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = SetPasswordActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_SET_PASSWORD;
        reqInfo.parser = new NormalParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        if (!isSetPwd){
            reqDateMap.put("oldPwd",set_old_password.getText().toString().trim());
        }
        reqDateMap.put("newPwd",set_new_password2.getText().toString().trim());
//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
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

    private long firstTime=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (isSetPwd){
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    ToastUtils.show("再按一次退出应用");
                    firstTime=secondTime;
                    return true;
                }else{
                    System.exit(0);
                }
            }else {
                onBackPressed();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
