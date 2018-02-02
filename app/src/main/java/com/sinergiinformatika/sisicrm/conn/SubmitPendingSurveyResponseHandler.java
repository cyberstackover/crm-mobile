package com.sinergiinformatika.sisicrm.conn;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wendi on 16-Jan-15.
 */
public class SubmitPendingSurveyResponseHandler extends JsonHttpResponseHandler {

    private static final String TAG = SubmitPendingSurveyResponseHandler.class.getSimpleName();

    private Context context;
    private SurveyTable surveyTable;
    private int id;
    private boolean canProcess = false;
    private Map<Integer, Survey> surveyProcessing = new HashMap<Integer, Survey>(0);

    public SubmitPendingSurveyResponseHandler(Context context, Map<Integer, Survey> surveyProcessing, int id) {
        this.context = context;
        this.surveyTable = new SurveyTable(context);
        this.surveyProcessing = surveyProcessing;
        this.id = id;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        if(Constants.DEBUG){
            Log.d(TAG, "response = " + response.toString());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonResponse<Survey> resp = objectMapper.readValue(response.toString(), new TypeReference<JsonResponse<Survey>>() {
            });
            if (resp.isSuccess()) {
                Survey survey = surveyProcessing.get(Integer.valueOf(id));
                if(survey != null){
                    surveyTable.sent(id, resp.getData().getSurveyId());
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            syncFailure();
        }

        remove(id);
    }


    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        syncFailure();
        remove(id);
        Log.e(TAG, "error status code: " + statusCode);
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        syncFailure();
        remove(id);
        Log.e(TAG, "error status code: " + statusCode);
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        syncFailure();
        remove(id);
        Log.e(TAG, "error status code: " + statusCode);
        super.onFailure(statusCode, headers, responseString, throwable);
    }

    @Override
    public void onFinish() {
    }


    @Override
    public void onStart() {
        Log.d(TAG, "sending...");
        surveyTable.sending(id);
    }


    public void remove(int id){

        if(surveyProcessing == null
                || surveyProcessing.isEmpty()
                || !surveyProcessing.containsKey(Integer.valueOf(id))){
            return;
        }

        surveyProcessing.remove(id);

    }

    private void syncFailure(){
        surveyTable.failed(id);
    }

}
