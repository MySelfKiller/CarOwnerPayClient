package com.kayu.car_owner_pay.extend;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.alibaba.triver.kit.api.proxy.IImageProxy;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

public class ImageImpl implements IImageProxy {
    public ImageImpl(Application application){
        Fresco.initialize(application);
    }
    @Override
    public void setImageUrl(final ImageView view, String url, ImageStrategy imageStrategy) {
        if (view == null || view.getLayoutParams() == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            view.setImageBitmap(null);
            return;
        }
        String temp = url;
        if (url.startsWith("//")) {
            temp = "http:" + url;
        }
        if (view.getLayoutParams().width <= 0 || view.getLayoutParams().height <= 0) {
            return;
        }

        Uri uri = Uri.parse(temp);

        ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                .setBackgroundColor(Color.GREEN)
                .build();

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setImageDecodeOptions(decodeOptions)
                .setAutoRotateEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();

        if(view instanceof DraweeView){
            Log.d("FrescoImageAdapter","load: "+url);
            ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(
                        String id,
                        @Nullable ImageInfo imageInfo,
                        @Nullable Animatable anim) {
                    if (imageInfo == null) {
                        return;
                    }
                    QualityInfo qualityInfo = imageInfo.getQualityInfo();
                    FLog.d("Final image received! " +
                                    "Size %d x %d",
                            "Quality level %d, good enough: %s, full quality: %s",
                            imageInfo.getWidth(),
                            imageInfo.getHeight(),
                            qualityInfo.getQuality(),
                            qualityInfo.isOfGoodEnoughQuality(),
                            qualityInfo.isOfFullQuality());
                }

                @Override
                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                    FLog.d("","Intermediate image received");
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    FLog.e(getClass(), throwable, "Error loading %s", id);
                }
            };
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setControllerListener(controllerListener)
                    .setUri(uri)
                    .setImageRequest(request)
                    .build();
            ((DraweeView)view).setController(controller);

        }else {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>>
                    dataSource = imagePipeline.fetchDecodedImage(request, new Object());
            DataSubscriber dataSubscriber =
                    new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
                        @Override
                        public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
                            if (imageReference != null) {
                                try {
                                    // do something with the image
                                    Preconditions.checkState(CloseableReference.isValid(imageReference));
                                    CloseableImage closeableImage = imageReference.get();
                                    if (closeableImage instanceof CloseableStaticBitmap) {
                                        CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
                                        view.setImageBitmap(closeableStaticBitmap.getUnderlyingBitmap());
                                        // boolean hasResult =  null != closeableStaticBitmap.getUnderlyingBitmap();
                                    } else {
                                        throw new UnsupportedOperationException("Unrecognized image class: " + closeableImage);
                                    }
                                } finally {
                                    imageReference.close();
                                }
                            }
                        }

                        @Override
                        public void onFailureImpl(DataSource dataSource) {
                        }
                    };

            dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());
        }
    }

    @Override
    public void loadImage(String url, ImageStrategy imageStrategy, final ImageListener imageListener) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        Uri uri = Uri.parse(url);

        ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                .setBackgroundColor(Color.GREEN)
                .build();

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setImageDecodeOptions(decodeOptions)
                .setPostprocessor(new Postprocessor() {
                    @Override
                    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory){
                        Bitmap bitmap = sourceBitmap;
                        if(bitmap != sourceBitmap) {
                            sourceBitmap.recycle();
                        }

                        final CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                                bitmap.getWidth(),
                                bitmap.getHeight());

                        try {
                            final Bitmap destBitmap = bitmapRef.get();
                            final Canvas canvas = new Canvas(destBitmap);

                            canvas.drawBitmap(
                                    bitmap,
                                    null,
                                    new Rect(0, 0, destBitmap.getWidth(), destBitmap.getHeight()),
                                    new Paint());

                            return CloseableReference.cloneOrNull(bitmapRef);
                        } finally {
                            CloseableReference.closeSafely(bitmapRef);
                        }
                    }

                    @Override
                    public String getName() {
                        return "BlurTransformation(radius=)";
                    }

                    @Override
                    public CacheKey getPostprocessorCacheKey() {
                        return new SimpleCacheKey("BlurTransformation(radius=)");
                    }
                })
                .setAutoRotateEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(request, new Object());
        DataSubscriber dataSubscriber =
                new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
                    @Override
                    public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                        CloseableReference<CloseableImage> imageReference = dataSource.getResult();
                        if (imageReference != null) {
                            try {
                                // do something with the image
                                Preconditions.checkState(CloseableReference.isValid(imageReference));
                                CloseableImage closeableImage = imageReference.get();
                                if (closeableImage instanceof CloseableStaticBitmap) {
                                    CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
                                    if(imageListener != null){
                                        imageListener.onImageFinish(new BitmapDrawable(closeableStaticBitmap.getUnderlyingBitmap()));
                                    }
                                    // boolean hasResult =  null != closeableStaticBitmap.getUnderlyingBitmap();
                                } else {
                                    throw new UnsupportedOperationException("Unrecognized image class: " + closeableImage);
                                }
                            } finally {
                                imageReference.close();
                            }
                        }
                    }

                    @Override
                    public void onFailureImpl(DataSource dataSource) {
                        Log.w("TAG", "fail");
                    }
                };

        dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());
    }
}
