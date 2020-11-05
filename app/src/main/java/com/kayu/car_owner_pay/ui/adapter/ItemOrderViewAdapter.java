package com.kayu.car_owner_pay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.ItemOilOrderBean;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemOrderViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    boolean isShowEmptyPage = false;
    private List<ItemOilOrderBean> dataList = new ArrayList<>();
    private ItemCallback itemCallback;
    private Context context;


    public ItemOrderViewAdapter(Context context, List<ItemOilOrderBean> dataList, boolean isShowEmptyPage, ItemCallback itemCallback) {
        this.dataList = dataList;
        this.isShowEmptyPage = isShowEmptyPage;
        this.itemCallback = itemCallback;
        this.context = context;

    }

    public void addAllData(List<ItemOilOrderBean> dataList, boolean isRemoveAllData) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        if (null != this.dataList) {
            this.dataList.clear();
        }
        isShowEmptyPage = false;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view_tab, parent, false);

            return new RecyclerView.ViewHolder(emptyView) {
            };

        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_oil_order_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            ItemOilOrderBean oilOrderData = dataList.get(position);
            //92#(2号枪)
            vh.order_name.setText(oilOrderData.gasName);
            vh.order_number.setText(oilOrderData.orderId);
            vh.oil_state.setText(oilOrderData.orderStatusName);
            vh.pay_model.setText(oilOrderData.payType);
            vh.pay_time.setText(oilOrderData.orderTime);
            vh.oil_info.setText(oilOrderData.oilNo+"("+oilOrderData.gunNo+"号枪)");
            vh.full_price.setText(oilOrderData.amountGun);
            vh.rebate_price.setText(oilOrderData.amountDiscounts);
            vh.sale_price.setText(oilOrderData.amountPay);
            if (!StringUtil.isEmpty(oilOrderData.qrCode4PetroChina)) {
                vh.qr_btn.setVisibility(View.VISIBLE);
                vh.qr_btn.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        itemCallback.onItemCallback(position,oilOrderData.qrCode4PetroChina);
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });

            } else {
                vh.qr_btn.setVisibility(View.GONE);
            }
        } else {
            if (!isShowEmptyPage) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (null == dataList || dataList.size() == 0) {
            return 1;
        }
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //在这里进行判断，如果我们的集合的长度为0时，我们就使用emptyView的布局
        if (null == dataList || dataList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return VIEW_TYPE_ITEM;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView order_name,order_number,oil_state,pay_model,
                pay_time, oil_info,full_price,rebate_price,sale_price,qr_btn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            qr_btn = itemView.findViewById(R.id.item_wash_qr_btn);
            order_name = itemView.findViewById(R.id.item_order_oil_name);
            order_number = itemView.findViewById(R.id.item_order_oil_number);
            oil_state = itemView.findViewById(R.id.item_order_oil_state);
            pay_model = itemView.findViewById(R.id.item_order_oil_pay_model);
            pay_time = itemView.findViewById(R.id.item_order_oil_pay_time);
            oil_info = itemView.findViewById(R.id.item_order_oil_info);
            full_price = itemView.findViewById(R.id.item_order_oil_full_price);
            rebate_price = itemView.findViewById(R.id.item_order_oil_rebate_price);
            sale_price = itemView.findViewById(R.id.item_order_oil_sale_price);
        }

    }
}
