package com.kayu.car_owner_pay.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.kayu.car_owner_pay.R;
import com.kayu.utils.LogUtil;

public class FadingScrollView extends NestedScrollView {
    public FadingScrollView(@NonNull Context context) {
        super(context);
    }

    public FadingScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static String TAG = "-----------FadingScrollView----------";
    //渐变view
    private View fadingView;
    //滑动view的高度，如果这里fadingHeightView是一张图片，
    // 那么就是这张图片上滑至完全消失时action bar 完全显示，
    // 过程中透明度不断增加，直至完全显示
    private View fadingHeightView;
    private int oldY;
    //滑动距离，默认设置滑动500 时完全显示，根据实际需求自己设置
    private int fadingHeight = 500;
    private int fadingViewHeight = 200;


    public void setFadingView(View view){this.fadingView = view;}
    public void setFadingHeightView(View v){this.fadingHeightView = v;}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(fadingHeightView != null)
            fadingHeight = fadingHeightView.getMeasuredHeight();
        if (null != fadingView)
            fadingViewHeight = fadingView.getMeasuredHeight();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        LogUtil.e(TAG,"top="+t+"oldTop="+oldt);
//        l,t  滑动后 xy位置，
//        oldl lodt 滑动前 xy 位置-----
        float fading = t>fadingViewHeight ? fadingViewHeight : (t > 30 ? t : 0);
//        LogUtil.e(TAG,"fading="+fading);
//        if (t<fadingHeight-fadingViewHeight){
////            fadingView.setAlpha();
//            fadingView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
//        }
//        if (t>fadingHeight-fadingViewHeight){
//
//            fadingView.setBackgroundColor(getResources().getColor(R.color.black1));
//        }

        updateActionBarAlpha( fading / fadingViewHeight);

    }

    void updateActionBarAlpha(float alpha){
        try {
            setActionBarAlpha(alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setActionBarAlpha(float alpha) throws Exception{
        if(fadingView==null){
            throw new Exception("fadingView is null...");
        }
        fadingView.setAlpha(alpha);

    }
}
