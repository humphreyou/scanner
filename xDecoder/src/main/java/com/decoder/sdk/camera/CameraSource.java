package com.decoder.sdk.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.decoder.sdk.settings.PreferenceUtils;
import com.google.android.gms.common.images.Size;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import com.decoder.sdk.R;

public final class CameraSource {
    private Camera camera;
    private int rotationDegrees;
    private Size previewSize;
    private Thread processingThread;
    private final CameraSource.FrameProcessingRunnable processingRunnable;
    private final Object processorLock;
    private FrameProcessor frameProcessor;
    private final IdentityHashMap bytesToByteBuffer;
    private final Context context;
    private final GraphicOverlay graphicOverlay;
    public static final int CAMERA_FACING_BACK = 0;
    private static final String TAG = "CameraSource";
    private static final int IMAGE_FORMAT = ImageFormat.NV21;
    private static final int MIN_CAMERA_PREVIEW_WIDTH = 400;
    private static final int MAX_CAMERA_PREVIEW_WIDTH = 1300;
    private static final int DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH = 640;
    private static final int DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT = 360;
    private static final float REQUESTED_CAMERA_FPS = 30.0F;

    @NonNull
    public final Size getPreviewSize() {
        return this.previewSize;
    }

    public final synchronized void start(@NonNull SurfaceHolder surfaceHolder) throws IOException {

        if (camera == null) {
            camera = createCamera();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            processingThread = new Thread(processingRunnable);
            processingRunnable.setActive(true);
            processingThread.start();

        }
    }

    public final synchronized void stop() {
        Log.e("CameraSource", "stop null ");
        processingRunnable.setActive(false);
        if (processingThread != null) {
            try {
                processingThread.join();
            } catch (InterruptedException var6) {
                Log.e("CameraSource", "Frame processing thread interrupted on stop.");
            }
            processingThread = null;
        }

        if (camera != null) {
            camera.stopPreview();
            Log.e("CameraSource", "setPreviewCallbackWithBuffer: null ");
            camera.setPreviewCallbackWithBuffer((PreviewCallback)null);
            try {
                camera.setPreviewDisplay((SurfaceHolder)null);
            } catch (Exception var5) {
                Log.e("CameraSource", "Failed to clear camera preview: " + var5);
            }

            camera.release();
            camera = null;
        }

        bytesToByteBuffer.clear();
    }

    public final void release() {
        Log.e("CameraSource", "release null ");
        graphicOverlay.clear();
        synchronized(processorLock) {
            this.stop();
            if (frameProcessor != null) {
                frameProcessor.stop();
            }
        }
    }

    public final void setFrameProcessor(@NonNull FrameProcessor processor) {
        graphicOverlay.clear();
        synchronized(processorLock) {
            if (frameProcessor != null) {
                frameProcessor.stop();
            }
            frameProcessor = processor;
        }
    }

    public final void updateFlashMode(@NonNull String flashMode) {
        Parameters parameters = camera != null ? camera.getParameters() : null;
        if (parameters != null) {
            Log.i("CameraSource", "updateFlashMode mode " + flashMode);
            parameters.setFlashMode(flashMode);
            camera.setParameters(parameters);
        }
    }

