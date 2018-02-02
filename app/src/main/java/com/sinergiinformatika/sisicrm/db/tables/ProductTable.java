package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 08-Jan-15.
 */
public class ProductTable {

    public static final String TABLE_NAME = "product";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_PRODUCT_NAME = "product_name";
    public static final String COLUMN_IS_COMPETITOR = "is_competitor";
    public static final String COLUMN_PRODUCT_WEIGHT = "product_weight";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_MODIFIED_DATE = "modified_date";
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_PRODUCT_NAME, COLUMN_IS_COMPETITOR,
            COLUMN_SYNC_DATE, COLUMN_MODIFIED_DATE, COLUMN_PRODUCT_WEIGHT, COLUMN_IS_COMPETITOR
    };
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            COLUMN_ID + " integer primary key" + ", " +
            COLUMN_PRODUCT_ID + " text" + ", " +
            COLUMN_PRODUCT_NAME + " text" + ", " +
            COLUMN_IS_COMPETITOR + " integer default 0" + ", " +
            COLUMN_PRODUCT_WEIGHT + " text" + ", " +
            COLUMN_SYNC_DATE + " text" + ", " +
            COLUMN_MODIFIED_DATE + " text" + ");";
    private static final String TAG = ProductTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_PRODUCT;
    private ContentResolver contentResolver;

    public ProductTable(Context context) {
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

    private void setValues(Cursor cursor, ItemPrice product) {
        product.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        product.setProductId(cursor.getString((cursor.getColumnIndex(COLUMN_PRODUCT_ID))));
        product.setProductName(cursor.getString((cursor.getColumnIndex(COLUMN_PRODUCT_NAME))));
        product.setIsCompetitor(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_COMPETITOR)));
        product.setProductWeight(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_WEIGHT)));
        product.setProductPackage(product.getProductWeight());
        product.setModifiedDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIFIED_DATE)));
    }

    private void setValues(ItemPrice product, ContentValues values) {
        values.put(COLUMN_PRODUCT_ID, product.getProductId());
        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_IS_COMPETITOR, product.getIsCompetitor());
        values.put(COLUMN_PRODUCT_WEIGHT, product.getProductWeight());
        values.put(COLUMN_MODIFIED_DATE, TextUtils.isEmpty(product.getModifiedDate()) ?
                DateUtil.formatDBDateTime(DateUtil.now()) : product.getModifiedDate());
    }


    public void insert(ItemPrice product) {
        //aLog.d(TAG,"insert store");
        ContentValues values = new ContentValues();
        setValues(product, values);
        contentResolver.insert(URI, values);
    }

    public void updateById(int id, ItemPrice product) {
        //Log.d(TAG, "update product, id = " + id);
        ContentValues values = new ContentValues();
        setValues(product, values);
        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    public void updateByProductId(String productId, ItemPrice product) {
        ContentValues values = new ContentValues();
        setValues(product, values);
        contentResolver.update(URI, values, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
    }

    public void delete(ItemPrice price) {
        String selection = null;
        String[] selectionArgs = null;
        Uri uri;

        if (price.getId() == null || price.getId() <= 0) {
            uri = URI;
            selection = COLUMN_PRODUCT_ID + " = ?";
            selectionArgs = new String[]{price.getProductId()};
        } else {
            uri = Uri.parse(URI + "/" + price.getId());
        }

        contentResolver.delete(uri, selection, selectionArgs);
    }

    public void sync(ItemPrice product) {
        //Log.d(TAG,"sync product");
        ItemPrice existProduct = getByProductId(product.getProductId());

        product.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));

        if (existProduct != null) {
            if (existProduct.getId() != null && existProduct.getId() > 0) {
                updateById(existProduct.getId(), product);
            } else {
                updateByProductId(existProduct.getProductId(), product);
            }
        } else {
            insert(product);
        }

    }

    public List<ItemPrice> getAll(String selection, String[] selectionArgs) {
        List<ItemPrice> products = new ArrayList<>();

        Cursor c = contentResolver.query(URI, ALL_COLUMNS,
                                         selection, selectionArgs, null);
        if (c == null) {
            return products;
        }

        if (c.moveToFirst()) {
            do {
                ItemPrice p = new ItemPrice();
                setValues(c, p);
                products.add(p);
            } while (c.moveToNext());
        }
        c.close();

        return products;
    }

    public ItemPrice getByProductId(String productId) {
        //Log.d(TAG, "get product, productId = " + productId);

        Cursor c = contentResolver.query(URI, ALL_COLUMNS,
                                         COLUMN_PRODUCT_ID + " = ?", new String[]{productId}, null);

        if (c != null && c.moveToFirst()) {
            ItemPrice p = new ItemPrice();
            setValues(c, p);
            c.close();
            return p;
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
