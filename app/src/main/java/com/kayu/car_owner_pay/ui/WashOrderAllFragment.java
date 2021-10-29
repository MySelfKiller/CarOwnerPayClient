package com.kayu.car_owner_pay.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.BaseActivity;
import com.kayu.car_owner_pay.activity.WashStationActivity;
import com.kayu.car_owner_pay.activity.WashUnusedActivity;
import com.kayu.car_owner_pay.data_parser.WashOrderListDataParser;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.model.ItemWashOrderBean;
import com.kayu.car_owner_pay.ui.adapter.ItemWashOrderAdapter;
import com.kayu.utils.Constants;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.permission.EasyPermissions;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WashOrderAllFragment extends Fragment {
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    private RefreshLayout refreshLayout;
    private ArrayList<ItemWashOrderBean> orderData;
    private RecyclerView recyclerView;
    private ItemWashOrderAdapter adapter;
    private int orderStatus;

    public WashOrderAllFragment(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wash_order_all, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
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
                if (null != adapter){
                    adapter.removeAllData();
                }
                reqData(orderStatus);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                if (isRefresh || isLoadmore)
                    return;
                isLoadmore = true;
                pageIndex = pageIndex + 1;
                reqData(orderStatus);
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.custom_list_recycler);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        if (getUserVisibleHint() && !mHasLoadedOnce){
//            refreshLayout.autoRefresh();
////            mHasLoadedOnce = true;
//        }
        isCreated = true;
    }

    private boolean mHasLoadedOnce = false;// 页面已经加载过
    private boolean isCreated = false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("hm","WashOrderAllFragment---------setUserVisibleHint===="+isVisibleToUser);
        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isCreated = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.e("hm","WashOrderAllFragment---------onPause===="+getUserVisibleHint()+orderStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("hm","WashOrderAllFragment---------onResume===="+getUserVisibleHint()+orderStatus);
        if (getUserVisibleHint() && !mHasLoadedOnce && isCreated) {
            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("hm","WashOrderAllFragment---------onStart===="+getUserVisibleHint()+orderStatus);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.e("hm","WashOrderAllFragment---------onStop===="+getUserVisibleHint()+orderStatus);
    }

    @SuppressLint("HandlerLeak")
    private void reqData(int orderStatus) {
        RequestInfo reques = new RequestInfo();
        reques.context = getContext();
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_LIST;
        reques.parser = new WashOrderListDataParser();
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("pageNow", pageIndex);
        if (orderStatus > 0) {
            reqDateMap.put("state", orderStatus);
        }
        reqDateMap.put("pageSize", 20);
        reques.reqDataMap = reqDateMap;
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                if (null == WashOrderAllFragment.this.getActivity()){
                    LogUtil.e("hm","WashOrderAllFragment 以销毁");
                    return;
                }
                if (resInfo.status == 1) {
                    orderData = (ArrayList<ItemWashOrderBean>) resInfo.responseData;
                    initViewData();
                }else {
                    ToastUtils.show(resInfo.msg);
                }
                if (isRefresh) {
                    refreshLayout.finishRefresh();
                    isRefresh = false;
                }
                if (isLoadmore) {
                    refreshLayout.finishLoadMore();
                    isLoadmore = false;
                }

                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

    private void initViewData() {
        if (isLoadmore) {
            if (null != adapter) {
                if (null != orderData && orderData.size()>0){
                    adapter.addAllData(orderData,false);
                }
            }
        } else {
            adapter = new ItemWashOrderAdapter(requireActivity(), orderData,  (null == orderData || orderData.size() == 0), new ItemCallback() {
                @Override
                public void onItemCallback(int position, Object obj) {
                    ItemWashOrderBean washOrderBean = (ItemWashOrderBean)obj;
                    KWApplication.getInstance().toNavi(getContext(),washOrderBean.latitude,washOrderBean.longitude,washOrderBean.address,"BD09");
                }

                @Override
                public void onDetailCallBack(int position, Object obj) {
                    permissionsCheck(new String[]{Manifest.permission.CALL_PHONE}, R.string.permiss_call_phone,new Callback() {
                        @Override
                        public void onSuccess() {
                            KWApplication.getInstance().callPhone(getActivity(),((ItemWashOrderBean)obj).telephone);
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    public void permissionsCheck(String[] perms, int resId, @NonNull Callback callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        ((BaseActivity)getActivity()).performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new BaseActivity.PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                callback.onSuccess();
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(requireContext(), 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) requireActivity());
                dialog.setTitle("需要获取以下权限");
                dialog.setMessage(getString(resId));
                dialog.setOkButton("下一步", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
                        return false;
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }
}