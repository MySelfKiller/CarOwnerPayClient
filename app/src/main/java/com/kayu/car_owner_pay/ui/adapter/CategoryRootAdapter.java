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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;


public class CategoryRootAdapter extends RecyclerView.Adapter<CategoryRootAdapter.MyViewHolder> {

    public List<List<CategoryBean>> dataList ;
    private ItemCallback itemCallback;
    private Context mContext;

    public CategoryRootAdapter(List<List<CategoryBean>> data, ItemCallback itemCallback){
        dataList = data;
//        this.mContext = mContent;
        this.itemCallback = itemCallback;
//        this.flag = flag;

    }

    public void addAllData(List<List<CategoryBean>> dataList) {
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
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_category_root_lay, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        holder.tv_title.setText(dataList.get(position).title);
//        holder.tv_title_sub.setText(dataList.get(position).remark);
//        if (!StringUtil.isEmpty(dataList.get(position).tag.trim())) {
//            holder.tv_tag.setText(dataList.get(position).tag);
//            holder.tv_tag.setVisibility(View.VISIBLE);
//        } else {
//            holder.tv_tag.setVisibility(View.GONE);
//        }
//
//        KWApplication.getInstance().loadImg(dataList.get(position).icon,holder.tv_img);
        holder.root_rv.setLayoutManager(new GridLayoutManager(mContext,dataList.get(position).size()));
        CategoryAdapter categoryAdapter = new CategoryAdapter(dataList.get(position), new ItemCallback() {
            @Override
            public void onItemCallback(int position, Object obj) {
                if (null != itemCallback) {
                    itemCallback.onItemCallback(position,obj);
                }
            }

            @Override
            public void onDetailCallBack(int position, Object obj) {

            }
        });
        holder.root_rv.setAdapter(categoryAdapter);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView root_rv;

        public MyViewHolder(View itemView) {
            super(itemView);
            root_rv = itemView.findViewById(R.id.item_cate_root_rv);
        }
    }
}
