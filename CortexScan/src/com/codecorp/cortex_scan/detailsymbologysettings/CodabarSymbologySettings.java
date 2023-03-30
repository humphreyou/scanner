package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.cortex_scan.SeekBarPreference;
import com.codecorp.symbology.Symbologies;

import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class CodabarSymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = CodabarSymbologySettings.class.getSimpleName();
    private SeekBarPreference mCodabarSeekBar;
    private ListPreference mCodabarChecksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.codabar_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        Symbologies.CodabarProperties codabarProperties = new Symbologies.CodabarProperties();

        int  minChar = codabarProperties.getMinChars(getApplicationContext());
        mCodabarSeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("codabar_min_chars");
        mCodabarSeekBar.setSummary(Integer.toString(minChar));
        mCodabarSeekBar.setProgress(minChar);
        mCodabarChecksum = (ListPreference) getPreferenceScreen().findPreference("codabar_checksum");

        Symbologies.CodabarPropertiesChecksum checksum = codabarProperties.getChecksumProperties(getApplicationContext());
        mCodabarChecksum.setSummary(getCodabarChecksumEntryForSummary(checksum));
        mCodabarChecksum.setValue(getCodabarChecksumEntryValue(checksum));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            codabarProperties.setEnabled(getApplicationContext(), true);
        }


        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        mCodabarSeekBar.setSummary(Integer.toString(mCodabarSeekBar.getProgress()));
        mCodabarChecksum.setSummary(mCodabarChecksum.getEntry());
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

    private String getCodabarChecksumEntryValue(Symbologies.CodabarPropertiesChecksum codabarPropertiesChecksum) {
        switch (codabarPropertiesChecksum) {
            case CodabarPropertiesChecksum_Disabled:
                return "0";
            case CodabarPropertiesChecksum_Enabled:
                return "1";
            case CodabarPropertiesChecksum_EnabledStripCheckCharacter:
                return "2";
            default:
                return "0";
        }
    }

    private CharSequence getCodabarChecksumEntryForSummary(Symbologies.CodabarPropertiesChecksum codabarPropertiesChecksum) {
        switch (codabarPropertiesChecksum) {
            case CodabarPropertiesChecksum_Disabled:
                return mCodabarChecksum.getEntries()[0];
            case CodabarPropertiesChecksum_Enabled:
                return mCodabarChecksum.getEntries()[1];
            case CodabarPropertiesChecksum_EnabledStripCheckCharacter:
                return mCodabarChecksum.getEntries()[2];
            default:
                return mCodabarChecksum.getEntries()[0];
        }
    }
}
