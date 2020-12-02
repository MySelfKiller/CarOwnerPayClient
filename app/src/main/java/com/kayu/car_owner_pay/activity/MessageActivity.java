package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.data_parser.MessageListParser;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.model.ItemMessageBean;
import com.kayu.car_owner_pay.ui.adapter.ItemMessageAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends BaseActivity {
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ItemMessageAdapter adapter;
    private ArrayList<ItemMessageBean> messageDataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
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
        title_name.setText("消息通知");
//        title_name.setVisibility(View.GONE);
        back_tv.setText("我的");
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
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
        recyclerView = (RecyclerView) findViewById(R.id.custom_list_recycler);
        Context context = MessageActivity.this;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        refreshLayout.autoRefresh();
//        isCreated = true;
    }

//    private boolean mHasLoadedOnce = false;// 页面已经加载过
//    private boolean isCreated = false;
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
//            refreshLayout.autoRefresh();
//        }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        isCreated = false;
    }

    @SuppressLint("HandlerLeak")
    private void reqData() {
        RequestInfo reques = new RequestInfo();
        reques.context = MessageActivity.this;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_MESSAGE_LIST;
        reques.parser = new MessageListParser();
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("pageNow", pageIndex);
        reqDateMap.put("pageSize", 20);
        reques.reqDataMap = reqDateMap;
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;

                if (resInfo.status == 1) {
                    messageDataList = (ArrayList<ItemMessageBean>) resInfo.responseData;
                    initViewData();
                }else {
                    Toast.makeText(MessageActivity.this,resInfo.msg,Toast.LENGTH_SHORT).show();
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
                if (null != messageDataList && messageDataList.size()>0){
                    adapter.addAllData(messageDataList,false);
                }
            }
        } else {
            adapter = new ItemMessageAdapter(MessageActivity.this, messageDataList,  (null == messageDataList || messageDataList.size() == 0), new ItemCallback() {
                @Override
                public void onItemCallback(int position, Object obj) {
                    Intent intent = new Intent(MessageActivity.this, WebViewActivity.class);
                    intent.putExtra("url",(String)obj);
//                    intent.putExtra("from",title);
                    startActivity(intent);
                }

                @Override
                public void onDetailCallBack(int position, Object obj) {

                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

}