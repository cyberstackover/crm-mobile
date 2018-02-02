package com.sinergiinformatika.sisicrm.conn;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 12/23/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class RestResponseHandler extends JsonHttpResponseHandler {
    protected boolean showToast;
    protected String tag;
    protected Context context;

    public RestResponseHandler(Context context) {
        this.showToast = false;
        this.tag = RestResponseHandler.class.getSimpleName();
        this.context = context;
    }

    public RestResponseHandler(boolean showToast, String tag, Context context) {
        this.showToast = showToast;
        this.tag = tag;
        this.context = context;
    }

    public static boolean isSuccess(JSONObject response) throws JSONException {
        String status = response.getString(Constants.JSON_KEY_STATUS);
        return Constants.JSON_STATUS_OK.equals(status);
    }

    public static JSONObject getData(JSONObject response) throws JSONException {
        return response.getJSONObject(Constants.JSON_KEY_DATA);
    }

    /*public static JSONArray getErrors(JSONObject response) throws JSONException {
        return response.getJSONArray(Constants.JSON_KEY_ERROR);
    }*/

    public static int getErrorCode(JSONObject response) throws JSONException {
        JSONObject error = response.getJSONObject(Constants.JSON_KEY_ERROR);
        return error.isNull(Constants.JSON_KEY_ERROR_CODE) ? 0
                : error.getInt(Constants.JSON_KEY_ERROR_CODE);
    }

    public static String getErrorMessage(JSONObject response) throws JSONException {
        JSONObject error = response.getJSONObject(Constants.JSON_KEY_ERROR);
        return error.isNull(Constants.JSON_KEY_ERROR_MESSAGE) ? null
                : error.getString(Constants.JSON_KEY_ERROR_MESSAGE);
    }

    @Override
    public void onCancel() {
        super.onCancel();
        Toast.makeText(context.getApplicationContext(), R.string.message_request_cancel,
                       Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRetry(int retryNo) {
        super.onRetry(retryNo);
        Toast.makeText(context.getApplicationContext(), R.string.message_retry, Toast.LENGTH_SHORT)
             .show();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                          JSONObject errorResponse) {
        if (errorResponse != null) {
            onFailure(throwable.getMessage(), errorResponse.toString(), throwable);
        } else {
            onFailure(throwable.getMessage(), throwable.getMessage(), throwable);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                          JSONArray errorResponse) {
        if (errorResponse != null) {
            onFailure(throwable.getMessage(), errorResponse.toString(), throwable);
        } else {
            onFailure(throwable.getMessage(), throwable.getMessage(), throwable);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString,
                          Throwable throwable) {
        if (!TextUtils.isEmpty(responseString)) {
            onFailure(throwable.getMessage(), responseString, throwable);
        } else {
            onFailure(throwable.getMessage(), throwable.getMessage(), throwable);
        }
    }

    public void onFailure(String message, String log, Throwable throwable) {
        Log.e(tag, log, throwable);

        if (showToast) {
            Toast.makeText(context.getApplicationContext(),
                           TextUtils.isEmpty(message) ? throwable.getMessage() : message,
                           Toast.LENGTH_LONG).show();
        }
    }
}
