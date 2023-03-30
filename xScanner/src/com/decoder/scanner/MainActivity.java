package com.decoder.scanner;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;

import com.decoder.sdk.camera.Utils;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.codecorp.decoder.CortexDecoderLibrary;
import com.codecorp.decoder.CortexDecoderLibraryCallback;
import com.codecorp.internal.Debug;
import com.codecorp.licensing.LicenseCallback;
import com.codecorp.licensing.LicenseStatusCode;
import com.codecorp.symbology.Symbologies;
import com.codecorp.symbology.SymbologyType;
import com.codecorp.util.Codewords;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;
import com.decoder.sdk.service.DecodeSdkLibrary;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CortexDecoderLibraryCallback {

    private ImageView barcodeImage;
    private ImageView decodeButton;
    private RelativeLayout mMainView;
    private View mAnimationView;
    private MyApplication mApplication;
    private SharedPreferences prefs;

    private TextView mBarcodeResult;
    private TextView mBarcodeLength;
    private TextView mSymbologyResult;
    private CortexDecoderLibrary mCortexDecoderLibrary;
    private DecodeSdkLibrary  mDecodeLib;
    private String picturePath;
    private Boolean uploadImage = false;
    private ArrayAdapter<String> arrayAdapter;
    private AlertDialog.Builder builderSingle;
    private Context mConext;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_PRELOADED_IMAGE = 2;
    private static int SELECT_FILE = 3;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 36;

    private int imageResource = 0;
    private final static String TAG = MainActivity.class.getName();

    TextView toolbar_save;
    private RelativeLayout mLoadingWheel;
    private Toast mToast;
    DrawerLayout drawer;
    private DecodeSdkLibrary sdk;
    private static final String EDK_ACTIVATE_LICENSE_KEY = "please use correct key";
private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConext = this;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mApplication = (MyApplication) getApplication();

        //Request run time permission for WRITE_EXTERNAL_STORAGE before accessing CortexDecoderLibrary for Android versions >= 23

        mCortexDecoderLibrary = CortexDecoderLibrary.sharedObject(mApplication, "nocamera");

        mDecodeLib = DecodeSdkLibrary.sharedObject(mApplication, "nocamera");

        Log.e("UI", "DecodeSdkLibrary version:"+mDecodeLib.getVersion());

        // Save mCortexDecoderLibrary in case another Activity needs it
        mApplication.mCortexDecoderLibrary = mCortexDecoderLibrary;
        mCortexDecoderLibrary.setCallback(this);
        mCortexDecoderLibrary.enableVibrateOnScan(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Debug.debugLevel = Log.VERBOSE;
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
       {

           @Override
           public boolean onOptionsItemSelected(MenuItem item) {
               Log.e(TAG, "onOptionsItemSelected.");
               if (item != null && item.getItemId() == android.R.id.home) {
                   if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                       drawer.closeDrawer(Gravity.RIGHT);
                   } else {
                       drawer.openDrawer(Gravity.RIGHT);
                   }
               }
               return false;
           }
        };


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            drawer.setDrawerListener(toggle);
        } else {
            drawer.addDrawerListener(toggle);
        }

        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mBarcodeResult = (TextView) findViewById(R.id.barcode_result);
        //mBarcodeLength = (TextView) findViewById(R.id.length_result);
        //mSymbologyResult = (TextView) findViewById(R.id.symbology_result);
        barcodeImage = (ImageView) findViewById(R.id.imageView);
        mLoadingWheel = (RelativeLayout) findViewById(R.id.loadingPanel);
        mLoadingWheel.setVisibility(View.GONE);

        mMainView = (RelativeLayout) findViewById(R.id.mainView);
        mAnimationView = findViewById(R.id.animationView);
        decodeButton = (ImageView) findViewById(R.id.barcode_button);

        mCortexDecoderLibrary.enableBeepPlayer(true);
        mCortexDecoderLibrary.changeBeepPlayerSound("1");

        findViewById(R.id.barcode_button_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mDecodeLib.startScanning(mConext);
            }
        });

        decodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //decodeImage();
                mDecodeLib.startScanning(mConext);
