package com.decoder.sdk.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.YuvImage;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
//import com.google.mlkit.md.camera.CameraSizePair;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import androidx.annotation.NonNull;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

public final class Utils {
    public static final float ASPECT_RATIO_TOLERANCE = 0.01F;
    public static final int   REQUEST_CODE_PHOTO_LIBRARY = 1;
    private static final String TAG = "Utils";

    public static final Utils Instance = new Utils();

    public final void requestRuntimePermissions(@NonNull Activity activity) {

        String[] permissions = getRequiredPermissions((Context)activity);

        Collection allNeededPermissions = (Collection)(new ArrayList());

        int i = 0;
        while(i < permissions.length) {
            String element = permissions[i++];
            if (ContextCompat.checkSelfPermission((Context)activity, element) != 0) {
                allNeededPermissions.add(element);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            String[] var15 = (String[])allNeededPermissions.toArray(new String[0]);
            if (var15 == null) {
                throw new NullPointerException("");
            }
            ActivityCompat.requestPermissions(activity, (String[])var15, 0);
        }

    }

    public final boolean allPermissionsGranted(@NonNull Context context) {
        boolean result = false;
        String[] permissions = getRequiredPermissions(context);

        for (int i = 0; i < permissions.length; i++) {
            String element = permissions[i];
            if (ContextCompat.checkSelfPermission(context, element) == 0) {
                result = true;
                continue;
            }
        }
        return result;
    }

    private final String[] getRequiredPermissions(Context context) {
        String[] result;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            result = info.requestedPermissions;
            result = result != null && result.length != 0 ? result : new String[0];
        } catch (Exception var5) {
            result = new String[0];
        }
        return result;
    }

    public final boolean isPortraitMode(@NonNull Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @NonNull
    public final List generateValidPreviewSizeList(@NonNull Camera camera) {

        Parameters parameters = camera.getParameters();
        List supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List supportedPictureSizes = parameters.getSupportedPictureSizes();
        ArrayList validPreviewSizes = new ArrayList();
        Iterator var7 = supportedPreviewSizes.iterator();

        while(true) {
            Size previewSize;
            while(var7.hasNext()) {
                previewSize = (Size)var7.next();
                float previewAspectRatio = (float)previewSize.width / (float)previewSize.height;
                Iterator var10 = supportedPictureSizes.iterator();

                while(var10.hasNext()) {
                    Size pictureSize = (Size)var10.next();
                    float pictureAspectRatio = (float)pictureSize.width / (float)pictureSize.height;
                    float var12 = previewAspectRatio - pictureAspectRatio;
                    if (Math.abs(var12) < 0.01F) {

                        validPreviewSizes.add(new CameraSizePair(previewSize, pictureSize));
                        break;
                    }
                }
            }

            if (validPreviewSizes.isEmpty()) {
                Log.w("Utils", "No preview sizes have a corresponding same-aspect-ratio picture size.");
                var7 = supportedPreviewSizes.iterator();

                while(var7.hasNext()) {
                    previewSize = (Size)var7.next();

                    validPreviewSizes.add(new CameraSizePair(previewSize, (Size)null));
                }
            }

            return (List)validPreviewSizes;
        }
    }

        @NonNull
        public final Bitmap getCornerRoundedBitmap(@NonNull Bitmap srcBitmap, int cornerRadius) {

            Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(dstBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0.0F, 0.0F, (float)srcBitmap.getWidth(), (float)srcBitmap.getHeight());
            canvas.drawRoundRect(rectF, (float)cornerRadius, (float)cornerRadius, paint);
            paint.setXfermode((Xfermode)(new PorterDuffXfermode(Mode.SRC_IN)));
            canvas.drawBitmap(srcBitmap, 0.0F, 0.0F, paint);
            return dstBitmap;
        }

        @NonNull
        public final Bitmap convertToBitmap(@NonNull ByteBuffer data, int width, int height, int rotationDegrees) {
            data.rewind();
            byte[] imageInBuffer = new byte[data.limit()];
            data.get(imageInBuffer, 0, imageInBuffer.length);

            try {
                YuvImage image = new YuvImage(imageInBuffer, 17, width, height, (int[])null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, width, height), 80, (OutputStream)stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.close();
                Matrix matrix = new Matrix();
                matrix.postRotate((float)rotationDegrees);
                return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            } catch (Exception var10) {
                Log.e("Utils", "Error: " + var10.getMessage());
                return null;
            }
        }

        public final void openImagePicker$app_debug(@NonNull Activity activity) {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("image/*");
            activity.startActivityForResult(intent, 1);
        }

        @NonNull
        public final Bitmap loadImage$app_debug(@NonNull Context context, @NonNull Uri imageUri, int maxImageDimension) throws IOException {

            InputStream inputStreamForSize = (InputStream)null;
            InputStream inputStreamForImage = (InputStream)null;

            Bitmap var9;
            try {
                inputStreamForSize = context.getContentResolver().openInputStream(imageUri);
                Options opts = new Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStreamForSize, (Rect)null, opts);
                int inSampleSize = Math.max(opts.outWidth / maxImageDimension, opts.outHeight / maxImageDimension);
                opts = new Options();
                opts.inSampleSize = inSampleSize;
                inputStreamForImage = context.getContentResolver().openInputStream(imageUri);
                Bitmap decodedBitmap = BitmapFactory.decodeStream(inputStreamForImage, (Rect)null, opts);
                ContentResolver var10001 = context.getContentResolver();
                var9 = this.maybeTransformBitmap(var10001, imageUri, decodedBitmap);
            } finally {
                if (inputStreamForSize != null) {
                    inputStreamForSize.close();
                }

                if (inputStreamForImage != null) {
                    inputStreamForImage.close();
                }

            }

            return var9;
        }

