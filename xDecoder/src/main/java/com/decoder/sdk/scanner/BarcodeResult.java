package com.decoder.sdk.scanner;
import com.google.mlkit.vision.barcode.common.Barcode;
import java.util.List;

public final class BarcodeResult {
    public List<Barcode> mBarcodes;
    public BarcodeResult(List<Barcode> barcodes) {
        mBarcodes = barcodes;
    }
}
