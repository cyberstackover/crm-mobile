package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Province;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 17-Feb-15.
 */
public class ProvinceTable {

    public static final String TABLE_NAME = "province";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROVINCE_ID = "province_id";
    public static final String COLUMN_PROVINCE_NAME = "province_name";
    public static final String COLUMN_PROVINCE_NAME_LOWERCASE = "province_name_lowercase";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_PROVINCE_ID, COLUMN_PROVINCE_NAME, COLUMN_SYNC_DATE
    };
    private static final String TAG = ProvinceTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_PROVINCE;
    private static final String CREATE_TABLE;

    static {
        CREATE_TABLE  = new StringBuilder()
                .append("create table ")
                .append(TABLE_NAME)
                .append(" (")
                .append(COLUMN_ID)
                .append(" integer primary key, ")
                .append(COLUMN_PROVINCE_ID)
                .append(" text not null, ")
                .append(COLUMN_PROVINCE_NAME)
                .append(" text not null, ")
                .append(COLUMN_PROVINCE_NAME_LOWERCASE)
                .append(" text not null, ")
                .append(COLUMN_SYNC_DATE)
                .append(" text)")
                .toString();
    }

    private Context context;


    public ProvinceTable(Context context) {
        this.context = context;
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 48) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } else {
            if (oldVersion < 50) {  // update for db version 50
                db.execSQL(
                        "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_SYNC_DATE + " TEXT");
            }
        }
    }



    public static void setValues(Cursor cursor, Province province) {
        province.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        province.setProvinceId(cursor.getString((cursor.getColumnIndex(COLUMN_PROVINCE_ID))));
        province.setProvinceName(cursor.getString(cursor.getColumnIndex(COLUMN_PROVINCE_NAME)));
    }

    public static void setValues(Province province, ContentValues values) {
        values.put(COLUMN_PROVINCE_ID, province.getProvinceId());
        values.put(COLUMN_PROVINCE_NAME, province.getProvinceName());
        values.put(COLUMN_PROVINCE_NAME_LOWERCASE, province.getProvinceName().toLowerCase());
        values.put(COLUMN_SYNC_DATE, province.getSyncDate());
    }

    public void save(Province province) {
        Province s = getByProvinceId(province.getProvinceId());

        if (s == null) {
            insert(province);
        } else {
            updateById(s.getId(), province);
        }
    }

    public void insert(Province province) {
        if (Constants.DEBUG) {
            Log.d(TAG, "insert province");
        }
        ContentValues values = new ContentValues();
        setValues(province, values);
        context.getContentResolver().insert(URI, values);
    }

    public void updateById(int id, Province province) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updateById, id = " + id);
        }

        ContentValues values = new ContentValues();
        setValues(province, values);
        Uri uri = Uri.parse(URI + "/" + id);
        context.getContentResolver().update(uri, values, null, null);
    }


    public Province getByProvinceId(String provinceId) {

        Cursor c = context.getContentResolver().query(URI, ALL_COLUMNS, COLUMN_PROVINCE_ID + " = ?",
                                                      new String[]{provinceId}, null);

        if (c != null && c.moveToFirst()) {
            Province s = new Province();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public Province getById(int id) {

        if (Constants.DEBUG) {
            Log.d(TAG, "getById, id = " + id);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        Cursor c = context.getContentResolver().query(uri, ALL_COLUMNS, null, null, null);

        if (c != null && c.moveToFirst()) {
            Province s = new Province();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }


    public boolean isEmpty() {

        Cursor c = context.getContentResolver().query(URI, new String[]{StoreTable.COLUMN_ID}, null, null, null);
        if (c == null)
            return true;

        if (c.moveToFirst()) {
            c.close();
            return false;
        }

        return true;
    }

    public List<Province> getAll() {
        return find(null);
    }

    public List<Province> find(String provinceName) {

        if (Constants.DEBUG) {
            Log.d(TAG, "find, provinceName = " + provinceName);
        }

        List<Province> provinces = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        if(provinceName != null){
            provinceName =  provinceName.toLowerCase();
            selection = COLUMN_PROVINCE_NAME_LOWERCASE + " like ?";
            selectionArgs = new String[]{"%"+provinceName+"%"};
        }

        String sortOrder = "province_name_lowercase asc";

        Cursor c = context.getContentResolver().query(URI, ALL_COLUMNS, selection, selectionArgs, sortOrder);

        if(c == null){
            return provinces;
        }

        if (c.moveToFirst()) {
            do {
                Province s = new Province();
                setValues(c, s);
                provinces.add(s);
            } while (c.moveToNext());
        }

        c.close();

        return provinces;
    }

    public String getLastSyncDate() {
        String lastSyncDate = null;
        Cursor c = context.getContentResolver().query(URI, new String[]{COLUMN_SYNC_DATE}, null,
                                                      null, COLUMN_SYNC_DATE + " DESC");

        if (c != null) {
            if (c.moveToFirst() && c.getColumnCount() > 0) {
                lastSyncDate = c.getString(c.getColumnIndex(COLUMN_SYNC_DATE));
            }

            c.close();
        }

        return lastSyncDate;
    }

    public void deleteByAreaId(String[] areaIds) {
        String where = COLUMN_PROVINCE_ID + " in (";

        for (int i = 0; i < areaIds.length; i++) {
            if (i > 0 && i < (areaIds.length - 1)) {
                where += ",";
            }
            where += "?";
//            i++;
        }

        where += ")";

        context.getContentResolver().delete(URI, where, areaIds);
    }
}
