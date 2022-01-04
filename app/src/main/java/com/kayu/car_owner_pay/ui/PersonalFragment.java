package com.kayu.car_owner_pay.ui;

import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.gcssloop.widget.PagerGridLayoutManager;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.CarWashListActivity;
import com.kayu.car_owner_pay.activity.CustomerActivity;
import com.kayu.car_owner_pay.activity.GasStationListActivity;
import com.kayu.car_owner_pay.activity.MainActivity;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.OilOrderListActivity;
import com.kayu.car_owner_pay.activity.SettingsActivity;
import com.kayu.car_owner_pay.activity.WashOrderListActivity;
import com.kayu.car_owner_pay.activity.WebViewActivity;
import com.kayu.car_owner_pay.glide.GlideRoundTransform;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.car_owner_pay.model.SysOrderBean;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.model.UserBean;
import com.kayu.car_owner_pay.ui.adapter.CategoryRootAdapter;
import com.kayu.car_owner_pay.ui.adapter.OrderCategoryAdapter;
import com.kayu.car_owner_pay.ui.income.BalanceFragment;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.view.RoundImageView;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipGifDialog;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PersonalFragment extends Fragment {
    private SmartRefreshLayout refreshLayout;
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private MainViewModel mainViewModel;
    private RoundImageView user_head_img;
    private TextView user_name;
    private TextView user_balance,web_info_tv,card_num,user_tip;
    private TextView explain_content;
//    private ConstraintLayout all_order_lay;
//    private LinearLayout more_lay;
//    private ImageView user_card_bg;
    private LinearLayout income_lay;
    private TextView user_expAmt,user_rewad;
    private RecyclerView category_rv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_personal, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        //用户头像
        user_head_img = view.findViewById(R.id.personal_user_head_img);
        //用户名称
        user_name = view.findViewById(R.id.personal_user_name);
        //累计节省
        user_expAmt = view.findViewById(R.id.personal_user_expAmt);
        explain_content = view.findViewById(R.id.personal_explain_content);

        //收益
        user_rewad = view.findViewById(R.id.personal_user_rewad);
        //可体现
        user_balance = view.findViewById(R.id.personal_user_balance);
        user_balance = view.findViewById(R.id.personal_user_balance);
//        user_card_bg = view.findViewById(R.id.personal_user_card_bg);
//        KWApplication.getInstance().loadImg(R.mipmap.ic_personal_bg,user_card_bg,new GlideRoundTransform(getContext()));
        card_num = view.findViewById(R.id.personal_card_num);
        //账户提示语
        user_tip = view.findViewById(R.id.personal_user_tip);
        web_info_tv = view.findViewById(R.id.personal_web_info);
        income_lay = view.findViewById(R.id.personal_income_lay);

        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        refreshLayout.setEnableOverScrollDrag(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                if (isRefresh || isLoadmore)
                    return;
                isRefresh = true;
                initView();
            }
        });
        TextView detailed_list = view.findViewById(R.id.personal_detailed_list);
        detailed_list.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                FragmentManager fg = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fg.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.main_root_lay,new BalanceFragment());
                fragmentTransaction.addToBackStack("ddd");
                fragmentTransaction.commit();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        ConstraintLayout customer_services_lay = view.findViewById(R.id.personal_customer_services_lay);
        customer_services_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                startActivity(new Intent(getContext(), CustomerActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
//        ConstraintLayout course_lay = view.findViewById(R.id.personal_course_lay);
//        course_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                mainViewModel.getParameter(getContext(),11).observe(requireActivity(), new Observer<SystemParam>() {
//                    @Override
//                    public void onChanged(SystemParam systemParam) {
//                        String target = systemParam.url;
//                        if (!StringUtil.isEmpty(target)){
//                            Intent intent = new Intent(getContext(), WebViewActivity.class);
//                            intent.putExtra("url",target);
//                            intent.putExtra("from","新手教程");
//                            requireActivity().startActivity(intent);
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
        ConstraintLayout setting_lay = view.findViewById(R.id.personal_setting_lay);
        setting_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
//        all_order_lay = view.findViewById(R.id.id_all_order_lay);
//        more_lay = view.findViewById(R.id.personal_more_lay);
        category_rv = view.findViewById(R.id.personal_category_rv);
//        oil_order_lay = view.findViewById(R.id.personal_oil_order_lay);
//        oil_order_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                startActivity(new Intent(getContext(),OilOrderListActivity.class));
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
//        wash_order_lay = view.findViewById(R.id.personal_shop_order_lay);
//        wash_order_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                startActivity(new Intent(getContext(),WashOrderListActivity.class));
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
//        if (getUserVisibleHint()){
//            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
//        }
        isCreated = true;
    }

    private boolean isCreated = false;
    private boolean mHasLoadedOnce = false;// 页面已经加载过

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("PersonalFragment----","----setUserVisibleHint---");
        if (isVisibleToUser && isCreated && !mHasLoadedOnce) {
            LogUtil.e("PersonalFragment----","----setUserVisibleHint---isCreated");
            TipGifDialog.show((AppCompatActivity) requireContext(), "加载中...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
            isRefresh = true;
            mHasLoadedOnce = true;
            initView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("PersonalFragment----","----onStart---");
        if (!getUserVisibleHint() || mHasLoadedOnce)
            return;
        LogUtil.e("PersonalFragment----","----onStart---isVisibleToUser");
        TipGifDialog.show((AppCompatActivity) requireContext(), "加载中...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        isRefresh = true;
        mHasLoadedOnce = true;
        initView();

    }

    private void initView() {
//        if (null != LocationManagerUtil.getSelf().getLoccation()){
//            mainViewModel.getReminder(getContext(), LocationManagerUtil.getSelf().getLoccation().getCity()).observe(requireActivity(), new Observer<String>() {
//                @Override
//                public void onChanged(String parameter) {
//                    explain_content.setText(parameter);
//                }
//            });
//        }
        mainViewModel.sendOilPayInfo(getContext());
        mainViewModel.getUserInfo(getContext()).observe(requireActivity(), new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                if (null == userBean)
                    return;

                mainViewModel.getUserTips(requireContext()).observe(requireActivity(), new Observer<SystemParam>() {
                    @Override
                    public void onChanged(SystemParam systemParam) {
                        if (null  == systemParam)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(systemParam.content);
                            String tipStr="";
                            String btnStr="";
                            if (userBean.type == 1) {
                                JSONObject json1 = jsonObject.optJSONObject("1");
                                tipStr = json1.getString("tip");
                                btnStr = json1.getString("btn");
                            } else if (userBean.type == 2) {
                                JSONObject json2 = jsonObject.optJSONObject("2");
                                tipStr = json2.getString("tip");
                                btnStr = json2.getString("btn");
                            } else if (userBean.type == 3) {
                                JSONObject json3 = jsonObject.optJSONObject("3");
                                tipStr = json3.getString("tip");
                                btnStr = json3.getString("btn");
                            }
                            user_tip.setText(tipStr);
                            web_info_tv.setText(btnStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                KWApplication.getInstance().loadImg(userBean.headPic,user_head_img);
                StringBuilder sb = new StringBuilder();
                sb.append(userBean.phone);

                switch (userBean.type) {
                    case 1:
                        sb.append("（VIP）");
                        break;
                    case 2:
                        sb.append("（团长）");
                        break;
                    case 3:
                        sb.append("（运营商）");
                        break;
                }
                user_name.setText(sb.toString());
                user_balance.setText(String.valueOf(userBean.balance));

                String[] sss = userBean.busTitle.split("#");
                if (null != sss && sss.length == 2) {
                    explain_content.setText(sss[0]);
                    user_expAmt.setText(sss[1]);
                }

                user_rewad.setText(String.valueOf(userBean.rewardAmt));
                if (!StringUtil.isEmpty(userBean.inviteNo)) {
                    card_num.setText("卡号："+userBean.inviteNo);
                    card_num.setVisibility(View.VISIBLE);
                }else {
                    card_num.setVisibility(View.INVISIBLE);
                }
                if (userBean.type < 1) {
                    income_lay.setVisibility(View.GONE);
                } else {
                    income_lay.setVisibility(View.VISIBLE);
                }
                web_info_tv.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        StringBuilder jumpUrl = new StringBuilder();
                        if (userBean.type == 1) {
                            jumpUrl.append("https://www.kykj909.com/carfriend/static/cyt/index.html#/purchase?token=");
                        } else {
                            jumpUrl.append("https://www.kykj909.com/carfriend/static/cyt/index.html#/upgrade?token=");
                        }
                        long randomNum = System.currentTimeMillis();
                        jumpUrl.append(KWApplication.getInstance().token).append("&").append(randomNum);
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra("url",jumpUrl.toString());
                        requireActivity().startActivity(intent);
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });

            }
        });
        mainViewModel.getSysOrderList(getContext()).observe(requireActivity(), new Observer<List<List<SysOrderBean>>>() {
            @Override
            public void onChanged(List<List<SysOrderBean>> categoryBeans) {
                if (null == categoryBeans)
                    return;
                for (List<SysOrderBean> list : categoryBeans) {
                    for (SysOrderBean categoryBean : list) {
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            KWApplication.getInstance().isGasPublic = categoryBean.isPublic;
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_WASH")){
                            KWApplication.getInstance().isWashPublic = categoryBean.isPublic;
                        }
                    }
                }
                int mColumns=1, mRows = categoryBeans.size();
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
                OrderCategoryAdapter categoryAdapter = new OrderCategoryAdapter(categoryBeans, new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
                        SysOrderBean categoryBean = (SysOrderBean) obj;
                        int userRole = KWApplication.getInstance().userRole;
                        int isPublic = categoryBean.isPublic;
                        if (userRole == -2 && isPublic == 0){
                            KWApplication.getInstance().showRegDialog(getContext());
                            return;
                        }
                        String target = categoryBean.href;
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {

                            startActivity(new Intent(getContext(),OilOrderListActivity.class));
                        }else if (StringUtil.equals(categoryBean.type, "KY_WASH")){
                            startActivity(new Intent(getContext(),WashOrderListActivity.class));
                        }else {
                            if (!StringUtil.isEmpty(target)) {
                                Intent intent = new Intent(getContext(), WebViewActivity.class);
                                StringBuilder sb = new StringBuilder();
                                sb.append(target);
//                                sb.append("https://www.ky808.cn/carfriend/static/cyt/text/index.html#/advertising"); 测试视屏广告链接
                                if (StringUtil.equals(categoryBean.type, "KY_H5")) {
                                    if (target.contains("?")) {
                                        sb.append("&token=");
                                    } else {
                                        sb.append("?token=");
                                    }
                                    sb.append(KWApplication.getInstance().token);
                                }
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

//        mainViewModel.getSysParameter(getContext(),10).observe(requireActivity(), new Observer<SystemParam>() {
//            @Override
//            public void onChanged(SystemParam systemParam) {
//                if (null  == systemParam)
//                    return;
//                try {
//                    JSONObject jsonObject = new JSONObject(systemParam.content);
//                    int showGas = jsonObject.optInt("gas");
//                    int showCarWash = jsonObject.optInt("carwash");
//                    if (showGas == 1 && showCarWash ==1 ) {
//                        all_order_lay.setVisibility(View.VISIBLE);
//                        wash_order_lay.setVisibility(View.VISIBLE);
//                        oil_order_lay.setVisibility(View.VISIBLE);
//
//                    }else if(showGas == 0 && showCarWash == 0){
//                        all_order_lay.setVisibility(View.GONE);
//                        wash_order_lay.setVisibility(View.GONE);
//                        oil_order_lay.setVisibility(View.GONE);
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(more_lay.getLayoutParams());
//                        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_15)
//                                ,getResources().getDimensionPixelSize(R.dimen.dp_90)
//                                ,getResources().getDimensionPixelSize(R.dimen.dp_15)
//                                ,getResources().getDimensionPixelSize(R.dimen.dp_20));
//                        more_lay.setLayoutParams(layoutParams);
//
//                    } else {
//
//                        if (showCarWash == 1) {
//                            wash_order_lay.setVisibility(View.VISIBLE);
//                        }else{
//                            wash_order_lay.setVisibility(View.GONE);
//                        }
//                        if (showGas == 1) {
//                            oil_order_lay.setVisibility(View.VISIBLE);
//                        } else {
//                            oil_order_lay.setVisibility(View.GONE);
//                        }
//                        all_order_lay.setVisibility(View.VISIBLE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
        if (isRefresh) {
            refreshLayout.finishRefresh();
            isRefresh = false;
        }
        if (isLoadmore) {
            refreshLayout.finishLoadMore();
            isLoadmore = false;
        }
    }
}