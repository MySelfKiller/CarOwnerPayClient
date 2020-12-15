package com.kayu.car_owner_pay.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.model.RefundInfo;
import com.kayu.car_owner_pay.ui.adapter.RefundModeAdapter;
import com.kayu.car_owner_pay.ui.adapter.RefundReasonAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipGifDialog;

public class WashRefundFragment extends BaseActivity {
    private Long orderId;
    private TextView refund_price;
    private RecyclerView mode_rv;
    private RecyclerView reason_rv;
    private TextView apply_btn;
    private MainViewModel mainViewModel;

//    public WashRefundFragment(Long orderId) {
//        this.orderId = orderId;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_refund);
        orderId = getIntent().getLongExtra("orderId",0);
        mainViewModel = new ViewModelProvider(WashRefundFragment.this).get(MainViewModel.class);
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
//        TextView back_tv = findViewById(R.id.title_back_tv);
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("申请退款");
//        title_name.setVisibility(View.GONE);
//        back_tv.setText("我的");


        refund_price = findViewById(R.id.wash_refund_price);
        mode_rv = findViewById(R.id.wash_refund_way_rv);
        reason_rv = findViewById(R.id.wash_refund_reason_rv);
        apply_btn = findViewById(R.id.wash_unused_apply_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WashRefundFragment.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(WashRefundFragment.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mode_rv.setLayoutManager(linearLayoutManager);
        reason_rv.setLayoutManager(linearLayoutManager1);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(WashRefundFragment.this,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(ContextCompat.getColor(WashRefundFragment.this,R.color.divider2)));
        mode_rv.addItemDecoration(dividerItemDecoration);
        reason_rv.addItemDecoration(dividerItemDecoration);

        mainViewModel.getRefundInfo(WashRefundFragment.this,orderId).observe(WashRefundFragment.this, new Observer<RefundInfo>() {
            @Override
            public void onChanged(RefundInfo refundInfo) {
                if (null != refundInfo) {
                    initViewData(refundInfo);
                }
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
//        return inflater.inflate(R.layout.fragment_wash_refund, container, false);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//
//    }

    private RefundInfo.RefundWayResultsDTO selectedMode;
    private String selectedReason;
    private void initViewData(RefundInfo refundInfo) {
        refund_price.setText("￥"+refundInfo.amount);

        mode_rv.setAdapter(new RefundModeAdapter(WashRefundFragment.this, refundInfo.refundWayResults, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                selectedMode = (RefundInfo.RefundWayResultsDTO)obj;
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        }));
        reason_rv.setAdapter(new RefundReasonAdapter(WashRefundFragment.this, refundInfo.reasons, new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                selectedReason = (String)obj;
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        }));

        apply_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (null == selectedMode || StringUtil.isEmpty(selectedReason)) {
                    TipGifDialog.show(WashRefundFragment.this,"请选择退款原因", TipGifDialog.TYPE.WARNING);
                    return;
                }

                MessageDialog.show(WashRefundFragment.this,"确认申请退款？","若门店信息有变更，重新下单可能不再享受优惠","确定","取消")
                        .setOkButton(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        TipGifDialog.show(WashRefundFragment.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
                        mainViewModel.sendRefund(WashRefundFragment.this,orderId,selectedMode.way,selectedReason, new ItemCallback() {
                            @Override
                            public void onItemCallback(int position, Object obj) {
                                ResponseInfo resInfo = (ResponseInfo)obj;
                                if (resInfo.status == 1) {
                                    TipGifDialog.show(WashRefundFragment.this,"申请成功，返回上一页", TipGifDialog.TYPE.SUCCESS);
                                    finish();

                                }else {
                                    TipGifDialog.show(WashRefundFragment.this,resInfo.msg, TipGifDialog.TYPE.ERROR);
                                }
                            }

                            @Override
                            public void onDetailCallBack(int position, Object obj) {

                            }
                        });
                        return false;
                    }
                }).setCancelButton(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        baseDialog.doDismiss();
                        return false;
                    }
                }).setCancelable(false);

            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
    }
}