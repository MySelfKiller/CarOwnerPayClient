package com.kayu.car_owner_pay.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.location.AMapLocation;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.WashOrderDetailBean;
import com.kayu.utils.GetJuLiUtils;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.QRCodeUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

public class WashUnusedActivity extends BaseActivity {
    private Banner station_banner;
    private TextView station_open_time;
    private TextView station_name;
    private TextView station_address;
    private TextView station_distance;
    private LinearLayout navi_lay;
    private LinearLayout phone_lay;

    private Long orderId;
    private int orderState;//0:待支付 1:已支付待使用 2:已取消 3:已使用 4:退款中 5:已退款 6:支付失败、7:退款失败 8:本机支付成功后自定义的状态
    private ImageView qr_img,qr_state_img;
    private TextView qr_string;
    private TextView valid_time;
    private TextView order_number,tag_order_nom,tag_pay_time,tag_expire_time;
    private TextView order_state;
    private TextView pay_time;
    private TextView expire_time;
    private TextView store_name;
    private TextView services_type;
    private TextView services_model;
    private TextView full_price;
    private TextView rebate_price;
    private TextView sale_price;
    private TextView unused_refund;
    private TextView unused_navi_btn;
    private ImageView state_img;
    private TextView state_tv;
    private ConstraintLayout state_lay;
    private MainViewModel mainViewModel;
    private TextView explain_tv;
    private ImageView explain_img;
    private ConstraintLayout unused_refund_lay;
    private ConstraintLayout qr_code_lay;

//    public WashUnusedActivity(Long orderId, Integer orderState) {
//        this.orderId = orderId;
//        this.orderState = orderState;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wash_unused);

        orderId = getIntent().getLongExtra("orderId",0);
        orderState = getIntent().getIntExtra("orderState",-1);

        mainViewModel = ViewModelProviders.of(WashUnusedActivity.this).get(MainViewModel.class);

        //标题栏
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
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("全国洗车特惠");
//        title_name.setVisibility(View.GONE);
        back_tv.setText("洗车订单");

        state_lay = findViewById(R.id.wash_unused_state_Lay);
        state_img = findViewById(R.id.wash_unused_state_img);
        state_tv = findViewById(R.id.wash_unused_state_tv);


        station_banner = findViewById(R.id.wash_unused_banner);
        station_name = findViewById(R.id.wash_unused_name);
        station_open_time = findViewById(R.id.wash_unused_time);
        station_address = findViewById(R.id.wash_unused_location);
        station_distance = findViewById(R.id.wash_unused_distance);
        navi_lay = findViewById(R.id.wash_unused_navi_lay);
        phone_lay = findViewById(R.id.wash_unused_phone_lay);

        qr_code_lay = findViewById(R.id.wash_unused_qr_code_lay);
        qr_img = findViewById(R.id.wash_unused_qr_img);
        qr_state_img = findViewById(R.id.wash_unused_qr_state_img);
        qr_string = findViewById(R.id.wash_unused_qr_code);
        valid_time = findViewById(R.id.wash_unused_valid_time);
        explain_tv = findViewById(R.id.wash_unused_explain_tv);
        explain_img = findViewById(R.id.wash_unused_explain_img);
        tag_order_nom = findViewById(R.id.id_tag_order_nom);
        tag_pay_time = findViewById(R.id.id_tag_order_pay_time);
        tag_expire_time = findViewById(R.id.id_tag_order_expire_time);
        order_number = findViewById(R.id.wash_unused_order_number);
        order_state = findViewById(R.id.wash_unused_order_state);
        pay_time = findViewById(R.id.wash_unused_pay_time);
        expire_time = findViewById(R.id.wash_unused_expire_time);
        store_name = findViewById(R.id.wash_unused_store_name);
        services_type = findViewById(R.id.wash_unused_services_type);
        services_model = findViewById(R.id.wash_unused_model);
        full_price = findViewById(R.id.wash_unused_full_price);
        rebate_price = findViewById(R.id.wash_unused_rebate);
        sale_price = findViewById(R.id.wash_unused_sale_price);

        //退款按钮
        unused_refund_lay = findViewById(R.id.wash_unused_refund_lay);
        unused_refund = findViewById(R.id.wash_unused_refund);
        unused_navi_btn = findViewById(R.id.wash_unused_navi_btn);

