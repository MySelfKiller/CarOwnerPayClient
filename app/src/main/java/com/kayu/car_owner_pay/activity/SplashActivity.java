package com.kayu.car_owner_pay.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.login.LoginAutoActivity;
import com.kayu.car_owner_pay.activity.login.SetPasswordActivity;
import com.kayu.car_owner_pay.config_ad.TTAdManagerHolder;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.model.SystemParamContent;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.ScreenUtils;
import com.kayu.utils.StringUtil;
import com.kayu.utils.status_bar_set.StatusBarUtil;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.util.AdError;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    //是否强制跳转到主页面
    private boolean mForceGoMain;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 3500;
    private String mCodeId = TTAdManagerHolder.splashID;
//    private boolean mIsExpress = false; //是否请求模板广告
    private LinearLayout splash_img;
    private boolean isLogin;
    private boolean isSetPsd;
    private MainViewModel mainViewModel;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1){
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(SplashActivity.this).get(MainViewModel.class);
        KWApplication.getInstance().displayWidth = ScreenUtils.getDisplayWidth(this);
        KWApplication.getInstance().displayHeight = ScreenUtils.getDisplayHeight(this);
//        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.dark));
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        StatusBarUtil.setTranslucentStatus(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_splash);

        mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
        splash_img = (LinearLayout) findViewById(R.id.splash_img_lay);

//        getExtraInfo();
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
//         TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
        isLogin = sp.getBoolean(Constants.isLogin, false);
        isSetPsd = sp.getBoolean(Constants.isSetPsd, false);
//        boolean isFirstShow = sp.getBoolean(Constants.isShowDialog, true);
        if (isLogin) {
            //step2:创建TTAdNative对象
            mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
            if (null != KWApplication.getInstance().systemArgs) {
                if (StringUtil.isEmpty(KWApplication.getInstance().systemArgs.android.showAd)) {
                    goToMainActivity();
                } else {
                    boolean isCsjAD;
                    if (KWApplication.getInstance().systemArgs.android.showAd.equals("csj")) {
                        isCsjAD = true;
                    } else {
                        isCsjAD = false;
                    }
                    loadSplashAd(isCsjAD);
                }
            } else {
                loadSplashAd(true);
            }

        }
        new Handler().postDelayed(runnable,1500*1);
//        permissionsCheck();
    }


