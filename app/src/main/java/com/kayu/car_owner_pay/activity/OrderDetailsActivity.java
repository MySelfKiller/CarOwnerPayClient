package com.kayu.car_owner_pay.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.login.LoginAutoActivity;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView shipment_number;
    private TextView card_num;
    private TextView activation_code;
    private AppCompatButton ask_btn;
    private String waybillNo;
    private String cardNo;
    private String cardCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        waybillNo = getIntent().getStringExtra("waybillNo");
        cardNo = getIntent().getStringExtra("cardNo");
        cardCode = getIntent().getStringExtra("cardCode");

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

        if (StringUtil.isEmpty(waybillNo)) {
            shipment_number.setText("未发货");
        } else {
            shipment_number.setText(waybillNo);

        }
        card_num.setText(cardNo);
        activation_code.setText(cardCode);
    }


}