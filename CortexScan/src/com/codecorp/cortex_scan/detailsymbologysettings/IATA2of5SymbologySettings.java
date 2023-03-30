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
public class IATA2of5SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = IATA2of5SymbologySettings.class.getSimpleName();
    private SeekBarPreference mIATA2o5SeekBar;
    private ListPreference mIATA2o5Checksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.iata2o5_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        Symbologies.IATA2of5Properties iata2of5Properties = new Symbologies.IATA2of5Properties();

        int minChar = iata2of5Properties.getMinChars(getApplicationContext());
        mIATA2o5SeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("iata2o5_min_chars");
        mIATA2o5SeekBar.setSummary(Integer.toString(minChar));
        mIATA2o5SeekBar.setProgress(minChar);

        mIATA2o5Checksum = (ListPreference) getPreferenceScreen().findPreference("iata2o5_checksum");
        Symbologies.Symbology2of5PropertiesChecksum checksum = iata2of5Properties.getChecksumProperties(getApplicationContext());
        mIATA2o5Checksum.setSummary(get2of5ChecksumEntryForSummary(checksum));
        mIATA2o5Checksum.setValue(get2of5ChecksumEntryValue(checksum));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            iata2of5Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        mIATA2o5SeekBar.setSummary(Integer.toString(mIATA2o5SeekBar.getProgress()));
        mIATA2o5Checksum.setSummary(mIATA2o5Checksum.getEntry());
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
                return mIATA2o5Checksum.getEntries()[0];
            case Checksum_Enabled:
                return mIATA2o5Checksum.getEntries()[1];
            case Checksum_EnabledStripCheckCharacter:
                return mIATA2o5Checksum.getEntries()[2];
            default:
                return mIATA2o5Checksum.getEntries()[0];
        }
    }
}
