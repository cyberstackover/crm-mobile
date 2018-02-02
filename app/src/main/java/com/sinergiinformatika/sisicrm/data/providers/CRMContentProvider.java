package com.sinergiinformatika.sisicrm.data.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.db.DbHelper;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.CityTable;
import com.sinergiinformatika.sisicrm.db.tables.CompetitorTable;
import com.sinergiinformatika.sisicrm.db.tables.ComplainTable;
import com.sinergiinformatika.sisicrm.db.tables.DistributorTable;
import com.sinergiinformatika.sisicrm.db.tables.NoteTable;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;
import com.sinergiinformatika.sisicrm.db.tables.ProvinceTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SubdistrictTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;

/**
 * Created by wendi on 30-Dec-14.
 * referensi http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
public class CRMContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.sinergiinformatika.sisicrm.providers";
    private static final String TAG = CRMContentProvider.class.getSimpleName();

    private static final int STORES = 1;
    private static final int STORE_ID = 10;
    private static final int AGENDA = 2;
    private static final int AGENDA_ID = 20;
    private static final int AGENDA_NO_JOIN = 21;
    private static final int DISTRIBUTOR = 3;
    private static final int SURVEY = 4;
    private static final int SURVEY_ID = 40;
    private static final int COMPLAIN = 5;
    private static final int COMPLAIN_ID = 50;
    private static final int PRODUCT = 6;
    private static final int PRODUCT_ID = 60;
    private static final int PROVINCE = 7;
    private static final int PROVINCE_ID = 70;
    private static final int CITY = 8;
    private static final int CITY_ID = 80;
    private static final int SUBDISTRICT = 9;
    private static final int SUBDISTRICT_ID = 90;
    private static final int ORDERS = 11;
    private static final int ORDER_ID = 110;
    private static final int COMPETITORS = 12;
    private static final int COMPETITOR_ID = 120;
    private static final int NOTES = 13;
    private static final int NOTE_ID = 130;

    private static final String BASE_STORES = "stores";
    public static final Uri URI_STORES = Uri.parse("content://" + AUTHORITY + "/" + BASE_STORES);

    private static final String BASE_AGENDA = "agenda";
    public static final Uri URI_AGENDA = Uri.parse("content://" + AUTHORITY + "/" + BASE_AGENDA);

    private static final String BASE_AGENDA_NO_JOIN = "agenda_no_join";
    public static final Uri URI_AGENDA_NO_JOIN =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_AGENDA_NO_JOIN);

    private static final String BASE_DISTRIBUTOR = "distributor";
    public static final Uri URI_DISTRIBUTOR =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_DISTRIBUTOR);

    private static final String BASE_SURVEY = "survey";
    public static final Uri URI_SURVEY = Uri.parse("content://" + AUTHORITY + "/" + BASE_SURVEY);

    private static final String BASE_PRODUCT = "product";
    public static final Uri URI_PRODUCT = Uri.parse("content://" + AUTHORITY + "/" + BASE_PRODUCT);

    private static final String BASE_COMPLAIN = "complain";
    public static final Uri URI_COMPLAIN =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_COMPLAIN);

    private static final String BASE_ORDERS = "orders";
    public static final Uri URI_ORDERS = Uri.parse("content://" + AUTHORITY + "/" + BASE_ORDERS);

    private static final String BASE_PROVINCE = "province";
    public static final Uri URI_PROVINCE =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PROVINCE);

    private static final String BASE_CITY = "city";
    public static final Uri URI_CITY = Uri.parse("content://" + AUTHORITY + "/" + BASE_CITY);

    private static final String BASE_SUBDISTRICT = "subdistrict";
    public static final Uri URI_SUBDISTRICT =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_SUBDISTRICT);

    private static final String BASE_COMPETITORS = "competitor_programs";
    public static final Uri URI_COMPETITORS =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_COMPETITORS);

    private static final String BASE_NOTES = "notes";
    public static final Uri URI_NOTES = Uri.parse("content://" + AUTHORITY + "/" + BASE_NOTES);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_STORES, STORES);
        uriMatcher.addURI(AUTHORITY, BASE_STORES + "/#", STORE_ID);

        uriMatcher.addURI(AUTHORITY, BASE_AGENDA, AGENDA);
        uriMatcher.addURI(AUTHORITY, BASE_AGENDA + "/#", AGENDA_ID);
        uriMatcher.addURI(AUTHORITY, BASE_AGENDA_NO_JOIN, AGENDA_NO_JOIN);

        uriMatcher.addURI(AUTHORITY, BASE_DISTRIBUTOR, DISTRIBUTOR);

        uriMatcher.addURI(AUTHORITY, BASE_SURVEY, SURVEY);
        uriMatcher.addURI(AUTHORITY, BASE_SURVEY + "/#", SURVEY_ID);

        uriMatcher.addURI(AUTHORITY, BASE_COMPLAIN, COMPLAIN);
        uriMatcher.addURI(AUTHORITY, BASE_COMPLAIN + "/#", COMPLAIN_ID);

        uriMatcher.addURI(AUTHORITY, BASE_PRODUCT, PRODUCT);
        uriMatcher.addURI(AUTHORITY, BASE_PRODUCT + "/#", PRODUCT_ID);

        uriMatcher.addURI(AUTHORITY, BASE_PROVINCE, PROVINCE);
        uriMatcher.addURI(AUTHORITY, BASE_PROVINCE + "/#", PROVINCE_ID);

        uriMatcher.addURI(AUTHORITY, BASE_CITY, CITY);
        uriMatcher.addURI(AUTHORITY, BASE_CITY + "/#", CITY_ID);

        uriMatcher.addURI(AUTHORITY, BASE_SUBDISTRICT, SUBDISTRICT);
        uriMatcher.addURI(AUTHORITY, BASE_SUBDISTRICT + "/#", SUBDISTRICT_ID);

        uriMatcher.addURI(AUTHORITY, BASE_ORDERS, ORDERS);
        uriMatcher.addURI(AUTHORITY, BASE_ORDERS + "/#", ORDER_ID);

        uriMatcher.addURI(AUTHORITY, BASE_COMPETITORS, COMPETITORS);
        uriMatcher.addURI(AUTHORITY, BASE_COMPETITORS + "/#", COMPETITOR_ID);

        uriMatcher.addURI(AUTHORITY, BASE_NOTES, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_NOTES + "/#", NOTE_ID);
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case STORE_ID:
                queryBuilder.appendWhere(StoreTable.COLUMN_ID + " = " + uri.getLastPathSegment());
            case STORES:
                queryBuilder.setTables(StoreTable.TABLE_NAME);
                break;
            case AGENDA_NO_JOIN:
                queryBuilder.setTables(AgendaTable.TABLE_NAME);
                break;
            case AGENDA_ID:
                queryBuilder.appendWhere(AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_ID
                                         + " = " + uri.getLastPathSegment());
            case AGENDA:
                String tableName = AgendaTable.TABLE_NAME
                                   + " JOIN " + StoreTable.TABLE_NAME + " ON (" +
                                   AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_STORE_ID +
                                   " = " + StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_ID + ") "
                                   + " LEFT JOIN " + SurveyTable.TABLE_NAME + " ON (" +
                                   SurveyTable.TABLE_NAME + "." + SurveyTable.COLUMN_ID + " = " +
                                   AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_SURVEY_DB_ID +
                                   ")";
                queryBuilder.setTables(tableName);
                break;
            case DISTRIBUTOR:
                queryBuilder.setTables(DistributorTable.TABLE_NAME);
                break;
            case SURVEY_ID:
                queryBuilder.appendWhere(SurveyTable.COLUMN_ID + " = " + uri.getLastPathSegment());
                break;
            case SURVEY:
                queryBuilder.setTables(SurveyTable.TABLE_NAME);
                break;
            case PRODUCT_ID:
                queryBuilder.appendWhere(ProductTable.COLUMN_ID + " = " + uri.getLastPathSegment());
                break;
            case PRODUCT:
                queryBuilder.setTables(ProductTable.TABLE_NAME);
                break;
            case COMPLAIN_ID:
                queryBuilder
                        .appendWhere(ComplainTable.COLUMN_ID + " = " + uri.getLastPathSegment());
                break;
            case COMPLAIN:
                queryBuilder.setTables(ComplainTable.TABLE_NAME);
                break;
            case PROVINCE:
                queryBuilder.setTables(ProvinceTable.TABLE_NAME);
                break;
            case CITY:
                queryBuilder.setTables(CityTable.TABLE_NAME);
                break;
            case SUBDISTRICT:
                queryBuilder.setTables(SubdistrictTable.TABLE_NAME);
                break;
            case ORDER_ID:
                queryBuilder.appendWhere(OrderTable.COLUMN_ID + " = " + uri.getLastPathSegment());
            case ORDERS:
                queryBuilder.setTables(OrderTable.TABLE_NAME);
                break;
            case COMPETITOR_ID:
                queryBuilder
                        .appendWhere(CompetitorTable.COLUMN_ID + " = " + uri.getLastPathSegment());
            case COMPETITORS:
                queryBuilder.setTables(CompetitorTable.TABLE_NAME);
                break;
            case NOTE_ID:
                queryBuilder.appendWhere(NoteTable.COLUMN_ID + " = " + uri.getLastPathSegment());
            case NOTES:
                queryBuilder.setTables(NoteTable.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null,
                                           null, sortOrder);

        if (Constants.DEBUG) {
            Log.d(TAG, "query: " + queryBuilder.buildQuery(
                    projection, selection, null, null, sortOrder, null));
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        boolean bypassInsert = false;
        int uriType = uriMatcher.match(uri);
        long id = 0;
        String basePath, tableName;
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        switch (uriType) {
            case AGENDA:
                basePath = BASE_AGENDA;
                tableName = AgendaTable.TABLE_NAME;
                Cursor cursor = query(URI_AGENDA_NO_JOIN, AgendaTable.ALL_COLUMNS,
                                      AgendaTable.COLUMN_AGENDA_DATE + " = ?" + " and " +
                                      AgendaTable.COLUMN_STORE_ID + " = ?",
                                      new String[]{
                                              values.getAsString(AgendaTable.COLUMN_AGENDA_DATE),
                                              values.getAsString(AgendaTable.COLUMN_STORE_ID)},
                                      null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_ID));
                    bypassInsert = true;
                    cursor.close();
                }
                break;
            case STORES:
                basePath = BASE_STORES;
                tableName = StoreTable.TABLE_NAME;
                break;
            case DISTRIBUTOR:
                basePath = BASE_DISTRIBUTOR;
                tableName = DistributorTable.TABLE_NAME;
                id = database.insertWithOnConflict(
                        tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                bypassInsert = true;
                break;
            case SURVEY:
                basePath = BASE_SURVEY;
                tableName = SurveyTable.TABLE_NAME;
                break;
            case PRODUCT:
                basePath = BASE_PRODUCT;
                tableName = ProductTable.TABLE_NAME;
                break;
            case COMPLAIN:
                basePath = BASE_COMPLAIN;
                tableName = ComplainTable.TABLE_NAME;
                break;
            case PROVINCE:
                basePath = BASE_PROVINCE;
                tableName = ProvinceTable.TABLE_NAME;
                break;
            case CITY:
                basePath = BASE_CITY;
                tableName = CityTable.TABLE_NAME;
                break;
            case SUBDISTRICT:
                basePath = BASE_SUBDISTRICT;
                tableName = SubdistrictTable.TABLE_NAME;
                break;
            case ORDERS:
                basePath = BASE_ORDERS;
                tableName = OrderTable.TABLE_NAME;
                break;
            case COMPETITORS:
                basePath = BASE_COMPETITORS;
                tableName = CompetitorTable.TABLE_NAME;
                break;
            case NOTES:
                basePath = BASE_NOTES;
                tableName = NoteTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for insert: " + uri);
        }

        if (!bypassInsert) {
            id = database.insert(tableName, null, values);
        }

        if (id < 0) {
            return null;
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return Uri.parse(basePath + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int affected;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String where = TextUtils.isEmpty(selection) ? "" : " and " + selection;

        switch (uriType) {
            case AGENDA_ID:
                affected = database.delete(AgendaTable.TABLE_NAME,
                                           AgendaTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() + where, selectionArgs);
                break;
            case AGENDA:
                affected = database.delete(AgendaTable.TABLE_NAME, selection, selectionArgs);
                break;
            case SURVEY:
                affected = database.delete(SurveyTable.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                affected = database.delete(ProductTable.TABLE_NAME,
                                           ProductTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() + where, selectionArgs);
                break;
            case PRODUCT:
                affected = database.delete(ProductTable.TABLE_NAME, selection, selectionArgs);
                break;
            case COMPLAIN_ID:
                affected = database.delete(ComplainTable.TABLE_NAME,
                                           ComplainTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() + where, selectionArgs);
                break;
            case COMPLAIN:
                affected = database.delete(ComplainTable.TABLE_NAME, selection, selectionArgs);
                break;
            case STORES:
                affected = database.delete(StoreTable.TABLE_NAME, selection, selectionArgs);
                break;
            case PROVINCE:
                affected = database.delete(ProvinceTable.TABLE_NAME, selection, selectionArgs);
                break;
            case CITY:
                affected = database.delete(CityTable.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBDISTRICT:
                affected = database.delete(SubdistrictTable.TABLE_NAME, selection, selectionArgs);
                break;
            case DISTRIBUTOR:
                affected = database.delete(DistributorTable.TABLE_NAME, selection, selectionArgs);
                break;
            case ORDER_ID:
                affected = database.delete(OrderTable.TABLE_NAME,
                                           OrderTable.COLUMN_ID + " = " + uri.getLastPathSegment() +
                                           where, selectionArgs);
                break;
            case ORDERS:
                affected = database.delete(OrderTable.TABLE_NAME, selection, selectionArgs);
                break;
            case COMPETITOR_ID:
                affected = database.delete(CompetitorTable.TABLE_NAME,
                                           CompetitorTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() + where, selectionArgs);
                break;
            case COMPETITORS:
                affected = database.delete(CompetitorTable.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTES:
                affected = database.delete(NoteTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown or unimplemented URI: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affected;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int affected;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String where = TextUtils.isEmpty(selection) ? "" : " and " + selection;

        switch (uriType) {
            case AGENDA_ID:
                affected = database.update(AgendaTable.TABLE_NAME, values,
                                           AgendaTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() + where, selectionArgs);
                break;
            case AGENDA:
                affected =
                        database.update(AgendaTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STORE_ID:
                affected = database.update(StoreTable.TABLE_NAME, values,
                                           StoreTable.COLUMN_ID + " = " + uri.getLastPathSegment(),
                                           selectionArgs);
                break;
            case STORES:
                affected = database.update(StoreTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SURVEY_ID:
                affected = database.update(SurveyTable.TABLE_NAME, values,
                                           SurveyTable.COLUMN_ID + " = " + uri.getLastPathSegment(),
                                           selectionArgs);
                break;
            case SURVEY:
                affected =
                        database.update(SurveyTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                affected = database.update(ProductTable.TABLE_NAME, values,
                                           ProductTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment(), selectionArgs);
                break;
            case COMPLAIN_ID:
                affected = database.update(ComplainTable.TABLE_NAME, values,
                                           ComplainTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment(), selectionArgs);
                break;
            case ORDER_ID:
                affected = database.update(OrderTable.TABLE_NAME, values,
                                           OrderTable.COLUMN_ID + " = " + uri.getLastPathSegment() +
                                           where, selectionArgs);
                break;
            case ORDERS:
                affected = database.update(OrderTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COMPETITOR_ID:
                affected = database.update(CompetitorTable.TABLE_NAME, values,
                                           CompetitorTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() +
                                           where, selectionArgs);
                break;
            case COMPETITORS:
                affected = database.update(CompetitorTable.TABLE_NAME, values, selection,
                                           selectionArgs);
                break;
            case PROVINCE_ID:
                affected = database.update(ProvinceTable.TABLE_NAME, values,
                                           ProvinceTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() +
                                           where, selectionArgs);
                break;
            case CITY_ID:
                affected = database.update(CityTable.TABLE_NAME, values,
                                           CityTable.COLUMN_ID + " = " + uri.getLastPathSegment() +
                                           where, selectionArgs);
                break;
            case SUBDISTRICT_ID:
                affected = database.update(SubdistrictTable.TABLE_NAME, values,
                                           SubdistrictTable.COLUMN_ID + " = " +
                                           uri.getLastPathSegment() +
                                           where, selectionArgs);
                break;
            case DISTRIBUTOR:
                affected = database.update(DistributorTable.TABLE_NAME, values, selection,
                                           selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown or unimplemented URI: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affected;
    }
}
