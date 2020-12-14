package com.kayu.car_owner_pay.ui.income;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.data_parser.IncomeDetailedParser;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;

public class DetailedIncomeFragment extends Fragment {
    boolean isLoadmore =false;
    boolean isRefresh =false;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private int balanceType;//收入类型 null全部,0:支出 1:收入

    private IncomeDetialedItemRecyclerAdapter adapter;

    public DetailedIncomeFragment(int balanceType) {
        this.balanceType = balanceType;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_income, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.detailed_recycler);
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
                reqData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                if (isRefresh || isLoadmore)
                    return;
                isLoadmore = true;
                pageIndex = pageIndex + 1;
                reqData();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getUserVisibleHint() && !mHasLoadedOnce){
            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
        }
        isCreated = true;
    }

    private boolean mHasLoadedOnce = false;// 页面已经加载过
    private boolean isCreated = false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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


    private int pageIndex;
    @SuppressLint("HandlerLeak")
    private void reqData(){
        RequestInfo reques = new RequestInfo();
        reques.context = getContext();
        reques.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_BALANCE_DEAIL;
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("pageNow", pageIndex);
        reqDateMap.put("pageSize", 20 );
        reqDateMap.put("type", balanceType);
        reques.parser = new IncomeDetailedParser();
        reques.reqDataMap = reqDateMap;
        reques.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo)msg.obj;

                if (resInfo.status ==1){
                    List<IncomeDetailedData> dataList = (List<IncomeDetailedData>)resInfo.responseData;
                    initViewData(dataList);
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

    private void initViewData(List<IncomeDetailedData> datalist) {
        if (isLoadmore){
            if (null != adapter) {
                if (null != datalist && datalist.size()>0){
                    adapter.addAllData(datalist,false);
                }
            }
        }else {
            adapter = new IncomeDetialedItemRecyclerAdapter(datalist, null == datalist || datalist.size() == 0);
            recyclerView.setAdapter(adapter);
        }

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
