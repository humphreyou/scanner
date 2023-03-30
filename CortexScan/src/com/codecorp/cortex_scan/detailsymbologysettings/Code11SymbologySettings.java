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
public class Code11SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = Code11SymbologySettings.class.getSimpleName();
    private ListPreference mCode11Checksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.code11_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        mCode11Checksum = (ListPreference) getPreferenceScreen().findPreference("code11_checksum");

        Symbologies.Code11Properties code11Properties = new Symbologies.Code11Properties();
        Symbologies.Code11PropertiesChecksum checksum = code11Properties.getChecksumProperties(getApplicationContext());
        boolean stripCheckChar = code11Properties.isStripChecksumEnabled(getApplicationContext());
        mCode11Checksum.setSummary(getCode11ChecksumEntryForSummary(checksum, stripCheckChar));
        mCode11Checksum.setValue(getCode11ChecksumEntryValue(checksum, stripCheckChar));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            code11Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mCode11Checksum.setSummary(mCode11Checksum.getEntry());
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

    private String getCode11ChecksumEntryValue(Symbologies.Code11PropertiesChecksum code11PropertiesChecksum, boolean checkStripChar) {
        switch (code11PropertiesChecksum) {
            case Code11PropertiesChecksum_Disabled:
                return "0";
            case Code11PropertiesChecksum_Enabled1Digit:
                if (!checkStripChar) return "1";
                else return "2";
            case Code11PropertiesChecksum_Enabled2Digit:
                if (!checkStripChar) return "3";
                else return "4";
            default:
                return "0";
        }
    }

    private CharSequence getCode11ChecksumEntryForSummary(Symbologies.Code11PropertiesChecksum code11PropertiesChecksum, boolean checkStripChar) {
        switch (code11PropertiesChecksum) {
            case Code11PropertiesChecksum_Disabled:
                return mCode11Checksum.getEntries()[0];
            case Code11PropertiesChecksum_Enabled1Digit:
                if (!checkStripChar) return mCode11Checksum.getEntries()[1];
                else return mCode11Checksum.getEntries()[2];
            case Code11PropertiesChecksum_Enabled2Digit:
                if (!checkStripChar) return mCode11Checksum.getEntries()[3];
                else return mCode11Checksum.getEntries()[4];
            default:
                return mCode11Checksum.getEntries()[0];
        }
    }
}