//    private void getExtraInfo() {
//        Intent intent = getIntent();
//        if(intent == null) {
//            return;
//        }
//        String codeId = intent.getStringExtra("splash_rit");
//        if (!TextUtils.isEmpty(codeId)){
//            mCodeId = codeId;
//        }
//        mIsExpress = intent.getBooleanExtra("is_express", false);
//    }

    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            goToMainActivity();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }
    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity        展示广告的activity
     * @param adContainer     展示广告的大容器
     * @param posId           广告位ID
     * @param adListener      广告状态监听器
     * @param fetchDelay      拉取广告的超时时长：取值范围[1500, 5000]，设为0表示使用优量汇SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, String posId, SplashADListener adListener, int fetchDelay) {
//        fetchSplashADTime = System.currentTimeMillis();
        // skipContainer 此时必须是 VISIBLE 状态，否则将不能正常曝光计费
        SplashAD splashAD = new SplashAD(activity, posId, adListener, fetchDelay);
        splashAD.fetchAndShowIn(adContainer);
//        splashAD.fetchAdOnly();
    }
    /**
     * 加载开屏广告
     */
    private void loadSplashAd(boolean isCsjAD) {
        if (!isCsjAD) {
            String posid = "5042238596128420";
            if (null != KWApplication.getInstance().systemArgs && !StringUtil.isEmpty(KWApplication.getInstance().systemArgs.android.ylhSplashid))
                posid = KWApplication.getInstance().systemArgs.android.ylhSplashid;
            fetchSplashAD(SplashActivity.this, mSplashContainer, posid, adListener,AD_TIME_OUT);
        } else {
            //穿山甲广告
            //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
            AdSlot adSlot = null;
//            if (mIsExpress) {
//                //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
//                //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
//                //float expressViewWidth = UIUtils.getScreenWidthDp(this);
//                //float expressViewHeight = UIUtils.getHeight(this);
//                adSlot = new AdSlot.Builder()
//                        .setCodeId(mCodeId)
//                        //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
//                        //view宽高等于图片的宽高
////                    .setExpressViewAcceptedSize(KWApplication.getInstance().displayWidth, KWApplication.getInstance().displayHeight)
//                        .setExpressViewAcceptedSize(KWApplication.getInstance().displayWidth,KWApplication.getInstance().displayHeight)
//                        .build();
//            } else {
            if (null != KWApplication.getInstance().systemArgs && !StringUtil.isEmpty(KWApplication.getInstance().systemArgs.android.csjPlacementid))
                mCodeId = KWApplication.getInstance().systemArgs.android.csjPlacementid;
                adSlot = new AdSlot.Builder()
                        .setCodeId(mCodeId)
//                    .setImageAcceptedSize(KWApplication.getInstance().displayWidth, KWApplication.getInstance().displayHeight)
                        .setImageAcceptedSize(KWApplication.getInstance().displayWidth,KWApplication.getInstance().displayHeight)
                        .build();
//            }
            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
            mTTAdNative.loadSplashAd(adSlot, splashAdListener, AD_TIME_OUT);
        }
    }


    //穿山甲广告回调
    TTAdNative.SplashAdListener splashAdListener = new TTAdNative.SplashAdListener() {
        @Override
        @MainThread
        public void onError(int code, String message) {
            Log.d(TAG, String.valueOf(message));
//                ToastUtils.show(message);
            goToMainActivity();
        }

        @Override
        @MainThread
        public void onTimeout() {
//                ToastUtils.show("开屏广告加载超时");
            goToMainActivity();
        }

        @Override
        @MainThread
        public void onSplashAdLoad(TTSplashAd ad) {
            Log.d(TAG, "开屏广告请求成功");
            if (ad == null) {
                return;
            }
            //获取SplashView
            View view = ad.getSplashView();
            if (view != null && mSplashContainer != null && !SplashActivity.this.isFinishing()) {
//                    mSplashContainer.removeAllViews();
                //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                mSplashContainer.addView(view);
                //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                //ad.setNotAllowSdkCountdown();
            }else {
                goToMainActivity();
            }

            //设置SplashView的交互监听器
            ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    Log.d(TAG, "onAdClicked");
//                        ToastUtils.show("开屏广告点击");
                }

                @Override
                public void onAdShow(View view, int type) {
                    Log.d(TAG, "onAdShow");
//                        ToastUtils.show("开屏广告展示");
                }

                @Override
                public void onAdSkip() {
                    Log.d(TAG, "onAdSkip");
//                        ToastUtils.show("开屏广告跳过");
                    goToMainActivity();

                }

                @Override
                public void onAdTimeOver() {
                    Log.d(TAG, "onAdTimeOver");
//                        ToastUtils.show("开屏广告倒计时结束");
                    goToMainActivity();
                }
            });
            if(ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                ad.setDownloadListener(new TTAppDownloadListener() {
                    boolean hasShow = false;

                    @Override
                    public void onIdle() {
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!hasShow) {
//                                ToastUtils.show("下载中...");
                            hasShow = true;
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                            ToastUtils.show("下载暂停...");

                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                            ToastUtils.show("下载失败...");

                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                            ToastUtils.show("下载完成...");

                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
//                            ToastUtils.show("安装完成...");

                    }
                });
            }
        }
    };

    //腾讯广告回调
    SplashADListener adListener = new SplashADListener(){
        @Override
        public void onADPresent() {
//            Log.i("AD_DEMO", "SplashADPresent");
        }

        @Override
        public void onADClicked() {
//            Log.i("AD_DEMO", "SplashADClicked clickUrl: "
//                    + (splashAD.getExt() != null ? splashAD.getExt().get("clickUrl") : ""));
        }

        /**
         * 倒计时回调，返回广告还将被展示的剩余时间。
         * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
         *
         * @param millisUntilFinished 剩余毫秒数
         */
        @Override
        public void onADTick(long millisUntilFinished) {
//            Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        }

        @Override
        public void onADExposure() {
//            Log.i("AD_DEMO", "SplashADExposure");
        }

        @Override
        public void onADLoaded(long l) {

        }

        @Override
        public void onADDismissed() {
//            next();
            goToMainActivity();
        }

        @Override
        public void onNoAD(AdError error) {
//            goToMainActivity();
        }
    };


    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {

        Intent intent;
        if (isLogin) {
            if (isSetPsd){
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }else {
                intent = new Intent(SplashActivity.this, SetPasswordActivity.class);
                intent.putExtra("title","设置密码");
                intent.putExtra("back","");
                intent.putExtra("isSetPwd",true);
            }
        } else {
            intent = new Intent(SplashActivity.this, LoginAutoActivity.class);
        }
        Uri data = getIntent().getData();
        if (data != null) {
            intent.setData(data);
        }
//            intent.putExtra("from", "splash");
        startActivity(intent);
//        mSplashContainer.removeAllViews();
        this.finish();
    }

//    public void permissionsCheck() {
////        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
//        String[] perms = needPermissions;
//
//        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
//            @Override
//            public void hasPermission(List<String> allPerms) {
//                if (!LocationManagerUtil.getSelf().isLocServiceEnable()){
//                    MessageDialog.show(SplashActivity.this, "定位服务未开启", "请打开定位服务", "开启定位服务","取消").setCancelable(false)
//                            .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
//                                @Override
//                                public boolean onClick(BaseDialog baseDialog, View v) {
//                                    baseDialog.doDismiss();
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
////                                    appManager.finishAllActivity();
////                                    LocationManagerUtil.getSelf().stopLocation();
////                                    finish();
//                                    return true;
//                                }
//                            }).setCancelButton(new OnDialogButtonClickListener() {
//                        @Override
//                        public boolean onClick(BaseDialog baseDialog, View v) {
//
//                            return false;
//                        }
//                    });
//                }
//                LocationManagerUtil.getSelf().reStartLocation();
//                //加载开屏广告
//                loadSplashAd();
//            }
//
//            @Override
//            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
//                EasyPermissions.goSettingsPermissions(SplashActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
//            }
//
//            @Override
//            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
//                MessageDialog dialog = MessageDialog.build((AppCompatActivity) SplashActivity.this);
//                dialog.setTitle(getString(R.string.app_name));
//                dialog.setMessage(getString(R.string.permiss_location));
//                dialog.setOkButton("确定", new OnDialogButtonClickListener() {
//
//                    @Override
//                    public boolean onClick(BaseDialog baseDialog, View v) {
//                        callback.onGranted();
//                        return false;
//                    }
//                }).setCancelButton("取消", new OnDialogButtonClickListener() {
//                    @Override
//                    public boolean onClick(BaseDialog baseDialog, View v) {
//                        return false;
//                    }
//                });
//                dialog.setCancelable(false);
//
//                dialog.show();
//            }
//        });
//    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            splash_img.setVisibility(View.GONE);
            if (!isLogin) {
//                loadSplashAd();
                goToMainActivity();
            }
        }
    };

    private void reqSystemArgs(){
        mainViewModel.getSysParameter(SplashActivity.this, 10).observe(this, new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null == systemParam)
                    return;
                SharedPreferences.Editor editor = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE).edit();
                editor.putString(Constants.system_args, systemParam.content);
                editor.apply();
                editor.commit();
                KWApplication.getInstance().systemArgs = GsonHelper.fromJson(systemParam.content, SystemParamContent.class);
//                try {
//                    JSONObject jsonObject = new JSONObject(systemParam.content);
//                    int showGas = jsonObject.optInt("gas");
//                    int showCarWash = jsonObject.optInt("carwash");
//                    if (showGas == 1 && showCarWash == 1) {
//                        slidingTabLayout.setVisibility(View.VISIBLE);
//                        mViewPager.setCurrentItem(fragIndex);
//                        slidingTabLayout.setCurrentTab(fragIndex);
//                        mViewPager.setScrollble(true);
//                    } else if (showGas == 0 && showCarWash == 0) {
//                        slidingTabLayout.setVisibility(View.GONE);
//                        mViewPager.setVisibility(View.GONE);
//                    } else {
//                        slidingTabLayout.setVisibility(View.GONE);
//
//                        if (showCarWash == 1) {
//                            fragIndex = 1;
//                            mViewPager.setCurrentItem(fragIndex);
//                            mViewPager.setScrollble(false);
//                        } else if (showGas == 1) {
//                            fragIndex = 0;
//                            mViewPager.setCurrentItem(fragIndex);
//                            mViewPager.setScrollble(false);
//                        }
//                    }
//                    loadChildData();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}
