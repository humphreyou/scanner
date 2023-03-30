package com.decoder.sdk.settings;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.Nullable;
import com.decoder.sdk.R;

public final class SettingsActivity extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container,
                (Fragment)(new SettingsFragment())).commit();
    }

    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }


}
