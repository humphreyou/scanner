package com.decoder.sdk.camera;

import android.graphics.Bitmap;
import org.jetbrains.annotations.NotNull;

public final class BitmapInputInfo implements InputInfo {
    private final Bitmap bitmap;
    @NotNull
    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public BitmapInputInfo(@NotNull Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

