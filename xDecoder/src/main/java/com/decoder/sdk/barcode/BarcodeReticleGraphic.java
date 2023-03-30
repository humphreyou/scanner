package com.decoder.sdk.barcode;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import androidx.core.content.ContextCompat;
import com.decoder.sdk.camera.CameraReticleAnimator;
import com.decoder.sdk.camera.GraphicOverlay;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import com.decoder.sdk.R;
import android.graphics.Color;
import android.util.Log;

public final class BarcodeReticleGraphic extends BarcodeGraphicBase {
    private final Paint ripplePaint;
    private final int rippleSizeOffset;
    private final int rippleStrokeWidth;
    private final int rippleAlpha;
    private final CameraReticleAnimator animator;

    public void draw(@NotNull Canvas canvas) {
        super.draw(canvas);
        this.ripplePaint.setAlpha((int)((float)this.rippleAlpha * this.animator.getRippleAlphaScale()));
        this.ripplePaint.setStrokeWidth((float)this.rippleStrokeWidth * this.animator.getRippleStrokeWidthScale());
        float offset = (float)this.rippleSizeOffset * this.animator.getRippleSizeScale();
        RectF rippleRect = new RectF(this.getBoxRect().left - offset, this.getBoxRect().top - offset, this.getBoxRect().right + offset, this.getBoxRect().bottom + offset);
        canvas.drawRoundRect(rippleRect, this.getBoxCornerRadius(), this.getBoxCornerRadius(), this.ripplePaint);
    }

    public BarcodeReticleGraphic(@NotNull GraphicOverlay overlay, @NotNull CameraReticleAnimator animator) {
        super(overlay);
        this.animator = animator;
        Resources resources = overlay.getResources();
        this.ripplePaint = new Paint();
        this.ripplePaint.setStyle(Style.STROKE);
        this.ripplePaint.setColor(ContextCompat.getColor(this.getContext(), R.color.reticle_ripple));
        this.rippleSizeOffset = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset);
        this.rippleStrokeWidth = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width);
        this.rippleAlpha = ripplePaint.getAlpha();
    }
}
