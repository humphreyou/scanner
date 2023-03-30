package com.decoder.sdk.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.app.Service;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

public class DecodeSdkService extends Service{
    IBinder mBinder;
    private static final String LOG_TAG = "DecodeSdkService";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG, "onRebind");
    }
}