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
import com.kayu.car_owner_pay.model.ParamParent;
import com.kayu.car_owner_pay.model.ParamWashBean;
import com.kayu.car_owner_pay.model.WashParam;
import com.kayu.car_owner_pay.model.WashStationBean;
import com.kayu.car_owner_pay.ui.adapter.ParamParentAdapter;
import com.kayu.car_owner_pay.ui.adapter.WashStationAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.location.CoordinateTransformUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kongzue.dialog.v3.TipGifDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CarWashListActivity extends BaseActivity {
    WashParam selectDistanceParam ;
    WashParam selectSortsParam ;
    private MainViewModel mainViewModel;
    private WashStationAdapter stationAdapter;
    private RecyclerView station_rv,param_recycle_view;
    private TextView param_distance,param_sort;
//    private Callback callback;
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash_list);
        mainViewModel = ViewModelProviders.of(CarWashListActivity.this).get(MainViewModel.class);

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("特惠洗车");

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

        station_rv = findViewById(R.id.car_wash_rv);
        param_distance = findViewById(R.id.car_wash_param_distance);
        param_sort = findViewById(R.id.car_wash_param_sort);
        param_recycle_view = findViewById(R.id.car_wash_param_recycler);
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
                if (null != stationAdapter){
                    stationAdapter.removeAllData(true);
                }
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                double[] bddfsdfs = CoordinateTransformUtil.gcj02tobd09(location.getLongitude(), location.getLatitude());
                reqData(refreshLayout, pageIndex,bddfsdfs[1],bddfsdfs[0],location.getCity());
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
                double[] bddfsdfs = CoordinateTransformUtil.gcj02tobd09(location.getLongitude(), location.getLatitude());
                reqData(refreshLayout, pageIndex,bddfsdfs[1],bddfsdfs[0],location.getCity());
            }
        });

        station_rv.setLayoutManager(new LinearLayoutManager(CarWashListActivity.this));

        stationAdapter = new WashStationAdapter(CarWashListActivity.this,null,true,true,new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                Intent intent = new Intent(CarWashListActivity.this, WashStationActivity.class);
                intent.putExtra("shopCode",((WashStationBean)obj).shopCode);
                startActivity(intent);
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        });
        station_rv.setAdapter(stationAdapter);

        param_recycle_view.setLayoutManager(new LinearLayoutManager(CarWashListActivity.this));

        TipGifDialog.show(CarWashListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        mainViewModel.getParamWash(CarWashListActivity.this).observe(CarWashListActivity.this, new Observer<ParamWashBean>() {
            @Override
            public void onChanged(ParamWashBean paramWashBean) {
                TipGifDialog.dismiss();
                if (null == paramWashBean)
                    return;
                for (WashParam item : paramWashBean.desList){
                    if (item.isDefault == 1){
                        param_distance.setText(item.name);
                        selectDistanceParam = item;
                    }
                }
                for (WashParam item : paramWashBean.typesList) {
                    if (item.isDefault == 1){
                        param_sort.setText(item.name);
                        selectSortsParam = item;
                    }
                }

                param_sort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        param_distance.setSelected(false);
                        if (param_sort.isSelected() ){
                            param_sort.setSelected(false);
                            param_recycle_view.setVisibility(View.GONE);
                            return;
                        }
                        List<ParamParent> parents= new ArrayList<>();
                        ParamParent paramParent = new ParamParent();
                        paramParent.type = -1;
                        paramParent.objList = new ArrayList<>(paramWashBean.typesList);
                        parents.add(paramParent);
                        showParamViewData(5,parents);
                        param_sort.setSelected(true);
                    }
                });
                param_distance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        param_sort.setSelected(false);
                        if (param_distance.isSelected() ){
                            param_distance.setSelected(false);
                            param_recycle_view.setVisibility(View.GONE);
                            return;
                        }
                        List<ParamParent> parents= new ArrayList<>();
                        ParamParent paramParent = new ParamParent();
                        paramParent.type = -1;
                        paramParent.objList = new ArrayList<>(paramWashBean.desList);
                        parents.add(paramParent);
                        showParamViewData(4,parents);
                        param_distance.setSelected(true);
                    }
                });
                refreshLayout.autoRefresh();
            }
        });
    }

    private void showParamViewData(int flag, List<ParamParent> data) {
        if (param_recycle_view.getVisibility() == View.GONE)
            param_recycle_view.setVisibility(View.VISIBLE);
        param_recycle_view.setAdapter(new ParamParentAdapter(CarWashListActivity.this, data, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                ParamWashBean paramWashBean = mainViewModel.getParamWash(CarWashListActivity.this).getValue();
                if (null  == paramWashBean || null == obj)
                    return;

                if (flag == 4 ){
                    selectDistanceParam = (WashParam) obj;
                    selectDistanceParam.isDefault = 1;
                    for (WashParam item : paramWashBean.desList){
                        if (item.isDefault == 1){
                            item.isDefault = 0;
                        }
                        if (item.val.equals(selectDistanceParam.val)){
                            item.isDefault = 1;
                            item = selectDistanceParam;
                            param_distance.setText(item.name);
                        }
                    }

                } else if (flag == 5) {
                    selectSortsParam = (WashParam) obj;
                    for (WashParam item : paramWashBean.typesList) {
                        if (item.isDefault == 1){
                            item.isDefault = 0;
                        }
                        if (item.val.equals(selectSortsParam.val)){
                            item.isDefault = 1;
                            item = selectSortsParam;
                            param_sort.setText(item.name);
                        }
                    }
                }
                if (param_sort.isSelected() ){
                    param_sort.setSelected(false);
                }
                if (param_distance.isSelected() ){
                    param_distance.setSelected(false);
                }
                param_recycle_view.setVisibility(View.GONE);
                pageIndex = 1;
                isRefresh = true;
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                double[] bddfsdfs = CoordinateTransformUtil.gcj02tobd09(location.getLongitude(), location.getLatitude());
                if (null != stationAdapter){
                    stationAdapter.removeAllData(true);
                }
                reqData(null, pageIndex,bddfsdfs[1],bddfsdfs[0],location.getCity());
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        },flag));
    }

    public void reqData(RefreshLayout refreshLayout, int pageIndex, double latitude, double longitude, String cityName) {
        if (null == refreshLayout) {
            TipGifDialog.show(CarWashListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        }

        if (null == selectSortsParam || null == selectDistanceParam) {
            mainViewModel.getParamWash(CarWashListActivity.this);
            TipGifDialog.show(CarWashListActivity.this,"查询参数错误,请重试", TipGifDialog.TYPE.WARNING);
            return;
        }

        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("pageNum",pageIndex);
        dataMap.put("pageSize",20);
        dataMap.put("cusLatitude",String.valueOf(latitude));
        dataMap.put("cusLongitude",String.valueOf(longitude));
        dataMap.put("cityName",cityName);
        dataMap.put("priority",selectDistanceParam.val);
        dataMap.put("serviceCode",selectSortsParam.val);
        mainViewModel.getWashStationList(CarWashListActivity.this,dataMap).observe(CarWashListActivity.this, new Observer<List<WashStationBean>>() {
            @Override
            public void onChanged(List<WashStationBean> oilStationBeans) {
                if (null == refreshLayout) {
                    TipGifDialog.dismiss();
                }else {
                    if (isRefresh) {
                        refreshLayout.finishRefresh();

                    }
                    if (isLoadmore) {
                        refreshLayout.finishLoadMore();
                    }
                }
                if (isLoadmore) {
                    if (null != stationAdapter) {
                        if (null != oilStationBeans && oilStationBeans.size() > 0) {
                            stationAdapter.addAllData(oilStationBeans, selectSortsParam.val, false);
                        }
                    }
                } else {
                    stationAdapter.addAllData(oilStationBeans, selectSortsParam.val, true);

                }
                isLoadmore = false;
                isRefresh = false;
            }
        });

    }

}