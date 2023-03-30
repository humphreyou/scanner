package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codecorp.decoder.CortexDecoderLibrary;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        WebView wv = findViewById(R.id.webview);
        TextView appVersion = findViewById(R.id.app_version);
        PackageManager manager = getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            appVersion.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            appVersion.setText(R.string.info_error);
        }
        TextView libraryVersion = findViewById(R.id.library_version);
        libraryVersion.setText(CortexDecoderLibrary.sharedObject(this, "").libraryVersion());
        TextView sdkVersion = findViewById(R.id.sdk_version);
        sdkVersion.setText(CortexDecoderLibrary.sharedObject(this, "").getSdkVersion());
        TextView decoderVersion = findViewById(R.id.decoder_version);
        decoderVersion.setText(String.format("%s %s", CortexDecoderLibrary.sharedObject(this, "").decoderVersion(), CortexDecoderLibrary.sharedObject(this, "").decoderVersionLevel()));
        wv.loadUrl("file:///android_asset/about.html");


        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}
