package com.kayu.car_owner_pay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.DistancesParam;
import com.kayu.car_owner_pay.model.OilStationBean;
import com.kayu.car_owner_pay.model.OilsParam;
import com.kayu.car_owner_pay.model.OilsTypeParam;
import com.kayu.car_owner_pay.model.ParamOilBean;
import com.kayu.car_owner_pay.model.ParamParent;
import com.kayu.car_owner_pay.model.SortsParam;
import com.kayu.car_owner_pay.ui.adapter.OilStationAdapter;
import com.kayu.car_owner_pay.ui.adapter.ParamParentAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.location.LocationManagerUtil;
import com.kongzue.dialog.v3.TipGifDialog;
import com.kongzue.dialog.v3.TipGifDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GasStationListActivity extends BaseActivity {
    private RecyclerView station_rv,param_recycle_view;
    private TextView param_distance,param_oil_type,param_sort;
    DistancesParam selectDistanceParam ;
    OilsParam selectOilParam ;
    SortsParam selectSortsParam ;
    private MainViewModel mainViewModel;
    private OilStationAdapter oilStationAdapter;
    private double mLatitude = 0;//纬度
    private double mLongitude = 0;//经度
    private String keyword = null;//搜索关键字
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    //    private Callback callback;
//    private View view;
//    private AdaptiveHeightViewPager viewPager;
//    private int fragment_id;
//    private Context context;

//    public GasStationActivity(AdaptiveHeightViewPager viewPager, int fragment_id, Callback callback) {
//        this.fragment_id = fragment_id;
//        this.viewPager = viewPager;
//        this.callback = callback;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_station_list);
        mainViewModel = ViewModelProviders.of(GasStationListActivity.this).get(MainViewModel.class);
        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("特惠加油");

        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        station_rv = findViewById(R.id.gas_station_rv);
        param_distance = findViewById(R.id.station_param_distance);
        param_oil_type = findViewById(R.id.station_param_oil_type);
        param_sort = findViewById(R.id.station_param_sort);
        param_recycle_view = findViewById(R.id.station_param_recycler);
        param_recycle_view.setLayoutManager(new LinearLayoutManager(GasStationListActivity.this));
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        refreshLayout.setEnableOverScrollDrag(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isRefresh || isLoadmore)
                    return;
                isRefresh = true;
                pageIndex = 1;
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                reqData(refreshLayout, pageIndex,location.getLatitude(),location.getLongitude());
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                if (isRefresh || isLoadmore)
                    return;
                isLoadmore = true;
                pageIndex = pageIndex + 1;
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                reqData(refreshLayout, pageIndex,location.getLatitude(),location.getLongitude());
            }
        });
        station_rv.setLayoutManager(new LinearLayoutManager(GasStationListActivity.this));

        TipGifDialog.show(GasStationListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        mainViewModel.getParamSelect(GasStationListActivity.this).observe( GasStationListActivity.this, new Observer<ParamOilBean>() {
            @Override
            public void onChanged(ParamOilBean paramOilBean) {
                TipGifDialog.dismiss();
                if (null == paramOilBean)
                    return;

                for (DistancesParam item : paramOilBean.distancesParamList){
                    if (item.isDefault == 1){
                        param_distance.setText(item.name);
//                        distance = item.val;
                        selectDistanceParam = item;
                    }
                }
                for (SortsParam item : paramOilBean.sortsParamList) {
                    if (item.isDefault == 1){
                        param_sort.setText(item.name);
//                        sort = item.val;
                        selectSortsParam = item;
                    }
                }
                for (OilsTypeParam oilsTypeParam : paramOilBean.oilsTypeParamList) {
                    for (OilsParam oilsParam : oilsTypeParam.oilsParamList) {
                        if (oilsParam.isDefault == 1) {
                            param_oil_type.setText(oilsParam.oilName);
//                            oilNo = oilsParam.oilNo;
                            selectOilParam = oilsParam;
                        }
                    }

                }
                param_sort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        param_oil_type.setSelected(false);
                        param_distance.setSelected(false);
                        if (param_sort.isSelected() ){
                            param_sort.setSelected(false);
                            param_recycle_view.setVisibility(View.GONE);
                            return;
                        }
                        List<ParamParent> parents= new ArrayList<>();
                        ParamParent paramParent = new ParamParent();
                        paramParent.type = -1;
                        paramParent.objList = new ArrayList<>(paramOilBean.sortsParamList);
                        parents.add(paramParent);
                        showParamViewData(3,parents);
                        param_sort.setSelected(true);
                    }
                });
                param_oil_type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        param_sort.setSelected(false);
                        param_distance.setSelected(false);
                        if (param_oil_type.isSelected() ){
                            param_oil_type.setSelected(false);
                            param_recycle_view.setVisibility(View.GONE);
                            return;
                        }
                        List<ParamParent> parents= new ArrayList<>();
                        for (int x = 0; x < paramOilBean.oilsTypeParamList.size(); x++) {
                            OilsTypeParam item = paramOilBean.oilsTypeParamList.get(x);
                            ParamParent paramParent = new ParamParent();
                            paramParent.type = item.oilType;
                            paramParent.objList = new ArrayList<>(item.oilsParamList);
                            parents.add(paramParent);
                        }
                        showParamViewData(2,parents);
                        param_oil_type.setSelected(true);
                    }
                });
                param_distance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        param_oil_type.setSelected(false);
                        param_sort.setSelected(false);
                        if (param_distance.isSelected() ){
                            param_distance.setSelected(false);
                            param_recycle_view.setVisibility(View.GONE);
                            return;
                        }
                        List<ParamParent> parents= new ArrayList<>();
                        ParamParent paramParent = new ParamParent();
                        paramParent.type = -1;
                        paramParent.objList = new ArrayList<>(paramOilBean.distancesParamList);
                        parents.add(paramParent);
                        showParamViewData(1,parents);
                        param_distance.setSelected(true);
                    }
                });
