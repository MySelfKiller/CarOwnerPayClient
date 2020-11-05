package com.kayu.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AdaptiveHeightViewPager extends ViewPager {

    private int current;
    private int height = 0;
    /**
     * 保存position与对于的View
     */
    private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<Integer, View>();

    private boolean scrollble = true;

    public AdaptiveHeightViewPager(Context context) {
        super(context);
    }

    public AdaptiveHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mChildrenViews.size() > current) {
            View child = mChildrenViews.get(current);
            if (child != null) {
                child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                height = child.getMeasuredHeight();
            }
        }

        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void resetHeight(int current) {
        this.current = current;
        if (mChildrenViews.size() > current) {

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
            } else {
                layoutParams.height = height;
            }
            setLayoutParams(layoutParams);
        }
    }

    /**
     * 保存position与对于的View
     */
    public void setObjectForPosition(View view, int position) {
        mChildrenViews.put(position, view);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return true;
        }
        return super.onTouchEvent(ev);
    }
    private int startX;
    private int startY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!scrollble) {
            switch (arg0.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) arg0.getX();
                    startY = (int) arg0.getY();
                    break;
                case MotionEvent.ACTION_MOVE:

//                int dX = (int) (ev.getX() - startX);
                    int dY = (int) (arg0.getY() - startX);
                    if (Math.abs(dY)>0)  // 说明上下方向滑动了
                    {
                        return false;
                    } else
                    {
                        return true;
                    }
                case MotionEvent.ACTION_UP:
                    break;
            }
        }

        return super.onInterceptTouchEvent(arg0);
    }

    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }


}
