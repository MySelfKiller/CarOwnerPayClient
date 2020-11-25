package com.kayu.car_owner_pay.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.data_parser.OilOrderListDataParser;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.model.ItemOilOrderBean;
import com.kayu.car_owner_pay.ui.adapter.ItemOrderViewAdapter;
import com.kayu.utils.ImageUtil;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.QRCodeUtil;
import com.kayu.utils.StringUtil;
import com.kongzue.dialog.v3.CustomDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OilOrderAllFragment extends Fragment {
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    private RefreshLayout refreshLayout;
    private ArrayList<ItemOilOrderBean> orderData;
    private RecyclerView recyclerView;
    private ItemOrderViewAdapter adapter;
    private int orderStatus;

    public OilOrderAllFragment(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_all, container, false);
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

    @SuppressLint("HandlerLeak")
    private void reqData(int orderStatus) {
        RequestInfo reques = new RequestInfo();
        reques.context = getContext();
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_ORDER_LIST;
        reques.parser = new OilOrderListDataParser();
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("pageIndex", pageIndex);
        if (orderStatus > 0) {
            reqDateMap.put("orderStatus", orderStatus);
        }
        reqDateMap.put("pageSize", 20);
        reques.reqDataMap = reqDateMap;
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                if (null == OilOrderAllFragment.this.getActivity()){
                    LogUtil.e("hm","OilOrderAllFragment 以销毁");
                    return;
                }
                if (resInfo.status == 1) {
                    orderData = (ArrayList<ItemOilOrderBean>) resInfo.responseData;
                    initViewData();
                }else {
                    Toast.makeText(getContext(),resInfo.msg,Toast.LENGTH_SHORT).show();
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
            adapter = new ItemOrderViewAdapter(getContext(), orderData,  (null == orderData || orderData.size() == 0), new ItemCallback() {
                @Override
                public void onItemCallback(int position, Object obj) {
                    showPop((String)obj);
                }

                @Override
                public void onDetailCallBack(int position, Object obj) {

                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private View popview;
    private ImageView qrcode_iv;
    private Button save_btn;
    private TextView compay_tv1;
    private CustomDialog dialog;

    private void initPopView() {
        final ViewGroup nullParent = null;
        popview = getLayoutInflater().inflate(R.layout.qrcode_lay, nullParent);
        qrcode_iv = popview.findViewById(R.id.shared_qrcode_iv);
        save_btn = popview.findViewById(R.id.shared_call_btn);
        compay_tv1 = popview.findViewById(R.id.shared_compay_tv1);
    }
    private void creatPopWindow(View view) {
        dialog = CustomDialog.build((AppCompatActivity) getContext(), view).setCancelable(true);
    }

    private void showWindo(){
        if (null !=dialog && !dialog.isShow)
            dialog.show();

    }

    Bitmap qrcodeBitmap = null;

    private void showPop(String qrCode) {
        if (StringUtil.isEmpty(qrCode)) {
            return;
        }
        qrcodeBitmap = QRCodeUtil.createQRCodeBitmap(qrCode, 280, 280, "UTF-8",
                null, "0", Color.BLACK, Color.WHITE, null, 0, null);
        qrcode_iv.setImageBitmap(qrcodeBitmap);
        initPopView();
        creatPopWindow(popview);
        showWindo();

        save_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (null == qrcodeBitmap) {
                    Toast.makeText(getContext(), "保存图片不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                String fileName = "qr_" + System.currentTimeMillis() + ".jpg";
                boolean isSaveSuccess = ImageUtil.saveImageToGallery(getActivity(), qrcodeBitmap, fileName);
                if (isSaveSuccess) {
                    Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        compay_tv1.setVisibility(View.GONE);

    }

}
