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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.R;
import com.kayu.utils.AppUtil;
import com.kayu.utils.Constants;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.status_bar_set.StatusBarUtil;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.v3.BottomMenu;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    WebView wvWebView;
    ProgressBar pbWebView;

    public static final String URL = "https://www.baidu.com";
    private String url;
    private String from;
    private String titleName = "加载中...";
    private TextView title_name;
    Map<String, String> headMap =new HashMap<>();

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
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
//        titleName = intent.getStringExtra("title");
        from = intent.getStringExtra("from");
//        title = intent.getStringExtra("title");
        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = findViewById(R.id.title_back_tv);
        title_name = findViewById(R.id.title_name_tv);

//        if (!StringUtil.isEmpty(titleName)){
//            title_name.setText(titleName);
//        }else {
//        }
        title_name.setText(titleName);
        if (StringUtil.isEmpty(from)){
            from = "返回";
        }
        back_tv.setText(from);

        wvWebView = findViewById(R.id.wvWebView);
        pbWebView = findViewById(R.id.pbWebView);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();

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
        //支持插件
//        webSettings.setPluginsEnabled(true);

//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // //和setUseWideViewPort(true)一起解决网页自适应问题

//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setDomStorageEnabled(true);

//其他细节操作
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

//        webSettings.setPluginState(WebSettings.PluginState.ON);


        webSettings.setAppCacheEnabled(true);//是否使用缓存

        //启用数据库
        webSettings.setDatabaseEnabled(true);

//设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);

//启用地理定位
        webSettings.setGeolocationEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//开启DomStorage缓存
        LogUtil.e("WebView","UserAgent: "+webSettings.getUserAgentString());

        // android 5.0及以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            //或者
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        wvWebView.addJavascriptInterface(new LocalJavascriptInterface(this),"androidMethod");
        wvWebView.requestFocus();
        wvWebView.clearCache(true);
        wvWebView.clearHistory();
/**
 * 当下载文件时打开系统自带的浏览器进行下载，当然也可以对捕获到的 url 进行处理在应用内下载。
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

                builder.setTitle("提示").setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                if (!StringUtil.isEmpty(url)){
//                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
//                                    startActivity(intent);
//                                }
                                result.confirm();
                            }
                        }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                }).create().show();

                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("提示")
                        .setMessage(message)
                        .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {

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
                pbWebView.setProgress(newProgress);
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
                    if (url.equals("https://www.kakayuy.com/close")){
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
                        // 以下固定写法,表示跳转到第三方应用
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        isDownload = false;  //该字段是用于判断是否需要跳转浏览器下载
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
                        // 防止没有安装的情况
                        e.printStackTrace();
                        ToastUtils.show("未安装相应的客户端");
                    }

                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                title_name.setText(titleName);
                pbWebView.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setLoadsImagesAutomatically(true);
                pbWebView.setVisibility(View.GONE);
                title_name.setText(StringUtil.getTrimedString(wvWebView.getTitle()));

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
     * 是否是支付宝的 url
     *
     * @param checkUrl url
     * @return 结果
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
            isDownload = true;//重置为初始状态
        }
    }
    private String cameraFielPath;
    private static final int FILE_CAMERA_RESULT_CODE = 0;
    private int FILE_CHOOSER_RESULT_CODE = 1;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadMessage;
    ///选择拍照还是相册
    public void openImageChooserActivity() {
        showCustomDialog();
    }

    //拍照
    private void takeCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFielPath = Environment.getExternalStorageDirectory() + "//" + System.currentTimeMillis() + ".jpg";
        File outputImage = new File(cameraFielPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
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


    //选择图片
    private void takePhoto() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    private void showCustomDialog() {
        BottomMenu.show(WebViewActivity.this, new String[]{"拍照", "从相册选择"}, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                if (index == 0) {
                    // 2018/12/10 拍照
//                    requestCode = FILE_CAMERA_RESULT_CODE;
                    takeCamera();
                } else if (index == 1) {
//                    requestCode = FILE_CHOOSER_RESULT_CODE;
                    // 2018/12/10 从相册选择
                    takePhoto();
                }
            }
        });

    }

//    private int requestCode = -2;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
        if (resultCode != RESULT_OK) {//同上所说需要回调onReceiveValue方法防止下次无法响应js方法
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
     * 判断文件是否存在
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

    //系统自带监听方法
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

    //类相关监听
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
}
