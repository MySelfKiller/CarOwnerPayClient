package com.kayu.car_owner_pay.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.kayu.utils.ImageUtil;
import com.kayu.utils.LogUtil;
import com.kayu.utils.Md5Util;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.Callback;
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
        //?????????????????????
        //???FitsSystemWindows?????? true ?????????????????????????????????????????????????????? padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //?????????????????????
        StatusBarUtil.setTranslucentStatus(this);

        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.nav_view);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        permissionsCheck();

        //?????????????????????
        mViewModel.getUserRole(MainActivity.this).observe(MainActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                KWApplication.getInstance().userRole = integer;
            }
        });
        mViewModel.getParamSelect(MainActivity.this);
        mViewModel.getParamWash(MainActivity.this);
    }

    private List<Fragment> getFragments(){
        List<Fragment> list = new ArrayList<Fragment>();
        list.add(new HomeFragment(navigation));
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

    // ????????????fragment
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
    //??????????????????????????????????????????
    private long firstTime = 0;
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() <= 0){//????????????????????????????????????Fragment?????????
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtils.show("????????????????????????");
                firstTime = secondTime;
                return;
            } else {
                appManager.finishAllActivity();
                LocationManagerUtil.getSelf().stopLocation();
                LocationManagerUtil.getSelf().destroyLocation();
                finish();
                System.exit(0);
            }
        } else{//??????????????????????????????Fragment,??????????????????????????????
            getSupportFragmentManager().popBackStack();
        }

    }


    private void showPermissTipsDialog(){
        MessageDialog.show(MainActivity.this, "????????????????????????", getString(R.string.permiss_location), "?????????","").setCancelable(false)
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        baseDialog.doDismiss();
//                        permissionsCheck();
                        return true;
                    }
                });
    }
    public void permissionsCheck() {
//        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        String[] perms = needPermissions;

        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                mViewModel.sendOilPayInfo(MainActivity.this);
//                if (!LocationManagerUtil.getSelf().isLocServiceEnable()){
//                    MessageDialog.show(MainActivity.this, "?????????????????????", getString(R.string.permiss_location), "??????????????????","??????").setCancelable(false)
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
                reqUpdate();
                if (!mHasShowOnce1)
                    reqActivityData(38);
                FragmentManager fragmentManager = getSupportFragmentManager();
                NavigationAdapter navigationAdapter = new NavigationAdapter(fragmentManager,getFragments());
                view_pager.addOnPageChangeListener(MainActivity.this);
                view_pager.setOffscreenPageLimit(2);
                view_pager.setAdapter(navigationAdapter);
                BottomNavigationViewHelper.disableShiftMode(navigation);
                navigation.setItemIconTintList(null);//??????item???????????????null??????menu???icon??????selector????????????
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                LocationManagerUtil.getSelf().reStartLocation();

            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                reqUpdate();
                if (!mHasShowOnce1)
                    reqActivityData(38);
                FragmentManager fragmentManager = getSupportFragmentManager();
                NavigationAdapter navigationAdapter = new NavigationAdapter(fragmentManager,getFragments());
                view_pager.addOnPageChangeListener(MainActivity.this);
                view_pager.setOffscreenPageLimit(2);
                view_pager.setAdapter(navigationAdapter);
                BottomNavigationViewHelper.disableShiftMode(navigation);
                navigation.setItemIconTintList(null);//??????item???????????????null??????menu???icon??????selector????????????
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//                EasyPermissions.goSettingsPermissions(MainActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
//                showPermissTipsDialog();
//                MessageDialog.show(MainActivity.this, "????????????????????????", getString(R.string.permiss_location), "?????????","").setCancelable(false)
//                        .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
//                            @Override
//                            public boolean onClick(BaseDialog baseDialog, View v) {
//                                baseDialog.doDismiss();
//                                EasyPermissions.goSettingsPermissions(MainActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
////                        permissionsCheck();
//                                return true;
//                            }
//                        });
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) MainActivity.this);
                dialog.setTitle("????????????????????????");
                dialog.setMessage(getString(R.string.permiss_location));
                dialog.setOkButton("?????????", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
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


    public void permissionsCheck(String[] perms, int resId, @NonNull Callback callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                callback.onSuccess();
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(MainActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) MainActivity.this);
                dialog.setTitle("????????????????????????");
                dialog.setMessage(getString(resId));
                dialog.setOkButton("?????????", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
                        return false;
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    public void updateDialog(boolean isMustUpdate, final String apkName){
        final File file = new File(KWApplication.getInstance().getDataPath() + File.separator + "apk" + File.separator + apkName);
        MessageDialog messageDialog = MessageDialog.build(MainActivity.this);
        String filMd5 = Md5Util.getFileMD5(file);
        boolean md5Eq = StringUtil.equals(filMd5,updateInfo.pathMd5);
        long fileLength = file.length();
        boolean lengthEq = fileLength == updateInfo.pathLength;
        if ( file.exists() && lengthEq && md5Eq ) {
            messageDialog.setTitle("??????APP");
            messageDialog.setMessage("??????????????????,????????????");
            messageDialog.setOkButton("??????");
            if (!isMustUpdate) {
                messageDialog.setCancelButton("??????");
                messageDialog.setCancelButton((baseDialog, v) -> {
                    messageDialog.doDismiss();
                    return false;
                });
            }
            messageDialog.setCancelable(!isMustUpdate);
            messageDialog.setOkButton((baseDialog, v) -> {
                messageDialog.doDismiss();
                permissionsCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string.permiss_write_store1,new Callback() {
                    @Override
                    public void onSuccess() {
                        installApk(file.getAbsolutePath());
                    }

                    @Override
                    public void onError() {

                    }
                });

                return false;
            });
        } else {
            if (hasSave() && file.exists())
                file.delete();
            final String md5 = updateInfo.pathMd5;
            final String url = updateInfo.url;
            messageDialog.setTitle("???????????????");
            messageDialog.setMessage(updateInfo.content);
            messageDialog.setOkButton("??????");
            if (!isMustUpdate) {
                messageDialog.setCancelButton("??????");
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
                    permissionsCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string.permiss_write_store1,new Callback() {
                        @Override
                        public void onSuccess() {
                            SharedPreferences userSettings = getSharedPreferences(Constants.SharedPreferences_name, 0);
                            SharedPreferences.Editor editor = userSettings.edit();
                            editor.putString("update_md5", md5);
                            editor.apply();
                            editor.commit();
                            initCallBack();
                            showProgressDialog(isMustUpdate);
                            InstallUtils.with(MainActivity.this)
                                    //??????-????????????
                                    .setApkUrl(updateInfo.url)
                                    //?????????-????????????????????????????????????+name.apk
                                    .setApkPath(KWApplication.getInstance().getDataPath() + File.separator + "apk" + File.separator + apkName)
                                    //?????????-????????????
                                    .setCallBack(downloadCallBack)
                                    //????????????
                                    .startDownload();
                        }

                        @Override
                        public void onError() {

                        }
                    });
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
        progressDialog.setTitle("?????????...");
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

                //??????????????????????????????
                InstallUtils.checkInstallPermission(MainActivity.this, new InstallUtils.InstallPermissionCallBack() {
                    @Override
                    public void onGranted() {
                        //?????????APK
                        installApk(apkDownloadPath);
                    }

                    @Override
                    public void onDenied() {
                        //????????????????????????
                        MessageDialog.show(MainActivity.this,"??????????????????","????????????????????????APK????????????????????????","??????")
                                .setCancelable(false)
                                .setOkButton(new OnDialogButtonClickListener() {
                                    @Override
                                    public boolean onClick(BaseDialog baseDialog, View v) {
                                        baseDialog.doDismiss();
                                        //??????????????????
                                        InstallUtils.openInstallPermissionSetting(MainActivity.this, new InstallUtils.InstallPermissionCallBack() {
                                            @Override
                                            public void onGranted() {
                                                //?????????APK
                                                installApk(apkDownloadPath);
                                            }

                                            @Override
                                            public void onDenied() {
                                                //????????????????????????
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
                //?????????????????????onLoading ????????????progress?????????+1?????????????????????
                int progress = (int) (current * 100 / total);
                progressbar.setProgress(progress);
            }

            @Override
            public void onFail(Exception e) {
                progressDialog.doDismiss();
                LogUtil.e("hm","????????????"+e.toString());
                TipGifDialog.show(MainActivity.this,"????????????", TipGifDialog.TYPE.ERROR);
            }

            @Override
            public void cancle() {
                progressDialog.doDismiss();
                TipGifDialog.show(MainActivity.this,"???????????????", TipGifDialog.TYPE.ERROR);
            }
        };
    }

    private void installApk(String path) {
        InstallUtils.installAPK(MainActivity.this, path, new InstallUtils.InstallCallBack() {
            @Override
            public void onSuccess() {
                //onSuccess???????????????????????????????????????
                //??????????????????????????????????????????????????????????????????????????????????????????
                appManager.finishAllActivity();
                finish();
            }

            @Override
            public void onFail(Exception e) {
                TipGifDialog.show(MainActivity.this,"????????????", TipGifDialog.TYPE.ERROR);            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //????????????,?????????????????????????????????????????????????????????
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
    private boolean mHasShowOnce1 = false;// ???????????????????????????????????? ????????????
    private boolean mHasShowOnce2 = false;// ???????????????????????????????????? ??????????????????
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
                //??????????????????????????????
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