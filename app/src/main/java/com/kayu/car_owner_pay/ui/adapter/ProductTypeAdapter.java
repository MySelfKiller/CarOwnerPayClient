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
import com.kayu.car_owner_pay.model.OilsParam;
import com.kayu.car_owner_pay.model.OilsTypeParam;
import com.kayu.utils.ItemCallback;

import java.util.List;

public class ProductTypeAdapter extends RecyclerView.Adapter<ProductTypeAdapter.NoticeHolder> {
    private Context context;
    private List<Object> dataList;
    private ItemCallback callback;
    private int flag;

    public void addAllData(List<Object> dataList,boolean isClean) {
        if (isClean) {
            this.dataList.clear();
            if (null != selectedView) {
                selectedView.nameText.setSelected(false);
                selectedView.nameText.setTextColor(context.getResources().getColor(R.color.colorAccent));
                selectedView.nameText.setTypeface(Typeface.DEFAULT);
                selectedView = null;
            }
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public ProductTypeAdapter(Context context, List<Object> dataList, int flag, ItemCallback callback) {
        this.context =context;
        this.dataList = dataList;
        this.callback = callback;
        this.flag = flag;
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_term_lay,null);
        return new NoticeHolder(view);
    }


    NoticeHolder selectedView;
    @Override
    public void onBindViewHolder(@NonNull final NoticeHolder viewHolder, final int i) {
//        Drawable drawable1 = context.getResources().getDrawable(R.mipmap.ic_arror_right);
//        drawable1.setBounds(0, 0, 25, 25);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//        viewHolder.button.setCompoundDrawables(null, null, drawable1, null);//只放左边
        String showValue = "";
        if (flag ==0){
            OilsTypeParam param = (OilsTypeParam) dataList.get(i);
            if (param.oilType == 1) {
                showValue = "汽油";
                viewHolder.nameText.setText("汽油");
            } else if (param.oilType == 2) {
                showValue = "柴油";
                viewHolder.nameText.setText("柴油");
            } else if (param.oilType == 3) {
                showValue = "天然气";
            }
        } else if (flag == 1) {
            OilsParam param = (OilsParam) dataList.get(i);
            showValue = param.oilName;
        } else if (flag == 2 ) {
            showValue = (String) dataList.get(i)+"号";
        }
        if (i == 0 && flag != 2) {
            viewHolder.nameText.setSelected(true);
            viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.colorAccent));
            viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
            selectedView = viewHolder;
        }
        viewHolder.nameText.setText(showValue);

        viewHolder.nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.nameText.isSelected()){
                    if (null != selectedView) {
                        selectedView.nameText.setSelected(false);
                        selectedView.nameText.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        selectedView.nameText.setTypeface(Typeface.DEFAULT);
                    }
                    viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    viewHolder.nameText.setTypeface(Typeface.DEFAULT_BOLD);
                    viewHolder.nameText.setSelected(true);
                    selectedView = viewHolder;
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

        public TextView nameText;

        private NoticeHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_type_tv);
        }
    }
}
