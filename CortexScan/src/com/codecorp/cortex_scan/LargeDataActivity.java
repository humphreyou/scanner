package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LargeDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.large_data_view);
        LinearLayout mRootLayout = findViewById(R.id.largeDataLayout);
        ImageView mDoneButton = findViewById(R.id.done_button);
        Intent intent = getIntent();
        boolean bool = intent.getBooleanExtra("numberOfBarcodes", false);
        if (!bool) {

            TextView symbology = new TextView(this);
            TextView barcode = new TextView(this);


            symbology.setText(String.format("Symbology: %s", intent.getStringExtra("symbology")));
            symbology.setTextColor(Color.BLACK);
            symbology.setPadding(10, 10, 10, 10);
            symbology.setBackgroundColor(Color.LTGRAY);
            symbology.setTextAppearance(this, android.R.style.TextAppearance_Medium);

            barcode.setText(String.format("Barcode: %s", intent.getStringExtra("barcode")));
            barcode.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            barcode.setTextColor(Color.BLACK);
            barcode.setPadding(10, 10, 10, 10);
            barcode.setSingleLine(false);

            mRootLayout.addView(symbology);
            mRootLayout.addView(barcode);
        } else {
            String[] bArr = intent.getStringArrayExtra("barcodeArr");
            String[] sArr = intent.getStringArrayExtra("symbologyArr");
            for (int i = 0; i < bArr.length; i++) {
                TextView symbology = new TextView(this);
                TextView spacing = new TextView(this);

                symbology.setText(String.format(Locale.getDefault()," Symbology: %s\n Barcode: %s\n Length: %d", sArr[i], bArr[i], bArr[i].length()));
                symbology.setPadding(10, 10, 10, 10);
                symbology.setBackgroundResource(R.drawable.large_data_gradient);
                symbology.setTextColor(Color.WHITE);
                symbology.setTextSize(18);
                mRootLayout.addView(symbology);

                mRootLayout.addView(spacing);
            }
        }
        mDoneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }
}
