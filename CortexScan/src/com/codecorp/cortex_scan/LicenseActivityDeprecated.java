package com.codecorp.cortex_scan;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.codecorp.licensing.LicenseCallback;
import com.codecorp.licensing.LicenseStatusCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* For APIs lower than 20 */
@SuppressWarnings("deprecation")
public class LicenseActivityDeprecated extends PreferenceActivity implements Preference.OnPreferenceClickListener, LicenseCallback {

    static final String TAG = LicenseActivityDeprecated.class.getSimpleName();
    private static int SELECT_FILE = 3;
    private MyApplication mApplication;
    private Preference mGenerateDeviceID;
    private Preference mLoadLicenseFile;
    private Preference mActivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (MyApplication) getApplication();
        mApplication.mCortexDecoderLibrary.setLicenseCallback(this);
        addPreferencesFromResource(R.xml.licensepreference);

        mGenerateDeviceID = getPreferenceScreen().findPreference("generate_deviceID");
        mGenerateDeviceID.setOnPreferenceClickListener(this);

        mLoadLicenseFile = getPreferenceScreen().findPreference("load_license");
        mLoadLicenseFile.setOnPreferenceClickListener(this);

        mActivate = getPreferenceScreen().findPreference("activate");
        mActivate.setOnPreferenceClickListener(this);
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Lock phone form factor to portrait.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return preferenceClickListener(preference);

    }

    private boolean preferenceClickListener(Preference preference) {
        if (preference == mGenerateDeviceID) {
            mApplication.mCortexDecoderLibrary.generateDeviceID();
            return true;
        } else if (preference == mLoadLicenseFile) {
            try {
                Intent PickerIntent = new Intent();
                PickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                PickerIntent.setType("*/*");
                startActivityForResult(Intent.createChooser(PickerIntent, "Select V2C File"), SELECT_FILE);
                setResult(RESULT_OK);
                return true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else if (preference == mActivate) {
            if (mApplication.mCortexDecoderLibrary.isLicenseActivated() && !mApplication.mCortexDecoderLibrary.isLicenseExpired()) {
                Toast.makeText(getApplicationContext(), R.string.alreadyActivated, Toast.LENGTH_SHORT).show();
            } else {
                if (isConnectedToInternet()) {
                    showActivationCodeDialog();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return false;
    }


    public void showActivationCodeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.activation_code_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.activationCodeEdit);
        edt.addTextChangedListener(new TextWatcher() {
            boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().replace("-", ""));

                if (sb.length() > 8)
                    sb.insert(8, "-");
                if (sb.length() > 13)
                    sb.insert(13, "-");
                if (sb.length() > 18)
                    sb.insert(18, "-");
                if (sb.length() > 23)
                    sb.insert(23, "-");
                if (sb.length() > 36)
                    sb.delete(36, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEditing = false;
            }
        });

        dialogBuilder.setTitle("Activate the license");
        dialogBuilder.setMessage("Please enter the activation code");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String actCode = edt.getText().toString().trim();
                if (actCode.isEmpty() || actCode.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter the activation code", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Activating the license...", Toast.LENGTH_SHORT).show();
                    mApplication.mCortexDecoderLibrary.activateLicense(actCode);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onActivationResult(LicenseStatusCode statusCode) {
        Log.d(TAG, "onActivationResult:" + statusCode);
        switch (statusCode) {
            case LicenseStatus_LicenseValid:
                Toast.makeText(this, R.string.licenseValid, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, R.string.licenseInvalid, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDeviceIDResult(int resultCode, String data) {
        if (resultCode == 0) {
            Toast mToast;
            try {
                FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/DeviceIDlatest.c2v"));//""
                out.write(data.getBytes());
                out.close();
                mToast = Toast.makeText(this, R.string.deviceIDCreated, Toast.LENGTH_SHORT);
                mToast.show();
            } catch (IOException e) {
                mToast = Toast.makeText(this, R.string.deviceIDNotCreated, Toast.LENGTH_SHORT);
                mToast.show();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FILE) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                if (fileUri == null) return;
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    if (is == null) return;
                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder v2C = new StringBuilder();
                    while ((line = r.readLine()) != null) {
                        v2C.append(line).append('\n');
                    }
                    line = v2C.toString();
                    Log.d(TAG, line);
                    mApplication.mCortexDecoderLibrary.loadLicenseFile(line);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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
}
