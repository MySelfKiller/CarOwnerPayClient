package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.login.LoginAutoActivity;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.model.UserBean;
import com.kayu.utils.AppUtil;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.ImageUtil;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.ImageCallback;
import com.kayu.utils.location.LocationManagerUtil;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;

public class SettingsActivity extends BaseActivity {
    private TextView app_version,app_new_version;
    private Button sign_out;
    private UserBean useData;
    private TextView user_name;
    private SystemParam mParamet;
    private MainViewModel mainViewModel;
    private CustomDialog dialog;
    private ItemCallback itemCallback = new ItemCallback() {
        @Override
        public void onItemCallback(int position, Object obj) {
            SystemParam systemParam1 = (SystemParam)obj;
            if (null != systemParam1){
                showPop();
            }
        }

        @Override
        public void onDetailCallBack(int position, Object obj) { }
    };
    private TextView user_agreement;
    private TextView user_privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mainViewModel = ViewModelProviders.of(SettingsActivity.this).get(MainViewModel.class);

        SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
        String jsonUser = sp.getString(Constants.userInfo,"");
        useData = GsonHelper.fromJson(jsonUser, UserBean.class);

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("设置");

        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
//        TextView back_tv = view.findViewById(R.id.title_back_tv);
//        back_tv.setText("我的");

        String version = AppUtil.getVersionName(SettingsActivity.this);

        user_name = findViewById(R.id.setting_user_name_tv);

        app_version = findViewById(R.id.setting_app_version_tv);
        app_new_version = findViewById(R.id.setting_app_new_version_tv);
        app_new_version.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
//                reqUpdate();
                showPop();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        app_version.setText(version);
        initViewData();
        reqAppDown(null);
        sign_out = findViewById(R.id.setting_sign_out_tv);
        sign_out.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                MessageDialog.show(SettingsActivity.this,getResources().getString(R.string.app_name),"确定需要退出登录？","退出登录","取消")
                        .setOkButton(new OnDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v) {
//                                SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean(Constants.isLogin, false);
                                editor.putString(Constants.userInfo, "");
                                editor.apply();
                                editor.commit();
                                AppManager.getAppManager().finishAllActivity();
                                LocationManagerUtil.getSelf().stopLocation();
//                        LocationManager.getSelf().destroyLocation();
                                startActivity(new Intent(SettingsActivity.this, LoginAutoActivity.class));
                                finish();
                                return false;
                            }
                        });
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        user_agreement = findViewById(R.id.setting_user_agreement_tv);
        user_privacy = findViewById(R.id.setting_user_privacy_tv);
