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
public class Code93SymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = Code93SymbologySettings.class.getSimpleName();
    private SeekBarPreference mCode93SeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.code93_symbology_settings);
        MyApplication myApplication = (MyApplication) getApplication();
        mCode93SeekBar = (SeekBarPreference) getPreferenceScreen().findPreference("code93_min_chars");

        Symbologies.Code93Properties code93Properties = new Symbologies.Code93Properties();
        int minChar = code93Properties.getMinChars(getApplicationContext());
        mCode93SeekBar.setSummary(Integer.toString(minChar));
        mCode93SeekBar.setProgress(minChar);

        if (myApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            code93Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        debug(TAG, "onSharedPreferenceChanged(" + key + ")");
        mCode93SeekBar.setSummary(Integer.toString(mCode93SeekBar.getProgress()));
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
