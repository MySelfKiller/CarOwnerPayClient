package com.kayu.car_owner_pay.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cmic.sso.sdk.widget.LoadingImageView;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.AppManager;
import com.kayu.car_owner_pay.activity.BaseActivity;
import com.kayu.car_owner_pay.activity.MainActivity;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.activity.WebViewActivity;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.LoginDataParse;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.car_owner_pay.model.LoginInfo;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.wxapi.WXShare;
import com.kayu.form_verify.Form;
import com.kayu.form_verify.Validate;
import com.kayu.form_verify.validator.PhoneValidator;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.SMSCountDownTimer;
import com.kayu.utils.ScreenUtils;
import com.kayu.utils.StringUtil;
import com.kayu.utils.location.LocationManagerUtil;
import com.kayu.utils.permission.EasyPermissions;
import com.kayu.utils.status_bar_set.StatusBarUtil;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import cn.jiguang.verifysdk.api.AuthPageEventListener;
import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jiguang.verifysdk.api.JVerifyUIClickCallback;
import cn.jiguang.verifysdk.api.JVerifyUIConfig;

import cn.jiguang.verifysdk.api.VerifyListener;

public class LoginAutoActivity extends BaseActivity {
    //    private EditText phone_number;
//    private EditText sms_code;
    private AppCompatButton ask_btn, activation_btn;
    //    private SMSCountDownTimer timer;
//    private TextView send_sms;
//    private TextView password_target;
//    private LinearLayout login_send_sms_lay;
//    private LinearLayout password_target_lay;
//    private LinearLayout login_sms_target_lay;
//    private TextView login_sms_target;
//    private TextView login_forget_password;
//    private boolean isSMSLogin = true;
    private MainViewModel mViewModel;
    private TextView user_agreement;
    //    private TextView user_privacy;
    private SharedPreferences sp;
    private boolean isFirstShow;
    String[] titles;//协议标题
    String[] urls;//协议连接
    private WXShare wxShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.black));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_login_new);

        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        ask_btn = findViewById(R.id.login_auto_btn);
        activation_btn = findViewById(R.id.login_activation_btn);

        ask_btn.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                permissionsCheck();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        user_agreement = findViewById(R.id.login_user_agreement_tv);
//        user_privacy = findViewById(R.id.login_user_privacy_tv);
        WaitDialog.show(this, "请稍等...");
        mViewModel.getParameter(this, 3).observe(this, new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                WaitDialog.dismiss();
                if (null != systemParam && systemParam.type == 3) {
                    titles = systemParam.title.split("@@");
                    urls = systemParam.url.split("@@");
                    isFirstShow = sp.getBoolean(Constants.isShowDialog, true);
                    if (isFirstShow) {
                        String menss = "请您务必谨慎阅读、充分理解\"" + titles[0] + "\"和\"" + titles[1] + "\"各条款，包括但不限于：为了向你提供及时通讯，内容分享等服务，我们需要收集你的定位信息，操作日志信息" +
                                "等。你可以在\"设置\"中查看、变更、删除个人信息并管理你的授权。" +
                                "<br>你可阅读<font color=\"#007aff\"><a href=\"" + urls[0] + "\" style=\"text-decoration:none;\">《" + titles[0] + "》</a></font>和<font color=\"#007aff\"><a href=\"" + urls[1] + "\" style=\"text-decoration:none;\">《" + titles[1] + "》</a></font>了解详细信息" +
                                "如您同意，请点击确定接收我们的服务";
                        MessageDialog.show(LoginAutoActivity.this,
                                titles[0] + "和" + titles[1], Html.fromHtml(menss)
                                , "同意", "暂不使用")
                                .setCancelable(false).setOkButton(new OnDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v) {
                                baseDialog.doDismiss();
                                isFirstShow = false;
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean(Constants.isShowDialog, isFirstShow);
                                editor.apply();
                                editor.commit();
                                return false;
                            }
                        }).setCancelButton(new OnDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v) {
                                baseDialog.doDismiss();
                                finish();
                                return false;
                            }
                        });

                    }


                    user_agreement.setText(titles[0]);