    private final Camera createCamera() throws IOException {
        Camera camera = Camera.open();
        if (camera == null) {
            throw (new IOException("There is no back-facing camera."));
        } else {
            Parameters parameters = camera.getParameters();
            setPreviewAndPictureSize(camera, parameters);
            setRotation(camera, parameters);

            int[] fpsRange = Companion.selectPreviewFpsRange(camera);
            if (fpsRange == null) {
                throw (new IOException("Could not find suitable preview frames per second range."));
            } else {

                parameters.setPreviewFpsRange(fpsRange[Parameters.PREVIEW_FPS_MIN_INDEX],
                        fpsRange[Parameters.PREVIEW_FPS_MAX_INDEX]);
                parameters.setPreviewFormat(IMAGE_FORMAT);

                if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else {
                    Log.i("CameraSource", "Camera auto focus is not supported on this device.");
                }

                camera.setParameters(parameters);
                Log.e("CameraSource", "setPreviewCallbackWithBuffer:"+previewSize.getWidth());
                 camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback(){
                    @Override
                    public  void onPreviewFrame(byte[] bytes, Camera camera){
                        if(processingRunnable != null) {
                            processingRunnable.setNextFrame(bytes, camera);
                        }
                    }
                 });

                // Four frame buffers are needed for working with the camera:
                //
                //   one for the frame that is currently being executed upon in doing detection
                //   one for the next pending frame to process immediately upon completing detection
                //   two for the frames that the camera uses to populate future preview images
                //
                // Through trial and error it appears that two free buffers, in addition to the two buffers
                // used in this code, are needed for the camera to work properly. Perhaps the camera has one
                // thread for acquiring images, and another thread for calling into user code. If only three
                // buffers are used, then the camera will spew thousands of warning messages when detection
                // takes a non-trivial amount of time.

                if (previewSize != null) {
                    camera.addCallbackBuffer(createPreviewBuffer(previewSize));
                    camera.addCallbackBuffer(createPreviewBuffer(previewSize));
                    camera.addCallbackBuffer(createPreviewBuffer(previewSize));
                    camera.addCallbackBuffer(createPreviewBuffer(previewSize));
                }
            }
        }
        return camera;
    }

    private final void setPreviewAndPictureSize(Camera camera, Parameters parameters) throws IOException {
        CameraSizePair sizePair = PreferenceUtils.Instance.getUserSpecifiedPreviewSize(context);

        if (sizePair == null) {

            Context context = graphicOverlay.getContext();
            float displayAspectRatioInLandscape = Utils.Instance.isPortraitMode(context) ?
                    (float)graphicOverlay.getHeight() / (float)graphicOverlay.getWidth() :
                    (float)graphicOverlay.getWidth() / (float)graphicOverlay.getHeight();
            sizePair = Companion.selectSizePair(camera, displayAspectRatioInLandscape);
        }

        if (sizePair == null) {
            throw (new IOException("Could not find suitable preview size."));
        } else {
            previewSize = sizePair.getPreview();
            Log.v("CameraSource", "Camera preview size: " + previewSize);
            parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
            PreferenceUtils.Instance.saveStringPreference(context, R.string.pref_key_rear_camera_preview_size, previewSize.toString());

            Size picSize = sizePair.getPicture();
            if (picSize != null) {
                Log.v("CameraSource", "Camera picture size: " + picSize);
                parameters.setPictureSize(picSize.getWidth(), picSize.getHeight());
                PreferenceUtils.Instance.saveStringPreference(context, R.string.pref_key_rear_camera_picture_size, picSize.toString());
            }
        }
    }

    private  void setRotation(Camera camera, Parameters parameters) {

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {

            int deviceRotation = windowManager.getDefaultDisplay().getRotation();
            int degrees = 0;
            if (deviceRotation >= Surface.ROTATION_0 && deviceRotation <= Surface.ROTATION_270) {
                degrees = deviceRotation * 90;
            }

            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(CAMERA_FACING_BACK, cameraInfo);
            int angle = (cameraInfo.orientation - degrees + 360) % 360;
            rotationDegrees = angle;
            camera.setDisplayOrientation(angle);
            parameters.setRotation(angle);
        }
    }

    private  byte[] createPreviewBuffer(Size previewSize) {
        int  bitsPerPixel = ImageFormat.getBitsPerPixel(IMAGE_FORMAT);
        long sizeInBits = (long)previewSize.getHeight() * (long)previewSize.getWidth() * (long)bitsPerPixel;
        int  bufferSize = (int)Math.ceil(sizeInBits / 8.0D) + 1;

        // Creating the byte array this way and wrapping it, as opposed to using .allocate(),
        // should guarantee that there will be an array to work with.
        byte[]  byteArray = new byte[bufferSize];
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        if (byteBuffer.hasArray() && Arrays.equals(byteBuffer.array(), byteArray)) {
            //Log.d("CameraSource", "createPreviewBuffer1 W:"+previewSize.getWidth() + " bs:"+bufferSize);
            bytesToByteBuffer.put(byteArray, byteBuffer);
            return byteArray;
        }else {
            // This should never happen. If it does, then we wouldn't be passing the preview content to
            // the underlying detector later.
            //"Failed to create valid buffer for camera source."
            Log.d("CameraSource", "ailed to create valid buffer for camera source.");
            return null;
        }
    }

    public CameraSource(@NonNull GraphicOverlay graphicOverlay) {
        super();
        this.graphicOverlay     = graphicOverlay;
        this.processingRunnable = new CameraSource.FrameProcessingRunnable();
        this.processorLock      = new Object();
        this.bytesToByteBuffer  = new IdentityHashMap();
        this.context = graphicOverlay.getContext();;
    }

    public final class FrameProcessingRunnable implements Runnable {
        private final Object lock = new Object();
        private boolean active    = true;
        private ByteBuffer pendingFrameData;

        public void setActive(boolean active) {
            synchronized(lock) {
                this.active = active;
                this.lock.notifyAll();
            }
        }

        public void setNextFrame(@NonNull byte[] data, @NonNull Camera camera) {
            synchronized(lock) {
                try {
                    //Log.d("CameraSource", "setNextFrame lenght:"+data.length);
                    if (pendingFrameData != null) {
                        camera.addCallbackBuffer(pendingFrameData.array());
                        pendingFrameData = null;
                    }
                    if (!bytesToByteBuffer.containsKey(data)) {
                        Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer" +
                                " associated with the image data from the camera.");
                        return;
                    }
                    pendingFrameData = (ByteBuffer) bytesToByteBuffer.get(data);
                    lock.notifyAll();
                } finally {
                }
            }

        }

        public void run() {

            ByteBuffer data = null;
            while(true) {
                synchronized (lock) {
                    while (active && pendingFrameData == null) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Log.e("CameraSource", "Frame processing loop terminated.", (Throwable) e);
                            return;
                        }
                    }
                    if (!active) {
                        // Exit the loop once this camera source is stopped or released.  We check this here,
                        // immediately after the wait() above, to handle the case where setActive(false) had
                        // been called, triggering the termination of this loop.
                        return;
                    }
                    // Hold onto the frame data locally, so that we can use this for detection
                    // below.  We need to clear pendingFrameData to ensure that this buffer isn't
                    // recycled back to the camera before we are done using that data.
                    data = pendingFrameData;
                    pendingFrameData = null;
                }

                try {
                    synchronized (processorLock) {
                        FrameMetadata frameMetadata = new FrameMetadata(getPreviewSize().getWidth(),
                                getPreviewSize().getHeight(), rotationDegrees);

                        if (data != null) {
                            if (frameProcessor != null) {
                                frameProcessor.process(data, frameMetadata, graphicOverlay);
                            }
                        }
                    }
                } catch (Exception t) {
                    Log.e("CameraSource", "Exception thrown from receiver.", (Throwable) t);
                } finally {
                    if (data != null) {
                        if (camera != null) {
                            camera.addCallbackBuffer(data.array());
                        }
                    }
                }
            }
        }

        public FrameProcessingRunnable() {
        }
    }

    public static final class Companion {

        public static CameraSizePair selectSizePair(Camera camera, float displayAspectRatioInLandscape) {
            List<CameraSizePair> validPreviewSizes      = Utils.Instance.generateValidPreviewSizeList(camera);
            CameraSizePair selectedPair = null;
            float minAspectRatioDiff    = Float.MAX_VALUE;

            for (int i = 0; i < validPreviewSizes.size(); i++) {
                CameraSizePair sizePair = validPreviewSizes.get(i);
                Size previewSize = sizePair.getPreview();
                if (previewSize.getWidth() < MIN_CAMERA_PREVIEW_WIDTH || previewSize.getWidth() > MAX_CAMERA_PREVIEW_WIDTH) {
                    continue;
                }
                float previewAspectRatio = (float)previewSize.getWidth() / (float)previewSize.getHeight();
                float aspectRatioDiff = Math.abs(displayAspectRatioInLandscape - previewAspectRatio);

                if (Math.abs(aspectRatioDiff - minAspectRatioDiff) < Utils.ASPECT_RATIO_TOLERANCE) {
                    if (selectedPair == null || selectedPair.getPreview().getWidth() < sizePair.getPreview().getWidth()) {
                        selectedPair = sizePair;
                    }
                } else if (aspectRatioDiff < minAspectRatioDiff) {
                    minAspectRatioDiff = aspectRatioDiff;
                    selectedPair = sizePair;
                }
            }

            if (selectedPair == null) {
                // Picks the one that has the minimum sum of the differences between the desired values and
                // the actual values for width and height.
                int minDiff = Integer.MAX_VALUE;
                for (int i = 0; i < validPreviewSizes.size(); i++) {
                    CameraSizePair sizePair = validPreviewSizes.get(i);
                    Size size = sizePair.getPreview();
                    int diff = Math.abs(size.getWidth() - DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH) +
                            Math.abs(size.getHeight() - DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT);

                    if (diff < minDiff) {
                        selectedPair = sizePair;
                        minDiff      = diff;
                    }
                }
            }
            return selectedPair;
        }

        public static  int[] selectPreviewFpsRange(Camera camera) {
            int desiredPreviewFpsScaled = (int)(REQUESTED_CAMERA_FPS * 1000f);
            int[] selectedFpsRange = null;
            int minDiff = Integer.MAX_VALUE;

            Parameters parameter = camera.getParameters();
            Iterator item = parameter.getSupportedPreviewFpsRange().iterator();

            while(item.hasNext()) {
                int[] range = (int[])item.next();
                int deltaMin = desiredPreviewFpsScaled - range[0];
                int deltaMax = desiredPreviewFpsScaled - range[1];
                int diff = Math.abs(deltaMin) + Math.abs(deltaMax);
                if (diff < minDiff) {
                    selectedFpsRange = range;
                    minDiff = diff;
                }
            }
            return selectedFpsRange;
        }
    }
}