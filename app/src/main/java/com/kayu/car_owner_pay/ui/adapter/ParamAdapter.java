package com.kayu.car_owner_pay.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.DistancesParam;
import com.kayu.car_owner_pay.model.OilsParam;
import com.kayu.car_owner_pay.model.SortsParam;
import com.kayu.car_owner_pay.model.WashParam;
import com.kayu.utils.ItemCallback;

import java.util.List;

public class ParamAdapter extends RecyclerView.Adapter<ParamAdapter.NoticeHolder> {
    private Context context;
    private List<Object> dataList;
    private ItemCallback callback;
    private ItemCallback parentCallback;
    private int flag;
    private int parentIndex;

    public void addAllData(List<Object> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public ParamAdapter(int parentIndex, Context context, List<Object> dataList, ItemCallback callback, int flag, ItemCallback parentCallback) {
        this.context =context;
        this.dataList = dataList;
        this.callback = callback;
        this.flag = flag;
        this.parentCallback = parentCallback;
        this.parentIndex = parentIndex;
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_term_lay,null);
        return new NoticeHolder(view);
    }

    public NoticeHolder selectedView;

    @Override
    public void onBindViewHolder(@NonNull final NoticeHolder viewHolder, final int i) {
        if (flag == 1 ){
            DistancesParam sortsParam = (DistancesParam) dataList.get(i);
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.setSelected(true);
                viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.red_bg_def,null));
                viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
                selectedView = viewHolder;
                callback.onItemCallback(i,null);
            }
            viewHolder.nameText.setText(sortsParam.name);
        } else if (flag == 2) {
            OilsParam sortsParam = (OilsParam) dataList.get(i);
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.setSelected(true);
                viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.red_bg_def,null));
                viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
                selectedView = viewHolder;
                callback.onItemCallback(i,null);
            }
            viewHolder.nameText.setText(sortsParam.oilName);
        } else if (flag == 3) {
            SortsParam sortsParam = (SortsParam) dataList.get(i);
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.setSelected(true);
                viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.red_bg_def,null));
                viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
                selectedView = viewHolder;
                callback.onItemCallback(i,null);
            }
            viewHolder.nameText.setText(sortsParam.name);
        } else if (flag == 4 || flag == 5 ) {
            WashParam sortsParam = (WashParam) dataList.get(i);
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.setSelected(true);
                viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.red_bg_def,null));
                viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
                selectedView = viewHolder;
                callback.onItemCallback(i,null);
            }
            viewHolder.nameText.setText(sortsParam.name);
        }

        viewHolder.nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.nameText.isSelected()){
                    if (null != selectedView) {
                        selectedView.nameText.setSelected(false);
                        selectedView.nameText.setTextColor(context.getResources().getColor(R.color.black, null));
                        selectedView.nameText.setTypeface(Typeface.DEFAULT);
                    } else {
                        parentCallback.onItemCallback(parentIndex,null);
                    }

                    viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.red_bg_def,null));
                    viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
                    viewHolder.nameText.setSelected(true);
                    selectedView = viewHolder;
//                    if (flag == 1) {
//                        DistancesParam sortsParam = (DistancesParam) dataList.get(i);
//                    } else if (flag == 2) {
//                        OilsParam sortsParam = (OilsParam) dataList.get(i);
//                    } else if (flag ==3) {
//                        SortsParam sortsParam = (SortsParam) dataList.get(i);
//                        sortsParam.isDefault
//                    }
                    callback.onItemCallback(i,dataList.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList==null?0:dataList.size();
    }

    public class NoticeHolder extends RecyclerView.ViewHolder{
        private View mView;
        public TextView nameText;

        private NoticeHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            nameText = itemView.findViewById(R.id.item_type_tv);
        }
    }
}
