package com.decoder.sdk.barcode;

import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.util.Log;
import androidx.annotation.MainThread;
import com.google.android.gms.tasks.Task;
import com.decoder.sdk.camera.InputInfo;
import com.decoder.sdk.camera.CameraReticleAnimator;
import com.decoder.sdk.camera.FrameProcessorBase;
import com.decoder.sdk.camera.GraphicOverlay;
import com.decoder.sdk.WorkflowModel;
import com.decoder.sdk.camera.GraphicOverlay.Graphic;
import com.decoder.sdk.WorkflowModel.WorkflowState;
import com.decoder.sdk.settings.PreferenceUtils;
//---import com.google.mlkit.vision.barcode.BarcodeScanner;
//---import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.decoder.sdk.scanner.ScannerManager;

public final class BarcodeProcessor extends FrameProcessorBase {
    //---    private final BarcodeScanner scanner;
    private final CameraReticleAnimator cameraReticleAnimator;
    private final WorkflowModel workflowModel;
    private static final String TAG = "BarcodeProcessor";

    @NotNull
    protected Task detectInImage(@NotNull InputImage image) {
        Log.e("BarcodeProcessor", "detectInImage: ");
        return  ScannerManager.instance().process(image);
    }

    @MainThread
    protected void onSuccess(@NotNull InputInfo inputInfo, @NotNull List<Barcode> results, @NotNull GraphicOverlay graphicOverlay) {

        Log.d("BarcodeProcessor","onSuccess:" + workflowModel.isCameraLive());
        if (workflowModel.isCameraLive()) {
            Log.d("BarcodeProcessor", "Barcode result size: " + results.size());

            Barcode barcodeInCenter = null;
            for (int i = 0; i < results.size(); i++) {
                barcodeInCenter = results.get(i);
                if (barcodeInCenter.getBoundingBox() != null) {
                    RectF box = graphicOverlay.translateRect(barcodeInCenter.getBoundingBox());
                    if(box.contains((float)graphicOverlay.getWidth() / 2.0F,
                                    (float)graphicOverlay.getHeight() / 2.0F));
                       break;
                }
            }

            graphicOverlay.clear();
            if (barcodeInCenter == null) {
                cameraReticleAnimator.start();
                Graphic layer = new BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator);
                graphicOverlay.add(layer);
                workflowModel.setWorkflowState(WorkflowState.DETECTING);
            } else {
                cameraReticleAnimator.cancel();
                float sizeProgress = PreferenceUtils.Instance.getProgressToMeetBarcodeSizeRequirement(graphicOverlay, barcodeInCenter);
                if (sizeProgress < (float)1) {
                    graphicOverlay.add((Graphic)(new BarcodeConfirmingGraphic(graphicOverlay, barcodeInCenter)));
                    Log.d("BarcodeProcessor", "BarcodeConfirmingGraphic: ");
                    workflowModel.setWorkflowState(WorkflowState.CONFIRMING);
                } else {
                    if (PreferenceUtils.Instance.shouldDelayLoadingBarcodeResult(graphicOverlay.getContext())) {
                        ValueAnimator loadingAnimator = createLoadingAnimator(graphicOverlay, barcodeInCenter);
                        loadingAnimator.start();
                        graphicOverlay.add((new BarcodeLoadingGraphic(graphicOverlay, loadingAnimator)));
                        Log.d("BarcodeProcessor", "BarcodeConfirmingGraphic: ");
                        workflowModel.setWorkflowState(WorkflowState.SEARCHING);
                    } else {
                        workflowModel.setWorkflowState(WorkflowState.DETECTED);
                        workflowModel.getDetectedBarcode().setValue(barcodeInCenter);
                    }
                }
            }

            graphicOverlay.invalidate();
        }
    }

    // $FF: synthetic method
    // $FF: bridge method
    public void onSuccess(InputInfo var1, Object var2, GraphicOverlay var3) {
        this.onSuccess(var1, (List)var2, var3);
    }

    private final ValueAnimator createLoadingAnimator(GraphicOverlay graphicOverlay, Barcode barcode) {
        float endProgress = 1.1F;
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0F, endProgress});
        animator.setDuration(200L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (Float.compare((Float)animation.getAnimatedValue(), endProgress) >= 0) {
                    graphicOverlay.clear();
                    workflowModel.setWorkflowState(WorkflowState.SEARCHED);
                    workflowModel.getDetectedBarcode().setValue(barcode);
                } else {
                    graphicOverlay.invalidate();
                }
            }
        });
        return animator;
    }

    protected void onFailure(@NotNull Exception e) {
        Log.e("BarcodeProcessor", "Barcode detection failed!", (Throwable)e);
    }

    public void stop() {
        super.stop();
        try {
            //this.scanner.close();
            ScannerManager.instance().close();

        } catch (Exception e) {
            Log.e("BarcodeProcessor", "Failed to close barcode detector!", (Throwable)e);
        }
    }

    public BarcodeProcessor(@NotNull GraphicOverlay graphicOverlay, @NotNull WorkflowModel model) {
        super();
        workflowModel = model;
        //scanner = BarcodeScanning.getClient();
        cameraReticleAnimator = new CameraReticleAnimator(graphicOverlay);
    }

    public static  WorkflowModel access(BarcodeProcessor processor) {
        return processor.workflowModel;
    }

}
