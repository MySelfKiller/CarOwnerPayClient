package com.kayu.car_owner_pay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.OilStationBean;
import com.kayu.utils.DoubleUtils;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;

import java.util.List;

public class WashAdapter extends RecyclerView.Adapter<WashAdapter.loanHolder> {

    private Context context;
    private List<OilStationBean> dataList;
    private ItemCallback itemCallback;
//    private String flag;

    public void addAllData(List<OilStationBean> dataList, boolean isRemoveAllData) {
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


    public WashAdapter(Context context, List<OilStationBean> data, ItemCallback itemCallback){
        this.context = context;
        dataList = data;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    @NonNull
    @Override
    public loanHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_station,viewGroup,false);
        loanHolder holder = new loanHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final loanHolder loanHolder, final int i) {
        KWApplication.getInstance().loadImg(dataList.get(i).gasLogoSmall,loanHolder.img);
        loanHolder.name.setText(dataList.get(i).gasName);
        loanHolder.location.setText(dataList.get(i).gasAddress);
        loanHolder.distance.setText(dataList.get(i).distance+"km");
        loanHolder.oil_price.setText("￥"+dataList.get(i).priceYfq);
        loanHolder.oil_price_sub.setText( "降"+String.valueOf(DoubleUtils.sub(dataList.get(i).priceOfficial,dataList.get(i).priceYfq))+"元");
        loanHolder.mView.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                itemCallback.onItemCallback(i,dataList.get(i));
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
        private final TextView name, location,distance,oil_price,oil_price_sub,navi;
        private View mView;
        private final ImageView img;

        public loanHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            img = itemView.findViewById(R.id.item_station_img);
            name = itemView.findViewById(R.id.item_station_name);
            location = itemView.findViewById(R.id.item_station_location);
            distance = itemView.findViewById(R.id.item_station_distance);
            oil_price = itemView.findViewById(R.id.item_station_oil_price);
            oil_price_sub = itemView.findViewById(R.id.item_station_oil_price_sub);
            navi = itemView.findViewById(R.id.item_station_navi);
//            pay_oil = itemView.findViewById(R.id.item_station_pay_oil);
        }
    }
}
