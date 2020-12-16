package com.kayu.car_owner_pay.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.kayu.car_owner_pay.R;
import com.kayu.utils.LogUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.status_bar_set.StatusBarUtil;

public class AgentWebViewActivity extends BaseActivity {
//    public static final String URL = "https://www.baidu.com";
    private String url;
    private String from;
    private String titleName = "加载中...";
    private TextView title_name;
    private AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;

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

        //        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));
        setContentView(R.layout.activity_agent_web);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
//        titleName = intent.getStringExtra("title");
        from = intent.getStringExtra("from");
//        title = intent.getStringExtra("title");
        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                finish();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = findViewById(R.id.title_back_tv);
        title_name = findViewById(R.id.title_name_tv);

//        if (!StringUtil.isEmpty(titleName)){
            title_name.setText(titleName);
//        }else {
//            title_name.setText("");
//        }
        if (StringUtil.isEmpty(from)){
            from = "返回";
        }
        back_tv.setText(from);

        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);

        initWebView();

    }


    private void initWebView(){

//        WebSettings webSettings = wvWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBlockNetworkImage(false);
//        //支持插件
////        webSettings.setPluginsEnabled(true);
//
////设置自适应屏幕，两者合用
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // //和setUseWideViewPort(true)一起解决网页自适应问题
//
////缩放操作
//        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
//        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
//        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//        webSettings.setDomStorageEnabled(true);
//
////其他细节操作
//        webSettings.setAllowFileAccess(true); //设置可以访问文件
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//
////        webSettings.setPluginState(WebSettings.PluginState.ON);
//
//
//        webSettings.setAppCacheEnabled(true);//是否使用缓存
//
//        //启用数据库
//        webSettings.setDatabaseEnabled(true);
//
////设置定位的数据库路径
//        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        webSettings.setGeolocationDatabasePath(dir);
//
////启用地理定位
//        webSettings.setGeolocationEnabled(true);
//        webSettings.setSupportMultipleWindows(true);
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//
//        // android 5.0及以上默认不支持Mixed Content
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            //或者
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
//        }

        mAgentWeb = AgentWeb.with(this)//

                .setAgentWebParent(mLinearLayout, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .useDefaultIndicator(getResources().getColor(R.color.colorAccent), 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings
                .go(url); //WebView载入该url地址的页面并显示。
        mAgentWeb.getJsInterfaceHolder().addJavaObject("androidMethod", new LocalJavascriptInterface(this));

        mAgentWeb.getAgentWebSettings().getWebSettings();
    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.e("AgentWebView","onPageFinished-----title:"+view.getTitle());
            title_name.postDelayed(() -> title_name.setText(view.getTitle()),100);


            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
//            lastOpenUrl = url;
            LogUtil.e("AgentWebView","onPageFinished: "+url);
            LogUtil.e("AgentWebView", "Cookies = " + CookieStr);

        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (StringUtil.isEmpty(titleName)) {
                title_name.setText(title);
            }
        }
    };

    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();//恢复
        super.onResume();
    }

    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause(); //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }



}
