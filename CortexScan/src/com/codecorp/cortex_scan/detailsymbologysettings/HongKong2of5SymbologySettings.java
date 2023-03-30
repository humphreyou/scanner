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
public class HongKong2of5SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = HongKong2of5SymbologySettings.class.getSimpleName();
    private ListPreference mHK205Checksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.hk2o5_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        mHK205Checksum = (ListPreference) getPreferenceScreen().findPreference("hk2o5_checksum");

        Symbologies.HongKong2of5Properties hongKong2of5Properties = new Symbologies.HongKong2of5Properties();
        Symbologies.Symbology2of5PropertiesChecksum checksum = hongKong2of5Properties.getChecksumProperties(getApplicationContext());
        mHK205Checksum.setSummary(get2of5ChecksumEntryForSummary(checksum));
        mHK205Checksum.setValue(get2of5ChecksumEntryValue(checksum));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            hongKong2of5Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mHK205Checksum.setSummary(mHK205Checksum.getEntry());
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
                return mHK205Checksum.getEntries()[0];
            case Checksum_Enabled:
                return mHK205Checksum.getEntries()[1];
            case Checksum_EnabledStripCheckCharacter:
                return mHK205Checksum.getEntries()[2];
            default:
                return mHK205Checksum.getEntries()[0];
        }
    }
}
