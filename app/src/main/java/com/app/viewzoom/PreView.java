package com.app.viewzoom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Maurice on 2017/6/8.
 * email:zhang_mingxu@126.com
 */

public class PreView extends LinearLayout {

    private boolean isLong = false;
    private boolean isInit = false;
    private FrameLayout contentParent;
    private ZoomView zoomView;
    private long startTime;
    private boolean isIntercept = false;//用来处理子控件中有没有点击事件

    public PreView(Context context) {
        this(context, null);
    }

    public PreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        contentParent = (FrameLayout) ((Activity) getContext()).getWindow()
                .getDecorView().findViewById(android.R.id.content);
        zoomView = new ZoomView(getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                isLong = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - startTime > 150 && !isLong) {
                    isLong = true;
                    isIntercept = true;
                }
                break;
        }
        if (isLong) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isIntercept && System.currentTimeMillis() - startTime > 150 && !isLong) {
                    isLong = true;
                    isIntercept = true;
                }
                if (isLong && !isInit) {
                    isInit = true;
                    Bitmap bitmapFromView = getBitmapFromView(this);
                    zoomView.setInitCurBitmap(bitmapFromView);
                    contentParent.addView(zoomView);
                }
                if (isLong && isInit) {
                    zoomView.setCurShowPos(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isLong && isInit) {
                    contentParent.removeView(zoomView);
                    isLong = false;
                    isInit = false;
                }
                isIntercept = false;//恢复默认
                break;
        }
        if (isLong) {
            return true;
        } else if (!isIntercept) {//此时没有点击事件需要拦截处理事件，
            // 有点击事件的时候，只用拦截才会进去onTouch,此时isLong为true
            return true;
        } else {
            return false;
        }
    }

    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        // Draw view to canvas
        v.draw(c);
        return b;
    }
}
