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
import java.util.ArrayList;
import java.util.List;

public class WashStationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_LOADING = -1;
    boolean isShowEmptyPage = false;
    boolean isLoadingPage = false;
    private Context context;
    private List<WashStationBean> dataList;
    private ItemCallback itemCallback;
    private String selectedVal;

    public void addAllData(List<WashStationBean> dataList, String  selectedVal,boolean isRemoveAllData) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear();
        }
        this.selectedVal = selectedVal;
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


    public WashStationAdapter(Context context, List<WashStationBean> data, boolean isShowEmptyPage,boolean isLoadingPage, ItemCallback itemCallback){
        this.context = context;
        this.isShowEmptyPage = isShowEmptyPage;
        this.isLoadingPage = isLoadingPage;
        dataList = data;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_tab, viewGroup, false);
            return new RecyclerView.ViewHolder(emptyView) {};
        }else if (viewType == VIEW_TYPE_LOADING){
            View emptyView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_tab_home, viewGroup, false);
            return new RecyclerView.ViewHolder(emptyView) {};
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_wash,viewGroup,false);
            return new LoanHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder loanHolder, final int i) {
        if (loanHolder instanceof LoanHolder) {
            LoanHolder vh = (LoanHolder) loanHolder;
            WashStationBean washStationBean = dataList.get(i);
            KWApplication.getInstance().loadImg(washStationBean.doorPhotoUrl,vh.img);
            vh.name.setText(washStationBean.shopName);
            vh.location.setText(washStationBean.address);
            DecimalFormat df = new DecimalFormat("0.0");//格式化小数
            String num = df.format((float)Integer.parseInt(washStationBean.distance)/1000.0);
            vh.distance.setText(num+"km");
            for (WashStationBean.ServiceListDTO item: washStationBean.serviceList) {
                if (item.serviceCode.equals(selectedVal)) {
                    vh.oil_price.setText("￥"+item.finalPrice);
                    vh.oil_price_sub.setText("￥"+item.price);
                }
            }
            StringBuffer sb = new StringBuffer();
            if (washStationBean.isOpen.equals("1")) {
                sb.append("营业中 | ");
            }else {
                sb.append("休息中 | ");

            }
            sb.append(washStationBean.openTimeStart).append("-").append(washStationBean.openTimeEnd);
            vh.time.setText(sb.toString());

            vh.mView.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    itemCallback.onItemCallback(i,washStationBean);
                }

                @Override
                protected void OnMoreErrorClick() {

                }
            });
            vh.navi.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    int userRole = KWApplication.getInstance().userRole;
                    int isPublic = KWApplication.getInstance().isWashPublic;
                    if (userRole == -2 && isPublic == 0){
                        KWApplication.getInstance().showRegDialog(context);
                        return;
                    }
                    KWApplication.getInstance().toNavi(context,washStationBean.latitude,washStationBean.longitude,washStationBean.address,"BD09");
                }

                @Override
                protected void OnMoreErrorClick() {

                }
            });
        }else {
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
        private final TextView name, location,distance,oil_price,oil_price_sub,navi,time;
        private View mView;
        private final ImageView img;

        public LoanHolder(@NonNull View itemView) {
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
