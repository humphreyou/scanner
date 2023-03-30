package com.decoder.sdk.barcode;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import com.decoder.sdk.camera.GraphicOverlay;
import org.jetbrains.annotations.NotNull;


public final class BarcodeLoadingGraphic extends BarcodeGraphicBase {
    private final PointF[] boxClockwiseCoordinates;
    private final Point[] coordinateOffsetBits;
    private final PointF lastPathPoint;
    private final ValueAnimator loadingAnimator;

    public void draw(@NotNull Canvas canvas) {
        super.draw(canvas);
        float boxPerimeter = (this.getBoxRect().width() + this.getBoxRect().height()) * (float)2;
        Path path = new Path();
        Object var10001 = this.loadingAnimator.getAnimatedValue();
        if (var10001 == null) {
            throw new NullPointerException("null cannot be cast to non-null type kotlin.Float");
        } else {
            float offsetLen = boxPerimeter * (Float)var10001 % boxPerimeter;

            int i;
            float pathLen;
            for(i = 0; i < 4; ++i) {
                pathLen = i % 2 == 0 ? this.getBoxRect().width() : this.getBoxRect().height();
                if (offsetLen <= pathLen) {
                    this.lastPathPoint.x = this.boxClockwiseCoordinates[i].x + (float)this.coordinateOffsetBits[i].x * offsetLen;
                    this.lastPathPoint.y = this.boxClockwiseCoordinates[i].y + (float)this.coordinateOffsetBits[i].y * offsetLen;
                    path.moveTo(this.lastPathPoint.x, this.lastPathPoint.y);
                    break;
                }

                offsetLen -= pathLen;
            }

            pathLen = boxPerimeter * 0.3F;
            int j = 0;

            for(byte var8 = 3; j <= var8; ++j) {
                int index = (i + j) % 4;
                int nextIndex = (i + j + 1) % 4;
                float lineLen = Math.abs(this.boxClockwiseCoordinates[nextIndex].x - this.lastPathPoint.x) + Math.abs(this.boxClockwiseCoordinates[nextIndex].y - this.lastPathPoint.y);
                if (lineLen >= pathLen) {
                    path.lineTo(this.lastPathPoint.x + pathLen * (float)this.coordinateOffsetBits[index].x, this.lastPathPoint.y + pathLen * (float)this.coordinateOffsetBits[index].y);
                    break;
                }

                this.lastPathPoint.x = this.boxClockwiseCoordinates[nextIndex].x;
                this.lastPathPoint.y = this.boxClockwiseCoordinates[nextIndex].y;
                path.lineTo(this.lastPathPoint.x, this.lastPathPoint.y);
                pathLen -= lineLen;
            }

            canvas.drawPath(path, this.getPathPaint());
        }
    }

    public BarcodeLoadingGraphic(@NotNull GraphicOverlay overlay, @NotNull ValueAnimator loadingAnimator) {
        super(overlay);
        this.loadingAnimator = loadingAnimator;
        this.boxClockwiseCoordinates = new PointF[]{new PointF(this.getBoxRect().left, this.getBoxRect().top), new PointF(this.getBoxRect().right, this.getBoxRect().top), new PointF(this.getBoxRect().right, this.getBoxRect().bottom), new PointF(this.getBoxRect().left, this.getBoxRect().bottom)};
        this.coordinateOffsetBits = new Point[]{new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1)};
        this.lastPathPoint = new PointF();
    }
}
