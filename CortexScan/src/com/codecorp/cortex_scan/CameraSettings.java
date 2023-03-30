package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.widget.Toast;

import com.codecorp.camera.CameraType;
import com.codecorp.camera.Focus;

import java.util.ArrayList;
import java.util.HashSet;

import static android.widget.Toast.LENGTH_SHORT;
import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class CameraSettings extends PreferenceActivity implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = CameraSettings.class.getSimpleName();
    private ListPreference mCameraType;
    private MyApplication mApplication;
    TwoStatePreference mIllumination;
    private ListPreference mResolution;
    private TwoStatePreference mFixedExposureMode;
    private ListPreference mFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.camerasettings);
        mApplication = (MyApplication) getApplication();

        mCameraType = (ListPreference) getPreferenceScreen().findPreference("camera_type");
        mIllumination = (TwoStatePreference) getPreferenceScreen().findPreference("illumination");
        mResolution = (ListPreference) getPreferenceScreen().findPreference("resolution");
        mResolution.setSummary(mResolution.getEntry());
        mFixedExposureMode = (TwoStatePreference) getPreferenceScreen().findPreference("exposure_setting");
        mFocus = (ListPreference) getPreferenceScreen().findPreference("focus");
        getPreferenceScreen().findPreference("advanced_focus_setting").setOnPreferenceClickListener(this);
        updateSettings();
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void updateSettings() {
        if (!mApplication.mCortexDecoderLibrary.hasTorch()) {
            mIllumination.setChecked(false);
            mIllumination.setEnabled(false);
        }
        // Set up focus modes
        setupFocusModes();
        // Set up camera types
        setupCameraTypes();
        if(isAutoFocusEnabled() || !mApplication.mCortexDecoderLibrary.isFixedExposureModeSupported()){
            mFixedExposureMode.setChecked(false);
            mFixedExposureMode.setEnabled(false);
        }else{
            mFixedExposureMode.setChecked(mFixedExposureMode.isChecked());
            mFixedExposureMode.setEnabled(true);
        }

    }

    private boolean isAutoFocusEnabled(){
        boolean mAutoFocusResetbyCount = getPreferenceManager().getSharedPreferences().getBoolean("autofocus_reset_count",false);
        boolean mAutoFocusResetbyTimeInterval = getPreferenceManager().getSharedPreferences().getBoolean("autofocus_reset_interval",false);
        return mAutoFocusResetbyCount || mAutoFocusResetbyTimeInterval;
    }

    private void setupFocusModes() {
        final Focus[] focusModes =
                mApplication.mCortexDecoderLibrary.getSupportedFocusModes();
        ArrayList<CharSequence> entries = new ArrayList<>();
        ArrayList<CharSequence> values = new ArrayList<>();
        HashSet<Focus> supportedModes = new HashSet<>();
        Focus current = focusStringToMode(mFocus.getValue());
        boolean currentModeSupported = false;
        Focus firstSupported = null;
        for (Focus f : Focus.values()) {
            String s = focusModeToString(f);
            if (isFocusModeSupported(focusModes, f)) {
                entries.add(s);
                values.add(s);
                firstSupported = f;
                if (f == current) currentModeSupported = true;
                supportedModes.add(f);
            } else {
                entries.add(s + " (" + getResources().getString(R.string.unsupported) + ")");
                values.add(s);
            }
        }
        mFocus.setEntries(entries.toArray(new CharSequence[0]));
        mFocus.setEntryValues(values.toArray(new CharSequence[0]));
        if (!currentModeSupported) {
            // Fixed Normal has highest priority
            if (supportedModes.contains(Focus.Focus_Fix)) {
                mFocus.setValue(focusModeToString(Focus.Focus_Fix));
            } else if (firstSupported != null) {
                // Pick the first mode that is supported
                mFocus.setValue(focusModeToString(firstSupported));
            }
        }
        mFocus.setSummary(mFocus.getEntry());
        mFocus.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String s = (String) newValue;
                boolean supported = isFocusModeSupported(focusModes, focusStringToMode(s));
                if (!supported)
                    Toast.makeText(CameraSettings.this, R.string.focus_mode_not_supported,
                            LENGTH_SHORT).show();
                return supported;
            }
        });
    }

    private String focusModeToString(Focus f) {
        if (f == Focus.Focus_Auto)
            return getString(R.string.focus_normal);
        if (f == Focus.Focus_Fix)
            return getString(R.string.focus_fixed);
        if (f == Focus.Focus_Far)
            return getString(R.string.focus_far);
        return "ERROR";
    }

    private Focus focusStringToMode(String s) {
        if (s.equals(getString(R.string.focus_normal)))
            return Focus.Focus_Auto;
        if (s.equals(getString(R.string.focus_fixed)))
            return Focus.Focus_Fix;
        if (s.equals(getString(R.string.focus_far)))
            return Focus.Focus_Far;
        return Focus.Focus_Auto;
    }

    private boolean isFocusModeSupported(Focus[] focusModes, Focus mode) {
        if (focusModes == null) {
            // For some reason CortexScan sent us a null pointer, so let's do the best we can.
            // Normal should be supported on most devices
            return mode == Focus.Focus_Auto;
        }
        for (Focus f : focusModes) {
            if (mode == f) return true;
        }
        return false;
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

    private void setupCameraTypes() {
        final CameraType[] cameraTypes =
                mApplication.mCortexDecoderLibrary.getSupportedCameraTypes();
        ArrayList<CharSequence> entries = new ArrayList<>();
        ArrayList<CharSequence> values = new ArrayList<>();
        for (CameraType f : CameraType.values()) {
            String s = cameraTypeToString(f);
            if (isCameraTypeSupported(cameraTypes, f)) {
                entries.add(s);
                values.add(s);
            } else {
                entries.add(s + " (" + getResources().getString(R.string.unsupported) + ")");
                values.add(s);
            }
        }
        mCameraType.setEntries(entries.toArray(new CharSequence[0]));
        mCameraType.setEntryValues(values.toArray(new CharSequence[0]));
        mCameraType.setSummary(mCameraType.getEntry());
        mCameraType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String s = (String) newValue;
                boolean supported = isCameraTypeSupported(cameraTypes, cameraTypeStringToEnum(s));
                if (!supported)
                    Toast.makeText(CameraSettings.this, R.string.camera_type_not_supported,
                            LENGTH_SHORT).show();
                return supported;
            }
        });
    }

    private boolean isCameraTypeSupported(CameraType[] list, CameraType cameraType) {
        if (list == null) {
            // For some reason CortexScan sent us a null pointer, so let's do the best we can.
            // Back-facing camera should be supported on most devices
            return cameraType == CameraType.BackFacing;
        }
        for (CameraType f : list) {
            if (cameraType == f) return true;
        }
        return false;
    }

    private String cameraTypeToString(CameraType f) {
        if (f == CameraType.BackFacing)
            return getString(R.string.camera_type_back);
        if (f == CameraType.FrontFacing)
            return getString(R.string.camera_type_front);
        return "ERROR";
    }

    private CameraType cameraTypeStringToEnum(String s) {
        if (s.equals(getString(R.string.camera_type_back)))
            return CameraType.BackFacing;
        if (s.equals(getString(R.string.camera_type_front)))
            return CameraType.FrontFacing;
        return CameraType.BackFacing;
    }

    boolean noSideEffects = false;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        if (noSideEffects)
            return;
        mCameraType.setSummary(mCameraType.getEntry());
        mFocus.setSummary(mFocus.getEntry());
        mResolution.setSummary(mResolution.getEntry());

        switch (key) {
            case "camera_type": {

                if (mCameraType.getEntry().equals("Back-Facing")) {
                    mIllumination.setEnabled(true);
                } else {
                    mIllumination.setChecked(false);
                    mIllumination.setEnabled(false);
                }

                mApplication.pushPreference(prefs, key);
                String tmpKey = "focus." + mCameraType.getValue();
                noSideEffects = true;
                if (prefs.contains(tmpKey))
                    mFocus.setValue(prefs.getString(tmpKey, mFocus.getValue()));
                noSideEffects = false;
                updateSettings();
                break;
            }
            case "focus": {
                String tmpKey = "focus." + mCameraType.getValue();
                setPreference(prefs, tmpKey, mFocus.getValue());
                break;
            }
            case "illumination": {
                String tmpKey = "illumination." + mCameraType.getValue();
                setPreference(prefs, tmpKey, mIllumination.isChecked());
                break;
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("advanced_focus_setting")) {
            Intent intent = new Intent(this, AdvancedFocusSettings.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPreferencesOnResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setPreferencesOnResume() {
        if (mCameraType.getEntry().equals("Front-Facing")) {
            mIllumination.setChecked(false);
            mIllumination.setEnabled(false);
        }
        if(isAutoFocusEnabled() || !mApplication.mCortexDecoderLibrary.isFixedExposureModeSupported()) {
            mFixedExposureMode.setChecked(false);
            mFixedExposureMode.setEnabled(false);
        }else{
            mFixedExposureMode.setChecked(mFixedExposureMode.isChecked());
            mFixedExposureMode.setEnabled(true);
        }
    }
}
