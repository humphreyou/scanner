package com.decoder.sdk.camera;

import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;
import java.nio.ByteBuffer;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import org.jetbrains.annotations.NotNull;


public abstract class FrameProcessorBase<T> implements FrameProcessor {
    @GuardedBy("this")
    private ByteBuffer latestFrame = null;
    @GuardedBy("this")
    private FrameMetadata latestFrameMetaData = null;
    @GuardedBy("this")
    private ByteBuffer processingFrame = null;
    @GuardedBy("this")
    private FrameMetadata processingFrameMetaData = null;
    private final ScopedExecutor executor;
    private static final String TAG = "FrameProcessorBase";

    public synchronized void process(@NotNull ByteBuffer data, @NotNull FrameMetadata frameMetadata, @NotNull GraphicOverlay graphicOverlay) {

        latestFrame = data;
        latestFrameMetaData = frameMetadata;
        Log.d("FrameProcessorBase", "process ");

        if (processingFrame == null && processingFrameMetaData == null) {
            processLatestFrame(graphicOverlay);
        }

    }

    private final synchronized void processLatestFrame(final GraphicOverlay graphicOverlay) {

        processingFrame         = latestFrame;
        processingFrameMetaData = latestFrameMetaData;
        latestFrame         = null;
        latestFrameMetaData = null;

        Log.d("FrameProcessorBase", "processLatestFrame ");

        if (processingFrame == null || processingFrameMetaData == null) {
            return;
        }

        ByteBuffer frame = processingFrame;
        FrameMetadata frameMetaData = processingFrameMetaData;
        InputImage image = InputImage.fromByteBuffer(frame, frameMetaData.getWidth(),
                frameMetaData.getHeight(),
                frameMetaData.getRotation(), InputImage.IMAGE_FORMAT_NV21);

        final long startMs = SystemClock.elapsedRealtime();
        Task task = detectInImage(image);

        task.addOnSuccessListener(executor, new OnSuccessListener<T>(){
            @Override
            public  void onSuccess(T result){
                Log.d(TAG, "Latency is: ${SystemClock.elapsedRealtime() - startMs}" + startMs);
                FrameProcessorBase.this.onSuccess(new CameraInputInfo(frame, frameMetaData), result, graphicOverlay);
                processLatestFrame(graphicOverlay);
            }
        });

        task.addOnFailureListener(executor, new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                FrameProcessorBase.this.onFailure(e);
            }
        });
    }

    public void stop() {
        executor.shutdown();
    }

    @NotNull
    protected abstract Task detectInImage(@NotNull InputImage image);

    protected abstract void onSuccess(@NotNull InputInfo inputInfo,
                                      T results,
                                      @NotNull GraphicOverlay graphicOverlay);

    protected abstract void onFailure(@NotNull Exception var1);

    public FrameProcessorBase() {
        executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }
}
