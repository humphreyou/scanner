package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.widget.Toast;

import com.codecorp.decoder.CortexDecoderLibrary;

import java.util.HashSet;

@SuppressWarnings("deprecation")
public class DecodeDataParsingFormatting extends PreferenceActivity implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mDecodeDataParsing;
    private MyApplication mApplication;
    HashSet<CortexDecoderLibrary.CD_PerformanceType> licensedPerfFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.decode_data_format_preference);
        mApplication = (MyApplication) getApplication();
        mDecodeDataParsing = (ListPreference) getPreferenceScreen().findPreference("decode_data_parsing");
        CortexDecoderLibrary.CD_DataParsingType type = mApplication.mCortexDecoderLibrary.getCurrentDataParsingType();
        mDecodeDataParsing.setSummary(getDataParsingEntryForSummary(type));
        mDecodeDataParsing.setValue(getDataParsingMEntryValue(type));

        TwoStatePreference mDecodeDataFormatting = (TwoStatePreference) getPreferenceScreen().findPreference("decode_data_format");
        mDecodeDataFormatting.setChecked(mApplication.mCortexDecoderLibrary.isDataFormattingEnabled());
        
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            licensedPerfFeatures = mApplication.mCortexDecoderLibrary.getLicensedPerformanceFeatures();
        }
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private String getDataParsingMEntryValue(CortexDecoderLibrary.CD_DataParsingType dataParsingType) {
        switch (dataParsingType) {
            case CD_DataParsing_Disabled:
                return "0";
            case CD_DataParsing_DLParsing:
                return "1";
            case CD_DataParsing_StrMatchReplace:
                return "2";
            case CD_DataParsing_GS1Parsing:
                return "3";
            case CD_DataParsing_ISOParsing:
                return "4";
            case CD_DataParsing_UDIParsing:
                return "5";
            case CD_DataParsing_JSONDLParsing:
                return "6";
            default:
                return "0";
        }
    }

    private CharSequence getDataParsingEntryForSummary(CortexDecoderLibrary.CD_DataParsingType dataParsingType) {
        switch (dataParsingType) {
            case CD_DataParsing_Disabled:
                return mDecodeDataParsing.getEntries()[0];
            case CD_DataParsing_DLParsing:
                return mDecodeDataParsing.getEntries()[1];
            case CD_DataParsing_StrMatchReplace:
                return mDecodeDataParsing.getEntries()[2];
            case CD_DataParsing_GS1Parsing:
                return mDecodeDataParsing.getEntries()[3];
            case CD_DataParsing_ISOParsing:
                return mDecodeDataParsing.getEntries()[4];
            case CD_DataParsing_UDIParsing:
                return mDecodeDataParsing.getEntries()[5];
            case CD_DataParsing_JSONDLParsing:
                return mDecodeDataParsing.getEntries()[6];
            default:
                return mDecodeDataParsing.getEntries()[0];
        }
    }

    /*
        Data parsing options:
           value = key
             "0" = No Parsing
             "1" = Driver’s License Parsing
             "2" = String Matching/Replacing
             "3" = GS1 Parsing
             "4" = ISO Validation
             "5" = UDI Validation
             "6" = JSON DL Parsing
    */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mDecodeDataParsing.setSummary(mDecodeDataParsing.getEntry());
        if (key.equalsIgnoreCase(mDecodeDataParsing.getKey())) {
            if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
                switch (mDecodeDataParsing.getValue()) {
                    case "1":
                        displayUnLicensedMessage(CortexDecoderLibrary.CD_PerformanceType.CD_PerformanceType_PARSE_DL);
                        break;
                    case "3":
                        displayUnLicensedMessage(CortexDecoderLibrary.CD_PerformanceType.CD_PerformanceType_PARSE_GS1);
                        break;
                    case "5":
                        displayUnLicensedMessage(CortexDecoderLibrary.CD_PerformanceType.CD_PerformanceType_PARSE_UDI);
                        break;
                    case "6":
                        displayUnLicensedMessage(CortexDecoderLibrary.CD_PerformanceType.CD_PerformanceType_PARSE_DL);
                        break;
                }
            }
        }
    }

    private void displayUnLicensedMessage(CortexDecoderLibrary.CD_PerformanceType performanceType) {
        if (!licensedPerfFeatures.contains(performanceType)) {
            mDecodeDataParsing.setValue(mDecodeDataParsing.getEntryValues()[0].toString());
            mDecodeDataParsing.setSummary(mDecodeDataParsing.getEntry());
            Toast.makeText(this, "This feature is not licensed", Toast.LENGTH_SHORT).show();
        }
    }
}
