package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.symbology.Symbologies;

@SuppressWarnings("deprecation")
public class UPCASymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = UPCASymbologySettings.class.getSimpleName();
    private CheckBoxPreference m2DigitSupplemental;
    private CheckBoxPreference m5DigitSupplemental;
    private CheckBoxPreference mAddSpace;
    private CheckBoxPreference mRequireSupplemental;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.upca_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        Symbologies.UPCAProperties upcaProperties = new Symbologies.UPCAProperties();

        m2DigitSupplemental = (CheckBoxPreference) getPreferenceScreen().findPreference("upca_2digit_supp");
        m2DigitSupplemental.setChecked(upcaProperties.isSupplemental2DigitDecodingEnabled(getApplicationContext()));

        m5DigitSupplemental = (CheckBoxPreference) getPreferenceScreen().findPreference("upca_5digit_supp");
        m5DigitSupplemental.setChecked(upcaProperties.isSupplemental5DigitDecodingEnabled(getApplicationContext()));

        mAddSpace = (CheckBoxPreference) getPreferenceScreen().findPreference("upca_add_space");
        mAddSpace.setChecked(upcaProperties.isAddSpaceEnabled(getApplicationContext()));

        mRequireSupplemental = (CheckBoxPreference) getPreferenceScreen().findPreference("upca_require_supp");
        mRequireSupplemental.setChecked(upcaProperties.isRequireSupplemental(getApplicationContext()));

        loadSettings();

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            upcaProperties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        loadSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void loadSettings() {
        if (!m2DigitSupplemental.isChecked() && !m5DigitSupplemental.isChecked()) {
            mAddSpace.setChecked(false);
            mAddSpace.setEnabled(false);

            mRequireSupplemental.setChecked(false);
            mRequireSupplemental.setEnabled(false);
        } else {
            mAddSpace.setEnabled(true);
            mRequireSupplemental.setEnabled(true);
        }
    }
}
