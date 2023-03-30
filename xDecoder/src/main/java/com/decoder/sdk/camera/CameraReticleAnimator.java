package com.decoder.sdk.camera;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.annotation.NonNull;

public final class CameraReticleAnimator {
    private float rippleAlphaScale;
    private float rippleSizeScale;
    private float rippleStrokeWidthScale;
    private final AnimatorSet animatorSet;
    private static final long DURATION_RIPPLE_FADE_IN_MS = 333L;
    private static final long DURATION_RIPPLE_FADE_OUT_MS = 500L;
    private static final long DURATION_RIPPLE_EXPAND_MS = 833L;
    private static final long DURATION_RIPPLE_STROKE_WIDTH_SHRINK_MS = 833L;
    private static final long DURATION_RESTART_DORMANCY_MS = 1333L;
    private static final long START_DELAY_RIPPLE_FADE_OUT_MS = 667L;
    private static final long START_DELAY_RIPPLE_EXPAND_MS = 333L;
    private static final long START_DELAY_RIPPLE_STROKE_WIDTH_SHRINK_MS = 333L;
    private static final long START_DELAY_RESTART_DORMANCY_MS = 1167L;

    public final float getRippleAlphaScale() {
        return this.rippleAlphaScale;
    }
    public final float getRippleSizeScale() {
        return this.rippleSizeScale;
    }
    public final float getRippleStrokeWidthScale() {
        return this.rippleStrokeWidthScale;
    }

    public final void start() {
        if (!this.animatorSet.isRunning()) {
            this.animatorSet.start();
        }

    }

    public final void cancel() {
        this.animatorSet.cancel();
        this.rippleAlphaScale = 0.0F;
        this.rippleSizeScale = 0.0F;
        this.rippleStrokeWidthScale = 1.0F;
    }

    public CameraReticleAnimator(@NonNull final GraphicOverlay graphicOverlay) {
        super();
        this.rippleStrokeWidthScale = 1.0F;
        ValueAnimator rippleFadeInAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F}).setDuration(DURATION_RIPPLE_FADE_IN_MS);
        rippleFadeInAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator animation) {
                CameraReticleAnimator var10000 = CameraReticleAnimator.this;
                Object var10001 = animation.getAnimatedValue();
                if (var10001 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Float");
                } else {
                    var10000.rippleAlphaScale = (Float)var10001;
                    graphicOverlay.postInvalidate();
                }
            }
        }));
        ValueAnimator rippleFadeOutAnimator = ValueAnimator.ofFloat(new float[]{1.0F, 0.0F}).setDuration(DURATION_RIPPLE_FADE_OUT_MS);
        rippleFadeOutAnimator.setStartDelay(START_DELAY_RIPPLE_FADE_OUT_MS);
        rippleFadeOutAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator animation) {
                CameraReticleAnimator var10000 = CameraReticleAnimator.this;
                Object var10001 = animation.getAnimatedValue();
                if (var10001 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Float");
                } else {
                    var10000.rippleAlphaScale = (Float)var10001;
                    graphicOverlay.postInvalidate();
                }
            }
        }));
        ValueAnimator rippleExpandAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F}).setDuration(DURATION_RIPPLE_EXPAND_MS);
        rippleExpandAnimator.setStartDelay(START_DELAY_RIPPLE_EXPAND_MS);
        rippleExpandAnimator.setInterpolator((TimeInterpolator)(new FastOutSlowInInterpolator()));
        rippleExpandAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator animation) {
                CameraReticleAnimator var10000 = CameraReticleAnimator.this;
                Object var10001 = animation.getAnimatedValue();
                if (var10001 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Float");
                } else {
                    var10000.rippleSizeScale = (Float)var10001;
                    graphicOverlay.postInvalidate();
                }
            }
        }));
        ValueAnimator rippleStrokeWidthShrinkAnimator = ValueAnimator.ofFloat(new float[]{1.0F, 0.5F}).setDuration(DURATION_RIPPLE_STROKE_WIDTH_SHRINK_MS);
        rippleStrokeWidthShrinkAnimator.setStartDelay(START_DELAY_RIPPLE_STROKE_WIDTH_SHRINK_MS);
        rippleStrokeWidthShrinkAnimator.setInterpolator((TimeInterpolator)(new FastOutSlowInInterpolator()));
        rippleStrokeWidthShrinkAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator animation) {
                CameraReticleAnimator var10000 = CameraReticleAnimator.this;
                Object var10001 = animation.getAnimatedValue();
                if (var10001 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Float");
                } else {
                    var10000.rippleStrokeWidthScale = (Float)var10001;
                    graphicOverlay.postInvalidate();
                }
            }
        }));

        ValueAnimator fakeAnimatorForRestartDelay = ValueAnimator.ofInt(new int[]{0, 0}).setDuration(DURATION_RESTART_DORMANCY_MS);
        fakeAnimatorForRestartDelay.setStartDelay(START_DELAY_RESTART_DORMANCY_MS);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{
                                rippleFadeInAnimator,
                                rippleFadeOutAnimator,
                                rippleExpandAnimator,
                                rippleStrokeWidthShrinkAnimator,
                                fakeAnimatorForRestartDelay});
    }
}
