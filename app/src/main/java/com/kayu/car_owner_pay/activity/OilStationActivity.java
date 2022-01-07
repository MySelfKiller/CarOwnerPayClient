package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.amap.api.location.AMapLocation;
import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.OilStationBean;
import com.kayu.car_owner_pay.model.OilsParam;
import com.kayu.car_owner_pay.model.OilsTypeParam;
import com.kayu.car_owner_pay.model.WebBean;
import com.kayu.car_owner_pay.ui.adapter.ProductTypeAdapter;
import com.kayu.utils.DoubleUtils;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kongzue.dialog.v3.TipGifDialog;

import java.util.ArrayList;
import java.util.Arrays;


public class OilStationActivity extends BaseActivity {


    private String gasId;
    private RecyclerView select_oil_rv, select_oil_gun_rv;
    private MainViewModel mainViewModel;
    private TextView next_ask_btn;
    private TextView oil_price_sub2;
    private TextView oil_price_sub1;
    private TextView oil_price;
    private TextView station_location;
    private TextView station_name;
    private ImageView station_img;
    private RecyclerView select_oil_type_rv;
    private ProductTypeAdapter rootTypeAdapter;
    private ProductTypeAdapter childTypeAdapter;
    private ProductTypeAdapter parentTypeAdapter;

    private String gunNo = null;//默认选中的枪号
//    private ConstraintLayout tip_lay;
//    private TextView tip_content;
//    private TextView tip_title;
//    private RefreshLayout refreshLayout;
//    boolean isLoadmore = false;
//    boolean isRefresh = false;

//    public OilStationActivity(String gasId) {
//        this.gasId = gasId;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.e("StationFragment----", "----onCreate---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_oil_station);
        mainViewModel = ViewModelProviders.of(OilStationActivity.this).get(MainViewModel.class);
        gasId = getIntent().getStringExtra("gasId");
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
        title_name.setVisibility(View.VISIBLE);
        title_name.setText("详情");
//        back_tv.setText("首页");

//        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
//        refreshLayout.setEnableAutoLoadMore(false);
//        refreshLayout.setEnableLoadMore(false);
//        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
//        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
//        refreshLayout.setEnableOverScrollDrag(true);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                if (isRefresh || isLoadmore)
//                    return;
//                isRefresh = true;
//                loadView();
//            }
//        });


        station_img = findViewById(R.id.station_img);
        station_name = findViewById(R.id.station_name);
        station_location = findViewById(R.id.station_location);
        oil_price = findViewById(R.id.station_oil_price);
        oil_price_sub1 = findViewById(R.id.station_oil_price_sub1);
        oil_price_sub2 = findViewById(R.id.station_oil_price_sub2);
        next_ask_btn = findViewById(R.id.station_next_tv);
//        tip_lay = findViewById(R.id.oil_station_tip_lay);
//        tip_title = findViewById(R.id.oil_station_tip_title);
//        tip_content = findViewById(R.id.oil_station_tip_content);

        select_oil_type_rv = findViewById(R.id.station_select_oil_type_rv);
        select_oil_rv = findViewById(R.id.station_select_oil_rv);
        select_oil_gun_rv = findViewById(R.id.station_select_oil_gun_rv);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
//        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        select_oil_type_rv.setLayoutManager(manager);

        select_oil_rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));

        StaggeredGridLayoutManager manager1 = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
//        manager1.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        select_oil_gun_rv.setLayoutManager(manager1);

//        refreshLayout.autoRefresh();
        loadView();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_oil_station, container, false);
