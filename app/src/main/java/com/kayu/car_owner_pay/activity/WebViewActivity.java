package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.JsXiaojuappApi;
import com.kayu.car_owner_pay.LocalJavascriptInterface;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.SettingInterface;
import com.kayu.car_owner_pay.activity.login.LoginActivity;
import com.kayu.car_owner_pay.config_ad.TTAdManagerHolder;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.NormalIntParse;
import com.kayu.car_owner_pay.http.parser.NormalParse;
import com.kayu.utils.AppUtil;
import com.kayu.utils.Constants;
import com.kayu.utils.DesCoderUtil;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.status_bar_set.StatusBarUtil;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.TipGifDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class WebViewActivity extends BaseActivity {

    WebView wvWebView;
//    ProgressBar pbWebView;

    public static final String URL = "https://www.baidu.com";
    private String url;
    private String from;
    private String titleName = "?????????...";
    private TextView title_name;
    Map<String, String> headMap =new HashMap<>();
    private static final String TAG = "RewardVideoActivity";
    private long adID = 0L;
    @SuppressLint("HandlerLeak")
    private Handler jsHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            LogUtil.e("WebViewActivity","advert----what:"+msg.what+"------arg1:"+msg.arg1);
            if (msg.what == 1) {
                adID =(long) msg.obj;
                loadAd(TTAdManagerHolder.videoID);
//                if (mttRewardVideoAd != null&&mIsLoaded) {
//                    //step6:???????????????????????????,???????????????onRewardVideoCached?????????????????????????????????????????????
//                    //???????????????????????????
////                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);
//
//                    //?????????????????????????????????????????????

//                } else {
////                    TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "??????????????????");
//            mContext.finish();
//                }
            } else if (msg.what == 2) {
                mttRewardVideoAd.showRewardVideoAd(WebViewActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                mttRewardVideoAd = null;
            }
            super.handleMessage(msg);
        }
    };
    private String data;//???????????????????????????
    private String channel;//?????????????????? ??????:ty ????????????:tyb ??????:qj
    private String gasId;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //????????????
        if (newConfig.fontScale != 1){
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//??????????????????
        Resources res = super.getResources();
        //????????????
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//????????????
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //?????????????????????
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));
        setContentView(R.layout.activity_webview);
        AndroidBug5497Workaround.assistActivity(this);
        LinearLayout webLay = findViewById(R.id.llWebView);
        if (AppUtil.hasNavBar(this)){
            int bottom = AppUtil.getNavigationBarHeight(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(webLay.getLayoutParams());
            lp.setMargins(0, 0, 0, bottom+80);
            webLay.setLayoutParams(lp);
        }

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        data = intent.getStringExtra("data");
        channel = intent.getStringExtra("channel");
        gasId = intent.getStringExtra("gasId");
//        titleName = intent.getStringExtra("title");
//        from = intent.getStringExtra("from");
//        title = intent.getStringExtra("title");
        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                sendOilPayInfo(WebViewActivity.this);
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        findViewById(R.id.title_close_btn).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                sendOilPayInfo(WebViewActivity.this);
                finish();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });

        title_name = findViewById(R.id.title_name_tv);

        title_name.setText(titleName);
        if (StringUtil.isEmpty(from)){
            from = "??????";
        }

        wvWebView = findViewById(R.id.wvWebView);
//        pbWebView = findViewById(R.id.pbWebView);
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            builder.detectFileUriExposure();
//        }
//        CookieSyncManager.createInstance(this);
//        CookieSyncManager.getInstance().startSync();
//        CookieManager.getInstance().removeSessionCookie();
        TipGifDialog.show(this, "?????????...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);

        initData();
    }

    protected void initViewsAndEvents() {
        initData();
    }

    public void initData() {
        if (StringUtil.isEmpty(url)) {
            url = URL;
        }
//        url = "https://wallet.xiaoying.com/fe/wallet-landing/blueRegPage/index.html?landId=306&source=100016303";

        WebSettings webSettings = wvWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        //????????????
//        webSettings.setPluginsEnabled(true);

//????????????????????????????????????
        webSettings.setUseWideViewPort(true); //????????????????????????webview?????????
        webSettings.setLoadWithOverviewMode(true); // //???setUseWideViewPort(true)?????????????????????????????????

//????????????
        webSettings.setSupportZoom(true); //????????????????????????true??????????????????????????????
        webSettings.setBuiltInZoomControls(true); //????????????????????????????????????false?????????WebView????????????
        webSettings.setDisplayZoomControls(false); //???????????????????????????
        webSettings.setDomStorageEnabled(true);

//??????????????????
        webSettings.setAllowFileAccess(true); //????????????????????????
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //????????????JS???????????????
        webSettings.setLoadsImagesAutomatically(true); //????????????????????????
        webSettings.setDefaultTextEncodingName("utf-8");//??????????????????

//        webSettings.setPluginState(WebSettings.PluginState.ON);


        webSettings.setAppCacheEnabled(true);//??????????????????

        //???????????????
        webSettings.setDatabaseEnabled(true);

//??????????????????????????????
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);