        mainViewModel.getWashOrderDetail(WashUnusedActivity.this, orderId).observe(WashUnusedActivity.this, new Observer<WashOrderDetailBean>() {
            @Override
            public void onChanged(WashOrderDetailBean orderDetailBean) {
                if (null != orderDetailBean) {
                    initViewData(orderDetailBean);
                }
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
//        return inflater.inflate(R.layout.fragment_wash_unused, container, false);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }


    private void initViewData(WashOrderDetailBean washStation) {
        if (null != washStation.doorImgList) {

            station_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                    .setIndicatorGravity(BannerConfig.RIGHT)
                    .setImageLoader(new BannerImageLoader())
                    .setImages(washStation.doorImgList)
                    .setDelayTime(2000)
                    .start();
        }

        station_name.setText(washStation.shopName);
        station_address.setText(washStation.address);

//        StringBuffer sb = new StringBuffer();
//        if (washStation.isOpen.equals("1")) {
//            sb.append("营业中 | ");
//        } else {
//            sb.append("休息中 | ");
//
//        }

        station_open_time.setText("营业时间：" + washStation.busTime);
        AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
        if (null != location){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double dis = GetJuLiUtils.getDistance(longitude, latitude, Double.parseDouble(washStation.longitude), Double.parseDouble(washStation.latitude));
            station_distance.setText("距您" + dis + "km");
        }
        navi_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                KWApplication.getInstance().toNavi(WashUnusedActivity.this, washStation.latitude, washStation.longitude, washStation.address,"BD09");
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        phone_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                KWApplication.getInstance().callPhone(WashUnusedActivity.this, washStation.telephone);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        store_name.setText(washStation.shopName);
        order_number.setText(washStation.orderNo);

        String orderStateStr;
        int qr_color= Color.LTGRAY;
        switch (washStation.state) {//0:待支付 1:已支付待使用 2:已取消 3:已使用 4:退款中 5:已退款 6:支付失败、7:退款失败
            case 0:
//                if (isAdded())
                    state_img.setImageResource(R.mipmap.ic_unpaid_time);
                orderStateStr = "待支付";
                state_lay.setVisibility(View.VISIBLE);
                qr_state_img.setVisibility(View.GONE);
                break;
            case 1:
                if (orderState == 8) {
//                    if (isAdded())
                        state_img.setImageResource(R.mipmap.ic_selected);

//            state_img.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_unpaid_time));
                    orderStateStr="支付成功";
                } else {
                    orderStateStr ="待使用";
                }
                state_lay.setVisibility(View.VISIBLE);
                qr_color = Color.BLACK;
                qr_state_img.setVisibility(View.GONE);
//                if (isAdded())
                    state_img.setImageResource(R.mipmap.ic_selected);
                break;
            case 2:
                orderStateStr = "已取消";
//                if (isAdded())
                    state_img.setImageResource(R.mipmap.ic_pay_state_cancle);
                state_lay.setVisibility(View.VISIBLE);
                qr_state_img.setVisibility(View.GONE);
                break;
            case 3:
                orderStateStr = "已使用";
                qr_color = Color.LTGRAY;
//                if (isAdded())
                    state_img.setImageResource(R.mipmap.ic_selected);
                state_lay.setVisibility(View.VISIBLE);
                qr_state_img.setVisibility(View.GONE);
                break;
            case 4:
                qr_state_img.setVisibility(View.VISIBLE);
                qr_state_img.setImageResource(R.mipmap.ic_refunding);
                orderStateStr = "退款中";
                qr_color = Color.LTGRAY;
                state_lay.setVisibility(View.GONE);
                break;
            case 5:
                qr_state_img.setVisibility(View.VISIBLE);
                qr_state_img.setImageResource(R.mipmap.ic_refunded);
                orderStateStr = "已退款";
                qr_color = Color.LTGRAY;
                state_lay.setVisibility(View.GONE);
                break;
            case 6:
                orderStateStr = "支付失败";
                qr_state_img.setVisibility(View.GONE);
                state_lay.setVisibility(View.VISIBLE);
                break;
            case 7:
                orderStateStr = "退款失败";
                qr_color = Color.LTGRAY;
                state_lay.setVisibility(View.VISIBLE);
                break;
            default:
                orderStateStr = "暂无";
                qr_state_img.setVisibility(View.GONE);
                state_lay.setVisibility(View.GONE);
        }
        order_state.setText(orderStateStr);
        state_tv.setText(orderStateStr);


        qr_img.setImageBitmap(QRCodeUtil.createQRCodeBitmap(washStation.qrString, 280, 280, "UTF-8",
                null, "0", qr_color, Color.WHITE, null, 0, null));
        qr_string.setText(washStation.qrString);
        valid_time.setText("有效期至" + washStation.effTime + " 00:00:00");
        explain_tv.setText("*" + washStation.explain);
        KWApplication.getInstance().loadImg(washStation.useExplain, explain_img);
        pay_time.setText(washStation.createTime);
        expire_time.setText(washStation.effTime + " 00:00:00");
        services_type.setText(washStation.serviceName.split("-")[0]);
        services_model.setText(washStation.serviceName.split("-")[1]);
        full_price.setText("￥" + washStation.amount);
        rebate_price.setText("-￥" + (washStation.amount - washStation.realAmount));
        sale_price.setText(String.valueOf(washStation.realAmount));

        if (washStation.state == 1) {
            qr_code_lay.setVisibility(View.VISIBLE);
            unused_refund_lay.setVisibility(View.VISIBLE);
            unused_navi_btn.setVisibility(View.VISIBLE);
            unused_navi_btn.setText("地图导航");

            tag_order_nom.setVisibility(View.VISIBLE);
            order_number.setVisibility(View.VISIBLE);
            tag_pay_time.setVisibility(View.VISIBLE);
            pay_time.setVisibility(View.VISIBLE);
            expire_time.setVisibility(View.VISIBLE);
            tag_expire_time.setVisibility(View.VISIBLE);
        } else {
            unused_refund_lay.setVisibility(View.GONE);
            if (washStation.state == 3
                    || washStation.state == 4
                    || washStation.state == 5
                    || washStation.state == 7) {
                qr_code_lay.setVisibility(View.VISIBLE);
                unused_navi_btn.setText("再来一单");
                unused_navi_btn.setVisibility(View.VISIBLE);

                tag_order_nom.setVisibility(View.VISIBLE);
                order_number.setVisibility(View.VISIBLE);
                tag_pay_time.setVisibility(View.VISIBLE);
                pay_time.setVisibility(View.VISIBLE);
                expire_time.setVisibility(View.VISIBLE);
                tag_expire_time.setVisibility(View.VISIBLE);
            } else {
                unused_navi_btn.setVisibility(View.GONE);
                qr_code_lay.setVisibility(View.GONE);

                tag_order_nom.setVisibility(View.GONE);
                order_number.setVisibility(View.GONE);
                tag_pay_time.setVisibility(View.GONE);
                pay_time.setVisibility(View.GONE);
                expire_time.setVisibility(View.GONE);
                tag_expire_time.setVisibility(View.GONE);
            }
        }

        unused_refund.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
//                FragmentManager fg = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                fragmentTransaction.add(R.id.main_root_lay, new WashRefundFragment(orderId));
//                fragmentTransaction.addToBackStack("ddd");
//                fragmentTransaction.commit();
                Intent intent = new Intent(WashUnusedActivity.this, WashRefundFragment.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                finish();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        unused_navi_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {

                if (washStation.state == 1) {
                    KWApplication.getInstance().toNavi(WashUnusedActivity.this, washStation.latitude, washStation.longitude, washStation.address,"BD09");
                } else if (washStation.state == 3
                        || washStation.state == 4
                        || washStation.state == 5
                        || washStation.state == 7) {
                    onBackPressed();
//                    FragmentManager fg = requireActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    fragmentTransaction.add(R.id.main_root_lay, new WashStationActivity(washStation.shopCode));
//                    fragmentTransaction.addToBackStack("ddd");
//                    fragmentTransaction.commit();
                    Intent intent = new Intent(WashUnusedActivity.this, WashStationActivity.class);
                    intent.putExtra("shopCode",washStation.shopCode);
                    startActivity(intent);

                }

            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

    }
}