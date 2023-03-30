package com.decoder.sdk.camera;

import android.graphics.Bitmap;
import org.jetbrains.annotations.NotNull;
import java.nio.ByteBuffer;

public final class CameraInputInfo implements InputInfo {
    private Bitmap bitmap;
    private final ByteBuffer frameByteBuffer;
    private final FrameMetadata frameMetadata;

    @NotNull
    public synchronized Bitmap getBitmap() {
        if (bitmap == null) {
            bitmap = Utils.Instance.convertToBitmap(frameByteBuffer,
                    frameMetadata.getWidth(),
                    frameMetadata.getHeight(),
                    frameMetadata.getRotation());
        }
        return bitmap;
    }

    public CameraInputInfo(@NotNull ByteBuffer frameByteBuffer, @NotNull FrameMetadata frameMetadata) {
        super();
        this.frameByteBuffer = frameByteBuffer;
        this.frameMetadata = frameMetadata;
    }
}