//??????????????????
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//??????DomStorage??????
//        LogUtil.e("WebView","UserAgent: "+webSettings.getUserAgentString());

        // android 5.0????????????????????????Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (!StringUtil.isEmpty(channel)) {
            if (channel.equals("tyb")) {
                wvWebView.addJavascriptInterface(new SettingInterface(data, gasId), "app");
            } else if (channel.equals("qj")) {
//            wvWebView.addJavascriptInterface(new JsXiaojuappApi(WebViewActivity.this, wvWebView, new Handler()), "xiaojuapp");
                wvWebView.addJavascriptInterface(new JsXiaojuappApi(WebViewActivity.this, new Handler()), "androidMethod");
            }
        } else {
            wvWebView.addJavascriptInterface(new LocalJavascriptInterface(this, jsHandler), "androidMethod");
        }

        wvWebView.requestFocus();
        wvWebView.clearCache(true);
        wvWebView.clearHistory();
/**
 * ????????????????????????????????????????????????????????????????????????????????????????????? url ?????????????????????????????????
 **/
//        wvWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
        wvWebView.setDownloadListener(new FileDownLoadListener());

        wvWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, final String url, String message, final JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("??????").setMessage(message)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                if (!StringUtil.isEmpty(url)){
//                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
//                                    startActivity(intent);
//                                }
                                result.confirm();
                            }
                        }).setNeutralButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                }).create().show();

                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("??????")
                        .setMessage(message)
                        .setPositiveButton("??????",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                }).setNeutralButton("??????", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b2.setCancelable(true);
                b2.create();
                b2.show();
                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                title_name.setText(titleName);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                pbWebView.setProgress(newProgress);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                openImageChooserActivity();


            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                openImageChooserActivity();
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                openImageChooserActivity();

            }

            // For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }


        });

        wvWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                LogUtil.e("webview","errorResponse="+errorResponse.toString());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                LogUtil.e("webview","description="+description+"  failingUrl="+failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.e("WebView","shouldOverrideUrlLoading: "+url);

//                view.loadUrl(url);
                if(url.startsWith("http:")|| url.startsWith("https:")){
                    if (url.equals(HttpConfig.CLOSE_WEB_VIEW)){
                        onBackPressed();
                        return true;
                    }else {
                        headMap.put("Referer",lastOpenUrl);
                        view.loadUrl(url,headMap);
                        lastOpenUrl = url;
                        return false;

                    }
                }else{
                    try {
                        // ??????????????????,??????????????????????????????
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        isDownload = false;  //?????????????????????????????????????????????????????????
                    } catch (Exception e) {
//                        if (url.startsWith("xywallet://")){
//                            String mUrl = "https://wallet.xiaoying.com/fe/wallet-activity/download/index.html?source=100021313&landId=910#/";
//                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
//                            startActivity(intent);
//                        }
//                        String mUrl = "https://wallet.xiaoying.com/fe/wallet-activity/download/index.html?source=100021313&landId=910#/";
//                        if (!url.startsWith("qihooloan://")){
//                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(lastOpenUrl));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
//                            startActivity(intent);
//                        }
                        // ???????????????????????????
                        e.printStackTrace();
                        ToastUtils.show("???????????????????????????");
                    }

                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                title_name.setText(titleName);
                TipGifDialog.show(WebViewActivity.this, "?????????...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
//                pbWebView.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setLoadsImagesAutomatically(true);
//                pbWebView.setVisibility(View.GONE);
//                LogUtil.e("WebView","onPageFinished-----title:"+view.getTitle());
                TipGifDialog.dismiss();
                title_name.postDelayed(() -> title_name.setText(view.getTitle()),100);


                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                lastOpenUrl = url;
                LogUtil.e("WebView","onPageFinished: "+url);
                LogUtil.e("WebView", "Cookies = " + CookieStr);

                super.onPageFinished(view, url);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                LogUtil.e("webview","SslErrorHandler="+handler.toString()+"  SslError="+error.toString());
                handler.proceed();
            }
        });

        wvWebView.loadUrl(url);
    }

    private String lastOpenUrl;

    /**
     * ????????????????????? url
     *
     * @param checkUrl url
     * @return ??????
     */
    private boolean isHaveAliPayLink(String checkUrl) {
        return !TextUtils.isEmpty(checkUrl) && (checkUrl.startsWith("alipays:") || checkUrl.startsWith("alipay"));
    }

    private boolean isDownload = true;
    class FileDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            LogUtil.e("WebView","DownloadListener-->url=" + url);
            LogUtil.e("WebView","isDownload-->" + isDownload);
            if (isDownload) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(uri);
                startActivity(intent);
            }
            isDownload = true;//?????????????????????
        }
    }
    private String cameraFielPath;
    private static final int FILE_CAMERA_RESULT_CODE = 0;
    private int FILE_CHOOSER_RESULT_CODE = 1;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadMessage;
    ///????????????????????????
    public void openImageChooserActivity() {
        showCustomDialog();
    }

    //??????
    private void takeCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFielPath = Environment.getExternalStorageDirectory() + "//" + System.currentTimeMillis() + ".jpg";
        File outputImage = new File(cameraFielPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0?????????
            Uri photoUri = FileProvider.getUriForFile(
                    this, Constants.authority,
                    outputImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("return-data", true);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
        }
        startActivityForResult(intent, FILE_CAMERA_RESULT_CODE);
    }


    //????????????
    private void takePhoto() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    private void showCustomDialog() {
        BottomMenu.show(WebViewActivity.this, new String[]{"??????", "???????????????"}, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                if (index == 0) {
                    // 2018/12/10 ??????
//                    requestCode = FILE_CAMERA_RESULT_CODE;
                    takeCamera();
                } else if (index == 1) {
//                    requestCode = FILE_CHOOSER_RESULT_CODE;
                    // 2018/12/10 ???????????????
                    takePhoto();
                }else {
//                    mUploadCallbackAboveL = null;
//                    mUploadMessage = null;
                }
            }
        }).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL.onReceiveValue(null);
                    mUploadCallbackAboveL = null;
                }
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }
            }
        });

    }

