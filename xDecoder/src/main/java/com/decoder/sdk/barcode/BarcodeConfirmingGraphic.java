package com.decoder.sdk.barcode;

import android.graphics.Canvas;
import android.graphics.Path;
import com.decoder.sdk.camera.GraphicOverlay;
import com.decoder.sdk.settings.PreferenceUtils;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import com.google.mlkit.vision.barcode.common.Barcode;

public final class BarcodeConfirmingGraphic extends BarcodeGraphicBase {
    private final Barcode barcode;

    public void draw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        super.draw(canvas);
        float sizeProgress = PreferenceUtils.Instance.getProgressToMeetBarcodeSizeRequirement(this.getOverlay(), this.barcode);
        Path path = new Path();
        if (sizeProgress > 0.95F) {
            path.moveTo(this.getBoxRect().left, this.getBoxRect().top);
            path.lineTo(this.getBoxRect().right, this.getBoxRect().top);
            path.lineTo(this.getBoxRect().right, this.getBoxRect().bottom);
            path.lineTo(this.getBoxRect().left, this.getBoxRect().bottom);
            path.close();
        } else {
            path.moveTo(this.getBoxRect().left, this.getBoxRect().top + this.getBoxRect().height() * sizeProgress);
            path.lineTo(this.getBoxRect().left, this.getBoxRect().top);
            path.lineTo(this.getBoxRect().left + this.getBoxRect().width() * sizeProgress, this.getBoxRect().top);
            path.moveTo(this.getBoxRect().right, this.getBoxRect().bottom - this.getBoxRect().height() * sizeProgress);
            path.lineTo(this.getBoxRect().right, this.getBoxRect().bottom);
            path.lineTo(this.getBoxRect().right - this.getBoxRect().width() * sizeProgress, this.getBoxRect().bottom);
        }

        canvas.drawPath(path, this.getPathPaint());
    }

    public BarcodeConfirmingGraphic(@NotNull GraphicOverlay overlay, @NotNull Barcode barcode) {
        super(overlay);
        this.barcode = barcode;
    }
}
