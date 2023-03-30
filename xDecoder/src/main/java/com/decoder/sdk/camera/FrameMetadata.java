package com.decoder.sdk.camera;
import java.nio.ByteBuffer;
import androidx.annotation.NonNull;

public final class FrameMetadata {
    private final int width;
    private final int height;
    private final int rotation;

    public final int getWidth() {
        return width;
    }
    public final int getHeight() {
        return height;
    }
    public final int getRotation() {
        return rotation;
    }

    public FrameMetadata(int width, int height, int rotation) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }
}

