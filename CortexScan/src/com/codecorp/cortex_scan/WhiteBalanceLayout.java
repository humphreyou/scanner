package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import java.util.ArrayList;

public class WhiteBalanceLayout extends LinearLayout {

    int widthSize = 0;
    int heightSize = 0;

    public WhiteBalanceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WhiteBalanceLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childHeight = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                //meausre the child within parent dimensions
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                //Grab the measured height of the child
                int childWidth = widthSize / (count + 4);
                childHeight = childWidth;
                @SuppressLint("DrawAllocation") ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(childWidth, childHeight);
                child.setLayoutParams(lp);
            }
        }
        setMeasuredDimension(widthSize, childHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int margin = ((widthSize - (getChildAt(0).getMeasuredWidth() * count)) / count);
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.setBackgroundResource(matchResourceToWhiteBalance(child.getTag().toString()));

            final int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            child.layout(i * width + ((i + 1) * margin), 0, (width * i) + ((i + 1) * margin) + width, height);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public ArrayList<View> getWhiteBalanceChildren() {
        ArrayList<View> buttonArr = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++)
            buttonArr.add(getChildAt(i));
        return buttonArr;
    }

    // Keep all Images in array
    private Integer[] mWhiteBalanceIds = {
            R.drawable.wb_auto_white_64,
            R.drawable.wb_cloudy_white_64,
            R.drawable.wb_daylight_white_64,
            R.drawable.wb_fluorescent_white_64,
            R.drawable.wb_shade_white_64,
            R.drawable.wb_twilight_white_sq_64,
            R.drawable.wb_incandescent_64,
            R.drawable.wb_cloudy_daylight_white_64
    };


    private Integer matchResourceToWhiteBalance(String mode) {
        if (mode.equals("CONTROL_AWB_MODE_OFF")) return mWhiteBalanceIds[0];
        if (mode.equals("CONTROL_AWB_MODE_AUTO")) return mWhiteBalanceIds[0];
        if (mode.equals("CONTROL_AWB_MODE_INCANDESCENT")) return mWhiteBalanceIds[6];
        if (mode.equals("CONTROL_AWB_MODE_FLUORESCENT")) return mWhiteBalanceIds[3];
        if (mode.equals("CONTROL_AWB_MODE_WARM_FLUORESCENT")) return mWhiteBalanceIds[3];
        if (mode.equals("CONTROL_AWB_MODE_DAYLIGHT")) return mWhiteBalanceIds[2];
        if (mode.equals("CONTROL_AWB_MODE_CLOUDY_DAYLIGHT")) return mWhiteBalanceIds[7];
        if (mode.equals("CONTROL_AWB_MODE_TWILIGHT")) return mWhiteBalanceIds[5];
        if (mode.equals("CONTROL_AWB_MODE_SHADE")) return mWhiteBalanceIds[4];
        if (mode.equals("CONTROL_AWB_MODE_OFF")) return mWhiteBalanceIds[0];
        if (mode.equals("auto")) return mWhiteBalanceIds[0];
        if (mode.equals("incandescent")) return mWhiteBalanceIds[6];
        if (mode.equals("fluorescent")) return mWhiteBalanceIds[3];
        if (mode.equals("warm-fluorescent")) return mWhiteBalanceIds[3];
        if (mode.equals("daylight")) return mWhiteBalanceIds[2];
        if (mode.equals("cloudy-daylight")) return mWhiteBalanceIds[7];
        if (mode.equals("twilight")) return mWhiteBalanceIds[5];
        if (mode.equals("shade")) return mWhiteBalanceIds[4];
        return mWhiteBalanceIds[0];
    }

}
