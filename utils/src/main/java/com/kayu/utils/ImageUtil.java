package com.kayu.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    public  static  Bitmap compoundBitmap(Bitmap bitmapOne,Bitmap bitmapTwo){
        Bitmap newBitmap = null;
        newBitmap=bitmapOne.copy(Bitmap.Config.ARGB_8888, true);
// newBitmap = Bitmap.createBitmap(bitmapOne.getWidth(),bitmapOne.getHeight(),bitmapOne.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        int w = bitmapOne.getWidth();
        int h = bitmapOne.getHeight();
        int w_2 = bitmapTwo.getWidth();
        int h_2 = bitmapTwo.getHeight();
// paint = new Paint();
//设置第二张图片的 左、上的位置坐标
        canvas.drawBitmap(bitmapTwo, (w-w_2)/2,
                h-h_2-20, paint);
        canvas.save();
// 存储新合成的图片
        canvas.restore();
        return newBitmap;
    }
    /**
     * 保存图片到指定路径
     *
     * @param context
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        // 保存图片至指定路径
        String storePath = Utils.getEnaviBaseStorage(context)+ File.separator  ;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //发送广播通知系统图库刷新数据
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
