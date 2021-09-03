package com.kayu.car_owner_pay.ui;

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

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.WashStationActivity;
import com.kayu.car_owner_pay.activity.login.SetPasswordActivity;
import com.kayu.car_owner_pay.model.ParamParent;
import com.kayu.car_owner_pay.model.ParamWashBean;
import com.kayu.car_owner_pay.model.WashParam;
import com.kayu.car_owner_pay.model.WashStationBean;
import com.kayu.car_owner_pay.ui.adapter.ParamParentAdapter;
import com.kayu.car_owner_pay.ui.adapter.WashStationAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.view.AdaptiveHeightViewPager;
import com.kongzue.dialog.v3.TipGifDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

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

    public HomeCarWashFragment(AdaptiveHeightViewPager viewPager, int fragment_id, Callback callback) {
        this.fragment_id = fragment_id;
        this.viewPager = viewPager;
        this.callback = callback;
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
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        LogUtil.e("-------HomeCarWashFragment----","----onCreateView---");
        view = inflater.inflate(R.layout.fragment_home_car_wash, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
//        this.view = view;
        station_rv = view.findViewById(R.id.car_wash_rv);
        param_distance = view.findViewById(R.id.car_wash_param_distance);
        param_sort = view.findViewById(R.id.car_wash_param_sort);
        param_recycle_view = view.findViewById(R.id.car_wash_param_recycler);

        station_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        param_recycle_view.setLayoutManager(new LinearLayoutManager(getContext()));

        stationAdapter = new WashStationAdapter(getContext(),null,true,true,new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                int userRole = KWApplication.getInstance().userRole;
                int isPublic = KWApplication.getInstance().isWashPublic;
                if ( userRole == -2 && isPublic == 0){
                    KWApplication.getInstance().showRegDialog(getContext());
                    return;
                }
                Intent intent = new Intent(getContext(), WashStationActivity.class);
                intent.putExtra("shopCode",((WashStationBean)obj).shopCode);
                startActivity(intent);
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        });
        station_rv.setAdapter(stationAdapter);


        mainViewModel.getParamWash(requireContext()).observe(requireActivity(), new Observer<ParamWashBean>() {
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
            }
        });
        LogUtil.e("-------HomeCarWashFragment----","----onViewCreated---");
    }

    private void showParamViewData(int flag, List<ParamParent> data) {
        if (param_recycle_view.getVisibility() == View.GONE)
            param_recycle_view.setVisibility(View.VISIBLE);
        param_recycle_view.setAdapter(new ParamParentAdapter(getContext(), data, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                ParamWashBean paramWashBean = mainViewModel.getParamWash(requireContext()).getValue();
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

    public void reqData(RefreshLayout refreshLayout, int pageIndex, final boolean isRefresh, final boolean isLoadmore, double latitude, double longitude, String cityName) {
        if (null == refreshLayout) {
            if (isAdded()) {
                TipGifDialog.show((AppCompatActivity) requireContext(), "刷新数据,稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
            }
        }
//        else {
//            callback.onSuccess();
//        }

        if (null == selectSortsParam || null == selectDistanceParam) {
            mainViewModel.getParamWash(requireContext());
//            if (isAdded()) {
//                TipGifDialog.show((AppCompatActivity) requireContext(),"查询参数错误,请重试", TipGifDialog.TYPE.WARNING);
//            }

            return;
        }

        mLatitude = latitude;
        mLongitude = longitude;
        mCityName = cityName;
        if (isRefresh && null != stationAdapter)
            stationAdapter.removeAllData(true);
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("pageNum",pageIndex);
        dataMap.put("pageSize",20);
        dataMap.put("cusLatitude",String.valueOf(latitude));
        dataMap.put("cusLongitude",String.valueOf(longitude));
        dataMap.put("cityName",cityName);
        dataMap.put("priority",selectDistanceParam.val);
        dataMap.put("serviceCode",selectSortsParam.val);
        mainViewModel.getWashStationList(requireContext(),dataMap).observe(requireActivity(), new Observer<List<WashStationBean>>() {
            @Override
            public void onChanged(List<WashStationBean> oilStationBeans) {
                if (null == refreshLayout) {
                    TipGifDialog.dismiss();
                }
                if (null != callback){
                    callback.onSuccess();
                }
//                if (null == oilStationBeans)
//                    return;
                if (isLoadmore) {
                    if (null != stationAdapter) {
                        if (null != oilStationBeans && oilStationBeans.size() > 0) {
                            stationAdapter.addAllData(oilStationBeans, selectSortsParam.val, false);
                        }
                    }
                } else {
                    stationAdapter.addAllData(oilStationBeans, selectSortsParam.val, true);


                }
                viewPager.setObjectForPosition(view,fragment_id);
            }
        });

    }

}