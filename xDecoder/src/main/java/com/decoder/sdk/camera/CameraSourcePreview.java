package com.decoder.sdk.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.widget.FrameLayout;
import com.google.android.gms.common.images.Size;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import com.decoder.sdk.R;

public final class CameraSourcePreview extends FrameLayout {
    private final SurfaceView surfaceView;
    private GraphicOverlay graphicOverlay;
    private boolean startRequested;
    private boolean surfaceAvailable;
    private CameraSource cameraSource;
    private Size cameraPreviewSize;
    private static final String TAG = "CameraSourcePreview";

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.graphicOverlay = this.findViewById(R.id.camera_preview_graphic_overlay);
    }

    public final void start(@NotNull CameraSource cameraSource) throws IOException {
        this.cameraSource = cameraSource;
        this.startRequested = true;
        this.startIfReady();
    }

    public final void stop() {
        if (cameraSource != null) {
            cameraSource.stop();
            this.cameraSource   = null;
            this.startRequested = false;
        }

    }

    private final void startIfReady() throws IOException {
        if (startRequested && surfaceAvailable) {
            if (cameraSource != null) {
                SurfaceHolder holder = surfaceView.getHolder();
                cameraSource.start(holder);
            }

            requestLayout();
            if (graphicOverlay != null) {
                if (cameraSource != null) {
                    graphicOverlay.setCameraInfo(cameraSource);
                }
                graphicOverlay.clear();
            }

            startRequested = false;
        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int layoutWidth  = right - left;
        int layoutHeight = bottom - top;
        Size PreviewSize;
        if (cameraSource != null) {
            PreviewSize = cameraSource.getPreviewSize();
            if (PreviewSize != null) {
                cameraPreviewSize = PreviewSize;
            }
        }

        float previewSizeRatio = 0.0f;

        if (cameraPreviewSize != null) {
            previewSizeRatio = Utils.Instance.isPortraitMode(getContext()) ?
                    (float)cameraPreviewSize.getHeight() / (float)cameraPreviewSize.getWidth() :
                    (float)cameraPreviewSize.getWidth() / (float)cameraPreviewSize.getHeight();
        } else {
            previewSizeRatio = (float)layoutWidth / (float)layoutHeight;
        }

        int childHeight = (int)((float)layoutWidth / previewSizeRatio);
        int excessLenInHalf;
        int i;
        if (childHeight <= layoutHeight) {
            excessLenInHalf = 0;
            for(i = this.getChildCount(); excessLenInHalf < i; ++excessLenInHalf) {
                this.getChildAt(excessLenInHalf).layout(0, 0, layoutWidth, childHeight);
            }
        } else {
            excessLenInHalf = (childHeight - layoutHeight) / 2;

            i = 0;
            for(int childCount = getChildCount(); i < childCount; ++i) {
                View childView = this.getChildAt(i);
                if(childView.getId() ==  R.id.static_overlay_container) {
                    childView.layout(0, 0, layoutWidth, layoutHeight);
                } else {
                    childView.layout(0, -excessLenInHalf, layoutWidth, layoutHeight + excessLenInHalf);
                }
            }
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", (Throwable)e);
        }
    }

    public CameraSourcePreview(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
        SurfaceView sfView = new SurfaceView(context);
        sfView.getHolder().addCallback(new CameraSourcePreview.SurfaceCallback());
        this.addView(sfView);
        this.surfaceView = sfView;
    }

    public static final boolean access(CameraSourcePreview $this) {
        return $this.surfaceAvailable;
    }

    private final class SurfaceCallback implements Callback {
        public void surfaceCreated(@NotNull SurfaceHolder surface) {
            surfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.",  (Throwable)e);
            }
        }

        public void surfaceDestroyed(@NotNull SurfaceHolder surface) {
            CameraSourcePreview.this.surfaceAvailable = false;
        }

        public void surfaceChanged(@NotNull SurfaceHolder holder, int format, int width, int height) {
        }

        public SurfaceCallback() {
        }
    }

}
