package com.kayu.car_owner_pay.activity;

import android.content.Context;
import android.widget.ImageView;

import com.kayu.car_owner_pay.KWApplication;
import com.youth.banner.loader.ImageLoader;

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(final Context context, Object path, ImageView imageView) {
        KWApplication.getInstance().loadImg((String) path,imageView);
    }
}
