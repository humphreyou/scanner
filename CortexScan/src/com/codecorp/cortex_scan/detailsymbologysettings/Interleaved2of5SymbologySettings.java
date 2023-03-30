package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.cortex_scan.SeekBarPreference;
import com.codecorp.symbology.Symbologies;

import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class Interleaved2of5SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = Interleaved2of5SymbologySettings.class.getSimpleName();
    private SeekBarPreference mInterleaved2o5SeekBar;
    private ListPreference mInterleaved2o5Checksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.interleaved2o5_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        Symbologies.Interleaved2of5Properties interleaved2of5Properties = new Symbologies.Interleaved2of5Properties();

        int minChar = interleaved2of5Properties.getMinChars(getApplicationContext());
        mInterleaved2o5SeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("interleaved2o5_min_chars");
        mInterleaved2o5SeekBar.setSummary(Integer.toString(minChar));
        mInterleaved2o5SeekBar.setProgress(minChar);

        mInterleaved2o5Checksum = (ListPreference) getPreferenceScreen().findPreference("interleaved2o5_checksum");
        Symbologies.Interleaved2of5PropertiesChecksum checksum = interleaved2of5Properties.getChecksumProperties(getApplicationContext());
        mInterleaved2o5Checksum.setSummary(get2of5ChecksumEntryForSummary(checksum));
        mInterleaved2o5Checksum.setValue(get2of5ChecksumEntryValue(checksum));

        CheckBoxPreference mAllowShortQuietZone = (CheckBoxPreference) getPreferenceScreen().findPreference("interleaved2o5_allow_shortQuietZone");
        mAllowShortQuietZone.setChecked(interleaved2of5Properties.isAllowShortQuietZone(getApplicationContext()));

        CheckBoxPreference mRejectPartialDecode = (CheckBoxPreference) getPreferenceScreen().findPreference("interleaved2o5_reject_partialDecode");
        mRejectPartialDecode.setChecked(interleaved2of5Properties.isRejectPartialDecode(getApplicationContext()));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            interleaved2of5Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        if (mInterleaved2o5SeekBar.getProgress() % 2 != 0 || mInterleaved2o5SeekBar.getProgress() < 2) {
            showToastForOddMinCharsVal();
            mInterleaved2o5SeekBar.setProgress(8); //If the selected value is an odd number then default it back to 2
        }
        mInterleaved2o5Checksum.setSummary(mInterleaved2o5Checksum.getEntry());
        mInterleaved2o5SeekBar.setSummary(Integer.toString(mInterleaved2o5SeekBar.getProgress()));
    }

    private void showToastForOddMinCharsVal() {
        Toast.makeText(this, "Please choose an even integer value for minimum characters", Toast.LENGTH_SHORT).show();
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

    private String get2of5ChecksumEntryValue(Symbologies.Interleaved2of5PropertiesChecksum symbology2of5PropertiesChecksum) {
        switch (symbology2of5PropertiesChecksum) {
            case Interleaved2of5PropertiesChecksum_Disabled:
                return "0";
            case Interleaved2of5PropertiesChecksum_Enabled:
                return "1";
            case Interleaved2of5PropertiesChecksum_EnabledStripCheckCharacter:
                return "2";
            default:
                return "0";
        }
    }

    private CharSequence get2of5ChecksumEntryForSummary(Symbologies.Interleaved2of5PropertiesChecksum symbology2of5PropertiesChecksum) {
        switch (symbology2of5PropertiesChecksum) {
            case Interleaved2of5PropertiesChecksum_Disabled:
                return mInterleaved2o5Checksum.getEntries()[0];
            case Interleaved2of5PropertiesChecksum_Enabled:
                return mInterleaved2o5Checksum.getEntries()[1];
            case Interleaved2of5PropertiesChecksum_EnabledStripCheckCharacter:
                return mInterleaved2o5Checksum.getEntries()[2];
            default:
                return mInterleaved2o5Checksum.getEntries()[0];
        }
    }
}
