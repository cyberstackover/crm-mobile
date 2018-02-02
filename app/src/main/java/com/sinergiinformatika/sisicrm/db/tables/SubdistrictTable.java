package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Subdistrict;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 17-Feb-15.
 */
public class SubdistrictTable {

    public static final String TABLE_NAME = "subdistrict";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUBDISTRICT_ID = "subdistrict_id";
    public static final String COLUMN_SUBDISTRICT_NAME = "subdistrict_name";
    public static final String COLUMN_SUBDISTRICT_NAME_LOWERCASE = "subdistrict_name_lowercase";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_CITY_ID = CityTable.COLUMN_CITY_ID;
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_SUBDISTRICT_ID, COLUMN_SUBDISTRICT_NAME, COLUMN_CITY_ID,
            COLUMN_SYNC_DATE
    };
    private static final String TAG = SubdistrictTable.class.getSimpleName();
    private static final String CREATE_TABLE;
    private static final Uri URI = CRMContentProvider.URI_SUBDISTRICT;

    static {
        CREATE_TABLE = new StringBuilder().append("create table ")
                                          .append(TABLE_NAME)
                                          .append(" (")
                                          .append(COLUMN_ID)
                                          .append(" integer primary key, ")
                                          .append(COLUMN_SUBDISTRICT_ID)
                                          .append(" text not null, ")
                                          .append(COLUMN_SUBDISTRICT_NAME)
                                          .append(" text not null, ")
                                          .append(COLUMN_SUBDISTRICT_NAME_LOWERCASE)
                                          .append(" text not null, ")
                                          .append(COLUMN_CITY_ID)
                                          .append(" text not null, ")
                                          .append(COLUMN_SYNC_DATE)
                                          .append(" text)")
                                          .toString();
    }

    private Context context;

    public SubdistrictTable(Context context) {
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

    public static void setValues(Cursor cursor, Subdistrict subdistrict) {
        subdistrict.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        subdistrict
                .setSubdistrictId(cursor.getString(cursor.getColumnIndex(COLUMN_SUBDISTRICT_ID)));
        subdistrict.setSubdistrictName(
                cursor.getString((cursor.getColumnIndex(COLUMN_SUBDISTRICT_NAME))));
        subdistrict.setCityId(cursor.getString(cursor.getColumnIndex(COLUMN_CITY_ID)));
    }

    public static void setValues(Subdistrict subdistrict, ContentValues values) {
        values.put(COLUMN_SUBDISTRICT_ID, subdistrict.getSubdistrictId());
        values.put(COLUMN_SUBDISTRICT_NAME, subdistrict.getSubdistrictName());
        values.put(COLUMN_SUBDISTRICT_NAME_LOWERCASE,
                   subdistrict.getSubdistrictName().toLowerCase());
        values.put(COLUMN_CITY_ID, subdistrict.getCityId());
        values.put(COLUMN_SYNC_DATE, subdistrict.getSyncDate());
    }

    public void save(Subdistrict subdistrict) {
        Subdistrict s = getBySubdistrictId(subdistrict.getSubdistrictId());

        if (s == null) {
            insert(subdistrict);
        } else {
            updateById(s.getId(), subdistrict);
        }
    }

    public void insert(Subdistrict subdistrict) {
        if (Constants.DEBUG) {
            Log.d(TAG, "insert subdistrict");
            Log.d(TAG, "data = " + subdistrict.getSubdistrictId() + " " +
                       subdistrict.getSubdistrictName() + " " + subdistrict.getCityId());
        }

        ContentValues values = new ContentValues();
        setValues(subdistrict, values);
        context.getContentResolver().insert(URI, values);
    }

    public void updateById(int id, Subdistrict subdistrict) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updateById, id = " + id);
        }

        ContentValues values = new ContentValues();
        setValues(subdistrict, values);
        Uri uri = Uri.parse(URI + "/" + id);
        context.getContentResolver().update(uri, values, null, null);
    }


    public Subdistrict getBySubdistrictId(String subdistrictId) {

        Cursor c = context.getContentResolver()
                          .query(URI, ALL_COLUMNS, COLUMN_SUBDISTRICT_ID + " = ?",
                                 new String[]{subdistrictId}, null);

        if (c != null && c.moveToFirst()) {
            Subdistrict s = new Subdistrict();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public Subdistrict getById(int id) {

        if (Constants.DEBUG) {
            Log.d(TAG, "getById, id = " + id);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        Cursor c = context.getContentResolver().query(uri, ALL_COLUMNS, null, null, null);

        if (c != null && c.moveToFirst()) {
            Subdistrict s = new Subdistrict();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public List<Subdistrict> findByCityId(String cityId) {

        Cursor c = context.getContentResolver()
                          .query(URI, ALL_COLUMNS, COLUMN_CITY_ID + " = ?", new String[]{cityId},
                                 null);

        List<Subdistrict> subdistrictList = new ArrayList<Subdistrict>();

        if (c == null) {
            return subdistrictList;
        }

        if (c.moveToFirst()) {
            do {
                Subdistrict s = new Subdistrict();
                setValues(c, s);
                subdistrictList.add(s);
            } while (c.moveToNext());
        }

        c.close();

        return subdistrictList;
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
        String where = COLUMN_SUBDISTRICT_ID + " in (";

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
