package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 08-Jan-15.
 */
public class ComplainTable {

    public static final String TABLE_NAME = "complain";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMPLAIN_ID = "complain_id";
    public static final String COLUMN_COMPLAIN_TEXT = "complain_text";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_MODIFIED_DATE = "modified_date";
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_COMPLAIN_ID, COLUMN_COMPLAIN_TEXT,
            COLUMN_SYNC_DATE, COLUMN_MODIFIED_DATE
    };
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
                                               COLUMN_ID + " integer primary key" + ", " +
                                               COLUMN_COMPLAIN_ID + " text" + ", " +
                                               COLUMN_COMPLAIN_TEXT + " text" + ", " +
                                               COLUMN_SYNC_DATE + " text" + ", " +
                                               COLUMN_MODIFIED_DATE + " text" + ");";
    private static final String TAG = ComplainTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_COMPLAIN;
    private ContentResolver contentResolver;

    public ComplainTable(Context context) {
        contentResolver = context.getContentResolver();
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 48) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    private void setValues(Cursor cursor, ItemComplain complain) {
        complain.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        complain.setComplainId(cursor.getString(cursor.getColumnIndex(COLUMN_COMPLAIN_ID)));
        complain.setComplain(cursor.getString(cursor.getColumnIndex(COLUMN_COMPLAIN_TEXT)));
        complain.setModifiedDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIFIED_DATE)));
    }

    private void setValues(ItemComplain complain, ContentValues values) {
        values.put(COLUMN_COMPLAIN_ID, complain.getComplainId());
        values.put(COLUMN_COMPLAIN_TEXT, complain.getComplain());
        values.put(COLUMN_MODIFIED_DATE, TextUtils.isEmpty(complain.getModifiedDate()) ?
                DateUtil.formatDBDateTime(DateUtil.now()) : complain.getModifiedDate());
    }


    public void insert(ItemComplain complain) {
        //aLog.d(TAG,"insert store");
        ContentValues values = new ContentValues();
        setValues(complain, values);

        contentResolver.insert(URI, values);
    }

    public void updateById(int id, ItemComplain complain) {
        //Log.d(TAG, "update survey, id = " + id);
        ContentValues values = new ContentValues();
        setValues(complain, values);

        Uri uri = Uri.parse(URI + "/" + id);

        contentResolver.update(uri, values, null, null);
    }

    public void updateByComplainId(String complainId, ItemComplain complain) {
        ContentValues values = new ContentValues();
        setValues(complain, values);

        contentResolver.update(URI, values, COLUMN_COMPLAIN_ID + " = ?", new String[]{complainId});
    }

    public void delete(ItemComplain complain) {
        String selection = null;
        String[] selectionArgs = null;
        Uri uri;

        if (complain.getId() == null || complain.getId() <= 0) {
            uri = URI;
            selection = COLUMN_COMPLAIN_ID + " = ?";
            selectionArgs = new String[]{complain.getComplainId()};
        } else {
            uri = Uri.parse(URI + "/" + complain.getId());
        }

        contentResolver.delete(uri, selection, selectionArgs);
    }

    public void sync(ItemComplain complain) {
        //Log.d(TAG,"sync complain");
        ItemComplain existComplain = getByComplainId(complain.getComplainId());

        complain.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));

        if (existComplain != null) {
            if (existComplain.getId() != null && existComplain.getId() > 0) {
                updateById(existComplain.getId(), complain);
            } else {
                updateByComplainId(existComplain.getComplainId(), complain);
            }
        } else {
            insert(complain);
        }
    }

    public List<ItemComplain> getAll(String selections, String[] selectionArgs) {

        List<ItemComplain> complains = new ArrayList<>();

        Cursor c = contentResolver.query(URI, ALL_COLUMNS, selections,
                                         selectionArgs, null);
        if (c == null) {
            return complains;
        }

        if (c.moveToFirst()) {
            do {
                ItemComplain com = new ItemComplain();
                setValues(c, com);
                complains.add(com);
            } while (c.moveToNext());
        }
        c.close();

        return complains;
    }

    public ItemComplain getByComplainId(String complainId) {
        //Log.d(TAG, "get complain, complainId = " + complainId);

        Cursor c = contentResolver.query(URI, ALL_COLUMNS,
                                         COLUMN_COMPLAIN_ID + " = ?",
                                         new String[]{complainId}, null);

        if (c != null && c.moveToFirst()) {
            ItemComplain complain = new ItemComplain();
            setValues(c, complain);
            c.close();
            return complain;
        }

        return null;
    }

    public String getLastSyncDate() {
        String lastSyncDate = null;
        Cursor c = contentResolver.query(URI, new String[]{COLUMN_MODIFIED_DATE}, null, null,
                                         COLUMN_MODIFIED_DATE + " DESC");

        if (c != null) {
            if (c.moveToFirst() && c.getColumnCount() > 0) {
                lastSyncDate = c.getString(0);
            }

            c.close();
        }

        return lastSyncDate;
    }

}