//    private int requestCode = -2;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
        if (resultCode != RESULT_OK) {//????????????????????????onReceiveValue??????????????????????????????js??????
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(null);
                mUploadCallbackAboveL = null;
            }
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            return;
        }
        Uri result = null;
        if (requestCode == FILE_CAMERA_RESULT_CODE) {
            if (null != data && null != data.getData()) {
                result = data.getData();
            }
            if (result == null && hasFile(cameraFielPath)) {
                result = Uri.fromFile(new File(cameraFielPath));
            }
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{result});
                mUploadCallbackAboveL = null;
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                result = data.getData();
            }
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }


    /**
     * ????????????????????????
     */
    public boolean hasFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            Log.i("error", e.toString());
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CAMERA_RESULT_CODE && requestCode != FILE_CHOOSER_RESULT_CODE
                || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (intent != null) {
            String dataString = intent.getDataString();
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null)
                results = new Uri[]{Uri.parse(dataString)};
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }

    //????????????????????????
    @Override
    public void onBackPressed() {
        if (wvWebView.canGoBack()) {
            wvWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            wvWebView.goBack();
            return;
        } else {
            finish();
        }

        super.onBackPressed();
    }

    //???????????????
    @Override
    protected void onPause() {
        super.onPause();
        wvWebView.onPause();
    }

    @Override
    protected void onResume() {
        wvWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (wvWebView != null) {
            wvWebView.destroy();
            wvWebView = null;
        }
        super.onDestroy();
    }

    private boolean mHasShowDownloadActive = false;
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mIsExpress = false; //????????????????????????
    private boolean mIsLoaded = true; //????????????????????????

    private void loadAd(final String codeId) {
        //step1:?????????sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //step3:??????TTAdNative??????,??????????????????????????????
        mTTAdNative = ttAdManager.createAdNative(this);
//        getExtraInfo();
//        initClickEvent();

        //step4:????????????????????????AdSlot,??????????????????????????????
        AdSlot adSlot;
        if (mIsExpress) {
            //?????????????????????????????????????????????view?????????????????????dp???
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    //????????????????????????????????????????????????????????????,??????dp,?????????????????????????????????????????????0??????
                    .setExpressViewAcceptedSize(500,500)
                    .build();
        } else {
            //????????????????????????????????????????????????????????????,??????dp,????????????????????????????????????????????????????????????????????????
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .build();
        }
        //step5:????????????
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtil.e(TAG, "WebViewActivity --> onError: " + code + ", " + String.valueOf(message));
                TipGifDialog.dismiss();
//                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, message);
            }

            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            @Override
            public void onRewardVideoCached() {
                LogUtil.e(TAG, "Callback --> onRewardVideoCached");
                mIsLoaded = true;
                jsHandler.sendMessage(jsHandler.obtainMessage(2));
//                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {

            }

            //????????????????????????????????????????????????url?????????????????????????????????????????????????????????????????????????????????????????????????????????
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                LogUtil.e(TAG, "Callback --> onRewardVideoAdLoad");

