package com.decoder.scanner;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.codecorp.decoder.CortexDecoderLibrary;
import com.codecorp.symbology.Symbologies;

import static com.codecorp.internal.Debug.debug;


public class MyApplication extends Application {
    private static final String TAG = "MainActivity";
    public CortexDecoderLibrary mCortexDecoderLibrary;

    public void pushPreference(SharedPreferences sharedPreferences,
                               String key) {


        if (key.equals("seekbar_value")) {
            Integer seektype = sharedPreferences.getInt(key, 0);
            debug(TAG, "Sending preference " + key + "=" + Integer.toString(seektype) + " to decoder");
            mCortexDecoderLibrary.setNumberOfBarcodesToDecode(seektype);
        } else if (key.equals("multi_res_mode")) {
            boolean enabled = sharedPreferences.getBoolean(key, false);
            mCortexDecoderLibrary.enableMultiResolutionDecoding(enabled);
        } else {
            // It must be a symbology
            //Below code is just for reference.
            boolean enabled = sharedPreferences.getBoolean(key, false);
            if (key.equals("aztec")) {
                Symbologies.AztecProperties p = new Symbologies.AztecProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("code11")) {
                Symbologies.Code11Properties p = new Symbologies.Code11Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("code128")) {
                Symbologies.Code128Properties p = new Symbologies.Code128Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("code32")) {
                Symbologies.Code32Properties p = new Symbologies.Code32Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("code39")) {
                Symbologies.Code39Properties p = new Symbologies.Code39Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("code49")) {
                Symbologies.Code49Properties p = new Symbologies.Code49Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("code93")) {
                Symbologies.Code93Properties p = new Symbologies.Code93Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("codabar")) {
                Symbologies.CodabarProperties p = new Symbologies.CodabarProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("data_matrix")) {
                Symbologies.DataMatrixProperties p = new Symbologies.DataMatrixProperties();
                p.enabled = enabled;
                p.extendedRectEnabled = enabled;
                p.saveProperties();
            } else if (key.equals("ean13")) {
                Symbologies.EAN13Properties p = new Symbologies.EAN13Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("ean8")) {
                Symbologies.EAN8Properties p = new Symbologies.EAN8Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("grid_matrix")) {
                Symbologies.GridMatrixProperties p = new Symbologies.GridMatrixProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("gs1_databar")) {
                Symbologies.GS1DataBar14Properties p = new Symbologies.GS1DataBar14Properties();
                p.enabled = enabled;
                p.ccaDecodingEnabled = enabled;
                p.ccbDecodingEnabled = enabled;
                p.cccDecodingEnabled = enabled;
                p.expandedDecodingEnabled = enabled;
                p.expandedStackDecodingEnabled = enabled;
                p.limitedDecodingEnabled = enabled;
                p.stackedDecodingEnabled = enabled;
                p.saveProperties();
            } else if (key.equals("hanxin_code")) {
                Symbologies.HanXinCodeProperties p = new Symbologies.HanXinCodeProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("hong_kong_2_of_5")) {
                Symbologies.HongKong2of5Properties p = new Symbologies.HongKong2of5Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("iata_2_of_5")) {
                Symbologies.IATA2of5Properties p = new Symbologies.IATA2of5Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("interleaved_2_of_5")) {
                Symbologies.Interleaved2of5Properties p = new Symbologies.Interleaved2of5Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("maxi_code")) {
                Symbologies.MaxiCodeProperties p = new Symbologies.MaxiCodeProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("matrix_2_of_5")) {
                Symbologies.Matrix2of5Properties p = new Symbologies.Matrix2of5Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("micropdf417")) {
                Symbologies.MicroPDF417Properties p = new Symbologies.MicroPDF417Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("microqr")) {
                Symbologies.MicroQRProperties p = new Symbologies.MicroQRProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("msi_plessey")) {
                Symbologies.MSIPlesseyProperties p = new Symbologies.MSIPlesseyProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("nec_2_of_5")) {
                Symbologies.NEC2of5Properties p = new Symbologies.NEC2of5Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("pdf417")) {
                Symbologies.PDF417Properties p = new Symbologies.PDF417Properties();
                p.enabled = enabled;
                if (sharedPreferences.getBoolean("dl_parse", false)) {
                    // p.dlParsing = CortexDecoderLibrary.Symbologies.DLProperties.DLProperties_Enabled;
                }
                p.saveProperties();
            } else if (key.equals("plessey")) {
                Symbologies.PlesseyProperties p = new Symbologies.PlesseyProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("qr_code")) {
                Symbologies.QRProperties p = new Symbologies.QRProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("straight_2_of_5")) {
                Symbologies.Straight2of5Properties p = new Symbologies.Straight2of5Properties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("telepen")) {
                Symbologies.TelepenProperties p = new Symbologies.TelepenProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("trioptic")) {
                Symbologies.TriopticProperties p = new Symbologies.TriopticProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("upc-a")) {
                Symbologies.UPCAProperties p = new Symbologies.UPCAProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("upc-e")) {
                Symbologies.UPCEProperties p = new Symbologies.UPCEProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("australia_post")) {
                Symbologies.AustraliaPostProperties p = new Symbologies.AustraliaPostProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("canada_post")) {
                Symbologies.CanadaPostProperties p = new Symbologies.CanadaPostProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("dutch_post")) {
                Symbologies.DutchPostProperties p = new Symbologies.DutchPostProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("dutch_post")) {
                Symbologies.DutchPostProperties p = new Symbologies.DutchPostProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("japan_post")) {
                Symbologies.JapanPostProperties p = new Symbologies.JapanPostProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("korea_post")) {
                Symbologies.KoreaPostProperties p = new Symbologies.KoreaPostProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("royal_mail")) {
                Symbologies.RoyalMailProperties p = new Symbologies.RoyalMailProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("usps_intelligent_mail")) {
                Symbologies.USPSIntelligentMailProperties p = new Symbologies.USPSIntelligentMailProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("usps_planet")) {
                Symbologies.USPSPlanetProperties p = new Symbologies.USPSPlanetProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("usps_postnet")) {
                Symbologies.USPSPostnetProperties p = new Symbologies.USPSPostnetProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else if (key.equals("upu")) {
                Symbologies.UPUProperties p = new Symbologies.UPUProperties();
                p.enabled = enabled;
                p.saveProperties();
            } else {
                Log.e(TAG, "Unknown preference key" + key);
            }
        }
    }
}

