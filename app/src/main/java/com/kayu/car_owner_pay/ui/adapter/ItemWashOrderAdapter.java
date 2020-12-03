package com.kayu.car_owner_pay.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.ItemWashOrderBean;
import com.kayu.car_owner_pay.activity.WashStationActivity;
import com.kayu.car_owner_pay.activity.WashUnusedActivity;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;

import java.util.List;

public class ItemWashOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    boolean isShowEmptyPage;
    private List<ItemWashOrderBean> dataList;
    private ItemCallback itemCallback;
    private FragmentActivity context;


    public ItemWashOrderAdapter(FragmentActivity context, List<ItemWashOrderBean> dataList, boolean isShowEmptyPage, ItemCallback itemCallback) {
        this.dataList = dataList;
        this.isShowEmptyPage = isShowEmptyPage;
        this.itemCallback = itemCallback;
        this.context = context;

    }

    public void addAllData(List<ItemWashOrderBean> dataList, boolean isRemoveAllData) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        if (null != this.dataList) {
            this.dataList.clear();
        }
        isShowEmptyPage = false;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view_tab, parent, false);

            return new RecyclerView.ViewHolder(emptyView) {
            };

        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wash_order_list, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            ItemWashOrderBean oilOrderData = dataList.get(position);
            KWApplication.getInstance().loadImg(oilOrderData.doorPhotoUrl,vh.img_bg);
            vh.order_name.setText(oilOrderData.shopName);
            String orderState;
            switch (oilOrderData.state) {//0:待支付 1:已支付待使用 2:已取消 3:已使用 4:退款中 5:已退款 6:支付失败、7:退款失败
                case 0:
                    orderState = "待支付";
                    break;
                case 1:
                    orderState = "待使用";
                    break;
                case 2:
                    orderState = "已取消";
                    break;
                case 3:
                    orderState = "已使用";
                    break;
                case 4:
                    orderState = "退款中";
                    break;
                case 5:
                    orderState = "已退款";
                    break;
                case 6:
                    orderState = "支付失败";
                    break;
                case 7:
                    orderState = "退款失败";
                    break;
                default:
                    orderState = "暂无";

            }
            vh.order_state.setText(orderState);
            vh.order_price.setText(String.valueOf(oilOrderData.realAmount));
            vh.wash_type.setText(oilOrderData.serviceName.split("-")[0]);
            if (oilOrderData.state == 4 || oilOrderData.state == 6 || oilOrderData.state == 7) {
                vh.open_time.setVisibility(View.GONE);
            } else {
                vh.open_time.setText("营业时间："+oilOrderData.busTime);
                vh.open_time.setVisibility(View.VISIBLE);
            }
            if (null != oilOrderData.surplusDay) {
                String str7 = "请在<font color=\"#ca4747\">" + oilOrderData.surplusDay + "</font>天内使用";
                vh.vali_time.setText(Html.fromHtml(str7));
                vh.vali_time.setVisibility(View.VISIBLE);
            } else {
                vh.vali_time.setVisibility(View.GONE);
            }
//            vh.vali_time.setText("请在"+oilOrderData.surplusDay+"天内使用");

            vh.store_address.setText(oilOrderData.address);
            if (oilOrderData.state == 4 || oilOrderData.state == 6 || oilOrderData.state == 7) {
                vh.pay_lay.setVisibility(View.GONE);
                vh.location_lay.setVisibility(View.GONE);
            } else if (oilOrderData.state == 1) {

                vh.location_lay.setVisibility(View.VISIBLE);
                vh.pay_lay.setVisibility(View.GONE);
                vh.navi_lay.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        itemCallback.onItemCallback(position, oilOrderData);
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });
                vh.phone_lay.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        itemCallback.onDetailCallBack(position, oilOrderData);
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });

            } else {
                vh.location_lay.setVisibility(View.GONE);
                vh.pay_lay.setVisibility(View.VISIBLE);
                vh.pay_btn.setText("再次购买");
                vh.pay_btn.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
//                        FragmentManager fg = context.getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                        fragmentTransaction.add(R.id.main_root_lay, new WashStationActivity(oilOrderData.shopCode));
//                        fragmentTransaction.addToBackStack("ddd");
//                        fragmentTransaction.commit();
                        Intent intent = new Intent(context, WashStationActivity.class);
                        intent.putExtra("shopCode",oilOrderData.shopCode);
                        context.startActivity(intent);
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });
            }
            vh.mView.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    // 2020/10/21 是否还需要细分订单状态再跳转不同页面
//                    Fragment jumpFragment;
//                    if (oilOrderData.state == 1) {
//                    } else {
//                    }
//                    if (oilOrderData.state == 0 || oilOrderData.state == 2 || oilOrderData.state == 6) {
//                        jumpFragment = new WashStationFragment(oilOrderData.shopCode);
//                    } else {
//                        jumpFragment = new WashUnusedActivity(oilOrderData.id, oilOrderData.state);
//                    }
//                    FragmentManager fg = context.getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    fragmentTransaction.add(R.id.main_root_lay,jumpFragment );
//                    fragmentTransaction.addToBackStack("ddd");
//                    fragmentTransaction.commit();
                    Intent intent = new Intent(context, WashUnusedActivity.class);
                    intent.putExtra("orderId", oilOrderData.id);
                    intent.putExtra("orderState",oilOrderData.state);
                    context.startActivity(intent);
                }

                @Override
                protected void OnMoreErrorClick() {

                }
            });

        } else {
            if (!isShowEmptyPage) {
                holder.itemView.setVisibility(View.GONE);
            } else {
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
        public final View mView;
        private final TextView order_name,wash_type,order_state,
                open_time,vali_time, store_address,order_price,pay_btn;
        private final ImageView img_bg;
        private final LinearLayout navi_lay,phone_lay;
        private final ConstraintLayout location_lay,pay_lay;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            img_bg = itemView.findViewById(R.id.item_wash_order_img_bg);
            order_name = itemView.findViewById(R.id.item_wash_order_name);
            order_state = itemView.findViewById(R.id.item_wash_order_state);
            order_price = itemView.findViewById(R.id.item_wash_order_price);
            wash_type = itemView.findViewById(R.id.item_wash_order_type);
            open_time = itemView.findViewById(R.id.item_wash_order_time);
            vali_time = itemView.findViewById(R.id.item_wash_order_vali_time);
            store_address = itemView.findViewById(R.id.item_wash_order_location);
            location_lay = itemView.findViewById(R.id.item_wash_order_location_lay);
            pay_lay = itemView.findViewById(R.id.item_wash_order_pay_lay);
            navi_lay = itemView.findViewById(R.id.item_wash_order_navi_lay);
            phone_lay = itemView.findViewById(R.id.item_wash_order_phone_lay);
            pay_btn = itemView.findViewById(R.id.item_wash_order_pay_btn);


        }

    }
}
