package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;

import com.codecorp.decoder.CortexDecoderLibrary;

import java.util.HashSet;
import java.util.Set;

import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private TwoStatePreference mContinousMode;
    private TwoStatePreference mDebugMode;
    private ListPreference mDebugLevel;
    private ListPreference mBeepSoundList;
    private ListPreference mEncodingChar;
    private TwoStatePreference mLargeData;
    private TwoStatePreference mVibrator;
    private MyApplication mApplication;
    private SeekBarPreference mSeekBar;
    private TwoStatePreference mPickListMode;
    private TwoStatePreference mExactlyNBarcodes;
    private ListPreference mSaveImagesNew;
    private ListPreference mDirectPartMarkingMode;
    //private Set<String> defaultDPM = new HashSet<>();
    private PreferenceScreen mSettingsPreferences;
    private HashSet<CortexDecoderLibrary.CD_PerformanceType> mLicensedPerformanceFeatures;
    private TwoStatePreference mMultiFrameDecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MyApplication) getApplication();
        // Load the XML preferences file
        addPreferencesFromResource(R.xml.preferences);
        mSettingsPreferences = (PreferenceScreen) getPreferenceScreen().findPreference("settings_preferences");
        mContinousMode = (TwoStatePreference) getPreferenceScreen()
                .findPreference("continuous_scan_mode");
        mVibrator = (TwoStatePreference) getPreferenceScreen().findPreference("vibrate_on_scan");
        mDebugMode = (TwoStatePreference) getPreferenceScreen().findPreference("debug_mode");
        //getPreferenceScreen().removePreference(mDebugMode);
        mDebugLevel = (ListPreference) getPreferenceScreen().findPreference("debug_level");
        getPreferenceScreen().removePreference(mDebugLevel);
        mDebugLevel.setSummary(mDebugLevel.getEntry());

        mBeepSoundList = (ListPreference) getPreferenceScreen().findPreference("beep_sounds");

        mEncodingChar = (ListPreference) getPreferenceScreen().findPreference("encoding_character");

        mLargeData = (TwoStatePreference) getPreferenceScreen().findPreference("large_data_display");
        mSeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("seekbar_value");
        mSeekBar.setSummary(Integer.toString(mSeekBar.getProgress()));

        mPickListMode = (TwoStatePreference) getPreferenceScreen().findPreference("picklist_mode");

        mExactlyNBarcodes = (TwoStatePreference) getPreferenceScreen().findPreference("n_barcodes");
        mMultiFrameDecode = (TwoStatePreference) getPreferenceScreen().findPreference("multi_frame_decode");

        mSaveImagesNew = (ListPreference) getPreferenceScreen().findPreference("save_images");
        mSaveImagesNew.setSummary(mSaveImagesNew.getEntry());
        getPreferenceScreen().removePreference(mSaveImagesNew);

        TwoStatePreference mDecodeCountPerMin = (TwoStatePreference) getPreferenceScreen().findPreference("decode_count");
        getPreferenceScreen().removePreference(mDecodeCountPerMin); //Comment out this line for testing

        mDirectPartMarkingMode = (ListPreference) getPreferenceScreen().findPreference("direct_part_marking");
        if (mApplication.mCortexDecoderLibrary != null) {
            CortexDecoderLibrary.CD_DPMType type = mApplication.mCortexDecoderLibrary.getCurrentDPMType();
            mDirectPartMarkingMode.setSummary(getDPMEntryForSummary(type));
            mDirectPartMarkingMode.setValue(getDPMEntryValue(type));
        }
        getPreferenceScreen().findPreference("symbologies")
                .setOnPreferenceClickListener(this);

        Preference licenseActivatePreference = getPreferenceScreen().findPreference("license_activate");
        licenseActivatePreference.setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("license_activate").setOnPreferenceClickListener(this);
        //only G based library is used
        if(CortexDecoderLibrary.sharedObject(this, "").decoderVersionLevel().equals(CortexDecoderLibrary.DECODER_VERSION_LEVEL_G) == false){
            getPreferenceScreen().removePreference(licenseActivatePreference);
        }

        getPreferenceScreen().findPreference("camera_settings").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("decode_data_format_parse").setOnPreferenceClickListener(this);
        updateSettings();

        setLargeDataListener();
        setContinousModeListener();


        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private String getDPMEntryValue(CortexDecoderLibrary.CD_DPMType dpmType) {
        switch (dpmType) {
            case CD_DPM_Disabled:
                return "0";
            case CD_DPM_DarkOnLight:
                return "1";
            case CD_DPM_LightOnDark:
                return "2";
            case CD_DPM_LaserChemEtch:
                return "3";
            default:
                return "0";
        }
    }

    private CharSequence getDPMEntryForSummary(CortexDecoderLibrary.CD_DPMType dpmType) {
        switch (dpmType) {
            case CD_DPM_Disabled:
                return mDirectPartMarkingMode.getEntries()[0];
            case CD_DPM_DarkOnLight:
                return mDirectPartMarkingMode.getEntries()[1];
            case CD_DPM_LightOnDark:
                return mDirectPartMarkingMode.getEntries()[2];
            case CD_DPM_LaserChemEtch:
                return mDirectPartMarkingMode.getEntries()[3];
            default:
                return mDirectPartMarkingMode.getEntries()[0];
        }
    }

    private void setDPMSummary() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> dpmStringSet = sharedPrefs.getStringSet("direct_part_marking", new HashSet<String>());
        StringBuilder sBuilder = new StringBuilder();
        for (String dpmVal : dpmStringSet) {
            switch (dpmVal) {
                case "0":
                    sBuilder.append("Dark on Light,");
                    break;
                case "1":
                    sBuilder.append(" Light on Dark,");
                    break;
                case "2":
                    sBuilder.append(" Laser/ Chem Etching");
                    break;
            }
        }
        String s = sBuilder.toString();
        //remove the punctuation at the end of the string
        if (s.length() > 0) {
            mDirectPartMarkingMode.setSummary((s.charAt(s.length() - 1) == ',') ? s.substring(0, s.length() - 1) : s);
        } else {
            mDirectPartMarkingMode.setSummary(s);
        }
    }

    private void updateSettings() {
        debug(TAG, "updateSettings()");
        if (mExactlyNBarcodes.isChecked() && mSeekBar.getProgress() != 1) {
            mPickListMode.setChecked(false);
            mPickListMode.setEnabled(false);
        } else {
            mPickListMode.setEnabled(true);
        }

        if (mMultiFrameDecode.isChecked()) {
            mExactlyNBarcodes.setChecked(false);
            mExactlyNBarcodes.setEnabled(false);
        } else {
            mExactlyNBarcodes.setEnabled(true);
        }

        // Check if device supports vibration
        mVibrator.setEnabled(((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("symbologies")) {
            Intent intent = new Intent();
            intent.setClass(this, SymbologiesActivity.class);
            startActivity(intent);
        } else if (preference.getKey().equals("large_data_display")) {
            if (mContinousMode.isChecked()) {
                mContinousMode.setChecked(false);
                mContinousMode.setEnabled(false);
            }
        } else if (preference.getKey().equals("license_activate")) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(this, LicenseActivity.class);
            } else {
                intent = new Intent(this, LicenseActivityDeprecated.class);
            }
            startActivity(intent);
        } else if (preference.getKey().equals("camera_settings")) {
            Intent intent = new Intent(this, CameraSettings.class);
            startActivity(intent);
        } else if (preference.getKey().equals("direct_part_marking")) {
            mDirectPartMarkingMode.setSummary(mDirectPartMarkingMode.getEntry());
            //setDPMSummary();
        } else if (preference.getKey().equals("decode_data_format_parse")) {
            Intent intent = new Intent(this, DecodeDataParsingFormatting.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPreferencesOnResume();
        if (mApplication.mCortexDecoderLibrary != null && mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            disableUnLicensedSymbologies();

        }
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void disableUnLicensedSymbologies() {
        mLicensedPerformanceFeatures = mApplication.mCortexDecoderLibrary.getLicensedPerformanceFeatures();
        for (int i = 0; i < mSettingsPreferences.getPreferenceCount(); i++) {
            String key = mSettingsPreferences.getPreference(i).getKey();
            Preference pref = mSettingsPreferences.getPreference(i);
            comparePreferenceToDisable(key, pref);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    boolean noSideEffects = false;

    public void onSharedPreferenceChanged(SharedPreferences prefs,
                                          String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        if (noSideEffects)
            return;
        mEncodingChar.setSummary(mEncodingChar.getEntry());
        mBeepSoundList.setSummary(mBeepSoundList.getEntry());
        mDebugLevel.setSummary(mDebugLevel.getEntry());
        mSeekBar.setSummary(Integer.toString(mSeekBar.getProgress()));
        mSaveImagesNew.setSummary(mSaveImagesNew.getEntry());
        mDirectPartMarkingMode.setSummary(mDirectPartMarkingMode.getEntry());
        //setDPMSummary();
        if (key.equals("beep_sounds")) {
            String tmpKey = mBeepSoundList.getValue();
            if (mApplication.mCortexDecoderLibrary != null) {
                mApplication.mCortexDecoderLibrary.changeBeepPlayerSound(tmpKey);
                mApplication.mCortexDecoderLibrary.playBeepSound();
            }
        } else if (key.equals("n_barcodes")) {
            if (mExactlyNBarcodes.isChecked() && mSeekBar.getProgress() != 1) {
                setPreference(prefs, "picklist_mode", false);
                mPickListMode.setChecked(false);
                mPickListMode.setEnabled(false);
            } else {
                mPickListMode.setEnabled(true);
            }
        } else if (key.equals("picklist_mode")) {
            if (mPickListMode.isChecked()) {
                setPreference(prefs, "debug_mode", false);
                mDebugMode.setChecked(false);
                mDebugMode.setEnabled(false);
            } else {
                mDebugMode.setEnabled(true);
            }
        } else if (key.equals("seekbar_value")) {
            if (mExactlyNBarcodes.isChecked() && mSeekBar.getProgress() != 1) {
                setPreference(prefs, "picklist_mode", false);
                mPickListMode.setChecked(false);
                mPickListMode.setEnabled(false);
            } else {
                mPickListMode.setEnabled(true);
            }
        } else if (key.equals("multi_frame_decode")) {
            if (mMultiFrameDecode.isChecked()) {
                setPreference(prefs, "n_barcodes", false);
                mExactlyNBarcodes.setChecked(false);
                mExactlyNBarcodes.setEnabled(false);
            } else {
                mExactlyNBarcodes.setEnabled(true);
            }
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

    /*
    Adding a listener to determine if the preference has been either turned on or off and update accordingly
     */
    private void setLargeDataListener() {
        mLargeData.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (mContinousMode.isChecked() || mContinousMode.isEnabled()) {
                    mContinousMode.setEnabled(false);
                } else if (!mContinousMode.isEnabled()) {
                    mContinousMode.setEnabled(true);
                }
                return true;
            }
        });

    }

    private void setContinousModeListener() {
        mContinousMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.equals(true)) {
                    mDebugMode.setChecked(false);
                    mDebugMode.setEnabled(false);
                } else {
                    mDebugMode.setEnabled(true);
                    mDebugMode.setChecked(false);
                }
                if (noSideEffects)
                    return false;
                if (mLargeData.isEnabled()) {
                    noSideEffects = true;
                    noSideEffects = false;
                    mLargeData.setEnabled(false);
                } else {
                    mLargeData.setEnabled(true);
                }
                return true;
            }
        });
    }

    /*
    Every time we exit the settings activity and come back we have to disable certain preferences if they are on or off
     */
    private void setPreferencesOnResume() {
        if (mContinousMode.isChecked()) {
            mLargeData.setEnabled(false);
            mDebugMode.setEnabled(false);
            mDebugMode.setChecked(false);
        } else if (mLargeData.isChecked())
            mContinousMode.setEnabled(false);

        if (mPickListMode.isChecked()) {
            mDebugMode.setChecked(false);
            mDebugMode.setEnabled(false);
        }

        if (mMultiFrameDecode.isChecked()) {
            mExactlyNBarcodes.setChecked(false);
            mExactlyNBarcodes.setEnabled(false);
        } else {
            mExactlyNBarcodes.setEnabled(true);
        }

    }

    private void comparePreferenceToDisable(String key, Preference pref) {
        switch (key) {
            case "low_contrast_mode":
                greyOutPreference(CortexDecoderLibrary.CD_PerformanceType.CD_PerformanceType_LOW_CONTRAST, pref);
                break;
            case "direct_part_marking":
                greyOutPreference(CortexDecoderLibrary.CD_PerformanceType.CD_PerformanceType_DPM, pref);
                break;
        }
    }

    private void greyOutPreference(CortexDecoderLibrary.CD_PerformanceType type, Preference p) {
        if (!mLicensedPerformanceFeatures.contains(type)) {
            if (p instanceof TwoStatePreference) {
                ((TwoStatePreference) p).setChecked(false);
            }
            p.setEnabled(false);
        }
    }

}