//                viewPager.setObjectForPosition(view,fragment_id);
                refreshLayout.autoRefresh();
            }
        });

    }


//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        this.view = view;
//
//    }

    private void showParamViewData(int flag, List<ParamParent> data) {
        if (param_recycle_view.getVisibility() != View.VISIBLE)
            param_recycle_view.setVisibility(View.VISIBLE);
        param_recycle_view.setAdapter(new ParamParentAdapter(GasStationListActivity.this, data, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                ParamOilBean paramOilBean = mainViewModel.getParamSelect(GasStationListActivity.this).getValue();
                if (null  == paramOilBean || null == obj)
                    return;

                if (flag == 1 ){
                    selectDistanceParam = (DistancesParam) obj;
                    selectDistanceParam.isDefault = 1;
                    for (DistancesParam item : paramOilBean.distancesParamList){
                        if (item.isDefault == 1){
                            item.isDefault = 0;
                        }
                        if (item.val == selectDistanceParam.val){
                            item.isDefault = 1;
                            item = selectDistanceParam;
                            param_distance.setText(item.name);
                        }
                    }

                } else if (flag == 2) {
                    selectOilParam= (OilsParam) obj;

                    for (OilsTypeParam oilsTypeParam : paramOilBean.oilsTypeParamList) {
                        for (OilsParam item : oilsTypeParam.oilsParamList) {
                            if (item.isDefault == 1){
                                item.isDefault = 0;
                            }
                            if (item.oilNo == selectOilParam.oilNo){
                                item.isDefault = 1;
                                item = selectOilParam;
                                param_oil_type.setText(item.oilName);
                            }
                        }
                    }
                } else if (flag == 3) {
                    selectSortsParam = (SortsParam) obj;
                    for (SortsParam item : paramOilBean.sortsParamList) {
                        if (item.isDefault == 1){
                            item.isDefault = 0;
                        }
                        if (item.val == selectSortsParam.val){
                            item.isDefault = 1;
                            item = selectSortsParam;
                            param_sort.setText(item.name);
                        }
                    }
                }
                if (param_sort.isSelected() ){
                    param_sort.setSelected(false);
                }
                if (param_oil_type.isSelected() ){
                    param_oil_type.setSelected(false);
                }
                if (param_distance.isSelected() ){
                    param_distance.setSelected(false);
                }
                param_recycle_view.setVisibility(View.GONE);

                isRefresh = true;
                pageIndex = 1;
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                reqData(null, pageIndex,location.getLatitude(),location.getLongitude());
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        },flag));
    }

    public void reqData(RefreshLayout refreshLayout, int pageIndex, double latitude, double longitude) {
        if (null == refreshLayout) {
            TipGifDialog.show(GasStationListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        }

        if (null == selectSortsParam || null == selectDistanceParam || null == selectOilParam) {
            mainViewModel.getParamSelect(GasStationListActivity.this);
            TipGifDialog.show(GasStationListActivity.this,"查询参数错误,请重试", TipGifDialog.TYPE.WARNING);
            return;
        }
        mLatitude = latitude;
        mLongitude = longitude;
        if (isRefresh && null != oilStationAdapter)
            oilStationAdapter.removeAllData();

        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("pageNow",pageIndex);
        dataMap.put("pageSize",20);
        dataMap.put("sort",selectSortsParam.val);
        dataMap.put("latitude",latitude);
        dataMap.put("longitude",longitude);
        dataMap.put("distance",selectDistanceParam.val);
        dataMap.put("oilNo",selectOilParam.oilNo);
        dataMap.put("keyword",keyword);
        mainViewModel.getStationList(GasStationListActivity.this,dataMap).observe(GasStationListActivity.this, new Observer<List<OilStationBean>>() {
            @Override
            public void onChanged(List<OilStationBean> oilStationBeans) {
                if (null == refreshLayout) {
                    TipGifDialog.dismiss();
                } else {
                    if (isRefresh) {
                        refreshLayout.finishRefresh();
                    }
                    if (isLoadmore) {
                        refreshLayout.finishLoadMore();
                    }
                }
                if (null == oilStationBeans)
                    return;
                if (isLoadmore) {
                    if (null != oilStationAdapter) {
                        if (null != oilStationBeans && oilStationBeans.size() > 0) {
                            oilStationAdapter.addAllData(oilStationBeans, false);

                        }
                    }
                } else {
                    oilStationAdapter = new OilStationAdapter(GasStationListActivity.this, oilStationBeans, new ItemCallback() {
                        @Override
                        public void onItemCallback(int position, Object obj) {
                            Intent intent = new Intent(GasStationListActivity.this,OilStationActivity.class);
                            intent.putExtra("gasId",((OilStationBean)obj).gasId);
                            startActivity(intent);
                        }

                        @Override
                        public void onDetailCallBack(int position, Object obj) {

                        }
                    });
                    station_rv.setAdapter(oilStationAdapter);
                }
                isRefresh = false;
                isLoadmore = false;
//                viewPager.setObjectForPosition(view,fragment_id);
            }
        });
    }
}