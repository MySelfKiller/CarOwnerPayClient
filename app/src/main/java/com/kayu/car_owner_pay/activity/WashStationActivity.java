package com.kayu.car_owner_pay.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.location.AMapLocation;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.WashStationDetailBean;
import com.kayu.car_owner_pay.ui.WashOrderFragment;
import com.kayu.car_owner_pay.ui.WashOrderListFragment;
import com.kayu.utils.GetJuLiUtils;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.location.LocationManagerUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/15.
 * PS: Not easy to write code, please indicate.
 */
public class WashStationActivity extends BaseActivity {
    private String shopCode;//洗车店编号
    private MainViewModel mainViewModel;
    private TextView pay_btn;
    private Banner station_banner;
    private TextView station_open_time;
    private TextView station_name;
    private TextView station_tag;
    private TextView station_address;
    private TextView station_distance;
    private LinearLayout navi_lay;
    private LinearLayout phone_lay;
    private TextView rebate_price;
    private View station_divider1;
    private ConstraintLayout station_type1_lay;
    private ConstraintLayout station_car_lay;
    private ImageView car_select_btn;
    private TextView station_car_price;
    private TextView station_car_sub_price;
    private ConstraintLayout station_suv_lay;
    private ImageView suv_select_btn;
    private TextView station_suv_price;
    private TextView station_suv_sub_price;
    private ConstraintLayout station_type2_lay;
    private ImageView all_car_select_btn;
    private TextView station_all_car_price;
    private TextView station_all_car_sub_price;
    private TextView station_car_name;
    private TextView station_suv_name;
    private TextView station_type1_name;
    private TextView station_type2_name;
    private TextView station_all_car_name;

//    public WashStationFragment(String shopCode) {
//        this.shopCode = shopCode;
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wash_station);
        shopCode = getIntent().getStringExtra("shopCode");
        mainViewModel = ViewModelProviders.of(WashStationActivity.this).get(MainViewModel.class);

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

        station_banner = findViewById(R.id.wash_station_banner);
        station_name = findViewById(R.id.wash_station_name);
        station_tag = findViewById(R.id.wash_station_tag);
        station_open_time = findViewById(R.id.wash_station_time);
        station_address = findViewById(R.id.wash_station_location);
        station_distance = findViewById(R.id.wash_station_distance);
        navi_lay = findViewById(R.id.wash_station_navi_lay);
        phone_lay = findViewById(R.id.wash_station_phone_lay);


        station_type1_lay = findViewById(R.id.wash_station_type1_lay);
        station_type1_name = findViewById(R.id.wash_station_type1_name);
        station_divider1 = findViewById(R.id.wash_station_divider1);

        station_car_lay = findViewById(R.id.wash_station_car_lay);
        car_select_btn = findViewById(R.id.wash_station_car_select_btn);
        station_car_name = findViewById(R.id.wash_station_car_name);
        station_car_price = findViewById(R.id.wash_station_car_price);
        station_car_sub_price = findViewById(R.id.wash_station_car_price_sub);

        station_suv_lay = findViewById(R.id.wash_station_suv_lay);
        suv_select_btn = findViewById(R.id.wash_station_suv_select_btn);
        station_suv_name = findViewById(R.id.wash_station_suv_name);
        station_suv_price = findViewById(R.id.wash_station_suv_price);
        station_suv_sub_price = findViewById(R.id.wash_station_suv_price_sub);

        station_type2_lay = findViewById(R.id.wash_station_type2_lay);
        station_type2_name = findViewById(R.id.wash_station_type2_name);
        all_car_select_btn = findViewById(R.id.wash_station_all_car_select_btn);
        station_all_car_name = findViewById(R.id.wash_station_all_car_name);
        station_all_car_price = findViewById(R.id.wash_station_all_car_price);
        station_all_car_sub_price = findViewById(R.id.wash_station_all_car_price_sub);

        rebate_price = findViewById(R.id.wash_station_rebate_price);
        pay_btn = findViewById(R.id.wash_station_pay_btn);
        TextView station_order_list = findViewById(R.id.wash_station_order_list);
        TextView station_services = findViewById(R.id.wash_station_services);
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_order_list);
        Drawable drawable1 = getResources().getDrawable(R.mipmap.ic_services);
        drawable1.setBounds(0, 0, 50, 50);
        drawable.setBounds(0, 0, 50, 50);
        drawable.setTint(getResources().getColor(R.color.colorAccent));
        drawable1.setTint(getResources().getColor(R.color.colorAccent));
        //40为设置图片的宽度，20为高度

        //（调用方法将图片设置进去）
        station_order_list.setCompoundDrawables(null, drawable, null, null);
        station_services.setCompoundDrawables(null, drawable1, null, null);

        station_order_list.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                FragmentManager fg = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fg.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.main_root_lay, new WashOrderListFragment());
                fragmentTransaction.addToBackStack("ddd");
                fragmentTransaction.commit();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        pay_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                FragmentManager fg = WashStationActivity.this.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fg.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.main_root_lay, new WashOrderFragment(selectedListDTO,serviceType));
                fragmentTransaction.addToBackStack("ddd");
                fragmentTransaction.commit();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        station_car_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (!car_select_btn.isSelected()) {
                    car_select_btn.setSelected(true);
                    selectedListDTO = selectedListDTO1;
                    String payPrice = selectedListDTO.finalPrice;
                    String rebatePirce = String.valueOf(Double.parseDouble(selectedListDTO.price)-Double.parseDouble(selectedListDTO.finalPrice));
                    pay_btn.setText("立即购买￥"+payPrice);
                    rebate_price.setText("立省"+rebatePirce+"元");
                }
                if (suv_select_btn.isSelected())
                    suv_select_btn.setSelected(false);
                if (all_car_select_btn.isSelected())
                    all_car_select_btn.setSelected(false);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        station_suv_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (!suv_select_btn.isSelected()) {
                    suv_select_btn.setSelected(true);
                    selectedListDTO = selectedListDTO2;
                    String payPrice = selectedListDTO.finalPrice;
                    String rebatePirce = String.valueOf(Double.parseDouble(selectedListDTO.price)-Double.parseDouble(selectedListDTO.finalPrice));
                    pay_btn.setText("立即购买￥"+payPrice);
                    rebate_price.setText("立省"+rebatePirce+"元");
                }
                if (car_select_btn.isSelected())
                    car_select_btn.setSelected(false);
                if (all_car_select_btn.isSelected())
                    all_car_select_btn.setSelected(false);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        station_type2_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (!all_car_select_btn.isSelected()) {
                    all_car_select_btn.setSelected(true);
                    selectedListDTO = selectedListDTO3;
                    String payPrice = selectedListDTO.finalPrice;
                    String rebatePirce = String.valueOf(Double.parseDouble(selectedListDTO.price)-Double.parseDouble(selectedListDTO.finalPrice));
                    pay_btn.setText("立即购买￥"+payPrice);
                    rebate_price.setText("立省"+rebatePirce+"元");
                }
                if (car_select_btn.isSelected())
                    car_select_btn.setSelected(false);
                if (suv_select_btn.isSelected())
                    suv_select_btn.setSelected(false);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        mainViewModel.getWashStoreDetail(WashStationActivity.this, shopCode).observe(WashStationActivity.this, new Observer<WashStationDetailBean>() {
            @Override
            public void onChanged(WashStationDetailBean washStationDetailBean) {
                if (null != washStationDetailBean)
                    initViewData(washStationDetailBean);
            }
        });

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
////        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
//        View root = inflater.inflate(R.layout.fragment_wash_station, container, false);
//        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
//        return root;
//    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }

    private String serviceType = "";
    private WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO = null;
    private WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO1 = null;
    private WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO2 = null;
    private WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO3 = null;

    private void initViewData(WashStationDetailBean washStation) {
        if (null != washStation.imgList) {
            List<String> loanBannrUrl = new ArrayList<>();
            for (WashStationDetailBean.ImgListDTO loanBanner : washStation.imgList) {
                loanBannrUrl.add(loanBanner.shopImgUrl);
            }

            station_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                    .setIndicatorGravity(BannerConfig.RIGHT)
                    .setImageLoader(new BannerImageLoader())
                    .setImages(loanBannrUrl)
                    .setDelayTime(2000)
                    .start();
        }

        station_name.setText(washStation.shopName);
        station_address.setText(washStation.address);

        StringBuffer sb = new StringBuffer();
        if (washStation.isOpen.equals("1")) {
            sb.append("营业中 | ");
        } else {
            sb.append("休息中 | ");

        }
        sb.append(washStation.openTimeStart).append("-").append(washStation.openTimeEnd);

//        checkLocation(Double.parseDouble(washStation.latitude), Double.parseDouble(washStation.longitude));
        AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
        if (null != location){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double dis = GetJuLiUtils.getDistance(longitude, latitude, Double.parseDouble(washStation.longitude), Double.parseDouble(washStation.latitude));
            station_distance.setText("距您" + dis + "km");
        }
        station_open_time.setText(sb.toString());
        navi_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                KWApplication.getInstance().toNavi(WashStationActivity.this, washStation.latitude, washStation.longitude, washStation.address,"BD09");
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        phone_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                KWApplication.getInstance().callPhone(WashStationActivity.this, washStation.telephone);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        List<WashStationDetailBean.ServicesDTO> servicesList = washStation.services;
        if (servicesList.size() <= 1) {
            station_divider1.setVisibility(View.GONE);
        } else {
            station_divider1.setVisibility(View.VISIBLE);
        }
        for (int x=0; x<servicesList.size(); x++) {
            WashStationDetailBean.ServicesDTO servicesDTO = servicesList.get(x);
            if (servicesDTO.washType == 1) {
                station_type1_name.setText(servicesDTO.name);
                station_type1_lay.setVisibility(View.VISIBLE);
            }
            if (servicesDTO.washType == 2) {
                station_type2_name.setText(servicesDTO.name);
                station_type2_lay.setVisibility(View.VISIBLE);
            }


            List<WashStationDetailBean.ServicesDTO.ListDTO> listDTOS = servicesDTO.list;
            for (int y = 0; y< listDTOS.size(); y++ ) {
                WashStationDetailBean.ServicesDTO.ListDTO listDTO = listDTOS.get(y);
                if (y == 0 && x == 0) {
                    selectedListDTO = listDTO;
                    String payPrice = listDTO.finalPrice;
                    serviceType = servicesDTO.name;
                    String rebatePirce = String.valueOf(Double.parseDouble(listDTO.price)-Double.parseDouble(listDTO.finalPrice));
                    pay_btn.setText("立即购买￥"+payPrice);
                    rebate_price.setText("立省"+rebatePirce+"元");
                }


                String[] sdf = listDTO.serviceName.split("-");
                String ddd = "";
                if (null != sdf && sdf.length > 0) {
                    ddd = sdf[1];
                }else {
                    if (selectedListDTO.carModel == 1) {
                        ddd = "小轿车";
                    }
                    if (selectedListDTO.carModel == 2) {
                        ddd = "SUV/MPV";
                    }
                    if (selectedListDTO.carModel == 3) {
                        ddd = "全车型";
                    }
                }
                switch (listDTO.carModel) {
                    case 1:
                        selectedListDTO1 = listDTO;
                        station_car_lay.setVisibility(View.VISIBLE);
                        station_car_name.setText(ddd);
                        station_car_price.setText(listDTO.finalPrice);
                        station_car_sub_price.setText(listDTO.price);
                        if (y == 0 && x == 0) {
                            car_select_btn.setSelected(true);
                            serviceType = servicesDTO.name;
                        }
                        break;
                    case 2:
                        selectedListDTO2 = listDTO;
                        station_suv_lay.setVisibility(View.VISIBLE);
                        station_suv_name.setText(ddd);
                        station_suv_price.setText(listDTO.finalPrice);
                        station_suv_sub_price.setText(listDTO.price);
                        if (y == 0 && x == 0) {
                            suv_select_btn.setSelected(true);
                            serviceType = servicesDTO.name;
                        }
                        break;
                    case 3:
                        selectedListDTO3 = listDTO;
                        station_all_car_name.setText(ddd);
                        station_all_car_price.setText(listDTO.finalPrice);
                        station_all_car_sub_price.setText(listDTO.price);
                        if (y == 0 && x == 0) {
                            all_car_select_btn.setSelected(true);
                            serviceType = servicesDTO.name;
                        }
                        break;
                    default:
                        break;
                }

            }

        }


    }
}
