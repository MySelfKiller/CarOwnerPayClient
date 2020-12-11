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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.CustomerActivity;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.OilOrderListActivity;
import com.kayu.car_owner_pay.activity.SettingsActivity;
import com.kayu.car_owner_pay.activity.WashOrderListActivity;
import com.kayu.car_owner_pay.activity.WebViewActivity;
import com.kayu.car_owner_pay.glide.GlideRoundTransform;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.model.UserBean;
import com.kayu.car_owner_pay.ui.income.BalanceFragment;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.view.RoundImageView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalFragment extends Fragment {
    private SmartRefreshLayout refreshLayout;
    boolean isLoadmore = false;
    boolean isRefresh = false;
    private MainViewModel mainViewModel;
    private RoundImageView user_head_img;
    private TextView user_name;
    private TextView user_balance,web_info_tv,card_num;
    private TextView explain_content;
    private ConstraintLayout oil_order_lay,wash_order_lay, all_order_lay;
    private LinearLayout more_lay;
    private ImageView user_card_bg;
    private LinearLayout income_lay;

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
        //卡余额
        user_balance = view.findViewById(R.id.personal_user_balance);
        user_card_bg = view.findViewById(R.id.personal_user_card_bg);
        KWApplication.getInstance().loadImg(R.mipmap.ic_personal_bg,user_card_bg,new GlideRoundTransform(getContext()));
        card_num = view.findViewById(R.id.personal_card_num);
        //账户提示语
//        explain_content = view.findViewById(R.id.personal_explain_content);
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
        ConstraintLayout course_lay = view.findViewById(R.id.personal_course_lay);
        course_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                mainViewModel.getParameter(getContext(),11).observe(requireActivity(), new Observer<SystemParam>() {
                    @Override
                    public void onChanged(SystemParam systemParam) {
                        String target = systemParam.url;
                        if (!StringUtil.isEmpty(target)){
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("url",target);
                            intent.putExtra("from","新手教程");
                            requireActivity().startActivity(intent);
                        }
                    }
                });


            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
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
        all_order_lay = view.findViewById(R.id.id_all_order_lay);
        more_lay = view.findViewById(R.id.personal_more_lay);
        oil_order_lay = view.findViewById(R.id.personal_oil_order_lay);
        oil_order_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                startActivity(new Intent(getContext(),OilOrderListActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        wash_order_lay = view.findViewById(R.id.personal_shop_order_lay);
        wash_order_lay.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                startActivity(new Intent(getContext(),WashOrderListActivity.class));
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        if (getUserVisibleHint()){
            refreshLayout.autoRefresh();
            mHasLoadedOnce = true;
        }
    }

    private boolean mHasLoadedOnce = false;// 页面已经加载过

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !mHasLoadedOnce) {
            refreshLayout.autoRefresh();
            mHasLoadedOnce = true;
        }
    }

    private void initView() {
        if (null != LocationManagerUtil.getSelf().getLoccation()){
            mainViewModel.getReminder(getContext(), LocationManagerUtil.getSelf().getLoccation().getCity()).observe(requireActivity(), new Observer<String>() {
                @Override
                public void onChanged(String parameter) {
//                    explain_content.setText(parameter);
                }
            });
        }

        mainViewModel.getUserInfo(getContext()).observe(requireActivity(), new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                if (isRefresh) {
                    refreshLayout.finishRefresh();
                    isRefresh = false;
                }
                if (isLoadmore) {
                    refreshLayout.finishLoadMore();
                    isLoadmore = false;
                }
                if (null == userBean)
                    return;
                KWApplication.getInstance().loadImg(userBean.headPic,user_head_img);
                user_name.setText(userBean.phone);
                user_balance.setText(String.valueOf(userBean.expAmt));
                card_num.setText("卡号："+userBean.inviteNo);
                if (userBean.type < 0) {
                    income_lay.setVisibility(View.GONE);
                } else {
                    income_lay.setVisibility(View.VISIBLE);
                }
                web_info_tv.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        StringBuilder jumpUrl = new StringBuilder();
                        if (userBean.type == 1) {
                            jumpUrl.append("https://www.ky808.cn/carfriend/static/cyt/index.html#/purchase?token=");
                        } else {
                            jumpUrl.append("https://www.ky808.cn/carfriend/static/cyt/index.html#/upgrade?token=");
                        }
                        int max=100,min=1;
                        long randomNum = System.currentTimeMillis();
                        int ran3 = (int) (randomNum%(max-min)+min);
                        jumpUrl.append(KWApplication.getInstance().token).append("&").append(randomNum);
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra("url",jumpUrl.toString());
                        intent.putExtra("from","新手教程");
                        requireActivity().startActivity(intent);
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });

            }
        });
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
                        all_order_lay.setVisibility(View.VISIBLE);
                        wash_order_lay.setVisibility(View.VISIBLE);
                        oil_order_lay.setVisibility(View.VISIBLE);

                    }else if(showGas == 0 && showCarWash == 0){
                        all_order_lay.setVisibility(View.GONE);
                        wash_order_lay.setVisibility(View.GONE);
                        oil_order_lay.setVisibility(View.GONE);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(more_lay.getLayoutParams());
                        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_15)
                                ,getResources().getDimensionPixelSize(R.dimen.dp_90)
                                ,getResources().getDimensionPixelSize(R.dimen.dp_15)
                                ,getResources().getDimensionPixelSize(R.dimen.dp_20));
                        more_lay.setLayoutParams(layoutParams);

                    } else {

                        if (showCarWash == 1) {
                            wash_order_lay.setVisibility(View.VISIBLE);
                        }else{
                            wash_order_lay.setVisibility(View.GONE);
                        }
                        if (showGas == 1) {
                            oil_order_lay.setVisibility(View.VISIBLE);
                        } else {
                            oil_order_lay.setVisibility(View.GONE);
                        }
                        all_order_lay.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}