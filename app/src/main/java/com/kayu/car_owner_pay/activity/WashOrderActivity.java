package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.PayOrderViewModel;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.model.WashStationDetailBean;
import com.kayu.car_owner_pay.wxapi.AliPayBean;
import com.kayu.car_owner_pay.wxapi.OnResponseListener;
import com.kayu.car_owner_pay.wxapi.PayResult;
import com.kayu.car_owner_pay.wxapi.WXShare;
import com.kayu.car_owner_pay.wxapi.WxPayBean;
import com.kayu.utils.GetJuLiUtils;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.TipGifDialog;

import java.util.Map;

public class WashOrderActivity extends BaseActivity {
    private WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO;//已选择的洗车服务
    private TextView order_name;
    private ImageView order_img_bg;
    private TextView order_price;
    private TextView order_sub_price;
    private TextView order_distance;
    private TextView order_open_time;
    private TextView services_type;
    private TextView services_mode;
    private TextView order_full_price;
    private TextView order_rebate_price;
    private TextView order_sale_price;
    private TextView order_price_tg;
    private TextView order_sub_price_tg;
    private TextView order_pay_btn;
    private MainViewModel mainViewModel;
    private ConstraintLayout wechat_option;
    private ConstraintLayout alipay_option;
    private ImageView wechat_checked;
    private ImageView alipay_checked;

    private PayOrderViewModel payOrderViewModel;
    private WXShare wxShare;
    private WxPayBean mWxPayBean;
    private String shopCode;
    private AliPayBean mAliPayBean;
    private int payWay = 2;//支付方式 0:微信JSAPI 、1:微信APP 、2:支付宝

