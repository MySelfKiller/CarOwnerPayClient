package com.kayu.car_owner_pay.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.gcssloop.widget.PagerGridLayoutManager;
import com.gcssloop.widget.PagerGridSnapHelper;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.car_owner_pay.ui.adapter.CategoryAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.ScreenUtils;
import com.kayu.utils.status_bar_set.StatusBarUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {

    private MainViewModel mainViewModel;
    private RecyclerView category_rv,goods_rv;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.light_red_bg));
        View root = inflater.inflate(R.layout.fragment_shop, container, false);
        category_rv = root.findViewById(R.id.shop_category_rv);
        goods_rv = root.findViewById(R.id.shop_goods_rv);
        mainViewModel.getCategoryList(getContext()).observe(getActivity(), new Observer<List<CategoryBean>>() {
            @Override
            public void onChanged(List<CategoryBean> categoryBeans) {
                if (null == categoryBeans)
                    return;
                category_rv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dipToPx(getContext(), 80) * 2));
                PagerGridLayoutManager mLayoutManager = new PagerGridLayoutManager(2, 4, PagerGridLayoutManager
                        .HORIZONTAL);
                // 系统带的 RecyclerView，无需自定义


                // 水平分页布局管理器
                mLayoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
                    @Override
                    public void onPageSizeChanged(int pageSize) {
                    }

                    @Override
                    public void onPageSelect(int pageIndex) {
                    }
                });    // 设置页面变化监听器
                category_rv.setLayoutManager(mLayoutManager);
                CategoryAdapter categoryAdapter = new CategoryAdapter(categoryBeans, new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
                        CategoryBean categoryBean = (CategoryBean) obj;
//                        Intent intent = new Intent(getContext(), CourseListActivity.class);
//                        intent.putExtra("from","首页");
//                        intent.putExtra("titleName","课程列表");
//                        intent.putExtra("title",categoryBean.title);
//                        intent.putExtra("flag",1);
//                        intent.putExtra("categoryId",categoryBean.id);
//                        startActivity(intent);
                    }

                    @Override
                    public void onDetailCallBack(int position, Object obj) {

                    }
                });
                category_rv.setAdapter(categoryAdapter);
            }
        });


        // 设置滚动辅助工具
        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
        pageSnapHelper.attachToRecyclerView(category_rv);
        return root;
    }
}