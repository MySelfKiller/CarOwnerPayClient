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

public class OilStationAdapter extends RecyclerView.Adapter<OilStationAdapter.loanHolder> {

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


    public OilStationAdapter(Context context, List<OilStationBean> data, ItemCallback itemCallback){
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
        OilStationBean oilStationBean = dataList.get(i);
        KWApplication.getInstance().loadImg(oilStationBean.gasLogoSmall,loanHolder.img);
        loanHolder.name.setText(oilStationBean.gasName);
        loanHolder.location.setText(oilStationBean.gasAddress);
        loanHolder.distance.setText(oilStationBean.distance+"km");
        loanHolder.oil_price.setText("￥"+oilStationBean.priceYfq);
        loanHolder.oil_price_sub.setText( "降"+DoubleUtils.sub(oilStationBean.priceOfficial,oilStationBean.priceYfq)+"元");
        loanHolder.mView.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                itemCallback.onItemCallback(i,oilStationBean);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        loanHolder.navi.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                KWApplication.getInstance().toNavi(context,String.valueOf(oilStationBean.gasAddressLatitude),String.valueOf(oilStationBean.gasAddressLongitude),oilStationBean.gasAddress,"GCJ02");
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
