package com.decoder.sdk.scanner;


import androidx.annotation.NonNull;
import com.decoder.sdk.interfaces.IXcodeScanning;
import com.decoder.sdk.scanner.mklscanner.MlkScannerImp;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public final class ScannerManager {

    private static ScannerManager s_scannerManger = null;
    private IXcodeScanning mScanner = null;

    public ScannerManager() {
        //1、Choose google machine learning scanner
        setScanner(new MlkScannerImp());

        //2、Choose XpCode scanner
        //setScanner(new XpCodeScannerImp());
    }

    public static ScannerManager instance() {
        synchronized (ScannerManager.class) {
            if (s_scannerManger == null) {
                s_scannerManger = new ScannerManager();
            }
        }
        return s_scannerManger;
    }

    @NonNull
    public  void setScanner(IXcodeScanning scanner) {
        mScanner = scanner;
    }

    @NonNull
    public  void close() {
        mScanner.close();
    }

    @NonNull
    public Task<List<Barcode>> process(@NonNull InputImage image) {
        return mScanner.process(image);
    }
}
