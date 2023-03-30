package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class StartUpScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_screen);

        ImageView scanNowButton = findViewById(R.id.getstartedbtn);
        scanNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        //Tablets more than likely are going to have a screen dp >= 600
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void showImageDialog() {
        final Dialog dialog = new Dialog(StartUpScreen.this);
        dialog.setContentView(R.layout.mainscreen_popup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        (dialog.findViewById(R.id.popupscreen)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(StartUpScreen.this, CortexScan.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }
}
