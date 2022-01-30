package com.kayu.car_owner_pay.ui;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.location.AMapLocation;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.TabEntity;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gcssloop.widget.PagerGridLayoutManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.BannerImageLoader;
import com.kayu.car_owner_pay.activity.CarWashListActivity;
import com.kayu.car_owner_pay.activity.GasStationListActivity;
import com.kayu.car_owner_pay.activity.MainActivity;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.MessageActivity;
import com.kayu.car_owner_pay.activity.MyPagerAdapter;
import com.kayu.car_owner_pay.activity.WebViewActivity;
import com.kayu.car_owner_pay.model.BannerBean;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.popupWindow.CustomPopupWindow;
import com.kayu.car_owner_pay.text_banner.TextBannerView;
import com.kayu.car_owner_pay.ui.adapter.CategoryRootAdapter;
import com.kayu.utils.Constants;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.location.CoordinateTransformUtil;
import com.kayu.utils.location.LocationCallback;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.view.AdaptiveHeightViewPager;
import com.kongzue.dialog.v3.MessageDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private MainViewModel mainViewModel;
    private Banner banner;
    private RecyclerView category_rv;
    private TextBannerView hostTextBanner;

    private RefreshLayout refreshLayout;
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private int pageIndex;
    boolean isFirstLoad = true;

    private CommonTabLayout slidingTabLayout;
    private AdaptiveHeightViewPager mViewPager;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private Callback callback = new Callback() {
        @Override
        public void onSuccess() {
            if (isRefresh) {
                refreshLayout.finishRefresh();
                isRefresh = false;
            }
            if (isLoadmore) {
                refreshLayout.finishLoadMore();
                isLoadmore = false;
            }
        }

        @Override
        public void onError() {
            pageIndex = 1;
        }
    };
    private TextView location_tv,notify_show;
    private PagerAdapter adapter;
    private LinearLayout title_lay_bg;
    private FadingScrollView scrollView;
    private BottomNavigationView navigation;
    //    private List<Fragment> subFragmentList;

