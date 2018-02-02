package com.sinergiinformatika.sisicrm.adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.BuildConfig;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.MainActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.models.City;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.models.Province;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.Subdistrict;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
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
import com.sinergiinformatika.sisicrm.utils.Calculator;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.MiscUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Mark on 2/17/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();
    private Context mContext;
    private ContentResolver mContentResolver;
    private int pushStoreCounter;
    private int pushSurveyCounter;
    private int pushOrderCounter;
    private int pullStoreCounter;
    private int pullProvinceCounter;
    private int pullCityCounter;
    private int pullSubDistrictCounter;
    private int pullComplainCounter;
    private int pullProductCounter;
    private int pullProgramsCounter;
    private boolean canceled;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context.getApplicationContext();
        mContentResolver = context.getContentResolver();
    }

    @SuppressWarnings("unused")
    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContext = context.getApplicationContext();
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle bundle,
                              String authority,
                              ContentProviderClient providerClient,
                              SyncResult syncResult) {

        Log.i("dsdsds", "bundle: " + bundle);
        Log.i("dsdsds", "bundle bool: " + bundle.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false));
        if (bundle != null && bundle.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false)) {
            canceled = false;
        }

        if (canceled) {
            return;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, mContext.MODE_PRIVATE);
        boolean syncInProgress = prefs.getBoolean("syncInProgress", false);
        boolean pushInProgress = prefs.getBoolean("pushInProgress", false);
        if(syncInProgress || pushInProgress){
            return;
        }

        if (Constants.DEBUG) Log.i(TAG, "sync performed");
        Log.i(TAG, "token: " + User.getInstance(mContext).getToken());

        Intent intent = new Intent(Constants.INTENT_ACTION_SYNC);
        intent.putExtra(Constants.EXTRA_SYNC_PROGRESS, Constants.SYNC_PROGRESS_STARTED);
        mContext.sendBroadcast(intent);

        /*boolean isPeriodic =
                bundle != null && bundle.getBoolean(Constants.SYNC_KEY_PERIODIC, false);*/

        if (!User.getInstance(mContext).getToken().isEmpty()) {
            pushStoreCounter = 0;
            pushSurveyCounter = 0;
            pushOrderCounter = 0;
            pullStoreCounter = 0;
            pullProvinceCounter = 0;
            pullCityCounter = 0;
            pullSubDistrictCounter = 0;
            pullComplainCounter = 0;
            pullProductCounter = 0;
            pullProgramsCounter = 0;

            pushPendingStores();
            pushPendingSurvey();
            pushPendingSurveyPhotos();
            pushOrders();
            pullStores();
            pullAreas();
            pullCompetitorPrograms();
            pullProducts();
            pullComplains();
        }

        /*if (isPeriodic) {
            pullStores();
        }*/

        NotificationCompat.InboxStyle inboxStyle = null;

        if (pushStoreCounter > 0) {
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pushStoreCounter + " data toko berhasil dikirim ke server.");
        }
        if (pullStoreCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pullStoreCounter + " data toko berhasil di-update.");
        }
        if (pushSurveyCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pushSurveyCounter + " data survey berhasil dikirim ke server.");
        }
        if (pushOrderCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pushOrderCounter + " order berhasil dikirim ke server.");
        }
        if (pullProvinceCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pullProvinceCounter + " provinsi berhasil di-update.");
        }
        if (pullCityCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pullCityCounter + " kota berhasil di-update.");
        }
        if (pullSubDistrictCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pullSubDistrictCounter + " kecamatan berhasil di-update.");
        }
        if (pullProductCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pullProductCounter + " data master produk berhasil di-update.");
        }
        if (pullProgramsCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(
                    pullProgramsCounter + " data master program promosi berhasil di-update.");
        }
        if (pullComplainCounter > 0) {
            if (inboxStyle == null) inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(pullComplainCounter + " data master keluhan berhasil di-update.");
        }

        if (inboxStyle != null) {
            MiscUtil.showNotificationMessage(mContext,
                                             mContext.getString(R.string.title_sync_completed),
                                             null,
                                             inboxStyle);
        }

        checkLatestVersion();

        intent.putExtra(Constants.EXTRA_SYNC_PROGRESS, Constants.SYNC_PROGRESS_FINISHED);
        mContext.sendBroadcast(intent);

        if (Constants.DEBUG) Log.i(TAG, "sync finished");
    }

    @Override
    public void onSyncCanceled() {
        canceled = true;
        super.onSyncCanceled();
    }

    private void pullComplains() {
        final ComplainTable mComplainTable = new ComplainTable(mContext);
        JsonHttpResponseHandler complainListHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (RestResponseHandler.isSuccess(response)) {

                        JSONObject dataObj = RestResponseHandler.getData(response);
                        JSONArray data = dataObj.getJSONArray("list");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            ItemComplain c = new ItemComplain();
                            Parser.setValues(datum, c);

                            if (!datum.isNull("deleted") && datum.getInt("deleted") == 1) {
                                mComplainTable.delete(c);
                            } else {
                                mComplainTable.sync(c);
                            }

                            pullComplainCounter++;
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);
                        if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            throw new Exception(
                                    mContext.getString(R.string.error_download_complain));
                        }
                    }
                } catch (Exception e) {
                    if (Constants.DEBUG) Log.e(TAG, "complain response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        RestClient.getInstance(mContext, complainListHandler).getComplainList(
                User.getInstance(mContext).getToken(), mComplainTable.getLastSyncDate(), false);
    }

    private void pullProducts() {
        final ProductTable mProductTable = new ProductTable(mContext);
        JsonHttpResponseHandler productListHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    if (Constants.DEBUG) {
                        Log.d(TAG, response.toString(4));
                    }

                    if (RestResponseHandler.isSuccess(response)) {

                        JSONObject dataObj = RestResponseHandler.getData(response);
                        JSONArray data = dataObj.getJSONArray("list");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            ItemPrice ip = new ItemPrice();
                            Parser.setValues(datum, ip);

                            if (!datum.isNull("deleted") && datum.getInt("deleted") == 1) {
                                mProductTable.delete(ip);
                            } else {
                                mProductTable.sync(ip);
                            }

                            pullProductCounter++;
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);
                        if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            throw new Exception(
                                    mContext.getString(R.string.error_download_product));
                        }
                    }

                } catch (Exception e) {
                    if (Constants.DEBUG) Log.e(TAG, "product response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        RestClient.getInstance(mContext, productListHandler).getProductList(
                User.getInstance(mContext).getToken(), mProductTable.getLastSyncDate(), false);
    }

    private void pullCompetitorPrograms() {
        final CompetitorTable mCompetitorTable = new CompetitorTable(mContext);
        JsonHttpResponseHandler competitorProgramsHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (Constants.DEBUG) {
                        Log.d(TAG, "competitor programs response = " + response.toString(4));
                    }
                    if (RestResponseHandler.isSuccess(response)) {
                        JSONObject data = RestResponseHandler.getData(response);
                        JSONArray arrProgramList = data.getJSONArray("list");
                        List<ItemCompetitor> itemCompetitors = new ArrayList<>();

                        for (int i = 0; i < arrProgramList.length(); i++) {
                            JSONObject obj = arrProgramList.getJSONObject(i);
                            ItemCompetitor competitor = Parser.jsonToCompetitorProgram(obj);

                            if (!obj.isNull("deleted") && obj.getInt("deleted") == 1) {
                                if (competitor == null) {
                                    competitor = new ItemCompetitor();
                                    try {
                                        competitor.setProgramId(obj.getString("program_id"));
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                        competitor = null;
                                    }
                                }

                                if (competitor != null) {
                                    mCompetitorTable.delete(competitor);
                                    pullProgramsCounter++;
                                }
                            } else {
                                if (competitor != null) {
                                    itemCompetitors.add(competitor);
                                    pullProgramsCounter++;
                                }
                            }
                        }

                        mCompetitorTable.sync(itemCompetitors);
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);
                        if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            throw new Exception(
                                    mContext.getString(R.string.error_download_promotion_programs));
                        }
                    }
                } catch (Exception e) {
                    if (Constants.DEBUG) {
                        Log.e(TAG, "competitor programs response = " + response.toString());
                    }
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        RestClient.getInstance(mContext, competitorProgramsHandler).getCompetitorPrograms(
                User.getInstance(mContext).getToken(), mCompetitorTable.getLastSyncDate(), false);
    }

    private void pullAreas() {
        final ProvinceTable mProvinceTable = new ProvinceTable(mContext);
        final CityTable mCityTable = new CityTable(mContext);
        final SubdistrictTable mSubdistrictTable = new SubdistrictTable(mContext);
        String areaId = User.getInstance(mContext).getAreaId();
        String token = User.getInstance(mContext).getToken();

        JsonHttpResponseHandler provinceResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (Constants.DEBUG) {
                    Log.d(TAG, "province response = " + response.toString());
                }

                try {

                    if (RestResponseHandler.isSuccess(response)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> deletedIds = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);

                            boolean deleted = !datum.isNull("deleted") &&
                                              datum.getInt("deleted") == 1;

                            if (deleted) {
                                deletedIds.add(datum.getString("id"));
                            } else {
                                Province p = new Province();
                                Parser.setValues(datum, p);
                                p.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));
                                mProvinceTable.save(p);
                            }

                            pullProvinceCounter++;
                        }

                        if (deletedIds.size() > 0) {
                            String[] ids = new String[deletedIds.size()];
                            deletedIds.toArray(ids);
                            mProvinceTable.deleteByAreaId(ids);
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);

                        if (errCode == Constants.ERROR_CODE_SESSION_EXPIRED) {
                            User.rePostLogin(mContext);
                        } else if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            String errMsg = RestResponseHandler.getErrorMessage(response);
                            if (TextUtils.isEmpty(errMsg)) {
                                errMsg = mContext.getString(R.string.error_download_province);
                            }

                            throw new Exception(errMsg);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "province response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        JsonHttpResponseHandler cityResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (Constants.DEBUG) {
                    Log.d(TAG, "city response = " + response.toString());
                }

                try {

                    if (RestResponseHandler.isSuccess(response)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> deletedIds = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);

                            boolean deleted = !datum.isNull("deleted") &&
                                              datum.getInt("deleted") == 1;

                            if (deleted) {
                                deletedIds.add(datum.getString("id"));
                            } else {
                                City c = new City();
                                Parser.setValues(datum, c);
                                c.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));
                                mCityTable.save(c);
                            }

                            pullCityCounter++;
                        }

                        if (deletedIds.size() > 0) {
                            String[] ids = new String[deletedIds.size()];
                            deletedIds.toArray(ids);
                            mCityTable.deleteByAreaId(ids);
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);

                        if (errCode == Constants.ERROR_CODE_SESSION_EXPIRED) {
                            User.rePostLogin(mContext);
                        } else if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            String errMsg = RestResponseHandler.getErrorMessage(response);
                            if (TextUtils.isEmpty(errMsg)) {
                                errMsg = mContext.getString(R.string.error_download_city);
                            }

                            throw new Exception(errMsg);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "city response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        JsonHttpResponseHandler subdistrictResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (Constants.DEBUG) {
                    Log.d(TAG, "subdistrict response = " + response.toString());
                }

                try {

                    if (RestResponseHandler.isSuccess(response)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> deletedIds = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);

                            boolean deleted = !datum.isNull("deleted") &&
                                              datum.getInt("deleted") == 1;

                            if (deleted) {
                                deletedIds.add(datum.getString("id"));
                            } else {
                                Subdistrict c = new Subdistrict();
                                Parser.setValues(datum, c);
                                c.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));
                                mSubdistrictTable.save(c);
                            }

                            pullSubDistrictCounter++;
                        }

                        if (deletedIds.size() > 0) {
                            String[] ids = new String[deletedIds.size()];
                            deletedIds.toArray(ids);
                            mSubdistrictTable.deleteByAreaId(ids);
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);

                        if (errCode == Constants.ERROR_CODE_SESSION_EXPIRED) {
                            User.rePostLogin(mContext);
                        } else if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            String errMsg = RestResponseHandler.getErrorMessage(response);
                            if (TextUtils.isEmpty(errMsg)) {
                                errMsg = mContext.getString(R.string.error_download_subdistrict);
                            }

                            throw new Exception(errMsg);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "subdistrict response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        String lastProvinceUpdated = mProvinceTable.getLastSyncDate();
        String lastCityUpdated = mCityTable.getLastSyncDate();
        String lastSubdistrictUpdated = mSubdistrictTable.getLastSyncDate();

        RestClient.getInstance(mContext, provinceResponseHandler)
                  .getProvinceList(token, areaId, lastProvinceUpdated, false);
        RestClient.getInstance(mContext, cityResponseHandler)
                  .getCityList(token, areaId, lastCityUpdated, false);
        RestClient.getInstance(mContext, subdistrictResponseHandler)
                  .getSubdistrictList(token, areaId, lastSubdistrictUpdated, false);
    }

    private void checkLatestVersion() {
        RestClient.getInstance(mContext, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (RestResponseHandler.isSuccess(response)) {
                        JSONObject data = RestResponseHandler.getData(response);
                        String version = data.getString("version");

                        if (MiscUtil.versionCompare(version, BuildConfig.VERSION_NAME) > 0) {
                            MiscUtil.showNotificationMessage(
                                    mContext,
                                    mContext.getString(R.string.title_notif_app_update),
                                    mContext.getString(R.string.message_app_update, version),
                                    null);
                        }
                    } else {
                        throw new IllegalArgumentException("Response status error");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "response: " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).getLatestVersion(false);
    }

    private void pullStores() {
        JsonHttpResponseHandler storeListHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (Constants.DEBUG) {
                        Log.d(TAG, "store response = " + response.toString(4));
                    }

                    if (RestResponseHandler.isSuccess(response)) {
                        JSONObject dataObj = RestResponseHandler.getData(response);
                        JSONArray data = dataObj.getJSONArray("list");
                        StoreTable mStoreTable = new StoreTable(mContentResolver);
                        List<String> deletedStoreIds = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            Store s = new Store();
                            Parser.setValues(datum, s);

                            if (s.getDeleted() < 1) {
                                s.setSyncStatus(Constants.SYNC_STATUS_SENT);
                                mStoreTable.syncSave(s);
                            } else {
                                deletedStoreIds.add(s.getStoreId());
                            }

                            pullStoreCounter++;
                        }

                        if (deletedStoreIds.size() > 0) {
                            String[] ids = new String[deletedStoreIds.size()];
                            deletedStoreIds.toArray(ids);

                            mStoreTable.deleteByStoreId(ids);
                            (new SurveyTable(mContentResolver)).deleteByStoreId(ids);
                            (new OrderTable(mContentResolver)).deleteByStoreId(ids);
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);

                        if (errCode == Constants.ERROR_CODE_SESSION_EXPIRED) {
                            User.rePostLogin(mContext);
                        } else if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            String errMsg = RestResponseHandler.getErrorMessage(response);
                            if (TextUtils.isEmpty(errMsg)) {
                                errMsg = mContext.getString(R.string.error_download_store);
                            }

                            throw new Exception(errMsg);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "store response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }

            }
        };
        JsonHttpResponseHandler distributorListHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (Constants.DEBUG) {
                        Log.d(TAG, "distributor response = " + response.toString(4));
                    }

                    if (RestResponseHandler.isSuccess(response)) {
                        JSONObject dataObj = RestResponseHandler.getData(response);
                        JSONArray data = dataObj.getJSONArray("list");
                        DistributorTable mDistributorTable = new DistributorTable(mContentResolver);
                        List<String> deletedStoreIds = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            boolean deleted =
                                    !datum.isNull("deleted") && datum.getInt("deleted") == 1;
                            Distributor d = new Distributor();
                            Parser.setValues(datum, d);

                            if (!deleted) {
                                mDistributorTable.save(d);
                            } else {
                                deletedStoreIds.add(d.getId());
                            }
                        }

                        if (deletedStoreIds.size() > 0) {
                            String[] ids = new String[deletedStoreIds.size()];
                            deletedStoreIds.toArray(ids);

                            mDistributorTable.deleteByDistributorIds(ids);
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);

                        if (errCode == Constants.ERROR_CODE_SESSION_EXPIRED) {
                            User.rePostLogin(mContext);
                        } else if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            String errMsg = RestResponseHandler.getErrorMessage(response);
                            if (TextUtils.isEmpty(errMsg)) {
                                errMsg = mContext.getString(R.string.error_download_distributor);
                            }

                            throw new Exception(errMsg);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "distributor response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }

            }
        };

        String lastSyncDate = (new StoreTable(mContentResolver)).getLastSyncDate();
        RestClient.getInstance(mContext, storeListHandler).getStoreList(
                User.getInstance(mContext).getToken(), Constants.STORE_TYPE_RESELLER, null,
                lastSyncDate, false);

        if (!TextUtils.isEmpty(User.getInstance(mContext).getRoleName()) &&
            User.getInstance(mContext).getRoleName().equalsIgnoreCase(Constants.ROLE_NAME_AM)) {
            lastSyncDate = (new DistributorTable(mContentResolver)).getLastSyncDate();
            RestClient.getInstance(mContext, distributorListHandler).getStoreList(
                    User.getInstance(mContext).getToken(), Constants.STORE_TYPE_CUSTOMER, null,
                    lastSyncDate, false);
        }
    }

    private void pushOrders() {
        List<Order> pendingOrders = getPendingOrders();
        if (pendingOrders != null && !pendingOrders.isEmpty()) {
            uploadPendingOrders(pendingOrders);
        }
    }

    private List<Order> getPendingOrders() {
        if (Constants.DEBUG) Log.d(TAG, "looking for pending orders");
        OrderTable orderTable = new OrderTable(mContentResolver);
        return orderTable.getPendingOrders();
    }

    private void uploadPendingOrders(List<Order> orders) {
        if (Constants.DEBUG) Log.d(TAG, "sending pending orders");

        String token = User.getInstance(mContext).getToken();

        for (Order order : orders) {

            if (Constants.DEBUG) {
                Log.d(TAG, "pending order id: " + order.getId());
                Log.d(TAG, "store id: " + order.getStoreDbId());
            }

            if (order.getStoreId() == null || order.getStoreId().trim().length() == 0) {
                StoreTable storeTable = new StoreTable(mContentResolver);
                Store store = storeTable.getById(order.getStoreDbId());
                if (store.getStoreId() == null || store.getStoreId().trim().length() == 0) {
                    continue;
                }
                order.setStoreId(store.getStoreId());
            }

            OrderTable orderTable = new OrderTable(mContentResolver);
            orderTable.updateOrderStatus(order.getId(), Constants.SYNC_STATUS_SENDING);

            //HttpClient client = new DefaultHttpClient();
            HttpClient client = createNewHttpClient();
            HttpPost post = new HttpPost(RestClient.generateURL(RestClient.ACTION_URL_ORDER_ADD));

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("session", token));
            params.add(new BasicNameValuePair("store_id", order.getStoreId()));
            params.add(new BasicNameValuePair("distributor_id", order.getDistributorId()));
            params.add(new BasicNameValuePair("date_delivered", order.getDeliveryDate()));
            params.add(new BasicNameValuePair("products", order.getProducts()));

            try {
                post.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(post);
                String respStr = EntityUtils.toString(response.getEntity());

                JSONObject jsonResp = new JSONObject(respStr);
                String status = jsonResp.getString(Constants.JSON_KEY_STATUS);

                if (status.equalsIgnoreCase(Constants.JSON_STATUS_OK)) {
                    String orderId;
                    try {
                        orderId = jsonResp.getJSONObject("data").getString("order_id");
                    } catch (JSONException e) {
                        orderId = "";
                    }

                    orderTable.setSent(order.getId(), orderId);
                    pushOrderCounter++;
                } else {
                    if (User.rePostLogin(mContext)) {
                        orderTable.setPending(order.getId());
                    } else {
                        Log.e(TAG, "server response: " + respStr);
                        throw new Exception("error pushing order: " + order.getId());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                orderTable.setFailed(order.getId());
            }
        }
    }

    /*private void pullOrders() {
        Date startDate = DateUtil.daysAgo(Constants.HISTORY_DAYS_AGO_FOR_SALES);
        if (User.getInstance(mContext).isAreaManager()) {
            startDate = DateUtil.daysAgo(Constants.HISTORY_DAYS_AGO_FOR_AREA_MANAGER);
        }

        JsonHttpResponseHandler orderListHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    if (Constants.DEBUG) {
                        Log.d(TAG, "order response = " + response.toString());
                    }

                    if (RestResponseHandler.isSuccess(response)) {
                        OrderTable mOrderTable = new OrderTable(mContentResolver);
                        JSONObject dataObj = RestResponseHandler.getData(response);
                        JSONArray data = dataObj.getJSONArray("list");
                        List<String> deletedIds = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            boolean deleted = !datum.isNull("deleted") &&
                                              datum.getInt("deleted") == 1;

                            if (deleted) {
                                deletedIds.add(datum.getString("order_id"));
                            } else {
                                Order o = new Order();
                                Parser.setValues(datum, o);
                                mOrderTable.save(o);
                            }
                        }

                        if (deletedIds.size() > 0) {
                            String[] ids = new String[deletedIds.size()];
                            deletedIds.toArray(ids);

                            mOrderTable.deleteByOrderId(ids);
                        }
                    } else {
                        int errCode = RestResponseHandler.getErrorCode(response);
                        if (errCode != Constants.ERROR_CODE_DATA_NOT_FOUND) {
                            String errMsg = RestResponseHandler.getErrorMessage(response);
                            if (TextUtils.isEmpty(errMsg)) {
                                errMsg = mContext.getString(R.string.error_download_store);
                            }

                            throw new Exception(errMsg);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "order response = " + response.toString());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };

        RestClient.getInstance(mContext, orderListHandler)
                  .getOrderList(User.getInstance(mContext).getToken(),
                                User.getInstance(mContext).getUserId(), startDate, DateUtil.now(),
                                null, null, null, false);
    }*/

    private void pushPendingStores() {

        StoreTable storeTable = new StoreTable(mContentResolver);

        List<Store> stores = storeTable.getPendingStores();

        if (stores == null || stores.isEmpty()) {
            if (Constants.DEBUG) {
                Log.d(TAG, "pending stores empty");
            }
            return;
        }

        for (Store store : stores) {
            String token = User.getInstance(mContext).getToken();

            try {

                storeTable.sending(store.getId());

                //HttpClient client = new DefaultHttpClient();
                HttpClient client = createNewHttpClient();
                HttpPost post;
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                if (!TextUtils.isEmpty(store.getStoreId())) {
                    post = new HttpPost(RestClient.generateURL(RestClient.ACTION_URL_STORE_EDIT));
                    entityBuilder.addTextBody("store_id", store.getStoreId());
                } else {
                    post = new HttpPost(RestClient.generateURL(RestClient.ACTION_URL_STORE_ADD));
                }

                entityBuilder.addTextBody(Constants.TOKEN_KEY, token);
                entityBuilder.addTextBody("store_name", store.getName());
                entityBuilder.addTextBody("store_category_code",
                                          String.valueOf(store.getCategoryCode()));

                if (!TextUtils.isEmpty(store.getDistributorId())) {
                    entityBuilder.addTextBody("distributor_id", store.getDistributorId());
                }

                if (store.getLongitude() != null) {
                    entityBuilder.addTextBody("longitude", String.valueOf(store.getLongitude()));
                }

                if (store.getLatitude() != null) {
                    entityBuilder.addTextBody("latitude", String.valueOf(store.getLatitude()));
                }

                entityBuilder.addTextBody("store_capacity",
                                          String.valueOf(store.getCapacity().intValue()));
                entityBuilder.addTextBody("store_address_province", store.getProvinceId());
                entityBuilder.addTextBody("store_address_city", store.getCityId());
                entityBuilder.addTextBody("store_address_subdistrict", store.getSubdistrictId());
                entityBuilder.addTextBody("store_address_street", store.getStreet());
                entityBuilder.addTextBody("store_zipcode", store.getZipcode());
                entityBuilder.addTextBody("store_phone", store.getPhone());
                entityBuilder.addTextBody("store_owner_full_name", store.getOwnerName());

                //optional
                entityBuilder.addTextBody("nfc_id", store.getNfcId());
                entityBuilder.addTextBody("store_phone_mobile", store.getPhoneMobile());
                entityBuilder.addTextBody("store_owner_birthdate", store.getOwnerBirthDate());
                entityBuilder.addTextBody("store_owner_religion_code",
                                          String.valueOf(store.getOwnerReligionCode()));
                entityBuilder.addTextBody("store_information", store.getInformation());

                if (!TextUtils.isEmpty(store.getCreated())) {
                    entityBuilder.addTextBody("store_created_datetime", store.getCreated());
                } else {
                    entityBuilder.addTextBody("store_created_datetime",
                                              DateUtil.formatDBDateTime(DateUtil.now()));
                }

                try {
                    File f = new File(store.getPhoto());
                    if (f.exists()) {
                        entityBuilder.addBinaryBody("store_photo", f);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                HttpEntity entity = entityBuilder.build();

                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                String respStr = EntityUtils.toString(response.getEntity());
                JSONObject jsonResp = new JSONObject(respStr);

                Log.d("respStr", respStr);

                String status = jsonResp.getString(Constants.JSON_KEY_STATUS);

                if (!Constants.JSON_STATUS_OK.equals(status)) {
                    if (RestResponseHandler.getErrorCode(jsonResp) ==
                        Constants.ERROR_CODE_SESSION_EXPIRED) {
                        User.rePostLogin(mContext);
                        storeTable.pending(store.getId());
                    } else {
                        throw new Exception("Error push pending store: " + store.getName());
                    }
                } else {
                    JSONObject data = RestResponseHandler.getData(jsonResp);
                    String storeId = data.getString("store_id");
                    storeTable.sent(store.getId(), storeId);
                    pushStoreCounter++;
                }
            } catch (Exception e) {

                storeTable.failed(store.getId());

                if (Constants.DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    private void pushPendingSurvey() {

        SurveyTable surveyTable = new SurveyTable(mContext);
        AgendaTable agendaTable = new AgendaTable(mContext);
        StoreTable storeTable = new StoreTable(mContext);

        List<Survey> surveyList = surveyTable.getPendingSurveyList();

        if (surveyList == null || surveyList.isEmpty()) {
            if (Constants.DEBUG) {
                Log.d(TAG, "pending survey list empty");
            }
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        for (Survey survey : surveyList) {
            String token = User.getInstance(mContext).getToken();

            try {


                String storeId = survey.getStoreId();
                String checkOutDateTime = survey.getCheckOut();

                if(TextUtils.isEmpty(storeId)){
                    Agenda agenda = agendaTable.getBySurveyDbId(survey.getId());

                    if (Constants.DEBUG) {
                        Log.d(TAG, "survey id: " + survey.getId());
                        Log.d(TAG, "survey store id: " + survey.getStoreId());
                    }

                    if (agenda != null) {
                        Log.d(TAG, "agenda survey id: " + agenda.getSurveyDbId());
                        Log.d(TAG, "agenda store id: " + agenda.getStoreId());

                        checkOutDateTime = agenda.getCheckOutDateTime();
                        storeId = agenda.getStoreId();

                    }
                }

                //storeId = storeTable.getStoreIdByName(survey.getStoreName());

                boolean doSend = !TextUtils.isEmpty(storeId) && !TextUtils.isEmpty(checkOutDateTime);

                if (doSend) {

                    //set status data = sending
                    surveyTable.sending(survey.getId());
                    surveyTable.dataSending(survey.getId());
                    survey.setStoreId(storeId);

                    HttpClient client = createNewHttpClient();

                    HttpPost post = new HttpPost(RestClient.generateURL(RestClient.ACTION_URL_SURVEY_ADD));

                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    entityBuilder.addTextBody(Constants.TOKEN_KEY, token);

                    entityBuilder.addTextBody("store_id", survey.getStoreId());
                    entityBuilder.addTextBody("check_in_nfc", String.valueOf(survey.isCheckInNfc()));
                    entityBuilder.addTextBody("check_in_time", survey.getCheckIn());
                    entityBuilder.addTextBody("check_in_longitude", String.valueOf(survey.getCheckInLongitude()));
                    entityBuilder.addTextBody("check_in_latitude", String.valueOf(survey.getCheckInLatitude()));
                    entityBuilder.addTextBody("check_out_time", survey.getCheckOut());
                    entityBuilder.addTextBody("check_out_longitude", String.valueOf(survey.getCheckOutLongitude()));
                    entityBuilder.addTextBody("check_out_latitude", String.valueOf(survey.getCheckOutLatitude()));
                    entityBuilder.addTextBody("plan_date", survey.getPlanDate());

                    if (survey.getPlanDate() == null || survey.getPlanDate().trim().length() == 0) {
                        entityBuilder.addTextBody("plan_date", survey.getCheckInTime());
                    }

                    if (survey.getSurveyDate() != null &&
                        survey.getSurveyDate().trim().length() > 0) {
                        entityBuilder.addTextBody("survey_date", survey.getSurveyDate());
                    }

                    if (survey.getSurveyClienDate() != null &&
                        survey.getSurveyClienDate().trim().length() > 0) {
                        entityBuilder.addTextBody("survey_client_date", survey.getSurveyClienDate());
                    }

                    if (survey.getSurveyId() != null && survey.getSurveyId().trim().length() > 0) {
                        entityBuilder.addTextBody("survey_id", survey.getSurveyId());
                    }

                    if (survey.getPrices() != null) {

                        List<Map<String, Object>> products = new ArrayList<>();

                        for (ItemPrice p : survey.getPrices()) {
                            Map<String, Object> product = new HashMap<>();

                            product.put("product_id", p.getProductId());
                            product.put("price", p.getPrice());

                            if (p.getPricePurchase() > 0) {
                                product.put("price_purchase", p.getPricePurchase());
                            }

                            double vol = p.getVolume();
                            if (Constants.UNIT_TON.equals(p.getVolumenUnit())) {
                                vol = Calculator.convert(p.getProductWeight(), vol);
                            }

                            double stock = p.getStock();
                            if (Constants.UNIT_TON.equals(p.getStockUnit())) {
                                stock = Calculator.convert(p.getProductWeight(), stock);
                            }

                            int termOfPayment = p.getTermOfPayment();
                            if (termOfPayment > 0) {
                                product.put("term_of_payment", p.getTermOfPayment());
                            }

                            product.put("volume", vol);
                            product.put("stock", stock);
                            products.add(product);
                        }

                        String productsStr = mapper.writeValueAsString(products);

                        entityBuilder.addTextBody("products", productsStr);
                    }

                    if (survey.getComplains() != null) {

                        List<Map<String, String>> complains = new ArrayList<>();


                        for (ItemComplain com : survey.getComplains()) {
                            Map<String, String> complain = new HashMap<>();

                            complain.put("complain_id", com.getComplainId());
                            complain.put("complain_text", com.getComplain());
                            complain.put("product_id", com.getProductId());
                            complain.put("product_name", com.getProductName());
                            boolean checked = com.isChecked() == Constants.FLAG_TRUE;

                            if (Constants.DEBUG) {
                                Log.d(TAG, String.format("%s = %s", com.getComplainId(), checked));
                            }

                            complain.put("value", String.valueOf(checked));

                            complains.add(complain);
                        }

                        String complainsStr = mapper.writeValueAsString(complains);

                        entityBuilder.addTextBody("complains", complainsStr);
                    }

                    if (survey.getCompetitorPrograms() != null) {

                        List<Map<String, String>> programs = new ArrayList<>();

                        for (ItemCompetitor com : survey.getCompetitorPrograms()) {
                            Map<String, String> program = new HashMap<>();

                            program.put("program_id", com.getProgramId());
                            program.put("program_name", com.getProgramName());
                            program.put("product_id", com.getProductId());
                            program.put("product_name", com.getProductName());
                            boolean checked = com.getChecked() == Constants.FLAG_TRUE;

                            if (Constants.DEBUG) {
                                Log.d(TAG, String.format("%s = %s", com.getProgramName(), checked));
                            }

                            program.put("value", String.valueOf(checked));

                            if (!TextUtils.isEmpty(com.getUnitValue())) {
                                program.put("unit_value", com.getUnitValue());
                            }

                            programs.add(program);
                        }

                        String competitorStr = mapper.writeValueAsString(programs);

                        entityBuilder.addTextBody("competitors", competitorStr);
                    }

                    if (survey.getNotes() != null) {
                        List<Map<String, String>> notes = new ArrayList<>();

                        for (ItemNote note : survey.getNotes()) {
                            Map<String, String> map = new HashMap<>();

                            map.put("product_id", note.getProductId());
                            map.put(note.getNoteType(), note.getNote());
                            notes.add(map);
                        }

                        entityBuilder.addTextBody("notes", mapper.writeValueAsString(notes));
                    }

                    /*if (survey.getImages() != null) {
                        int imgCount = 0;
                        for (ItemImage img : survey.getImages()) {
                            try {
                                File f = new File(img.getImageUri());
                                if (f.exists()) {
                                    entityBuilder
                                            .addBinaryBody(String.format("photos[%d]", imgCount++),
                                                           f);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                    }*/

                    HttpEntity entity = entityBuilder.build();

                    post.setEntity(entity);

                    Log.d(TAG, "executing post request");
                    HttpResponse response = client.execute(post);
                    String respStr = EntityUtils.toString(response.getEntity());

                    JSONObject jsonResp = new JSONObject(respStr);

                    try {
                        if (Constants.DEBUG) Log.d(TAG, "survey response: " + jsonResp.toString(4));
                    } catch (JSONException e) {
                        if (Constants.DEBUG) Log.d(TAG, "survey response: " + jsonResp.toString());
                    }

                    String status = jsonResp.getString(Constants.JSON_KEY_STATUS);
                    JSONObject data = RestResponseHandler.getData(jsonResp);

                    if (!Constants.JSON_STATUS_OK.equals(status)) {
                        if (User.rePostLogin(mContext)) {
                            surveyTable.pending(survey.getId());
                            surveyTable.dataPending(survey.getId());
                        } else {
                            throw new Exception("Error push pending survey id: " + survey.getId());
                        }
                    } else {
                        String surveyId = data.getString("survey_id");

                        //set status data= sent
                        surveyTable.sent(survey.getId(), surveyId);
                        surveyTable.dataSent(survey.getId(), surveyId);
                        pushSurveyCounter++;
                    }
                }

            } catch (Exception e) {
                surveyTable.failed(survey.getId());
                surveyTable.dataFailed(survey.getId());

                if (Constants.DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public void pushPendingSurveyPhotos() {
        final SurveyTable surveyTable = new SurveyTable(mContext);
        String token = User.getInstance(mContext).getToken();
        List<Survey> surveyList = surveyTable.getPendingSurveyPhotos();

        for (Survey survey : surveyList) {
            if (survey.getImages() != null && !survey.getImages().isEmpty()) {
                SurveyPhotoResponseHandler responseHandler =
                        new SurveyPhotoResponseHandler(mContext, survey.getId());
                RestClient.getInstance(mContext, responseHandler)
                          .postSurveyPhotos(token, survey, false);
            }
        }
    }

    public HttpClient createNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws
                        CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws
                        CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws
                IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private class SurveyPhotoResponseHandler extends RestResponseHandler {
        private int surveyId;
        private SurveyTable surveyTable;

        public SurveyPhotoResponseHandler(Context context, int surveyId) {
            super(false, "SurveyPhotoResponse", context);
            this.surveyId = surveyId;
            this.surveyTable = new SurveyTable(context);
        }

        @Override
        public void onFailure(String message, String log, Throwable throwable) {
            super.onFailure(message, log, throwable);
            surveyTable.imageFailed(surveyId);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if (isSuccess(response)) {
                    surveyTable.imageSent(surveyId);
                } else {
                    if (Constants.DEBUG) Log.e(super.tag, "response: " + response.toString());
                    throw new Exception(
                            "Failed uploading photo, response error, survey id: " + surveyId);
                }
            } catch (Exception e) {
                surveyTable.imageFailed(surveyId);

                if (Constants.DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }
}
