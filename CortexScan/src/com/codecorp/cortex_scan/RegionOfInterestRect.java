package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

public class RegionOfInterestRect extends View {

    int mROITop;
    int mROILeft;
    int mROIWidth;
    int mROIHeight;
    int mPreviewWidth;
    int mPreviewHeight;
    Context mContext;

    public RegionOfInterestRect(Context context, int screenTop, int screenLeft, int screenWidth, int screenHeight, int previewWidth, int previewHeight) {
        super(context);
        mContext = context;
        this.mROITop = screenTop;
        this.mROILeft = screenLeft;
        this.mROIWidth = screenWidth;
        this.mROIHeight = screenHeight;
        this.mPreviewWidth = previewWidth;
        this.mPreviewHeight = previewHeight;
    }

    public RegionOfInterestRect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onDraw(Canvas canvas) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return;
        int rotation = wm.getDefaultDisplay().getRotation();
        drawPortrait(canvas, 0);
        switch (rotation) {
            case Surface.ROTATION_0:
                drawPortrait(canvas, 0);
                break;
            case Surface.ROTATION_90:
                break;
            case Surface.ROTATION_180:
                break;
            case Surface.ROTATION_270:
                break;
            default:
                break;
        }
    }

    private void drawPortrait(Canvas c, int rotate) {
        Paint mPaint = new Paint();
        mPaint.setColor(1748159794);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        Path mPath = new Path();
        c.drawRect(mROILeft, (mROITop), (mROILeft + mROIWidth), (mROITop + mROIHeight), mPaint);
    }
}
