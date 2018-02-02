package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mark on 7/24/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class MediaStoreUtil {

    public static String getPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        String temp = uri.getPath();
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            temp = cursor.getString(column_index);
            cursor.close();
        }

        return temp;
    }

    public static File getOutputImageFile(String tag) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Constants.IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("MediaStoreUtil", "could not create directory");
            }
        }

        String timeStamp = DateUtil.formatImageDate(Calendar.getInstance().getTime());

        File imageFile;
        if (tag == null) {
            imageFile = new File(mediaStorageDir.getPath() + File.separator + "image_" + timeStamp
                    + ".jpg");
        } else {
            imageFile = new File(mediaStorageDir.getPath() + File.separator + "image_" + timeStamp
                    + tag + ".jpg");
        }

        return imageFile;
    }

    public static Uri getOutputImageFileUri(String tag) {
        return Uri.fromFile(getOutputImageFile(tag));
    }

}
