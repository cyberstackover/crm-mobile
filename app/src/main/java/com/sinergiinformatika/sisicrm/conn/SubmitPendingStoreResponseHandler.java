package com.sinergiinformatika.sisicrm.conn;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wendi on 16-Feb-15.
 */
public class SubmitPendingStoreResponseHandler extends JsonHttpResponseHandler {

    private static final String TAG = SubmitPendingStoreResponseHandler.class.getSimpleName();

    private Context context;
    private StoreTable storeTable;
    private int id;
    private boolean canProcess = false;
    private Map<Integer, Store> storeProcessing = new HashMap<Integer, Store>(0);

    public SubmitPendingStoreResponseHandler(Context context, Map<Integer, Store> surveyProcessing, int id) {
        this.context = context;
        this.storeTable = new StoreTable(context);
        this.storeProcessing = surveyProcessing;
        this.id = id;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        if (Constants.DEBUG) {
            Log.d(TAG, "response store: " + response.toString());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonResponse<Store> resp = objectMapper.readValue(response.toString(), new TypeReference<JsonResponse<Store>>() {
            });
            if (resp.isSuccess()) {

                Store store = storeProcessing.get(Integer.valueOf(id));
                if (store != null) {
                    storeTable.sent(id, resp.getData().getStoreId());

                    Intent intent = new Intent(Constants.DATA_UPLOADED_INTENT_ACTION);
                    intent.putExtra(Constants.EXTRA_STORE_DB_ID, id);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        syncFailure();
        remove(id);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        syncFailure();
        remove(id);
    }

    @Override
    public void onFinish() {
    }


    @Override
    public void onStart() {
        storeTable.sending(id);
    }


    public void remove(int id) {

        if (storeProcessing == null
                || storeProcessing.isEmpty()
                || !storeProcessing.containsKey(Integer.valueOf(id))) {
            return;
        }

        storeProcessing.remove(id);

    }

    private void syncFailure() {
        storeTable.failed(id);
    }

}
