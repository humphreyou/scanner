package com.decoder.sdk.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.content.Context;
import androidx.annotation.NonNull;

import com.decoder.sdk.scanner.ScannerManager;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.List;
import android.graphics.Point;
import android.graphics.Rect;

import java.nio.ByteBuffer;
import com.decoder.sdk.LiveBarcodeScanningActivity;

public class DecodeSdkLibrary {
    Context mContext    = null;
    String  mParaString = null;
    private static DecodeSdkLibrary mLibInstance = null;

    public int getVersion() {
         return 101;
    }

    public  DecodeSdkLibrary(Context context, String paramString) {
        mContext = context;
        mParaString = paramString;
    }

    public void startScanning(Context context) {
        context.startActivity(new Intent(context, LiveBarcodeScanningActivity.class));
    }

    public static DecodeSdkLibrary sharedObject(Context context, String paramString) {
        if (mLibInstance == null)
            mLibInstance = new DecodeSdkLibrary(context, paramString);
        return mLibInstance;
    }

    public void doDecodeBitMap(Bitmap bitmap, int w, int h){
        Log.e("UI", "doDecodeBitMap w:"+ w +" h:"+h);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        scanBarcodes(image);
    }
    public void doDecode(ByteBuffer buffer, int w, int h) {
        Log.e("UI", "DecodeSdkLibrary w:"+ w +" h:"+h);
        InputImage image = InputImage.fromByteBuffer(buffer,
                /* image width */ w,//760,
                /* image height */h,//640,
                0,
                InputImage.IMAGE_FORMAT_NV21
        );

        scanBarcodes(image);
    }

    private void scanBarcodes(InputImage image) {
        // [START set_detector_options]
        //BarcodeScannerOptions options =
        //        new BarcodeScannerOptions.Builder()
        //                .setBarcodeFormats(
        //                        Barcode.FORMAT_ALL_FORMATS)
        //                .build();
        // [END set_detector_options]

        // [START get_detector]
        //BarcodeScanner scanner = BarcodeScanning.getClient(options);
        // Or, to specify the formats to recognize:
        // BarcodeScanner scanner = BarcodeScanning.getClient(options);
        // [END get_detector]
        //BarcodeScanner scanner = BarcodeScanning.getClient();
        // [START run_detector]
        //Task<List<Barcode>> result = scanner.process(image);

        Task<List<Barcode>> result = ScannerManager.instance().process(image);

        result.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_barcodes]
                        Log.e("UI", "DecodeSdkLibrary barcodes:"+barcodes.size());

                        for (Barcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();
                            Log.e("UI", "DecodeSdkLibrary TYPE_URL:"+rawValue);
                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case Barcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    Log.e("UI", "DecodeSdkLibrary password:"+password);
                                    break;

                                case Barcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    Log.e("UI", "DecodeSdkLibrary TYPE_URL:"+url);
                                    break;
                            }
                        }
                        // [END get_barcodes]
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Log.e("UI", "DecodeSdkLibrary onFailure:");
                    }
                });
        // [END run_detector]
    }
}