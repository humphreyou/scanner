package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class CrosshairsView extends View {
    private float logicalDensity;
    private Paint mPaint;

    public CrosshairsView(Context context) {
        super(context);
        setup(context);
    }

    public CrosshairsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;
        windowManager.getDefaultDisplay().getMetrics(metrics);
        logicalDensity = metrics.density;
        mPaint = new Paint();
        Resources resources = getResources();
        mPaint.setColor(resources.getColor(R.color.cross_hairs));
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int xMid = width / 2;
        int yMid = height / 2;
        int bigSize = dpToPx(10);
        int smallSize = dpToPx(1);
        canvas.drawRect(xMid - bigSize, yMid - bigSize, xMid + bigSize, yMid + bigSize, mPaint);
        canvas.drawRect(xMid - bigSize * 3, yMid - smallSize, xMid + bigSize * 3, yMid + smallSize, mPaint);
        canvas.drawRect(xMid - smallSize, yMid - bigSize * 3, xMid + smallSize, yMid + bigSize * 3, mPaint);
    }

    private int dpToPx(int dp) {
        return (int) Math.ceil(dp * logicalDensity);
    }
}
