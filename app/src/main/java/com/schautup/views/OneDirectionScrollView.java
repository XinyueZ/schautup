package com.schautup.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class OneDirectionScrollView extends ScrollView {
    private GestureDetector mGestureDetector;
    private boolean mAllowTouch = true;

    public OneDirectionScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
		if (!isInEditMode())
			mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mGestureDetector != null)
			mGestureDetector.onTouchEvent(ev);
		if (mAllowTouch)
			return super.onInterceptTouchEvent(ev);
		return false;
    }

    // Return false if we're scrolling in the x direction  
    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        	if(Math.abs(distanceY) > Math.abs(distanceX)) {
            	mAllowTouch = true;
                return true;
            }
            mAllowTouch = false;
            return false;
        }
    }
}