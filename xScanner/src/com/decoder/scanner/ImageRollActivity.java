package com.decoder.scanner;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.inputmethodservice.InputMethodService;

import androidx.appcompat.app.AppCompatActivity;

public class ImageRollActivity extends AppCompatActivity {

    ImageView barcodeView;
    ArrayAdapter<ImageView> arrayAdapter;
    GridView gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_grid);
        gridLayout = (GridView) findViewById(R.id.gridview);
        arrayAdapter = new ArrayAdapter<ImageView>(this, android.R.layout.simple_list_item_1);
        gridLayout.setAdapter(new ImageAdapter(this));


        gridLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                intent.putExtra("imageData", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            int x = 1;
        }
    }
}


