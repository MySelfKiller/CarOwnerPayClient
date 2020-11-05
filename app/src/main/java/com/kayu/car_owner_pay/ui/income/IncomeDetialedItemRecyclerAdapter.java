package com.kayu.car_owner_pay.ui.income;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;

import java.util.List;

public class IncomeDetialedItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private List<IncomeDetailedData> dataList;
    boolean isShowEmptyPage = false;

    public IncomeDetialedItemRecyclerAdapter(List<IncomeDetailedData> dataList,boolean isShowEmptyPage) {
        this.dataList = dataList;
        this.isShowEmptyPage = isShowEmptyPage;
    }

    public void addAllData(List<IncomeDetailedData> dataList, boolean isRemoveAllData) {
        if (isRemoveAllData && null != this.dataList){
            this.dataList.clear();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void removeAllData(){
        if (null != this.dataList){
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

            return new RecyclerView.ViewHolder(emptyView) {};

        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detailed_lay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            ViewHolder vh = (ViewHolder) holder;
            vh.income_name.setText(dataList.get(position).explain);
            vh.income_time.setText(dataList.get(position).createTime);
            vh.income_amout.setText(dataList.get(position).amount+"元");
        }else {
            if (!isShowEmptyPage){
                holder.itemView.setVisibility(View.GONE);
            }else {
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
        public final TextView income_name,income_time,income_amout;
//        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            income_name = (TextView) view.findViewById(R.id.item_income_name);
            income_time = (TextView) view.findViewById(R.id.item_income_time);
            income_amout = (TextView) view.findViewById(R.id.item_income_amout);
        }
    }
}