//    private double distance;//距离/km
//    private int sort;//排序方式
//    private int oilNo;//油号类型

    public HomeFragment(BottomNavigationView view){
        navigation = view;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogUtil.e("HomeFragment----","----onCreateView---");
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_home_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.e("HomeFragment----","----onViewCreated---");
        banner = view.findViewById(R.id.home_smart_banner);
        location_tv = view.findViewById(R.id.home_location_tv);
        view.findViewById(R.id.home_exchange_code).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                startActivity(new Intent(getContext(), MessageActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        notify_show = view.findViewById(R.id.home_notify_show);
        title_lay_bg = view.findViewById(R.id.home_title_lay);
        title_lay_bg.setAlpha(0);
        scrollView = view.findViewById(R.id.home_scroll);
        scrollView.setFadingView(title_lay_bg);
        scrollView.setFadingHeightView(banner);
        category_rv = view.findViewById(R.id.home_category_rv);
        hostTextBanner = view.findViewById(R.id.home_hostTextBanner);
        slidingTabLayout = view.findViewById(R.id.list_ctl);
        mViewPager = view.findViewById(R.id.list_vp);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
//        refreshLayout.setEnableNestedScroll(false);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        refreshLayout.setEnableOverScrollDrag(true);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                if (!isHasLocation){
//                    return;
//                }
                if (isRefresh || isLoadmore)
                    return;
                isRefresh = true;
                pageIndex = 1;
                if (mHasLoadedOnce) {
//                    LogUtil.e("HomeFragment----","----setOnRefreshListener---mHasLoadedOnce");
                    initView();
                }
                initListView();
                mHasLoadedOnce = true;

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                if (isRefresh || isLoadmore)
                    return;
                isLoadmore = true;
                pageIndex = pageIndex + 1;
                loadChildData();

            }
        });
        mTabEntities.add(new TabEntity("加油", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close));
        mTabEntities.add(new TabEntity("洗车", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close));
        mFragments.add(new HomeGasStationFragment(mViewPager, 0, callback));
        mFragments.add(new HomeCarWashFragment(mViewPager, 1, callback));
        adapter = new MyPagerAdapter(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(adapter);
        slidingTabLayout.setTabData(mTabEntities);
        slidingTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
                fragIndex = position;
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragIndex = position;
                slidingTabLayout.setCurrentTab(position);
                mViewPager.resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        checkLocation();
        LocationManagerUtil.getSelf().setLocationListener(new LocationCallback() {
            @Override
            public void onLocationChanged(AMapLocation location) {
//                    LogUtil.e("HomeFragment----","----onStart--------LocationCallback");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                cityName = location.getCity();
                location_tv.setText(cityName);
                isHasLocation = true;
                if (!hasAutoRefresh) {
//                    LogUtil.e("HomeFragment----","----onLocationChanged--- hasAutoRefresh----" );
                    isRefresh = true;
                    pageIndex = 1;
                    initListView();
                    mHasLoadedOnce = true;
                    hasAutoRefresh = true;
                }
            }
        });
        isCreated = true;
    }
    private boolean isHasLocation = false;
    private boolean mHasLoadedOnce = false;// 页面已经加载过
    private boolean isCreated = false;
    private boolean hasAutoRefresh= false;

    @Override
    public void onStart() {
        super.onStart();
        if (!getUserVisibleHint())
            return;
//        LogUtil.e("HomeFragment----","----onStart---");
        if (!mHasLoadedOnce) {
//            LogUtil.e("HomeFragment----","----onStart---mHasLoadedOnce");
            initView();
            if (isHasLocation) {
                isRefresh = true;
                pageIndex = 1;
                initListView();
                mHasLoadedOnce = true;
                hasAutoRefresh = true;
//            LogUtil.e("HomeFragment----","----onStart---isHasLocation");

            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        LogUtil.e("HomeFragment----","----setUserVisibleHint---");
        if (isVisibleToUser && isCreated) {
//            LogUtil.e("HomeFragment----","----setUserVisibleHint---isCreated");
            if (!mHasLoadedOnce) {
//                LogUtil.e("HomeFragment----","----setUserVisibleHint---mHasLoadedOnce");
                initView();
                if (isHasLocation) {
                    isRefresh = true;
                    pageIndex = 1;
                    initListView();
                    mHasLoadedOnce = true;
                    hasAutoRefresh = true;
//                LogUtil.e("HomeFragment----","----setUserVisibleHint---isHasLocation");

                }
            }
        }
        if (null != popWindow &&!hasClose){
            if (isVisibleToUser){
                popWindow.showAtLocation(navigation, Gravity.NO_GRAVITY
                        ,(KWApplication.getInstance().displayWidth - navigation.getMeasuredWidth()) / 2
                        ,KWApplication.getInstance().displayHeight - navigation.getMeasuredHeight() - navigation.getMeasuredHeight()/3);
            }else {
                popWindow.dismiss();
            }
        }

    }

    private int fragIndex = 0;

    private void initListView() {
        mainViewModel.getSysParameter(getContext(), 10).observe(requireActivity(), new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null == systemParam)
                    return;
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE).edit();
                editor.putString(Constants.system_args, systemParam.content);
                editor.apply();
                editor.commit();
                try {
                    JSONObject jsonObject = new JSONObject(systemParam.content);
                    int showGas = jsonObject.optInt("gas");
                    int showCarWash = jsonObject.optInt("carwash");
                    if (showGas == 1 && showCarWash == 1) {
                        slidingTabLayout.setVisibility(View.VISIBLE);
                        mViewPager.setCurrentItem(fragIndex);
                        slidingTabLayout.setCurrentTab(fragIndex);
                        mViewPager.setScrollble(true);
                    } else if (showGas == 0 && showCarWash == 0) {
                        slidingTabLayout.setVisibility(View.GONE);
                        mViewPager.setVisibility(View.GONE);
                    } else {
                        slidingTabLayout.setVisibility(View.GONE);

                        if (showCarWash == 1) {
                            fragIndex = 1;
                            mViewPager.setCurrentItem(fragIndex);
                            mViewPager.setScrollble(false);
                        } else if (showGas == 1) {
                            fragIndex = 0;
                            mViewPager.setCurrentItem(fragIndex);
                            mViewPager.setScrollble(false);
                        }
                    }
                    loadChildData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        if (isRefresh) {
            refreshLayout.finishRefresh();
            isRefresh = false;
        }
        if (isLoadmore) {
            refreshLayout.finishLoadMore();
            isLoadmore = false;
        }
    }

    private boolean hasShow = false;
    private boolean hasClose = false;

    private void initView() {
        mainViewModel.getRegDialogTip(getActivity()).observe(getActivity(), new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                KWApplication.getInstance().regDialogTip = systemParam;
                //KWApplication.getInstance().userRole == -2 &&
                if ( null != KWApplication.getInstance().regDialogTip && KWApplication.getInstance().userRole == -2 && !hasShow) {
                    showApplyCardDialog(getActivity(),getContext(),navigation);
                    hasShow = true;
                }
            }
        });
        mainViewModel.getNotifyNum(getContext()).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (null == integer)
                    return;
                if (integer == 0) {
                    notify_show.setVisibility(View.GONE);
                } else {
                    notify_show.setVisibility(View.VISIBLE);
                }
            }
        });
        mainViewModel.getNotifyList(getContext()).observe(requireActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
//                List<String> hostBannerData = new ArrayList<>();
                if (null != strings && strings.size() > 0) {
                    hostTextBanner.setDatas(strings);
                }
            }
        });

        mainViewModel.getBannerList(getContext()).observe(requireActivity(), new Observer<List<BannerBean>>() {
            @Override
            public void onChanged(List<BannerBean> bannerBeans) {
                if (null == bannerBeans)
                    return;
                List<String> urlList = new ArrayList<>();
                for (BannerBean item : bannerBeans) {
                    if (StringUtil.equals(item.type, "KY_GAS")) {
                        KWApplication.getInstance().isGasPublic = item.isPublic;
                    }
                    if (StringUtil.equals(item.type, "KY_WASH")){
                        KWApplication.getInstance().isWashPublic = item.isPublic;
                    }
                    urlList.add(item.img);
                }
//                title_lay.setBackgroundColor(Color.parseColor(bannerBeans.get(0).bgColor));
//                StatusBarUtil.setStatusBarColor(getActivity(), Color.parseColor(bannerBeans.get(0).bgColor));
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                        .setIndicatorGravity(BannerConfig.RIGHT)
                        .setImageLoader(new BannerImageLoader())
                        .setImages(urlList)
//                .setBannerTitles(titles)
                        .setDelayTime(2000)
                        .start()
                        .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
//                                if (getUserVisibleHint()) {
//                                    title_lay.setBackgroundColor(Color.parseColor(bannerBeans.get(position).bgColor));
//                                    StatusBarUtil.setStatusBarColor(getActivity(), Color.parseColor(bannerBeans.get(position).bgColor));
//                                }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        String target = bannerBeans.get(position).href;
                        int isPublic = bannerBeans.get(position).isPublic;
                        int userRole = KWApplication.getInstance().userRole;
                        if ( userRole == -2 &&  isPublic == 0){
                            KWApplication.getInstance().showRegDialog(getContext());
                            return;
                        }
                        if (StringUtil.equals(bannerBeans.get(position).type, "KY_GAS")) {
                            startActivity(new Intent(getContext(), GasStationListActivity.class));
                        }else if (StringUtil.equals(bannerBeans.get(position).type, "KY_WASH")){
                            startActivity(new Intent(getContext(), CarWashListActivity.class));
                        }else {
                            if (!StringUtil.isEmpty(target)) {
                                Intent intent = new Intent(getContext(), WebViewActivity.class);
                                StringBuilder sb = new StringBuilder();
                                sb.append(target);
                                if (StringUtil.equals(bannerBeans.get(position).type, "KY_H5")) {
                                    if (target.contains("?")) {//KYCityName KYLat KYLon
                                        sb.append("&token=");
                                    } else {
                                        sb.append("?token=");
                                    }
                                    sb.append(KWApplication.getInstance().token);
                                    sb.append("&locationName=");
                                    sb.append(cityName);
                                    sb.append("&selectLocation=");
                                    sb.append(longitude);
                                    sb.append(",");
                                    sb.append(latitude);
                                }
                                intent.putExtra("url", sb.toString());
                                intent.putExtra("from", "首页");
                                startActivity(intent);

                            } else {
                                MessageDialog.show((AppCompatActivity) requireContext(), "温馨提示", "功能未开启，敬请期待");
                            }
                        }
                    }
                });
            }
        });

        mainViewModel.getCategoryList(getContext()).observe(requireActivity(), new Observer<List<List<CategoryBean>>>() {
            @Override
            public void onChanged(List<List<CategoryBean>> categoryBeans) {
                if (null == categoryBeans)
                    return;
                List<List<CategoryBean>> categoryListNew = new ArrayList<>();
                List<CategoryBean> categoryBeans1 = new ArrayList<>();
                for (List<CategoryBean> list : categoryBeans) {
                    for (CategoryBean categoryBean : list) {
                        if (StringUtil.equals(categoryBean.title, "特惠加油")
                        || StringUtil.equals(categoryBean.title, "特惠洗车")
                        || StringUtil.equals(categoryBean.title, "电影订票")){
                            categoryBeans1.add(categoryBean);
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            KWApplication.getInstance().isGasPublic = categoryBean.isPublic;
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_WASH")){
                            KWApplication.getInstance().isWashPublic = categoryBean.isPublic;
                        }
                    }
                }
                categoryListNew.add(categoryBeans1);
                //当前是游客模式展示3个
                if (null !=KWApplication.getInstance().regDialogTip && !KWApplication.getInstance().regDialogTip.blank1.equals("-2")) {
                    categoryListNew = categoryBeans;
                }
                int mColumns=1, mRows = categoryListNew.size();
//                if (categoryBeans.size() <= 4) {
//                    mColumns = 4;
//                    mRows = 1;
//
//                } else {
//                    mRows = categoryBeans.size() % 4 == 0 ? categoryBeans.size() / 4 : categoryBeans.size() / 4 + 1;
//                    mColumns = 4;
//                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_84) * mRows);
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.dp_14);
                category_rv.setLayoutParams(layoutParams);

                PagerGridLayoutManager mLayoutManager = new PagerGridLayoutManager(mRows, mColumns, PagerGridLayoutManager
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
                CategoryRootAdapter categoryAdapter = new CategoryRootAdapter(categoryListNew, new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
                        CategoryBean categoryBean = (CategoryBean) obj;
                        int userRole = KWApplication.getInstance().userRole;
                        int isPublic = categoryBean.isPublic;
                        if (userRole == -2 && isPublic == 0){
                            KWApplication.getInstance().showRegDialog(getContext());
                            return;
                        }
                        String target = categoryBean.href;
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            startActivity(new Intent(getContext(), GasStationListActivity.class));
                        }else if (StringUtil.equals(categoryBean.type, "KY_WASH")){
                            startActivity(new Intent(getContext(), CarWashListActivity.class));
                        }else {
                            if (!StringUtil.isEmpty(target)) {
                                Intent intent = new Intent(getContext(), WebViewActivity.class);
                                StringBuilder sb = new StringBuilder();
                                sb.append(target);
//                                sb.append("https://www.ky808.cn/carfriend/static/alone/demo.html"); //测试视屏广告链接
                                if (StringUtil.equals(categoryBean.type, "KY_H5")) {
                                    if (target.contains("?")) {
                                        sb.append("&token=");
                                    } else {
                                        sb.append("?token=");
                                    }
                                    sb.append(KWApplication.getInstance().token);
                                    sb.append("&locationName=");
                                    sb.append(cityName);
                                    sb.append("&selectLocation=");
                                    sb.append(longitude);
                                    sb.append(",");
                                    sb.append(latitude);
                                }
//                                intent.putExtra("url", "http://192.168.3.32:8080/#/index");
                                intent.putExtra("url", sb.toString());
                                intent.putExtra("from", "首页");
                                startActivity(intent);

                            } else {
                                MessageDialog.show((AppCompatActivity) requireContext(), "温馨提示", "功能未开启，敬请期待");
                            }
                        }

                    }

                    @Override
                    public void onDetailCallBack(int position, Object obj) {

                    }
                });
                category_rv.setAdapter(categoryAdapter);
            }
        });


        // 设置滚动辅助工具