/*
                if (mCortexDecoderLibrary.isLicenseActivated()) {
                    enableEAN13Properties();
                    enableDotCodeProperties();
                    if (mCortexDecoderLibrary.isLicenseExpired()) {
                        displayCenterToast(getString(R.string.expiredLicenseToast));
                    } else {
                        decodeImage();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.activateLicenseToast, Toast.LENGTH_SHORT).show();
                }(*/
            }
        });

        //Activating the EDK decoder library - Start
        if(mCortexDecoderLibrary.decoderVersionLevel().equals(CortexDecoderLibrary.DECODER_VERSION_LEVEL_E)) {
            mCortexDecoderLibrary.setLicenseCallback(new LicenseCallback() {
                @Override
                public void onActivationResult(LicenseStatusCode statusCode) {
                    Log.d(TAG, "onActivationResult:" + statusCode);
                    switch (statusCode) {
                        case LicenseStatus_LicenseValid:
                            Toast.makeText(getApplicationContext(), R.string.licenseValid, Toast.LENGTH_SHORT).show();
                            break;
                        case LicenseStatus_LicenseExpired:
                            Toast.makeText(getApplicationContext(), R.string.expiredLicenseToast, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.licenseInvalid, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onDeviceIDResult(int resultCode, String data) {

                }
            });

            mCortexDecoderLibrary.activateLicense(EDK_ACTIVATE_LICENSE_KEY);
        }
        //Activating the EDK decoder library - end

        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        //createDialogBuilder();
    }

    private void enableEAN13Properties() {
        if (mCortexDecoderLibrary != null && mCortexDecoderLibrary.isLicenseActivated()) {
            Symbologies.EAN13Properties ean13Properties = new Symbologies.EAN13Properties();
            ean13Properties.setEnabled(getApplicationContext(), true);
        }
    }

    private void enableDotCodeProperties() {
        if (mCortexDecoderLibrary != null && mCortexDecoderLibrary.isLicenseActivated()) {
            Symbologies.DotCodeProperties p = new Symbologies.DotCodeProperties();
            p.setEnabled(getApplicationContext(), true);
            p.setMirrorDecodingEnabled(getApplicationContext(), true);
            p.setPolarity(getApplicationContext(), Symbologies.DotCodePropertiesPolarity.DotCodePropertiesPolarity_Either);
        }
    }

    void displayCenterToast(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }*/
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // The action bar home/up action should open or close the drawer.
    // ActionBarDrawerToggle will take care of this.
    if (toggle.onOptionsItemSelected(item)) {
        return true;
    }

    return super.onOptionsItemSelected(item);
}

    @Override
    public void onBackPressed() {
    /*    if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.phoneGallery) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSIONS_REQUEST_CODE);
                    return false;
                } else {
                    startPhoneGalleryIntent();
                }
            } else {
                startPhoneGalleryIntent();
            }

        } else if (id == R.id.barcodeGallery) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ImageRollActivity.class);
            startActivityForResult(intent, 2);
        } else if (id == R.id.defaultImage) {
            imageResource = R.drawable.qrcode_gray;
            Drawable image = getResources().getDrawable(imageResource);
            barcodeImage.setImageDrawable(image);
            uploadImage = false;

            Bitmap bitmap=BitmapFactory.decodeResource(getResources(), imageResource);
            mDecodeLib.doDecodeBitMap(bitmap, bitmap.getWidth(), bitmap.getHeight());

        } else if (id == R.id.settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startPhoneGalleryIntent() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    //Here is where will set the image into a bitmap and decode it from the decoderlib file.

    private void decodeImage() {

        if (imageResource != 0 || uploadImage) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inScaled = false; /* Flag for no scalling */

            /* Load the bitmap with the options */
            Bitmap bm;
            if (!uploadImage)
                bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), imageResource, opts);
            else
                bm = BitmapFactory.decodeFile(picturePath);
            int width = bm.getWidth();
            int height = bm.getHeight();

            // allocate buffer to store bitmap RGB pixel data
            int[] rgb = new int[width * height];
            byte[] gray = new byte[width * height];

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] byteArray = stream.toByteArray();

            int size = bm.getRowBytes() * bm.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bm.copyPixelsToBuffer(byteBuffer);
            byteArray = byteBuffer.array();

            // copy color pixels into rgb buffer
            bm.getPixels(rgb, 0, width, 0, 0, width, height);

            // convert RGB pixels to grayscal pixels and place them in gray buffer
            for (int i = 0; i < width * height - 1; i++) {
                gray[i] = (byte) ((306 * ((rgb[i] >> 16) & 0xFF) + 601 * ((rgb[i] >> 8) & 0xFF) + 117 * (rgb[i] & 0xFF)) / 1024);
            }

            // create ByteBuffer with grayscale pixels

            //Method - 1 : Getting result with a callback method
            ByteBuffer pixBuf = ByteBuffer.allocateDirect(width * height);
            pixBuf.put(gray);

            if(false) {
                mCortexDecoderLibrary.doDecode(pixBuf, width, height, width);
            }
            else {
                mDecodeLib.doDecode(pixBuf, width, height);
            }

            //Method - 2 : Getting result without callback - doDecodeForResult() returns Result with all the decoded information

            //You can pass either the grayscale pixel data, width and height of an image to this API. Or you can pass byte array obtained from the Camera preview along with its width and height.
            //If you are using this API, you do not need to implement CortexDecoderLibraryCallback and its callback methods
            /*final Result res = mCortexDecoderLibrary.doDecodeForResult(gray, width, height);
            if (res != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAnimationView.setBackgroundColor(1738800970);
                        scaleView();
                        mBarcodeResult.setText(res.text);
                        mBarcodeLength.setText(String.valueOf(res.text.length()));
                        mSymbologyResult.setText(res.format);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAnimationView.setBackgroundColor(1761542144);
                        scaleView();
                    }
                });
            }*/
        } else {
            //displayCenterToast("Please load an image to decode");
        }
    }


    @Override
    public void receivedDecodedData(final String data, SymbologyType type) {
        final String symString = CortexDecoderLibrary.stringFromSymbologyType(type);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAnimationView.setBackgroundColor(1738800970);
                scaleView();
                mBarcodeResult.setText(data);
                mBarcodeLength.setText(String.valueOf(data.length()));
                mSymbologyResult.setText(symString);
            }
        });
    }

    @Override
    public void receivedMultipleDecodedData(final String[] Data, final SymbologyType[] mTypes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAnimationView.setBackgroundColor(1738800970);
                scaleView();
                for (int i = 0; i < mTypes.length; i++) {
                    String symString = CortexDecoderLibrary.stringFromSymbologyType(mTypes[i]);
                    arrayAdapter.add("Barcode: " + Data[i] + "\n" + "Symbology: " + symString + "\n" + "Length: " + Integer.toString(Data[i].length()));
                }
                Drawable image = getResources().getDrawable(imageResource);
                Bitmap b = ((BitmapDrawable) image).getBitmap();
                Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 200, 200, false);
                builderSingle.setIcon(new BitmapDrawable(getResources(), bitmapResized));
                builderSingle.show();
            }
        });
    }

    @Override
    public void receiveBarcodeCorners(int[] corners) {

    }

    @Override
    public void receivedDecodedCodewordsData(Codewords codewords) {
        if(codewords != null){
            codewords.getNumberOfCodewords();
            codewords.getNumberOfShortCodewordsBlocks();
            codewords.getNumberOfLongCodewordsBlocks();
            codewords.getNumberOfDataCodewords();
            codewords.getNumberOfErrorCodewords();
            codewords.getCodewordsBeforeErrorCorrection();
            codewords.getCodewordsAfterErrorCorrection();
        }
    }

    @Override
    public void barcodeDecodeFailed(boolean result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAnimationView.setBackgroundColor(1761542144);
                scaleView();
            }
        });
    }

    @Override
    public void multiFrameDecodeCount(int decodeCount) {
        //Callback will not be called since the app uses decoder as Image Scan
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (picturePath == null)
                return;

            File file = new File(picturePath);
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            uploadImage = true;

            Drawable d = new BitmapDrawable(getResources(), bitmap);


            Log.e("Loading", "load from gallery h:" + bitmap.getHeight()+ ", w"+ bitmap.getWidth()+ "");

            barcodeImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            mDecodeLib.doDecodeBitMap(bitmap, bitmap.getWidth(), bitmap.getHeight());


        } else if (requestCode == RESULT_LOAD_PRELOADED_IMAGE && resultCode == RESULT_OK) {
            int position = data.getIntExtra("imageData", -1);
            ImageAdapter imageAdapter = new ImageAdapter(this);
            imageResource = imageAdapter.mThumbIds[position];
            Drawable image = getResources().getDrawable(imageResource);
            barcodeImage.setImageDrawable(image);
            uploadImage = false;

            Bitmap bitmap=BitmapFactory.decodeResource(getResources(), imageResource);
            mDecodeLib.doDecodeBitMap(bitmap, bitmap.getWidth(), bitmap.getHeight());

        } else if (requestCode == SELECT_FILE) {

            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                // String selectedFileUriPath = fileUri.getPath();
                Log.i("came", resultCode + "");

                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer v2C = new StringBuffer();
                    while ((line = r.readLine()) != null) {
                        v2C.append(line).append('\n');
                    }
                    line = v2C.toString();
                    Log.d(TAG, line);
                    mCortexDecoderLibrary.loadLicenseFile(line);
                    mLoadingWheel.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        pushPreferences();

        if (!Utils.Instance.allPermissionsGranted(this)) {
            Utils.Instance.requestRuntimePermissions(this);
        }
    }

    private void pushPreferences() {
        for (Map.Entry<String, ?> e : prefs.getAll().entrySet()) {
            try {
                mApplication.pushPreference(prefs, e.getKey());
            } catch (Exception ex) {
                //Log.e(TAG, "Error pushing preference " + e.getKey() + ":", ex);
            }
        }
    }

    /*
            This is used to Scale a view to add a nice effect when a san is successful.
            The screen with display a green flash notifying of a successful scan.
         */
    public void scaleView() {

        int screenW = mMainView.getWidth();
        int screenH = mMainView.getHeight();

        int _x = mAnimationView.getWidth();
        int _y = mAnimationView.getHeight();

        float xRatio = (float) screenW / (float) _x;
        float yRatio = (float) screenH / (float) _y;

        float xMiddle = (float) mAnimationView.getWidth() / (float) 2;
        float yMiddle = (float) mAnimationView.getHeight() / (float) 2;

        ScaleAnimation scale = new ScaleAnimation((float) 0.1, xRatio, (float) 0.1, yRatio, xMiddle, yMiddle);
        scale.setDuration(500);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimationView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimationView.startAnimation(scale);

    }


    public void createDialogBuilder() {
        builderSingle = new AlertDialog.Builder(this);
        //  builderSingle.setIcon(imageResource);
        builderSingle.setTitle("Decoded Barcodes");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                arrayAdapter.clear();
            }
        });
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                arrayAdapter.clear();
            }
        });

    }

    private boolean checkPermissions(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(), permission);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(final String permission, final int requestCode) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                permission);
/*
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
*/
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.mainView),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{permission},
                                    requestCode);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission},
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == STORAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                startPhoneGalleryIntent();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.mainView),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }
}
