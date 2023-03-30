package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.symbology.Symbologies;

@SuppressWarnings("deprecation")
public class NEC2of5SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = NEC2of5SymbologySettings.class.getSimpleName();
    private ListPreference mNEC2o5Checksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nec2o5_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        mNEC2o5Checksum = (ListPreference) getPreferenceScreen().findPreference("nec2o5_checksum");

        Symbologies.NEC2of5Properties nec2of5Properties = new Symbologies.NEC2of5Properties();
        Symbologies.Symbology2of5PropertiesChecksum checksum = nec2of5Properties.getChecksumProperties(getApplicationContext());
        mNEC2o5Checksum.setSummary(get2of5ChecksumEntryForSummary(checksum));
        mNEC2o5Checksum.setValue(get2of5ChecksumEntryValue(checksum));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            nec2of5Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mNEC2o5Checksum.setSummary(mNEC2o5Checksum.getEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private String get2of5ChecksumEntryValue(Symbologies.Symbology2of5PropertiesChecksum symbology2of5PropertiesChecksum) {
        switch (symbology2of5PropertiesChecksum) {
            case Checksum_Disabled:
                return "0";
            case Checksum_Enabled:
                return "1";
            case Checksum_EnabledStripCheckCharacter:
                return "2";
            default:
                return "0";
        }
    }

    private CharSequence get2of5ChecksumEntryForSummary(Symbologies.Symbology2of5PropertiesChecksum symbology2of5PropertiesChecksum) {
        switch (symbology2of5PropertiesChecksum) {
            case Checksum_Disabled:
                return mNEC2o5Checksum.getEntries()[0];
            case Checksum_Enabled:
                return mNEC2o5Checksum.getEntries()[1];
            case Checksum_EnabledStripCheckCharacter:
                return mNEC2o5Checksum.getEntries()[2];
            default:
                return mNEC2o5Checksum.getEntries()[0];
        }
    }
}

