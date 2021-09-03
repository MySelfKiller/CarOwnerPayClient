package com.kayu.car_owner_pay.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.DistancesParam;
import com.kayu.car_owner_pay.model.OilStationBean;
import com.kayu.car_owner_pay.model.OilsParam;
import com.kayu.car_owner_pay.model.OilsTypeParam;
import com.kayu.car_owner_pay.model.ParamOilBean;
import com.kayu.car_owner_pay.model.ParamParent;
import com.kayu.car_owner_pay.model.SortsParam;
import com.kayu.car_owner_pay.model.WebBean;
import com.kayu.car_owner_pay.ui.adapter.OilStationAdapter;
import com.kayu.car_owner_pay.ui.adapter.ParamParentAdapter;
import com.kayu.utils.Constants;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.location.LocationCallback;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.permission.EasyPermissions;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
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
    private String keyword = null;//搜索关键字
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    private RefreshLayout refreshLayout;

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
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        refreshLayout.setEnableOverScrollDrag(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                if (isRefresh || isLoadmore || null ==  location){
                    refreshLayout.finishRefresh();
                    return;
                }
                isRefresh = true;
                pageIndex = 1;
                if ( null != oilStationAdapter)
                    oilStationAdapter.removeAllData(true);
                reqData(refreshLayout, pageIndex,location.getLatitude(),location.getLongitude());
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                if (isRefresh || isLoadmore || null == location) {
                    refreshLayout.finishLoadMore();
                    return;
                }
                isLoadmore = true;
                pageIndex = pageIndex + 1;
                reqData(refreshLayout, pageIndex,location.getLatitude(),location.getLongitude());
            }
        });
        station_rv.setLayoutManager(new LinearLayoutManager(GasStationListActivity.this));

        oilStationAdapter = new OilStationAdapter(GasStationListActivity.this, null,true,true, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                OilStationBean oilStationBean = (OilStationBean)obj;
                if (oilStationBean.nextIsBuy == 1) {
                    AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                    mainViewModel.getPayUrl(GasStationListActivity.this,
                            oilStationBean.gasId, -1,selectOilParam.oilNo,
                            location.getLatitude(),location.getLongitude())
                            .observe(GasStationListActivity.this, new Observer<WebBean>() {
                        @Override
                        public void onChanged(WebBean webBean) {
                            if (null == webBean){
                                ToastUtils.show("未获取到支付信息");
                                return;
                            }

                            Class jumpCls ;
//                            if (oilStationBean.channel.equals("qj")) {
//                                jumpCls = AgentWebViewActivity.class;
//                            } else {
//                                jumpCls = WebViewActivity.class;
//                            }
                            jumpCls = WebViewActivity.class;
                            Intent intent = new Intent(GasStationListActivity.this, jumpCls);
                            intent.putExtra("url", webBean.link);
                            intent.putExtra("title", "订单");
                            intent.putExtra("data",webBean.data);
                            intent.putExtra("channel",oilStationBean.channel);
                            intent.putExtra("gasId",oilStationBean.gasId);
//                                intent.putExtra("from", "首页");
                            startActivityForResult(intent,111);
                        }
                    });
                } else {
                    int userRole = KWApplication.getInstance().userRole;
                    int isPublic = KWApplication.getInstance().isGasPublic;
                    if ( userRole == -2 && isPublic == 0){
                        KWApplication.getInstance().showRegDialog(GasStationListActivity.this);
                        return;
                    }
                    Intent intent = new Intent(GasStationListActivity.this,OilStationActivity.class);
                    intent.putExtra("gasId",((OilStationBean)obj).gasId);
                    startActivity(intent);
                }

            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        });
        station_rv.setAdapter(oilStationAdapter);

        permissionsCheck();
    }

    public void permissionsCheck() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
//        String[] perms = needPermissions;

        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                if (!LocationManagerUtil.getSelf().isLocServiceEnable()){
                    MessageDialog.show(GasStationListActivity.this, "定位服务未开启", "请打开定位服务", "开启定位服务","取消").setCancelable(false)
                            .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v) {
                                    baseDialog.doDismiss();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
//                                    appManager.finishAllActivity();
//                                    LocationManagerUtil.getSelf().stopLocation();
//                                    finish();
                                    onBackPressed();
                                    return true;
                                }
                            }).setCancelButton(new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            onBackPressed();
                            return false;
                        }
                    });
                }else {
                    loadParam();
                }
                if (null == LocationManagerUtil.getSelf().getLoccation()){
                    LocationManagerUtil.getSelf().reStartLocation();
                }
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(GasStationListActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) GasStationListActivity.this);
                dialog.setTitle(getString(R.string.app_name));
                dialog.setMessage(getString(R.string.permiss_location));
                dialog.setOkButton("确定", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
                        return false;
                    }
                }).setCancelButton("取消", new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        onBackPressed();
                        return false;
                    }
                });
                dialog.setCancelable(false);

                dialog.show();
            }
        });
    }


    private void loadParam() {
//        TipGifDialog.show(GasStationListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        mainViewModel.getParamSelect(GasStationListActivity.this).observe( GasStationListActivity.this, new Observer<ParamOilBean>() {
            @Override
            public void onChanged(ParamOilBean paramOilBean) {
//                TipGifDialog.dismiss();
                if (null == paramOilBean)
                    return;

                for (DistancesParam item : paramOilBean.distancesParamList){
                    if (item.isDefault == 1){
                        param_distance.setText(item.name);
                        selectDistanceParam = item;
                    }
                }
                for (SortsParam item : paramOilBean.sortsParamList) {
                    if (item.isDefault == 1){
                        param_sort.setText(item.name);
                        selectSortsParam = item;
                    }
                }
                for (OilsTypeParam oilsTypeParam : paramOilBean.oilsTypeParamList) {
                    for (OilsParam oilsParam : oilsTypeParam.oilsParamList) {
                        if (oilsParam.isDefault == 1) {
                            param_oil_type.setText(oilsParam.oilName);
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
//                refreshLayout.autoRefresh();
                AMapLocation location = LocationManagerUtil.getSelf().getLoccation();
                pageIndex = 1;
                if ( null != oilStationAdapter)
                    oilStationAdapter.removeAllData(true);
                reqData(null, pageIndex,location.getLatitude(),location.getLongitude());
            }
        });
    }

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
                if ( null != oilStationAdapter)
                    oilStationAdapter.removeAllData(true);
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
//                if (null == oilStationBeans)
//                    return;
                if (isLoadmore) {
                    if (null != oilStationAdapter) {
                        if (null != oilStationBeans && oilStationBeans.size() > 0) {
                            oilStationAdapter.addAllData(oilStationBeans, false);

                        }
                    }
                } else {
                    oilStationAdapter.addAllData(oilStationBeans, true);
                }
                isRefresh = false;
                isLoadmore = false;
//                viewPager.setObjectForPosition(view,fragment_id);
            }
        });
    }
}