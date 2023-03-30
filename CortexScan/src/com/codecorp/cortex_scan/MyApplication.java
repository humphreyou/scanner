package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import com.codecorp.camera.CameraType;
import com.codecorp.decoder.CortexDecoderLibrary;
import com.codecorp.internal.Debug;
import com.codecorp.symbology.Symbologies;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import static com.codecorp.camera.Focus.*;
import static com.codecorp.camera.Resolution.*;
import static com.codecorp.internal.Debug.debug;

public class MyApplication extends Application {
    private static final String TAG = CortexScan.TAG;
    public CortexDecoderLibrary mCortexDecoderLibrary;

    public void pushPreference(SharedPreferences sharedPreferences, String key) {
        if ((key.equals("focus.Back-Facing")) ||
                (key.equals("focus.Front-Facing")) ||
                (key.equals("illumination.Back-Facing")) ||
                (key.equals("illumination.Front-Facing")) ||
                (key.equals("App Restrictions"))) {
            // Do nothing for now by checking for these types this prevents an exception that was occurring
            return;
        }
        debug(TAG, "Sending preference " + key + " to decoder");
        if (key.equals("focus")) {
            String focus = sharedPreferences.getString(key, "ERROR");
            debug(TAG, "Sending preference " + key + "=" + focus + " to decoder");
            if (focus.equals("Auto"))
                mCortexDecoderLibrary.setFocus(Focus_Auto);
            else if (focus.equals("Fixed"))
                mCortexDecoderLibrary.setFocus(Focus_Fix);
            else if (focus.equals("Far"))
                mCortexDecoderLibrary.setFocus(Focus_Far);
            else
                Log.e(TAG, "Unknown focus setting: " + focus);
        } else if (key.equals("camera_type")) {
            String cameraTypeString = sharedPreferences.getString(key, "ERROR");
            debug(TAG, "Sending preference " + key + "=" + cameraTypeString + " to decoder");
            if (cameraTypeString.equals(getString(R.string.camera_type_back)))
                mCortexDecoderLibrary.setCameraType(CameraType.BackFacing);
            else if (cameraTypeString.equals(getString(R.string.camera_type_front)))
                mCortexDecoderLibrary.setCameraType(CameraType.FrontFacing);
            else
                Log.e(TAG, "Unknown camera type: " + cameraTypeString);
        } else if (key.equals("resolution")) {
            String resolution = sharedPreferences.getString(key, "ERROR");
            debug(TAG, "Sending preference " + key + "=" + resolution + " to decoder");
            if (resolution.equals("3840 x 2160"))
                mCortexDecoderLibrary.setDecoderResolution(Resolution_3840x2160);
            else if (resolution.equals("1920 x 1080"))
                mCortexDecoderLibrary.setDecoderResolution(Resolution_1920x1080);
            else if (resolution.equals("1280 x 720"))
                mCortexDecoderLibrary.setDecoderResolution(Resolution_1280x720);
            else if (resolution.equals("640 x 480"))
                mCortexDecoderLibrary.setDecoderResolution(Resolution_640x480);
            else
                Log.e(TAG, "Unknown resolution setting: " + resolution);
        } else if (key.equals("illumination")) {
            boolean illumination = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + illumination + " to decoder");
            mCortexDecoderLibrary.setTorch(illumination);
        } else if (key.equals("low_contrast_mode")) {
            boolean mode = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + mode + " to decoder");
            mCortexDecoderLibrary.lowContrastDecodingEnabled(mode);
        } else if (key.equals("large_data_display")) {
            // Nothing to do here
        } else if (key.equals("vibrate_on_scan")) {
            boolean mode = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + mode + " to decoder");
            mCortexDecoderLibrary.enableVibrateOnScan(mode);
        } else if (key.equals("exposure_setting")) {
            boolean mode = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + mode + " to decoder");
            mCortexDecoderLibrary.enableFixedExposureMode(mode);
        } else if (key.equals("continuous_scan_mode")) {
            // Nothing to do here
        } else if (key.equals("beep_mode")) {
            boolean mode = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + mode + " to decoder");
            mCortexDecoderLibrary.enableBeepPlayer(mode);
        } else if (key.equals("encoding_character")) {
            String encodedChar = sharedPreferences.getString(key, "ERROR");
            debug(TAG, "Sending preference " + key + "=" + encodedChar + " to decoder");
            mCortexDecoderLibrary.setEncodingCharsetName(encodedChar);
        } else if (key.equals("beep_sounds")) {
            String beeptype = sharedPreferences.getString(key, "ERROR");
            debug(TAG, "Sending preference " + key + "=" + beeptype + " to decoder");
            mCortexDecoderLibrary.changeBeepPlayerSound(beeptype);
        } else if (key.equals("seekbar_value")) {
            Integer seektype = sharedPreferences.getInt(key, 0);
            debug(TAG, "Sending preference " + key + "=" + Integer.toString(seektype) + " to decoder");
            mCortexDecoderLibrary.setNumberOfBarcodesToDecode(seektype);
        } else if (key.equals("n_barcodes")) {
            Boolean nBarcode = sharedPreferences.getBoolean(key, false);
            mCortexDecoderLibrary.setExactlyNBarcodes(nBarcode);
        } else if (key.equals("picklist_mode")) {
            boolean enabled = sharedPreferences.getBoolean(key, false);
            if (enabled) {
                mCortexDecoderLibrary.setDecoderToleranceLevel(0); //picklistmode
            } else {
                mCortexDecoderLibrary.setDecoderToleranceLevel(10);
            }
        } else if (key.equals("debug_mode")) {
            boolean enabled = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + enabled + " to decoder");
            mCortexDecoderLibrary.enableScannedImageCapture(enabled);
        } else if (key.equals("decode_count")) {
            boolean enabled = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Sending preference " + key + "=" + enabled + " to decoder");
            mCortexDecoderLibrary.enableDecodeCountPerMin(enabled);
        } else if (key.equals("save_images")) {
            String option = sharedPreferences.getString("save_images", "0");
            /*
               option:
                    "0" = Not saving image
                    "1" = Capture with successful decoding
                    "2" = Capture with decoder failure
                    "3" = Capture with decode data match
                    "4" = Capture with decode data mismatch
                    "5" = Capture before decoding/delete after
             */
        } else if (key.equals("decode_data_parsing")) {
            /*
               option:
                    "0" = No Parsing
                    "1" = Driver’s License Parsing
                    "2" = String Matching/Replacing
                    "3" = GS1 Parsing
                    "4" = ISO Validation
                    "5" = UDI Validation
                    "6" = JSON DL Parsing
             */
            String option = sharedPreferences.getString("decode_data_parsing", "0");
            String configStr;
            switch (option) {
                case "0":
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_Disabled, "");
                    break;
                case "1":
                    configStr = "00000308130328130331091845584578130648320668320678320681010";
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_DLParsing, configStr);
                    break;
                case "2":
                    configStr = "00+_aI`+_aI`!,,|12^0OK:^B019\\x01@";
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_StrMatchReplace, configStr);
                    break;
                case "3":
                    configStr = "00+_aI`+_aI`|;^3^C#/0D016\\x01@";
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_GS1Parsing, configStr);
                    break;
                case "4":
                    configStr = "00+_aI`+_aI`00C\\x01@";
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_ISOParsing, configStr);
                    break;
                case "5":
                    configStr = "00+_aI`+_aI`00C\\x01@";
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_UDIParsing, configStr);
                    break;
                case "6":
                    configStr = "00000308130328130331091845584578130648320668320678320681010";
                    mCortexDecoderLibrary.setDataParsingProperty(CortexDecoderLibrary.CD_DataParsingType.CD_DataParsing_JSONDLParsing, configStr);
                    break;
            }
        } else if (key.equals("decode_data_format")) {
            boolean isDataFormatEnabled = sharedPreferences.getBoolean("decode_data_format", false);
            String configStr = "00+_aI`+_aI`<P>!2,5,<S>017\\x01@";
            if (isDataFormatEnabled) {
                mCortexDecoderLibrary.setDataFormatting(configStr);
            } else {
                mCortexDecoderLibrary.setDataFormatting(null);
            }

        } else if (key.equals("debug_level")) {
            String debugLevelString = sharedPreferences.getString("debug_level", "4");
            Debug.debugLevel = Integer.parseInt(debugLevelString);
        } else if (key.equals("autofocus_reset_count")) {
            boolean enable = sharedPreferences.getBoolean("autofocus_reset_count", false);
            mCortexDecoderLibrary.setAutoFocusResetByCount(enable);
        } else if (key.equals("autofocus_reset_interval")) {
            boolean enable = sharedPreferences.getBoolean("autofocus_reset_interval", false);
            mCortexDecoderLibrary.setAutoFocusAndInterval(enable);
        } else if (key.equals("custom_exposure_val")) {
            // Added to avoid "Error pushing preference [custom_exposure_val]"
        } else if (key.equals("custom_exposure_comp_val")) {
            // Added to avoid "Error pushing preference [custom_exposure_comp_val]"
        } else if (key.equals("cameraAPI")) {
            // Added to avoid "Error pushing preference [cameraAPI]"
        } else if (key.equals("direct_part_marking")) {
             /*option:
               "0" = No DPM
               "1" = Dot Peen (Dark on Light)
               "2" = Dot Peen (Light on Dark)
               "3" = Laser/Chem Etching
            */
            String option = sharedPreferences.getString("direct_part_marking", "0");
            switch (option) {
                case "0":
                    mCortexDecoderLibrary.setDPMProperty(CortexDecoderLibrary.CD_DPMType.CD_DPM_Disabled);
                    break;
                case "1":
                    mCortexDecoderLibrary.setDPMProperty(CortexDecoderLibrary.CD_DPMType.CD_DPM_DarkOnLight);
                    break;
                case "2":
                    mCortexDecoderLibrary.setDPMProperty(CortexDecoderLibrary.CD_DPMType.CD_DPM_LightOnDark);
                    break;
                case "3":
                    mCortexDecoderLibrary.setDPMProperty(CortexDecoderLibrary.CD_DPMType.CD_DPM_LaserChemEtch);
                    break;
                default:
                    mCortexDecoderLibrary.setDPMProperty(CortexDecoderLibrary.CD_DPMType.CD_DPM_Disabled);
                    break;
            }
        } else if (key.equals("multi_res_mode")) {
            boolean enabled = sharedPreferences.getBoolean(key, false);
            mCortexDecoderLibrary.enableMultiResolutionDecoding(enabled);
        } else {
            // It must be a symbology
            boolean enabled = sharedPreferences.getBoolean(key, false);
            debug(TAG, "Symbology " + key + " enabled? " + enabled);
            if (key.equals("aztec")) {
                boolean enabledAztec = sharedPreferences.getBoolean("aztec", true);
                Symbologies.AztecProperties p = new Symbologies.AztecProperties();
                p.setEnabled(getApplicationContext(), enabledAztec);
                p.setMirrorDecodingEnabled(getApplicationContext(), enabledAztec);
                p.setPolarity(getApplicationContext(), Symbologies.AztecPropertiesPolarity.AztecPropertiesPolarity_Either);
            } else if (key.equals("code11")) {
                Symbologies.Code11Properties p = new Symbologies.Code11Properties();
                p.setEnabled(getApplicationContext(), enabled);
                String option = sharedPreferences.getString("code11_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code11PropertiesChecksum.Code11PropertiesChecksum_Disabled);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code11PropertiesChecksum.Code11PropertiesChecksum_Enabled1Digit);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code11PropertiesChecksum.Code11PropertiesChecksum_Enabled1Digit);
                        p.setStripChecksumEnabled(getApplicationContext(), true);
                        break;
                    case "3":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code11PropertiesChecksum.Code11PropertiesChecksum_Enabled2Digit);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "4":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code11PropertiesChecksum.Code11PropertiesChecksum_Enabled2Digit);
                        p.setStripChecksumEnabled(getApplicationContext(), true);
                        break;
                }
            } else if (key.equals("code128")) {
                boolean enabledC128 = sharedPreferences.getBoolean("code128", true);
                Symbologies.Code128Properties p = new Symbologies.Code128Properties();
                p.setEnabled(getApplicationContext(), enabledC128);
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("code128_min_chars", 1));
            } else if (key.equals("code32")) {
                Symbologies.Code32Properties p = new Symbologies.Code32Properties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("code39")) {
                boolean enabledC39 = sharedPreferences.getBoolean("code39", true);
                Symbologies.Code39Properties p = new Symbologies.Code39Properties();
                p.setEnabled(getApplicationContext(), enabledC39);
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("code39_min_chars", 1));
                p.setAsciiModeEnabled(getApplicationContext(), sharedPreferences.getBoolean("code39_ascii_support", false));
                String option = sharedPreferences.getString("code39_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code39PropertiesChecksum.Code39PropertiesChecksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code39PropertiesChecksum.Code39PropertiesChecksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Code39PropertiesChecksum.Code39PropertiesChecksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("code49")) {
                Symbologies.Code49Properties p = new Symbologies.Code49Properties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("code93")) {
                boolean enabledC93 = sharedPreferences.getBoolean("code93", true);
                Symbologies.Code93Properties p = new Symbologies.Code93Properties();
                p.setEnabled(getApplicationContext(), enabledC93);
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("code93_min_chars", 1));
            } else if (key.equals("codabar")) {
                boolean enabledCBar = sharedPreferences.getBoolean("codabar", true);
                Symbologies.CodabarProperties p = new Symbologies.CodabarProperties();
                p.setEnabled(getApplicationContext(), enabledCBar);
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("codabar_min_chars", 4));
                String option = sharedPreferences.getString("codabar_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.CodabarPropertiesChecksum.CodabarPropertiesChecksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.CodabarPropertiesChecksum.CodabarPropertiesChecksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.CodabarPropertiesChecksum.CodabarPropertiesChecksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("codablockf")) {
                Symbologies.CodablockFProperties p = new Symbologies.CodablockFProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("composite_code")) {
                Symbologies.CompositeCodeProperties p = new Symbologies.CompositeCodeProperties();
                p.setEnabled(getApplicationContext(), enabled);
                if (enabled) {
                    p.setCcaDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("cca", enabled));
                    p.setCcbDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("ccb", enabled));
                    p.setCccDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("ccc", enabled));
                }
            } else if (key.equals("data_matrix")) {
                boolean enabledDM = sharedPreferences.getBoolean("data_matrix", true);
                Symbologies.DataMatrixProperties p = new Symbologies.DataMatrixProperties();
                p.setEnabled(getApplicationContext(), enabledDM);
                p.setExtendedRectEnabled(getApplicationContext(), enabledDM);
                p.setMirrorDecodingEnabled(getApplicationContext(), enabledDM);
                p.setPolarity(getApplicationContext(), Symbologies.DataMatrixPropertiesPolarity.DataMatrixPropertiesPolarity_Either);
            } else if (key.equals("ean13")) {
                boolean enabledEAN13 = sharedPreferences.getBoolean("ean13", true);
                Symbologies.EAN13Properties p = new Symbologies.EAN13Properties();
                p.setEnabled(getApplicationContext(), enabledEAN13);
                p.setSupplemental2DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("ean13_2digit_supp", false));
                p.setSupplemental5DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("ean13_5digit_supp", false));
                p.setAddSpaceEnabled(getApplicationContext(), sharedPreferences.getBoolean("ean13_add_space", false));
                p.setRequireSupplemental(getApplicationContext(), sharedPreferences.getBoolean("ean13_require_supp", false));
            } else if (key.equals("ean8")) {
                boolean enabledEAN8 = sharedPreferences.getBoolean("ean8", true);
                Symbologies.EAN8Properties p = new Symbologies.EAN8Properties();
                p.setEnabled(getApplicationContext(), enabledEAN8);
                p.setSupplemental2DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("ean8_2digit_supp", false));
                p.setSupplemental5DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("ean8_5digit_supp", false));
                p.setAddSpaceEnabled(getApplicationContext(), sharedPreferences.getBoolean("ean8_add_space", false));
                p.setRequireSupplemental(getApplicationContext(), sharedPreferences.getBoolean("ean8_require_supp", false));
            } else if (key.equals("grid_matrix")) {
                Symbologies.GridMatrixProperties p = new Symbologies.GridMatrixProperties();
                p.setEnabled(getApplicationContext(), enabled);
                p.setMirrorDecodingEnabled(getApplicationContext(), enabled);
                p.setPolarity(getApplicationContext(), Symbologies.GridMatrixPropertiesPolarity.GridMatrixPropertiesPolarity_Either);
            } else if (key.equals("dot_code")) {
                Symbologies.DotCodeProperties p = new Symbologies.DotCodeProperties();
                p.setEnabled(getApplicationContext(), enabled);
                p.setMirrorDecodingEnabled(getApplicationContext(), enabled);
                p.setPolarity(getApplicationContext(), Symbologies.DotCodePropertiesPolarity.DotCodePropertiesPolarity_Either);
            } else if (key.equals("gs1_databar")) {
                boolean enabledGS1 = sharedPreferences.getBoolean("gs1_databar", true);
                Symbologies.GS1DataBar14Properties p = new Symbologies.GS1DataBar14Properties();
                p.setEnabled(getApplicationContext(), enabledGS1);
                if (enabled) {
                    p.setOmniTruncatedDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("gs1databar_omni", true));
                    p.setStackedDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("gs1databar_stacked_omni", true));
                    p.setLimitedDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("gs1databar_limited", true));
                    p.setExpandedDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("gs1databar_expanded", true));
                    p.setExpandedStackDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("gs1databar_expanded_stacked", true));
                }
            } else if (key.equals("hanxin_code")) {
                Symbologies.HanXinCodeProperties p = new Symbologies.HanXinCodeProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("hong_kong_2_of_5")) {
                Symbologies.HongKong2of5Properties p = new Symbologies.HongKong2of5Properties();
                p.setEnabled(getApplicationContext(), enabled);
                String option = sharedPreferences.getString("hk2o5_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("iata_2_of_5")) {
                Symbologies.IATA2of5Properties p = new Symbologies.IATA2of5Properties();
                p.setEnabled(getApplicationContext(), enabled);
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("iata2o5_min_chars", 1));
                String option = sharedPreferences.getString("iata2o5_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("interleaved_2_of_5")) {
                boolean enabledI2of5 = sharedPreferences.getBoolean("interleaved_2_of_5", true);
                Symbologies.Interleaved2of5Properties p = new Symbologies.Interleaved2of5Properties();
                p.setEnabled(getApplicationContext(), enabledI2of5);
                p.setRejectPartialDecode(getApplicationContext(), sharedPreferences.getBoolean("interleaved2o5_reject_partialDecode", false));
                p.setAllowShortQuietZone(getApplicationContext(), sharedPreferences.getBoolean("interleaved2o5_allow_shortQuietZone", false));
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("interleaved2o5_min_chars", 8));
                String option = sharedPreferences.getString("interleaved2o5_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Interleaved2of5PropertiesChecksum.Interleaved2of5PropertiesChecksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Interleaved2of5PropertiesChecksum.Interleaved2of5PropertiesChecksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Interleaved2of5PropertiesChecksum.Interleaved2of5PropertiesChecksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("maxi_code")) {
                Symbologies.MaxiCodeProperties p = new Symbologies.MaxiCodeProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("matrix_2_of_5")) {
                Symbologies.Matrix2of5Properties p = new Symbologies.Matrix2of5Properties();
                p.setEnabled(getApplicationContext(), enabled);
                String option = sharedPreferences.getString("matrix2o5_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("micropdf417")) {
                Symbologies.MicroPDF417Properties p = new Symbologies.MicroPDF417Properties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("microqr")) {
                boolean enabledMicroQR = sharedPreferences.getBoolean("microqr", true);
                Symbologies.MicroQRProperties p = new Symbologies.MicroQRProperties();
                p.setEnabled(getApplicationContext(), enabledMicroQR);
            } else if (key.equals("msi_plessey")) {
                Symbologies.MSIPlesseyProperties p = new Symbologies.MSIPlesseyProperties();
                p.setEnabled(getApplicationContext(), enabled);
                p.setMinChars(getApplicationContext(), sharedPreferences.getInt("msiplessey_min_chars", 1));
                String option = sharedPreferences.getString("msiplessey_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_Disabled);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_EnabledMod10);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_EnabledMod10);
                        p.setStripChecksumEnabled(getApplicationContext(), true);
                        break;
                    case "3":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_EnabledMod10_10);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "4":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_EnabledMod10_10);
                        p.setStripChecksumEnabled(getApplicationContext(), true);
                        break;
                    case "5":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_EnabledMod11_10);
                        p.setStripChecksumEnabled(getApplicationContext(), false);
                        break;
                    case "6":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.MSIPlesseyPropertiesChecksum.MSIPlesseyPropertiesChecksum_EnabledMod11_10);
                        p.setStripChecksumEnabled(getApplicationContext(), true);
                        break;
                }
            } else if (key.equals("nec_2_of_5")) {
                Symbologies.NEC2of5Properties p = new Symbologies.NEC2of5Properties();
                p.setEnabled(getApplicationContext(), enabled);
                String option = sharedPreferences.getString("nec2o5_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("pdf417")) {
                boolean enabledPDF417 = sharedPreferences.getBoolean("pdf417", true);
                Symbologies.PDF417Properties p = new Symbologies.PDF417Properties();
                p.setEnabled(getApplicationContext(), enabledPDF417);
            } else if (key.equals("plessey")) {
                Symbologies.PlesseyProperties p = new Symbologies.PlesseyProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("qr_code")) {
                boolean enabledQR = sharedPreferences.getBoolean("qr_code", true);
                Symbologies.QRProperties p = new Symbologies.QRProperties();
                p.setEnabled(getApplicationContext(), enabledQR);
                p.setModel1DecodingEnabled(getApplicationContext(), enabledQR);
                p.setMirrorDecodingEnabled(getApplicationContext(), enabledQR);
                p.setPolarity(getApplicationContext(), Symbologies.QRPropertiesPolarity.QRPropertiesPolarity_Either);
            } else if (key.equals("straight_2_of_5")) {
                Symbologies.Straight2of5Properties p = new Symbologies.Straight2of5Properties();
                p.setEnabled(getApplicationContext(), enabled);
                String option = sharedPreferences.getString("straight2o5_checksum", "0");
                switch (option) {
                    case "0":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Disabled);
                        break;
                    case "1":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_Enabled);
                        break;
                    case "2":
                        p.setChecksumProperties(getApplicationContext(), Symbologies.Symbology2of5PropertiesChecksum.Checksum_EnabledStripCheckCharacter);
                        break;
                }
            } else if (key.equals("telepen")) {
                Symbologies.TelepenProperties p = new Symbologies.TelepenProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("trioptic")) {
                Symbologies.TriopticProperties p = new Symbologies.TriopticProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("upc-a")) {
                boolean enabledUPCA = sharedPreferences.getBoolean("upc-a", true);
                Symbologies.UPCAProperties p = new Symbologies.UPCAProperties();
                p.setEnabled(getApplicationContext(), enabledUPCA);
                p.setSupplemental2DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("upca_2digit_supp", false));
                p.setSupplemental5DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("upca_5digit_supp", false));
                p.setAddSpaceEnabled(getApplicationContext(), sharedPreferences.getBoolean("upca_add_space", false));
                p.setRequireSupplemental(getApplicationContext(), sharedPreferences.getBoolean("upca_require_supp", false));
            } else if (key.equals("upc-e")) {
                boolean enabledUPCE = sharedPreferences.getBoolean("upc-e", true);
                Symbologies.UPCEProperties p = new Symbologies.UPCEProperties();
                p.setEnabled(getApplicationContext(), enabledUPCE);
                p.setSupplemental2DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("upce_2digit_supp", false));
                p.setSupplemental5DigitDecodingEnabled(getApplicationContext(), sharedPreferences.getBoolean("upce_5digit_supp", false));
                p.setAddSpaceEnabled(getApplicationContext(), sharedPreferences.getBoolean("upce_add_space", false));
                p.setRequireSupplemental(getApplicationContext(), sharedPreferences.getBoolean("upce_require_supp", false));
                p.setExpansionEnabled(getApplicationContext(), sharedPreferences.getBoolean("upce_enable_expansion", false));
            } else if (key.equals("australia_post")) {
                Symbologies.AustraliaPostProperties p = new Symbologies.AustraliaPostProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("canada_post")) {
                Symbologies.CanadaPostProperties p = new Symbologies.CanadaPostProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("dutch_post")) {
                Symbologies.DutchPostProperties p = new Symbologies.DutchPostProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("japan_post")) {
                Symbologies.JapanPostProperties p = new Symbologies.JapanPostProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("korea_post")) {
                Symbologies.KoreaPostProperties p = new Symbologies.KoreaPostProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("royal_mail")) {
                Symbologies.RoyalMailProperties p = new Symbologies.RoyalMailProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("usps_intelligent_mail")) {
                Symbologies.USPSIntelligentMailProperties p = new Symbologies.USPSIntelligentMailProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("usps_planet")) {
                Symbologies.USPSPlanetProperties p = new Symbologies.USPSPlanetProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("usps_postnet")) {
                Symbologies.USPSPostnetProperties p = new Symbologies.USPSPostnetProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else if (key.equals("upu")) {
                Symbologies.UPUProperties p = new Symbologies.UPUProperties();
                p.setEnabled(getApplicationContext(), enabled);
            } else {
                Log.e(TAG, "Unknown preference key" + key);
            }
        }
    }
}
