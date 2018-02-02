package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

/**
 * Created by Mark on 1/7/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class DistributorTable {
    public static final String TABLE_NAME = "distributor";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DISTRIBUTOR_ID = "distributor_id";
    public static final String COLUMN_DISTRIBUTOR_NAME = "distributor_name";
    private static final String COLUMN_SYNC_DATE = "sync_date";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                                                    COLUMN_ID +
                                                    " integer primary key autoincrement, " +
                                                    COLUMN_DISTRIBUTOR_ID +
                                                    " text not null unique, " +
                                                    COLUMN_DISTRIBUTOR_NAME + " text not null, " +
                                                    COLUMN_SYNC_DATE + " text" +
                                                    ");";
    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_DISTRIBUTOR_ID, COLUMN_DISTRIBUTOR_NAME, COLUMN_SYNC_DATE};

    private static final String TAG = DistributorTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_DISTRIBUTOR;

    private ContentResolver contentResolver;

    public DistributorTable(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public static void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "creating table: " + TABLE_NAME);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrading from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 15) {
            onCreate(sqLiteDatabase);
        } else if (oldVersion < 16) {
            Log.w(TAG, "Upgrading database from version "
                       + oldVersion + " to " + newVersion
                       + ", which will destroy all old data");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        } else {
            if (oldVersion < 51) {  // update for db version 51
                Log.w(TAG, "Upgrading database from version "
                           + oldVersion + " to " + newVersion);
                sqLiteDatabase.execSQL(
                        "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_SYNC_DATE + " TEXT");
            }
        }
    }

    public static void setValues(Distributor source, ContentValues target) {

        target.put(COLUMN_DISTRIBUTOR_ID, source.getId());
        target.put(COLUMN_DISTRIBUTOR_NAME, source.getName());
        target.put(COLUMN_SYNC_DATE, source.getSyncDate());
    }

    private void setValues(Cursor c, Distributor d) {
        d.setDbId(c.getLong(c.getColumnIndex(COLUMN_ID)));
        d.setId(c.getString(c.getColumnIndex(COLUMN_DISTRIBUTOR_ID)));
        d.setName(c.getString(c.getColumnIndex(COLUMN_DISTRIBUTOR_NAME)));
        d.setSyncDate(c.getString(c.getColumnIndex(COLUMN_SYNC_DATE)));
    }

    public Distributor getByDistributorId(String id) {
        Cursor c = contentResolver
                .query(URI, ALL_COLUMNS, COLUMN_DISTRIBUTOR_ID + " = ?",
                       new String[]{id}, null);

        if (c != null && c.moveToFirst()) {
            Distributor d = new Distributor();
            setValues(c, d);
            c.close();
            return d;
        }

        return null;
    }

    public void save(Distributor distributor) {
        Distributor d = getByDistributorId(distributor.getId());

        if (d == null) {
            insert(distributor);
        } else {
            updateByDistributorId(d.getId(), distributor);
        }
    }

    public void insert(Distributor distributor) {
        if (Constants.DEBUG) {
            Log.d(TAG, "insert distributor");
        }

        distributor.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));

        ContentValues values = new ContentValues();
        setValues(distributor, values);

        contentResolver.insert(URI, values);
    }

    public void updateByDistributorId(String distributorId, Distributor distributor) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updateByStoreId, distributorId = " + distributorId);
        }

        distributor.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));

        ContentValues values = new ContentValues();
        setValues(distributor, values);

        contentResolver
                .update(URI, values, COLUMN_DISTRIBUTOR_ID + " = ?", new String[]{distributorId});
    }

    public void deleteByDistributorIds(String[] ids) {
        String where = COLUMN_DISTRIBUTOR_ID + " in (";

        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                where += ",";
            }
            where += "?";
            i++;
        }

        where += ")";

        contentResolver.delete(URI, where, ids);
    }

    public String getLastSyncDate() {
        String lastSyncDate = null;
        Cursor c = contentResolver.query(URI, new String[]{COLUMN_SYNC_DATE}, null, null,
                                         COLUMN_SYNC_DATE + " DESC");

        if (c != null) {
            if (c.moveToFirst() && c.getColumnCount() > 0) {
                lastSyncDate = c.getString(c.getColumnIndex(COLUMN_SYNC_DATE));
            }

            c.close();
        }

        return lastSyncDate;
    }
}
