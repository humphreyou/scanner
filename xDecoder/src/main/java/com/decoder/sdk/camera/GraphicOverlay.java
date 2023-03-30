package com.decoder.sdk.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.google.android.gms.common.images.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import androidx.annotation.NonNull;

public final class GraphicOverlay extends View {
    private final Object lock;
    private int previewWidth;
    private float widthScaleFactor;
    private int previewHeight;
    private float heightScaleFactor;
    private final ArrayList graphics;
    private HashMap findViewCache;

    public final void clear() {
        synchronized(lock) {
            graphics.clear();
        }
        this.postInvalidate();
    }

    public final void add(@NonNull GraphicOverlay.Graphic graphic) {
        synchronized(lock) {
            graphics.add(graphic);
        }
    }

    public final void setCameraInfo(@NonNull CameraSource cameraSource) {

        Size var10000 = cameraSource.getPreviewSize();
        if (var10000 != null) {
            Size previewSize = var10000;
            Utils var3 = Utils.Instance;
            Context var10001 = this.getContext();

            if (var3.isPortraitMode(var10001)) {
                previewWidth = previewSize.getHeight();
                previewHeight = previewSize.getWidth();
            } else {
                previewWidth = previewSize.getWidth();
                previewHeight = previewSize.getHeight();
            }

        }
    }

    public final float translateX(float x) {
        return x * this.widthScaleFactor;
    }

    public final float translateY(float y) {
        return y * this.heightScaleFactor;
    }

    @NonNull
    public final RectF translateRect(@NonNull Rect rect) {
        return new RectF(translateX((float)rect.left),
                translateY((float)rect.top),
                translateX((float)rect.right),
                translateY((float)rect.bottom));
    }

    protected void onDraw(@NonNull Canvas canvas) {

        super.onDraw(canvas);
        if (previewWidth > 0 && previewHeight > 0) {
            widthScaleFactor = (float)getWidth() / (float)previewWidth;
            heightScaleFactor = (float)getHeight() / (float)previewHeight;
        }

        synchronized(lock) {
            for(int i = 0; i < graphics.size(); i++) {
                GraphicOverlay.Graphic it = (GraphicOverlay.Graphic)graphics.get(i);
                 it.draw(canvas);
            }
        }
    }

    public GraphicOverlay(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        this.lock = new Object();
        this.widthScaleFactor = 1.0F;
        this.heightScaleFactor = 1.0F;
        this.graphics = new ArrayList();
    }


    public abstract static class Graphic {
        private   final Context context;
        private   final GraphicOverlay overlay;
        protected final Context getContext() {
            return this.context;
        }
        public abstract void draw(@NonNull Canvas var1);
        protected final GraphicOverlay getOverlay() {
            return this.overlay;
        }
        protected Graphic(@NonNull GraphicOverlay overlay) {
            super();
            this.overlay = overlay;
            this.context = overlay.getContext();
        }
    }
}
