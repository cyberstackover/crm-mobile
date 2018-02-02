package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 3/23/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class NoteTable {
    public static final String TABLE_NAME = "orders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_TYPE = "note_type";
    public static final String COLUMN_NOTE = "note";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_TYPE, COLUMN_NOTE
    };
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            COLUMN_ID + " integer primary key" + ", " +
            COLUMN_PRODUCT_ID + " text" + ", " +
            COLUMN_TYPE + " text" + ", " +
            COLUMN_NOTE + " text" + ");";
    private static final String TAG = NoteTable.class.getSimpleName();

    private ContentResolver contentResolver;

    public NoteTable(Context context) {
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

    private void setValues(Cursor cursor, ItemNote note) {
        note.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        note.setProductId(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID)));
        note.setNoteType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
        note.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
    }

    private void setValues(ItemNote note, ContentValues values) {
        values.put(COLUMN_PRODUCT_ID, note.getProductId());
        values.put(COLUMN_TYPE, note.getNoteType());
        values.put(COLUMN_NOTE, note.getNote());
    }

    public void insert(ItemNote note) {
        ContentValues values = new ContentValues();
        setValues(note, values);

        if (Constants.DEBUG)
            Log.d(TAG, String.format("inserting --> %s | %s", note.getProductId(), note.getNote()));

        contentResolver.insert(CRMContentProvider.URI_NOTES, values);
    }

    public List<ItemNote> getAll(String selections, String[] selectionArgs) {
        if (Constants.DEBUG) Log.d(TAG, "reading notes");
        List<ItemNote> notes = new ArrayList<>();

        Cursor c = contentResolver.query(
                CRMContentProvider.URI_NOTES, ALL_COLUMNS, selections, selectionArgs, null);
        if (c == null)
            return notes;

        if (c.moveToFirst()) {
            do {
                ItemNote note = new ItemNote();
                setValues(c, note);

                if (Constants.DEBUG)
                    Log.d(TAG, String.format("fetching --> %s | %s", note.getProductId(), note.getNote()));

                notes.add(note);
            } while (c.moveToNext());
        }
        c.close();

        return notes;
    }
}
