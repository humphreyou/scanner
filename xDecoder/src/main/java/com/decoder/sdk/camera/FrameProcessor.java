package com.decoder.sdk.camera;

import java.nio.ByteBuffer;
import androidx.annotation.NonNull;

public interface FrameProcessor {
    void process(@NonNull ByteBuffer data, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay);
    void stop();
}