//        return root;
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (!mHasLoadedOnce){
//            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
//        }
//        isCreated = true;
//    }
//    private boolean mHasLoadedOnce = false;// 页面已经加载过
//    private boolean isCreated = false;
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
//            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
//        }
//    }

    private void loadView(){

//        mainViewModel.getParameter(OilStationActivity.this,13).observe(OilStationActivity.this, new Observer<SystemParam>() {
//            @Override
//            public void onChanged(SystemParam systemParam) {
//                if (null != systemParam) {
//                    if (StringUtil.isEmpty(systemParam.title) || StringUtil.isEmpty(systemParam.content)) {
//                        tip_lay.setVisibility(View.GONE);
//                        return;
//                    }
//                    tip_title.setText(systemParam.title);
//                    tip_content.setText(systemParam.content.replace("#","\n"));
//                    tip_lay.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        TipGifDialog.show(OilStationActivity.this, "请稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        mainViewModel.getOilStationDetail(OilStationActivity.this, gasId).observe(OilStationActivity.this, new Observer<OilStationBean>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(OilStationBean oilStationBean) {
                if (null == oilStationBean) {
                    TipGifDialog.show(OilStationActivity.this, "数据获取错误", TipGifDialog.TYPE.ERROR);
                    return;
                }
                TipGifDialog.dismiss();
                station_name.setText(oilStationBean.gasName);
                station_location.setText(oilStationBean.gasAddress);

                KWApplication.getInstance().loadImg(oilStationBean.gasLogoSmall, station_img);

                RootParamItemCallback rootParamItemCallback = new RootParamItemCallback();
                ParentParamItemCallback parentParamItemCallback = new ParentParamItemCallback();
                ChildParamItemCallback childParamItemCallback = new ChildParamItemCallback();

                rootTypeAdapter = new ProductTypeAdapter(OilStationActivity.this, new ArrayList<>(oilStationBean.oilsTypeList), 0, rootParamItemCallback);
                select_oil_type_rv.setAdapter(rootTypeAdapter);

                parentTypeAdapter = new ProductTypeAdapter(OilStationActivity.this, new ArrayList<>(oilStationBean.oilsTypeList.get(0).oilsParamList), 1, parentParamItemCallback);
                select_oil_rv.setAdapter(parentTypeAdapter);
                OilsParam defultOilParam = oilStationBean.oilsTypeList.get(0).oilsParamList.get(0);
                selectedOilNo = defultOilParam.oilNo;
                oil_price.setText(String.valueOf(defultOilParam.priceYfq));
                oil_price_sub1.setText("比国标价降" + DoubleUtils.sub(defultOilParam.priceOfficial, defultOilParam.priceYfq) + "元");
                oil_price_sub2.setText("比油站降" + DoubleUtils.sub(defultOilParam.priceGun, defultOilParam.priceYfq) + "元");
                String[] gunArrs = defultOilParam.gunNos.split(",");
//                gunNo = gunArrs[0];
                childTypeAdapter = new ProductTypeAdapter(OilStationActivity.this, new ArrayList<>(Arrays.asList(gunArrs)), 2, childParamItemCallback);
                select_oil_gun_rv.setAdapter(childTypeAdapter);

                next_ask_btn.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        if (StringUtil.isEmpty(gunNo)) {
                            TipGifDialog.show(OilStationActivity.this,"请选择枪号", TipGifDialog.TYPE.WARNING);
                            return;
                        }
                        AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                        mainViewModel.getPayUrl(OilStationActivity.this, gasId,
                                Integer.parseInt(gunNo),selectedOilNo,
                                location.getLatitude(),location.getLongitude())
                                .observe(OilStationActivity.this, new Observer<WebBean>() {
                            @Override
                            public void onChanged(WebBean webBean) {
                                if (null  == webBean){
                                    ToastUtils.show("未获取到支付信息");
                                    return;
                                }
                                Intent intent = new Intent(OilStationActivity.this, WebViewActivity.class);
                                intent.putExtra("url", webBean.link);
                                intent.putExtra("title", "订单");
                                intent.putExtra("data",webBean.data);
                                intent.putExtra("gasId",oilStationBean.gasId);
//                                intent.putExtra("from", "首页");
                                startActivityForResult(intent,111);
                            }
                        });
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });
            }
        });
    }

    class RootParamItemCallback implements ItemCallback {
        @Override
        public void onItemCallback(int position, Object obj) {
            OilsTypeParam typeParam = (OilsTypeParam) obj;
//            rootSelectedIndex = position;
            parentTypeAdapter.addAllData(new ArrayList<>(typeParam.oilsParamList), true);
            OilsParam param = typeParam.oilsParamList.get(0);
            oil_price.setText(String.valueOf(param.priceYfq));
            oil_price_sub1.setText("比国标价降" + DoubleUtils.sub(param.priceOfficial, param.priceYfq) + "元");
            oil_price_sub2.setText("比油站降" + DoubleUtils.sub(param.priceGun, param.priceYfq) + "元");

            String[] gunArrs = param.gunNos.split(",");
            gunNo = null;
            childTypeAdapter.addAllData(new ArrayList<>(Arrays.asList(gunArrs)), true);
        }

        @Override
        public void onDetailCallBack(int position, Object obj) {

        }
    }
    int selectedOilNo = -100;
    class ParentParamItemCallback implements ItemCallback {
        @Override
        public void onItemCallback(int position, Object obj) {
//            parentSelectedIndex = position;
            OilsParam param = (OilsParam) obj;
            selectedOilNo = param.oilNo;
            oil_price.setText(String.valueOf(param.priceYfq));
            oil_price_sub1.setText("比国标价降" + DoubleUtils.sub(param.priceOfficial, param.priceYfq) + "元");
            oil_price_sub2.setText("比油站降" + DoubleUtils.sub(param.priceGun, param.priceYfq) + "元");

            String[] gunArrs = param.gunNos.split(",");
            gunNo = null;
            childTypeAdapter.addAllData(new ArrayList<>(Arrays.asList(gunArrs)), true);
        }

        @Override
        public void onDetailCallBack(int position, Object obj) {

        }
    }

    class ChildParamItemCallback implements ItemCallback {
        @Override
        public void onItemCallback(int position, Object obj) {
            gunNo = (String) obj;
        }

        @Override
        public void onDetailCallBack(int position, Object obj) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            mainViewModel.sendOilPayInfo(OilStationActivity.this);
        }
    }
}