//                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd loaded ???????????????" + getAdType(ad.getRewardVideoAdType()));
                mIsLoaded = false;
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        LogUtil.d(TAG, "Callback --> rewardVideoAd show");
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        LogUtil.d(TAG, "Callback --> rewardVideoAd bar click");
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> rewardVideoAd close");
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd close");
                    }

                    //????????????????????????
                    @Override
                    public void onVideoComplete() {
                        LogUtil.d(TAG, "Callback --> rewardVideoAd complete");
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        LogUtil.e(TAG, "Callback --> rewardVideoAd error");
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd error");
                    }

                    //?????????????????????????????????????????????rewardVerify??????????????????rewardAmount??????????????????rewardName???????????????
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        LogUtil.e(TAG, "Callback --> " + logString);
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, logString);
                        if (adID == 0L )
                            return;

                        if (rewardVerify)
                            sendADComplete(adID);
                    }

                    @Override
                    public void onSkippedVideo() {
                        LogUtil.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        LogUtil.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
//                            TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "????????????????????????????????????", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        LogUtil.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "???????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        LogUtil.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        LogUtil.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        LogUtil.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
//                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "???????????????????????????????????????", Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void sendADComplete(long mfid) {

        //??????requestId
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");
        //??????key
        StringBuilder keyBuilder = new StringBuilder();
        //?????????
        StringBuilder seatBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            //???????????? <= 9
            int index = random.nextInt(10);
            //????????????
            seatBuilder.append(index);
            //?????????????????????key
            keyBuilder.append(requestId.charAt(index));
        }

        Map<String,Object> map = new HashMap<>();
        map.put("mfId",mfid);
        map.put("timeMillis",System.currentTimeMillis());
        HashMap<String,Object> reqDateMap = new HashMap<>();

        try {

            String seats = seatBuilder.toString();
            String key = keyBuilder.toString();
//            JSONObject jsonObject = new JSONObject(map);

//            String data = DesCoderUtil.encryptDES(jsonObject.toString(),key);
            String data = DesCoderUtil.encryptDES(GsonHelper.toJsonString(map),key);
            reqDateMap.put("seats",seats);
            reqDateMap.put("data",data);
            reqDateMap.put("requestId",requestId);
            LogUtil.e("key",key);
            LogUtil.e("requestId",requestId);
            LogUtil.e("data",data);

        } catch (Exception e) {

        }


        final RequestInfo reqInfo = new RequestInfo();
        reqInfo.context = WebViewActivity.this;
        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_AD_COMPLETE;
        reqInfo.parser = new NormalParse();


        reqInfo.reqDataMap = reqDateMap;
        reqInfo.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo)msg.obj;
                if (resInfo.status ==1 ){
//                    ToastUtils.show("??????????????????");
                }else {
//                    ToastUtils.show(resInfo.msg);
                }
                super.handleMessage(msg);
            }
        };

        ResponseCallback callback = new ResponseCallback(reqInfo);
        ReqUtil.getInstance().setReqInfo(reqInfo);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

    @SuppressLint("HandlerLeak")
    public void sendOilPayInfo(Context context) {
        RequestInfo reques = new RequestInfo();
        reques.context = context;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_NOTIFIED;
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reques.reqDataMap = reqDateMap;
        reques.parser = new NormalIntParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

}