//        WaitDialog.show((AppCompatActivity) requireContext(),"请稍等");
        mainViewModel.getParameter(SettingsActivity.this,3).observe(SettingsActivity.this, new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
//                WaitDialog.dismiss();
                if (null != systemParam && systemParam.type ==3){
                    String[] titles = systemParam.title.split("@@");
                    String[] urls = systemParam.url.split("@@");
                    user_agreement.setText(titles[0]);
                    user_privacy.setText(titles[1]);
                    user_agreement.setOnClickListener(new NoMoreClickListener() {
                        @Override
                        protected void OnMoreClick(View view) {
                            jumpWeb(titles[0],urls[0]);
                        }

                        @Override
                        protected void OnMoreErrorClick() {

                        }
                    });
                    user_privacy.setOnClickListener(new NoMoreClickListener() {
                        @Override
                        protected void OnMoreClick(View view) {
                            jumpWeb(titles[1],urls[1]);
                        }

                        @Override
                        protected void OnMoreErrorClick() {

                        }
                    });
                }

            }
        });

    }

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        mainViewModel = ViewModelProviders.of(SettingsFragment.this).get(MainViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_setting, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    private void jumpWeb(String title, String url){
        Intent intent = new Intent(SettingsActivity.this, WebViewActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("from",title);
        startActivity(intent);
    }

    Bitmap qrcodeBitmap = null;
    private void showPop() {
        SystemParam systemParam = null;
        if (null == mParamet){
            reqAppDown(itemCallback);
        }else {
            systemParam = mParamet;
            if (StringUtil.isEmpty(systemParam.url)){
                MessageDialog.show(SettingsActivity.this,"提示","地址链接错误","重新获取","取消")
                        .setCancelable(false)
                        .setOkButton(new OnDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v) {
                                baseDialog.doDismiss();
                                reqAppDown(itemCallback);
                                return false;
                            }
                        });
                return;
            }

            initPopView();
            KWApplication.getInstance().loadImg(systemParam.url,qrcode_iv,new ImageCallback() {
                @Override
                public void onSuccess(Bitmap resource) {
                    qrcodeBitmap = resource;
                    creatPopWindow(popview);
                    showWindo();
                }

                @Override
                public void onError() {

                }
            });

            save_btn.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void OnMoreClick(View view) {
                    if (null == qrcodeBitmap) {
                        Toast.makeText(SettingsActivity.this,"保存图片不存在",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
                    boolean isSaveSuccess = ImageUtil.saveImageToGallery(SettingsActivity.this, qrcodeBitmap,fileName);
                    if (isSaveSuccess) {
                        Toast.makeText(SettingsActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                protected void OnMoreErrorClick() {

                }
            });
            if (TextUtils.isEmpty(systemParam.title)){
                compay_tv1.setVisibility(View.GONE);
            }else {
                compay_tv1.setText(systemParam.title);
                compay_tv1.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initViewData() {
        if (null != useData){
            user_name.setText(useData.username);
        }
    }

    private View popview;
    private ImageView qrcode_iv;
    private Button save_btn;
    private TextView compay_tv1;

    private void initPopView() {
        final ViewGroup nullParent = null;
        popview = getLayoutInflater().inflate(R.layout.qrcode_lay, nullParent);
        qrcode_iv = popview.findViewById(R.id.shared_qrcode_iv);
        save_btn = popview.findViewById(R.id.shared_call_btn);
        compay_tv1 = popview.findViewById(R.id.shared_compay_tv1);
    }
    private void creatPopWindow(View view) {
        dialog = CustomDialog.build(SettingsActivity.this, view).setCancelable(true);
    }

    private void showWindo(){
        if (null !=dialog && !dialog.isShow)
            dialog.show();

    }

    @SuppressLint("HandlerLeak")
    private void reqAppDown(final ItemCallback itemCallback) {
        if (null != itemCallback){
            WaitDialog.show(SettingsActivity.this,"请稍等");
        }
        mainViewModel.getParameter(SettingsActivity.this,1).observe(SettingsActivity.this, new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null != itemCallback ){
                    WaitDialog.dismiss();
                }
                if (null != systemParam&& systemParam.type ==1){
                    mParamet = systemParam;
                    if (null != itemCallback){
                        itemCallback.onItemCallback(0,mParamet);
                    }
                }

            }
        });
//        RequestInfo reques = new RequestInfo();
//        reques.context = getContext();
//        reques.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_GET_PARAMETER;
//        HashMap<String,Object> dataMap = new HashMap<>();
//        dataMap.put("type",1);
//        reques.reqDataMap = dataMap;
//        reques.parser = new ParameterDataParser();
//        reques.handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                ResponseInfo resInfo = (ResponseInfo)msg.obj;
//                if (null != itemCallback ){
//                    WaitDialog.dismiss();
//                }
//                if (resInfo.status ==1){
//                    mParamet = (SystemParam) resInfo.responseData;
//                    if (null != itemCallback){
//                        itemCallback.onItemCallback(0,mParamet);
//                    }
//                }else {
//                    Toast.makeText(getContext(),resInfo.msg,Toast.LENGTH_SHORT).show();
//                }
//                super.handleMessage(msg);
//            }
//        };
//        ResponseCallback callback = new ResponseCallback(reques);
//        ReqUtil.getInstance().setReqInfo(reques);
//        ReqUtil.getInstance().requestPostJSON(callback);
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
}