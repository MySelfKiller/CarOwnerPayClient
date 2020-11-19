package com.kayu.car_owner_pay.activity;

import android.Manifest;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.utils.ScreenUtils;
import com.kayu.utils.permission.EasyPermissions;
import com.kayu.utils.status_bar_set.StatusBarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionWithDialogCallbacks{

    /**
     * activity堆栈管理
     */
    protected AppManager appManager = AppManager.getAppManager();

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
//        setStatusBar();
//        if(NavigationBarUtil.checkNavigationBarShow(this)){
//            NavigationBarUtil.initActivity(findViewById(android.R.id.content));
//        }

        appManager.addActivity(this);
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));
//        StatusBarUtil.

//        StatusBarUtil.setTranslucent(this, 0);
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.white));
        KWApplication.getInstance().displayWidth = ScreenUtils.getDisplayWidth(this);
        KWApplication.getInstance().displayHeight = ScreenUtils.getDisplayHeight(this);
//        int displayHeight = CommonUtils.getDisplayHeight(this);
//        LogUtil.e("screen","displayWidth："+KWApplication.getInstance().displayWidth
//                +" displayHeight："+displayHeight);
//        setContentView(R.layout.activity_abs);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void setStatusBar() {
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.dark_grey));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 从栈中移除activity
        appManager.finishActivity(this);
    }

    /**
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * Android6.0权限控制
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     */

    /**
     * 需要进行检测的权限数组
     */
    public String[] needPermissions = {
            Manifest.permission.CALL_PHONE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.READ_CALL_LOG,
//            Manifest.permission.WRITE_CALL_LOG,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.CAMERA

    };
    private Map<Integer, PermissionCallback> mPermissonCallbacks = null;
    private Map<Integer, String[]> mPermissions = null;

    public interface PermissionCallback {
        /**
         * has all permission
         *
         * @param allPerms all permissions
         */
        void hasPermission(List<String> allPerms);

        /**
         * denied some permission
         *
         * @param deniedPerms          denied permission
         * @param grantedPerms         granted permission
         * @param hasPermanentlyDenied has permission denied permanently
         */
        void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied);

        /**
         * @param dialogType dialogType
         * @param callback   callback from easypermissions
         */
        void showDialog(int dialogType, EasyPermissions.DialogCallback callback);
    }

    /**
     * request permission
     *
     * @param dialogType  dialogType
     * @param requestCode requestCode
     * @param perms       permissions
     * @param callback    callback
     */
    public void performCodeWithPermission(int dialogType,
                                          final int requestCode, @NonNull String[] perms, @NonNull PermissionCallback callback) {
        if (EasyPermissions.hasPermissions(this, perms)) {
            callback.hasPermission(Arrays.asList(perms));
        } else {
            if (mPermissonCallbacks == null) {
                mPermissonCallbacks = new HashMap<>();
            }
            mPermissonCallbacks.put(requestCode, callback);

            if (mPermissions == null) {
                mPermissions = new HashMap<>();
            }
            mPermissions.put(requestCode, perms);

            EasyPermissions.requestPermissions(this, dialogType, requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        if (mPermissions == null || !mPermissions.containsKey(requestCode)) {
            return;
        }

        // 100% granted permissions
        if (mPermissions.get(requestCode).length == perms.size()) {
            mPermissonCallbacks.get(requestCode).hasPermission(Arrays.asList(mPermissions.get(requestCode)));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        if (mPermissions == null || !mPermissions.containsKey(requestCode)) {
            return;
        }

        //granted permission
        List<String> grantedPerms = new ArrayList<>();
        for (String perm : mPermissions.get(requestCode)) {
            if (!perms.contains(perm)) {
                grantedPerms.add(perm);
            }
        }

        //check has permission denied permanently
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            mPermissonCallbacks.get(requestCode).noPermission(perms, grantedPerms, true);
        } else {
            mPermissonCallbacks.get(requestCode).noPermission(perms, grantedPerms, false);
        }
    }

    @Override
    public void onDialog(int requestCode, int dialogType, EasyPermissions.DialogCallback callback) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        mPermissonCallbacks.get(requestCode).showDialog(dialogType, callback);
    }

}
