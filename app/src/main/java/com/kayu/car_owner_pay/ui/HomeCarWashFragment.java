package com.kayu.car_owner_pay.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.model.ParamParent;
import com.kayu.car_owner_pay.model.ParamWashBean;
import com.kayu.car_owner_pay.model.WashParam;
import com.kayu.car_owner_pay.model.WashStationBean;
import com.kayu.car_owner_pay.ui.adapter.ParamParentAdapter;
import com.kayu.car_owner_pay.ui.adapter.WashStationAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.view.AdaptiveHeightViewPager;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeCarWashFragment extends Fragment {
    private View view;
    private final AdaptiveHeightViewPager viewPager;
    private int fragment_id;
    WashParam selectDistanceParam ;
    WashParam selectSortsParam ;
    private MainViewModel mainViewModel;
    private WashStationAdapter stationAdapter;
    private RecyclerView station_rv,param_recycle_view;
    private TextView param_distance,param_sort;
    private Callback callback;
    private String mCityName;
    private double mLatitude = 0;//纬度
    private double mLongitude = 0;//经度
    private Context context;

    public HomeCarWashFragment(Context context, MainViewModel mainViewModel , AdaptiveHeightViewPager viewPager, int fragment_id, Callback callback) {
        this.fragment_id = fragment_id;
        this.viewPager = viewPager;
        this.callback = callback;
        this.mainViewModel = mainViewModel;
        this.context = context;
    }

    public HomeCarWashFragment setFragment_id(int fragment_id) {
        this.fragment_id = fragment_id;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_car_wash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        station_rv = view.findViewById(R.id.car_wash_rv);
        param_distance = view.findViewById(R.id.car_wash_param_distance);
        param_sort = view.findViewById(R.id.car_wash_param_sort);
        param_recycle_view = view.findViewById(R.id.car_wash_param_recycler);

        station_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        param_recycle_view.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void showParamViewData(int flag, List<ParamParent> data) {
        if (param_recycle_view.getVisibility() == View.GONE)
            param_recycle_view.setVisibility(View.VISIBLE);
        param_recycle_view.setAdapter(new ParamParentAdapter(getContext(), data, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                ParamWashBean paramWashBean = mainViewModel.getParamWash(context).getValue();
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
                int pageIndex = 1;
                callback.onError();
                reqData(null, pageIndex, true,false,mLatitude,mLongitude,mCityName);
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        },flag));
    }

    public void reqData(RefreshLayout refreshLayout, int pageIndex, final boolean isRefresh, final boolean isLoadmore,double latitude,double longitude,String cityName) {
        if (null == refreshLayout) {
            WaitDialog.show((AppCompatActivity) getContext(), "刷新数据！稍等");
        } else {
            callback.onSuccess();
        }

        mainViewModel.getParamWash(context).observe((LifecycleOwner) context, new Observer<ParamWashBean>() {
            @Override
            public void onChanged(ParamWashBean paramWashBean) {
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
                viewPager.setObjectForPosition(view,fragment_id);

                if (null == selectSortsParam || null == selectDistanceParam) {
                    mainViewModel.getParamWash(context);
                    TipDialog.show((AppCompatActivity) getContext(),"查询参数错误,请重试", TipDialog.TYPE.WARNING);
                    return;
                }

                mLatitude = latitude;
                mLongitude = longitude;
                mCityName = cityName;

                HashMap<String,Object> dataMap = new HashMap<>();
                dataMap.put("pageNum",pageIndex);
                dataMap.put("pageSize",20);
                dataMap.put("cusLatitude",String.valueOf(latitude));
                dataMap.put("cusLongitude",String.valueOf(longitude));
                dataMap.put("cityName",cityName);
                dataMap.put("priority",selectDistanceParam.val);
                dataMap.put("serviceCode",selectSortsParam.val);
                mainViewModel.getWashStationList(context,dataMap).observe((LifecycleOwner) context, new Observer<List<WashStationBean>>() {
                    @Override
                    public void onChanged(List<WashStationBean> oilStationBeans) {
                        if (null == refreshLayout) {
                            WaitDialog.dismiss();
                        }
                        callback.onSuccess();
                        if (null == oilStationBeans)
                            return;
                        if (isLoadmore) {
                            if (null != stationAdapter) {
                                if (null != oilStationBeans && oilStationBeans.size() > 0) {
                                    stationAdapter.addAllData(oilStationBeans, false);
                                }
                            }
                        } else {
                            stationAdapter = new WashStationAdapter(getContext(),oilStationBeans, selectSortsParam.val,new ItemCallback() {
                                @Override
                                public void onItemCallback(int position, Object obj) {
                                    FragmentManager fg = requireActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
                                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    fragmentTransaction.add(R.id.main_root_lay,new WashStationFragment(((WashStationBean)obj).shopCode));
                                    fragmentTransaction.addToBackStack("ddd");
                                    fragmentTransaction.commit();
                                }

                                @Override
                                public void onDetailCallBack(int position, Object obj) {

                                }
                            });
                            station_rv.setAdapter(stationAdapter);

                        }
                        viewPager.setObjectForPosition(view,fragment_id);
                    }
                });
            }
        });
    }

//    public void checkLocation(RefreshLayout refreshLayout, int pageIndex, boolean isRefresh, boolean isLoadmore, Callback callback) {
//        this.callback = callback;
//        if (isRefresh && null != stationAdapter)
//            stationAdapter.removeAllData();
//
////        WaitDialog.show((AppCompatActivity) getContext(),"定位中...");
//        LocationManager.getSelf().startLocation();
//        LocationManager.getSelf().setLocationListener(new LocationCallback() {
//            @Override
//            public void onLocationChanged(AMapLocation location) {
////                WaitDialog.dismiss();
//                if (location.getErrorCode() == 0) {
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
//                    cityName = location.getCity();
////                    refreshLayout.autoRefresh();
//                    reqData(refreshLayout,pageIndex, isRefresh, isLoadmore);
//                } else {
//                    MessageDialog.show((AppCompatActivity)getActivity(), "定位失败", "请重新定位", "重新定位")
//                            .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
//                                @Override
//                                public boolean onClick(BaseDialog baseDialog, View v) {
//                                    checkLocation(refreshLayout,pageIndex,isRefresh,isLoadmore,callback);
//                                    return true;
//                                }
//                            });
//                }
//            }
//        });
//    }

}