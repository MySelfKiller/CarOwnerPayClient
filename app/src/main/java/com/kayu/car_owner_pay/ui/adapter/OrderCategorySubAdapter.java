/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2017-09-18 23:47:01
 *
 * GitHub: https://github.com/GcsSloop
 * WeiBo: http://weibo.com/GcsSloop
 * WebSite: http://www.gcssloop.com
 */

package com.kayu.car_owner_pay.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.SysOrderBean;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;


public class OrderCategorySubAdapter extends RecyclerView.Adapter<OrderCategorySubAdapter.MyViewHolder> {

    public List<SysOrderBean> dataList = new ArrayList<>();
    private ItemCallback itemCallback;
//    private Context mContext;

    public OrderCategorySubAdapter(List<SysOrderBean> data, ItemCallback itemCallback){
        dataList = data;
//        this.mContext = mContent;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    public void addAllData(List<SysOrderBean> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }
    public void removeAllData(){
        if (null != this.dataList){
            this.dataList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //    Log.i("GCS", "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_order_category_lay, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_title.setText(dataList.get(position).title);
//        holder.tv_title_sub.setText(dataList.get(position).remark);
//        if (!StringUtil.isEmpty(dataList.get(position).tag)) {
//            holder.tv_tag.setText(dataList.get(position).tag);
//            holder.tv_tag.setVisibility(View.VISIBLE);
//        } else {
//
//        }
        holder.tv_tag.setVisibility(View.GONE);
        holder.tv_title_sub.setVisibility(View.GONE);
        KWApplication.getInstance().loadImg(dataList.get(position).icon,holder.tv_img);
        holder.itemView.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                if (null != itemCallback) {
                    itemCallback.onItemCallback(position,dataList.get(position));
                }
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_title_sub,tv_tag;
        ImageView tv_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.item_cate_text);
            tv_tag = itemView.findViewById(R.id.item_cate_tag);
            tv_title_sub = itemView.findViewById(R.id.item_cate_text_sub);
            tv_img = itemView.findViewById(R.id.item_cate_img);
        }
    }
}
