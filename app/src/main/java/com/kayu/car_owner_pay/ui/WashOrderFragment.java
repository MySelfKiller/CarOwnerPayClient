package com.kayu.car_owner_pay.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.PayOrderViewModel;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.model.WashStationDetailBean;
import com.kayu.car_owner_pay.wxapi.AliPayBean;
import com.kayu.car_owner_pay.wxapi.OnResponseListener;
import com.kayu.car_owner_pay.wxapi.PayResult;
import com.kayu.car_owner_pay.wxapi.WXShare;
import com.kayu.car_owner_pay.wxapi.WxPayBean;
import com.kayu.utils.GetJuLiUtils;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.location.LocationCallback;
import com.kayu.utils.location.LocationManager;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;

import java.util.Map;

public class WashOrderFragment extends Fragment {
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
                        TipDialog.show((AppCompatActivity) getActivity(), "支付成功！", TipDialog.TYPE.SUCCESS).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                if (null != mAliPayBean) {
                                    requireActivity().onBackPressed();
                                    requireActivity().onBackPressed();
                                    FragmentManager fg = requireActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
                                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    fragmentTransaction.add(R.id.main_root_lay, new WashUnusedFragment(mAliPayBean.orderId,8));
                                    fragmentTransaction.addToBackStack("ddd");
                                    fragmentTransaction.commit();
                                }
                            }
                        });
                    } else {
                        LogUtil.e("支付宝取消支付返回结果",resultInfo);
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (null != mAliPayBean) {
                            payOrderViewModel.cancelPay(getContext(),mAliPayBean.orderId);
                        }
                        TipDialog.show((AppCompatActivity) getActivity(), "支付失败！", TipDialog.TYPE.ERROR).setOnDismissListener(new OnDismissListener() {
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

    public WashOrderFragment(WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO,String serviceType ) {
        this.selectedListDTO = selectedListDTO;
        this.serviceType = serviceType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        payOrderViewModel = ViewModelProviders.of(this).get(PayOrderViewModel.class);
        return inflater.inflate(R.layout.fragment_wash_order, container, false);
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
        TextView back_tv = view.findViewById(R.id.title_back_tv);
        TextView title_name = view.findViewById(R.id.title_name_tv);
        title_name.setText("全国汽车特惠");
//        title_name.setVisibility(View.GONE);
//        back_tv.setText("我的");

        order_img_bg = view.findViewById(R.id.wash_order_img_bg);
        order_name = view.findViewById(R.id.wash_order_name);
        order_price = view.findViewById(R.id.wash_order_price);
        order_sub_price = view.findViewById(R.id.wash_order_sub_price);
        order_distance = view.findViewById(R.id.wash_order_distance);
        order_open_time = view.findViewById(R.id.wash_order_time);
        services_type = view.findViewById(R.id.wash_order_services_type);
        services_mode = view.findViewById(R.id.wash_order_mode);
        order_full_price = view.findViewById(R.id.wash_order_full_price);
        order_rebate_price = view.findViewById(R.id.wash_order_rebate_price);
        order_sale_price = view.findViewById(R.id.wash_order_sale_price);
        order_price_tg = view.findViewById(R.id.wash_order_price_tg);
        order_sub_price_tg = view.findViewById(R.id.wash_order_sub_price_tg);

        wechat_option = view.findViewById(R.id.wash_order_wechat_option);
        wechat_checked = view.findViewById(R.id.wash_order_wechat_checked);
        alipay_option = view.findViewById(R.id.wash_order_alipay_option);
        alipay_checked = view.findViewById(R.id.wash_order_alipay_checked);
        //默认选择支付宝支付
        alipay_checked.setSelected(true);
        payWay = 2;

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


        order_pay_btn = view.findViewById(R.id.wash_order_pay_btn);

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

        mainViewModel.getWashStoreDetail(getContext(), null).observe(requireActivity(), new Observer<WashStationDetailBean>() {
            @Override
            public void onChanged(WashStationDetailBean washStationDetailBean) {
                if (null != washStationDetailBean)
                    initViewData(washStationDetailBean);
            }
        });

    }

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
        AMapLocation location = LocationManager.getSelf().getLoccation();
        if (null != location){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double dis = GetJuLiUtils.getDistance(longitude, latitude, Double.parseDouble(washStation.longitude), Double.parseDouble(washStation.latitude));
            order_distance.setText("距您" + dis + "km");
        }
        order_open_time.setText(sb.toString());
    }

    private void wechatPayOrder() {
        wxShare = new WXShare(getContext());
        wxShare.register();
        wxShare.setListener(new OnResponseListener() {
            @Override
            public void onSuccess() {
                LogUtil.e("hm", "支付成功");
                TipDialog.show((AppCompatActivity) getActivity(), "支付成功", TipDialog.TYPE.SUCCESS).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (null != mWxPayBean) {
                            requireActivity().onBackPressed();
                            requireActivity().onBackPressed();
                            FragmentManager fg = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fg.beginTransaction();
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.add(R.id.main_root_lay, new WashUnusedFragment(mWxPayBean.orderId,8));
                            fragmentTransaction.addToBackStack("ddd");
                            fragmentTransaction.commit();
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
                LogUtil.e("hm", "支付已取消");
                if (null != mWxPayBean) {
                    payOrderViewModel.cancelPay(getContext(),mWxPayBean.orderId);
                }
                TipDialog.show((AppCompatActivity) getActivity(), "支付已取消", TipDialog.TYPE.ERROR).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                });
            }

            @Override
            public void onFail(String message) {
                LogUtil.e("hm", message);
                TipDialog.show((AppCompatActivity) getActivity(), message, TipDialog.TYPE.ERROR).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (null != mWxPayBean) {
                            payOrderViewModel.cancelPay(getContext(),mWxPayBean.orderId);
                        }
                        TipDialog.show((AppCompatActivity) getActivity(), "支付失败", TipDialog.TYPE.ERROR).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });
                    }

                });

            }
        });
        payOrderViewModel.getWeChatPayInfo(getContext(),shopCode,selectedListDTO.serviceCode).observe(this, new Observer<WxPayBean>() {
            @Override
            public void onChanged(WxPayBean wxPayBean) {
                mWxPayBean = wxPayBean;
                reqWxPay(wxPayBean);
            }
        });
    }

    private void aliapyPayOrder() {
        payOrderViewModel.getAliPayInfo(getContext(),shopCode,selectedListDTO.serviceCode).observe(this, new Observer<AliPayBean>() {
            @Override
            public void onChanged(AliPayBean aliPayBean) {
                mAliPayBean = aliPayBean;
                final Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(getActivity());
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