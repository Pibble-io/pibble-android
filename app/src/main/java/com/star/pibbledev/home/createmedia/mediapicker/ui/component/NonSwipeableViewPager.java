package com.star.pibbledev.home.createmedia.mediapicker.ui.component;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipeableViewPager extends ViewPager {
    private boolean swipe = false;

    public boolean isSwipeable() {
        return swipe;
    }

    public void setSwipeable(boolean swipe) {
        this.swipe = swipe;
    }

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {

        if (swipe) {
            return super.onInterceptTouchEvent(arg0);
        }

        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if (swipe) {
            return super.onTouchEvent(event);
        }

        return false;
    }
}