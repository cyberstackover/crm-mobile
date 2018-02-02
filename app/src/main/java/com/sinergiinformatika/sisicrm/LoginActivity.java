package com.sinergiinformatika.sisicrm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.City;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.models.Province;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.Subdistrict;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.CityTable;
import com.sinergiinformatika.sisicrm.db.tables.CompetitorTable;
import com.sinergiinformatika.sisicrm.db.tables.ComplainTable;
import com.sinergiinformatika.sisicrm.db.tables.DistributorTable;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;
import com.sinergiinformatika.sisicrm.db.tables.ProvinceTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SubdistrictTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.exceptions.HashGenerationException;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.Hasher;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.ServerErrorUtil;
import com.sinergiinformatika.sisicrm.utils.SyncUtil;
import com.sinergiinformatika.sisicrm.utils.TypeFaceModifier;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean running = false;
    private String previousUserId;
    private boolean isSyncError = false;
    private ProvinceTable mProvinceTable;
    private CityTable mCityTable;
    private SubdistrictTable mSubdistrictTable;
    private ComplainTable mComplainTable;
    private ProductTable mProductTable;
    private StoreTable mStoreTable;
    private SurveyTable mSurveyTable;
    private JsonHttpResponseHandler surveyListHandler =
            new RestResponseHandler(LoginActivity.this) {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {

                        if (Constants.DEBUG) {
                            Log.d(TAG, "survey response = " + response.toString());
                        }

                        if (isSuccess(response)) {

                            getContentResolver().delete(
                                    CRMContentProvider.URI_SURVEY,
                                    SurveyTable.COLUMN_SYNC_STATUS + " = ?",
                                    new String[]{Constants.SYNC_STATUS_SENT});

                            JSONObject dataObj = getData(response);
                            JSONArray data = dataObj.getJSONArray("list");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datum = data.getJSONObject(i);
                                Survey s = new Survey();
                                Parser.setValues(datum, s);
                                mSurveyTable.syncInsert(s);
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "survey response = " + response.toString());
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                }
            };
    private OrderTable mOrderTable;
    private JsonHttpResponseHandler orderListHandler = new RestResponseHandler(LoginActivity.this) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            try {

                if (Constants.DEBUG) {
                    Log.d(TAG, "order response = " + response.toString());
                }

                if (isSuccess(response)) {
                    getContentResolver().delete(
                            CRMContentProvider.URI_ORDERS,
                            OrderTable.COLUMN_SYNC_STATUS + " = ?",
                            new String[]{Constants.SYNC_STATUS_SENT});

                    JSONObject dataObj = getData(response);
                    JSONArray data = dataObj.getJSONArray("list");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datum = data.getJSONObject(i);
                        Order o = new Order();
                        Parser.setValues(datum, o);
                        mOrderTable.save(o);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "order response = " + response.toString());
                Log.e(TAG, e.getMessage(), e);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONObject errorResponse) {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONArray errorResponse) {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onFinish() {
        }
    };
    private CompetitorTable mCompetitorTable;
    private ProgressDialog mProgressBar;
    private BroadcastReceiver dataDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.DEBUG) Log.i(TAG, "data master downloading..");

            isSyncError = intent.getBooleanExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, false);

            Log.d(TAG, "isSyncError = " + isSyncError);
            if (isSyncError) {
                RestClient.cancelRequests(LoginActivity.this);
                User.getInstance(LoginActivity.this).signOut();
                mProgressBar.dismiss();

                String errorMessage = intent.getStringExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA);

                if ("".equals(errorMessage)) {
                    errorMessage = "Gagal mengunduh data";
                }
                Log.d(TAG, "errorMessage = " + errorMessage);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                int counter = intent.getIntExtra(Constants.PROGRESS_COUNTER_ID_EXTRA, 0);
                if (Constants.DEBUG) {
                    Log.d(TAG, "counter = " + counter);
                }

                try {
                    mProgressBar.setProgress(counter);

                    if (counter >= Constants.PROGRESS_STATUS_FINISH) {
                        mProgressBar.dismiss();
                        invokeMainApp();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    };
    private int mProgressCounter = 0;
    JsonHttpResponseHandler storeListHandler = new RestResponseHandler(LoginActivity.this) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            try {

                if (Constants.DEBUG) {
                    Log.d(TAG, "store response = " + response.toString(4));
                }

                if (isSuccess(response)) {

                    JSONObject dataObj = getData(response);
                    JSONArray data = dataObj.getJSONArray("list");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datum = data.getJSONObject(i);
                        Store s = new Store();
                        Parser.setValues(datum, s);
                        mStoreTable.syncInsert(s);
                    }

                    sendProgressStatus();
                } else {
                    int errCode = getErrorCode(response);
                    if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                        throw new Exception(getString(R.string.error_no_store));
                    } else {
                        throw new Exception(getString(R.string.error_download_store));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "store response = " + response.toString());
                Log.e(TAG, e.getMessage(), e);
                storeSyncFailure();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONArray errorResponse) {
            storeSyncFailure();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            storeSyncFailure();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONObject errorResponse) {
            storeSyncFailure();
        }

    };
    private JsonHttpResponseHandler provinceResponseHandler =
            new RestResponseHandler(LoginActivity.this) {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (Constants.DEBUG) {
                        Log.d(TAG, "province response = " + response.toString());
                    }

                    try {

                        if (isSuccess(response)) {

                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datum = data.getJSONObject(i);
                                Province p = new Province();
                                Parser.setValues(datum, p);
                                mProvinceTable.insert(p);
                            }

                            sendProgressStatus();
                        } else {
                            int errCode = getErrorCode(response);
                            if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                                throw new Exception(getString(R.string.error_no_province));
                            } else {
                                throw new Exception(getString(R.string.error_download_province));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "province response = " + response.toString());
                        Log.e(TAG, e.getMessage(), e);
                        provinceSyncFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    provinceSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    provinceSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                    provinceSyncFailure();
                }
            };
    private RestResponseHandler cityResponseHandler = new RestResponseHandler(LoginActivity.this) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (Constants.DEBUG) {
                Log.d(TAG, "city response = " + response.toString());
            }

            try {

                if (isSuccess(response)) {

                    JSONArray data = response.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datum = data.getJSONObject(i);
                        City c = new City();
                        Parser.setValues(datum, c);
                        mCityTable.insert(c);
                    }

                    sendProgressStatus();
                } else {
                    int errCode = getErrorCode(response);
                    if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                        throw new Exception(getString(R.string.error_no_city));
                    } else {
                        throw new Exception(getString(R.string.error_download_city));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "city response = " + response.toString());
                Log.e(TAG, e.getMessage(), e);
                citySyncFailure();
            }
        }


        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONObject errorResponse) {
            citySyncFailure();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            citySyncFailure();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONArray errorResponse) {
            citySyncFailure();
        }
    };
    private RestResponseHandler subdistrictResponseHandler =
            new RestResponseHandler(LoginActivity.this) {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (Constants.DEBUG) {
                        Log.d(TAG, "response = " + response.toString());
                    }

                    try {

                        if (isSuccess(response)) {

                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datum = data.getJSONObject(i);
                                Subdistrict c = new Subdistrict();
                                Parser.setValues(datum, c);
                                mSubdistrictTable.insert(c);
                            }

                            sendProgressStatus();
                        } else {
                            int errCode = getErrorCode(response);
                            if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                                throw new Exception(getString(R.string.error_no_subdistrict));
                            } else {
                                throw new Exception(getString(R.string.error_download_subdistrict));
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "subdistrict response = " + response.toString());
                        Log.e(TAG, e.getMessage(), e);
                        subdistrictSyncFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    subdistrictSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    subdistrictSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                    subdistrictSyncFailure();
                }
            };
    private JsonHttpResponseHandler complainListHandler =
            new RestResponseHandler(LoginActivity.this) {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        if (isSuccess(response)) {

                            JSONObject dataObj = getData(response);
                            JSONArray data = dataObj.getJSONArray("list");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datum = data.getJSONObject(i);
                                ItemComplain c = new ItemComplain();
                                Parser.setValues(datum, c);
                                mComplainTable.sync(c);
                            }

                            sendProgressStatus();
                        } else {
                            int errCode = getErrorCode(response);
                            if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                                throw new Exception(getString(R.string.error_no_complain_list));
                            } else {
                                throw new Exception(getString(R.string.error_download_complain));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "complain response = " + response.toString());
                        Log.e(TAG, e.getMessage(), e);
                        complainSyncFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    complainSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    complainSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                    complainSyncFailure();
                }

            };
    private JsonHttpResponseHandler productListHandler =
            new RestResponseHandler(LoginActivity.this) {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {

                        if (Constants.DEBUG) {
                            Log.d(TAG, response.toString(4));
                        }

                        if (isSuccess(response)) {

                            JSONObject dataObj = getData(response);
                            JSONArray data = dataObj.getJSONArray("list");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datum = data.getJSONObject(i);
                                ItemPrice ip = new ItemPrice();
                                Parser.setValues(datum, ip);
                                mProductTable.sync(ip);
                            }

                            sendProgressStatus();
                        } else {
                            int errCode = getErrorCode(response);
                            if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                                throw new Exception(getString(R.string.error_no_product_found));
                            } else {
                                throw new Exception(getString(R.string.error_download_product));
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "product response = " + response.toString());
                        Log.e(TAG, e.getMessage(), e);
                        productSyncFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    productSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    productSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                    productSyncFailure();
                }
            };
    private RestResponseHandler distributorResponseHandler =
            new RestResponseHandler(LoginActivity.this) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {

                        if (Constants.DEBUG) {
                            Log.d(TAG, response.toString(4));
                        }

                        if (isSuccess(response)) {

                            JSONObject dataObj = getData(response);
                            JSONArray data = dataObj.getJSONArray("list");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datum = data.getJSONObject(i);
                                Distributor d = new Distributor();

                                Parser.setValues(datum, d);

                                ContentValues values = new ContentValues();
                                DistributorTable.setValues(d, values);

                                getContentResolver()
                                        .insert(CRMContentProvider.URI_DISTRIBUTOR, values);
                            }

                            sendProgressStatus();
                        } else {
                            int errCode = getErrorCode(response);
                            if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                                throw new Exception(getString(R.string.error_no_distributor));
                            } else {
                                throw new Exception(getString(R.string.error_download_distributor));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "distributor response = " + response.toString());
                        Log.e(TAG, e.getMessage(), e);
                        distributorSyncFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    distributorSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    distributorSyncFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                    distributorSyncFailure();
                }
            };
    private JsonHttpResponseHandler competitorProgramsHandler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if (Constants.DEBUG) {
                    Log.d(TAG, "competitor programs response = " + response.toString(4));
                }
                if (RestResponseHandler.isSuccess(response)) {
                    JSONObject data = RestResponseHandler.getData(response);
                    JSONArray arrProgramList = data.getJSONArray("list");
                    List<ItemCompetitor> itemCompetitors =
                            Parser.jsonArrayToProgramList(arrProgramList);

                    mCompetitorTable.sync(itemCompetitors);

                    sendProgressStatus();
                } else {
                    int errCode = RestResponseHandler.getErrorCode(response);
                    if (errCode == Constants.ERROR_CODE_DATA_NOT_FOUND) {
                        throw new Exception(getString(R.string.error_no_programs));
                    } else {
                        throw new Exception(getString(R.string.error_download_promotion_programs));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "competitor programs response = " + response.toString());
                Log.e(TAG, e.getMessage(), e);
                competitorSyncFailure();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                              JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            competitorSyncFailure();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            competitorSyncFailure();
        }

        private void competitorSyncFailure() {
            Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
            intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
            intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                            getString(R.string.error_download_promotion_programs));
            LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(intent);
        }
    };
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private String hashedPassword;
    // Response handler
    private JsonHttpResponseHandler loginResponseHandler = new RestResponseHandler(this) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if (Constants.DEBUG) Log.d(TAG, response.toString(4));

                if (isSuccess(response)) {
                    SyncUtil.stopSync();

                    JSONObject data = getData(response);

                    if (areaValid(data)) {
                        String userId = data.getString(Constants.JSON_KEY_USER_ID);

                        boolean download = false;

                        if (previousUserId == null) {
                            download = true;
                        } else if (!previousUserId.equals(userId)) {
                            getContentResolver().delete(CRMContentProvider.URI_AGENDA, null, null);
                            getContentResolver()
                                    .delete(CRMContentProvider.URI_COMPLAIN, null, null);
                            getContentResolver()
                                    .delete(CRMContentProvider.URI_DISTRIBUTOR, null, null);
                            getContentResolver().delete(CRMContentProvider.URI_PRODUCT, null, null);
                            getContentResolver().delete(CRMContentProvider.URI_STORES, null, null);
                            getContentResolver()
                                    .delete(CRMContentProvider.URI_SUBDISTRICT, null, null);
                            getContentResolver().delete(CRMContentProvider.URI_CITY, null, null);
                            getContentResolver()
                                    .delete(CRMContentProvider.URI_PROVINCE, null, null);
                            getContentResolver().delete(CRMContentProvider.URI_SURVEY, null, null);
                            getContentResolver().delete(CRMContentProvider.URI_ORDERS, null, null);
                            getContentResolver()
                                    .delete(CRMContentProvider.URI_COMPETITORS, null, null);
                            getContentResolver().delete(CRMContentProvider.URI_NOTES, null, null);
                            download = true;
                        } else if (userId.equals(previousUserId) && isSyncError) {
                            download = true;
                        }

                        User.getInstance(LoginActivity.this).signIn(data);
                        User.getInstance(LoginActivity.this).setKey(hashedPassword);

                        String token = User.getInstance(LoginActivity.this).getToken();


                        Date startDate = DateUtil.daysAgo(Constants.HISTORY_DAYS_AGO_FOR_SALES);
                        if (User.getInstance(LoginActivity.this).isAreaManager()) {
                            startDate =
                                    DateUtil.daysAgo(Constants.HISTORY_DAYS_AGO_FOR_AREA_MANAGER);
                        }

                        RestClient.getInstance(LoginActivity.this, surveyListHandler)
                                  .getSurveyList(token, userId, startDate, DateUtil.now(), null,
                                                 null, null);
                        RestClient.getInstance(LoginActivity.this, orderListHandler)
                                  .getOrderList(token, userId, startDate, DateUtil.now(), null,
                                                null, null);

                        if (download) {
                            mProgressBar = new ProgressDialog(LoginActivity.this);
                            mProgressBar.setCancelable(true);
                            mProgressBar.setMessage(getString(R.string.message_downloading));
                            mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressBar.setProgress(Constants.PROGRESS_STATUS_START);
                            mProgressBar.setMax(Constants.PROGRESS_STATUS_FINISH);
                            mProgressBar.show();

                            mProvinceTable = new ProvinceTable(LoginActivity.this);
                            mCityTable = new CityTable(LoginActivity.this);
                            mSubdistrictTable = new SubdistrictTable(LoginActivity.this);
                            mComplainTable = new ComplainTable(LoginActivity.this);
                            mProductTable = new ProductTable(LoginActivity.this);
                            mStoreTable = new StoreTable(LoginActivity.this);
                            mCompetitorTable = new CompetitorTable(LoginActivity.this);

                            String areaId = User.getInstance(LoginActivity.this).getAreaId();

                            RestClient.getInstance(LoginActivity.this, provinceResponseHandler)
                                      .getProvinceList(token, areaId);
                            RestClient.getInstance(LoginActivity.this, cityResponseHandler)
                                      .getCityList(token, areaId);
                            RestClient.getInstance(LoginActivity.this, subdistrictResponseHandler)
                                      .getSubdistrictList(token, areaId);
                            RestClient.getInstance(LoginActivity.this, complainListHandler)
                                      .getComplainList(token);
                            RestClient.getInstance(LoginActivity.this, distributorResponseHandler)
                                      .getStoreList(token, Constants.STORE_TYPE_CUSTOMER);
                            RestClient.getInstance(LoginActivity.this, productListHandler)
                                      .getProductList(token);
                            RestClient.getInstance(LoginActivity.this, storeListHandler)
                                      .getStoreList(token, Constants.STORE_TYPE_RESELLER, null,
                                                    true);
                            RestClient.getInstance(LoginActivity.this, competitorProgramsHandler)
                                      .getCompetitorPrograms(token);
                        } else {
                            invokeMainApp();
                        }
                    }
                } else {
                    int errorCode = getErrorCode(response);
                    String errorMsg =
                            ServerErrorUtil.getErrorMessage(LoginActivity.this, errorCode);

                    if (errorMsg == null) {
                        errorMsg = getErrorMessage(response);
                    }

                    if (errorMsg == null) {
                        errorMsg = getString(R.string.error_invalid_response);
                    }

                    mUsernameView.requestFocus();

                    throw new IllegalArgumentException(errorMsg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);

                String message = LoginActivity.this.getString(R.string.error_unknown);
                if (e instanceof IllegalArgumentException) {
                    IllegalArgumentException exc = (IllegalArgumentException) e;
                    message = exc.getMessage();
                }

                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            showProgress(true);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            showProgress(false);
            running = false;
            mEmailSignInButton.setText(getString(R.string.action_sign_in_short));
        }
    };

    private boolean areaValid(JSONObject data) {
        if (data.isNull("area")) {
            Toast.makeText(LoginActivity.this, getString(R.string.error_no_area_assignment),
                           Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

        /*try {
            JSONObject jsonObject = data.getJSONObject("area");
        } catch (JSONException e) {
            Toast.makeText(LoginActivity.this, getString(R.string.error_no_area_assignment),
                           Toast.LENGTH_LONG).show();
            return false;
        }

        return true;*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);
        TypeFaceModifier.overrideWithFontAwesome(getApplicationContext(), "SERIF");
        setContentView(R.layout.activity_login);

        isSyncError = false;
        previousUserId = User.getInstance(this).getUserId();
        mSurveyTable = new SurveyTable(this);
        mOrderTable = new OrderTable(getContentResolver());

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mPasswordView.setTypeface(Typeface.SERIF);
        mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL || keyEvent == null
                    || keyEvent.getKeyCode() == EditorInfo.IME_ACTION_SEND) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void loginClick(View view) {
        mProgressCounter = 0;
        attemptLogin();
    }

    public void forgotPasswordClick(View view) {
        Toast.makeText(this, "Unimplemented", Toast.LENGTH_SHORT).show();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (running) {
            RestClient.cancelRequests(this);
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Kick off a background task to perform the user login attempt.
            showProgress(true);
            hashedPassword = null;
            try {
                hashedPassword = Hasher.generateMD5(password);
            } catch (HashGenerationException e) {
                e.printStackTrace();
                Log.e(TAG, getString(R.string.error_hash_failed) + e.getMessage(), e);
            }

            if (hashedPassword != null) {
                running = true;
                mEmailSignInButton.setText(getString(R.string.action_cancel));

                RestClient.getInstance(this, loginResponseHandler)
                          .postLogin(username, hashedPassword);
            } else {
                Toast.makeText(
                        this, getString(R.string.error_hash_failed), Toast.LENGTH_LONG)
                     .show();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= Constants.PASSWORD_MIN_LENGTH;
    }

    /**
     * Shows the mProgressCounter UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the mProgressCounter spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? Constants.FLAG_FALSE : Constants.FLAG_TRUE)
                          .setListener(new AnimatorListenerAdapter() {
                              @Override
                              public void onAnimationEnd(Animator animation) {
                                  mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                              }
                          });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? Constants.FLAG_TRUE : Constants.FLAG_FALSE)
                         .setListener(new AnimatorListenerAdapter() {
                             @Override
                             public void onAnimationEnd(Animator animation) {
                                 mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                             }
                         });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void invokeMainApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                dataDownloadReceiver, new IntentFilter(Constants.DATA_DOWNLOADED_INTENT_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataDownloadReceiver);
    }

    private void sendProgressStatus() {
        mProgressCounter++;
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.PROGRESS_COUNTER_ID_EXTRA, mProgressCounter);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void provinceSyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_province));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void citySyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_city));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void subdistrictSyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_subdistrict));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void storeSyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_store));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void distributorSyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_distributor));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void productSyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_product));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void complainSyncFailure() {
        Intent intent = new Intent(Constants.DATA_DOWNLOADED_INTENT_ACTION);
        intent.putExtra(Constants.DOWNLOAD_ERROR_ID_EXTRA, true);
        intent.putExtra(Constants.DOWNLOAD_ERROR_MESSAGE_EXTRA,
                        getString(R.string.error_download_complain));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
