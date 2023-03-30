package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.cortex_scan.SeekBarPreference;
import com.codecorp.symbology.Symbologies;

import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class Code39SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = Code39SymbologySettings.class.getSimpleName();
    private SeekBarPreference mCode39SeekBar;
    private ListPreference mCode39Checksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.code39_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        Symbologies.Code39Properties code39Properties = new Symbologies.Code39Properties();

        int minChar = code39Properties.getMinChars(getApplicationContext());
        mCode39SeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("code39_min_chars");
        mCode39SeekBar.setSummary(Integer.toString(minChar));
        mCode39SeekBar.setProgress(minChar);

        CheckBoxPreference mFullASCIIModeEnabled = (CheckBoxPreference) getPreferenceScreen().findPreference("code39_ascii_support");
        mFullASCIIModeEnabled.setChecked(code39Properties.isAsciiModeEnabled(getApplicationContext()));

        mCode39Checksum = (ListPreference) getPreferenceScreen().findPreference("code39_checksum");

        Symbologies.Code39PropertiesChecksum checksum = code39Properties.getChecksumProperties(getApplicationContext());
        mCode39Checksum.setSummary(getCode39ChecksumEntryForSummary(checksum));
        mCode39Checksum.setValue(getCode39ChecksumEntryValue(checksum));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            code39Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        mCode39SeekBar.setSummary(Integer.toString(mCode39SeekBar.getProgress()));
        mCode39Checksum.setSummary(mCode39Checksum.getEntry());
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

    private String getCode39ChecksumEntryValue(Symbologies.Code39PropertiesChecksum code39PropertiesChecksum) {
        switch (code39PropertiesChecksum) {
            case Code39PropertiesChecksum_Disabled:
                return "0";
            case Code39PropertiesChecksum_Enabled:
                return "1";
            case Code39PropertiesChecksum_EnabledStripCheckCharacter:
                return "2";
            default:
                return "0";
        }
    }

    private CharSequence getCode39ChecksumEntryForSummary(Symbologies.Code39PropertiesChecksum code39PropertiesChecksum) {
        switch (code39PropertiesChecksum) {
            case Code39PropertiesChecksum_Disabled:
                return mCode39Checksum.getEntries()[0];
            case Code39PropertiesChecksum_Enabled:
                return mCode39Checksum.getEntries()[1];
            case Code39PropertiesChecksum_EnabledStripCheckCharacter:
                return mCode39Checksum.getEntries()[2];
            default:
                return mCode39Checksum.getEntries()[0];
        }
    }
}
