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
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 08-Jan-15.
 */
public class CompetitorTable {

    public static final String TABLE_NAME = "competitor_programs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMPETITOR_PROGRAM_ID = "program_id";
    public static final String COLUMN_COMPETITOR_PROGRAM_TEXT = "program_text";
    public static final String COLUMN_MODIFIED_DATE = "modified_date";
    public static final String COLUMN_UNIT_NAME = "unit_name";
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_COMPETITOR_PROGRAM_ID, COLUMN_COMPETITOR_PROGRAM_TEXT,
            COLUMN_MODIFIED_DATE, COLUMN_UNIT_NAME
    };
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
                                               COLUMN_ID + " integer primary key" + ", " +
                                               COLUMN_COMPETITOR_PROGRAM_ID + " text" + ", " +
                                               COLUMN_COMPETITOR_PROGRAM_TEXT + " text" + ", " +
                                               COLUMN_UNIT_NAME + " text" + ", " +
                                               COLUMN_MODIFIED_DATE + " text" + ");";
    private static final String TAG = CompetitorTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_COMPETITORS;

    private ContentResolver contentResolver;

    public CompetitorTable(Context context) {
        contentResolver = context.getContentResolver();
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 48) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } else if (oldVersion < 52) {
            Log.w(TAG, "Upgrading database from version "
                       + oldVersion + " to " + newVersion);
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_UNIT_NAME + " TEXT");
        }
    }

    private void setValues(Cursor cursor, ItemCompetitor program) {
        program.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        program.setProgramId(cursor.getString(cursor.getColumnIndex(COLUMN_COMPETITOR_PROGRAM_ID)));
        program.setProgramName(
                cursor.getString(cursor.getColumnIndex(COLUMN_COMPETITOR_PROGRAM_TEXT)));
        program.setUnitName(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_NAME)));
        program.setModifiedDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIFIED_DATE)));
    }

    private void setValues(ItemCompetitor program, ContentValues values) {
        values.put(COLUMN_COMPETITOR_PROGRAM_ID, program.getProgramId());
        values.put(COLUMN_COMPETITOR_PROGRAM_TEXT, program.getProgramName());
        values.put(COLUMN_UNIT_NAME, program.getUnitName());
        values.put(COLUMN_MODIFIED_DATE, TextUtils.isEmpty(program.getModifiedDate()) ?
                DateUtil.formatDBDateTime(DateUtil.now()) : program.getModifiedDate());
    }


    public void insert(ItemCompetitor program) {
        ContentValues values = new ContentValues();
        setValues(program, values);

        if (Constants.DEBUG) {
            Log.d(TAG, String.format("inserting --> %s | %s", program.getProgramId(),
                                     program.getProgramName()));
        }

        contentResolver.insert(URI, values);
    }

    public void updateById(int id, ItemCompetitor program) {
        if (Constants.DEBUG) Log.d(TAG, "update competitor program, id = " + id);

        ContentValues values = new ContentValues();
        setValues(program, values);

        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    public void updateByProgramId(String programId, ItemCompetitor program) {
        if (Constants.DEBUG) Log.d(TAG, "update competitor program, program id = " + programId);

        ContentValues values = new ContentValues();
        setValues(program, values);

        contentResolver.update(URI, values,
                               COLUMN_COMPETITOR_PROGRAM_ID + " = ?", new String[]{programId});
    }

    public void insertOrUpdate(ItemCompetitor program) {
        if (null != getByProgramId(program.getProgramId())) {
            if (program.getId() == null || program.getId() <= 0) {
                updateByProgramId(program.getProgramId(), program);
            } else {
                updateById(program.getId(), program);
            }
        } else {
            insert(program);
        }
    }

    public void delete(ItemCompetitor program) {
        String selection = null;
        String[] selectionArgs = null;
        Uri uri;

        if (program.getId() == null || program.getId() <= 0) {
            uri = URI;
            selection = COLUMN_COMPETITOR_PROGRAM_ID + " = ?";
            selectionArgs = new String[]{program.getProgramId()};
        } else {
            uri = Uri.parse(URI + "/" + program.getId());
        }

        contentResolver.delete(uri, selection, selectionArgs);
    }

    public List<ItemCompetitor> getAll(String selections, String[] selectionArgs) {
        if (Constants.DEBUG) Log.d(TAG, "reading competitor programs");
        List<ItemCompetitor> complains = new ArrayList<>();

        Cursor c = contentResolver.query(
                URI, ALL_COLUMNS, selections, selectionArgs, null);
        if (c == null) {
            return complains;
        }

        if (c.moveToFirst()) {
            do {
                ItemCompetitor com = new ItemCompetitor();
                setValues(c, com);

                if (Constants.DEBUG) {
                    Log.d(TAG, String.format("fetching --> %s | %s", com.getProgramId(),
                                             com.getProgramName()));
                }

                complains.add(com);
            } while (c.moveToNext());
        }
        c.close();

        return complains;
    }

    public void sync(List<ItemCompetitor> programs) {
//        String now = DateUtil.formatDBDateTime(Calendar.getInstance().getTime());
        for (ItemCompetitor program : programs) {
            insertOrUpdate(program);
        }

        /*contentResolver.delete(
                URI, COLUMN_MODIFIED_DATE + " < ?",
                new String[]{now});*/
    }

    public ItemCompetitor getByProgramId(String programId) {
        Cursor c = contentResolver.query(
                URI,
                ALL_COLUMNS,
                COLUMN_COMPETITOR_PROGRAM_ID + " = ?",
                new String[]{programId},
                null);

        if (c != null) {
            ItemCompetitor program = null;
            if (c.moveToFirst()) {
                program = new ItemCompetitor();
                setValues(c, program);
            }
            c.close();
            return program;
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