//        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
//        pageSnapHelper.attachToRecyclerView(category_rv);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void loadChildData() {
        if (isFirstLoad) {
            for (int x = 0; x < mFragments.size(); x++) {
                if (mFragments.get(x) instanceof HomeGasStationFragment) {
                    HomeGasStationFragment homeGasStationFragment = (HomeGasStationFragment) mFragments.get(x);
                    homeGasStationFragment.reqData(refreshLayout, pageIndex, isRefresh, isLoadmore, latitude, longitude);
                }
                if (mFragments.get(x) instanceof HomeCarWashFragment) {
                    HomeCarWashFragment homeCarWashFragment = (HomeCarWashFragment) mFragments.get(x);
                    double[] bddfsdfs = CoordinateTransformUtil.gcj02tobd09(longitude, latitude);
                    homeCarWashFragment.reqData(refreshLayout, pageIndex, isRefresh, isLoadmore, bddfsdfs[1], bddfsdfs[0], cityName);
                }
            }
            isFirstLoad = false;
        } else {
            if (fragIndex == 0) {
                HomeGasStationFragment homeGasStationFragment = (HomeGasStationFragment) mFragments.get(fragIndex);
                homeGasStationFragment.reqData(refreshLayout, pageIndex, isRefresh, isLoadmore, latitude, longitude);
            } else if (fragIndex == 1) {
                HomeCarWashFragment homeCarWashFragment = (HomeCarWashFragment) mFragments.get(fragIndex);
                double[] bddfsdfs = CoordinateTransformUtil.gcj02tobd09(longitude, latitude);
                homeCarWashFragment.reqData(refreshLayout, pageIndex, isRefresh, isLoadmore, bddfsdfs[1], bddfsdfs[0], cityName);
            }
        }
    }

    private double latitude, longitude;
    private String cityName;

    private CustomPopupWindow popWindow;
    private String regTips = null;
    public void showApplyCardDialog(Activity activity, Context context, View v){
        SystemParam regDialogTip = KWApplication.getInstance().regDialogTip;
        if (null == regDialogTip || StringUtil.isEmpty(regDialogTip.content)){

            return;
        }
        try {
            //{
            // "content": "{\"title\":\"免费办理会员\",\"desc\":\"成为会员，立享全球超百项特权\",\"regBtn\":\"立即免费办理\",\"pastTitle\":\"已办理车友团特权卡\",\"pastBtn\":\"激活车友团特权卡\",\"regTips\":\"成为特权卡会员,每年立省1000元#去办卡\"}",
            //}
            JSONObject contentJSon = new JSONObject(regDialogTip.content);
            regTips = contentJSon.getString("regTips");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String[] tips = regTips.split("#");
        if (!StringUtil.isEmpty(regTips)){
            if (null == tips || tips.length!=2)
                return;

        }
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_apply_card,null);
        ImageView dia_close = view.findViewById(R.id.dia_close_iv);
        dia_close.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                popWindow.dismiss();
                hasClose = true;
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView dia_content = view.findViewById(R.id.dia_act_context);
        dia_content.setText(tips[0]);
        AppCompatButton dia_btn_handle = view.findViewById(R.id.dia_act_btn_handle);
        dia_btn_handle.setText(tips[1]);
        dia_btn_handle.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {

                if (StringUtil.isEmpty(regDialogTip.url))
                    return;
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", regDialogTip.url);
                context.startActivity(intent);
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        popWindow = new CustomPopupWindow.PopupWindowBuilder(context)
                //.setView(R.layout.pop_layout)
                .setView(view)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocusable(false)
                //弹出popWindow时，背景是否变暗
                .enableBackgroundDark(false)
                //控制亮度
                .setBgDarkAlpha(0.0f)
                .setOutsideTouchable(false)
//                            .setAnimationStyle(R.style.popWindowStyle)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //对话框销毁时
                    }
                })
                .create();
        popWindow.showAtLocation(v, Gravity.NO_GRAVITY
                ,(KWApplication.getInstance().displayWidth - view.getMeasuredWidth()) / 2
                ,KWApplication.getInstance().displayHeight - v.getMeasuredHeight() -v.getMeasuredHeight()/3);

    }


}