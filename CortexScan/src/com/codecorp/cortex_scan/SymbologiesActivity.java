package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;

import com.codecorp.cortex_scan.detailsymbologysettings.CodabarSymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Code11SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Code128SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Code39SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Code93SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.CompositeCodeSymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.EAN13SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.EAN8SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.GS1DataBarSymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.HongKong2of5SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.IATA2of5SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Interleaved2of5SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.MSIPlesseySymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Matrix2of5SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.NEC2of5SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.Straight2of5SymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.UPCASymbologySettings;
import com.codecorp.cortex_scan.detailsymbologysettings.UPCESymbologySettings;
import com.codecorp.symbology.Symbologies;
import com.codecorp.symbology.SymbologyType;

import java.util.HashSet;

@SuppressWarnings("deprecation")
public class SymbologiesActivity extends PreferenceActivity implements
        Preference.OnPreferenceClickListener {

    private Preference mEnableAll;
    private Preference mDisableAll;
    private PreferenceScreen mSymbologies;
    private TwoStatePreference mAztec;
    private TwoStatePreference mCodablockF;
    private TwoStatePreference mCode32;
    private TwoStatePreference mCode49;
    private TwoStatePreference mDataMatrix;
    private TwoStatePreference mGridMatrix;
    private TwoStatePreference mDotCode;
    private TwoStatePreference mHanXinCode;
    private TwoStatePreference mMaxiCode;
    private TwoStatePreference mMicroPDF;
    private TwoStatePreference mMicroQR;
    private TwoStatePreference mPDF417;
    private TwoStatePreference mPlessey;
    private TwoStatePreference mQRCode;
    private TwoStatePreference mTelepen;
    private TwoStatePreference mTrioptic;
    private TwoStatePreference mAustraliaPost;
    private TwoStatePreference mCanadaPost;
    private TwoStatePreference mDutchPost;
    private TwoStatePreference mJapanPost;
    private TwoStatePreference mKoreaPost;
    private TwoStatePreference mRoyalPost;
    private TwoStatePreference mUSPSIntelligentMail;
    private TwoStatePreference mUSPSPlanet;
    private TwoStatePreference mUSPSPostnet;
    private TwoStatePreference mUPU;
    private Preference mCodabar;
    private Preference mCode128;
    private Preference mCode11;
    private Preference mCode93;
    private Preference mCode39;
    private Preference mCodeEAN13;
    private Preference mCodeEAN8;
    private Preference mHK2o5;
    private Preference mIATA2o5;
    private Preference mInterleaved2o5;
    private Preference mMatrix2o5;
    private Preference mNEC2o5;
    private Preference mStraight2o5;
    private Preference mMSIPlessey;
    private Preference mUPCA;
    private Preference mUPCE;
    private Preference mGS1Databar;
    private Preference mCompositeCode;
    private MyApplication mApplication;
    private HashSet<SymbologyType> licensedSym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the XML preferences file
        addPreferencesFromResource(R.xml.symbologies);
        mApplication = (MyApplication) getApplication();
        mSymbologies = (PreferenceScreen) getPreferenceScreen().findPreference(
                "symbologies");
        mEnableAll = getPreferenceScreen().findPreference(
                "enable_all");
        mEnableAll.setOnPreferenceClickListener(this);
        mDisableAll = getPreferenceScreen().findPreference(
                "disable_all");
        mDisableAll.setOnPreferenceClickListener(this);

        mAztec = (TwoStatePreference) getPreferenceScreen().findPreference("aztec");
        mCodablockF = (TwoStatePreference) getPreferenceScreen().findPreference("codablockf");
        mCode32 = (TwoStatePreference) getPreferenceScreen().findPreference("code32");
        mDataMatrix = (TwoStatePreference) getPreferenceScreen().findPreference("data_matrix");
        mGridMatrix = (TwoStatePreference) getPreferenceScreen().findPreference("grid_matrix");
        mDotCode = (TwoStatePreference) getPreferenceScreen().findPreference("dot_code");
        mCode49 = (TwoStatePreference) getPreferenceScreen().findPreference("code49");
        mHanXinCode = (TwoStatePreference) getPreferenceScreen().findPreference("hanxin_code");
        mMaxiCode = (TwoStatePreference) getPreferenceScreen().findPreference("maxi_code");
        mMicroPDF = (TwoStatePreference) getPreferenceScreen().findPreference("micropdf417");
        mMicroQR = (TwoStatePreference) getPreferenceScreen().findPreference("microqr");
        mPDF417 = (TwoStatePreference) getPreferenceScreen().findPreference("pdf417");
        mPlessey = (TwoStatePreference) getPreferenceScreen().findPreference("plessey");
        mQRCode = (TwoStatePreference) getPreferenceScreen().findPreference("qr_code");
        mTelepen = (TwoStatePreference) getPreferenceScreen().findPreference("telepen");
        mTrioptic = (TwoStatePreference) getPreferenceScreen().findPreference("trioptic");
        mAustraliaPost = (TwoStatePreference) getPreferenceScreen().findPreference("australia_post");
        mCanadaPost = (TwoStatePreference) getPreferenceScreen().findPreference("canada_post");
        mDutchPost = (TwoStatePreference) getPreferenceScreen().findPreference("dutch_post");
        mJapanPost = (TwoStatePreference) getPreferenceScreen().findPreference("japan_post");
        mKoreaPost = (TwoStatePreference) getPreferenceScreen().findPreference("korea_post");
        mRoyalPost = (TwoStatePreference) getPreferenceScreen().findPreference("royal_mail");
        mUSPSIntelligentMail = (TwoStatePreference) getPreferenceScreen().findPreference("usps_intelligent_mail");
        mUSPSPlanet = (TwoStatePreference) getPreferenceScreen().findPreference("usps_planet");
        mUSPSPostnet = (TwoStatePreference) getPreferenceScreen().findPreference("usps_postnet");
        mUPU = (TwoStatePreference) getPreferenceScreen().findPreference("upu");

        mCodabar = getPreferenceScreen().findPreference("codabar");
        mCodabar.setOnPreferenceClickListener(this);

        mCode128 = getPreferenceScreen().findPreference("code128");
        mCode128.setOnPreferenceClickListener(this);

        mCode11 = getPreferenceScreen().findPreference("code11");
        mCode11.setOnPreferenceClickListener(this);

        mCode93 = getPreferenceScreen().findPreference("code93");
        mCode93.setOnPreferenceClickListener(this);

        mCode39 = getPreferenceScreen().findPreference("code39");
        mCode39.setOnPreferenceClickListener(this);

        mCodeEAN13 = getPreferenceScreen().findPreference("ean13");
        mCodeEAN13.setOnPreferenceClickListener(this);

        mCodeEAN8 = getPreferenceScreen().findPreference("ean8");
        mCodeEAN8.setOnPreferenceClickListener(this);

        mHK2o5 = getPreferenceScreen().findPreference("hong_kong_2_of_5");
        mHK2o5.setOnPreferenceClickListener(this);

        mIATA2o5 = getPreferenceScreen().findPreference("iata_2_of_5");
        mIATA2o5.setOnPreferenceClickListener(this);

        mInterleaved2o5 = getPreferenceScreen().findPreference("interleaved_2_of_5");
        mInterleaved2o5.setOnPreferenceClickListener(this);

        mMatrix2o5 = getPreferenceScreen().findPreference("matrix_2_of_5");
        mMatrix2o5.setOnPreferenceClickListener(this);

        mNEC2o5 = getPreferenceScreen().findPreference("nec_2_of_5");
        mNEC2o5.setOnPreferenceClickListener(this);

        mStraight2o5 = getPreferenceScreen().findPreference("straight_2_of_5");
        mStraight2o5.setOnPreferenceClickListener(this);

        mMSIPlessey = getPreferenceScreen().findPreference("msi_plessey");
        mMSIPlessey.setOnPreferenceClickListener(this);

        mUPCA = getPreferenceScreen().findPreference("upc-a");
        mUPCA.setOnPreferenceClickListener(this);

        mUPCE = getPreferenceScreen().findPreference("upc-e");
        mUPCE.setOnPreferenceClickListener(this);

        mGS1Databar = getPreferenceScreen().findPreference("gs1_databar");
        mGS1Databar.setOnPreferenceClickListener(this);

        mCompositeCode = getPreferenceScreen().findPreference("composite_code");
        mCompositeCode.setOnPreferenceClickListener(this);

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDecoderStatus();
        if (mApplication.mCortexDecoderLibrary.isLicenseActivated()) {
            disableLicensedSymbologies();
        }
    }

    private void disableLicensedSymbologies() {
        licensedSym = mApplication.mCortexDecoderLibrary.getLicensedSymbologies();
        for (int i = 0; i < mSymbologies.getPreferenceCount(); i++) {
            String key = mSymbologies.getPreference(i).getKey();
            Preference pref = mSymbologies.getPreference(i);
            comparePreferenceToDisable(key, pref);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mEnableAll) {
            // Enable all symbologies. CortexScan will detect the preference
            // change and do the actual work.
            int count = mSymbologies.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference p = mSymbologies.getPreference(i);
                if (p.isEnabled()) {
                    if (p instanceof AdvancedPreference) {
                        ((AdvancedPreference) p).setChecked(true);
                    }
                    if (p instanceof TwoStatePreference)
                        ((TwoStatePreference) p).setChecked(true);
                }
            }
            return true;
        } else if (preference == mDisableAll) {
            // Disable all symbologies. CortexScan will detect the preference
            // change and do the actual work.
            int count = mSymbologies.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference p = mSymbologies.getPreference(i);
                if (p.isEnabled()) {
                    if (p instanceof AdvancedPreference) {
                        ((AdvancedPreference) p).setChecked(false);
                    }
                    if (p instanceof TwoStatePreference)
                        ((TwoStatePreference) p).setChecked(false);
                }
            }
            return true;
        } else if (preference == mCodabar) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, CodabarSymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mCode128) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Code128SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mCode11) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Code11SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mCode93) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Code93SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mCode39) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Code39SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mCompositeCode) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, CompositeCodeSymbologySettings.class);
                startActivity(intent);
            }
        } else if (preference == mCodeEAN13) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, EAN13SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mCodeEAN8) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, EAN8SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mHK2o5) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, HongKong2of5SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mIATA2o5) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, IATA2of5SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mInterleaved2o5) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Interleaved2of5SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mMatrix2o5) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Matrix2of5SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mNEC2o5) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, NEC2of5SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mStraight2o5) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, Straight2of5SymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mMSIPlessey) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, MSIPlesseySymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mUPCA) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, UPCASymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mUPCE) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, UPCESymbologySettings.class);
                startActivity(intent);
            }
            return true;
        } else if (preference == mGS1Databar) {
            if (((AdvancedPreference) preference).isChecked()) {
                Intent intent = new Intent(this, GS1DataBarSymbologySettings.class);
                startActivity(intent);
            }
            return true;
        }
        return false;
    }

    private void greyOutPreference(SymbologyType type, Preference p) {
        if (p.getKey().equalsIgnoreCase("composite_code")) {
            if (!licensedSym.contains(SymbologyType.SymbologyType_CCA) && !licensedSym.contains(SymbologyType.SymbologyType_CCB) && !licensedSym.contains(SymbologyType.SymbologyType_CCC)) {
                if (p instanceof AdvancedPreference) {
                    ((AdvancedPreference) p).setChecked(false);
                }
                p.setEnabled(false);
            }
        } else {
            if (!licensedSym.contains(type)) {
                if (p instanceof TwoStatePreference) {
                    ((TwoStatePreference) p).setChecked(false);
                }
                if (p instanceof AdvancedPreference) {
                    ((AdvancedPreference) p).setChecked(false);
                }
                p.setEnabled(false);
            }
        }
    }

    private void comparePreferenceToDisable(String key, Preference pref) {
        switch (key) {
            case "aztec":
                greyOutPreference(SymbologyType.SymbologyType_Aztec, pref);
                break;
            case "codabar":
                greyOutPreference(SymbologyType.SymbologyType_Codabar, pref);
                break;
            case "codablockf":
                greyOutPreference(SymbologyType.SymbologyType_CodablockF, pref);
                break;
            case "code11":
                greyOutPreference(SymbologyType.SymbologyType_Code11, pref);
                break;
            case "code128":
                greyOutPreference(SymbologyType.SymbologyType_Code128, pref);
                break;
            case "code32":
                greyOutPreference(SymbologyType.SymbologyType_Code32, pref);
                break;
            case "code39":
                greyOutPreference(SymbologyType.SymbologyType_Code39, pref);
                break;
            case "code49":
                greyOutPreference(SymbologyType.SymbologyType_Code49, pref);
                break;
            case "code93":
                greyOutPreference(SymbologyType.SymbologyType_Code93, pref);
                break;
            case "composite_code":
                greyOutPreference(SymbologyType.SymbologyType_CCA, pref);
                break;
            case "data_matrix":
                greyOutPreference(SymbologyType.SymbologyType_DataMatrix, pref);
                break;
            case "ean13":
                greyOutPreference(SymbologyType.SymbologyType_EAN13, pref);
                break;
            case "ean8":
                greyOutPreference(SymbologyType.SymbologyType_EAN8, pref);
                break;
            case "grid_matrix":
                greyOutPreference(SymbologyType.SymbologyType_GridMatrix, pref);
                break;
            case "dot_code":
                greyOutPreference(SymbologyType.SymbologyType_DC, pref);
                break;
            case "gs1_databar":
                greyOutPreference(SymbologyType.SymbologyType_DB14, pref);
                break;
            case "hanxin_code":
                greyOutPreference(SymbologyType.SymbologyType_HanXin, pref);
                break;
            case "hong_kong_2_of_5":
                greyOutPreference(SymbologyType.SymbologyType_HongKong2of5, pref);
                break;
            case "iata_2_of_5":
                greyOutPreference(SymbologyType.SymbologyType_IATA2of5, pref);
                break;
            case "interleaved_2_of_5":
                greyOutPreference(SymbologyType.SymbologyType_Interleaved2of5, pref);
                break;
            case "matrix_2_of_5":
                greyOutPreference(SymbologyType.SymbologyType_Matrix2of5, pref);
                break;
            case "maxi_code":
                greyOutPreference(SymbologyType.SymbologyType_MC, pref);
                break;
            case "micropdf417":
                greyOutPreference(SymbologyType.SymbologyType_MPDF, pref);
                break;
            case "microqr":
                greyOutPreference(SymbologyType.SymbologyType_QRMicro, pref);
                break;
            case "msi_plessey":
                greyOutPreference(SymbologyType.SymbologyType_MSIPlessy, pref);
                break;
            case "nec_2_of_5":
                greyOutPreference(SymbologyType.SymbologyType_NEC2of5, pref);
                break;
            case "pdf417":
                greyOutPreference(SymbologyType.SymbologyType_PDF417, pref);
                break;
            case "plessey":
                greyOutPreference(SymbologyType.SymbologyType_Plessy, pref);
                break;
            case "qr_code":
                greyOutPreference(SymbologyType.SymbologyType_QR, pref);
                break;
            case "straight_2_of_5":
                greyOutPreference(SymbologyType.SymbologyType_Straight2of5, pref);
                break;
            case "telepen":
                greyOutPreference(SymbologyType.SymbologyType_Telepen, pref);
                break;
            case "trioptic":
                greyOutPreference(SymbologyType.SymbologyType_Trioptic, pref);
                break;
            case "upc-a":
                greyOutPreference(SymbologyType.SymbologyType_UPCA, pref);
                break;
            case "upc-e":
                greyOutPreference(SymbologyType.SymbologyType_UPCE, pref);
                break;
            case "australia_post":
                greyOutPreference(SymbologyType.SymbologyType_AustraliaPost, pref);
                break;
            case "canada_post":
                greyOutPreference(SymbologyType.SymbologyType_CanadaPost, pref);
                break;
            case "dutch_post":
                greyOutPreference(SymbologyType.SymbologyType_DutchPost, pref);
                break;
            case "japan_post":
                greyOutPreference(SymbologyType.SymbologyType_JapanMail, pref);
                break;
            case "korea_post":
                greyOutPreference(SymbologyType.SymbologyType_KoreaPost, pref);
                break;
            case "royal_mail":
                greyOutPreference(SymbologyType.SymbologyType_RoyalMail, pref);
                break;
            case "usps_intelligent_mail":
                greyOutPreference(SymbologyType.SymbologyType_USPSIntelligentMail, pref);
                break;
            case "usps_planet":
                greyOutPreference(SymbologyType.SymbologyType_USPSPlanet, pref);
                break;
            case "usps_postnet":
                greyOutPreference(SymbologyType.SymbologyType_USPSPostnet, pref);
                break;
            case "upu":
                greyOutPreference(SymbologyType.SymbologyType_UPU, pref);
                break;
        }
    }

    private void setDecoderStatus() {
        for (int i = 0; i < mSymbologies.getPreferenceCount(); i++) {
            Preference preference = mSymbologies.getPreference(i);
            if (preference == mAztec) {
                Symbologies.AztecProperties aztecProperties = new Symbologies.AztecProperties();
                mAztec.setChecked(aztecProperties.isEnabled(getApplicationContext()));
            } else if (preference == mCodablockF) {
                Symbologies.CodablockFProperties codablockFProperties = new Symbologies.CodablockFProperties();
                mCodablockF.setChecked(codablockFProperties.isEnabled(getApplicationContext()));
            } else if (preference == mCode32) {
                Symbologies.Code32Properties code32Properties = new Symbologies.Code32Properties();
                mCode32.setChecked(code32Properties.isEnabled(getApplicationContext()));
            } else if (preference == mCode49) {
                Symbologies.Code49Properties code49Properties = new Symbologies.Code49Properties();
                mCode49.setChecked(code49Properties.isEnabled(getApplicationContext()));
            } else if (preference == mDataMatrix) {
                Symbologies.DataMatrixProperties dataMatrixProperties = new Symbologies.DataMatrixProperties();
                mDataMatrix.setChecked(dataMatrixProperties.isEnabled(getApplicationContext()));
            } else if (preference == mGridMatrix) {
                Symbologies.GridMatrixProperties gridMatrixProperties = new Symbologies.GridMatrixProperties();
                mGridMatrix.setChecked(gridMatrixProperties.isEnabled(getApplicationContext()));
            } else if (preference == mDotCode) {
                Symbologies.DotCodeProperties dotCodeProperties = new Symbologies.DotCodeProperties();
                mDotCode.setChecked(dotCodeProperties.isEnabled(getApplicationContext()));
            } else if (preference == mHanXinCode) {
                Symbologies.HanXinCodeProperties hanXinCodeProperties = new Symbologies.HanXinCodeProperties();
                mHanXinCode.setChecked(hanXinCodeProperties.isEnabled(getApplicationContext()));
            } else if (preference == mMaxiCode) {
                Symbologies.MaxiCodeProperties maxiCodeProperties = new Symbologies.MaxiCodeProperties();
                mMaxiCode.setChecked(maxiCodeProperties.isEnabled(getApplicationContext()));
            } else if (preference == mMicroPDF) {
                Symbologies.MicroPDF417Properties microPDF417Properties = new Symbologies.MicroPDF417Properties();
                mMicroPDF.setChecked(microPDF417Properties.isEnabled(getApplicationContext()));
            } else if (preference == mMicroQR) {
                Symbologies.MicroQRProperties microQRProperties = new Symbologies.MicroQRProperties();
                mMicroQR.setChecked(microQRProperties.isEnabled(getApplicationContext()));
            } else if (preference == mPDF417) {
                Symbologies.PDF417Properties pdf417Properties = new Symbologies.PDF417Properties();
                mPDF417.setChecked(pdf417Properties.isEnabled(getApplicationContext()));
            } else if (preference == mPlessey) {
                Symbologies.PlesseyProperties plesseyProperties = new Symbologies.PlesseyProperties();
                mPlessey.setChecked(plesseyProperties.isEnabled(getApplicationContext()));
            } else if (preference == mQRCode) {
                Symbologies.QRProperties qrProperties = new Symbologies.QRProperties();
                mQRCode.setChecked(qrProperties.isEnabled(getApplicationContext()));
            } else if (preference == mTelepen) {
                Symbologies.TelepenProperties telepenProperties = new Symbologies.TelepenProperties();
                mTelepen.setChecked(telepenProperties.isEnabled(getApplicationContext()));
            } else if (preference == mTrioptic) {
                Symbologies.TriopticProperties triopticProperties = new Symbologies.TriopticProperties();
                mTrioptic.setChecked(triopticProperties.isEnabled(getApplicationContext()));
            } else if (preference == mAustraliaPost) {
                Symbologies.AustraliaPostProperties australiaPostProperties = new Symbologies.AustraliaPostProperties();
                mAustraliaPost.setChecked(australiaPostProperties.isEnabled(getApplicationContext()));
            } else if (preference == mCanadaPost) {
                Symbologies.CanadaPostProperties canadaPostProperties = new Symbologies.CanadaPostProperties();
                mCanadaPost.setChecked(canadaPostProperties.isEnabled(getApplicationContext()));
            } else if (preference == mDutchPost) {
                Symbologies.DutchPostProperties dutchPostProperties = new Symbologies.DutchPostProperties();
                mDutchPost.setChecked(dutchPostProperties.isEnabled(getApplicationContext()));
            } else if (preference == mJapanPost) {
                Symbologies.JapanPostProperties japanPostProperties = new Symbologies.JapanPostProperties();
                mJapanPost.setChecked(japanPostProperties.isEnabled(getApplicationContext()));
            } else if (preference == mKoreaPost) {
                Symbologies.KoreaPostProperties koreaPostProperties = new Symbologies.KoreaPostProperties();
                mKoreaPost.setChecked(koreaPostProperties.isEnabled(getApplicationContext()));
            } else if (preference == mRoyalPost) {
                Symbologies.RoyalMailProperties royalMailProperties = new Symbologies.RoyalMailProperties();
                mRoyalPost.setChecked(royalMailProperties.isEnabled(getApplicationContext()));
            } else if (preference == mUSPSPlanet) {
                Symbologies.USPSPlanetProperties uspsPlanetProperties = new Symbologies.USPSPlanetProperties();
                mUSPSPlanet.setChecked(uspsPlanetProperties.isEnabled(getApplicationContext()));
            } else if (preference == mUSPSPostnet) {
                Symbologies.USPSPostnetProperties uspsPostnetProperties = new Symbologies.USPSPostnetProperties();
                mUSPSPostnet.setChecked(uspsPostnetProperties.isEnabled(getApplicationContext()));
            } else if (preference == mUSPSIntelligentMail) {
                Symbologies.USPSIntelligentMailProperties uspsIntelligentMailProperties = new Symbologies.USPSIntelligentMailProperties();
                mUSPSIntelligentMail.setChecked(uspsIntelligentMailProperties.isEnabled(getApplicationContext()));
            } else if (preference == mUPU) {
                Symbologies.UPUProperties upuProperties = new Symbologies.UPUProperties();
                mUPU.setChecked(upuProperties.isEnabled(getApplicationContext()));
            } else if (preference == mCodabar) {
                Symbologies.CodabarProperties codabarProperties = new Symbologies.CodabarProperties();
                ((AdvancedPreference) mCodabar).setChecked(codabarProperties.isEnabled(getApplicationContext()));
            } else if (preference == mCode128) {
                Symbologies.Code128Properties code128Properties = new Symbologies.Code128Properties();
                ((AdvancedPreference) mCode128).setChecked(code128Properties.isEnabled(getApplicationContext()));
            } else if (preference == mCode11) {
                Symbologies.Code11Properties code11Properties = new Symbologies.Code11Properties();
                ((AdvancedPreference) mCode11).setChecked(code11Properties.isEnabled(getApplicationContext()));
            } else if (preference == mCode39) {
                Symbologies.Code39Properties code39Properties = new Symbologies.Code39Properties();
                ((AdvancedPreference) mCode39).setChecked(code39Properties.isEnabled(getApplicationContext()));
            } else if (preference == mCode93) {
                Symbologies.Code93Properties code93Properties = new Symbologies.Code93Properties();
                ((AdvancedPreference) mCode93).setChecked(code93Properties.isEnabled(getApplicationContext()));
            } else if (preference == mCompositeCode) {
                Symbologies.CompositeCodeProperties compositeCodeProperties = new Symbologies.CompositeCodeProperties();
                ((AdvancedPreference) mCompositeCode).setChecked(compositeCodeProperties.isEnabled(getApplicationContext()));
            } else if (preference == mCodeEAN13) {
                Symbologies.EAN13Properties ean13Properties = new Symbologies.EAN13Properties();
                ((AdvancedPreference) mCodeEAN13).setChecked(ean13Properties.isEnabled(getApplicationContext()));
            } else if (preference == mCodeEAN8) {
                Symbologies.EAN8Properties ean8Properties = new Symbologies.EAN8Properties();
                ((AdvancedPreference) mCodeEAN8).setChecked(ean8Properties.isEnabled(getApplicationContext()));
            } else if (preference == mGS1Databar) {
                Symbologies.GS1DataBar14Properties gs1DataBar14Properties = new Symbologies.GS1DataBar14Properties();
                ((AdvancedPreference) mGS1Databar).setChecked(gs1DataBar14Properties.isEnabled(getApplicationContext()));
            } else if (preference == mHK2o5) {
                Symbologies.HongKong2of5Properties hongKong2of5Properties = new Symbologies.HongKong2of5Properties();
                ((AdvancedPreference) mHK2o5).setChecked(hongKong2of5Properties.isEnabled(getApplicationContext()));
            } else if (preference == mIATA2o5) {
                Symbologies.IATA2of5Properties iata2of5Properties = new Symbologies.IATA2of5Properties();
                ((AdvancedPreference) mIATA2o5).setChecked(iata2of5Properties.isEnabled(getApplicationContext()));
            } else if (preference == mInterleaved2o5) {
                Symbologies.Interleaved2of5Properties interleaved2of5Properties = new Symbologies.Interleaved2of5Properties();
                ((AdvancedPreference) mInterleaved2o5).setChecked(interleaved2of5Properties.isEnabled(getApplicationContext()));
            } else if (preference == mMatrix2o5) {
                Symbologies.Matrix2of5Properties matrix2of5Properties = new Symbologies.Matrix2of5Properties();
                ((AdvancedPreference) mMatrix2o5).setChecked(matrix2of5Properties.isEnabled(getApplicationContext()));
            } else if (preference == mMSIPlessey) {
                Symbologies.MSIPlesseyProperties msiPlesseyProperties = new Symbologies.MSIPlesseyProperties();
                ((AdvancedPreference) mMSIPlessey).setChecked(msiPlesseyProperties.isEnabled(getApplicationContext()));
            } else if (preference == mNEC2o5) {
                Symbologies.NEC2of5Properties nec2of5Properties = new Symbologies.NEC2of5Properties();
                ((AdvancedPreference) mNEC2o5).setChecked(nec2of5Properties.isEnabled(getApplicationContext()));
            } else if (preference == mStraight2o5) {
                Symbologies.Straight2of5Properties straight2of5Properties = new Symbologies.Straight2of5Properties();
                ((AdvancedPreference) mStraight2o5).setChecked(straight2of5Properties.isEnabled(getApplicationContext()));
            } else if (preference == mUPCA) {
                Symbologies.UPCAProperties upcaProperties = new Symbologies.UPCAProperties();
                ((AdvancedPreference) mUPCA).setChecked(upcaProperties.isEnabled(getApplicationContext()));
            } else if (preference == mUPCE) {
                Symbologies.UPCEProperties upceProperties = new Symbologies.UPCEProperties();
                ((AdvancedPreference) mUPCE).setChecked(upceProperties.isEnabled(getApplicationContext()));
            }
        }
    }
}
