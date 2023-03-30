package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import static com.codecorp.internal.Debug.debug;
import static com.codecorp.internal.Debug.verbose;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codecorp.decoder.CortexDecoderLibrary;
import com.codecorp.decoder.CortexDecoderLibraryCallback;
import com.codecorp.internal.Debug;
import com.codecorp.licensing.LicenseCallback;
import com.codecorp.licensing.LicenseStatusCode;
import com.codecorp.symbology.SymbologyType;
import com.codecorp.util.Codewords;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class CortexScan extends AppCompatActivity implements CortexDecoderLibraryCallback {
    static final String TAG = CortexScan.class.getSimpleName();
    private SharedPreferences prefs;
    private CortexDecoderLibrary mCortexDecoderLibrary;
    private TextView mBarcodeResult;
    private TextView mBarcodeLength;
    private TextView mSymbologyResult;
    private View mCameraPreview;
    private TextView mTap;
    private CrosshairsView mCrosshairs;
    private PicklistView mPicklistView;
    private TextView noLicenseTextView;
    private MyApplication mApplication;
    private RelativeLayout mCameraFrame;
    private RelativeLayout mCameraSliderFrame;
    private WhiteBalanceLayout mWhiteBalanceLayout;
    private Handler mMainHandler;
    private boolean mPicklistMode;
    private boolean mCaptureMode;
    private ImageView mGetItNowImg;
    private ImageView mScanButton;
    private int continuousScanCount = 0;
    private boolean mSuccessfulScan = false;
    private boolean mScanning = false;
    private boolean mStopDecoding = false;
    private Toast mToast;
    private ImageView mCaptureButton;
    private SeekBar mFocusSlider;
    private SeekBar mExposureSlider;
    private SeekBar mSensitivitySlider;
    private SeekBar mFocusDistanceSlider;
    private SeekBar mZoomSlider;
    private LinearLayout mExposureLayout;
    private LinearLayout mFocusDistanceLayout;
    private LinearLayout mZoomLayout;
    private Boolean cameraSettings = false;
    private int c = 1;
    private boolean mTargetLocator = false;
    private ArrayList<BarcodeFinderView> bfArr = new ArrayList<BarcodeFinderView>();
    private ArrayList<RegionOfInterestRect> roiArr = new ArrayList<>();
    private Timer timer;
    private TimerTask timerTask;
    private TextView mWhiteText;
    private TextView mExposureText;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 35;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 36;
    private static final int CAMERA_STORAGE_PERMISSION_CODE = 37;
    String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    DisplayMetrics displayMetrics;

    private static final String EDK_ACTIVATE_LICENSE_KEY = "please use correct key";

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle state) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.symbologies, true);
        PreferenceManager.setDefaultValues(this, R.xml.advancedfocussetting, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPicklistMode = prefs.getBoolean("picklist_mode", false);
        mApplication = (MyApplication) getApplication();
        mApplication.pushPreference(prefs, "debug_level");
        // Uncomment a line below to override the debug level
        setDebugLevel(Log.INFO);
        setDebugLevel(Log.DEBUG);
        setDebugLevel(Log.VERBOSE);

        debug(TAG, "onCreate()");

        verbose(TAG, "MANUFACTURER: " + android.os.Build.MANUFACTURER);
        verbose(TAG, "MODEL: " + android.os.Build.MODEL);
        verbose(TAG, "BRAND: " + android.os.Build.BRAND);
        verbose(TAG, "DEVICE: " + android.os.Build.DEVICE);
        verbose(TAG, "HARDWARE: " + android.os.Build.HARDWARE);
        verbose(TAG, "FINGERPRINT: " + android.os.Build.FINGERPRINT);
        verbose(TAG, "PRODUCT: " + android.os.Build.PRODUCT);
        verbose(TAG, "ID: " + android.os.Build.ID);
        verbose(TAG, "HOST: " + android.os.Build.HOST);

        super.onCreate(state);
        setContentView(R.layout.main_view);
        mBarcodeResult = findViewById(R.id.barcode_result);
        mBarcodeLength = findViewById(R.id.length_result);
        mSymbologyResult = findViewById(R.id.symbology_result);

        //Request run time permission for WRITE_EXTERNAL_STORAGE and CAMERA before accessing CortexDecoderLibrary for Android versions >= 23

        // Use the Application context, which is shared by this activity
        // and the settings activity.
        String currentCameraAPI = prefs.getString("cameraAPI", "auto-select");
        if (currentCameraAPI.equalsIgnoreCase("auto-select")) {
            mCortexDecoderLibrary = CortexDecoderLibrary.sharedObject(mApplication, "");
        } else {
            mCortexDecoderLibrary = CortexDecoderLibrary.sharedObject(mApplication, currentCameraAPI);
        }
        // Save mCortexDecoderLibrary in case another Activity needs it
        mApplication.mCortexDecoderLibrary = mCortexDecoderLibrary;
        mCortexDecoderLibrary.setCallback(this);
        mCameraPreview = mCortexDecoderLibrary.getCameraPreview();
        mCameraFrame = findViewById(R.id.cortex_scanner_view);
        mCameraFrame.addView(mCameraPreview, 0);
        mTap = findViewById(R.id.tap_to_scan_again);
        mCrosshairs = findViewById(R.id.crosshairs_view);
        mPicklistView = findViewById(R.id.picklist_view);
        noLicenseTextView = findViewById(R.id.no_license);
        mScanButton = findViewById(R.id.scanButton);
        mCaptureButton = findViewById(R.id.image_capture);
        mGetItNowImg = findViewById(R.id.getitnowlogo);
        View mAnimationView = findViewById(R.id.animationView);
        mExposureSlider = findViewById(R.id.exposure_slider);
        mSensitivitySlider = findViewById(R.id.iso_slider);
        mFocusDistanceSlider = findViewById(R.id.focus_distance_slider);
        mZoomSlider = findViewById(R.id.zoom_slider);
        TextView mZoomSliderText = findViewById(R.id.zoomSliderText);
        mZoomLayout = findViewById(R.id.zoomLayout);
        mExposureLayout = findViewById(R.id.exposureLayout);
        mFocusDistanceLayout = findViewById(R.id.focusDistanceLayout);
        //mWhiteBalanceLayout = (WhiteBalanceLayout) findViewById(R.id.white_balance);
        mWhiteText = findViewById(R.id.white_balance_text);
        mExposureText = findViewById(R.id.exposure_text);
        setOnTouchListener();
        mToast = Toast.makeText(this, "Continuous Scan Count: ", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 200);
        mMainHandler = new Handler(Looper.getMainLooper());

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) || !checkPermissions(Manifest.permission.CAMERA)) {
                requestPermission(PERMISSIONS, CAMERA_STORAGE_PERMISSION_CODE);
            }
        }

        //Activating the EDK decoder library - Start
        if(mCortexDecoderLibrary.decoderVersionLevel().equals(CortexDecoderLibrary.DECODER_VERSION_LEVEL_E)){
            mCortexDecoderLibrary.setLicenseCallback(new LicenseCallback() {
                @Override
                public void onActivationResult(LicenseStatusCode statusCode) {
                    Log.d(TAG, "onActivationResult:" + statusCode);
                    switch (statusCode) {
                        case LicenseStatus_LicenseValid:
                            Toast.makeText(getApplicationContext(), R.string.licenseValid, Toast.LENGTH_SHORT).show();
                            break;
                        case LicenseStatus_LicenseExpired:
                            Toast.makeText(getApplicationContext(), R.string.expiredLicenseToast, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.licenseInvalid, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onDeviceIDResult(int resultCode, String data) {

                }
            });

            mCortexDecoderLibrary.activateLicense(EDK_ACTIVATE_LICENSE_KEY);
        }
        //Activating the EDK decoder library - end

        //Tablets more than likely are going to have a screen dp >= 600
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    private void setPreference(SharedPreferences sharedPreferences,
                               String key, Object value) {
        debug(TAG, "setPreference(" + key + ", " + value + ")");
        SharedPreferences.Editor e = sharedPreferences.edit();
        if (value instanceof String)
            e.putString(key, (String) value);
        else if (value instanceof Boolean)
            e.putBoolean(key, (Boolean) value);
        else
            throw new RuntimeException("Programming error!  Unknown value type.");
        e.apply();
    }

    private void setDebugLevel(int level) {
        Log.d(TAG, "setDebugLevel(" + level + ")");
        Debug.debugLevel = level;
        Editor e = prefs.edit();
        e.putString("debug_level", Integer.toString(level));
        e.apply();
    }

    private boolean checkPermissions(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(), permission);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void pushPreferences() {
        for (Entry<String, ?> e : prefs.getAll().entrySet()) {
            try {
                mApplication.pushPreference(prefs, e.getKey());
            } catch (Exception ex) {
                Log.e(TAG, "Error pushing preference " + e.getKey() + ":", ex);
            }
        }
    }

    private void resetBarcodeResultUI() {
        mBarcodeResult.setText("");
        mBarcodeLength.setText("");
        mSymbologyResult.setText("");
    }

    OnClickListener tapListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            debug(TAG, "onClick()");
            resetBarcodeResultUI();
            mSuccessfulScan = false;
            if (mTargetLocator)
                removeLocatorOverlays();

            if (mCortexDecoderLibrary.isLicenseActivated()) {
                if (mCortexDecoderLibrary.isLicenseExpired()) {
                    displayCenterToast(getString(R.string.expiredLicenseToast));
                } else {
                    startScanning();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.activateLicenseToast, Toast.LENGTH_SHORT).show();
            }
        }
    };

    void displayCenterToast(String message) {
        Toast toast = Toast.makeText(CortexScan.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    void startScanning() {
        debug(TAG, "startScanning()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPicklistMode) {
                    mPicklistView.setVisibility(View.VISIBLE);
                    mCrosshairs.setVisibility(View.INVISIBLE);
                } else {
                    mCrosshairs.setVisibility(View.VISIBLE);
                    mPicklistView.setVisibility(View.INVISIBLE);
                }

                if (mCaptureMode) {
                    mCaptureButton.setVisibility(View.VISIBLE);
                    mGetItNowImg.setVisibility(View.GONE);
                } else {
                    mCaptureButton.setVisibility(View.GONE);
                    mGetItNowImg.setVisibility(View.VISIBLE);
                }
                mTap.setVisibility(View.INVISIBLE);
            }
        });

        mCameraFrame.setOnClickListener(null);
        // This can take a while if we need to open the camera, so post
        // it after the UI update.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions(Manifest.permission.CAMERA)) {
                showSnackBarRationale(new String[]{Manifest.permission.CAMERA});
            } else {
                postStartCameraAndDecodeTask();
            }
        } else {
            postStartCameraAndDecodeTask();
        }
    }

    private void postStartCameraAndDecodeTask() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mCortexDecoderLibrary.startCameraPreview();
                if (!mPicklistMode && !mStopDecoding) {
                    mCortexDecoderLibrary.startDecoding();
                }
            }
        });
    }

    void stopScanning(boolean launchingIntent) {
        debug(TAG, "stopScanning()");
        if (mPicklistMode) {
            mPicklistView.setVisibility(View.INVISIBLE);
            mSuccessfulScan = true;
            if (!launchingIntent) {
                mTap.setVisibility(View.VISIBLE);
            }
        } else {
            mCrosshairs.setVisibility(View.INVISIBLE);
            if (!launchingIntent) {
                mTap.setVisibility(View.VISIBLE);
            }
        }
        mCortexDecoderLibrary.stopDecoding();
        mCameraFrame.setOnClickListener(tapListener);
        mCortexDecoderLibrary.stopCameraPreview();
    }

    public void stopCamera() {
        debug(TAG, "stopCamera()");
        mCortexDecoderLibrary.stopDecoding();

    }

    public void closeCamera() {
        debug(TAG, "closeCamera()");
        mCortexDecoderLibrary.stopDecoding();
        mCortexDecoderLibrary.stopCameraPreview();
        mCortexDecoderLibrary.closeCamera();
    }

    public void onSettingsClicked(View view) {
        stopScanning(true);
        hideAndDisableSliders();
        // Launch the settings activity
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onInfoClicked(View view) {
        stopScanning(true);
        hideAndDisableSliders();
        // Launch the information activity
        if (mCortexDecoderLibrary.isLicenseActivated()) {
            Intent intent = new Intent();
            intent.setClass(this, InformationActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), R.string.activateLicenseToast, Toast.LENGTH_SHORT).show();
        }
    }


    //This gets called whenever the screen is tapped while the Cross Hairs are active.
    public void resetContinuousScan(View view) {
        continuousScanCount = 0;
    }

    public void onScanClicked(View view) {
        //startScanning();
    }

    private void displayContinuousCount() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText("Continuous Scan Count: " + Integer.toString(continuousScanCount));
                mToast.show();
            }
        });
    }

    private void displayMultiFrameCount(final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText("No of decoded barcodes: " + Integer.toString(count));
                mToast.show();
            }
        });
    }

    @Override
    protected void onStart() {
        debug(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        debug(TAG, "onResume()");
        super.onResume();
        mStopDecoding = false;
        mPicklistMode = prefs.getBoolean("picklist_mode", false);
        mCaptureMode = prefs.getBoolean("debug_mode", false);
        mTargetLocator = prefs.getBoolean("barcode_locator", false);
        mSuccessfulScan = false;
        ImageView iv = findViewById(R.id.scanButton);
        iv.setVisibility(mPicklistMode ? View.VISIBLE : View.GONE);
        if (mTargetLocator)
            removeLocatorOverlays();

        //Special case API. Should not be called if regular behaviour is expected from the library
        mCortexDecoderLibrary.enableMultiFrameDecoding(prefs.getBoolean("multi_frame_decode", false));

        //enable get codewords
        mCortexDecoderLibrary.enableCodewordsOutput(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((!checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (!checkPermissions(Manifest.permission.CAMERA))) {
                showSnackBarRationale(PERMISSIONS);
            } else {
                startScanningAndDecoding();
            }
        } else {
            startScanningAndDecoding();
        }

    }

    private void startScanningAndDecoding() {
        if (mCortexDecoderLibrary.isLicenseActivated()) {
            noLicenseTextView.setVisibility(View.INVISIBLE);
            if (mCortexDecoderLibrary.isLicenseExpired()) {
                displayCenterToast("License is expired");
            } else {
                setOnImageCaptureButtonClickListener();
                pushPreferences();
                startScanning();
            }
        } else {
            noLicenseTextView.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.activateLicenseToast, Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnImageCaptureButtonClickListener() {
        mCaptureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptureMode) {
                    mCortexDecoderLibrary.captureCurrentImageInBuffer();
                }
            }
        });
    }

    @Override
    public void onPause() {
        debug(TAG, "onPause()");
        super.onPause();
        mStopDecoding = true;
        stopCamera();
        closeCamera();
        resetBarcodeResultUI();
        if (mTargetLocator) {
            cancelTimer();
            removeLocatorOverlays();
        }
    }

    @Override
    public void onStop() {
        debug(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        debug(TAG, "onDestroy()");
        mCortexDecoderLibrary.closeSharedObject();
        super.onDestroy();
    }

    @Override
    public void receivedDecodedData(final String data, final SymbologyType type) {
        debug(TAG, "receivedDecodedData()");
        final String symString = CortexDecoderLibrary.stringFromSymbologyType(type);
        if (!prefs.getBoolean("large_data_display", false)) {
            if (!prefs.getBoolean("continuous_scan_mode", false)) {
                continuousScanCount = 0;
                mCortexDecoderLibrary.stopDecoding();
                mCortexDecoderLibrary.stopCameraPreview();
                updateBarcodeData(data, type, true);
            } else {
                updateBarcodeData(data, type, false);
                continuousScanCount++;
                displayContinuousCount();
                if (mTargetLocator) {
                    startTimer();
                    removeLocatorOverlays();
                }
                if (mScanning)
                    mCortexDecoderLibrary.startDecoding();
                else
                    startScanning();
            }
        } else {
            launchLargeDataActivity(new String[]{symString}, new String[]{data});
        }
    }


    @Override
    public void receivedMultipleDecodedData(String[] data, SymbologyType[] types) {
        // Launch the large data view activity
        String[] typesArr = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            String symString = CortexDecoderLibrary.stringFromSymbologyType(types[i]);
            typesArr[i] = symString;
        }
        launchLargeDataActivity(typesArr, data);
    }

    @Override
    public void receiveBarcodeCorners(final int[] corners) {
        if (mTargetLocator) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    createTargetLocator(corners);
                }
            });
        }
    }

    @Override
    public void receivedDecodedCodewordsData(Codewords codewords) {
        if(codewords != null){
            codewords.getNumberOfCodewords();
            codewords.getNumberOfShortCodewordsBlocks();
            codewords.getNumberOfLongCodewordsBlocks();
            codewords.getNumberOfDataCodewords();
            codewords.getNumberOfErrorCodewords();
            codewords.getCodewordsBeforeErrorCorrection();
            codewords.getCodewordsAfterErrorCorrection();
        }
    }

    @Override
    public void barcodeDecodeFailed(boolean result) {

    }

    @Override
    public void multiFrameDecodeCount(int decodeCount) {
        displayMultiFrameCount(decodeCount);
    }

    private void updateBarcodeData(final String data, SymbologyType type, final boolean mStop) {
        final String symString = CortexDecoderLibrary.stringFromSymbologyType(type);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBarcodeResult.setText(data);
                mBarcodeLength.setText(String.valueOf(data.length()));
                mSymbologyResult.setText(symString);
                if (mStop)
                    stopScanning(false);
            }
        });
    }

    /*
        This will launch the large data activity display
     */
    private void launchLargeDataActivity(String[] typesArr, String[] data) {
        mCortexDecoderLibrary.stopCameraPreview();
        mCortexDecoderLibrary.stopDecoding();
        Intent intent = new Intent();
        intent.setClass(CortexScan.this, LargeDataActivity.class);
        intent.putExtra("symbologyArr", typesArr);
        intent.putExtra("barcodeArr", data);
        intent.putExtra("numberOfBarcodes", true);
        startActivityForResult(intent, 777);
    }

    //We are doing conversions from camera preview display to the actual size of the preview on the screen. Find the correct ratios and passing it along to the BarcodeFinderViewClass.
    private void createTargetLocator(int[] corners) {
        //We will have to take these points and draw them to the screen
        int pWidth = mCameraPreview.getWidth();
        int pHeight = mCameraPreview.getHeight();

        Display display = getWindowManager().getDefaultDisplay();
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (Exception ignore) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        int screenH = point.y;
        int _y = mCameraFrame.getChildAt(1).getMeasuredHeight();
        int mPreviewH;

        int diffY = screenH - _y;
        mPreviewH = (screenH - diffY);
        com.codecorp.util.Size sz = mCortexDecoderLibrary.getSizeForROI();

        int screenDiff = 0;
        if (pHeight > mPreviewH) {
            screenDiff = (int) ((pHeight - mPreviewH) * 0.5);
        }
        //This checks to see if we are in portrait mode and we do a conversion taking into account that the photo is internally always viewed in landscape with
        //The origin being at the top right of the screen
        if (pWidth <= pHeight) {
            float prh = (float) pWidth / sz.height;
            float prw = (float) pHeight / sz.width;

            final BarcodeFinderView bf = new BarcodeFinderView(this, corners, pWidth, pHeight, screenDiff, prh, prw);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bf.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bfArr.add(bf);
                    mCameraFrame.addView(bf);
                }
            });
        } else {
            float prw = (float) pWidth / sz.width;
            float prh = (float) pHeight / sz.height;

            final BarcodeFinderView bf = new BarcodeFinderView(this, corners, pWidth, pHeight, screenDiff, prh, prw);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bf.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bfArr.add(bf);
                    mCameraFrame.addView(bf);
                }
            });
        }
    }

    //This is a function to remove all the locator overlays if this feature has been enabled in the settings
    private void removeLocatorOverlays() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Iterator<BarcodeFinderView> iterator = bfArr.listIterator(); iterator.hasNext(); ) {
                    BarcodeFinderView b = iterator.next();
                    iterator.remove();
                    mCameraFrame.removeView(b);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
    }

    /*
     ***Setting up the on Touch Listener event for the scan button***
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {
        mScanButton.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mSuccessfulScan) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mCortexDecoderLibrary.startDecoding();
                            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            mScanning = true;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                                mCortexDecoderLibrary.stopDecoding();
                                mScanning = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            mCortexDecoderLibrary.stopDecoding();
                            mScanning = false;
                            break;

                        default:
                            break;
                    }
                }
                return false;

            }
        });
    }

    /*
     ** This is the functionality to add the white scale balance to the UI
     */
    @TargetApi(17)
    private void addWhiteBalanceButtons() {
        setSliderCallbacks();
    }

    /*
      This is a function that will make a call to the camera zoom.
     */
    public void zoomInCamera(View view) {
        hideAndDisableSliders();
        float[] mZoom = mCortexDecoderLibrary.getZoomRatios();
        float midVal;
        float quarterVal;
        float ratio = mCortexDecoderLibrary.getMaxZoom() / mZoom[mZoom.length - 1];
        float reset;
        if (mZoom.length != 2) {
            midVal = mZoom[mZoom.length / 2];
            quarterVal = mZoom[mZoom.length / 8];
            reset = 0;
        } else {
            midVal = mZoom[1] / 2;
            quarterVal = mZoom[1] / 4;
            if (quarterVal == 1) {
                quarterVal = 1.5f;
            }
            reset = mZoom[0];
        }


        if (mCortexDecoderLibrary.isZoomSupported()) {
            float zoomVal;
            if (c == 1) {
                zoomVal = ratio * quarterVal;
                c++;
            } else if (c == 2) {
                zoomVal = ratio * midVal;
                c = 0;
            } else {
                zoomVal = reset;
                c++;
            }
            mCortexDecoderLibrary.setCameraZoom(true, zoomVal);
            Toast.makeText(getApplicationContext(), "Zoom: " + (zoomVal), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideAndDisableSliders() {
        cameraSettings = false;
        mWhiteText.setVisibility(View.GONE);
        mExposureText.setVisibility(View.GONE);
        mZoomLayout.setVisibility(View.GONE);
        mExposureLayout.setVisibility(View.GONE);
        mFocusDistanceLayout.setVisibility(View.GONE);
        mZoomSlider.setOnSeekBarChangeListener(null);
        mExposureSlider.setOnSeekBarChangeListener(null);
        mSensitivitySlider.setOnSeekBarChangeListener(null);
        mFocusDistanceSlider.setOnSeekBarChangeListener(null);
    }

    public void translateLiveCameraSettingsView(View view) {
        mZoomLayout.setVisibility(View.VISIBLE);
        mExposureText.setVisibility(View.VISIBLE);
        mExposureLayout.setVisibility(View.VISIBLE);
        if (prefs.getBoolean("exposure_setting", false) && mCortexDecoderLibrary.isFixedExposureModeSupported()) {
            mExposureText.setText(String.format("%s Sec", String.format(Locale.getDefault(), "%.3f", (double) mCortexDecoderLibrary.getFixedExposureTime() / 1E9)));
        } else {
            mExposureText.setText(String.format("EV %s", String.format(Locale.getDefault(), "%+.1f", mCortexDecoderLibrary.getExposureValue())));
        }

        if (mCortexDecoderLibrary.getCurrentFocusMode().equalsIgnoreCase("fixed") && mCortexDecoderLibrary.getCameraAPI().contains("camera2") && !mCortexDecoderLibrary.isAutoFocusResetByCount() && !mCortexDecoderLibrary.isAutoFocusResetWithInterval()) {
            mFocusDistanceLayout.setVisibility(View.VISIBLE);
        } else {
            mFocusDistanceLayout.setVisibility(View.GONE);
        }
        int xDpToPx = -1 * (int) (300 * Resources.getSystem().getDisplayMetrics().density);
        int yDpToPx = (int) (1 * Resources.getSystem().getDisplayMetrics().density);
        int startX;
        int endX;
        if (!cameraSettings) {
            startX = xDpToPx;
            endX = yDpToPx;
            cameraSettings = true;
            addWhiteBalanceButtons();
        } else {
            startX = yDpToPx;
            endX = xDpToPx;
            hideAndDisableSliders();
        }

        TranslateAnimation translateAnimate = new TranslateAnimation(startX, endX, 0, 0);
        translateAnimate.setDuration(500);
        translateAnimate.setFillAfter(true);
        translateAnimate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.sliderButton).setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.sliderButton).setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /*
     ****THE SLIDER CALLBACKS FOR THE CAMERA!
     */
    private void setSliderCallbacks() {

        if (mCortexDecoderLibrary.isZoomSupported()) {
            final float[] zoomRatios = mCortexDecoderLibrary.getZoomRatios();
            if (mCortexDecoderLibrary.getCameraAPI().equalsIgnoreCase("camera2")) {
                mZoomSlider.setMax((int) (zoomRatios[zoomRatios.length - 1]));
            } else {
                mZoomSlider.setMax((int) (zoomRatios[zoomRatios.length - 1] / 10));
            }
            mZoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress == 0) {
                        mCortexDecoderLibrary.setCameraZoom(true, zoomRatios[0]);
                    } else if (progress <= zoomRatios[zoomRatios.length - 1]) {
                        mCortexDecoderLibrary.setCameraZoom(true, progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        int max;
        if (prefs.getBoolean("exposure_setting", false) && mCortexDecoderLibrary.isFixedExposureModeSupported()) {
            final long[] exposureTimeRange = mCortexDecoderLibrary.getFixedExposureTimeRange();
            max = (int) Math.abs(exposureTimeRange[0]) + (int) Math.abs(exposureTimeRange[1]);
            mExposureSlider.setMax(max);
            int time = (int) mCortexDecoderLibrary.getFixedExposureTime();
            mExposureSlider.setProgress(Math.abs((int) exposureTimeRange[0] + time));
            mExposureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Format exposureTime from nanoseconds to seconds by dividing exposureTime by 1,000,000,000
                    String exposureText;
                    if ((progress + exposureTimeRange[0]) >= exposureTimeRange[1]) {
                        mCortexDecoderLibrary.setFixedExposureTime(exposureTimeRange[1]);
                        exposureText = String.format(Locale.getDefault(), "%.3f", (double) mCortexDecoderLibrary.getFixedExposureTime() / 1E9);
                    } else {
                        mCortexDecoderLibrary.setFixedExposureTime(exposureTimeRange[0] + progress);
                        exposureText = String.format(Locale.getDefault(), "%.3f", (double) mCortexDecoderLibrary.getFixedExposureTime() / 1E9);
                    }
                    mExposureText.setText(String.format("%s Sec", exposureText));
                    Log.i(TAG, "Exposure Time: " + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } else {
            final int[] exposureCompensationRange = mCortexDecoderLibrary.getExposureCompensationRange();
            max = Math.abs(exposureCompensationRange[0]) + Math.abs(exposureCompensationRange[1]);
            mExposureSlider.setMax(max);
            int compensation = (int) (mCortexDecoderLibrary.getExposureValue() / mCortexDecoderLibrary.getExposureStep());
            mExposureSlider.setProgress(Math.abs(exposureCompensationRange[0]) + compensation);
            mExposureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    String exposureText;
                    if ((progress + exposureCompensationRange[0]) >= exposureCompensationRange[1]) {
                        mCortexDecoderLibrary.setExposureCompensation(exposureCompensationRange[1]);
                        exposureText = String.format(Locale.getDefault(), "%+.1f", mCortexDecoderLibrary.getExposureValue());
                    } else {
                        mCortexDecoderLibrary.setExposureCompensation(exposureCompensationRange[0] + progress);
                        exposureText = String.format(Locale.getDefault(), "%+.1f", mCortexDecoderLibrary.getExposureValue());
                    }
                    mExposureText.setText(String.format("EV %s", exposureText));
                    Log.i(TAG, "Exposure Compensation Index: " + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        ArrayList<String> isoVals = mCortexDecoderLibrary.getSensitivityBoost();
        if (isoVals != null && isoVals.size() > 0) {
            int value = Integer.parseInt(isoVals.get(isoVals.size() - 1));
            mSensitivitySlider.setMax(value);
        }
        mSensitivitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCortexDecoderLibrary.setExposureSensitivity(Integer.toString(progress));
                Log.i("Senstivity", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (mCortexDecoderLibrary.getCameraAPI().equalsIgnoreCase("camera2")) {
            float[] focusMin = mCortexDecoderLibrary.getFocusDistance();
            if (focusMin.length > 1) {
                mFocusDistanceSlider.setMax(Math.round(focusMin[1]));
            }
            mFocusDistanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mCortexDecoderLibrary.setFocusDistance(progress);
                    Log.i("Progress", progress + "");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

    }

    private void startTimer() {
        if (timer == null)
            timer = new Timer();

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                removeLocatorOverlays();
            }
        };

        timer.schedule(timerTask, 250);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    //  ****************** Used to set the region of interest **************************


    /*void setRoiInterest() {
        int[] roiArr = mCortexDecoderLibrary.convertCamScreenToDecoderImageCoordinates(124, 525, 140, 973, Resolution.Resolution_1280x720, displayMetrics);
        mApplication.mCortexDecoderLibrary.ensureRegionOfInterest(true);
        mApplication.mCortexDecoderLibrary.regionOfInterestLeft(roiArr[0]);
        mApplication.mCortexDecoderLibrary.regionOfInterestWidth(roiArr[1]);
        mApplication.mCortexDecoderLibrary.regionOfInterestTop(roiArr[2]);
        mApplication.mCortexDecoderLibrary.regionOfInterestHeight(roiArr[3]);
    }*/

    private void requestPermission(final String[] permissions, final int requestCode) {
        boolean shouldProvideRationale = ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                permissions[0])) || (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])));

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.main_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(CortexScan.this,
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
            ActivityCompat.requestPermissions(CortexScan.this,
                    permissions,
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                if (mCortexDecoderLibrary.isLicenseActivated() && !mCortexDecoderLibrary.isLicenseExpired()) {
                    postStartCameraAndDecodeTask();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackBarForPermissions();
            }
        } else if (requestCode == STORAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                startScanningAndDecoding();
            } else {
                // Permission denied.
                showSnackBarForPermissions();
            }
        } else if (requestCode == CAMERA_STORAGE_PERMISSION_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults.length != 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied.
                showSnackBarForPermissions();
            }
            // Permission was granted.
        }
    }

    private void showSnackBarForPermissions() {
        Snackbar.make(
                findViewById(R.id.main_view),
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void showSnackBarRationale(final String[] permissions) {
        boolean shouldShowRationale = false;
        final List<String> permissionStr = new ArrayList<>();

        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CortexScan.this, permission)) {
                shouldShowRationale = true;
                permissionStr.add(permission);
            }
        }

        if (shouldShowRationale) {
            Snackbar.make(
                    findViewById(R.id.main_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            if (permissionStr.size() == 1) {
                                if (permissionStr.get(0).equals(Manifest.permission.CAMERA)) {
                                    ActivityCompat.requestPermissions(CortexScan.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            CAMERA_PERMISSIONS_REQUEST_CODE);
                                } else {
                                    ActivityCompat.requestPermissions(CortexScan.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSIONS_REQUEST_CODE);
                                }
                            } else if (permissionStr.size() == 2) {
                                ActivityCompat.requestPermissions(CortexScan.this,
                                        permissions,
                                        CAMERA_STORAGE_PERMISSION_CODE);
                            }
                        }
                    })
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), "Please grant all permissions", Toast.LENGTH_SHORT).show();
        }

    }

}