//                    user_privacy.setText(titles[1]);
                    user_agreement.setOnClickListener(new NoMoreClickListener() {
                        @Override
                        protected void OnMoreClick(View view) {
                            jumpWeb("服务条款", urls[0]);
                        }

                        @Override
                        protected void OnMoreErrorClick() {

                        }
                    });
//                    user_privacy.setOnClickListener(new NoMoreClickListener() {
//                        @Override
//                        protected void OnMoreClick(View view) {
//                            jumpWeb(titles[1],urls[1]);
//                        }
//
//                        @Override
//                        protected void OnMoreErrorClick() {
//
//                        }
//                    });
                }

            }
        });

    }

    private void jumpWeb(String title, String url) {
        Intent intent = new Intent(LoginAutoActivity.this, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("from", title);
        startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    private void sendSubRequest(String loginToken) {
        WaitDialog.show(LoginAutoActivity.this, "登录...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = LoginAutoActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_LOGIN;
        reqInfo.parser = new LoginDataParse();
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("loginToken", loginToken);
//        reqDateMap.put("password",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                if (resInfo.status == 1) {
                    LoginInfo user = (LoginInfo) resInfo.responseData;
                    if (null != user) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(Constants.isLogin, true);
                        editor.putString(Constants.token, user.token);
                        editor.putBoolean(Constants.isSetPsd, true);
                        editor.putString(Constants.login_info, GsonHelper.toJsonString(user));
                        editor.apply();
                        editor.commit();
                        KWApplication.getInstance().token = user.token;
                        AppManager.getAppManager().finishAllActivity();
                        startActivity(new Intent(LoginAutoActivity.this, MainActivity.class));
                        finish();
//                        }

                    }
                } else {
                    Toast.makeText(LoginAutoActivity.this, resInfo.msg, Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);

    }

    public void permissionsCheck() {
//        String[] perms = {Manifest.permission.CAMERA};
        String[] perms = {Manifest.permission.READ_PHONE_STATE};

        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                // 检查当前是否初始化成功极光 SDK
                if (JVerificationInterface.isInitSuccess()) {
                    // 判断当前的手机网络环境是否可以使用认证。
                    if (!JVerificationInterface.checkVerifyEnable(LoginAutoActivity.this)) {
//                        Toast.makeText(LoginAutoActivity.this, "[2016],msg = 当前网络环境不支持认证", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginAutoActivity.this, "当前网络环境不支持认证", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginAutoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    WaitDialog.show(LoginAutoActivity.this, "稍等...");
//                    LoginSettings loginSettings = new LoginSettings();
//                    loginSettings.setAutoFinish(true);
//                    loginSettings.setTimeout(15 * 1000);
//                    loginSettings.setAuthPageEventListener(new AuthPageEventListener() {
//                        @Override
//                        public void onEvent(int i, String s) {
//                            LogUtil.e("JPush", "onEvent---code=" + i + ", msg=" + s);
//
//                        }
//                    });
//                    Resources resources = LoginAutoActivity.this.getResources();
//                    View view = getLayoutInflater().inflate(R.layout.login_part_lay,null,false);
//                    view.findViewById(R.id.login_part_phone).setOnClickListener(new NoMoreClickListener() {
//                        @Override
//                        protected void OnMoreClick(View view) {
//                            startActivity(new Intent(LoginAutoActivity.this, LoginActivity.class));
//                        }
//
//                        @Override
//                        protected void OnMoreErrorClick() {
//
//                        }
//                    });
//                    view.findViewById(R.id.login_part_wechat).setOnClickListener(new NoMoreClickListener() {
//                        @Override
//                        protected void OnMoreClick(View view) {
//                            wxShare = new WXShare(LoginAutoActivity.this);
//                            wxShare.register();
//                            wxShare.getAuth(new ItemCallback() {
//                                @Override
//                                public void onItemCallback(int position, Object obj) {
//                                    Toast.makeText(LoginAutoActivity.this,"微信token"+(String)obj,Toast.LENGTH_SHORT).show();
//                                    reqSignIn((String)obj);
//                                }
//
//                                @Override
//                                public void onDetailCallBack(int position, Object obj) {
//
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        protected void OnMoreErrorClick() {
//
//                        }
//                    });
//                    JVerifyUIConfig uiConfig = new JVerifyUIConfig.Builder()
////                            .setStatusBarTransparent(true)
////                            .setStatusBarHidden(f)
////                            .setStatusBarColorWithNav(true)
////                            .setVirtualButtonTransparent(true)
////                            .setPrivacyVirtualButtonTransparent(true)
////                            .setPrivacyVirtualButtonTransparent(true)
//                            .setStatusBarDarkMode(false)
//                            .setNavColor(resources.getColor(R.color.white))
//                            .setNavText("登录")
//                            .setNavTextSize(20)
////                            .setNavTextBold(true)
//                            .setPrivacyNavColor(resources.getColor(R.color.white))
//                            .setNavTextColor(resources.getColor(R.color.black1))
//                            .setNavReturnImgPath("normal_btu_black")
//                            .setNavReturnBtnOffsetX(20)
//                            .setLogoImgPath("ic_login_bg")
//                            .setLogoWidth(80)
//                            .setLogoHeight(60)
//                            .setLogoHidden(false)
//                            .setNumberColor(resources.getColor(R.color.black1))
//                            .setLogBtnText("一键登录")
//                            .setLogBtnTextSize(16)
//                            .setLogBtnHeight(40)
//                            .setLogBtnTextColor(resources.getColor(R.color.select_text_color))
//                            .setLogBtnImgPath("ic_login_btn_bg")
//                            .setAppPrivacyOne(titles[0], urls[0])
//                            .setAppPrivacyTwo(titles[1], urls[1])
//
//                            .setAppPrivacyColor(0xFFBBBCC5, 0xFF8998FF)
//                            .setPrivacyCheckboxHidden(true)
//                            .setPrivacyState(true)
//                            .setSloganTextColor(resources.getColor(R.color.grayText2))
//                            .setSloganTextSize(12)
//                            .setLogoOffsetY(100)
////                            .setLogoImgPath("logo_cm")
//                            .setNumFieldOffsetY(190)
//                            .setSloganOffsetY(235)
//                            .setLogBtnOffsetY(260)
//                            .setNumberSize(22)
//                            .setPrivacyState(true)
//                            .setPrivacyTextCenterGravity(true)
////                            .setPrivacyOffsetX(30)
//                            .setPrivacyTextSize(12)
//                            .addCustomView(view, false, new JVerifyUIClickCallback() {
//                                @Override
//                                public void onClicked(Context context, View view) {
////                                    Toast.makeText(context,"动态注册的其他按钮",Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .setNavTransparent(false).build();

//                    JVerificationInterface.setCustomUIWithConfig(uiConfig);
                    JVerificationInterface.setCustomUIWithConfig(getFullScreenPortraitConfig());
                    JVerificationInterface.loginAuth(LoginAutoActivity.this, new VerifyListener() {
                        @Override
                        public void onResult(int code, String content, String operator) {
                            WaitDialog.dismiss();
                            if (code == 6000) {
                                LogUtil.e("JPush", "code=" + code + ", token=" + content + " ,operator=" + operator);
                                sendSubRequest(content);
                            } else if (code == 6001) {
                                LogUtil.e("JPush", "code=" + code + ", content=" + content + " ,operator=" + operator);
                                TipDialog.show(LoginAutoActivity.this, "登录失败", TipDialog.TYPE.ERROR);
                            } else {
                                LogUtil.e("JPush", "code=" + code + ", content=" + content + " ,operator=" + operator);
                            }
                        }
                    });

                } else {
                    Toast.makeText(LoginAutoActivity.this, "极光 SDK 尚未初始化成功～！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(LoginAutoActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) LoginAutoActivity.this);
                dialog.setStyle(DialogSettings.STYLE.STYLE_IOS);
                dialog.setTheme(DialogSettings.THEME.LIGHT);
                dialog.setTitle(getString(R.string.app_name));
                dialog.setMessage(getString(R.string.permiss_read_phone));
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

    @SuppressLint("HandlerLeak")
    private void reqSignIn(String code) {
        if (null== code || StringUtil.isEmpty(code)){
            return;
        }
        WaitDialog.show(LoginAutoActivity.this,"确认中...");
        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = LoginAutoActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_LOGIN;
        reqInfo.parser = new LoginDataParse();
        HashMap<String,Object> reqDateMap = new HashMap<>();
        reqDateMap.put("wxCode",code);

//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                WaitDialog.dismiss();
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
                    LoginInfo user = (LoginInfo) resInfo.responseData;
                    if (null != user){

                        SharedPreferences.Editor editor = sp.edit();
//                        KWApplication.getInstance().sign = user.sign;
//                        if (user.initPwd == 1){//是否需要设定密码 0:否 1:是
//                            editor.putBoolean(Constants.isLogin,true);
//                            editor.putString(Constants.sign,user.sign);
//                            editor.putBoolean(Constants.isSetPsd,false);
//                            editor.putString(Constants.userInfo, GsonHelper.toJsonString(user));
//                            editor.apply();
//                            editor.commit();
//                            Intent intent = new Intent(LoginActivity.this, SetPasswordActivity.class);
//                            intent.putExtra("title","设置密码");
//                            intent.putExtra("back","");
//                            intent.putExtra("isSetPwd",true);
//                            startActivity(intent);
//                        }else {
                        editor.putBoolean(Constants.isLogin,true);
                        editor.putString(Constants.token,user.token);
                        editor.putBoolean(Constants.isSetPsd,true);
                        editor.putString(Constants.login_info, GsonHelper.toJsonString(user));
                        editor.apply();
                        editor.commit();
                        KWApplication.getInstance().token = user.token;
                        AppManager.getAppManager().finishAllActivity();
                        finish();
                        startActivity(new Intent(LoginAutoActivity.this, MainActivity.class));
//                        }

                    }
                }else {
                    Toast.makeText(LoginAutoActivity.this,resInfo.msg,Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);
    }


    @Override
    protected void onDestroy() {
        if (null != wxShare)
            wxShare.unregister();
        super.onDestroy();
    }


    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(LoginAutoActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void toNativeVerifyActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private int dp2Pix(Context context, float dp) {
        try {
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5F);
        } catch (Exception e) {
            return (int) dp;
        }
    }

    private int px2dip(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private JVerifyUIConfig getFullScreenPortraitConfig(){
        JVerifyUIConfig.Builder uiConfigBuilder = new JVerifyUIConfig.Builder();
        uiConfigBuilder.setStatusBarDarkMode(false);
        uiConfigBuilder.setNavColor(getResources().getColor(R.color.white));
        uiConfigBuilder.setNavText("登录");
        uiConfigBuilder.setNavTextSize(20);
//        uiConfigBuilder.setPrivacyNavColor(getResources().getColor(R.color.white));
        uiConfigBuilder.setNavTextColor(getResources().getColor(R.color.black1));
        uiConfigBuilder.setNavReturnImgPath("normal_btu_black");
        uiConfigBuilder.setNavReturnBtnOffsetX(20);
        uiConfigBuilder.setLogoImgPath("ic_login_bg");
        uiConfigBuilder.setLogoWidth(80);
        uiConfigBuilder.setLogoHeight(60);
        uiConfigBuilder.setLogoHidden(false);
        uiConfigBuilder.setNumberColor(getResources().getColor(R.color.black1));
        uiConfigBuilder.setLogBtnText("一键登录");
        uiConfigBuilder.setLogBtnTextSize(16);
        uiConfigBuilder.setLogBtnHeight(40);
        uiConfigBuilder.setLogBtnTextColor(getResources().getColor(R.color.select_text_color));
        uiConfigBuilder.setLogBtnImgPath("ic_login_btn_bg");
//        uiConfigBuilder.setAppPrivacyOne(titles[0], urls[0]);
//        uiConfigBuilder.setAppPrivacyTwo(titles[1], urls[1]);

        uiConfigBuilder.setAppPrivacyColor(0xFFBBBCC5, 0xFF8998FF);
        uiConfigBuilder.setPrivacyCheckboxHidden(true);
        uiConfigBuilder.setPrivacyState(true);
        uiConfigBuilder.setSloganTextColor(getResources().getColor(R.color.grayText2));
        uiConfigBuilder.setSloganTextSize(12);
        uiConfigBuilder.setLogoOffsetY(100);
//                            .setLogoImgPath("logo_cm")
        uiConfigBuilder.setNumFieldOffsetY(190);
        uiConfigBuilder.setSloganOffsetY(235);
        uiConfigBuilder.setLogBtnOffsetY(260);
        uiConfigBuilder.setNumberSize(22);
        uiConfigBuilder.setPrivacyState(true);
//        uiConfigBuilder.setPrivacyTextCenterGravity(true);
        uiConfigBuilder.setPrivacyTextCenterGravity(true);
        uiConfigBuilder.setPrivacyTextSize(12);
        uiConfigBuilder.setPrivacyText("登录即同意《","》《","》《","》并授权"+getResources().getString(R.string.app_name)+"获取本机号码");
//        uiConfigBuilder.setPrivacyOffsetX(52-15);

        // 手机登录按钮
        RelativeLayout.LayoutParams layoutParamPhoneLogin = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamPhoneLogin.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dp_300),0,0);
        layoutParamPhoneLogin.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        layoutParamPhoneLogin.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        TextView tvPhoneLogin = new TextView(this);
        tvPhoneLogin.setText("手机号码登录");
        tvPhoneLogin.setTextColor(getResources().getColor(R.color.grayText));
//        tvPhoneLogin.setTextSize(getResources().getDimensionPixelSize(R.dimen.sp_14));
        tvPhoneLogin.setLayoutParams(layoutParamPhoneLogin);
        uiConfigBuilder.addCustomView(tvPhoneLogin, false, new JVerifyUIClickCallback() {
            @Override
            public void onClicked(Context context, View view) {
                toNativeVerifyActivity();
            }
        });

        // 微信登录

        LinearLayout linearLayout = new LinearLayout(this);
        RelativeLayout.LayoutParams layoutLoginGroupParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutLoginGroupParam.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dp_472), 0, 0);
        layoutLoginGroupParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutLoginGroupParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutLoginGroupParam.setLayoutDirection(LinearLayout.VERTICAL);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutLoginGroupParam);
        int padding = getResources().getDimensionPixelSize(R.dimen.dp_5);
        linearLayout.setPadding(padding,padding,padding,padding);

        ImageView btnWechat = new ImageView(this);
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams texParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        texParam.setMargins(0,padding,0,0);
        textView.setLayoutParams(texParam);
        textView.setText("微信登录");
//        textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.sp_14));
        textView.setTextColor(getResources().getColor(R.color.grayText));

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                JShareInterface.authorize(Wechat.Name, mAuthListener);
                wxShare = new WXShare(LoginAutoActivity.this);
                wxShare.register();
                wxShare.getAuth(new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
//                        Toast.makeText(LoginAutoActivity.this,"微信token"+(String)obj,Toast.LENGTH_SHORT).show();
                        reqSignIn((String)obj);
                    }

                    @Override
                    public void onDetailCallBack(int position, Object obj) {

                    }
                });
            }
        });

        btnWechat.setImageResource(R.drawable.ic_contact_wx);

        LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParam.setMargins(25,0,25,0);


        linearLayout.addView(btnWechat,btnParam);
        linearLayout.addView(textView);
//        layoutLoginGroup.addView(btnXinlang,btnParam);
        uiConfigBuilder.addCustomView(linearLayout, false, new JVerifyUIClickCallback() {
            @Override
            public void onClicked(Context context, View view) {
                wxShare = new WXShare(LoginAutoActivity.this);
                wxShare.register();
                wxShare.getAuth(new ItemCallback() {
                    @Override
                    public void onItemCallback(int position, Object obj) {
//                        Toast.makeText(LoginAutoActivity.this,"微信token"+(String)obj,Toast.LENGTH_SHORT).show();
                        reqSignIn((String)obj);
                    }

                    @Override
                    public void onDetailCallBack(int position, Object obj) {

                    }
                });
            }
        });

//
//        final View dialogViewTitle = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_login_title,null, false);
//
//        uiConfigBuilder.addNavControlView(dialogViewTitle, new JVerifyUIClickCallback() {
//            @Override
//            public void onClicked(Context context, View view) {
//
//            }
//        });
        return uiConfigBuilder.build();
    }

}
