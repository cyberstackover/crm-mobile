package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 30-Dec-14.
 *
 * @author wendi
 */
public class StoreTable {

    public static final String TABLE_NAME = "store";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STORE_ID = "store_id";
    public static final String COLUMN_STORE_NAME = "store_name";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_CATEGORY_CODE = "category_code";
    public static final String COLUMN_CATEGORY_LABEL = "category_label";
    public static final String COLUMN_DISTRIBUTOR_ID = "distributor_id";
    public static final String COLUMN_NFC_ID = "nfc_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_SUBDISTRICT = "district";
    public static final String COLUMN_PROVINCE_ID = "province_id";
    public static final String COLUMN_CITY_ID = "city_id";
    public static final String COLUMN_SUBDISTRICT_ID = "district_id";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_ZIPCODE = "zipcode";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_PHONE_MOBILE = "phone_mobile";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_OWNER_NAME = "owner_name";
    public static final String COLUMN_OWNER_BIRTH_DATE = "owner_birth_date";
    public static final String COLUMN_OWNER_RELIGION_CODE = "owner_religion_code";
    public static final String COLUMN_OWNER_RELIGION_LABEL = "owner_religion_label";
    public static final String COLUMN_STORE_INFORMATION = "store_information";
    public static final String COLUMN_LAST_CHECKIN = "last_checkin";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_CREATE_DATE = "create_date";
    public static final String COLUMN_MODIFIED_DATE = "modified_date";
    public static final String COLUMN_SYNC_STATUS = "sync_status";


    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_STORE_ID, COLUMN_STORE_NAME, COLUMN_CAPACITY, COLUMN_CATEGORY_CODE,
            COLUMN_CATEGORY_LABEL, COLUMN_NFC_ID, COLUMN_LONGITUDE, COLUMN_LATITUDE,
            COLUMN_PROVINCE, COLUMN_CITY, COLUMN_SUBDISTRICT, COLUMN_DISTRIBUTOR_ID,
            COLUMN_PROVINCE_ID, COLUMN_CITY_ID, COLUMN_SUBDISTRICT_ID,
            COLUMN_STREET, COLUMN_ZIPCODE, COLUMN_PHOTO, COLUMN_PHONE_MOBILE, COLUMN_CREATE_DATE,
            COLUMN_PHONE, COLUMN_OWNER_NAME, COLUMN_OWNER_BIRTH_DATE, COLUMN_OWNER_RELIGION_CODE,
            COLUMN_OWNER_RELIGION_LABEL, COLUMN_STORE_INFORMATION, COLUMN_LAST_CHECKIN,
            COLUMN_STATUS, COLUMN_SYNC_DATE, COLUMN_MODIFIED_DATE, COLUMN_SYNC_STATUS
    };

    private static final String CREATE_TABLE;
    private static final String TAG = StoreTable.class.getSimpleName();
    public static final Uri URI = CRMContentProvider.URI_STORES;

    static {
        CREATE_TABLE = new StringBuilder()
                .append("create table ")
                .append(TABLE_NAME)
                .append(" (")
                .append(COLUMN_ID)
                .append(" integer primary key, ")
                .append(COLUMN_STORE_ID)
                .append(" text, ")
                .append(COLUMN_STORE_NAME)
                .append(" text not null, ")
                .append(COLUMN_CAPACITY)
                .append(" integer, ")
                .append(COLUMN_CATEGORY_CODE)
                .append(" integer not null, ")
                .append(COLUMN_CATEGORY_LABEL)
                .append(" text, ")
                .append(COLUMN_DISTRIBUTOR_ID)
                .append(" text, ")
                .append(COLUMN_NFC_ID)
                .append(" text, ")
                .append(COLUMN_LONGITUDE)
                .append(" real, ")
                .append(COLUMN_LATITUDE)
                .append(" real, ")
                .append(COLUMN_PROVINCE)
                .append(" text, ")
                .append(COLUMN_CITY)
                .append(" text, ")
                .append(COLUMN_SUBDISTRICT)
                .append(" text, ")
                .append(COLUMN_PROVINCE_ID)
                .append(" text, ")
                .append(COLUMN_CITY_ID)
                .append(" text, ")
                .append(COLUMN_SUBDISTRICT_ID)
                .append(" text, ")
                .append(COLUMN_STREET)
                .append(" text, ")
                .append(COLUMN_ZIPCODE)
                .append(" text, ")
                .append(COLUMN_PHOTO)
                .append(" text, ")
                .append(COLUMN_PHONE_MOBILE)
                .append(" text, ")
                .append(COLUMN_PHONE)
                .append(" text, ")
                .append(COLUMN_OWNER_NAME)
                .append(" text, ")
                .append(COLUMN_OWNER_BIRTH_DATE)
                .append(" text, ")
                .append(COLUMN_OWNER_RELIGION_CODE)
                .append(" integer, ")
                .append(COLUMN_OWNER_RELIGION_LABEL)
                .append(" text, ")
                .append(COLUMN_STORE_INFORMATION)
                .append(" text, ")
                .append(COLUMN_LAST_CHECKIN)
                .append(" text, ")
                .append(COLUMN_STATUS)
                .append(" text, ")
                .append(COLUMN_SYNC_DATE)
                .append(" text, ")
                .append(COLUMN_CREATE_DATE)
                .append(" text, ")
                .append(COLUMN_MODIFIED_DATE)
                .append(" text, ")
                .append(COLUMN_SYNC_STATUS)
                .append(" text);")
                .toString();
    }

    private User currentUser;
    private ContentResolver contentResolver;

    public StoreTable(Context context) {
        this.currentUser = User.getInstance(context);
        this.contentResolver = context.getContentResolver();
    }

    public StoreTable(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 48) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } else if (oldVersion < 49) {  // update for db version 49
            Log.w(TAG, "Upgrading database from version "
                       + oldVersion + " to " + newVersion);
            db.execSQL(
                    "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DISTRIBUTOR_ID +
                    " TEXT");
        } else if (oldVersion < 53) {
            Log.w(TAG, "Upgrading database from version "
                       + oldVersion + " to " + newVersion);
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_CREATE_DATE + " TEXT");
        }
    }

    /**
     * @param cursor cursor pointing to db row
     * @param store  target object to set the value
     */
    public static void setValues(Cursor cursor, Store store) {
        store.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        store.setStoreId(cursor.getString((cursor.getColumnIndex(COLUMN_STORE_ID))));
        store.setName(cursor.getString(cursor.getColumnIndex(COLUMN_STORE_NAME)));
        store.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
        store.setNfcId(cursor.getString(cursor.getColumnIndex(COLUMN_NFC_ID)));
        store.setCategoryCode(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_CODE)));
        store.setCategoryLabel(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_LABEL)));
        store.setDistributorId(cursor.getString(cursor.getColumnIndex(COLUMN_DISTRIBUTOR_ID)));
        store.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
        store.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));

        store.setProvince(cursor.getString(cursor.getColumnIndex(COLUMN_PROVINCE)));
        store.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
        store.setSubdistrict(cursor.getString(cursor.getColumnIndex(COLUMN_SUBDISTRICT)));
        store.setProvinceId(cursor.getString(cursor.getColumnIndex(COLUMN_PROVINCE_ID)));
        store.setCityId(cursor.getString(cursor.getColumnIndex(COLUMN_CITY_ID)));
        store.setSubdistrictId(cursor.getString(cursor.getColumnIndex(COLUMN_SUBDISTRICT_ID)));

        store.setStreet(cursor.getString(cursor.getColumnIndex(COLUMN_STREET)));
        store.setZipcode(cursor.getString(cursor.getColumnIndex(COLUMN_ZIPCODE)));
        store.setPhoto(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO)));
        store.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
        store.setPhoneMobile(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_MOBILE)));
        store.setOwnerName(cursor.getString(cursor.getColumnIndex(COLUMN_OWNER_NAME)));
        store.setOwnerBirthDate(cursor.getString(cursor.getColumnIndex(COLUMN_OWNER_BIRTH_DATE)));
        store.setOwnerReligionCode(cursor.getInt(cursor.getColumnIndex(COLUMN_OWNER_RELIGION_CODE)));
        store.setOwnerReligionLabel(cursor.getString(cursor.getColumnIndex(COLUMN_OWNER_RELIGION_LABEL)));
        store.setInformation(cursor.getString(cursor.getColumnIndex(COLUMN_STORE_INFORMATION)));
        store.setLastCheckIn(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_CHECKIN)));
        store.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
        store.setSyncDate(cursor.getString(cursor.getColumnIndex(COLUMN_SYNC_DATE)));
        store.setCreated(cursor.getString(cursor.getColumnIndex(COLUMN_CREATE_DATE)));
        store.setModifiedDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIFIED_DATE)));
        store.setPhoto(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO)));

        store.setSyncStatus(cursor.getString(cursor.getColumnIndex(COLUMN_SYNC_STATUS)));
    }

    /**
     * @param store  source store
     * @param values target object to set the value
     */

    public static void setValues(Store store, ContentValues values) {
        values.put(COLUMN_STORE_ID, store.getStoreId());
        values.put(COLUMN_STORE_NAME, store.getName());
        values.put(COLUMN_CATEGORY_CODE, store.getCategoryCode());
        values.put(COLUMN_CATEGORY_LABEL, store.getCategoryLabel());
        values.put(COLUMN_DISTRIBUTOR_ID, store.getDistributorId());
        values.put(COLUMN_CAPACITY, store.getCapacity());
        values.put(COLUMN_NFC_ID, store.getNfcId());
        values.put(COLUMN_LONGITUDE, store.getLongitude());
        values.put(COLUMN_LATITUDE, store.getLatitude());

        values.put(COLUMN_PROVINCE, store.getProvince());
        values.put(COLUMN_CITY, store.getCity());
        values.put(COLUMN_SUBDISTRICT, store.getSubdistrict());

        values.put(COLUMN_PROVINCE_ID, store.getProvinceId());
        values.put(COLUMN_CITY_ID, store.getCityId());
        values.put(COLUMN_SUBDISTRICT_ID, store.getSubdistrictId());

        values.put(COLUMN_STREET, store.getStreet());
        values.put(COLUMN_ZIPCODE, store.getZipcode());
        values.put(COLUMN_PHOTO, store.getPhoto());
        values.put(COLUMN_PHONE_MOBILE, store.getPhoneMobile());
        values.put(COLUMN_PHONE, store.getPhone());
        values.put(COLUMN_OWNER_NAME, store.getOwnerName());
        values.put(COLUMN_OWNER_BIRTH_DATE, store.getOwnerBirthDate());
        values.put(COLUMN_OWNER_RELIGION_CODE, store.getOwnerReligionCode());
        values.put(COLUMN_OWNER_RELIGION_LABEL, store.getOwnerReligionLabel());
        values.put(COLUMN_LAST_CHECKIN, store.getLastCheckIn());
        values.put(COLUMN_STORE_INFORMATION, store.getInformation());
        values.put(COLUMN_STATUS, store.getStatus());
        values.put(COLUMN_SYNC_DATE, store.getSyncDate());
        values.put(COLUMN_CREATE_DATE, store.getCreated());
        values.put(COLUMN_MODIFIED_DATE, store.getModifiedDate());
        values.put(COLUMN_PHOTO, store.getPhoto());

        values.put(COLUMN_SYNC_STATUS, store.getSyncStatus());
    }

    public void insert(Store store) {
        if (Constants.DEBUG) {
            Log.d(TAG, "insert store");
        }

        if (TextUtils.isEmpty(store.getCreated())) {
            store.setCreated(DateUtil.formatDBDateTime(DateUtil.now()));
        }
        store.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));

        ContentValues values = new ContentValues();
        setValues(store, values);

        contentResolver.insert(URI, values);
    }

    public void syncInsert(Store store) {
        List<Store> stores = get(COLUMN_STORE_NAME + " = ? AND " + COLUMN_CREATE_DATE + " = ?",
                                 new String[]{store.getName(), store.getCreated()});

        if (!stores.isEmpty()) {
            Log.w(TAG, String.format("Store %s already saved, aborting insert", store.getName()));
            return;
        }

        if (TextUtils.isEmpty(store.getStatus())) {
            store.setStatus(Constants.STORE_STATUS_ACTIVE);
        }

        store.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));

        if (TextUtils.isEmpty(store.getSyncStatus())) {
            store.setSyncStatus(Constants.SYNC_STATUS_SENT);
        }

        insert(store);

    }

    public void syncUpdate(Store store) {
        if (TextUtils.isEmpty(store.getStatus())) {
            store.setStatus(Constants.STORE_STATUS_ACTIVE);
        }

        store.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));

        if (TextUtils.isEmpty(store.getSyncStatus())) {
            store.setSyncStatus(Constants.SYNC_STATUS_SENT);
        }

        updateByStoreId(store.getStoreId(), store);
    }

    public void syncSave(Store store) {
        Store s = getByStoreId(store.getStoreId());
        if (s == null) {
            syncInsert(store);
        } else if (s.getSyncStatus().equals(Constants.SYNC_STATUS_SENT)) {
            syncUpdate(store);
        }
    }

    public void save(Store store) {
        if (Constants.DEBUG) {
            Log.d(TAG, "saving store");
        }

        if (TextUtils.isEmpty(store.getStatus())) {
            if (currentUser.isAreaManager()) {
                store.setStatus(Constants.STORE_STATUS_ACTIVE);
            } else {
                store.setStatus(Constants.STORE_STATUS_UNVERIFIED);
            }
        }

        store.setSyncStatus(Constants.SYNC_STATUS_PENDING);

        Store s = getById(store.getId());
        if (s == null) {
            insert(store);
        } else {
            update(store);
        }
    }

    public void update(Store store) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updateById, id = " + store.getId());
        }

        store.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));
        ContentValues values = new ContentValues();
        setValues(store, values);
        Uri uri = Uri.parse(URI + "/" + store.getId());
        contentResolver.update(uri, values, null, null);
    }

    public void update(@NonNull ContentValues values, String selection, String[] selectionArgs) {
        contentResolver.update(URI, values, selection, selectionArgs);
    }

    /*public void updateStoreIdById(int id, String storeId) {
        if (Constants.DEBUG) {
            Log.d(TAG, String.format("updateStoreIdById, id = %s, storeId = %s", id, storeId));
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORE_ID, storeId);
        values.put(COLUMN_MODIFIED_DATE, DateUtil.formatDBDateTime(DateUtil.now()));
        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }*/

    public void updateByStoreId(String storeId, Store store) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updateByStoreId, storeId = " + storeId);
        }
        store.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));
        ContentValues values = new ContentValues();
        setValues(store, values);
        String where = "store_id = ?";
        String[] selectionArgs = {storeId};
        contentResolver.update(URI, values, where, selectionArgs);
    }

    public Store getByStoreId(String storeId) {
        //Log.d(TAG, "getByStoreId");

        Cursor c = contentResolver.query(URI, ALL_COLUMNS, COLUMN_STORE_ID + " = ?", new String[]{storeId}, null);

        if (c != null && c.moveToFirst()) {
            Store s = new Store();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public Store getById(int id) {

        if (Constants.DEBUG) {
            Log.d(TAG, "getById, id = " + id);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        Cursor c = contentResolver.query(uri, ALL_COLUMNS, null, null, null);

        if (c != null && c.moveToFirst()) {
            Store s = new Store();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public List<Store> get(String selection, String[] selectionArgs) {
        List<Store> stores = new ArrayList<>();

        Cursor c = contentResolver
                .query(URI, ALL_COLUMNS, selection, selectionArgs, COLUMN_STORE_NAME);
        if (c == null) {
            return stores;
        }

        if (c.moveToFirst()) {
            do {
                Store s = new Store();
                setValues(c, s);
                stores.add(s);
            } while (c.moveToNext());
        }
        c.close();

        return stores;
    }

    public List<Store> getAll() {
        return get(null, null);
    }

    /*public Map<String, Integer> getIdAndStoreIds() {


        String selections = COLUMN_STATUS + " = ?";
        String[] selectionArgs = new String[]{Constants.STORE_STATUS_ACTIVE};
        String[] projection = {
                COLUMN_ID,
                COLUMN_STORE_ID
        };

        Cursor c = contentResolver.query(URI, projection, selections, selectionArgs, null);
        if (c == null) {
            return null;
        }

        Map<String, Integer> map = new HashMap<>(0);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt((c.getColumnIndex(COLUMN_ID)));
                String storeId = c.getString((c.getColumnIndex(COLUMN_STORE_ID)));
                map.put(storeId, id);
            } while (c.moveToNext());
        }
        c.close();

        return map;
    }*/

    public void deleteByStoreId(String[] storeIds) {
        String where = COLUMN_STORE_ID + " in (";

        for (int i = 0; i < storeIds.length; i++) {
            if (i > 0) {
                where += ",";
            }
            where += "?";
            i++;
        }

        where += ")";

        contentResolver.delete(URI, where, storeIds);

    }

//    public void deleteAll() {
//        contentResolver.delete(URI, null, null);
//    }

    public boolean isEmpty() {

        Cursor c = contentResolver.query(URI, new String[]{COLUMN_ID}, null, null, null);
        if (c == null) {
            return true;
        }

        if (c.moveToFirst()) {
            c.close();
            return false;
        }

        return true;
    }

    /**
     * List Pending Store
     * @param limit
     * @return
     */
    private List<Store> listPendingStores(String limit) {
        //String selections = COLUMN_STORE_ID + " is null";
        String selections = COLUMN_SYNC_STATUS + " = ?";
        String[] args = new String[]{Constants.SYNC_STATUS_PENDING};
        String sortOrder = COLUMN_ID;

        List<Store> stores = new ArrayList<>();
        Cursor c = null;
        if(limit != null){
            c = contentResolver.query(URI.buildUpon().appendQueryParameter("limit",
                    limit).build(), ALL_COLUMNS, selections, args, COLUMN_MODIFIED_DATE + " DESC");
        } else {
            c = contentResolver.query(URI, ALL_COLUMNS, selections, args, sortOrder);
        }

        if (c == null) {
            return stores;
        }

        if (c.moveToFirst()) {
            do {
                Store s = new Store();
                setValues(c, s);
                stores.add(s);
            } while (c.moveToNext());
        }
        c.close();

        return stores;
    }

    /**
     * get All Pending Stores
     * @return
     */
    public List<Store> getPendingStores() {
        return listPendingStores(null);
    }

    /**
     * get Limited Pending Stores
     * @param limit
     * @return
     */
    public List<Store> getPendingStores(String limit) {
        return listPendingStores(limit);
    }


    public void sent(int id, String storeId) {
        updateSyncStatus(id, Constants.SYNC_STATUS_SENT, storeId);
    }

//    public void pending(int id) {
//        updateSyncStatus(id, Constants.SYNC_STATUS_PENDING);
//    }

    public void sending(int id) {
        updateSyncStatus(id, Constants.SYNC_STATUS_SENDING);
    }

    public void pending(int id) {
        updateSyncStatus(id, Constants.SYNC_STATUS_PENDING);
    }

    public void failed(int id) {
        updateSyncStatus(id, Constants.SYNC_STATUS_FAILED);
    }

    public void updateLocation(int id, double longitude, double latitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_LATITUDE, latitude);

        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    private void updateSyncStatus(int id, String syncStatus) {
        updateSyncStatus(id, syncStatus, null);
    }

    private void updateSyncStatus(int id, String syncStatus, String storeId) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_MODIFIED_DATE, DateUtil.formatDBDateTime(DateUtil.now()));
        values.put(COLUMN_SYNC_STATUS, syncStatus);
        if (storeId != null) {
            values.put(COLUMN_STORE_ID, storeId);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    public String getLastSyncDate() {
        String lastSyncDate = null;
        Cursor c = contentResolver.query(URI, new String[]{COLUMN_SYNC_DATE}, null, null,
                                         COLUMN_SYNC_DATE + " DESC");

        if (c != null) {
            if (c.moveToFirst() && c.getColumnCount() > 0) {
                lastSyncDate = c.getString(0);
            }

            c.close();
        }

        return lastSyncDate;
    }

    public List<Distributor> getDistributors(String selection, String[] selectionArgs) {
        List<Distributor> distributors = new ArrayList<>();
        Cursor cursor = contentResolver.query(
                CRMContentProvider.URI_DISTRIBUTOR,
                DistributorTable.ALL_COLUMNS, selection, selectionArgs, null);

        if (cursor != null) {
            distributors = Parser.cursorToDistributorList(cursor);
            cursor.close();
        }

        return distributors;
    }
}
