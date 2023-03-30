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
public class GS1DataBarSymbologySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    static final String TAG = GS1DataBarSymbologySettings.class.getSimpleName();
    private HashSet mLicensedSymbologies;
    private MyApplication mApplication;
    private PreferenceScreen mGS1DatabarSettingsScreen;
    private Set<String> mGS1PreferenceSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gs1databar_symbology_settings);
        mApplication = (MyApplication) getApplication();
        mGS1DatabarSettingsScreen = (PreferenceScreen) getPreferenceScreen().findPreference("gs1databar_settings");

        Symbologies.GS1DataBar14Properties gs1DataBar14Properties = new Symbologies.GS1DataBar14Properties();

        CheckBoxPreference mGS1DatabarOmni = (CheckBoxPreference) getPreferenceScreen().findPreference("gs1databar_omni");
        mGS1DatabarOmni.setChecked(gs1DataBar14Properties.isOmniTruncatedDecodingEnabled(getApplicationContext()));

        CheckBoxPreference mGS1DatabarStackedOmni = (CheckBoxPreference) getPreferenceScreen().findPreference("gs1databar_stacked_omni");
        mGS1DatabarStackedOmni.setChecked(gs1DataBar14Properties.isStackedDecodingEnabled(getApplicationContext()));

        CheckBoxPreference mGS1DatabarLimited = (CheckBoxPreference) getPreferenceScreen().findPreference("gs1databar_limited");
        mGS1DatabarLimited.setChecked(gs1DataBar14Properties.isLimitedDecodingEnabled(getApplicationContext()));

        CheckBoxPreference mGS1DatabarExpanded = (CheckBoxPreference) getPreferenceScreen().findPreference("gs1databar_expanded");
        mGS1DatabarExpanded.setChecked(gs1DataBar14Properties.isExpandedDecodingEnabled(getApplicationContext()));

        CheckBoxPreference mGS1DatabarExpandedStacked = (CheckBoxPreference) getPreferenceScreen().findPreference("gs1databar_expanded_stacked");
        mGS1DatabarExpandedStacked.setChecked(gs1DataBar14Properties.isExpandedStackDecodingEnabled(getApplicationContext()));

        if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            gs1DataBar14Properties.setEnabled(getApplicationContext(), true);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void loadCheckedPreferencesToSet() {
        for (int i = 0; i < mGS1DatabarSettingsScreen.getPreferenceCount(); i++) {
            if (((CheckBoxPreference) mGS1DatabarSettingsScreen.getPreference(i)).isChecked()) {
                mGS1PreferenceSet.add(mGS1DatabarSettingsScreen.getPreference(i).getKey());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initializeClickListener() {
        for (int i = 0; i < mGS1DatabarSettingsScreen.getPreferenceCount(); i++) {
            mGS1DatabarSettingsScreen.getPreference(i).setOnPreferenceClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initializeClickListener();
        loadCheckedPreferencesToSet();
        if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            disableUnLicensedGS1Properties();
        }
    }

    private void disableUnLicensedGS1Properties() {
        mLicensedSymbologies = mApplication.mCortexDecoderLibrary.getLicensedSymbologies();
        for (int i = 0; i < mGS1DatabarSettingsScreen.getPreferenceCount(); i++) {
            Preference pref = mGS1DatabarSettingsScreen.getPreference(i);
            String key = mGS1DatabarSettingsScreen.getPreference(i).getKey();
            comparePreferenceToDisable(key, pref);
        }
    }

    private void comparePreferenceToDisable(String key, Preference pref) {
        switch (key) {
            case "gs1databar_omni":
                greyOutPreference(SymbologyType.SymbologyType_DB14, pref);
                break;
            case "gs1databar_stacked_omni":
                greyOutPreference(SymbologyType.SymbologyType_DataBarStacked, pref);
                break;
            case "gs1databar_limited":
                greyOutPreference(SymbologyType.SymbologyType_DataBarLimited, pref);
                break;
            case "gs1databar_expanded":
                greyOutPreference(SymbologyType.SymbologyType_DataBarExpanded, pref);
                break;
            case "gs1databar_expanded_stacked":
                greyOutPreference(SymbologyType.SymbologyType_DataBarExpandedStacked, pref);
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (((CheckBoxPreference) preference).isChecked() && !mGS1PreferenceSet.contains(preference.getKey())) {
            mGS1PreferenceSet.add(preference.getKey());
        } else if (!((CheckBoxPreference) preference).isChecked() && mGS1PreferenceSet.contains(preference.getKey())) {
            if (mGS1PreferenceSet.size() > 1) {
                mGS1PreferenceSet.remove(preference.getKey());
            } else {
                ((CheckBoxPreference) preference).setChecked(true);
                Toast.makeText(this, "Atleast one setting should be enabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}
