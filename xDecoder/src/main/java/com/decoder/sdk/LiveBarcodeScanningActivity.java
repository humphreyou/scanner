package com.decoder.sdk;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.hardware.Camera;
import com.google.android.material.chip.Chip;
import com.decoder.sdk.camera.CameraSource;
import com.decoder.sdk.camera.CameraSourcePreview;
import com.decoder.sdk.camera.GraphicOverlay;
import com.decoder.sdk.barcode.BarcodeProcessor;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.barcode.common.Barcode;
import java.io.IOException;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.decoder.sdk.R;
import com.decoder.sdk.barcode.BarcodeField;
import com.decoder.sdk.barcode.BarcodeResultFragment;
import com.decoder.sdk.settings.SettingsActivity;
import android.content.Context;
import android.content.Intent;

public final class LiveBarcodeScanningActivity extends AppCompatActivity implements OnClickListener {
    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private View settingsButton;
    private View flashButton;
    private Chip promptChip;
    private AnimatorSet promptChipAnimator;
    private WorkflowModel workflowModel;
    private WorkflowModel.WorkflowState currentWorkflowState;
    private static final String TAG = "LiveBarcodeActivity";

    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 35;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 36;
    private static final int CAMERA_STORAGE_PERMISSION_CODE = 37;
    private static final  String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_barcode);
        preview = findViewById(R.id.camera_preview);

        graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
        graphicOverlay.setOnClickListener(this);

        cameraSource = new CameraSource(graphicOverlay);

        promptChip = findViewById(R.id.bottom_prompt_chip);
        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
        if (animator != null) {
            promptChipAnimator = (AnimatorSet) animator;
            promptChipAnimator.setTarget(this.promptChip);
        }

        findViewById(R.id.close_button).setOnClickListener(this);

        flashButton = findViewById(R.id.flash_button);
        flashButton.setOnClickListener(this);

        settingsButton = this.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !checkPermissions(Manifest.permission.CAMERA)) {
                requestPermission(PERMISSIONS, CAMERA_STORAGE_PERMISSION_CODE);
            }
        }

        setUpWorkflowModel();

    }

    protected void onResume() {
        super.onResume();
        if (workflowModel != null) {
            workflowModel.markCameraFrozen();
        }

        if (settingsButton != null) {
            settingsButton.setEnabled(true);
        }

        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;

        if (cameraSource != null) {
            cameraSource.setFrameProcessor(new BarcodeProcessor(graphicOverlay, workflowModel));
        }

        if (workflowModel != null) {
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);
        }
    }

    protected void onPostResume() {
        super.onPostResume();
        BarcodeResultFragment.dismiss(getSupportFragmentManager());
    }

    protected void onPause() {
        super.onPause();
        this.currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
        this.stopCameraPreview();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    public void onClick(@NotNull View view) {

        if (view.getId() == R.id.flash_button) {
            if (flashButton.isSelected()) {
                flashButton.setSelected(false);
                if (cameraSource != null) {
                    cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
            } else{
                flashButton.setSelected(true);
                if (cameraSource != null) {
                    cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            }
        }
        else if (view.getId() == R.id.settings_button) {
            settingsButton.setEnabled(false);
            startActivity(new Intent((Context)this, SettingsActivity.class));
        }
        else if (view.getId() == R.id.close_button) {
            onBackPressed();
        }
    }

    private final void startCameraPreview() {
        if (workflowModel != null && cameraSource != null) {
            if (!workflowModel.isCameraLive()) {
                try {
                    workflowModel.markCameraLive();
                    if (preview != null) {
                        preview.start(cameraSource);
                    }
                } catch (IOException e) {
                    Log.e("LiveBarcodeActivity", "Failed to start camera preview!",e);
                    cameraSource.release();
                    cameraSource = null;
                }
            }
        }
    }

    private final void stopCameraPreview() {
        if (workflowModel != null && workflowModel.isCameraLive()) {
            workflowModel.markCameraFrozen();
            if (flashButton != null) {
                flashButton.setSelected(false);
            }
            if (preview != null) {
                preview.stop();
            }
        }
    }

    private  void onWorkFlowStateChanged(WorkflowModel.WorkflowState state) {

        Log.e(TAG, "onWorkFlowStateChanged workflow state: " + state.name());
        if (state == null || currentWorkflowState == state) {
            return;
        }

        //1. update the work state
        currentWorkflowState = state;
        Log.e(TAG, "onWorkFlowStateChanged2 workflow state: " + state.name());
        boolean wasPromptChipGone = false;
        if (promptChip != null) {
            wasPromptChipGone = (promptChip.getVisibility() == View.GONE);
        }

        //2. set the camera preview state
        if(state == WorkflowModel.WorkflowState.DETECTING) {
            promptChip.setVisibility(View.VISIBLE);
            promptChip.setText(R.string.prompt_point_at_a_barcode);
            startCameraPreview();
        } else if(state == WorkflowModel.WorkflowState.CONFIRMING) {
            promptChip.setVisibility(View.VISIBLE);
            promptChip.setText(R.string.prompt_move_camera_closer);
            startCameraPreview();
        }else if(state == WorkflowModel.WorkflowState.SEARCHING) {
            promptChip.setVisibility(View.VISIBLE);
            promptChip.setText(R.string.prompt_searching);
            stopCameraPreview();
        }else if(state == WorkflowModel.WorkflowState.DETECTED ||
                 state == WorkflowModel.WorkflowState.SEARCHED) {
            promptChip.setVisibility(View.GONE);
            stopCameraPreview();
        } else {
            promptChip.setVisibility(View.GONE);
        }

        //3. Show the chip tip string
        if (wasPromptChipGone &&
           promptChip.getVisibility() == View.VISIBLE &&
           promptChipAnimator != null &&
           !promptChipAnimator.isRunning()) {
            promptChipAnimator.start();
        }
    }

    // Show the barcode result here.
    private  void onBarcodeChanged(Barcode barcode) {
        Log.i("", "onBarcodeChanged connected: " + barcode.getRawValue());
        if (barcode != null) {
            ArrayList barcodeFieldList = new ArrayList();
            BarcodeField codefied = new BarcodeField("BarCode Value", barcode.getRawValue().isEmpty()?"":barcode.getRawValue());
            barcodeFieldList.add(codefied);
            BarcodeResultFragment.show(getSupportFragmentManager(), barcodeFieldList);
        }
    }

    private  void setUpWorkflowModel() {

        workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

        // Observe the workflow state changed,
        workflowModel.getWorkflowState().observe(this, new Observer() {
            public void onChanged(Object workflowState) {
                onWorkFlowStateChanged((WorkflowModel.WorkflowState) workflowState);
            }});

        // Observe the barcode result return.
        workflowModel.getDetectedBarcode().observe(this, new Observer() {
            public void onChanged(Object code) {
                onBarcodeChanged((Barcode) code);
            }});
    }

    private boolean checkPermissions(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(), permission);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(final String[] permissions, final int requestCode) {
        boolean shouldProvideRationale = ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                permissions[0])) || (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])));

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                            findViewById(R.id.camera_preview),
                            "Permission",
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(LiveBarcodeScanningActivity.this,
                                    permissions,
                                    requestCode);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(LiveBarcodeScanningActivity.this,
                    permissions,
                    requestCode);
        }
    }
}
