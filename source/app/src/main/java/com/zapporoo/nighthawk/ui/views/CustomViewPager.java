    package com.zapporoo.nighthawk.ui.views;
    
    import android.content.Context;
    import android.support.v4.view.ViewPager;
    import android.util.AttributeSet;
    import android.view.MotionEvent;
    
    public class CustomViewPager extends ViewPager {
    
    private boolean swipeEnabled;
        private boolean mLastPageEnabled = true;
        private int mLastPageIndex = 0;

        public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeEnabled = true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipeEnabled) {
            return super.onTouchEvent(event);
        }
    
        return false;
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.swipeEnabled) {
            return super.onInterceptTouchEvent(event);
        }
    
        return false;
    }
    
    public void setLastPageEnabled(boolean enabled) {
        mLastPageEnabled = enabled;
    }

    public void setLastPageIndex(int index) {
        mLastPageIndex = index;
    }
    
    public void setPagingEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    } }
