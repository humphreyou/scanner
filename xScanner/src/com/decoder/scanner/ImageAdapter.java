package com.decoder.scanner;
// Copyright (c) 2014-2019 The Code Corporation. All rights reserved.

import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import static com.codecorp.internal.Debug.debug;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        ImageDownloaderTask downloaderTask = new ImageDownloaderTask(mContext, imageView, mThumbIds[position]);
        downloaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mThumbIds[position]);

        return imageView;
    }

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.issn_1,
            R.drawable.qrcode_gray,
            R.drawable.default_qrcode,
            R.drawable.upc_color,
            R.drawable.d_1,
            R.drawable.d_4,
            R.drawable.d_5,
            R.drawable.d_6,
            R.drawable.qr_6,
            R.drawable.qr_7,
            R.drawable.qr_8,
            R.drawable.multibarcode4,
            R.drawable.multibarcode6,
            R.drawable.multibarcode7,
            R.drawable.issn_1,
            R.drawable.qrcode_gray,
            R.drawable.default_qrcode,
            R.drawable.upc_color,
            R.drawable.d_1,
            R.drawable.d_4,
            R.drawable.d_5,
            R.drawable.d_6,
            R.drawable.qr_6,
            R.drawable.qr_7,
            R.drawable.qr_8,
            R.drawable.multibarcode4,
            R.drawable.multibarcode6,
            R.drawable.multibarcode7
    };

    class ImageDownloaderTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private Context context;
        private Integer position;

        public ImageDownloaderTask(Context context, ImageView imageView, Integer pos) {
            this.context = context;
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.position = pos;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            return decodeSampledBitmapFromResource(this.context.getApplicationContext().getResources(), params[0], 100, 100);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}