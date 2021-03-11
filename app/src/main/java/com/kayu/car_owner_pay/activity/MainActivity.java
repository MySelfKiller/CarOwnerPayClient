package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.ui.HomeFragment;
import com.kayu.car_owner_pay.ui.PersonalFragment;
import com.kayu.car_owner_pay.ui.adapter.BottomNavigationViewHelper;
import com.kayu.car_owner_pay.update.UpdateCallBack;
import com.kayu.car_owner_pay.update.UpdateInfo;
import com.kayu.car_owner_pay.update.UpdateInfoParse;
import com.kayu.utils.AppUtil;
import com.kayu.utils.Constants;
import com.kayu.utils.LogUtil;
import com.kayu.utils.Md5Util;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.ImageCallback;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.permission.EasyPermissions;
import com.kayu.utils.status_bar_set.StatusBarUtil;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipGifDialog;
import com.maning.updatelibrary.InstallUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private ViewPager view_pager;
    private MainViewModel mViewModel;
    private String apkDownloadPath;
    private InstallUtils.DownloadCallBack downloadCallBack;
    private MessageDialog progressDialog;
    private NumberProgressBar progressbar;
    private int lastSelectItemid;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            LogUtil.e("hm","NavigationItemSelected position="+item.getItemId());
            if (lastSelectItemid == item.getItemId()){
                return true;
            }
            lastSelectItemid = item.getItemId();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    view_pager.setCurrentItem(0);
                    break;
                case R.id.navigation_personal:
                    view_pager.setCurrentItem(1);
                    break;
            }
            return true;
        }
    };
    private BottomNavigationView navigation;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.nav_view);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        permissionsCheck();
    }

    private List<Fragment> getFragments(){
        List<Fragment> list = new ArrayList<Fragment>();
        list.add(new HomeFragment());
        list.add(new PersonalFragment());
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_PERMISSION_BASE) {
            permissionsCheck();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        for (Fragment fragment : fragmentManager.getFragments()) {
            processAllFragment(fragment, requestCode, resultCode, data);
        }
    }

    // 遍历所有fragment
    private void processAllFragment(Fragment fragment, int requestCode, int resultCode, @Nullable Intent data) {
        if (fragment == null) {
            return;
        }
        for (Fragment childFragment : fragment.getChildFragmentManager().getFragments()) {
            processAllFragment(childFragment, requestCode, resultCode, data);
        }
        if (!fragment.isAdded() || fragment.isDetached()) {
            return;
        }
        fragment.onActivityResult(requestCode, resultCode, data);
    }
    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() <= 0){//这里是取出我们返回栈存在Fragment的个数
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtils.show("再按一次退出应用");
                firstTime = secondTime;
                return;
            } else {
                appManager.finishAllActivity();
                LocationManagerUtil.getSelf().stopLocation();
                LocationManagerUtil.getSelf().destroyLocation();
                finish();
                System.exit(0);
            }
        } else{//取出我们返回栈保存的Fragment,这里会从栈顶开始弹栈
            getSupportFragmentManager().popBackStack();
        }

    }

    public void permissionsCheck() {
//        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        String[] perms = needPermissions;

        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                mViewModel.sendOilPayInfo(MainActivity.this);
                if (!LocationManagerUtil.getSelf().isLocServiceEnable()){
                    MessageDialog.show(MainActivity.this, "定位服务未开启", "请打开定位服务", "开启定位服务","取消").setCancelable(false)
                            .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v) {
                                    baseDialog.doDismiss();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
//                                    appManager.finishAllActivity();
//                                    LocationManagerUtil.getSelf().stopLocation();
//                                    finish();
                                    return true;
                                }
                            }).setCancelButton(new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {

                            return false;
                        }
                    });
                }
                reqUpdate();
                if (!mHasShowOnce1)
                    reqActivityData(38);
                FragmentManager fragmentManager = getSupportFragmentManager();
                NavigationAdapter navigationAdapter = new NavigationAdapter(fragmentManager,getFragments());
                view_pager.addOnPageChangeListener(MainActivity.this);
                view_pager.setOffscreenPageLimit(2);
                view_pager.setAdapter(navigationAdapter);
                BottomNavigationViewHelper.disableShiftMode(navigation);
                navigation.setItemIconTintList(null);//设置item图标颜色为null，当menu里icon设置selector的时候，
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                LocationManagerUtil.getSelf().reStartLocation();

            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(MainActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) MainActivity.this);
                dialog.setTitle(getString(R.string.app_name));
                dialog.setMessage(getString(R.string.permiss_location));
                dialog.setOkButton("确定", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
                        return false;
                    }
                }).setCancelButton("取消", new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        return false;
                    }
                });
                dialog.setCancelable(false);

                dialog.show();
            }
        });
    }

    private UpdateInfo updateInfo = null;

    @SuppressLint("HandlerLeak")
    private void reqUpdate() {
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = MainActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_CHECK_UPDAGE;
        reqInfo.parser = new UpdateInfoParse();
        File file = new File(KWApplication.getInstance().getDataPath() + File.separator + "apk" + File.separator);
        if (!file.exists())
            file.mkdirs();

        reqInfo.reqDataMap = new HashMap<String, Object>();
        reqInfo.reqDataMap.put("version", AppUtil.getVersionName(this));
        reqInfo.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                if (resInfo.status == 1) {
                    updateInfo = (UpdateInfo) resInfo.responseData;
                    String[] ss = updateInfo.url.split("/");
                    String apkNme = ss[ss.length-1];
                    boolean mustUpdata = false;
                    boolean hasUpdata = false;
                    if (updateInfo.force == 1) {
                        hasUpdata = true;
                    } else if (updateInfo.force == 2) {
                        mustUpdata = true;
                    }
                    if (hasUpdata || mustUpdata) {
                        updateDialog(mustUpdata,apkNme);
                    }
                }
            }
        };
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(new UpdateCallBack(reqInfo));
    }

    public void updateDialog(boolean isMustUpdate, final String apkName){
        final File file = new File(KWApplication.getInstance().getDataPath() + File.separator + "apk" + File.separator + apkName);
        MessageDialog messageDialog = MessageDialog.build(MainActivity.this);
        String filMd5 = Md5Util.getFileMD5(file);
        boolean md5Eq = StringUtil.equals(filMd5,updateInfo.pathMd5);
        long fileLength = file.length();
        boolean lengthEq = fileLength == updateInfo.pathLength;
        if ( file.exists() && lengthEq && md5Eq ) {
            messageDialog.setTitle("更新APP");
            messageDialog.setMessage("新版本已下载,请安装！");
            messageDialog.setOkButton("安装");
            if (!isMustUpdate) {
                messageDialog.setCancelButton("取消");
                messageDialog.setCancelButton((baseDialog, v) -> {
                    messageDialog.doDismiss();
                    return false;
                });
            }
            messageDialog.setCancelable(!isMustUpdate);
            messageDialog.setOkButton((baseDialog, v) -> {
                messageDialog.doDismiss();
                installApk(file.getAbsolutePath());
                return false;
            });
        } else {
            if (hasSave() && file.exists())
                file.delete();
            final String md5 = updateInfo.pathMd5;
            final String url = updateInfo.url;
            messageDialog.setTitle("检测到新版");
            messageDialog.setMessage(updateInfo.content);
            messageDialog.setOkButton("升级");
            if (!isMustUpdate) {
                messageDialog.setCancelButton("取消");
                messageDialog.setCancelButton((baseDialog, v) -> {
                    messageDialog.doDismiss();
                    return false;
                });
            }
            messageDialog.setCancelable(false);
            messageDialog.setOkButton(new OnDialogButtonClickListener() {
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                    messageDialog.doDismiss();
                    SharedPreferences userSettings = getSharedPreferences(Constants.SharedPreferences_name, 0);
                    SharedPreferences.Editor editor = userSettings.edit();
                    editor.putString("update_md5", md5);
                    editor.apply();

                    initCallBack();
                    showProgressDialog(isMustUpdate);
                    InstallUtils.with(MainActivity.this)
                            //必须-下载地址
                            .setApkUrl(updateInfo.url)
                            //非必须-下载保存的文件的完整路径+name.apk
                            .setApkPath(KWApplication.getInstance().getDataPath() + File.separator + "apk" + File.separator + apkName)
                            //非必须-下载回调
                            .setCallBack(downloadCallBack)
                            //开始下载
                            .startDownload();
                    return false;
                }
            });
        }
        messageDialog.show();
    }

    private boolean hasSave() {
        SharedPreferences userSettings = getSharedPreferences(Constants.SharedPreferences_name, 0);
        String md5 = userSettings.getString("update_md5", null);
        return StringUtil.equals(md5,updateInfo.pathMd5);
    }

    private void showProgressDialog(boolean isMustUpdate){
        progressDialog = MessageDialog.build(MainActivity.this);
        progressDialog.setTitle("下载中...");
        progressDialog.setOkButton((String) null);
        progressDialog.setCustomView(R.layout.progress_lay, new MessageDialog.OnBindView() {
            @Override
            public void onBind(MessageDialog dialog, View v) {
                progressbar = v.findViewById(R.id.progressbar);
            }
        });
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initCallBack() {
        downloadCallBack = new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                progressbar.setProgress(0);
            }

            @Override
            public void onComplete(String path) {
                apkDownloadPath = path;
                progressbar.setProgress(100);
                progressDialog.doDismiss();

                //先判断有没有安装权限
                InstallUtils.checkInstallPermission(MainActivity.this, new InstallUtils.InstallPermissionCallBack() {
                    @Override
                    public void onGranted() {
                        //去安装APK
                        installApk(apkDownloadPath);
                    }

                    @Override
                    public void onDenied() {
                        //弹出弹框提醒用户
                        MessageDialog.show(MainActivity.this,"安装授权提示","必须授权才能安装APK，请设置允许安装","设置")
                                .setCancelable(false)
                                .setOkButton(new OnDialogButtonClickListener() {
                                    @Override
                                    public boolean onClick(BaseDialog baseDialog, View v) {
                                        baseDialog.doDismiss();
                                        //打开设置页面
                                        InstallUtils.openInstallPermissionSetting(MainActivity.this, new InstallUtils.InstallPermissionCallBack() {
                                            @Override
                                            public void onGranted() {
                                                //去安装APK
                                                installApk(apkDownloadPath);
                                            }

                                            @Override
                                            public void onDenied() {
                                                //还是不允许咋搞？
                                                appManager.finishAllActivity();
                                                finish();
                                            }
                                        });
                                        return false;
                                    }
                                });
                    }
                });
            }

            @Override
            public void onLoading(long total, long current) {
                //内部做了处理，onLoading 进度转回progress必须是+1，防止频率过快
                int progress = (int) (current * 100 / total);
                progressbar.setProgress(progress);
            }

            @Override
            public void onFail(Exception e) {
                progressDialog.doDismiss();
                LogUtil.e("hm","下载失败"+e.toString());
                TipGifDialog.show(MainActivity.this,"下载失败", TipGifDialog.TYPE.ERROR);
            }

            @Override
            public void cancle() {
                progressDialog.doDismiss();
                TipGifDialog.show(MainActivity.this,"下载已取消", TipGifDialog.TYPE.ERROR);
            }
        };
    }

    private void installApk(String path) {
        InstallUtils.installAPK(this, path, new InstallUtils.InstallCallBack() {
            @Override
            public void onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                appManager.finishAllActivity();
                finish();
            }

            @Override
            public void onFail(Exception e) {
                TipGifDialog.show(MainActivity.this,"安装失败", TipGifDialog.TYPE.ERROR);            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置监听,防止其他页面设置回调后当前页面回调失效
//        if (InstallUtils.isDownloading()) {
//            InstallUtils.setDownloadCallBack(downloadCallBack);
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onCleared();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int selectedItemId = 0;
        switch (position){
            case 0:
                selectedItemId = R.id.navigation_home;
                if (!mHasShowOnce1)
                    reqActivityData(38);
                break;

            case 1:
                selectedItemId = R.id.navigation_personal;
                if (!mHasShowOnce2)
                    reqActivityData(39);
                break;
        }
//        LogUtil.e("hm","viewPage getSelectedItemId="+navigation.getSelectedItemId());
//        LogUtil.e("hm","viewPage selectedItemId="+selectedItemId);
        if (navigation.getSelectedItemId()!= selectedItemId){
            navigation.setSelectedItemId(selectedItemId);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressLint("HandlerLeak")
    private void reqActivityData(int type) {
        if (type == 38) {
            mViewModel.getHomeActivity(MainActivity.this).observe(MainActivity.this, new Observer<SystemParam>() {
                @Override
                public void onChanged(SystemParam systemParam) {
                    if (null == systemParam)
                        return;
                    initActivityView(systemParam,type);
                }
            });

        }else if (type ==39){
            mViewModel.getSettingActivity(MainActivity.this).observe(MainActivity.this, new Observer<SystemParam>() {
                @Override
                public void onChanged(SystemParam systemParam) {
                    if (null == systemParam)
                        return;
                    initActivityView(systemParam,type);
                }
            });
        }
    }
//    private CustomPopupWindow popWindow;
    private boolean mHasShowOnce1 = false;// 活动弹框已经显示过一次了 首页展示
    private boolean mHasShowOnce2 = false;// 活动弹框已经显示过一次了 个人中心展示
    private void initActivityView(SystemParam systemParam, int type) {
        if (null == systemParam){
            return;
        }
        if (type == 38 && !mHasShowOnce1){
            showPopvView(type,systemParam.title,systemParam.url,systemParam.content);
        }else if (type == 39 && !mHasShowOnce2){
            showPopvView(type,systemParam.title,systemParam.url,systemParam.content);
        }

    }
    private void showPopvView(int type,String jumpTitle, String jumpUrl,String imgUrl){
        if (StringUtil.isEmpty(imgUrl))
            return;
//        final View customView = getLayoutInflater().inflate(R.layout.activity_activity_layout,null);

        KWApplication.getInstance().loadImg(imgUrl, null, new ImageCallback() {

            @Override
            public void onSuccess(Bitmap resource) {
                //对于已实例化的布局：
                customDialog = CustomDialog.show(MainActivity.this, R.layout.activity_activity_layout, new CustomDialog.OnBindView() {
                    @Override
                    public void onBind(final CustomDialog dialog, View v) {
                        ImageView showAcy = v.findViewById(R.id.act_show_img);
                        showAcy.setImageBitmap(resource);
//                ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(resource.getWidth(),resource.getHeight());
//                showAcy.setLayoutParams(params1);
                        showAcy.setOnClickListener(new NoMoreClickListener() {
                            @Override
                            protected void OnMoreClick(View view) {
                                if (!StringUtil.isEmpty(jumpUrl)){
                                    Intent intent= new Intent(MainActivity.this, WebViewActivity.class);
                                    intent.putExtra("url",jumpUrl);
                                    intent.putExtra("from",jumpTitle);
                                    startActivity(intent);
                                }
                                customDialog.doDismiss();
                            }

                            @Override
                            protected void OnMoreErrorClick() {

                            }
                        });
                        ImageView closeAct = v.findViewById(R.id.act_close_img);
                        closeAct.setOnClickListener(new NoMoreClickListener() {
                            @Override
                            protected void OnMoreClick(View view) {
                                customDialog.doDismiss();
                            }

                            @Override
                            protected void OnMoreErrorClick() {

                            }
                        });
                    }
                }).setCancelable(false).setFullScreen(false).setCustomLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (type == 38) {
                    mHasShowOnce1 = true;
                } else if (type == 39) {
                    mHasShowOnce2 = true;
                }
            }

            @Override
            public void onError() {
                if (type == 38) {
                    mHasShowOnce1 = true;
                } else if (type == 39) {
                    mHasShowOnce2 = true;
                }
            }
        });
    }

}