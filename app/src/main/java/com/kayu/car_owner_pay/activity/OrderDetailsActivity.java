package com.kayu.car_owner_pay.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.login.LoginAutoActivity;
import com.kayu.car_owner_pay.data_parser.OrderDetailParse;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.LoginDataParse;
import com.kayu.car_owner_pay.model.LoginInfo;
import com.kayu.car_owner_pay.model.OrderDetailBean;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView shipment_number;
    private TextView card_num;
    private TextView activation_code;
    private AppCompatButton ask_btn;
    private String phone;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");

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

        shipment_number = findViewById(R.id.details_shipment_number);
        card_num = findViewById(R.id.details_card_num);
        activation_code = findViewById(R.id.details_activation_code);
        ask_btn = findViewById(R.id.details_ask_btn);
        ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, LoginAutoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        getInfo();
    }

    @SuppressLint("HandlerLeak")
    private void getInfo() {
        WaitDialog.show(OrderDetailsActivity.this,"查询中...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = OrderDetailsActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_ORDER_DETAIL;
        reqInfo.parser = new OrderDetailParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("phone",phone);
        reqDateMap.put("code",code);

//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    OrderDetailBean orderDetailBean = (OrderDetailBean) resInfo.responseData;
                    if (null != orderDetailBean){
                        if (StringUtil.isEmpty(orderDetailBean.waybillNo)) {
                            shipment_number.setText("未发货");
                        } else {
                            shipment_number.setText(orderDetailBean.waybillNo);

                        }
                        card_num.setText(orderDetailBean.cardNo);
                        activation_code.setText(orderDetailBean.cardCode);
                    }
                }else {
                    Toast.makeText(OrderDetailsActivity.this,resInfo.msg,Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);

    }
}