package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.managers.ImageCacheManager;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Mark on 1/10/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ImageDecodeUtil {
    private static final String TAG = ImageDecodeUtil.class.getSimpleName();
    private static Hashtable<String, Bitmap> images = new Hashtable<>();

    public static Bitmap decodeFile(Context context, String key, String filePath) {
        Bitmap result;

        if (!images.containsKey(key)) {
            int imageOrientation = getOrientationFromExif(filePath);
            int screenWidth = DisplayScreen.getScreenWidth(context);
            int imageHeight = screenWidth / 2;
            BitmapFactory.Options bOptions = new BitmapFactory.Options();

            bOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, bOptions);
            bOptions.inSampleSize = ImageCacheManager.calculateInSampleSize(
                    bOptions, screenWidth, imageHeight);
            bOptions.inJustDecodeBounds = false;

            Bitmap decoded = BitmapFactory.decodeFile(filePath, bOptions);

            if (imageOrientation >= 0 && imageOrientation != 0) {
                if (Constants.DEBUG) Log.d(TAG, "rotating image");

                int rotation = getRotationFromOrientation(imageOrientation);

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                result = Bitmap.createBitmap(
                        decoded, 0, 0, decoded.getWidth(), decoded.getHeight(), matrix, true);
            } else {
                result = decoded;
            }

            images.put(key, result);
        } else {
            result = images.get(key);
        }

        return result;
    }

    public static Bitmap decodeUrl(Context context, String id, int type, String url) {
        Bitmap result = null;
        String key = id + "_" + type;

        if (!images.containsKey(key)) {
            ImageCacheManager imageCacheManager = ImageCacheManager.getInstance(context);
            try {
                result = imageCacheManager.getImageFile(type, id, url);
                if (result != null) {
                    images.put(key, result);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            result = images.get(key);
        }

        return result;
    }

    public static int getOrientationFromExif(String imagePath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                       ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;

                    break;
                default:
                    break;
            }

            if (Constants.DEBUG) Log.d(TAG, "image orientation: " + orientation);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get image exif orientation", e);
        }

        return orientation;
    }

    public static int getRotationFromOrientation(int imageOrientation) {
        int rotation;
        switch (imageOrientation) {
            case 90:
                rotation = 90;
                break;
            case 180:
                rotation = -180;
                break;/*
                    case 270:
                        rotation = -90;
                        break;*/
            default:
                rotation = 0;
                break;
        }

        return rotation;
    }

    public static String getImageIdFromUri(String imageUri) {
        if (Constants.DEBUG) Log.d(TAG, "image URL: " + imageUri);

        String imageId = null;
        int lastSlashIdx = imageUri.lastIndexOf('/');

        if (lastSlashIdx > -1 && lastSlashIdx < imageUri.length() - 1) {
            String temp = imageUri.substring(lastSlashIdx + 1);
            if (!TextUtils.isEmpty(temp) && temp.length() > 4 && temp.contains(".")) {
                imageId = temp;
            }
        }

        return imageId;
    }
}
