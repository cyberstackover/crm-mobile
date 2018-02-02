package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;

/**
 * Created by Mark on 1/15/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ServerErrorUtil {
    private static final String TAG = ServerErrorUtil.class.getSimpleName();
    private static SparseIntArray errorMap;

    static {
        errorMap = new SparseIntArray();
        errorMap.append(Constants.ERROR_CODE_INVALID_LOGIN, R.string.error_invalid_login);
        errorMap.append(Constants.ERROR_CODE_DATA_NOT_FOUND, R.string.error_data_not_found);
        errorMap.append(Constants.ERROR_CODE_SESSION_EXPIRED, R.string.error_session_expired);
        errorMap.append(Constants.ERROR_CODE_UNAUTHORIZED, R.string.error_unauthorized);
    }

    public static String getErrorMessage(Context context, int errorCode) {
        if (Constants.DEBUG) Log.d(TAG, "error code: " + errorCode);
        int value = errorMap.get(errorCode);
        if (value > 0) {
            return context.getString(value);
        }

        return null;
    }
}
