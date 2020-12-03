package com.kayu.car_owner_pay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.kayu.utils.callback.Callback;
import com.kayu.utils.location.CoordinateTransformUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.view.AdaptiveHeightViewPager;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CarWashListActivity extends BaseActivity {
//    private View view;
//    private final AdaptiveHeightViewPager viewPager;
//    private int fragment_id;
    WashParam selectDistanceParam ;
    WashParam selectSortsParam ;
    private MainViewModel mainViewModel;
    private WashStationAdapter stationAdapter;
    private RecyclerView station_rv,param_recycle_view;
    private TextView param_distance,param_sort;
//    private Callback callback;
    private String mCityName;
    private double mLatitude = 0;//纬度
    private double mLongitude = 0;//经度
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    //    private Context context;

//    public CarWashListActivity(AdaptiveHeightViewPager viewPager, int fragment_id, Callback callback) {
//        this.fragment_id = fragment_id;
//        this.viewPager = viewPager;
//        this.callback = callback;
//    }

//    public CarWashListActivity setFragment_id(int fragment_id) {
//        this.fragment_id = fragment_id;
//        return this;
//    }

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
        param_recycle_view.setLayoutManager(new LinearLayoutManager(CarWashListActivity.this));

        WaitDialog.show(CarWashListActivity.this,"稍等...");
        mainViewModel.getParamWash(CarWashListActivity.this).observe(CarWashListActivity.this, new Observer<ParamWashBean>() {
            @Override
            public void onChanged(ParamWashBean paramWashBean) {
                WaitDialog.dismiss();
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
//                viewPager.setObjectForPosition(view,fragment_id);
                refreshLayout.autoRefresh();
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
//        return inflater.inflate(R.layout.fragment_home_car_wash, container, false);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        this.view = view;
//
//    }

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
                reqData(null, pageIndex,bddfsdfs[1],bddfsdfs[0],location.getCity());
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        },flag));
    }

    public void reqData(RefreshLayout refreshLayout, int pageIndex, double latitude, double longitude, String cityName) {
        if (null == refreshLayout) {
            WaitDialog.show(CarWashListActivity.this, "稍等...");
        }

        if (null == selectSortsParam || null == selectDistanceParam) {
            mainViewModel.getParamWash(CarWashListActivity.this);
            TipDialog.show(CarWashListActivity.this,"查询参数错误,请重试", TipDialog.TYPE.WARNING);
            return;
        }

        mLatitude = latitude;
        mLongitude = longitude;
        mCityName = cityName;
        if (isRefresh && null != stationAdapter)
            stationAdapter.removeAllData();
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
                    WaitDialog.dismiss();
                }else {
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
                    if (null != stationAdapter) {
                        if (null != oilStationBeans && oilStationBeans.size() > 0) {
                            stationAdapter.addAllData(oilStationBeans, false);
                        }
                    }
                } else {
                    stationAdapter = new WashStationAdapter(CarWashListActivity.this,oilStationBeans, selectSortsParam.val,new ItemCallback() {
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
                }
                isLoadmore = false;
                isRefresh = false;
//                viewPager.setObjectForPosition(view,fragment_id);
            }
        });

    }

}