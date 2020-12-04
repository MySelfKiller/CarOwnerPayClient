package com.kayu.car_owner_pay.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.BannerImageLoader;
import com.kayu.car_owner_pay.activity.CarWashListActivity;
import com.kayu.car_owner_pay.activity.GasStationListActivity;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.MessageActivity;
import com.kayu.car_owner_pay.activity.MyPagerAdapter;
import com.kayu.car_owner_pay.activity.WebViewActivity;
import com.kayu.car_owner_pay.model.BannerBean;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.text_banner.TextBannerView;
import com.kayu.car_owner_pay.ui.adapter.CategoryAdapter;
import com.kayu.car_owner_pay.ui.adapter.CategoryRootAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.ScreenUtils;
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
    private ImageView title_iv;
    //    private List<Fragment> subFragmentList;

//    private double distance;//距离/km
//    private int sort;//排序方式
//    private int oilNo;//油号类型

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        LogUtil.e("HomeFragment----","----onCreateView---");
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        LogUtil.e("HomeFragment----","----onViewCreated---");
        banner = view.findViewById(R.id.home_smart_banner);
        location_tv = view.findViewById(R.id.home_location_tv);
        view.findViewById(R.id.home_exchange_code).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
//                FragmentManager fg = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                fragmentTransaction.add(R.id.main_root_lay, new MessageActivity());
//                fragmentTransaction.addToBackStack("ddd");
//                fragmentTransaction.commit();
                startActivity(new Intent(getContext(), MessageActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        notify_show = view.findViewById(R.id.home_notify_show);
        title_iv = view.findViewById(R.id.home_title_iv);
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
                if (isRefresh || isLoadmore)
                    return;
                isRefresh = true;
                pageIndex = 1;
                initListView();

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

    }

    private boolean mHasLoadedOnce = false;// 页面已经加载过
    private boolean isCreated = false;

    @Override
    public void onStart() {
        super.onStart();
//        LogUtil.e("HomeFragment----","----onStart---");
        if (!isCreated) {
            initView();
//            LogUtil.e("HomeFragment----","----onStart------isCreated");
            LocationManagerUtil.getSelf().setLocationListener(new LocationCallback() {
                @Override
                public void onLocationChanged(AMapLocation location) {
//                    LogUtil.e("HomeFragment----","----onStart--------LocationCallback");
                    if (location.getErrorCode() == 0) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        cityName = location.getCity();
                        location_tv.setText(cityName);
                        if (!mHasLoadedOnce) {
                            refreshLayout.autoRefresh();
                            mHasLoadedOnce = true;
                        }

                    }
                }
            });
            isCreated = true;
        }

    }

    private int fragIndex = 0;

    private void initListView() {
        mainViewModel.getParameter(getContext(), 10).observe(requireActivity(), new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null == systemParam)
                    return;
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
    }

    private void initView() {
        mainViewModel.getParamSelect(requireContext());
        mainViewModel.getParamWash(requireContext());
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

//        hostBannerData.add("车主 151****6046，5分钟前，加油300元，节省55元");
//        hostBannerData.add("车主 188****2234，15分钟前，加油500元，节省100元");
//        hostBannerData.add("车主 134****2589，20分钟前，加油600元，节省145元");
//        for (Notice notice : indexData.noticeList){
//            hostBannerData.add(notice.title);
//        }


        mainViewModel.getBannerList(getContext()).observe(requireActivity(), new Observer<List<BannerBean>>() {
            @Override
            public void onChanged(List<BannerBean> bannerBeans) {
                if (null == bannerBeans)
                    return;
                List<String> urlList = new ArrayList<>();
//                urlList.add("http://static.kakayuy.com/group1/M00/00/01/rBoO7123JxOADlyoAAE4u9SZe-g672.jpg");
//                urlList.add("http://static.kakayuy.com/group1/M00/00/01/rBoO7123JxWAGoUGAAFhmDWpACM043.jpg");
//                urlList.add("http://static.kakayuy.com/group1/M00/00/02/rBoO717kdAiAX1V-AAFAnQJ_ZdA288.JPG");
                for (BannerBean item : bannerBeans) {
                    urlList.add(item.img);
                }
                title_iv.setBackgroundColor(Color.parseColor(bannerBeans.get(0).bgColor));
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
                                if (getUserVisibleHint()) {
                                    title_iv.setBackgroundColor(Color.parseColor(bannerBeans.get(position).bgColor));
//                                    StatusBarUtil.setStatusBarColor(getActivity(), Color.parseColor(bannerBeans.get(position).bgColor));
                                }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        String target = bannerBeans.get(position).href;
                        if (!StringUtil.isEmpty(target.trim())) {
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("url", target);
                            intent.putExtra("from", "首页");
                            getActivity().startActivity(intent);
                        }
                    }
                });
            }
        });

        mainViewModel.getCategoryList(getContext()).observe(getActivity(), new Observer<List<List<CategoryBean>>>() {
            @Override
            public void onChanged(List<List<CategoryBean>> categoryBeans) {
                if (null == categoryBeans)
                    return;

                int mColumns=1, mRows = categoryBeans.size();
//                if (categoryBeans.size() <= 4) {
//                    mColumns = 4;
//                    mRows = 1;
//
//                } else {
//                    mRows = categoryBeans.size() % 4 == 0 ? categoryBeans.size() / 4 : categoryBeans.size() / 4 + 1;
//                    mColumns = 4;
//                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_82) * mRows);
                layoutParams.topMargin = ScreenUtils.dipToPx(getContext(), getResources().getDimensionPixelSize(R.dimen.dp_3));
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
                CategoryRootAdapter categoryAdapter = new CategoryRootAdapter(categoryBeans, new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
                        CategoryBean categoryBean = (CategoryBean) obj;
                        String target = categoryBean.href;
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            // FIXME: 2020/12/1 添加加油跳转本地页面
                            startActivity(new Intent(getContext(), GasStationListActivity.class));
                        }else if (StringUtil.equals(categoryBean.type, "KY_WASH")){
                            // FIXME: 2020/12/1 添加洗车跳转本地页面
                            startActivity(new Intent(getContext(), CarWashListActivity.class));
                        }else {
                            if (!StringUtil.isEmpty(target)) {
                                Intent intent = new Intent(getContext(), WebViewActivity.class);
                                StringBuilder sb = new StringBuilder();
                                sb.append(target);
                                if (StringUtil.equals(categoryBean.type, "KY_H5")) {
                                    sb.append("&token=").append(KWApplication.getInstance().token);
                                }
                                intent.putExtra("url", target + sb.toString());
                                intent.putExtra("from", "首页");
                                startActivity(intent);

                            } else {
                                MessageDialog.show((AppCompatActivity) getContext(), "温馨提示", "功能未开启，敬请期待");
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
//        if (requestCode == Constants.RC_PERMISSION_BASE) {
//            permissionsCheck();
//        }
    }


//    public void permissionsCheck() {
////        String[] perms = {Manifest.permission.CAMERA};
//        String[] perms = ((MainActivity) requireActivity()).needPermissions;
//
//        ((MainActivity) requireActivity()).performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new BaseActivity.PermissionCallback() {
//            @Override
//            public void hasPermission(List<String> allPerms) {
//                mainViewModel.sendOilPayInfo(getContext());
//                checkLocation();
//            }
//
//            @Override
//            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
//                EasyPermissions.goSettingsPermissions(getActivity(), 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
//            }
//
//            @Override
//            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
//                MessageDialog dialog = MessageDialog.build((AppCompatActivity) requireActivity());
//                dialog.setStyle(DialogSettings.STYLE.STYLE_IOS);
//                dialog.setTheme(DialogSettings.THEME.LIGHT);
//                dialog.setTitle(getString(R.string.app_name));
//                dialog.setMessage(getString(R.string.dialog_rationale_ask_again));
//                dialog.setOkButton("设置", new OnDialogButtonClickListener() {
//
//                    @Override
//                    public boolean onClick(BaseDialog baseDialog, View v) {
//                        callback.onGranted();
//                        return false;
//                    }
//                });
//                dialog.setCancelable(false);
//
//                dialog.show();
//            }
//        });
//    }

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

//    private void requestLocation() {
//        WaitDialog.show((AppCompatActivity) getContext(),"定位中...");
//        LocationManagerUtil.getSelf().startLocation();
//    }
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        LogUtil.e("HomeFragment----","----onAttach---");
//    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        LogUtil.e("HomeFragment----","----setUserVisibleHint---");
//        if (!isVisibleToUser && isCreated){
////            StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
//        }
//    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        LogUtil.e("HomeFragment----","----onDestroyView---");
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        LogUtil.e("HomeFragment----","----onActivityCreated---");
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LogUtil.e("HomeFragment----","----onCreate---");
//    }
//
//    @Override
//    public void onAttachFragment(@NonNull Fragment childFragment) {
//        super.onAttachFragment(childFragment);
//        LogUtil.e("HomeFragment----","----onAttachFragment---");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        LogUtil.e("HomeFragment----","----onDestroy---");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        LogUtil.e("HomeFragment----","----onResume---");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        LogUtil.e("HomeFragment----","----onPause---");
////        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        LogUtil.e("HomeFragment----","----onStop---");
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        LogUtil.e("HomeFragment----","----onDetach---");
//    }

}