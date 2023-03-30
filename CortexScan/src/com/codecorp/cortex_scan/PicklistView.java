package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class PicklistView extends View {

    private static final float FRAME_STROKE_WIDTH = 4.0f;
    private static final float FRAME_CORNER_RADIUS = 8.0f;

    private Paint mPaintFill;
    private Paint mPaintStroke;

    public PicklistView(Context context) {
        super(context);
        setup(context);
    }

    public PicklistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;
        windowManager.getDefaultDisplay().getMetrics(metrics);

        Resources res = getResources();

        mPaintFill = new Paint();
        mPaintFill.setColor(res.getColor(R.color.picklist_fill));
        mPaintFill.setStyle(Paint.Style.FILL);

        mPaintStroke = new Paint();
        mPaintStroke.setColor(res.getColor(R.color.picklist_stroke));
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(FRAME_STROKE_WIDTH);
    }

    @Override
    public void onDraw(Canvas canvas) {

        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float xMid = width / 2;
        float yMid = height / 2;

        @SuppressLint("DrawAllocation") RectF frameRect = new RectF(xMid - width / 2, yMid - height / 2,
                xMid + width / 2, yMid + height / 2);
        canvas.drawRoundRect(frameRect, FRAME_CORNER_RADIUS, FRAME_CORNER_RADIUS, mPaintFill);

        frameRect.inset((int) (FRAME_STROKE_WIDTH / 2), (int) (FRAME_STROKE_WIDTH / 2));
        canvas.drawRoundRect(frameRect, FRAME_CORNER_RADIUS, FRAME_CORNER_RADIUS, mPaintStroke);
    }
}
