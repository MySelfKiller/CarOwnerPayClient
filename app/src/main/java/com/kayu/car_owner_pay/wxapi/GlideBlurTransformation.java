/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.kayu.car_owner_pay.wxapi;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.kayu.car_owner_pay.KWApplication;

import java.security.MessageDigest;

public class GlideBlurTransformation extends BitmapTransformation {


  public GlideBlurTransformation(Context context) {
    super();
  }

  @Override
  protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
    int targetWidth = KWApplication.getInstance().displayWidth;
//    LogUtil.e("hm","displayWidth="+ targetWidth);
//    LogUtil.e("hm","source.getHeight()="+source.getHeight()+",source.getWidth()="+source.getWidth()+",targetWidth="+targetWidth);

    if(toTransform.getWidth()==0){
      return toTransform;
    }

    //如果图片小于设置的宽度，则返回原图
    double aspectRatio = (double) outHeight / outWidth;
    int targetHeight = (int) (targetWidth * aspectRatio);
    if (targetHeight != 0 && targetWidth != 0) {
      Bitmap result = Bitmap.createScaledBitmap(toTransform, targetWidth, targetHeight, true);
      if (result != toTransform) {
        // Same bitmap is returned if sizes are the same
        toTransform.recycle();
      }
      return result;
    } else {
      return toTransform;
    }
  }

  @Override
  public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

  }
}
