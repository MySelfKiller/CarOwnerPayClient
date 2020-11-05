package com.kayu.car_owner_pay.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.LoginDataParse;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validator.NotEmptyValidator;
import com.kayu.utils.NoMoreClickListener;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;

public class ExchangeFragment extends Fragment {


    private AppCompatButton apply_btn;
    private EditText code_et;

    public ExchangeFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exchange, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //标题栏
        view.findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                requireActivity().onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
//        TextView back_tv = view.findViewById(R.id.title_back_tv);
        TextView title_name = view.findViewById(R.id.title_name_tv);
        title_name.setText("兑换码");
//        back_tv.setText("首页");

        code_et = view.findViewById(R.id.exchange_code_et);
        apply_btn = view.findViewById(R.id.exchange_apply_btn);
        apply_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Form form = new Form();
                Validate codeValiv = new Validate(code_et);
                codeValiv.addValidator(new NotEmptyValidator(getContext()));
                form.addValidates(codeValiv);
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
        WaitDialog.show((AppCompatActivity) getContext(),"请稍等...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = getContext();
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_GET_EXCHANGE;
        reqInfo.parser = new LoginDataParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("code",code_et.getText().toString().trim());
//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    MessageDialog.show((AppCompatActivity) getContext(),"兑换成功","","继续兑换","返回首页")
                            .setCancelable(false)
                            .setOkButton(new OnDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v) {
                                    baseDialog.doDismiss();
                                    return false;
                                }
                            })
                            .setCancelButton(new OnDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v) {
                                    baseDialog.doDismiss();
                                    requireActivity().onBackPressed();
                                    return false;
                                }
                            });
                }else {
                    TipDialog.show((AppCompatActivity)getContext(),resInfo.msg, TipDialog.TYPE.WARNING);
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);
    }
}