package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;

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
public class AgendaTable {

    public static final String TABLE_NAME = "agenda";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AGENDA_DATE = "agenda_date";
    public static final String COLUMN_STORE_ID = "store_db_id";
    public static final String COLUMN_CHECK_IN = "agenda_check_in";
    public static final String COLUMN_CHECK_OUT = "agenda_check_out";
    public static final String COLUMN_SURVEY_DB_ID = "survey_db_id";
    public static final String COLUMN_CHECK_IN_LONG = "check_in_long";
    public static final String COLUMN_CHECK_IN_LAT = "check_in_lat";
    public static final String COLUMN_CHECK_OUT_LONG = "check_out_long";
    public static final String COLUMN_CHECK_OUT_LAT = "check_out_lat";
    public static final String CREATE_TABLE_QUERY = "create table " + TABLE_NAME + " (" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_AGENDA_DATE + " text not null, " +
            COLUMN_STORE_ID + " integer not null, " +
            COLUMN_CHECK_IN + " text, " +
            COLUMN_CHECK_OUT + " text, " +
            COLUMN_CHECK_IN_LONG + " real, " +
            COLUMN_CHECK_IN_LAT + " real, " +
            COLUMN_CHECK_OUT_LONG + " real, " +
            COLUMN_CHECK_OUT_LAT + " real, " +
            COLUMN_SURVEY_DB_ID + " integer" +
            ");";
    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_AGENDA_DATE, COLUMN_STORE_ID,
            COLUMN_CHECK_IN, COLUMN_CHECK_IN_LONG, COLUMN_CHECK_IN_LAT,
            COLUMN_CHECK_OUT, COLUMN_CHECK_OUT_LONG, COLUMN_CHECK_OUT_LAT, COLUMN_SURVEY_DB_ID};

    public static final String[] COMPLETE_COLUMN = {
            TABLE_NAME + "." + COLUMN_ID,
            COLUMN_AGENDA_DATE,
            COLUMN_STORE_ID,
            COLUMN_CHECK_IN,
            COLUMN_CHECK_OUT,
            COLUMN_CHECK_IN_LONG,
            COLUMN_CHECK_IN_LAT,
            COLUMN_CHECK_OUT_LONG,
            COLUMN_CHECK_OUT_LAT,
            COLUMN_SURVEY_DB_ID,
            StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_ID,
            StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_NAME,
            StoreTable.COLUMN_CATEGORY_CODE,
            StoreTable.COLUMN_CATEGORY_LABEL,
            StoreTable.COLUMN_STREET,
            StoreTable.COLUMN_SUBDISTRICT,
            StoreTable.COLUMN_CITY,
            StoreTable.COLUMN_PROVINCE,
            StoreTable.COLUMN_ZIPCODE,
            StoreTable.COLUMN_CAPACITY,
            StoreTable.COLUMN_PHONE,
            StoreTable.COLUMN_STORE_INFORMATION,
            StoreTable.COLUMN_NFC_ID,
            StoreTable.COLUMN_LONGITUDE,
            StoreTable.COLUMN_LATITUDE,
            StoreTable.COLUMN_OWNER_NAME,
            StoreTable.COLUMN_OWNER_BIRTH_DATE,
            StoreTable.COLUMN_OWNER_RELIGION_CODE,
            StoreTable.COLUMN_OWNER_RELIGION_LABEL,
            StoreTable.COLUMN_PHONE_MOBILE,
            StoreTable.COLUMN_LAST_CHECKIN,
            SurveyTable.COLUMN_COMPETITORS,
            SurveyTable.COLUMN_IS_SURVEY,
            SurveyTable.COLUMN_IS_PHOTO,
            SurveyTable.COLUMN_IS_COMPLAIN,
            SurveyTable.COLUMN_IS_COMPETITOR,
            SurveyTable.COLUMN_IS_COMPETITOR_NOTES
    };
    private static final String TAG = AgendaTable.class.getSimpleName();
    private ContentResolver resolver;

    public AgendaTable(Context context) {
        resolver = context.getContentResolver();
    }

    public static void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "creating table: " + TABLE_NAME);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrading from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 48) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public static void setValues(Cursor cursor, Agenda agenda) {
        agenda.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        agenda.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_AGENDA_DATE)));
        agenda.setCheckInDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_CHECK_IN)));
        agenda.setCheckOutDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_CHECK_OUT)));
        agenda.setCheckInLong(cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_IN_LONG)));
        agenda.setCheckInLat(cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_IN_LAT)));
        agenda.setCheckOutLong(cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_OUT_LONG)));
        agenda.setCheckOutLat(cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_OUT_LAT)));
        agenda.setCheckOutLat(cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_OUT_LAT)));
        agenda.setStoreDbId(cursor.getLong(cursor.getColumnIndex(COLUMN_STORE_ID)));
        agenda.setStoreId(cursor.getString(cursor.getColumnIndex(StoreTable.COLUMN_STORE_ID)));
        agenda.setStoreName(cursor.getString(cursor.getColumnIndex(StoreTable.COLUMN_STORE_NAME)));

        Integer isSurvey = cursor.getInt(cursor.getColumnIndex(SurveyTable.COLUMN_IS_SURVEY));
        Integer isPhoto = cursor.getInt(cursor.getColumnIndex(SurveyTable.COLUMN_IS_PHOTO));
        Integer isComplain = cursor.getInt(cursor.getColumnIndex(SurveyTable.COLUMN_IS_COMPLAIN));

        if (isSurvey != null) {
            agenda.setIsSurvey(isSurvey);
        }

        if (isPhoto != null) {
            agenda.setIsPhoto(isPhoto);
        }

        if (isComplain != null) {
            agenda.setIsComplain(isComplain);
        }

    }

    public Agenda getById(int id) {

        String[] projection = {
                TABLE_NAME + "." + COLUMN_ID,
                COLUMN_AGENDA_DATE,
                COLUMN_STORE_ID,
                COLUMN_CHECK_IN,
                COLUMN_CHECK_OUT,
                COLUMN_CHECK_IN_LONG,
                COLUMN_CHECK_IN_LAT,
                COLUMN_CHECK_OUT_LONG,
                COLUMN_CHECK_OUT_LAT,
                StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_ID,
                StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_NAME,
                SurveyTable.COLUMN_IS_SURVEY,
                SurveyTable.COLUMN_IS_PHOTO,
                SurveyTable.COLUMN_IS_COMPLAIN
        };

        Uri uri = Uri.parse(CRMContentProvider.URI_AGENDA + "/" + id);
        Cursor c = resolver.query(uri, projection, null, null, null);

        if (c != null && c.moveToFirst()) {
            Agenda agenda = new Agenda();
            setValues(c, agenda);
            c.close();
            return agenda;
        }

        return null;
    }

    public void updateSurveyDbId(int id, int surveyDbId) {
        Log.d(TAG, "update agenda, id = " + id + ", surveyDbId = " + surveyDbId);
        ContentValues values = new ContentValues();
        values.put(AgendaTable.COLUMN_SURVEY_DB_ID, surveyDbId);
        Uri uri = Uri.parse(CRMContentProvider.URI_AGENDA + "/" + id);
        resolver.update(uri, values, null, null);
    }

    public Agenda getBySurveyDbId(int surveyDbId) {

        String[] projection = {
                TABLE_NAME + "." + COLUMN_ID,
                COLUMN_AGENDA_DATE,
                COLUMN_STORE_ID,
                COLUMN_CHECK_IN,
                COLUMN_CHECK_OUT,
                COLUMN_CHECK_IN_LONG,
                COLUMN_CHECK_IN_LAT,
                COLUMN_CHECK_OUT_LONG,
                COLUMN_CHECK_OUT_LAT,
                StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_ID,
                StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_NAME,
                SurveyTable.COLUMN_IS_SURVEY,
                SurveyTable.COLUMN_IS_PHOTO,
                SurveyTable.COLUMN_IS_COMPLAIN
        };

        String selection = COLUMN_SURVEY_DB_ID + " = " + surveyDbId;
        /*String[] selectionArgs = {String.valueOf(surveyDbId)};
        Cursor c = resolver.query(CRMContentProvider.URI_AGENDA, projection, selection, selectionArgs, null);*/
        Cursor c = resolver.query(CRMContentProvider.URI_AGENDA, projection, selection, null, null);

        if (c != null && c.moveToFirst()) {
            Agenda agenda = new Agenda();
            setValues(c, agenda);
            c.close();
            return agenda;
        }

        return null;
    }

    public List<String> getAgendaDates(int year, int month) {
        String[] projection = {COLUMN_AGENDA_DATE};
        String selection = COLUMN_AGENDA_DATE + " >= ? AND " + COLUMN_AGENDA_DATE + " <= ?";
        String[] selectionArgs = {year + "-" + String.format("%02d", (month + 1)) + "-01", year + "-" + String.format("%02d", (month + 1)) + "-31"};

        if (Constants.DEBUG)
            Log.d(TAG, "where clause: " +
                    String.format("%s >= \"%s\" AND %s <= \"%s\"", COLUMN_AGENDA_DATE, selectionArgs[0], COLUMN_AGENDA_DATE, selectionArgs[1]));

        Cursor c = resolver.query(
                CRMContentProvider.URI_AGENDA_NO_JOIN, projection, selection, selectionArgs, COLUMN_AGENDA_DATE);
        List<String> agendaDates = new ArrayList<>();

        if (c != null) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String temp = c.getString(c.getColumnIndex(COLUMN_AGENDA_DATE));
                if (Constants.DEBUG) Log.d(TAG, "agenda entry for: " + temp);
                if (!TextUtils.isEmpty(temp))
                    agendaDates.add(temp);

                c.moveToNext();
            }

            c.close();
        }

        return agendaDates;
    }
}
