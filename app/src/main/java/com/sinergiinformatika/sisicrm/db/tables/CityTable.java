package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.City;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 17-Feb-15.
 */
public class CityTable {

    public static final String TABLE_NAME = "city";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CITY_ID = "city_id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_CITY_NAME_LOWERCASE = "city_name_lowercase";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_PROVINCE_ID = ProvinceTable.COLUMN_PROVINCE_ID;
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_CITY_ID, COLUMN_CITY_NAME, COLUMN_PROVINCE_ID, COLUMN_SYNC_DATE
    };
    private static final String TAG = CityTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_CITY;
    private static final String CREATE_TABLE;

    static {
        CREATE_TABLE = new StringBuilder().append("create table ")
                                          .append(TABLE_NAME)
                                          .append(" (")
                                          .append(COLUMN_ID)
                                          .append(" integer primary key, ")
                                          .append(COLUMN_CITY_ID)
                                          .append(" text not null, ")
                                          .append(COLUMN_CITY_NAME)
                                          .append(" text not null, ")
                                          .append(COLUMN_CITY_NAME_LOWERCASE)
                                          .append(" text not null, ")
                                          .append(COLUMN_PROVINCE_ID)
                                          .append(" text not null, ")
                                          .append(COLUMN_SYNC_DATE)
                                          .append(" text)")
                                          .toString();
    }

    private Context context;

    public CityTable(Context context) {
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


    public static void setValues(Cursor cursor, City city) {
        city.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        city.setCityId(cursor.getString(cursor.getColumnIndex(COLUMN_CITY_ID)));
        city.setCityName(cursor.getString(cursor.getColumnIndex(COLUMN_CITY_NAME)));
        city.setProvinceId(cursor.getString((cursor.getColumnIndex(COLUMN_PROVINCE_ID))));
    }

    public static void setValues(City city, ContentValues values) {
        values.put(COLUMN_CITY_ID, city.getCityId());
        values.put(COLUMN_CITY_NAME, city.getCityName());
        values.put(COLUMN_CITY_NAME_LOWERCASE, city.getCityName().toLowerCase());
        values.put(COLUMN_PROVINCE_ID, city.getProvinceId());
        values.put(COLUMN_SYNC_DATE, city.getSyncDate());
    }

    public void save(City city) {
        City s = getByCityId(city.getCityId());

        if (s == null) {
            insert(city);
        } else {
            updateById(s.getId(), city);
        }
    }

    public void insert(City city) {
        if (Constants.DEBUG) {
            Log.d(TAG, "insert city");
            Log.d(TAG, "data = " + city.getCityId() + " " + city.getCityName() + " " +
                       city.getProvinceId());
        }

        ContentValues values = new ContentValues();
        setValues(city, values);
        context.getContentResolver().insert(URI, values);
    }

    public void updateById(int id, City city) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updateById, id = " + id);
        }

        ContentValues values = new ContentValues();
        setValues(city, values);
        Uri uri = Uri.parse(URI + "/" + id);
        context.getContentResolver().update(uri, values, null, null);
    }

    public City getByCityId(String cityId) {

        Cursor c = context.getContentResolver()
                          .query(URI, ALL_COLUMNS, COLUMN_CITY_ID + " = ?", new String[]{cityId},
                                 null);

        if (c != null && c.moveToFirst()) {
            City s = new City();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public City getById(int id) {

        if (Constants.DEBUG) {
            Log.d(TAG, "getById, id = " + id);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        Cursor c = context.getContentResolver().query(uri, ALL_COLUMNS, null, null, null);

        if (c != null && c.moveToFirst()) {
            City s = new City();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public List<City> findByProvinceId(String provinceId) {

        Cursor c = context.getContentResolver().query(URI, ALL_COLUMNS, COLUMN_PROVINCE_ID + " = ?",
                                                      new String[]{provinceId}, null);

        List<City> cities = new ArrayList<City>();

        if (c == null) {
            return cities;
        }

        if (c.moveToFirst()) {
            do {
                City s = new City();
                setValues(c, s);
                cities.add(s);
            } while (c.moveToNext());
        }

        c.close();

        return cities;
    }


    public boolean isEmpty() {

        Cursor c = context.getContentResolver()
                          .query(URI, new String[]{StoreTable.COLUMN_ID}, null, null, null);
        if (c == null) {
            return true;
        }

        if (c.moveToFirst()) {
            c.close();
            return false;
        }

        return true;
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
        String where = COLUMN_CITY_ID + " in (";

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
