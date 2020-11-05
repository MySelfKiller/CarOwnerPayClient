package com.kayu.utils.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageCallback {
    void onSuccess(Bitmap resource);

    void onError();

    public static class EmptyCallback implements ImageCallback {

        @Override public void onSuccess(Bitmap resource) {
        }

        @Override public void onError() {
        }
    }
}
