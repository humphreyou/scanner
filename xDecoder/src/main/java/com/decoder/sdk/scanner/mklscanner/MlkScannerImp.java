package com.decoder.sdk.scanner.mklscanner;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import com.decoder.sdk.interfaces.IXcodeScanning;

public class MlkScannerImp implements IXcodeScanning {
    private BarcodeScanner mScanner = null;

    public MlkScannerImp() {
        MlkScannerInit();
        if (mScanner == null) {
            mScanner = BarcodeScanning.getClient();
        }
    }

    private void MlkScannerInit() {
        // [START set_detector_options]
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build();
        // [END set_detector_options]

        // [START get_detector]
        mScanner = BarcodeScanning.getClient(options);
        // [END get_detector]
    }
    @Override
    public  void close() {
        mScanner.close();
    }

    @Override
    public  Task process(@NonNull InputImage image) {
        return  mScanner.process(image);
   }
}