        private final Bitmap maybeTransformBitmap(ContentResolver resolver, Uri uri, Bitmap bitmap) {
            Matrix var10000;
            Matrix var5;
            boolean var7;
            switch(this.getExifOrientationTag(resolver, uri)) {
                case 0:
                case 1:
                    var10000 = null;
                    break;
                case 2:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postScale(-1.0F, 1.0F);
                    var10000 = var5;
                    break;
                case 3:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postRotate(180.0F);
                    var10000 = var5;
                    break;
                case 4:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postScale(1.0F, -1.0F);
                    var10000 = var5;
                    break;
                case 5:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postScale(-1.0F, 1.0F);
                    var10000 = var5;
                    break;
                case 6:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postRotate(90.0F);
                    var10000 = var5;
                    break;
                case 7:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postRotate(-90.0F);
                    var5.postScale(-1.0F, 1.0F);
                    var10000 = var5;
                    break;
                case 8:
                    var5 = new Matrix();
                    var7 = false;
                    var5.postRotate(-90.0F);
                    var10000 = var5;
                    break;
                default:
                    var10000 = null;
            }

            Matrix matrix = var10000;
            Bitmap var8;
            if (matrix != null) {
                var8 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else {
                var8 = bitmap;
            }

            return var8;
        }

        private final int getExifOrientationTag(ContentResolver resolver, Uri imageUri) {

            if (!imageUri.getScheme().equals(ContentResolver.SCHEME_CONTENT) &&
                    !imageUri.getScheme().equals(ContentResolver.SCHEME_FILE) ) {
                return 0;
            }

            ExifInterface exif = (ExifInterface)null;

            try {
                InputStream var10000 = resolver.openInputStream(imageUri);
                if (var10000 != null) {
                    Closeable var4 = (Closeable)var10000;
                    Throwable var5 = null;

                    try {
                        InputStream inputStream = (InputStream)var4;
                        exif = new ExifInterface(inputStream);
                    } catch (Throwable var11) {
                        var5 = var11;
                        throw var11;
                    } finally {
                        //CloseableKt.closeFinally(var4, var5);
                    }
                }
            } catch (IOException var13) {
                Log.e("Utils", "Failed to open file to read rotation meta data: " + imageUri, (Throwable)var13);
            }

            int var14;
            if (exif != null) {
                var14 = exif.getAttributeInt("Orientation", 1);
            } else {
                var14 = 0;
            }
            return var14;

        }

    private Utils() {
    }


}
