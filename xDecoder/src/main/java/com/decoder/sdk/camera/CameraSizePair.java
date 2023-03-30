package com.decoder.sdk.camera;

import com.google.android.gms.common.images.Size;
import androidx.annotation.NonNull;

public final class CameraSizePair {
    private final Size preview;
    private final Size picture;

    public final Size getPreview() {
        return preview;
    }
    public final Size getPicture() {
        return picture;
    }

    public CameraSizePair(@NonNull android.hardware.Camera.Size previewSize, @NonNull android.hardware.Camera.Size pictureSize) {
        super();
        preview = new Size(previewSize.width, previewSize.height);
        if (pictureSize != null) {
            picture = new Size(pictureSize.width, pictureSize.height);
        } else {
            picture = null;
        }
    }

    public CameraSizePair(@NonNull Size previewSize, @NonNull Size pictureSize) {
        super();
        preview = previewSize;
        picture = pictureSize;
    }
}
