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

import java.util.ArrayList;
import java.util.List;

public class OilStationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_LOADING = -1;
    boolean isShowEmptyPage = false;
    boolean isLoadingPage = false;
    private Context context;
    private List<OilStationBean> dataList;
    private ItemCallback itemCallback;
//    private String flag;

    public void addAllData( List<OilStationBean> dataList, boolean isRemoveAllData) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear();
        }
        if (null  ==  this.dataList)
            this.dataList = new ArrayList<>();
        if (null != dataList) {
            this.dataList.addAll(dataList);
        }
        isLoadingPage = false;
        notifyDataSetChanged();
    }
    public void removeAllData(boolean isLoadingPage){
        if (null != this.dataList){
            this.dataList.clear();
        }
        this.isLoadingPage = isLoadingPage;
        isShowEmptyPage = true;
        notifyDataSetChanged();
    }


    public OilStationAdapter(Context context, List<OilStationBean> data,boolean isShowEmptyPage, boolean isLoadingPage, ItemCallback itemCallback){
        this.context = context;
        dataList = data;
        this.isShowEmptyPage = isShowEmptyPage;
        this.isLoadingPage = isLoadingPage;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_tab, viewGroup, false);
            return new RecyclerView.ViewHolder(emptyView) {};
        }else if (viewType == VIEW_TYPE_LOADING){
            View emptyView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_tab_home, viewGroup, false);
            return new RecyclerView.ViewHolder(emptyView) {};
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_station,viewGroup,false);
            return new LoanHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder loanHolder, final int i) {
        if (loanHolder instanceof LoanHolder) {
            LoanHolder vh = (LoanHolder) loanHolder;
            OilStationBean oilStationBean = dataList.get(i);
            KWApplication.getInstance().loadImg(oilStationBean.gasLogoSmall, vh.img);
            vh.name.setText(oilStationBean.gasName);
            vh.location.setText(oilStationBean.gasAddress);
            vh.distance.setText(oilStationBean.distance + "km");
            vh.oil_price.setText("￥" + oilStationBean.priceYfq);
            vh.oil_price_full.setText("￥" + oilStationBean.priceGun);
            vh.oil_rebate.setText(oilStationBean.gunDiscount + "折");
            vh.oil_rebate.setVisibility(View.VISIBLE);
            vh.oil_price_sub.setText("降" + DoubleUtils.sub(oilStationBean.priceGun, oilStationBean.priceYfq) + "元");
            vh.mView.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    itemCallback.onItemCallback(i, oilStationBean);
                }

                @Override
                protected void OnMoreErrorClick() {

                }
            });
            vh.navi.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    KWApplication.getInstance().toNavi(context, String.valueOf(oilStationBean.gasAddressLatitude), String.valueOf(oilStationBean.gasAddressLongitude), oilStationBean.gasAddress, "GCJ02");
                }

                @Override
                protected void OnMoreErrorClick() {

                }
            });
        } else {
            if (!isShowEmptyPage) {
                loanHolder.itemView.setVisibility(View.GONE);
            } else {
                loanHolder.itemView.setVisibility(View.VISIBLE);
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
            if (isLoadingPage)
                return VIEW_TYPE_LOADING;
            else
                return VIEW_TYPE_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return VIEW_TYPE_ITEM;
    }

    class LoanHolder extends RecyclerView.ViewHolder{
        private final TextView name, location,distance,oil_price,oil_price_sub,navi,oil_price_full,oil_rebate;
        private View mView;
        private final ImageView img;

        public LoanHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            img = itemView.findViewById(R.id.item_station_img);
            name = itemView.findViewById(R.id.item_station_name);
            location = itemView.findViewById(R.id.item_station_location);
            distance = itemView.findViewById(R.id.item_station_distance);
            oil_price_full = itemView.findViewById(R.id.item_station_oil_price_full);
            oil_price = itemView.findViewById(R.id.item_station_oil_price);
            oil_price_sub = itemView.findViewById(R.id.item_station_oil_price_sub);
            oil_rebate = itemView.findViewById(R.id.item_station_oil_rebate);
            navi = itemView.findViewById(R.id.item_station_navi);
//            pay_oil = itemView.findViewById(R.id.item_station_pay_oil);
        }
    }
}
