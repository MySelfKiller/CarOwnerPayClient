package com.kayu.car_owner_pay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.RefundInfo;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;

import java.util.List;

public class RefundModeAdapter extends RecyclerView.Adapter<RefundModeAdapter.loanHolder> {

    private Context context;
    private List<RefundInfo.RefundWayResultsDTO> dataList;
    private ItemCallback itemCallback;

    public void addAllData(List<RefundInfo.RefundWayResultsDTO> dataList, boolean isRemoveAllData) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }
    public void removeAllData(){
        if (null != this.dataList){
            this.dataList.clear();
        }
        notifyDataSetChanged();
    }


    public RefundModeAdapter(Context context, List<RefundInfo.RefundWayResultsDTO> data, ItemCallback itemCallback){
        this.context = context;
        dataList = data;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    private ImageView selectedItem;

    @NonNull
    @Override
    public loanHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_refund_way,viewGroup,false);
        loanHolder holder = new loanHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final loanHolder loanHolder, final int i) {
        RefundInfo.RefundWayResultsDTO washStationBean = dataList.get(i);
        loanHolder.mode_tv.setText(washStationBean.content);
        if (i == 0) {
            loanHolder.mode_check.setSelected(true);
            itemCallback.onItemCallback(i,washStationBean);
            selectedItem = loanHolder.mode_check;
        } else {
            loanHolder.mode_check.setSelected(false);
        }
        loanHolder.mView.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (!loanHolder.mode_check.isSelected()) {
                    loanHolder.mode_check.setSelected(true);
                    if (null != selectedItem)
                        selectedItem.setSelected(false);
                    itemCallback.onItemCallback(i,washStationBean);
                    selectedItem = loanHolder.mode_check;
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList==null?0:dataList.size();
    }


    class loanHolder extends RecyclerView.ViewHolder{
        private View mView;
        private final TextView mode_tv;
        private final ImageView mode_check;

        public loanHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mode_check = itemView.findViewById(R.id.item_refund_mode_check);
            mode_tv = itemView.findViewById(R.id.item_refund_mode_tv);
        }
    }
}
