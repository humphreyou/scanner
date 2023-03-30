package com.decoder.sdk.scanner.xpscanner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.decoder.sdk.interfaces.IXcodeScanning;

public interface XpCodeScannerImp extends IXcodeScanning{

    @Override
    void close();

    @Override
    Task<Barcode> process(@NonNull InputImage image);
}
