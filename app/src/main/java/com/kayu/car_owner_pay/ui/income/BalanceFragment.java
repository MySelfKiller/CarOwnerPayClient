package com.kayu.car_owner_pay.ui.income;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.TabEntity;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.MyPagerAdapter;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.status_bar_set.StatusBarUtil;

import java.util.ArrayList;

public class BalanceFragment extends Fragment {
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ViewPager mViewPager;
    private CommonTabLayout slidingTabLayout;

    public BalanceFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //标题栏
        view.findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                requireActivity().onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = view.findViewById(R.id.title_back_tv);
        TextView title_name = view.findViewById(R.id.title_name_tv);
        title_name.setText("明细");
//        title_name.setVisibility(View.GONE);
        back_tv.setText("我的");


        slidingTabLayout = view.findViewById(R.id.detailed_list_sl);
        mViewPager = view.findViewById(R.id.detailed_view_pager);
        if (getUserVisibleHint() && !mHasLoadedOnce){
            initView();
            mHasLoadedOnce = true;
        }
        isCreated = true;
    }

    private boolean isCreated = false;
    private boolean mHasLoadedOnce = false;// 页面已经加载过
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("hm","CustomerListBankFragment---------setUserVisibleHint===="+isVisibleToUser);
        if (isVisibleToUser&& !mHasLoadedOnce && isCreated){
            initView();
            mHasLoadedOnce = true;
        }
    }

    private void initView(){
        mTabEntities.add(new TabEntity("支出",R.mipmap.ic_bg_close,R.mipmap.ic_bg_close));
        mTabEntities.add(new TabEntity("收入",R.mipmap.ic_bg_close,R.mipmap.ic_bg_close));



        slidingTabLayout.setTabData(mTabEntities);
        slidingTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });


        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                slidingTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mFragments.add(new DetailedIncomeFragment(0 ));
        mFragments.add(new DetailedIncomeFragment(1) );
        PagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager(),mFragments);
        mViewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(0);
        slidingTabLayout.setCurrentTab(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.startOrgColor_btn));
        LogUtil.e("StationFragment----", "----onDetach---");
    }
}