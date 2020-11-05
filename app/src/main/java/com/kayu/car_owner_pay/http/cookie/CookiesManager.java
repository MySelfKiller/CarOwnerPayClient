package com.kayu.car_owner_pay.http.cookie;


import com.kayu.car_owner_pay.http.NetUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookiesManager implements CookieJar {
    private PersistentCookieStore cookieStore;
    private SetTokenCallBack setTokenCallBack;
//    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    public CookiesManager(PersistentCookieStore cookieStore, SetTokenCallBack setTokenCallBack){
        this.cookieStore = cookieStore;
        this.setTokenCallBack = setTokenCallBack;
    }
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//        LogUtil.e("hm","------saveFromResponse-----");
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
//                LogUtil.e("hm","cookieItem respones = name:"+item.name()+"----value:"+item.value());
                if (item.name().equals("token")){
                    NetUtil.token = item.value();
                    if (null != setTokenCallBack){
                        setTokenCallBack.setToken(item.value());
                    }

//                    LogUtil.e("hm","------token_key-----"+KWApplication.getInstance().token_key);
                }
                cookieStore.add(url, item);
            }
//            cookieStore.put(url.host(), cookies);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
//        SharedPreferences sp = KWApplication.getInstance().getApplicationContext().getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
////        boolean isLogin = sp.getBoolean(Constants.isLogin,false);
////        if (!isLogin){
//////            cookieStore.removeAll();
////            List<Cookie> cookies = cookieStore.getCookies();
////            for (Cookie item : cookies){
////                if (item.name().equals("token")){
////                    cookieStore.remove(url,item);
////
////                }
////            }
////        }

        List<Cookie> cookies = cookieStore.get(url);
//        if ( null == cookies || cookies.size()==0){
//            cookies.add(new Cookie.Builder().name("token").value("fAM3WaRwKsJPigEIlBfHWXxk0z+UsCgq1tN4Yxas74upza4rpEEX1nUrV/a4RUSBUeQmmOrs2" +
//                    "lffXHX5kGgWWL1PmGqS6gXXvbilvwc7T39SR2oPtCYtlqz74gNOrzrjXgbXBSvG0Oh8ANnU9mLfQhP81frSXe6ID1JYr0CVEEqiUqkSPsI" +
//                    "7tKAy2LRgD8gGCQ/eq6kbt/Kh+B+a9x4g3YqGx7u5QumRV1SQXPEi1GOIj923Qhdy419z2d7aRGNI2c4HfhNbrCS0Bt080RS7rThrl+pZRe" +
//                    "r3Twaj/fRdKYFVAly4FmN7JjHqD80HdRs+lPbdRTF3ync41N55UdEPw7hNNGrLxZEcImo9IDSeREE=").domain("www.kakayuy.com").build());
//        }
//        List<Cookie> cookies = cookieStore.get(url.host());

//        LogUtil.e("hm","------loadForRequest-----");
        if (null != cookies ){
            for (Cookie item : cookies) {
//                LogUtil.e("hm","cookieItem request = name:"+item.name()+"----value:"+item.value());
//            cookieStore.add(url, item);
                if (item.name().equals("token")){
                    NetUtil.token = item.value();
                    if (null != setTokenCallBack){
                        setTokenCallBack.setToken(item.value());
                    }
//                    LogUtil.e("hm","------token_key-----"+KWApplication.getInstance().token_key);
                }
            }
        }
        return cookies != null ? cookies : new ArrayList<Cookie>();

//        return cookies;
    }
}
