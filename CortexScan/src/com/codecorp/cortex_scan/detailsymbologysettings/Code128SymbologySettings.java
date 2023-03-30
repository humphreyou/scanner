package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.cortex_scan.SeekBarPreference;
import com.codecorp.symbology.Symbologies;

import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class Code128SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = Code128SymbologySettings.class.getSimpleName();
    private SeekBarPreference mCode128SeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.code128_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        mCode128SeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("code128_min_chars");

        Symbologies.Code128Properties code128Properties = new Symbologies.Code128Properties();
        int minChar = code128Properties.getMinChars(getApplicationContext());
        mCode128SeekBar.setSummary(Integer.toString(minChar));
        mCode128SeekBar.setProgress(minChar);

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            code128Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        mCode128SeekBar.setSummary(Integer.toString(mCode128SeekBar.getProgress()));
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
}
