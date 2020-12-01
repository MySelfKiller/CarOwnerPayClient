package com.kayu.car_owner_pay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.ItemMessageBean;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * viewType--分别为item以及空view
     */
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    boolean isShowEmptyPage = false;
    private List<ItemMessageBean> dataList = new ArrayList<>();
    private ItemCallback itemCallback;
    private Context context;


    public ItemMessageAdapter(Context context, List<ItemMessageBean> dataList, boolean isShowEmptyPage, ItemCallback itemCallback) {
        this.dataList = dataList;
        this.isShowEmptyPage = isShowEmptyPage;
        this.itemCallback = itemCallback;
        this.context = context;

    }

    public void addAllData(List<ItemMessageBean> dataList, boolean isRemoveAllData) {
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view_tab, parent, false);

            return new RecyclerView.ViewHolder(emptyView) {
            };

        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            ItemMessageBean messageBean = dataList.get(position);
            //92#(2号枪)
            vh.time_tv.setText(messageBean.createTime);
            vh.title_tv.setText(messageBean.title);
            vh.content_tv.setText(messageBean.content);
            vh.mView.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    if (!StringUtil.isEmpty(messageBean.url.trim())) {
                        if (null != itemCallback) {
                            itemCallback.onItemCallback(position,messageBean.url);
                        }
                    }
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
        private TextView time_tv,title_tv,content_tv;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            time_tv = itemView.findViewById(R.id.item_message_time_tv);
            title_tv = itemView.findViewById(R.id.item_message_title_tv);
            content_tv = itemView.findViewById(R.id.item_message_content_tv);
        }

    }
}
