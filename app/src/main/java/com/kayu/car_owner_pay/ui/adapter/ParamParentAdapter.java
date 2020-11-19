package com.kayu.car_owner_pay.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.ParamParent;
import com.kayu.utils.ItemCallback;

import java.util.ArrayList;
import java.util.List;

public class ParamParentAdapter extends RecyclerView.Adapter<ParamParentAdapter.loanHolder> {

    private Context context;
    private List<ParamParent> dataList;
    private List<ParamAdapter> childAdapterList = new ArrayList<>();
    private ItemCallback itemCallback;
    private int flag;
    ItemCallback parentCallback;
//    private ParamAdapter paramAdapter;
//    private String flag;

    public void addAllData(List<ParamParent> dataList, boolean isRemoveAllData) {
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


    public ParamParentAdapter(Context context, List<ParamParent> data, ItemCallback itemCallback , int flag){
        this.flag = flag;
        this.context = context;
        dataList = data;
        this.itemCallback = itemCallback;
//        this.flag = flag;
        parentCallback = new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                for (int x = 0; x < childAdapterList.size(); x++) {
                    if (x != position){
                        if (null != childAdapterList.get(x).selectedView) {
                            childAdapterList.get(x).selectedView.nameText.setSelected(false);
                            childAdapterList.get(x).selectedView.nameText.setTextColor(context.getResources().getColor(R.color.black));
                            childAdapterList.get(x).selectedView.nameText.setTypeface(Typeface.DEFAULT);
                            childAdapterList.get(x).selectedView = null;
                        }
                    }
                }
                for ( ParamParent param: dataList) {

                }

            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        };

    }

    @NonNull
    @Override
    public loanHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_param_lay,viewGroup,false);
        loanHolder holder = new loanHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final loanHolder loanHolder, final int i) {
        ParamParent item = dataList.get(i);

        if (item.type <= 0) {
            loanHolder.param_name.setVisibility(View.GONE);
        }else {
            loanHolder.param_name.setVisibility(View.VISIBLE);
        }
        if (flag == 3) {
            loanHolder.param_rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        } else if (flag == 4 || flag == 5) {
            loanHolder.param_rv.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        } else {
            loanHolder.param_rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        }
        loanHolder.param_rv.setItemAnimator(null);

        ParamAdapter paramAdapter = new ParamAdapter(i,context, item.objList, itemCallback, flag, parentCallback);
        childAdapterList.add(paramAdapter);
        loanHolder.param_rv.setAdapter(paramAdapter);
        switch (item.type) {
            case 1:
                loanHolder.param_name.setVisibility(View.VISIBLE);
                loanHolder.param_name.setText("汽油");
                break;
            case 2:
                loanHolder.param_name.setVisibility(View.VISIBLE);
                loanHolder.param_name.setText("柴油");
                break;
            case 3:
                loanHolder.param_name.setVisibility(View.VISIBLE);
                loanHolder.param_name.setText("天然气");
                break;
            default:
                loanHolder.param_name.setVisibility(View.GONE);
        }
//        loanHolder.mView.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                itemCallback.onItemCallback(i,null);
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return dataList==null?0:dataList.size();
    }


    class loanHolder extends RecyclerView.ViewHolder{
        private final TextView param_name;
        private View mView;
        private final RecyclerView param_rv;

        public loanHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            param_name = itemView.findViewById(R.id.item_param_name);
            param_rv = itemView.findViewById(R.id.item_param_rv);

//            pay_oil = itemView.findViewById(R.id.item_station_pay_oil);
        }
    }
}
