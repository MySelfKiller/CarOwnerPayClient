package com.kayu.car_owner_pay.http;


import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebSettings;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.http.cookie.CookiesManager;
import com.kayu.car_owner_pay.http.cookie.PersistentCookieStore;
import com.kayu.utils.LogUtil;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Killer on 2018/5/24.
 */

public class OkHttpManager {
    static OkHttpManager manager;
    private OkHttpClient mClient;

    /**
     * 方法加锁，防止多线程操作时出现多个实例
     */
    private static synchronized void init() {
        if (manager == null) {
            manager = new OkHttpManager();
        }
    }

    /**
     * 获得当前对象实例
     *
     * @return 当前实例对象
     */
    public final static OkHttpManager getInstance() {
        if (manager == null) {
            init();
        }
        return manager;
    }

//    /**
//     * 管理器初始化，建议在application中调用
//     *
//     * @param context
//     */
//    public static void init(Context context) {
//        getInstance();
//    }

    private OkHttpManager() {
        initOkhttpClient();
    }
    public OkHttpClient getHttpClient(){
        if (null ==mClient)
            initOkhttpClient();
        return mClient;
    }

    public OkHttpClient resetHttpClient(){
        mClient = null;
        initOkhttpClient();
        return mClient;
    }
    /**
     * 初始化okhttp
     */
    private void initOkhttpClient() {

        LogUtil.e("hm","执行OkHttpClient初始化");
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(30, TimeUnit.SECONDS);
        okBuilder.readTimeout(30, TimeUnit.SECONDS);
        okBuilder.writeTimeout(30, TimeUnit.SECONDS);
        okBuilder.addInterceptor(new Interceptor() {
                                     @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                                     @Override
                                     public Response intercept(Chain chain) throws IOException {
                                         Request request = chain.request()
                                                 .newBuilder()
                                                 .removeHeader("User-Agent")//移除旧的
//                                                 WebSettings.getDefaultUserAgent(mContext) 是获取原来的User-Agent
                                                 .addHeader("User-Agent",WebSettings.getDefaultUserAgent(KWApplication.getInstance()) )
                                                 .build();
                                         return chain.proceed(request);
                                     }
                                 });
        okBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        if (HttpConfig.HOST.startsWith("https")){

            SSLContext contextSSL = null;
            try {
                contextSSL = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                contextSSL.init(null, new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }}, new SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            okBuilder.sslSocketFactory( TrustAllSSLSocketFactory.getDefaultfactory(),new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            });

        }
        okBuilder.cookieJar(new CookiesManager(new PersistentCookieStore(KWApplication.getInstance()), null));
        mClient = okBuilder.build();
    }
}
