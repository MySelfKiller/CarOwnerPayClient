package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.ui.adapter.BottomNavigationViewHelper;
import com.kayu.car_owner_pay.update.UpdateCallBack;
import com.kayu.car_owner_pay.update.UpdateInfo;
import com.kayu.car_owner_pay.update.UpdateInfoParse;
import com.kayu.utils.AppUtil;
import com.kayu.utils.Constants;
import com.kayu.utils.Md5Util;
import com.kayu.utils.StringUtil;
import com.kayu.utils.permission.EasyPermissions;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.maning.updatelibrary.InstallUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    private MainViewModel mViewModel;
    private String apkDownloadPath;
    private InstallUtils.DownloadCallBack downloadCallBack;
    private MessageDialog progressDialog;
    private NumberProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setSelectedItemId(navView.getMenu().getItem(0).getItemId());

        BottomNavigationViewHelper.disableShiftMode(navView);
        navView.setItemIconTintList(null);//设置item图标颜色为null，当menu里icon设置selector的时候，
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        permissionsCheck();
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
                Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return;
            } else {
                appManager.finishAllActivity();
                finish();
            }
        } else{//取出我们返回栈保存的Fragment,这里会从栈顶开始弹栈
            getSupportFragmentManager().popBackStack();
        }

    }

    public void permissionsCheck() {
//        String[] perms = {Manifest.permission.CAMERA};
        String[] perms = needPermissions;

        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                mViewModel.sendOilPayInfo(MainActivity.this);
                reqUpdate();
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(MainActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) MainActivity.this);
                dialog.setStyle(DialogSettings.STYLE.STYLE_IOS);
                dialog.setTheme(DialogSettings.THEME.LIGHT);
                dialog.setTitle(getString(R.string.app_name));
                dialog.setMessage(getString(R.string.dialog_rationale_ask_again));
                dialog.setOkButton("设置", new OnDialogButtonClickListener() {

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
            messageDialog.setOkButton("下载");
            if (!isMustUpdate) {
                messageDialog.setCancelButton("取消");
                messageDialog.setCancelButton((baseDialog, v) -> {
                    messageDialog.doDismiss();
                    return false;
                });
            }
            messageDialog.setCancelable(!isMustUpdate);
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
                TipDialog.show(MainActivity.this,"下载失败", TipDialog.TYPE.ERROR);
            }

            @Override
            public void cancle() {
                progressDialog.doDismiss();
                TipDialog.show(MainActivity.this,"下载已取消", TipDialog.TYPE.ERROR);
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
                TipDialog.show(MainActivity.this,"安装失败", TipDialog.TYPE.ERROR);            }
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
}