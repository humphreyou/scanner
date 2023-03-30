package com.codecorp.cortex_scan.detailsymbologysettings;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.widget.Toast;

import com.codecorp.cortex_scan.MyApplication;
import com.codecorp.cortex_scan.R;
import com.codecorp.symbology.Symbologies;
import com.codecorp.symbology.SymbologyType;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class CompositeCodeSymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    static final String TAG = CompositeCodeSymbologySettings.class.getSimpleName();
    private HashSet mLicensedSymbologies;
    private MyApplication mApplication;
    private PreferenceScreen mCompositeCodeSettingsScreen;
    private Set<String> mCCPreferenceSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.compositecode_symbology_settings);
        mApplication = (MyApplication) getApplication();
        mCompositeCodeSettingsScreen = (PreferenceScreen) getPreferenceScreen().findPreference("compositecode_settings");

        Symbologies.CompositeCodeProperties compositeCodeProperties = new Symbologies.CompositeCodeProperties();

        CheckBoxPreference mCCA = (CheckBoxPreference) getPreferenceScreen().findPreference("cca");
        mCCA.setChecked(compositeCodeProperties.isCcaDecodingEnabled(getApplicationContext()));

        CheckBoxPreference mCCB = (CheckBoxPreference) getPreferenceScreen().findPreference("ccb");
        mCCB.setChecked(compositeCodeProperties.isCcbDecodingEnabled(getApplicationContext()));

        CheckBoxPreference mCCC = (CheckBoxPreference) getPreferenceScreen().findPreference("ccc");
        mCCC.setChecked(compositeCodeProperties.isCccDecodingEnabled(getApplicationContext()));

        if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            compositeCodeProperties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void loadCheckedPreferencesToSet() {
        for (int i = 0; i < mCompositeCodeSettingsScreen.getPreferenceCount(); i++) {
            if (((CheckBoxPreference) mCompositeCodeSettingsScreen.getPreference(i)).isChecked()) {
                mCCPreferenceSet.add(mCompositeCodeSettingsScreen.getPreference(i).getKey());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initializeClickListener() {
        for (int i = 0; i < mCompositeCodeSettingsScreen.getPreferenceCount(); i++) {
            mCompositeCodeSettingsScreen.getPreference(i).setOnPreferenceClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initializeClickListener();
        loadCheckedPreferencesToSet();
        if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            disableUnLicensedCCProperties();
        }
    }

    private void disableUnLicensedCCProperties() {
        mLicensedSymbologies = mApplication.mCortexDecoderLibrary.getLicensedSymbologies();
        for (int i = 0; i < mCompositeCodeSettingsScreen.getPreferenceCount(); i++) {
            Preference pref = mCompositeCodeSettingsScreen.getPreference(i);
            String key = mCompositeCodeSettingsScreen.getPreference(i).getKey();
            comparePreferenceToDisable(key, pref);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (((CheckBoxPreference) preference).isChecked() && !mCCPreferenceSet.contains(preference.getKey())) {
            mCCPreferenceSet.add(preference.getKey());
        } else if (!((CheckBoxPreference) preference).isChecked() && mCCPreferenceSet.contains(preference.getKey())) {
            if (mCCPreferenceSet.size() > 1) {
                mCCPreferenceSet.remove(preference.getKey());
            } else {
                ((CheckBoxPreference) preference).setChecked(true);
                Toast.makeText(this, "Atleast one setting should be enabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void comparePreferenceToDisable(String key, Preference pref) {
        switch (key) {
            case "cca":
                greyOutPreference(SymbologyType.SymbologyType_CCA, pref);
                break;
            case "ccb":
                greyOutPreference(SymbologyType.SymbologyType_CCB, pref);
                break;
            case "ccc":
                greyOutPreference(SymbologyType.SymbologyType_CCC, pref);
                break;
        }
    }

    private void greyOutPreference(SymbologyType type, Preference p) {
        if (!mLicensedSymbologies.contains(type)) {
            if (p instanceof TwoStatePreference) {
                ((TwoStatePreference) p).setChecked(false);
            }
            p.setEnabled(false);
        }
    }
}
