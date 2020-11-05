package com.kayu.car_owner_pay.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

@GlideModule
public class UnsafeOkHttpGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide_cache", 1025*1024*50));
        //指定内存缓存大小
//        builder.setMemoryCache(new LruResourceCache(1024*3));
//        //全部的内存缓存用来作为图片缓存
//        builder.setBitmapPool(new LruBitmapPool(1024*6));
        // Apply options to the builder here.
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
