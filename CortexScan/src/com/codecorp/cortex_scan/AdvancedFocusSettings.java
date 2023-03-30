package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.widget.Toast;

import static com.codecorp.internal.Debug.debug;

@SuppressWarnings("deprecation")
public class AdvancedFocusSettings extends PreferenceActivity implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = AdvancedFocusSettings.class.getSimpleName();
    private ListPreference mCameraAPIPreference;
    private TwoStatePreference mAutofocusResetByCount;
    private TwoStatePreference mAutofocusResetByTimeInterval;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advancedfocussetting);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCameraAPIPreference = (ListPreference) getPreferenceScreen().findPreference("cameraAPI");
        mCameraAPIPreference.setSummary(mCameraAPIPreference.getEntry());
        mAutofocusResetByCount = (TwoStatePreference) getPreferenceScreen().findPreference("autofocus_reset_count");
        mAutofocusResetByTimeInterval = (TwoStatePreference) getPreferenceScreen().findPreference("autofocus_reset_interval");
        setAutoFocusResetByCountListener();
        setAutoFocusResetByTimeIntervalListener();
        loadPreviousSetting();
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    private void setPreference(SharedPreferences sharedPreferences,
                               String key, Object value) {
        debug(TAG, "setPreference(" + key + ", " + value + ")");
        SharedPreferences.Editor e = sharedPreferences.edit();
        if (value instanceof String)
            e.putString(key, (String) value);
        else if (value instanceof Boolean)
            e.putBoolean(key, (Boolean) value);
        else
            throw new RuntimeException("Programming error!  Unknown value type.");
        e.apply();
    }

    private void setAutoFocusResetByCountListener() {
        mAutofocusResetByCount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (mAutofocusResetByTimeInterval.isChecked() || mAutofocusResetByTimeInterval.isEnabled()) {
                    mAutofocusResetByTimeInterval.setEnabled(false);
                    mAutofocusResetByTimeInterval.setChecked(false);
                } else {
                    mAutofocusResetByTimeInterval.setEnabled(true);
                }
                mAutofocusResetByTimeInterval.setChecked(false);
                return true;
            }
        });
    }

    private void setAutoFocusResetByTimeIntervalListener() {
        mAutofocusResetByTimeInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (mAutofocusResetByCount.isChecked() || mAutofocusResetByCount.isEnabled()) {
                    mAutofocusResetByCount.setEnabled(false);
                    mAutofocusResetByCount.setChecked(false);
                } else {
                    mAutofocusResetByCount.setEnabled(true);
                }
                mAutofocusResetByCount.setChecked(false);
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mCameraAPIPreference.setSummary(mCameraAPIPreference.getEntry());
        if (key.equals(mCameraAPIPreference.getKey())) {
            Toast.makeText(getApplicationContext(), "Please restart the app to change the API selected", Toast.LENGTH_SHORT).show();
        }
        if (key.equals(mAutofocusResetByCount.getKey())) {
            if (mAutofocusResetByCount.isChecked()) {
                setPreference(sharedPreferences, "autofocus_reset_count", true);
            } else {
                setPreference(sharedPreferences, "autofocus_reset_count", false);
            }
        }
        if (key.equals(mAutofocusResetByTimeInterval.getKey())) {
            if (mAutofocusResetByTimeInterval.isChecked()) {
                setPreference(sharedPreferences, "autofocus_reset_interval", true);
            } else {
                setPreference(sharedPreferences, "autofocus_reset_interval", false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPreferencesOnResume();
        String mFocusMethodSet = mPrefs.getString("focus", "Auto");
        if (mFocusMethodSet.equalsIgnoreCase("Fixed")) {
            disableForceAutoFocusSwitches();
        }
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void disableForceAutoFocusSwitches() {
        mAutofocusResetByCount.setChecked(false);
        mAutofocusResetByCount.setEnabled(false);
        mAutofocusResetByTimeInterval.setChecked(false);
        mAutofocusResetByTimeInterval.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setPreferencesOnResume() {
        if (mAutofocusResetByCount.isChecked()) {
            mAutofocusResetByTimeInterval.setChecked(false);
            mAutofocusResetByTimeInterval.setEnabled(false);
        }

        if (mAutofocusResetByTimeInterval.isChecked()) {
            mAutofocusResetByCount.setChecked(false);
            mAutofocusResetByCount.setEnabled(false);
        }
        loadPreviousSetting();
    }

    private void loadPreviousSetting() {
        if (mAutofocusResetByCount.isEnabled() && !mAutofocusResetByCount.isChecked()) {
            mAutofocusResetByCount.setChecked(getPreferenceManager().getSharedPreferences().getBoolean("autofocus_reset_count", false));
        }

        if (mAutofocusResetByTimeInterval.isEnabled() && !mAutofocusResetByTimeInterval.isChecked()) {
            mAutofocusResetByTimeInterval.setChecked(getPreferenceManager().getSharedPreferences().getBoolean("autofocus_reset_interval", false));
        }
    }
}
