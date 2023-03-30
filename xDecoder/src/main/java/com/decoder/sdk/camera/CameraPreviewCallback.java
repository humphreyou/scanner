package com.decoder.sdk.camera;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;

import java.util.Objects;

public abstract class CameraPreviewCallback implements Camera.PreviewCallback {
    public  final  CameraSource.FrameProcessingRunnable runner;
    public  abstract void onPreviewFrame(byte[] var1, Camera var2);
    public  CameraPreviewCallback(CameraSource.FrameProcessingRunnable data) {
        runner = data;
    }
}
