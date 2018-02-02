package com.sinergiinformatika.sisicrm.conn;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemImage;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.utils.Calculator;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 12/16/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class RestClient {
    /**
     * Warning! Do not change API address unless it is actually moved.
     * Only use publicly-available addresses.
     */
    /*public static final String API_SERVER_ADDR = (Constants.DEBUG)
            ? "http://api.sinergicrm.com"
            : "https://crm.semenindonesia.com/api"*/;

    public static final String API_SERVER_ADDR = (Constants.DEBUG)
            ? "https://10.15.2.146/api"
            : "https://crm.semenindonesia.com/api";

    //    public static final int MAX_RETRY = 5;
    public static final int MAX_TIMEOUT_CONSTANT = 3;
    public static final int MAX_TIMEOUT =
            AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT * MAX_TIMEOUT_CONSTANT;

    public static final String ACTION_URL_LOGIN = "/account/login";
    public static final String ACTION_URL_PASSWORD_CHANGE = "/account/change_password";
    public static final String ACTION_URL_STORE_LIST = "/store/list";
    public static final String ACTION_URL_STORE_ADD = "/store/add";
    public static final String ACTION_URL_STORE_EDIT = "/store/update";
    public static final String ACTION_URL_STORE_UPDATE_LOC = "/store/update_location";
    public static final String ACTION_URL_SURVEY_LIST = "/survey/list";
    public static final String ACTION_URL_ORDER_LIST = "/order/list";
    public static final String ACTION_URL_ORDER_ADD = "/order/add";
    //    public static final String ACTION_URL_ORDER_DETAIL = "/order/detail";
    public static final String ACTION_URL_SURVEY_ADD = "/survey/add";
    public static final String ACTION_URL_SURVEY_ADD_PHOTOS = "/survey/add_photos";
    //    public static final String ACTION_URL_SURVEY_DETAIL = "/survey/detail";
    public static final String ACTION_URL_PRODUCT_LIST = "/product/list";
    public static final String ACTION_URL_COMPLAIN_LIST = "/complain/list";
    public static final String ACTION_URL_STATISTICS = "/statistic/resume";
    public static final String ACTION_URL_COMPETITOR_PROGRAMS = "/program/list";
    public static final String ACTION_URL_DATA_REF = "/ref";
    public static final String ACTION_URL_LATEST_VERSION = "/version/latest";

    private static final String TAG = "RestClient";
    //    private static final String REF_TYPE_AREA = "area";
    private static final String REF_TYPE_PROVINCE = "province";
    private static final String REF_TYPE_CITY = "city";
    private static final String REF_TYPE_SUBDISTRICT = "subdistrict";
    private static RestClient instance = null;
    private AsyncHttpClient asyncHttpClient;
    private SyncHttpClient syncHttpClient;
    private AsyncHttpResponseHandler responseHandler;

    private RestClient() {

    }

    public static RestClient getInstance(Context context,
                                         AsyncHttpResponseHandler responseHandler) {
        if (instance == null) {
            instance = new RestClient();

            PersistentCookieStore cookieStore = new PersistentCookieStore(context);
            MySSLSocketFactory socketFactory = null;
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                socketFactory = new MySSLSocketFactory(keyStore);
                socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            } catch (KeyStoreException | IOException | NoSuchAlgorithmException |
                    CertificateException | UnrecoverableKeyException | KeyManagementException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            instance.asyncHttpClient = new AsyncHttpClient(true, 80, 443);
            instance.asyncHttpClient.setCookieStore(cookieStore);
            instance.asyncHttpClient.setTimeout(MAX_TIMEOUT);

            if (socketFactory != null) {
                instance.asyncHttpClient.setSSLSocketFactory(socketFactory);
            }

            instance.syncHttpClient = new SyncHttpClient(true, 80, 443);
            instance.syncHttpClient.setCookieStore(cookieStore);
            instance.syncHttpClient.setTimeout(MAX_TIMEOUT);

            if (socketFactory != null) {
                instance.syncHttpClient.setSSLSocketFactory(socketFactory);
            }
        }

        instance.responseHandler = responseHandler;

        return instance;
    }

    public static void cancelRequests(Context context) {
        if (instance != null) {
            instance.asyncHttpClient.cancelRequests(context, true);
        }
    }

    public static String generateURL(String method) {
        return API_SERVER_ADDR + method;
    }

    private void post(String method, RequestParams params) {
        post(method, params, true);
    }

    public void post(String method, RequestParams params, boolean async) {
        String uri = generateURL(method);
        if (Constants.DEBUG) {
            Log.v(TAG, "post request to: " + uri);

            if (params != null) {
                Log.d(TAG, params.toString());
            } else {
                Log.d(TAG, "null param");
            }
        }

        if (async) {
            asyncHttpClient.post(uri, params, responseHandler);
        } else {
            responseHandler.setUseSynchronousMode(true);
            syncHttpClient.post(uri, params, responseHandler);
        }
    }

    private void get(String method, RequestParams params, boolean generateURL) {
        String uri = generateURL ? generateURL(method) : method;
        if (Constants.DEBUG) {
            Log.d(TAG, "get request to: " + uri);

            if (params != null) {
                Log.d(TAG, params.toString());
            } else {
                Log.d(TAG, "null param");
            }
        }

        if (params != null) {
            asyncHttpClient.get(uri, params, responseHandler);
        } else {
            asyncHttpClient.get(uri, responseHandler);
        }
    }

    private void getSync(String method, RequestParams params, boolean generateURL) {
        String uri = generateURL ? generateURL(method) : method;
        if (Constants.DEBUG) {
            Log.d(TAG, "get request to: " + uri);

            if (params != null) {
                Log.d(TAG, params.toString());
            } else {
                Log.d(TAG, "null param");
            }
        }

        responseHandler.setUseSynchronousMode(true);
        if (params != null) {
            syncHttpClient.get(uri, params, responseHandler);
        } else {
            syncHttpClient.get(uri, responseHandler);
        }
    }

    public void postLogin(String username, String password) {
        postLogin(username, password, true);
    }

    public void postLogin(String username, String password, boolean async) {
        if (Constants.DEBUG) {
            Log.d(TAG, String.format("username: %s | password: %s", username, password));
        }

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);

        post(ACTION_URL_LOGIN, params, async);
    }

    /**
     * @param storeType reseller atau customer
     */
    public void getStoreList(String token, String storeType) {
        getStoreList(token, storeType, Constants.STORE_STATUS_ACTIVE, true);
    }

    public void getStoreList(String token, String storeType, String status, boolean async) {
        getStoreList(token, storeType, status, null, async);
    }

    public void getStoreList(String token, String storeType, String status, String lastModified,
                             boolean async) {
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("type", storeType);

        if (!TextUtils.isEmpty(status)) {
            params.put("status", status);
        }

        if (!TextUtils.isEmpty(lastModified)) {
            params.put("last_update", lastModified);
            params.put("sync", 1);
        }

        if (Constants.DEBUG) Log.i(TAG, "token: " + token);

        if (async) {
            get(ACTION_URL_STORE_LIST, params, true);
        } else {
            getSync(ACTION_URL_STORE_LIST, params, true);
        }
    }

    public void postStore(String token, Store store) {

        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);

        if (!TextUtils.isEmpty(store.getStoreId())) {
            params.put("store_id", store.getStoreId());
        }

        params.put("store_name", store.getName());

        params.put("store_category_code", store.getCategoryCode());

        if (!TextUtils.isEmpty(store.getDistributorId())) {
            params.put("distributor_id", store.getDistributorId());
        }

        if (store.getLongitude() != null) {
            params.put("longitude", store.getLongitude());
        }

        if (store.getLatitude() != null) {
            params.put("latitude", store.getLatitude());
        }

        //required
        params.put("store_capacity", store.getCapacity().intValue());

        params.put("store_address_province", store.getProvinceId());
        params.put("store_address_city", store.getCityId());
        params.put("store_address_subdistrict", store.getSubdistrictId());

        params.put("store_address_street", store.getStreet());
        params.put("store_zipcode", store.getZipcode());
        params.put("store_phone", store.getPhone());
        params.put("store_owner_full_name", store.getOwnerName());

        if (!TextUtils.isEmpty(store.getCreated())) {
            params.put("store_created_datetime", store.getCreated());
        } else {
            params.put("store_created_datetime",
                       DateUtil.formatDBDateTime(DateUtil.now()));
        }

        //optional
        params.put("nfc_id", store.getNfcId());
        params.put("store_phone_mobile", store.getPhoneMobile());
        params.put("store_owner_birthdate", store.getOwnerBirthDate());
        params.put("store_owner_religion_code", store.getOwnerReligionCode());
        params.put("store_information", store.getInformation());

        try {
            File f = new File(store.getPhoto());
            if (f.exists()) {
                params.put("store_photo", f);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (Constants.DEBUG) {
            Log.d(TAG, "params post store:" + params.toString());
        }

        post(ACTION_URL_STORE_ADD, params);
    }

    public void postStoreLocation(String token, String storeId, LatLng latLng) {
        if (Constants.DEBUG) Log.d(TAG, "session: " + token);

        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("store_id", storeId);
        params.put("latitude", latLng.latitude);
        params.put("longitude", latLng.longitude);

        post(ACTION_URL_STORE_UPDATE_LOC, params);
    }

    public void getSurveyList(String token, String userId, Date startDate, Date endDate,
                              Integer offset, Integer maxResults, String search) {

        //Log.d(TAG, "getSurveyList, offset = "+offset);

        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("user_id", userId);

        if (offset != null) {
            params.put("offset", offset);
        }

        if (maxResults != null) {
            params.put("max_results", maxResults);
        }

        if (search != null) {
            params.put("search", search);
        }

        if (startDate != null && endDate != null) {
            params.put("start_date", DateUtil.formatDBDateOnly(startDate));
            params.put("end_date", DateUtil.formatDBDateOnly(endDate));
        }

        get(ACTION_URL_SURVEY_LIST, params, true);
    }

    public void getOrderList(String token, String userId, Date startDate, Date endDate,
                             Integer offset, Integer maxResults, String search) {

        getOrderList(token, userId, startDate, endDate, offset, maxResults, search, true);
    }

    public void getOrderList(String token, String userId, Date startDate, Date endDate,
                             Integer offset, Integer maxResults, String search, boolean async) {

        //Log.d(TAG, "getOrderList, offset = "+offset);

        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("user_id", userId);

        if (offset != null) {
            params.put("offset", offset);
        }

        if (maxResults != null) {
            params.put("max_results", maxResults);
        }

        if (search != null) {
            params.put("search", search);
        }

        if (startDate != null && endDate != null) {
            params.put("start_date", DateUtil.formatDBDateOnly(startDate));
            params.put("end_date", DateUtil.formatDBDateOnly(endDate));
        }

        if (async) {
            get(ACTION_URL_ORDER_LIST, params, true);
        } else {
            getSync(ACTION_URL_ORDER_LIST, params, true);
        }
    }

    /*public void getOrderDetail(String token, String orderId) {
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("order_id", orderId);
        get(ACTION_URL_ORDER_DETAIL, params, true);
    }

    public void postOrder(String token, Date orderDate, String storeId, List<ItemPrice> orderList) {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> orders = new ArrayList<>();
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("date_delivered", DateUtil.formatDBDateOnly(orderDate));
        params.put("store_id", storeId);
        params.put("distributor_id", orderList.get(0).getDistributorId());

        for (ItemPrice orderItem : orderList) {
            Map<String, Object> order = new HashMap<>();
            order.put("product_id", orderItem.getProductId());
            order.put("quantity", orderItem.getVolume());

            orders.add(order);
        }

        try {
            String ordersStr = mapper.writeValueAsString(orders);
            params.put("products", ordersStr);

            if (Constants.DEBUG) {
                Log.d(TAG, "session: " + token);
                Log.d(TAG, "store_id: " + storeId);
                Log.d(TAG, "distributor_id: " + orderList.get(0).getDistributorId());
                Log.d(TAG, "date_delivered: " + DateUtil.formatDBDateOnly(orderDate));
                Log.d(TAG, "products: " + ordersStr);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (Constants.DEBUG) {
            Log.d(TAG, "params post order = " + params);
        }

        post(ACTION_URL_ORDER_ADD, params);
    }*/

    public void postSurvey(String token, Survey survey) {
        RequestParams params;

        try {
            if (Constants.DEBUG) {
                Log.d(TAG, "postSurvey, getId = " + survey.getId());
                Log.d(TAG, "postSurvey, getStoreId = " + survey.getStoreId());
            }

            ObjectMapper mapper = new ObjectMapper();

            params = new RequestParams();
            params.put(Constants.TOKEN_KEY, token);
            params.put("store_id", survey.getStoreId());
            params.put("check_in_nfc", String.valueOf(survey.isCheckInNfc()));
            params.put("check_in_time", survey.getCheckInTime());
            params.put("check_in_longitude", survey.getCheckInLongitude());
            params.put("check_in_latitude", survey.getCheckInLatitude());
            params.put("check_out_time", survey.getCheckOut());
            params.put("check_out_longitude", survey.getCheckOutLongitude());
            params.put("check_out_latitude", survey.getCheckOutLatitude());
            params.put("plan_date", survey.getPlanDate());

            if (survey.getPlanDate() == null || survey.getPlanDate().trim().length() == 0) {
                params.put("plan_date", survey.getCheckInTime());
            }

            if (survey.getSurveyDate() != null && survey.getSurveyDate().trim().length() > 0) {
                params.put("survey_date", survey.getSurveyDate());
            }

            if (survey.getSurveyClienDate() != null && survey.getSurveyClienDate().trim().length
                    () > 0) {
                params.put("survey_client_date", survey.getSurveyClienDate());
            }

            if (survey.getSurveyId() != null && survey.getSurveyId().trim().length() > 0) {
                params.put("survey_id", survey.getSurveyId());
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
                params.put("products", productsStr);
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

                params.put("complains", complainsStr);
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

                params.put("competitors", competitorStr);
            }

            if (survey.getNotes() != null) {
                List<Map<String, String>> notes = new ArrayList<>();

                for (ItemNote note : survey.getNotes()) {
                    Map<String, String> map = new HashMap<>();

                    map.put("product_id", note.getProductId());
                    map.put(note.getNoteType(), note.getNote());

                    notes.add(map);
                }

                params.put("notes", mapper.writeValueAsString(notes));
            }

            /*if (survey.getImages() != null) {
                int imgCount = 0;
                for (ItemImage img : survey.getImages()) {
                    try {
                        File f = new File(img.getImageUri());
                        if (f.exists()) {
                            params.put(String.format("photos[%d]", imgCount++), f);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }*/

            if (Constants.DEBUG) {
                Log.d(TAG, "params post survey:" + params.toString());
            }
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.getMessage(), e);
            params = null;
        }

        if (params != null) {
            post(ACTION_URL_SURVEY_ADD, params);
        }
    }

    public void postSurveyPhotos(String token, Survey survey) {
        postSurveyPhotos(token, survey, true);
    }

    public void postSurveyPhotos(String token, Survey survey, boolean async) {
        if (!TextUtils.isEmpty(survey.getSurveyId()) && survey.getImages() != null &&
            !survey.getImages().isEmpty()) {
            RequestParams params = new RequestParams();

            params.put(Constants.TOKEN_KEY, token);
            params.put("survey_id", survey.getSurveyId());

            int imgCount = 0;
            for (ItemImage img : survey.getImages()) {
                try {
                    File f = new File(img.getImageUri());
                    if (f.exists()) {
                        params.put(String.format("photos[%d]", imgCount++), f);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            post(ACTION_URL_SURVEY_ADD_PHOTOS, params, async);
        }
    }

    public void getProductList(String token) {
        getProductList(token, null, true);
    }

    public void getProductList(String token, String lastModified, boolean async) {
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);

        if (!TextUtils.isEmpty(lastModified)) {
            params.put("last_update", lastModified);
            params.put("sync", 1);
        }

        if (async) {
            get(ACTION_URL_PRODUCT_LIST, params, true);
        } else {
            getSync(ACTION_URL_PRODUCT_LIST, params, true);
        }
    }

    public void getComplainList(String token) {
        getComplainList(token, null, true);
    }

    public void getComplainList(String token, String lastModified, boolean async) {
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);

        if (!TextUtils.isEmpty(lastModified)) {
            params.put("last_update", lastModified);
            params.put("sync", 1);
        }

        if (async) {
            get(ACTION_URL_COMPLAIN_LIST, params, true);
        } else {
            getSync(ACTION_URL_COMPLAIN_LIST, params, true);
        }
    }

    /*public void getSurveyDetail(String token, String surveyId) {

        //Log.d(TAG, "getComplainList");
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("survey_id", surveyId);
        get(ACTION_URL_SURVEY_DETAIL, params, true);
    }*/

    public void getStatistics(String token, Date sDate, Date eDate) {
        if (Constants.DEBUG) {
            Log.d(TAG, "session: " + token);
            Log.d(TAG, "start_date: " + DateUtil.formatDBDateOnly(sDate));
            Log.d(TAG, "end_date: " + DateUtil.formatDBDateOnly(eDate));
        }

        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);

        if (sDate != null) {
            params.put("start_date", DateUtil.formatDBDateOnly(sDate));
        }
        if (eDate != null) {
            params.put("end_date", DateUtil.formatDBDateOnly(eDate));
        }

        get(ACTION_URL_STATISTICS, params, true);
    }

    private void getDateRef(String token, String refType, String userId, String areaId,
                            String lastModified, boolean async) {
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);
        params.put("type", refType);

        if (userId != null && userId.trim().length() > 0) {
            params.put("user_id", userId);
        }

        if (areaId != null) {
            params.put("area_id", areaId);
        }

        if (!TextUtils.isEmpty(lastModified)) {
            params.put("last_update", lastModified);
            params.put("sync", 1);
        }

        if (async) {
            get(ACTION_URL_DATA_REF, params, true);
        } else {
            getSync(ACTION_URL_DATA_REF, params, true);
        }
    }

    /*public void getProvinceList(String token) {
        getProvinceList(token, null);
    }*/

    public void getProvinceList(String token, String areaId) {
        getDateRef(token, REF_TYPE_PROVINCE, null, areaId, null, true);
    }

    public void getProvinceList(String token, String areaId, String lastUpdate, boolean async) {
        getDateRef(token, REF_TYPE_PROVINCE, null, areaId, lastUpdate, async);
    }

    /*public void getCityList(String token) {
        getCityList(token, null);
    }*/

    public void getCityList(String token, String areaId) {
        getDateRef(token, REF_TYPE_CITY, null, areaId, null, true);
    }

    public void getCityList(String token, String areaId, String lastUpdate, boolean async) {
        getDateRef(token, REF_TYPE_CITY, null, areaId, lastUpdate, async);
    }

    /*public void getSubdistrictList(String token) {
        getSubdistrictList(token, null);
    }*/

    public void getSubdistrictList(String token, String areaId) {
        getDateRef(token, REF_TYPE_SUBDISTRICT, null, areaId, null, true);
    }

    public void getSubdistrictList(String token, String areaId, String lastUpdate, boolean async) {
        getDateRef(token, REF_TYPE_SUBDISTRICT, null, areaId, lastUpdate, async);
    }

    /*public void getArea(String token, String userId) {
        getDateRef(token, REF_TYPE_AREA, userId, null);
    }*/

    public void getCompetitorPrograms(String token) {
        getCompetitorPrograms(token, null, true);
    }

    public void getCompetitorPrograms(String token, String lastModified, boolean async) {
        RequestParams params = new RequestParams();
        params.put(Constants.TOKEN_KEY, token);

        if (!TextUtils.isEmpty(lastModified)) {
            params.put("last_update", lastModified);
            params.put("sync", 1);
        }

        if (async) {
            get(ACTION_URL_COMPETITOR_PROGRAMS, params, true);
        } else {
            getSync(ACTION_URL_COMPETITOR_PROGRAMS, params, true);
        }
    }

    public void getLatestVersion(boolean async) {
        if (async) {
            get(ACTION_URL_LATEST_VERSION, null, true);
        } else {
            getSync(ACTION_URL_LATEST_VERSION, null, true);
        }
    }

    public void getLatestBuild(String url, boolean async) {
        if (async) {
            get(url, null, false);
        } else {
            getSync(url, null, false);
        }
    }

    public void postPasswordChange(String token, String passwordOld, String passwordNew,
                                   String passwordConfirm, boolean async) {
        RequestParams params = new RequestParams();
        params.put(Constants.JSON_KEY_USER_SESSION, token);
        params.put("old_password", passwordOld);
        params.put("new_password", passwordNew);
        params.put("confirm_new_password", passwordConfirm);

        post(ACTION_URL_PASSWORD_CHANGE, params, async);
    }
}
