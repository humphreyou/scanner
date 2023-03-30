package com.decoder.sdk.barcode;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import androidx.core.content.ContextCompat;
import com.decoder.sdk.camera.GraphicOverlay;
import com.decoder.sdk.camera.GraphicOverlay.Graphic;
import com.decoder.sdk.settings.PreferenceUtils;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import com.decoder.sdk.R;
import android.graphics.Color;

public abstract class BarcodeGraphicBase extends Graphic {
    private final Paint boxPaint;
    private final Paint scrimPaint;
    private final Paint eraserPaint;
    private final float boxCornerRadius;
    @NotNull
    private final Paint pathPaint;
    @NotNull
    private final RectF boxRect;

    public final float getBoxCornerRadius() {
        return this.boxCornerRadius;
    }
    @NotNull
    public final Paint getPathPaint() {
        return this.pathPaint;
    }
    @NotNull
    public final RectF getBoxRect() {
        return this.boxRect;
    }

    public void draw(@NotNull Canvas canvas) {
        canvas.drawRect(0.0F, 0.0F, (float)canvas.getWidth(), (float)canvas.getHeight(), this.scrimPaint);
        this.eraserPaint.setStyle(Style.FILL);
        canvas.drawRoundRect(this.boxRect, this.boxCornerRadius, this.boxCornerRadius, this.eraserPaint);
        this.eraserPaint.setStyle(Style.STROKE);
        canvas.drawRoundRect(this.boxRect, this.boxCornerRadius, this.boxCornerRadius, this.eraserPaint);
        canvas.drawRoundRect(this.boxRect, this.boxCornerRadius, this.boxCornerRadius, this.boxPaint);
    }

    public BarcodeGraphicBase(@NotNull GraphicOverlay overlay) {
        super(overlay);

        boxPaint = new Paint();
        boxPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.barcode_reticle_stroke));
        boxPaint.setStyle(Style.STROKE);
        boxPaint.setStrokeWidth((float)this.getContext().getResources().getDimensionPixelOffset(R.dimen.barcode_reticle_stroke_width));

        scrimPaint = new Paint();
        scrimPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.barcode_reticle_background));

        eraserPaint = new Paint();
        eraserPaint.setStrokeWidth(boxPaint.getStrokeWidth());
        eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

        boxCornerRadius = (float)this.getContext().getResources().getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius);

        pathPaint = new Paint();
        pathPaint.setColor(Color.GRAY);
        pathPaint.setStyle(Style.STROKE);
        pathPaint.setStrokeWidth(boxPaint.getStrokeWidth());
        pathPaint.setPathEffect((new CornerPathEffect(boxCornerRadius)));

        boxRect = PreferenceUtils.Instance.getBarcodeReticleBox(overlay);
    }
}
