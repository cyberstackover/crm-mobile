package com.sinergiinformatika.sisicrm.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class ActionPoolService extends Service {
    //public static final String ACTION_PUSH_SURVEY = "id.co.cp.pokphandcrm.action.PUSH_SURVEY";

    private String token;
    private ActionPoolBinder binder = new ActionPoolBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        if (Constants.DEBUG) Log.v(getClass().getName(),
                "service started, token is null: " + (token == null));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Constants.DEBUG) {
            Log.v(getClass().getName(), "starting command");
        }

        if (intent != null) {
            final String action = intent.getAction();
        }

        return Service.START_STICKY;
    }

    /**
     * Handle action Push in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPush(String token) {
        if (Constants.DEBUG) {
            Log.v(getClass().getName(), "searching for data to push");
        }


    }

    public void setToken(String token) {
        this.token = token;
    }

    public class ActionPoolBinder extends Binder {
        public ActionPoolService getService() {
            return ActionPoolService.this;
        }
    }
}
