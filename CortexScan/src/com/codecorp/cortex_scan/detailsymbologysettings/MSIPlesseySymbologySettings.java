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
public class MSIPlesseySymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = MSIPlesseySymbologySettings.class.getSimpleName();
    private SeekBarPreference mMSIPlesseySeekBar;
    private ListPreference mMSIPlesseyChecksum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.msiplessey_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        Symbologies.MSIPlesseyProperties msiPlesseyProperties = new Symbologies.MSIPlesseyProperties();

        mMSIPlesseySeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("msiplessey_min_chars");
        int minChar = msiPlesseyProperties.getMinChars(getApplicationContext());
        mMSIPlesseySeekBar.setSummary(Integer.toString(minChar));
        mMSIPlesseySeekBar.setProgress(minChar);

        mMSIPlesseyChecksum = (ListPreference) getPreferenceScreen().findPreference("msiplessey_checksum");
        Symbologies.MSIPlesseyPropertiesChecksum checksum = msiPlesseyProperties.getChecksumProperties(getApplicationContext());
        boolean stripCheckChar = msiPlesseyProperties.isStripChecksumEnabled(getApplicationContext());
        mMSIPlesseyChecksum.setSummary(getMSIPlesseyChecksumEntryForSummary(checksum, stripCheckChar));
        mMSIPlesseyChecksum.setValue(getMSIPlesseyChecksumEntryValue(checksum, stripCheckChar));

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            msiPlesseyProperties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        mMSIPlesseySeekBar.setSummary(Integer.toString(mMSIPlesseySeekBar.getProgress()));
        mMSIPlesseyChecksum.setSummary(mMSIPlesseyChecksum.getEntry());
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

    private String getMSIPlesseyChecksumEntryValue(Symbologies.MSIPlesseyPropertiesChecksum msiPlesseyPropertiesChecksum, boolean stripCheckCharEnabled) {
        switch (msiPlesseyPropertiesChecksum) {
            case MSIPlesseyPropertiesChecksum_Disabled:
                return "0";
            case MSIPlesseyPropertiesChecksum_EnabledMod10:
                if (!stripCheckCharEnabled) return "1";
                else return "2";
            case MSIPlesseyPropertiesChecksum_EnabledMod10_10:
                if (!stripCheckCharEnabled) return "3";
                else return "4";
            case MSIPlesseyPropertiesChecksum_EnabledMod11_10:
                if (!stripCheckCharEnabled) return "5";
                else return "6";
            default:
                return "0";
        }
    }

    private CharSequence getMSIPlesseyChecksumEntryForSummary(Symbologies.MSIPlesseyPropertiesChecksum msiPlesseyPropertiesChecksum, boolean stripCheckCharEnabled) {
        switch (msiPlesseyPropertiesChecksum) {
            case MSIPlesseyPropertiesChecksum_Disabled:
                return mMSIPlesseyChecksum.getEntries()[0];
            case MSIPlesseyPropertiesChecksum_EnabledMod10:
                if (!stripCheckCharEnabled) return mMSIPlesseyChecksum.getEntries()[1];
                else return mMSIPlesseyChecksum.getEntries()[2];
            case MSIPlesseyPropertiesChecksum_EnabledMod10_10:
                if (!stripCheckCharEnabled) return mMSIPlesseyChecksum.getEntries()[3];
                else return mMSIPlesseyChecksum.getEntries()[4];
            case MSIPlesseyPropertiesChecksum_EnabledMod11_10:
                if (!stripCheckCharEnabled) return mMSIPlesseyChecksum.getEntries()[5];
                else return mMSIPlesseyChecksum.getEntries()[6];
            default:
                return mMSIPlesseyChecksum.getEntries()[0];
        }
    }
}

