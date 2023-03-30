package com.decoder.sdk.interfaces;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;


public interface IXcodeScanning<ResultT> {
    void close();
    @NonNull
    Task<ResultT> process(@NonNull InputImage image);
}
