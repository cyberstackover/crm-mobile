package com.sinergiinformatika.sisicrm.managers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.utils.DisplayScreen;
import com.sinergiinformatika.sisicrm.utils.ImageDecodeUtil;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Mark on 1/22/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ImageCacheManager {
    public static final int IMAGE_TYPE_ITEM_THUMB = 1;
    public static final int IMAGE_TYPE_ITEM_LARGE = 2;
    public static final int IMAGE_TYPE_PROFILE_THUMB = 3;
    private static final String ITEM_THUMB_FILE_PREFIX = "item_thumb_";
    private static final String ITEM_LARGE_FILE_PREFIX = "item_";
    private static final String PROFILE_THUMB_FILE_PREFIX = "thumb_";
    private static ImageCacheManager manager;
    private int imageType = IMAGE_TYPE_ITEM_THUMB;
    private Context context;

    private ImageCacheManager(Context context) {
        this.context = context;
    }

    public static ImageCacheManager getInstance(Context context) {
        if (manager == null)
            manager = new ImageCacheManager(context);
        return manager;
    }

    public static String generateImageFileName(int imageType, String objectId, String extraId) {
        String filename = "";
        switch (imageType) {
            case IMAGE_TYPE_ITEM_THUMB:
                filename += ITEM_THUMB_FILE_PREFIX;
                break;
            case IMAGE_TYPE_ITEM_LARGE:
                filename += ITEM_LARGE_FILE_PREFIX;
                break;
            case IMAGE_TYPE_PROFILE_THUMB:
                filename += PROFILE_THUMB_FILE_PREFIX;
                break;
        }
        filename += objectId;
        if (extraId != null)
            filename += "_" + extraId;
        return filename;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public boolean isImageExist(String name) {
        File storageDir = context.getExternalCacheDir();
        if (storageDir != null) {
            File imageFile = new File(storageDir, name);
            return imageFile.exists();
        }
        return false;
    }

    public String saveImageFile(String name, byte[] data) throws IOException {
        File storageDir = context.getExternalCacheDir();
        File imageFile = new File(storageDir, name);
        if (imageFile.exists()) {
            if (imageFile.length() == data.length)
                return null;
            else
                imageFile.delete();
        }
        FileOutputStream fos = new FileOutputStream(imageFile, false);
        fos.write(data);
        fos.close();
        return imageFile.getAbsolutePath();
    }

    public boolean deleteImageFile(String name) throws IOException {
        File storageDir = context.getExternalCacheDir();
        File imageFile = new File(storageDir, name);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        return true;
    }

    public Bitmap getImageFile(int imageType, String objectId, String imageUrl)
            throws IOException {
        return getImageFile(imageType, objectId, null, imageUrl, true);
    }

    public Bitmap getImageFile(int imageType, String objectId, String extraId,
                               String imageUrl, boolean download) throws IOException {
        this.imageType = imageType;

        Log.d("ImageCacheManager", "get image for: " + imageType + " | " + objectId);

        Bitmap bitmap;
        bitmap = getImageFile(generateImageFileName(imageType, objectId, extraId));
        if ((bitmap == null) && (imageUrl != null) && (!imageUrl.equals("")) && download) {

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(imageUrl, new ImageDownloader(imageType, objectId, extraId));
        }
        return bitmap;
    }

    public Bitmap getImageFile(String name) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        File storageDir = context.getExternalCacheDir();
        if (storageDir != null) {
            File imageFile = new File(storageDir, name);
            if (imageFile.exists()) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int pixelPerDip = metrics.densityDpi / 160;
                int reqHeight;
                int reqWidth;

                if (imageType == IMAGE_TYPE_PROFILE_THUMB) {
                    reqHeight = pixelPerDip * Constants.DIP_HEIGHT_PROFILE_PICTURE;
                    reqWidth = pixelPerDip * Constants.DIP_WIDTH_PROFILE_PICTURE;
                } else if (imageType == IMAGE_TYPE_ITEM_THUMB) {
                    reqHeight = pixelPerDip * Constants.DIP_HEIGHT_ITEM_THUMB;
                    reqWidth = pixelPerDip * Constants.DIP_WIDTH_ITEM_THUMB;
                } else {
                    reqHeight = DisplayScreen.getScreenHeight(context) / 2;
                    reqWidth = DisplayScreen.getScreenWidth(context) / 2;
                }

                if (reqHeight == 0 && reqWidth == 0) {
                    if (Constants.DEBUG) Log.d("ImageCacheManager", "not modifying sample size");
                    options.inSampleSize = 2;
                } else {
                    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                }
                options.inJustDecodeBounds = false;

                if (Constants.DEBUG) {
                    Log.d("ImageCacheManager", "display density: " + metrics.densityDpi);
                    Log.d("ImageCacheManager", "pixel per dip: " + pixelPerDip);
                    Log.d("ImageCacheManager", "sample size: " + options.inSampleSize);
                }

                Bitmap unmodifiedBitmap = BitmapFactory.decodeFile(
                        imageFile.getAbsolutePath(), options);
                int rotation = ImageDecodeUtil.getRotationFromOrientation(
                        ImageDecodeUtil.getOrientationFromExif(imageFile.getAbsolutePath()));
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                return Bitmap.createBitmap(unmodifiedBitmap, 0, 0,
                        unmodifiedBitmap.getWidth(), unmodifiedBitmap.getHeight(), matrix, true);
            }
        }
        return null;
    }

    private class ImageDownloader extends AsyncHttpResponseHandler {

        private int imageType;
        private String objectId;
        private String extraId = null;

        public ImageDownloader(int imageType, String objectId, String extraId) {
            this.imageType = imageType;
            this.objectId = objectId;
            this.extraId = extraId;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (Constants.DEBUG) Log.d("ImageCacheManager", "downloading image");
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            if (bytes != null && bytes.length > 0) {
                try {
                    String fn = saveImageFile(generateImageFileName(imageType, objectId, extraId), bytes);

                    if (fn != null) {

                        Intent intent = new Intent(
                                Constants.INTENT_ACTION_IMAGE_DOWNLOADED);
                        intent.putExtra(Constants.IMAGE_ID_EXTRA,
                                objectId);
                        intent.putExtra(Constants.IMAGE_TYPE_EXTRA,
                                imageType);
                        intent.putExtra(Constants.IMAGE_EXTRA_ID_EXTRA,
                                extraId);
                        intent.putExtra(Constants.IMAGE_FILENAME,
                                fn);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            //do nothing
        }
    }
}