    //    private long vipId;
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
                        TipGifDialog.show(WashOrderActivity.this, "支付成功", TipGifDialog.TYPE.SUCCESS).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                if (null != mAliPayBean) {
//                                    onBackPressed();
//                                    onBackPressed();
//                                    FragmentManager fg = requireActivity().getSupportFragmentManager();
//                                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                                    fragmentTransaction.add(R.id.main_root_lay, new WashUnusedActivity(mAliPayBean.orderId,8));
//                                    fragmentTransaction.addToBackStack("ddd");
//                                    fragmentTransaction.commit();
                                    Intent intent = new Intent(WashOrderActivity.this, WashUnusedActivity.class);
                                    intent.putExtra("orderId", mAliPayBean.orderId);
                                    intent.putExtra("orderState",8);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        LogUtil.e("支付宝取消支付返回结果",resultInfo);
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (null != mAliPayBean) {
                            payOrderViewModel.cancelPay(WashOrderActivity.this,mAliPayBean.orderId);
                        }
                        TipGifDialog.show(WashOrderActivity.this, "支付已取消", TipGifDialog.TYPE.WARNING).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private String serviceType;
    private LinearLayout pay_way_lay;

//    public WashOrderFragment(WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO,String serviceType ) {
//        this.selectedListDTO = selectedListDTO;
//        this.serviceType = serviceType;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_order);
        selectedListDTO = getIntent().getParcelableExtra("selectedListDTO");
        serviceType = getIntent().getStringExtra("serviceType");
        shopCode = getIntent().getStringExtra("shopCode");

        mainViewModel = ViewModelProviders.of(WashOrderActivity.this).get(MainViewModel.class);
        payOrderViewModel = ViewModelProviders.of(WashOrderActivity.this).get(PayOrderViewModel.class);

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
        title_name.setText("全国汽车特惠");
//        title_name.setVisibility(View.GONE);
//        back_tv.setText("我的");

        order_img_bg = findViewById(R.id.wash_order_img_bg);
        order_name = findViewById(R.id.wash_order_name);
        order_price = findViewById(R.id.wash_order_price);
        order_sub_price = findViewById(R.id.wash_order_sub_price);
        order_distance = findViewById(R.id.wash_order_distance);
        order_open_time = findViewById(R.id.wash_order_time);
        services_type = findViewById(R.id.wash_order_services_type);
        services_mode = findViewById(R.id.wash_order_mode);
        order_full_price = findViewById(R.id.wash_order_full_price);
        order_rebate_price = findViewById(R.id.wash_order_rebate_price);
        order_sale_price = findViewById(R.id.wash_order_sale_price);
        order_price_tg = findViewById(R.id.wash_order_price_tg);
        order_sub_price_tg = findViewById(R.id.wash_order_sub_price_tg);

        pay_way_lay = findViewById(R.id.wash_order_pay_way_lay);
        wechat_option = findViewById(R.id.wash_order_wechat_option);
        wechat_checked = findViewById(R.id.wash_order_wechat_checked);
        alipay_option = findViewById(R.id.wash_order_alipay_option);
        alipay_checked = findViewById(R.id.wash_order_alipay_checked);

        alipay_option.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (!alipay_checked.isSelected()) {
                    alipay_checked.setSelected(true);
                    payWay = 2;
                }
                if (wechat_checked.isSelected())
                    wechat_checked.setSelected(false);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        wechat_option.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (!wechat_checked.isSelected()) {
                    wechat_checked.setSelected(true);
                    payWay = 1;
                }
                if (alipay_checked.isSelected())
                    alipay_checked.setSelected(false);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });


        order_pay_btn = findViewById(R.id.wash_order_pay_btn);

        order_pay_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (payWay == 1) {
                    wechatPayOrder();
                } else {
                    aliapyPayOrder();
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        mainViewModel.getWashStoreDetail(WashOrderActivity.this, shopCode).observe(WashOrderActivity.this, new Observer<WashStationDetailBean>() {
            @Override
            public void onChanged(WashStationDetailBean washStationDetailBean) {
                if (null != washStationDetailBean)
                    initViewData(washStationDetailBean);
            }
        });
        mainViewModel.getParameter(WashOrderActivity.this,20).observe(WashOrderActivity.this, new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null == systemParam){
                    return;
                }
                if (!StringUtil.isEmpty(systemParam.content.trim())){
                    String[] arr = systemParam.content.split("#");
//                    LogUtil.e("hm","截取---"+arr[0]);
                    for (int x = 0; x < arr.length; x++) {
                        if (arr[x].equals("支付宝")) {
                            alipay_option.setVisibility(View.VISIBLE);
                            if (x ==0){
                                alipay_checked.setSelected(true);
                                payWay = 2;
                                pay_way_lay.setVisibility(View.VISIBLE);
                                order_pay_btn.setVisibility(View.VISIBLE);
                            }
                        } else if (arr[x].equals("微信")) {
                            wechat_option.setVisibility(View.VISIBLE);
                            if (x ==0){
                                wechat_checked.setSelected(true);
                                payWay = 1;
                                pay_way_lay.setVisibility(View.VISIBLE);
                                order_pay_btn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mainViewModel = ViewModelProviders.of(WashOrderFragment.this).get(MainViewModel.class);
//        payOrderViewModel = ViewModelProviders.of(WashOrderFragment.this).get(PayOrderViewModel.class);
//        return inflater.inflate(R.layout.fragment_wash_order, container, false);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    private void initViewData(WashStationDetailBean washStation) {
        shopCode = washStation.shopCode;
        services_type.setText(serviceType);
        String[] sdf = selectedListDTO.serviceName.split("-");
        if (null != sdf && sdf.length > 0) {
            services_mode.setText(sdf[1]);
        }else {

            if (selectedListDTO.carModel == 1) {
                services_mode.setText("小轿车");
            }
            if (selectedListDTO.carModel == 2) {
                services_mode.setText("SUV/MPV");
            }
            if (selectedListDTO.carModel == 3) {
                services_mode.setText("全车型");
            }
        }
        KWApplication.getInstance().loadImg(washStation.doorPhotoUrl,order_img_bg);
        order_name.setText(washStation.shopName);
        StringBuffer sb = new StringBuffer();
        if (washStation.isOpen.equals("1")) {
            sb.append("营业中 | ");
        } else {
            sb.append("休息中 | ");

        }
        sb.append(washStation.openTimeStart).append("-").append(washStation.openTimeEnd);

        order_price.setText(selectedListDTO.finalPrice);
        order_sale_price.setText(selectedListDTO.finalPrice);
        order_price_tg.setText(selectedListDTO.finalPrice);
        order_sub_price.setText("￥"+selectedListDTO.price);
        order_full_price.setText("￥"+selectedListDTO.price);
        order_sub_price_tg.setText("￥"+selectedListDTO.price);

        String rebatePirce = String.valueOf(Double.parseDouble(selectedListDTO.price)-Double.parseDouble(selectedListDTO.finalPrice));
        order_rebate_price.setText("-￥"+rebatePirce);
        AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
        if (null != location){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double dis = GetJuLiUtils.getDistance(longitude, latitude, Double.parseDouble(washStation.longitude), Double.parseDouble(washStation.latitude));
            order_distance.setText("距您" + dis + "km");
        }
        order_open_time.setText(sb.toString());
    }

    private void wechatPayOrder() {
        wxShare = new WXShare(WashOrderActivity.this);
        wxShare.register();
        wxShare.setListener(new OnResponseListener() {
            @Override
            public void onSuccess() {
//                LogUtil.e("hm", "支付成功");
                TipGifDialog.show(WashOrderActivity.this, "支付成功", TipGifDialog.TYPE.SUCCESS).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (null != mWxPayBean) {
//                            onBackPressed();
//                            onBackPressed();
//                            FragmentManager fg = requireActivity().getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                            fragmentTransaction.add(R.id.main_root_lay, new WashUnusedActivity(mWxPayBean.orderId,8));
//                            fragmentTransaction.addToBackStack("ddd");
//                            fragmentTransaction.commit();
                            Intent intent = new Intent(WashOrderActivity.this, WashUnusedActivity.class);
                            intent.putExtra("orderId", mWxPayBean.orderId);
                            intent.putExtra("orderState",8);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
//                LogUtil.e("hm", "支付已取消");
                if (null != mWxPayBean) {
                    payOrderViewModel.cancelPay(WashOrderActivity.this,mWxPayBean.orderId);
                }
                TipGifDialog.show(WashOrderActivity.this, "支付已取消", TipGifDialog.TYPE.WARNING).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                });
            }

            @Override
            public void onFail(String message) {
                LogUtil.e("hm", message);
                TipGifDialog.show(WashOrderActivity.this, message, TipGifDialog.TYPE.ERROR).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (null != mWxPayBean) {
                            payOrderViewModel.cancelPay(WashOrderActivity.this,mWxPayBean.orderId);
                        }
                        TipGifDialog.show(WashOrderActivity.this, "支付失败", TipGifDialog.TYPE.ERROR).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });
                    }

                });

            }
        });
        payOrderViewModel.getWeChatPayInfo(WashOrderActivity.this,shopCode,selectedListDTO.serviceCode).observe(this, new Observer<WxPayBean>() {
            @Override
            public void onChanged(WxPayBean wxPayBean) {
                mWxPayBean = wxPayBean;
                reqWxPay(wxPayBean);
            }
        });
    }

    private void aliapyPayOrder() {
        payOrderViewModel.getAliPayInfo(WashOrderActivity.this,shopCode,selectedListDTO.serviceCode).observe(this, new Observer<AliPayBean>() {
            @Override
            public void onChanged(AliPayBean aliPayBean) {
                mAliPayBean = aliPayBean;
                final Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(WashOrderActivity.this);
                        Map<String, String> result = alipay.payV2(aliPayBean.body, true);
                        Log.i("msp", result.toString());

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        });
    }

    private void reqWxPay(WxPayBean wxPayBean) {
        if (null != wxPayBean) {
            wxShare.getWXPay(wxPayBean, null);
        }
    }


}