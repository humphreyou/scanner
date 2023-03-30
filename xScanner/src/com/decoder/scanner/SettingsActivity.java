package com.decoder.scanner;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.MenuItem;
import com.codecorp.decoder.CortexDecoderLibrary;


@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar myToolbar = findViewById(R.id.prefernceToolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Settings");
        myToolbar.setTitleTextColor(Color.WHITE);
        addPreferencesFromResource(R.xml.preferences);
        myToolbar.setNavigationIcon(R.drawable.arrow_back_white_24);
        Preference licenseActivatePreference = getPreferenceScreen().findPreference("license_activate");
        licenseActivatePreference.setOnPreferenceClickListener(this);

        //only G based library is used
        if(CortexDecoderLibrary.sharedObject(this, "").decoderVersionLevel().equals(CortexDecoderLibrary.DECODER_VERSION_LEVEL_G) == false){
            getPreferenceScreen().removePreference(licenseActivatePreference);
        }

        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("license_activate")) {
            //Intent intent = new Intent(this, LicenseActivity.class);
            //startActivity(intent);
        }
        return false;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
