package com.kayu.car_owner_pay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.MyPagerAdapter;
import com.kayu.car_owner_pay.activity.WebViewActivity;
import com.kayu.car_owner_pay.model.BannerBean;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.text_banner.TextBannerView;
import com.kayu.car_owner_pay.ui.adapter.CategoryAdapter;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.ScreenUtils;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.location.LocationCallback;
import com.kayu.utils.location.LocationManager;
import com.kayu.utils.status_bar_set.StatusBarUtil;
import com.kayu.utils.view.AdaptiveHeightViewPager;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
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
    private TextView location_tv;
    private PagerAdapter adapter;
    //    private List<Fragment> subFragmentList;

//    private double distance;//距离/km
//    private int sort;//排序方式
//    private int oilNo;//油号类型

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogUtil.e("HomeFragment----","----onCreateView---");
        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        banner = view.findViewById(R.id.home_smart_banner);
        location_tv = view.findViewById(R.id.home_location_tv);
        view.findViewById(R.id.home_exchange_code).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                FragmentManager fg = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fg.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.main_root_lay,new ExchangeFragment());
                fragmentTransaction.addToBackStack("ddd");
                fragmentTransaction.commit();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        category_rv = view.findViewById(R.id.home_category_rv);
        hostTextBanner = view.findViewById(R.id.home_hostTextBanner);
        slidingTabLayout = view.findViewById(R.id.list_ctl);
        mViewPager = view.findViewById(R.id.list_vp);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);

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
                initView();

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
        mTabEntities.add(new TabEntity("附近加油站",R.mipmap.ic_bg_close,R.mipmap.ic_bg_close));
        mTabEntities.add(new TabEntity("附近洗车",R.mipmap.ic_bg_close,R.mipmap.ic_bg_close));
        mFragments.add(new HomeGasStationFragment(getContext(),mainViewModel, mViewPager,0,callback));
        mFragments.add(new HomeCarWashFragment(getContext(),mainViewModel, mViewPager,1,callback));
        adapter = new MyPagerAdapter(getChildFragmentManager(),mFragments);
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

        checkLocation();
    }

    private int fragIndex = 0;

    private void initView() {
        mainViewModel.getParameter(getContext(),10).observe(requireActivity(), new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null  == systemParam)
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(systemParam.content);
                    int showGas = jsonObject.optInt("gas");
                    int showCarWash = jsonObject.optInt("carwash");
                    if (showGas == 1 && showCarWash ==1 ) {
                        slidingTabLayout.setVisibility(View.VISIBLE);
                        mViewPager.setCurrentItem(0);
                        slidingTabLayout.setCurrentTab(0);
                        mViewPager.setScrollble(true);
                    }else if(showGas == 0 && showCarWash == 0){
                        slidingTabLayout.setVisibility(View.GONE);
                        mViewPager.setVisibility(View.GONE);
                    } else {
                        slidingTabLayout.setVisibility(View.GONE);

                        if (showCarWash == 1) {
                            mViewPager.setCurrentItem(1);
                            mViewPager.setScrollble(false);
                            fragIndex = 1;
                        }else if (showGas == 1){
                            mViewPager.setCurrentItem(0);
                            fragIndex = 0;
                            mViewPager.setScrollble(false);
                        }
                    }
                    loadChildData();
                } catch (JSONException e) {
                    e.printStackTrace();
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
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                        .setIndicatorGravity(BannerConfig.RIGHT)
                        .setImageLoader(new BannerImageLoader())
                        .setImages(urlList)
//                .setBannerTitles(titles)
                        .setDelayTime(2000)
                        .start();
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        String target = bannerBeans.get(position).href;
                        if (!StringUtil.isEmpty(target)){
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("url",target);
                            intent.putExtra("from","首页");
                            getActivity().startActivity(intent);
                        }
                    }
                });
            }
        });

        mainViewModel.getCategoryList(getContext()).observe(getActivity(), new Observer<List<CategoryBean>>() {
            @Override
            public void onChanged(List<CategoryBean> categoryBeans) {
                if (null == categoryBeans)
                    return;

                int mColumns,mRows;
                if (categoryBeans.size() <= 5) {
                    if (categoryBeans.size() == 5){
                        mColumns = 5;
                    }else {
                        mColumns = 4;
                    }
                    mRows = 1;

                } else {
                    mRows = categoryBeans.size()%5 == 0 ? categoryBeans.size()/5 : categoryBeans.size()/5 +1;
                    mColumns = 5;
                }

                category_rv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dipToPx(getContext(),80)*mRows));
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
                CategoryAdapter categoryAdapter = new CategoryAdapter(categoryBeans, new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
                        CategoryBean categoryBean = (CategoryBean)obj;
                        String target = categoryBean.href;
                        if (!StringUtil.isEmpty(target)) {
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("url", target + "?token=" + KWApplication.getInstance().token);
                            intent.putExtra("from", "首页");
                            getActivity().startActivity(intent);

                        } else {
                            MessageDialog.show((AppCompatActivity) getContext(),"温馨提示","功能未开启，敬请期待");
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
                    homeCarWashFragment.reqData(refreshLayout, pageIndex, isRefresh,isLoadmore, latitude, longitude, cityName);
                }
            }
            isFirstLoad = false;
        } else {
            if (fragIndex == 0) {
                HomeGasStationFragment homeGasStationFragment = (HomeGasStationFragment) mFragments.get(fragIndex);
                homeGasStationFragment.reqData(refreshLayout, pageIndex, isRefresh, isLoadmore, latitude, longitude);
            } else if (fragIndex == 1) {
                HomeCarWashFragment homeCarWashFragment = (HomeCarWashFragment) mFragments.get(fragIndex);
                homeCarWashFragment.reqData(refreshLayout, pageIndex, isRefresh, isLoadmore, latitude, longitude, cityName);
            }
        }
    }

    private double latitude,longitude;
    private String cityName;

    private void checkLocation() {
        WaitDialog.show((AppCompatActivity) getContext(),"定位中...");
        LocationManager.getSelf().startLocation();
        LocationManager.getSelf().setLocationListener(new LocationCallback() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                WaitDialog.dismiss();
                if (location.getErrorCode() == 0) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    cityName = location.getCity();
                    location_tv.setText(cityName);
                    refreshLayout.autoRefresh();

                } else {
                    MessageDialog.show((AppCompatActivity)getActivity(), "定位失败", "请重新定位", "重新定位").setCancelable(false)
                            .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v) {
                                    baseDialog.doDismiss();
                                    checkLocation();
                                    return true;
                                }
                            });
                }
            }
        });
    }
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        LogUtil.e("HomeFragment----","----onAttach---");
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        LogUtil.e("HomeFragment----","----onViewCreated---");
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        LogUtil.e("HomeFragment----","----onDestroyView---");
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        LogUtil.e("HomeFragment----","----onStart---");
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