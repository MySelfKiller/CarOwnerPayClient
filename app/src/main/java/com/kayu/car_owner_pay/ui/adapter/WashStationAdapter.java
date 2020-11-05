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
import com.kayu.car_owner_pay.model.WashStationBean;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;

import java.text.DecimalFormat;
import java.util.List;

public class WashStationAdapter extends RecyclerView.Adapter<WashStationAdapter.loanHolder> {

    private Context context;
    private List<WashStationBean> dataList;
    private ItemCallback itemCallback;
    private String selectedVal;

    public void addAllData(List<WashStationBean> dataList, boolean isRemoveAllData) {
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


    public WashStationAdapter(Context context, List<WashStationBean> data, String  selectedVal, ItemCallback itemCallback){
        this.selectedVal = selectedVal;
        this.context = context;
        dataList = data;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    @NonNull
    @Override
    public loanHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_wash,viewGroup,false);
        loanHolder holder = new loanHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final loanHolder loanHolder, final int i) {
        WashStationBean washStationBean = dataList.get(i);
        KWApplication.getInstance().loadImg(washStationBean.doorPhotoUrl,loanHolder.img);
        loanHolder.name.setText(washStationBean.shopName);
        loanHolder.location.setText(washStationBean.address);
        DecimalFormat df = new DecimalFormat("0.0");//格式化小数
        String num = df.format((float)Integer.parseInt(washStationBean.distance)/1000.0);
        loanHolder.distance.setText(num+"km");
        for (WashStationBean.ServiceListDTO item: washStationBean.serviceList) {
            if (item.serviceCode.equals(selectedVal)) {
                loanHolder.oil_price.setText("￥"+item.finalPrice);
                loanHolder.oil_price_sub.setText("￥"+item.price);
            }
        }
        StringBuffer sb = new StringBuffer();
        if (washStationBean.isOpen.equals("1")) {
            sb.append("营业中 | ");
        }else {
            sb.append("休息中 | ");

        }
        sb.append(washStationBean.openTimeStart).append("-").append(washStationBean.openTimeEnd);
        loanHolder.time.setText(sb.toString());

        loanHolder.mView.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                itemCallback.onItemCallback(i,washStationBean);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        loanHolder.navi.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                KWApplication.getInstance().toNavi(context,washStationBean.latitude,washStationBean.longitude,washStationBean.address);
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
        private final TextView name, location,distance,oil_price,oil_price_sub,navi,time;
        private View mView;
        private final ImageView img;

        public loanHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            img = itemView.findViewById(R.id.item_wash_img);
            name = itemView.findViewById(R.id.item_wash_name);
            location = itemView.findViewById(R.id.item_wash_location);
            distance = itemView.findViewById(R.id.item_wash_distance);
            time = itemView.findViewById(R.id.item_wash_time);
            oil_price = itemView.findViewById(R.id.item_wash_oil_price);
            oil_price_sub = itemView.findViewById(R.id.item_wash_oil_price_sub);
            navi = itemView.findViewById(R.id.item_wash_navi);
//            pay_oil = itemView.findViewById(R.id.item_station_pay_oil);
        }
    }
}
