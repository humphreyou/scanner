package com.decoder.sdk.settings;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.Preference.OnPreferenceChangeListener;
import com.decoder.sdk.camera.Utils;
import com.decoder.sdk.camera.CameraSizePair;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;
import com.decoder.sdk.R;

public final class SettingsFragment extends PreferenceFragmentCompat {

    public void onCreatePreferences(@Nullable Bundle bundle, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences_sdk, rootKey);
        setUpRearCameraPreviewSizePreference();
    }

    private final void setUpRearCameraPreviewSizePreference() {
        /*
        ListPreference previewSizePreference = findPreference(this.getString(R.string.pref_key_rear_camera_preview_size));
        Camera camera = (Camera)null;
        boolean var11 = false;

        label112: {
            try {

                camera = Camera.open(0);


                List previewSizeList = Utils.Instance.generateValidPreviewSizeList(camera);
                String[] previewSizeStringValues = new String[previewSizeList.size()];
                final HashMap previewToPictureSizeStringMap = new HashMap();
                int i = 0;

                for(int var7 = ((Collection)previewSizeList).size(); i < var7; ++i) {
                    CameraSizePair sizePair = (CameraSizePair)previewSizeList.get(i);
                    previewSizeStringValues[i] = sizePair.getPreview().toString();
                    if (sizePair.getPicture() != null) {
                        Map var16 = (Map)previewToPictureSizeStringMap;
                        String var10001 = sizePair.getPreview().toString();
                        Intrinsics.checkNotNullExpressionValue(var10001, "sizePair.preview.toString()");
                        String var10002 = sizePair.getPicture().toString();
                        Intrinsics.checkNotNullExpressionValue(var10002, "sizePair.picture.toString()");
                        var16.put(var10001, var10002);
                    }
                }

                previewSizePreference.setEntries((CharSequence[])previewSizeStringValues);
                previewSizePreference.setEntryValues((CharSequence[])previewSizeStringValues);
                previewSizePreference.setSummary(previewSizePreference.getEntry());
                previewSizePreference.setOnPreferenceChangeListener((OnPreferenceChangeListener)(new OnPreferenceChangeListener() {
                    public final boolean onPreferenceChange(Preference $noName_0, Object newValue) {
                        if (newValue == null) {
                            throw new NullPointerException("null cannot be cast to non-null type kotlin.String");
                        } else {
                            String newPreviewSizeStringValue = (String)newValue;
                            FragmentActivity var10000 = SettingsFragment.this.getActivity();
                            if (var10000 == null) {
                                return false;
                            } else {
                                Intrinsics.checkNotNullExpressionValue(var10000, "activity ?: return@setOn…renceChangeListener false");
                                FragmentActivity context = var10000;
                                previewSizePreference.setSummary((CharSequence)newPreviewSizeStringValue);
                                PreferenceUtils.Instance.saveStringPreference((Context)context,  R.string.pref_key_rear_camera_picture_size, (String)previewToPictureSizeStringMap.get(newPreviewSizeStringValue));
                                return true;
                            }
                        }
                    }
                }));
                var11 = false;
                break label112;
            } catch (Exception var12) {
                PreferenceGroup var14 = previewSizePreference.getParent();
                if (var14 != null) {
                    var14.removePreference((Preference)previewSizePreference);
                    var11 = false;
                } else {
                    var11 = false;
                }
            } finally {
                if (var11) {
                    if (camera != null) {
                        camera.release();
                    }

                }
            }

            if (camera != null) {
                camera.release();
            }

            return;
        }

        camera.release();*/
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}
