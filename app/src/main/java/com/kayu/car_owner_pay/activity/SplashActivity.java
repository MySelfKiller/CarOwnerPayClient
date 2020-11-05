package com.kayu.car_owner_pay.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.login.LoginActivity;
import com.kayu.car_owner_pay.activity.login.SetPasswordActivity;
import com.kayu.utils.Constants;
import com.kayu.utils.status_bar_set.StatusBarUtil;


public class SplashActivity extends AppCompatActivity {

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
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        StatusBarUtil.setTranslucentStatus(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(runnable,1000*3);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent;
            SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
            boolean isLogin = sp.getBoolean(Constants.isLogin, false);
            boolean isSetPsd = sp.getBoolean(Constants.isSetPsd, false);
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
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            Uri data = getIntent().getData();
            if (data != null) {
                intent.setData(data);
            }
//            intent.putExtra("from", "splash");
            startActivity(intent);
            finish();
        }
    };
}
