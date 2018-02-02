package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;

import net.grandcentrix.tray.TrayAppPreferences;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 10/29/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class User {
    private static User instance;
    private TrayAppPreferences preferences;

    private User() {

    }

    public static User getInstance(Context context) {
        if (instance == null) {
            instance = new User();
            instance.preferences = new TrayAppPreferences(context);
        }

        return instance;
    }

    public static boolean rePostLogin(final Context context) {
        return rePostLogin(context, false);
    }

    public static boolean rePostLogin(final Context context, boolean async) {
        final boolean[] result = {false};
        String username = getInstance(context).getUsername();
        String password = getInstance(context).getKey();

        JsonHttpResponseHandler responseHandler = new RestResponseHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (isSuccess(response)) {
                        JSONObject data = getData(response);
                        getInstance(context)
                                .setToken(data.getString(Constants.JSON_KEY_USER_SESSION));

                        result[0] = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            RestClient.getInstance(context, responseHandler).postLogin(username, password, async);
        }

        return result[0];
    }

    private String getStringProperty(String key) {
        return preferences.getString(key, "");
    }

    private void setStringProperty(String key, String value) {
        /*SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value).apply();*/
        preferences.put(key, value);
    }

    private boolean getBooleanProperty(String key) {
        return preferences.getBoolean(key, false);
    }

    private void setBooleanProperty(String key, boolean value) {
        /*SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value).apply();*/
        preferences.put(key, value);
    }

    /*private int getIntProperty(String key) {
        return preferences.getInt(key, 0);
    }

    private void setIntProperty(String key, int value) {
        *//*SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value).apply();*//*
        preferences.put(key, value);
    }*/

    private float getFloatProperty(String key) {
        return preferences.getFloat(key, 0.0F);
    }

    private void setFloatProperty(String key, float value) {
        /*SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value).apply();*/
        preferences.put(key, value);
    }

    /**
     * Clearing all saved preferences, used for logging out
     */
    public void reset() {
        String userId = getUserId();
        /*SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();*/
        preferences.clear();
        setStringProperty(Constants.USER_ID, userId);
    }

    public boolean isLoggedIn() {
        return !getUserId().isEmpty() && !getToken().isEmpty();
    }

    public void signIn(JSONObject data) throws JSONException {
        setStringProperty(Constants.USER_ID, data.getString(Constants.JSON_KEY_USER_ID));
        setStringProperty(Constants.USER_NAME, data.getString(Constants.JSON_KEY_USER_NAME));
        setStringProperty(Constants.USER_FIRST_NAME,
                          data.getString(Constants.JSON_KEY_USER_FIRST_NAME));
        setStringProperty(Constants.USER_LAST_NAME,
                          data.getString(Constants.JSON_KEY_USER_LAST_NAME));
        setStringProperty(Constants.USER_ROLE, data.getString(Constants.JSON_KEY_USER_ROLE));
        setStringProperty(Constants.USER_SESSION, data.getString(Constants.JSON_KEY_USER_SESSION));

        boolean isAreaIdExists = false;
        boolean isAreaNameExists = false;

        if (!data.isNull("area")) {

            //Log.d(TAG, data.get("area").getClass().getSimpleName());

            JSONObject area = data.getJSONObject("area");
            if (!area.isNull("name")) {
                isAreaNameExists = true;
                setStringProperty(Constants.USER_AREA_NAME, area.getString("name"));
            }

            if (!area.isNull("id")) {
                isAreaIdExists = true;
                setStringProperty(Constants.USER_AREA_ID, area.getString("id"));
            }
        }

        if (!isAreaNameExists) {
            setStringProperty(Constants.USER_AREA_NAME, "");
        }

        if (!isAreaIdExists) {
            setStringProperty(Constants.USER_AREA_ID, "");
        }

        boolean isDistributorExists = false;
        if (!data.isNull("distributor")) {
            JSONObject distributor = data.getJSONObject("distributor");
            if (!distributor.isNull("name")) {
                isDistributorExists = true;
                setStringProperty(Constants.USER_DISTRIBUTOR_NAME, distributor.getString("name"));
            }
            if (!distributor.isNull("id")) {
                setStringProperty(Constants.USER_DISTRIBUTOR_ID, distributor.getString("id"));
            }
        }

        if (!isDistributorExists) {
            setStringProperty(Constants.USER_DISTRIBUTOR_ID, "");
            setStringProperty(Constants.USER_DISTRIBUTOR_NAME, "");
        }


        boolean isAllowSurveyWithoutCheckin = Constants.IS_ALLOW_SURVEY_WITHOUT_CHECKIN;
        float maxCheckInDistance = (float) Constants.MAX_ALLOWED_DISTANCE;

        if (!data.isNull("setting")) {

            JSONObject setting = data.getJSONObject("setting");
            if (!setting.isNull("is_allow_survey_check_in")) {
                isAllowSurveyWithoutCheckin = setting.getBoolean("is_allow_survey_check_in");
            }

            if (!setting.isNull("max_check_in_distance")) {
                maxCheckInDistance = (float) setting.getDouble("max_check_in_distance");
            }
        }

        setBooleanProperty(Constants.USER_ALLOW_SURVEY_WITHOUT_CHECKIN,
                           isAllowSurveyWithoutCheckin);
        setFloatProperty(Constants.USER_MAX_CHECKIN_DISTANCE, maxCheckInDistance);

    }

    public void signOut() {
        reset();
    }

    public String getUserId() {
        return getStringProperty(Constants.USER_ID);
    }

    public String getToken() {
        return getStringProperty(Constants.USER_SESSION);
    }

    public void setToken(String token) {
        setStringProperty(Constants.USER_SESSION, token);
    }

    public String getUsername() {
        return getStringProperty(Constants.USER_NAME);
    }

    public String getFirstName() {
        return getStringProperty(Constants.USER_FIRST_NAME);
    }

    public String getLastName() {
        return getStringProperty(Constants.USER_LAST_NAME);
    }

    public String getRoleName() {
        return getStringProperty(Constants.USER_ROLE);
    }

    public String getAreaName() {
        return getStringProperty(Constants.USER_AREA_NAME);
    }

    public String getAreaId() {
        return getStringProperty(Constants.USER_AREA_ID);
    }

    public String getDistributorId() {
        return getStringProperty(Constants.USER_DISTRIBUTOR_ID);
    }

    public String getDistributor() {
        return getStringProperty(Constants.USER_DISTRIBUTOR_NAME);
    }

    public String getKey() {
        return getStringProperty(Constants.USER_KEY);
    }

    public void setKey(String key) {
        setStringProperty(Constants.USER_KEY, key);
    }

    public boolean isAllowSurveyWithoutCheckIn() {
        return getBooleanProperty(Constants.USER_ALLOW_SURVEY_WITHOUT_CHECKIN);
    }

    public double getMaxCheckInDistance() {
        double maxCheckInDistance = getFloatProperty(Constants.USER_MAX_CHECKIN_DISTANCE);
        if (maxCheckInDistance == 0.0) {
            maxCheckInDistance = Constants.MAX_ALLOWED_DISTANCE;
        }
        return maxCheckInDistance;
    }

    /*public boolean isSales() {
        return TextUtils.isEmpty(getRoleName()) ||
               Constants.ROLE_NAME_SALES.equals(getRoleName().toLowerCase());
    }*/

    public boolean isAreaManager() {
        return !TextUtils.isEmpty(getRoleName()) &&
               Constants.ROLE_NAME_AM.equals(getRoleName().toLowerCase());
    }

    public boolean isSales() {
        return !TextUtils.isEmpty(getRoleName()) &&
               Constants.ROLE_NAME_SALES.equals(getRoleName().toLowerCase());
    }
}
