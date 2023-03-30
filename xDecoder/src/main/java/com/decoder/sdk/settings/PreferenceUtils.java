package com.decoder.sdk.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import androidx.annotation.StringRes;
import com.google.android.gms.common.images.Size;
import androidx.annotation.NonNull;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.decoder.sdk.camera.CameraSizePair;
import com.decoder.sdk.camera.GraphicOverlay;
import kotlin.ranges.RangesKt;
import com.decoder.sdk.R;

public final class PreferenceUtils {
    @NonNull
    public static final PreferenceUtils Instance;

    public final boolean isAutoSearchEnabled(@NonNull Context context) {
        return getBooleanPref(context,  R.string.pref_key_enable_auto_search, true);
    }

    public final boolean isMultipleObjectsMode(@NonNull Context context) {
        return getBooleanPref(context, R.string.pref_key_object_detector_enable_multiple_objects, false);
    }

    public final boolean isClassificationEnabled(@NonNull Context context) {
        return getBooleanPref(context, R.string.pref_key_object_detector_enable_classification, false);
    }

    public final void saveStringPreference(@NonNull Context context, @StringRes int prefKeyId, @NonNull String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(context.getString(prefKeyId), value).apply();
    }

    public final int getConfirmationTimeMs(@NonNull Context context) {
        return  isMultipleObjectsMode(context) ? 300 : isAutoSearchEnabled(context) ?
                getIntPref(context, R.string.pref_key_confirmation_time_in_auto_search, 1500) :
                getIntPref(context, R.string.pref_key_confirmation_time_in_manual_search, 500);
    }

    public final float getProgressToMeetBarcodeSizeRequirement(@NonNull GraphicOverlay overlay, @NonNull Barcode barcode) {
        Context context = overlay.getContext();
        if (getBooleanPref(overlay.getContext(), R.string.pref_key_enable_barcode_size_check, false)) {
            float reticleBoxWidth = getBarcodeReticleBox(overlay).width();
            Rect BoundingBox = barcode.getBoundingBox();
            float barcodeWidth = overlay.translateX(BoundingBox != null ? (float)BoundingBox.width() : 0.0F);
            float requiredWidth = reticleBoxWidth * (float)this.getIntPref(context, R.string.pref_key_minimum_barcode_width, 50) / (float)100;
            return RangesKt.coerceAtMost(barcodeWidth / requiredWidth, 1.0F);
        }

        return 1.0F;
    }

    @NonNull
    public final RectF getBarcodeReticleBox(@NonNull GraphicOverlay overlay) {

        Context context = overlay.getContext();
        float overlayWidth = (float)overlay.getWidth();
        float overlayHeight = (float)overlay.getHeight();

        float boxWidth = overlayWidth * (float)this.getIntPref(context, R.string.pref_key_barcode_reticle_width, 80) / (float)100;
        float boxHeight = overlayHeight * (float)this.getIntPref(context, R.string.pref_key_barcode_reticle_height, 35) / (float)100;
        float cx = overlayWidth / (float)2;
        float cy = overlayHeight / (float)2;
        return new RectF(cx - boxWidth / (float)2, cy - boxHeight / (float)2, cx + boxWidth / (float)2, cy + boxHeight / (float)2);
    }

    public final boolean shouldDelayLoadingBarcodeResult(@NonNull Context context) {
        return this.getBooleanPref(context, R.string.pref_key_delay_loading_barcode_result, true);
    }

    private final int getIntPref(Context context, @StringRes int prefKeyId, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(prefKeyId);
        return sharedPreferences.getInt(prefKey, defaultValue);
    }

    public final CameraSizePair getUserSpecifiedPreviewSize(@NonNull Context context) {
        CameraSizePair pair = null;
        try {
            String previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
            String pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            String var10002 = sharedPreferences.getString(previewSizePrefKey, (String)null);
            String var10003 = sharedPreferences.getString(pictureSizePrefKey, (String)null);
            pair = new CameraSizePair(Size.parseSize(sharedPreferences.getString(previewSizePrefKey, (String)null)),
                                      Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, (String)null)));

        } catch (Exception var5) {
            pair = null;
        }
        return  pair;
    }

    private final boolean getBooleanPref(Context context, @StringRes int prefKeyId, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                                .getBoolean(context.getString(prefKeyId), defaultValue);
    }

    private PreferenceUtils() {
    }

    static {
        Instance = new PreferenceUtils();
    }
}
