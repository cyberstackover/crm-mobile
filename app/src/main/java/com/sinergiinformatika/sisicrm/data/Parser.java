package com.sinergiinformatika.sisicrm.data;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.models.Area;
import com.sinergiinformatika.sisicrm.data.models.City;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemImage;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.models.Province;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.data.models.Subdistrict;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.DistributorTable;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 1/2/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    public static List<Store> cursorToStoreList(Cursor cursor) {
        List<Store> stores = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Store store = new Store();
            store.setId(cursor.getInt(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_ID)));
            store.setName(
                    cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_STORE_NAME)));
            store.setCategoryCode(cursor.getInt(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_CATEGORY_CODE)));
            store.setCategoryLabel(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_CATEGORY_LABEL)));
            store.setNfcId(
                    cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_NFC_ID)));
            store.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_LONGITUDE)));
            store.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_LATITUDE)));
            store.setProvince(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_PROVINCE)));
            store.setCity(cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_CITY)));
            store.setSubdistrict(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_SUBDISTRICT)));
            store.setStreet(
                    cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_STREET)));
            store.setZipcode(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_ZIPCODE)));
            store.setPhoneMobile(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_PHONE_MOBILE)));
            store.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_PHONE)));
            store.setOwnerName(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_OWNER_NAME)));
            store.setOwnerBirthDate(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_OWNER_BIRTH_DATE)));
            store.setOwnerReligionCode(cursor.getInt(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_OWNER_RELIGION_CODE)));
            store.setOwnerReligionLabel(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_OWNER_RELIGION_LABEL)));
            store.setInformation(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_STORE_INFORMATION)));
            store.setLastCheckIn(cursor.getString(cursor.getColumnIndexOrThrow(
                    StoreTable.COLUMN_LAST_CHECKIN)));

            stores.add(store);
            cursor.moveToNext();
        }
        cursor.close();

        return stores;
    }

    public static List<Distributor> jsonArrayToDistributorList(JSONArray data) throws
            JSONException {
        List<Distributor> distributors = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject datum = data.getJSONObject(i);
            distributors.add(new Distributor(datum.getString("store_id"),
                                             datum.getString("store_name")));
        }

        return distributors;
    }

    public static List<Distributor> cursorToDistributorList(Cursor cursor) {
        List<Distributor> distributors = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Distributor distributor = new Distributor();
            distributor.setDbId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(DistributorTable.COLUMN_ID)));
            distributor.setId(cursor.getString(
                    cursor.getColumnIndexOrThrow(DistributorTable.COLUMN_DISTRIBUTOR_ID)));
            distributor.setName(cursor.getString(
                    cursor.getColumnIndexOrThrow(DistributorTable.COLUMN_DISTRIBUTOR_NAME)));

            distributors.add(distributor);
            cursor.moveToNext();
        }

        return distributors;
    }

    public static List<Agenda> cursorToJoinedAgendaList(Cursor cursor) {
        List<Agenda> agendas = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_ID));
            String date =
                    cursor.getString(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_AGENDA_DATE));
            long storeDbId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_STORE_ID));
            String storeId =
                    cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_STORE_ID));
            String storeName =
                    cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_STORE_NAME));
            double storeLong =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_LONGITUDE));
            double storeLat =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_LATITUDE));
            String checkIn =
                    cursor.getString(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_IN));
            double checkInLong = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_IN_LONG));
            double checkInLat =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_IN_LAT));
            String checkOut =
                    cursor.getString(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_OUT));
            double checkOutLong = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_OUT_LONG));
            double checkOutLat = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_OUT_LAT));

            long surveyDbId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_SURVEY_DB_ID));
            int isSurvey =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_SURVEY));
            int isPhoto = cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_PHOTO));
            int isComplain =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_COMPLAIN));

            Agenda agenda = new Agenda(id, date, storeDbId, storeId, storeName, storeLong, storeLat,
                                       checkIn, checkInLong, checkInLat, checkOut, checkOutLong,
                                       checkOutLat);

            agenda.setSurveyDbId(surveyDbId);
            agenda.setIsSurvey(isSurvey);
            agenda.setIsPhoto(isPhoto);
            agenda.setIsComplain(isComplain);

            agendas.add(agenda);

            cursor.moveToNext();
        }

        return agendas;
    }

    public static List<Order> cursorToOrderList(Cursor cursor) {
        List<Order> orders = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Order order = null;

            long id = cursor.getLong(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_ID));
            String orderId =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_ORDER_ID));
            int storeDbId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_STORE_DB_ID));
            String storeId =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_STORE_ID));
            String storeName =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_STORE_NAME));
            String distributorId = cursor.getString(
                    cursor.getColumnIndexOrThrow(OrderTable.COLUMN_DISTRIBUTOR_ID));
            String distributorName = cursor.getString(
                    cursor.getColumnIndexOrThrow(OrderTable.COLUMN_DISTRIBUTOR_NAME));
            String orderDateStr =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_ORDER_DATE));
            String products =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_PRODUCTS));
            String status =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_SYNC_STATUS));
            String deliveryDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.COLUMN_DELIVERY_DATE));

            if (/*!TextUtils.isEmpty(storeId) &&*/ !TextUtils.isEmpty(distributorId)
                                                   && !TextUtils.isEmpty(orderDateStr)) {
                order = new Order();
                order.setId(id);
                order.setStoreDbId(storeDbId);
                order.setStoreId(storeId);
                order.setDistributorId(distributorId);
                order.setOrderDate(orderDateStr);
                order.setSyncStatus(status);
                order.setDeliveryDate(deliveryDate);

                if (!TextUtils.isEmpty(orderId)) {
                    order.setOrderId(orderId);
                }
                if (!TextUtils.isEmpty(storeName)) {
                    order.setStoreName(storeName);
                }
                if (!TextUtils.isEmpty(distributorName)) {
                    order.setDistributorName(distributorName);
                }
                if (!TextUtils.isEmpty(products)) {
                    order.setProducts(products);
                    try {
                        JSONArray arrProducts = new JSONArray(products);
                        order.setPrices(jsonArrayToItemPriceList(arrProducts));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            if (order != null) {
                orders.add(order);
            }

            cursor.moveToNext();
        }

        return orders;
    }

    public static List<ItemPrice> jsonArrayToItemPriceList(JSONArray arr) throws JSONException {
        List<ItemPrice> prices = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String productId = obj.isNull("product_id") ? "" : obj.getString("product_id");
            String productName = obj.isNull("product_name") ? "" : obj.getString("product_name");
            double volume = obj.isNull("quantity") ? 0 : obj.getDouble("quantity");

            if (!TextUtils.isEmpty(productId)) {
                ItemPrice price = new ItemPrice();
                price.setProductId(productId);
                price.setProductName(productName);
                price.setVolume(volume);

                prices.add(price);
            }
        }

        return prices;
    }

    public static List<City> jsonArrayToCityList(JSONArray data) throws JSONException {
        List<City> cities = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject datum = data.getJSONObject(i);
            City c = new City();
            c.setCityId(datum.getString("id"));
            c.setCityName(datum.getString("name"));
            c.setProvinceId(datum.getString("province_id"));
            cities.add(c);
        }

        return cities;
    }

    private static int getInt(JSONObject source, String name) {
        try {
            return source.getInt(name);
        } catch (JSONException e) {
            //Log.d(TAG, e.getMessage(), e);
        }


        try {
            return Integer.parseInt(source.getString(name));
        } catch (NumberFormatException e) {
        } catch (JSONException e) {
        }


        return 0;
    }

    private static String getString(JSONObject source, String name) {
        try {
            String temp = source.isNull(name) ? "" : source.getString(name);
            Log.d(TAG, String.format("retrieving %s, value is: %s", name, temp));
            return temp;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    private static double getDouble(JSONObject source, String name) {
        try {
            return source.getDouble(name);
        } catch (JSONException e) {
            //Log.d(TAG, e.getMessage(), e);
        }

        try {
            return Double.parseDouble(source.getString(name));
        } catch (NumberFormatException e) {
        } catch (JSONException e) {
        }

        return 0.0;
    }

    private static boolean getBoolean(JSONObject source, String name) {
        try {
            return source.getBoolean(name);
        } catch (JSONException e) {
        }

        try {
            return Boolean.parseBoolean(source.getString(name));
        } catch (JSONException e) {
        } catch (Exception e) {

        }

        return false;
    }

    private static JSONArray getJSONArray(JSONObject source, String name) {
        try {
            return source.getJSONArray(name);
        } catch (JSONException e) {
        }

        return null;
    }

    public static void setValues(JSONObject source, Store target) {

        target.setStoreId(getString(source, "store_id"));
        target.setName(getString(source, "store_name"));
        target.setCapacity(getInt(source, "store_capacity"));
        target.setCategoryLabel(getString(source, "store_category"));
        if (!source.isNull("store_category_code")) {
            target.setCategoryCode(getInt(source, "store_category_code"));
        } else {
            if (target.getCategoryLabel().equals(StoreCategory.LABEL_PLATINUM.toLowerCase())) {
                target.setCategoryCode(StoreCategory.CODE_PLATINUM);
            } else if (target.getCategoryLabel().equals(StoreCategory.LABEL_GOLD.toLowerCase())) {
                target.setCategoryCode(StoreCategory.CODE_GOLD);
            } else {
                target.setCategoryCode(StoreCategory.CODE_SILVER);
            }
        }

        target.setDistributorId(getString(source, "distributor_id"));
        target.setNfcId(getString(source, "nfc_id"));
        target.setLongitude(getDouble(source, "longitude"));
        target.setLatitude(getDouble(source, "latitude"));

        target.setProvince(getString(source, "store_address_province"));
        target.setCity(getString(source, "store_address_city"));
        target.setSubdistrict(getString(source, "store_address_subdistrict"));

        if (!source.isNull("store_address_province_id")) {
            target.setProvinceId(getString(source, "store_address_province_id"));
        }

        if (!source.isNull("store_address_city_id")) {
            target.setCityId(getString(source, "store_address_city_id"));
        }

        if (!source.isNull("store_address_subdistrict_id")) {
            target.setSubdistrictId(getString(source, "store_address_subdistrict_id"));
        }

        target.setStreet(getString(source, "store_address_street"));
        target.setZipcode(getString(source, "store_zip_code"));

        target.setPhoneMobile(getString(source, "store_phone_mobile"));
        target.setPhone(getString(source, "store_phone"));
        target.setOwnerName(getString(source, "store_owner_full_name"));
        target.setOwnerBirthDate(getString(source, "store_owner_birthdate"));
        target.setOwnerReligionCode(getInt(source, "store_owner_religion_code"));
        target.setOwnerReligionLabel(getString(source, "store_owner_religion"));
        target.setLastCheckIn(getString(source, "last_check_in"));

        target.setInformation(getString(source, "store_information"));
        target.setPhoto(getString(source, "store_photo"));
        target.setStatus(getString(source, "store_status"));
        target.setCreated(getString(source, "date_entered"));
        target.setDeleted(source.isNull("deleted") ? 0 : getInt(source, "deleted"));
    }

    public static void setValues(JSONObject source, ItemPrice target) {

        target.setPriceId(getString(source, "price_id"));
        target.setProductId(getString(source, "product_id"));
        target.setProductName(getString(source, "product_name"));
        target.setProductPackage(getString(source, "product_package"));
        target.setPrice(getDouble(source, "price"));
        target.setPricePurchase(getDouble(source, "price_purchase"));
        target.setTermOfPayment(getInt(source, "term_of_payment"));

        //digunakan untuk survey dan order
        //di order menggunakan json key: quantity
        //di survey menggunakan json key: volume
        double volume = getDouble(source, "volume");
        double quantity = getDouble(source, "quantity");
        if (volume > 0.0) {
            target.setVolume(volume);
        } else {
            target.setVolume(quantity);
        }

        target.setStock(getDouble(source, "stock"));
        target.setDistributorId(getString(source, "distributor_id"));
        target.setDistributorName(getString(source, "distributor_name"));
        target.setIsCompetitor(getInt(source, "is_competitor"));
        target.setProductWeight(getString(source, "product_weight"));

    }

    public static void setValues(JSONObject source, ItemComplain target) {

        target.setProductId(getString(source, "product_id"));
        target.setProductName(getString(source, "product_name"));
        target.setComplainId(getString(source, "complain_id"));
        target.setComplain(getString(source, "complain_text"));

        boolean value = getBoolean(source, "value");
        target.setChecked(value ? Constants.FLAG_TRUE : Constants.FLAG_FALSE);
        target.setValue(getBoolean(source, "value"));
        /*target.setChecked(Constants.FLAG_FALSE);
        if (target.getValue()) {
            target.setChecked(Constants.FLAG_TRUE);
        }*/
    }

    public static void setValues(JSONObject source, Area target) {

        target.setAreaId(getString(source, "id"));
        target.setAreaName(getString(source, "name"));

    }

    public static void setValues(JSONObject source, Province target) {

        target.setProvinceId(getString(source, "id"));
        target.setProvinceName(getString(source, "name"));
        target.setSyncDate(getString(source, "date_modified"));
    }

    public static void setValues(JSONObject source, City target) {

        target.setCityId(getString(source, "id"));
        target.setCityName(getString(source, "name"));
        target.setProvinceId(getString(source, "province_id"));
        target.setSyncDate(getString(source, "date_modified"));
    }

    public static void setValues(JSONObject source, Subdistrict target) {

        target.setSubdistrictId(getString(source, "id"));
        target.setSubdistrictName(getString(source, "name"));
        target.setCityId(getString(source, "city_id"));
        target.setSyncDate(getString(source, "date_modified"));
    }

    public static void setValues(JSONObject source, Distributor target) {

        target.setId(getString(source, "store_id"));
        target.setName(getString(source, "store_name"));

    }

    public static void setValues(JSONObject source, Survey target) {
        if (Constants.DEBUG) Log.d(TAG, source.toString());

        target.setSurveyId(getString(source, "survey_id"));
        target.setStoreId(getString(source, "store_id"));
        target.setStoreName(getString(source, "store_name"));
        target.setIsSurvey(getBoolean(source, "is_survey"));
        target.setIsPhoto(getBoolean(source, "is_photo"));
        target.setIsComplain(getBoolean(source, "is_complain"));
        target.setStoreId(getString(source, "store_id"));
        target.setSurveyDate(getString(source, "survey_date"));
        target.setPrices(createPrices(getJSONArray(source, "products")));
        target.setImages(createImages(getJSONArray(source, "photos")));
        target.setComplains(createComplains(getJSONArray(source, "complains")));
        target.setCompetitorPrograms(jsonArrayToProgramList(getJSONArray(source, "competitors")));
        target.setCheckIn(getString(source, "survey_check_in"));
        target.setCheckInlatitude(getDouble(source, "survey_check_in_latitude"));
        target.setCheckInLongitude(getDouble(source, "survey_check_in_longitude"));
        target.setCheckOut(getString(source, "survey_check_out"));
        target.setCheckOutLatitude(getDouble(source, "survey_check_out_latitude"));
        target.setCheckOutLongitude(getDouble(source, "survey_check_out_longitude"));
        target.setPlanDate(getString(source, "survey_plan_date"));
        target.setNotes(jsonArrayToNotes(getJSONArray(source, "notes")));

    }

    public static void setValues(JSONObject source, ItemImage target) {

        target.setImageId(getString(source, "photo_id"));
        target.setImageUri(getString(source, "source"));

    }

    public static void setValues(JSONObject source, Order target) {

        target.setOrderId(getString(source, "order_id"));
        target.setStoreName(getString(source, "store_name"));
        target.setOrderDate(getString(source, "order_date"));
        target.setDistributorName(getString(source, "distributor_name"));
        target.setPrices(createPrices(getJSONArray(source, "orders")));

    }


    public static Store createStore(JSONObject source) {
        Store s = new Store();
        setValues(source, s);
        return s;
    }

    public static Distributor createDistributor(JSONObject source) {
        Distributor s = new Distributor();
        setValues(source, s);
        return s;
    }

    public static ItemComplain createComplain(JSONObject source) {
        ItemComplain s = new ItemComplain();
        setValues(source, s);
        return s;
    }

    public static Province createProvince(JSONObject source) {
        Province s = new Province();
        setValues(source, s);
        return s;
    }

    public static City createCity(JSONObject source) {
        City s = new City();
        setValues(source, s);
        return s;
    }

    public static Subdistrict createSubdistrict(JSONObject source) {
        Subdistrict s = new Subdistrict();
        setValues(source, s);
        return s;
    }

    public static ItemPrice createProduct(JSONObject source) {
        ItemPrice s = new ItemPrice();
        setValues(source, s);
        return s;
    }

    public static ItemImage createImage(JSONObject source) {
        ItemImage s = new ItemImage();
        setValues(source, s);
        return s;
    }

    public static List<ItemPrice> createPrices(JSONArray jsonArray) {

        List<ItemPrice> prices = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    prices.add(createProduct(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return prices;

    }

    public static List<ItemImage> createImages(JSONArray jsonArray) {

        List<ItemImage> images = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    images.add(createImage(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return images;

    }

    public static List<ItemComplain> createComplains(JSONArray jsonArray) {

        List<ItemComplain> complains = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    complains.add(createComplain(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return complains;

    }

    public static List<ItemCompetitor> jsonArrayToProgramList(JSONArray arr) {
        List<ItemCompetitor> itemCompetitors = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            try {
                ItemCompetitor item = jsonToCompetitorProgram(arr.getJSONObject(i));
                if (item != null) {
                    itemCompetitors.add(item);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return itemCompetitors;
    }

    public static List<ItemNote> jsonArrayToNotes(JSONArray arr) {
        List<ItemNote> notes = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            try {
                ItemNote note = jsonToNote(arr.getJSONObject(i));
                if (note != null) {
                    notes.add(note);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return notes;
    }

    public static ItemCompetitor jsonToCompetitorProgram(JSONObject obj) {
        ItemCompetitor program;

        try {
            String programId = obj.getString("program_id");
            String programName = obj.getString("program_name");
            program = new ItemCompetitor(programId, programName);

            String value = obj.isNull("value") ? "false" : obj.getString("value");

            program.setProductId(obj.isNull("product_id") ? null : obj.getString("product_id"));
            program.setProductName(
                    obj.isNull("product_name") ? null : obj.getString("product_name"));
            program.setUnitName(obj.isNull("unit_name") ? null : obj.getString("unit_name"));
            program.setUnitValue(obj.isNull("unit_value") ? null : obj.getString("unit_value"));
            program.setChecked(
                    value.equalsIgnoreCase("true") ? Constants.FLAG_TRUE : Constants.FLAG_FALSE);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }

        return program;
    }

    public static ItemNote jsonToNote(JSONObject obj) {
        ItemNote note = null;

        try {
            String productId = obj.getString("product_id");
            String noteType = null;
            String noteText = null;

            if (!obj.isNull(Constants.NOTE_TYPE_COMPLAIN)) {
                noteType = Constants.NOTE_TYPE_COMPLAIN;
                noteText = obj.getString(Constants.NOTE_TYPE_COMPLAIN);
            } else if (!obj.isNull(Constants.NOTE_TYPE_COMPETITOR)) {
                noteType = Constants.NOTE_TYPE_COMPETITOR;
                noteText = obj.getString(Constants.NOTE_TYPE_COMPETITOR);
            }

            if (!TextUtils.isEmpty(noteType)) {
                note = new ItemNote(productId, noteType, noteText);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return note;
    }
}
