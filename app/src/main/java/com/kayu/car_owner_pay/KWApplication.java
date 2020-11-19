package com.kayu.car_owner_pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.model.MapInfoModel;
import com.kayu.utils.Constants;
import com.kayu.utils.LogUtil;
import com.kayu.utils.StringUtil;
import com.kayu.utils.Utils;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.callback.ImageCallback;
import com.kayu.utils.location.CoordinateTransformUtil;
import com.kayu.utils.location.LocationManager;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class KWApplication extends Application {

    public int displayWidth = 0;
    public int displayHeight = 0;
    private static KWApplication self;
    public String token_key;
//    private Picasso picasso;
    private String photographName;
    private String fileName;
    public String token;//登录成功后返回的token
    private int downloadIndex;

    public static KWApplication getInstance() {
        return self;
    }

    @Override
    public void onCreate() {
        self = this;
        super.onCreate();
//        setFornts();
        initDialogSetting();
        LocationManager.init(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO );

//        ZoomMediaLoader.getInstance().init(new TestImageLoader());
        SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
        token = sp.getString(Constants.token,"");
        LogUtil.setIsDebug( BuildConfig.LOG_DEBUG);
    }

//    public Picasso getPicasso(){
//        return picasso;
//    }
//    private void initPicasso(Context context){
//        LocationManager.init(this);
//        Picasso.Builder builder = new Picasso.Builder(context);
//        builder.downloader(new OkHttp3Downloader(OkHttpManager.getInstance().getHttpClient()));
//        picasso = builder.listener(new Picasso.Listener() {
//            @Override
//            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                exception.printStackTrace();
//            }
//        })
//                .loggingEnabled(true)// 打log
//                .build();
//    }

    private void initDialogSetting(){
        DialogSettings.isUseBlur = true;                   //是否开启模糊效果，默认关闭
        DialogSettings.modalDialog = false;                 //是否开启模态窗口模式，一次显示多个对话框将以队列形式一个一个显示，默认关闭
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;          //全局主题风格，提供三种可选风格，STYLE_MATERIAL, STYLE_KONGZUE, STYLE_IOS
        DialogSettings.theme = DialogSettings.THEME.LIGHT;          //全局对话框明暗风格，提供两种可选主题，LIGHT, DARK
        DialogSettings.tipTheme = (DialogSettings.THEME.LIGHT);       //全局提示框明暗风格，提供两种可选主题，LIGHT, DARK
//        DialogSettings.titleTextInfo = (TextInfo);              //全局对话框标题文字样式
//        DialogSettings.menuTitleInfo = (TextInfo);              //全局菜单标题文字样式
//        DialogSettings.menuTextInfo = (TextInfo);               //全局菜单列表文字样式
//        DialogSettings.contentTextInfo = (TextInfo);            //全局正文文字样式
//        DialogSettings.buttonTextInfo = (TextInfo);             //全局默认按钮文字样式
//        DialogSettings.buttonPositiveTextInfo = (TextInfo);     //全局焦点按钮文字样式（一般指确定按钮）
//        DialogSettings.inputInfo = (InputInfo);                 //全局输入框文本样式
//        DialogSettings.backgroundColor = (ColorInt);            //全局对话框背景颜色，值0时不生效
//        DialogSettings.cancelable = (boolean);                  //全局对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipDialog）以及等待框（TipDialog）
//        DialogSettings.cancelableTipDialog = (boolean);         //全局提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
//        DialogSettings.DEBUGMODE = (boolean);                   //是否允许打印日志
//        DialogSettings.blurAlpha = (int);                       //开启模糊后的透明度（0~255）
//        DialogSettings.systemDialogStyle = (styleResId);        //自定义系统对话框style，注意设置此功能会导致原对话框风格和动画失效
//        DialogSettings.dialogLifeCycleListener = (DialogLifeCycleListener);  //全局Dialog生命周期监听器
//        DialogSettings.defaultCancelButtonText = (String);      //设置 BottomMenu 和 ShareDialog 默认“取消”按钮的文字
//        DialogSettings.tipBackgroundResId = (drawableResId);    //设置 TipDialog 和 WaitDialog 的背景资源
//        DialogSettings.tipTextInfo = (InputInfo);               //设置 TipDialog 和 WaitDialog 文字样式
//        DialogSettings.autoShowInputKeyboard = (boolean);       //设置 InputDialog 是否自动弹出输入法
//        DialogSettings.okButtonDrawable = (drawable);           //设置确定按钮背景资源
//        DialogSettings.cancelButtonDrawable = (drawable);       //设置取消按钮背景资源
//        DialogSettings.otherButtonDrawable = (drawable);        //设置其他按钮背景资源
//        Notification.mode = Notification.Mode.FLOATING_WINDOW;  //通知实现方式。可选 TOAST 使用自定义吐司实现以及 FLOATING_WINDOW 悬浮窗实现方式

//检查 Renderscript 兼容性，若设备不支持，DialogSettings.isUseBlur 会自动关闭；
        boolean renderscriptSupport = DialogSettings.checkRenderscriptSupport(getApplicationContext());

        DialogSettings.init();                           //初始化清空 BaseDialog 队列

    }

    private void setFornts(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        try
        {
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null, typeface);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

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

    @SuppressLint("CheckResult")
    public void loadImg(final Activity activity, String url, SubsamplingScaleImageView view, final Callback callback){
        final String mUrl;
        if (url.startsWith("http")){
            mUrl = url;
        }else {
            mUrl = HttpConfig.HOST+url;
        }
//        KWApplication.getInstance().getPicasso()
//                .load(mUrl)
////                .placeholder(R.mipmap.ic_defult_img)
////                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .transform(new BlurTransformation())
//                .into(view, callback);
        Glide.with(this)
                .load(mUrl)
                .downloadOnly(new ViewTarget<SubsamplingScaleImageView, File>(view) {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
//                        Toasty.warning(activity, "开始渲染", Toast.LENGTH_SHORT).show();
                        view.setImage(ImageSource.uri(Uri.fromFile(resource)));
//                        Toasty.warning(activity, "渲染完成", Toast.LENGTH_SHORT).show();
//                        view.setImage(ImageSource.asset("norway_test.jpg"));
                        callback.onSuccess();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        callback.onError();
                    }
                });

    }

    public void loadImg(String url, ImageView view){
        final String mUrl;
        if (StringUtil.isEmpty(url)){
            return;
        }
        if (url.startsWith("http")){
            mUrl = url;
        }else {
            mUrl = HttpConfig.HOST+url;
        }

        Glide.with(this).load(mUrl).into(view);
    }
    //加载本地资源
    public void loadImg(int ids, ImageView view){
        final String mUrl;

        Glide.with(this).load(ids).into(view);
    }
    //带回调的图片加载
    public void loadImg(String url, ImageView view, ImageCallback callback){
        final String mUrl;
        if (StringUtil.isEmpty(url)){
            return;
        }
        if (url.startsWith("http")){
            mUrl = url;
        }else {
            mUrl = HttpConfig.HOST+url;
        }
        Glide.with(this).asBitmap().load(mUrl).into(new CustomTarget<Bitmap>() {


            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                view.setImageBitmap(resource);
                callback.onSuccess(resource);

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                callback.onError();
            }
        });

    }

    public String getDataPath() {
        return Utils.getEnaviBaseStorage(this);
    }



    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    public void callPhone(final Activity context, final String phoneNum){

        MessageDialog.show((AppCompatActivity) context, "拨打电话", phoneNum, "呼叫", "取消").setOkButton(new OnDialogButtonClickListener() {
            @Override
            public boolean onClick(BaseDialog baseDialog, View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + phoneNum);
                intent.setData(data);
                context.startActivity(intent);
                return false;
            }
        });
    }

    public void toNavi(Context context,String latitude, String longtitude, String address, String flag){

        final List<MapInfoModel> mapList = getMapInfoModels(context);
        if (mapList == null) return;
        ArrayList<CharSequence> menuArr = new ArrayList<>();
        for (MapInfoModel model : mapList) {
            menuArr.add(model.mapName);
        }

        double[] bdCoordinate;
        double[] gcj02Coordinate;
        switch (flag) {
            case "WGS84":
                bdCoordinate = CoordinateTransformUtil.wgs84tobd09(Double.parseDouble(longtitude),Double.parseDouble(latitude));
                gcj02Coordinate = CoordinateTransformUtil.wgs84togcj02(Double.parseDouble(longtitude),Double.parseDouble(latitude));
                break;
            case "GCJ02":
                bdCoordinate = CoordinateTransformUtil.gcj02tobd09(Double.parseDouble(longtitude),Double.parseDouble(latitude));
                gcj02Coordinate = new double[]{Double.parseDouble(longtitude),Double.parseDouble(latitude)};
                break;
            case "BD09":
                gcj02Coordinate = CoordinateTransformUtil.bd09togcj02(Double.parseDouble(longtitude),Double.parseDouble(latitude));
                bdCoordinate = new double[]{Double.parseDouble(longtitude),Double.parseDouble(latitude)};
                break;
            default:
                gcj02Coordinate = new double[]{Double.parseDouble(longtitude),Double.parseDouble(latitude)};
                bdCoordinate = new double[]{Double.parseDouble(longtitude),Double.parseDouble(latitude)};
                break;
        }
        BottomMenu.show((AppCompatActivity) context, menuArr, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (text) {
                    case "高德地图":
                        goGaodeMap(context, String.valueOf(gcj02Coordinate[1]), String.valueOf(gcj02Coordinate[0]), address);
                        break;
                    case "谷歌地图":
                        goGoogleMap(context, String.valueOf(gcj02Coordinate[1]), String.valueOf(gcj02Coordinate[0]), address);
                        break;
                    case "百度地图":
                        goBaiduMap(context, String.valueOf(bdCoordinate[1]), String.valueOf(bdCoordinate[0]), address);
                        break;
                    case "腾讯地图":
                        goTencentMap(context, String.valueOf(gcj02Coordinate[1]), String.valueOf(gcj02Coordinate[0]), address);
                        break;
                }
            }
        });
    }

    private List<MapInfoModel> getMapInfoModels(Context context) {
        final List<MapInfoModel> mapList = new ArrayList<MapInfoModel>();

        if (isNavigationApk(context, "com.autonavi.minimap")) {
            MapInfoModel model = new MapInfoModel();
            model.mapId = "0";
            model.mapName = "高德地图";
            mapList.add(model);
        }
        if (isNavigationApk(context, "com.google.android.apps.maps")) {
            MapInfoModel model = new MapInfoModel();
            model.mapId = "1";
            model.mapName = "谷歌地图" ;
            mapList.add(model);
        }
        if (isNavigationApk(context, "com.baidu.BaiduMap")) {
            MapInfoModel model = new MapInfoModel();
            model.mapId = "2";
            model.mapName = "百度地图" ;
            mapList.add(model);
        }
        if (isNavigationApk(context, "com.tencent.map")) {
            MapInfoModel model = new MapInfoModel();
            model.mapId = "3";
            model.mapName = "腾讯地图";
            mapList.add(model);
        }
        if (mapList.size() == 0) {
            TipDialog.show((AppCompatActivity) context,"您尚未安装导航APP", TipDialog.TYPE.WARNING);
            return null;

        }
        return mapList;
    }

    /**
     * 判断手机中是否有导航app
     * @param context
     * @param packagename 包名
     */
    public static boolean isNavigationApk(Context context, String packagename) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(packagename)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }


    /**
     * 跳转到百度地图
     * @param activity
     * @param latitude 纬度
     * @param longtitude 经度
     * @param address 终点
     * */
    private void goBaiduMap(Context activity,String latitude, String longtitude, String address) {
        if (isNavigationApk(activity, "com.baidu.BaiduMap")) {
            try {
                Intent intent = Intent.getIntent("intent://map/direction?destination=latlng:"
                        + latitude + ","
                        + longtitude + "|name:" + address + //终点：该地址会在导航页面的终点输入框显示
                        "&mode=driving&" + //选择导航方式 此处为驾驶
                        "region=" + //
                        "&src=#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                activity.startActivity(intent);
            } catch (URISyntaxException e) {
                Log.e("goError", e.getMessage());
            }
        } else {
            Toast.makeText(activity, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 跳转到高德地图
     * @param activity
     * @param latitude 纬度
     * @param longtitude 经度
     * @param address 终点
     * */
    private void goGaodeMap(Context activity,String latitude, String longtitude, String address) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            try {
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                        + "&lon=" + longtitude + "&dev=0");
                activity.startActivity(intent);
            } catch (URISyntaxException e) {
                Log.e("goError", e.getMessage());
            }
        } else {
            Toast.makeText(activity, "您尚未安装高德地图", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 跳转到谷歌地图
     * @param activity
     * @param latitude 纬度
     * @param longtitude 经度
     * @param address 终点
     * */
    private void goGoogleMap(Context activity,String latitude, String longtitude, String address) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu" +
                        ".google" + ".cn/maps?hl=zh&mrt=loc&q=" + latitude + "," + longtitude + "(" + address + ")"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "您尚未安装谷歌地图", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到腾讯地图
     * @param activity
     * @param latitude 纬度
     * @param longtitude 经度
     * @param address 终点
     * */
    private void goTencentMap(Context activity,String latitude, String longtitude, String address) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("qqmap://map/routeplan?type=bus&from=我的位置&fromcoord=0,0"
                    + "&to=" + address
                    + "&tocoord=" + latitude + "," + longtitude
                    + "&policy=1&referer=myapp"));
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "您尚未安装谷歌地图", Toast.LENGTH_SHORT).show();
        }
    }


}
