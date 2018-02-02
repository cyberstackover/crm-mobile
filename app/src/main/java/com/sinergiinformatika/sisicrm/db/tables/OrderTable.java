package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 2/16/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class OrderTable {

    public static final String TABLE_NAME = "orders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_DELIVERY_DATE = "delivery_date";
    public static final String COLUMN_STORE_DB_ID = "store_db_id";
    public static final String COLUMN_STORE_ID = "store_id";
    public static final String COLUMN_STORE_NAME = "store_name";
    public static final String COLUMN_DISTRIBUTOR_ID = "distributor_id";
    public static final String COLUMN_DISTRIBUTOR_NAME = "distributor_name";
    public static final String COLUMN_PRODUCTS = "products";
    public static final String COLUMN_SYNC_STATUS = "sync_status";


    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " +
                                                    TABLE_NAME + " (" +
                                                    COLUMN_ID +
                                                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                    COLUMN_ORDER_ID + " TEXT, " +
                                                    COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                                                    COLUMN_DELIVERY_DATE + " TEXT NOT NULL, " +
                                                    COLUMN_STORE_DB_ID + " INTEGER, " +
                                                    COLUMN_STORE_ID + " TEXT, " +
                                                    COLUMN_STORE_NAME + " TEXT, " +
                                                    COLUMN_DISTRIBUTOR_ID + " TEXT NOT NULL, " +
                                                    COLUMN_DISTRIBUTOR_NAME + " TEXT, " +
                                                    COLUMN_PRODUCTS + " TEXT NOT NULL, " +
                                                    COLUMN_SYNC_STATUS + " TEXT NOT NULL);";

    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_ORDER_ID,
            COLUMN_ORDER_DATE, COLUMN_STORE_DB_ID, COLUMN_STORE_ID, COLUMN_STORE_NAME,
            COLUMN_DISTRIBUTOR_ID, COLUMN_DISTRIBUTOR_NAME,
            COLUMN_DELIVERY_DATE, COLUMN_PRODUCTS, COLUMN_SYNC_STATUS};
    private static final String TAG = OrderTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_ORDERS;
    private ContentResolver contentResolver;

    public OrderTable(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "creating table: " + TABLE_NAME);
        database.execSQL(CREATE_TABLE_QUERY);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.d(TAG, "upgrading from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 48) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }

    public static ContentValues setValues(Date deliveryDate, String storeId,
                                          String storeName,
                                          List<ItemPrice> orders,
                                          String syncStatus) {
        if (orders == null || orders.isEmpty()) {
            return null;
        }

        String products = "";
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> temp = new ArrayList<>();
        ContentValues values = new ContentValues();

        for (ItemPrice order : orders) {
            Map<String, Object> item = new HashMap<>();
            item.put("product_id", order.getProductId());
            item.put("product_name", order.getProductName());
            item.put("quantity", order.getVolume());

            temp.add(item);
        }

        try {
            products = mapper.writeValueAsString(temp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        values.put(COLUMN_ORDER_DATE, DateUtil.formatDBDateTime(DateUtil.now()));
        values.put(COLUMN_DELIVERY_DATE, DateUtil.formatDBDateOnly(deliveryDate));
        values.put(COLUMN_STORE_ID, storeId);
        values.put(COLUMN_STORE_NAME, storeName);
        values.put(COLUMN_DISTRIBUTOR_ID, orders.get(0).getDistributorId());
        values.put(COLUMN_DISTRIBUTOR_NAME, orders.get(0).getDistributorName());
        values.put(COLUMN_PRODUCTS, products);
        values.put(COLUMN_SYNC_STATUS, syncStatus);

        return values;
    }

    public static void setValues(Order order, ContentValues values) {

        values.put(COLUMN_ORDER_ID, order.getOrderId());
        values.put(COLUMN_ORDER_DATE, DateUtil.formatDBDateTime(order.getOrderDateInDate(true)));
        if (order.getDeliveryDate() != null && order.getDeliveryDate().trim().length() > 0) {
            values.put(COLUMN_DELIVERY_DATE,
                       DateUtil.formatDBDateOnly(order.getDeliveryDateInDate()));
        } else {
            values.put(COLUMN_DELIVERY_DATE, DateUtil.formatDBDateOnly(DateUtil.now()));
        }

        String storeId = order.getStoreId();
        if (storeId == null) {
            storeId = "";
        }

        values.put(COLUMN_STORE_ID, storeId);
        values.put(COLUMN_STORE_NAME, order.getStoreName());

        String distributorId = order.getDistributorId();
        if (distributorId == null) {
            distributorId = "";
        }
        values.put(COLUMN_DISTRIBUTOR_ID, distributorId);
        values.put(COLUMN_DISTRIBUTOR_NAME, order.getDistributorName());
        values.put(COLUMN_SYNC_STATUS, order.getSyncStatus());

        String products = "";

        try {

            List<Map<String, Object>> temp = new ArrayList<>();

            for (ItemPrice p : order.getPrices()) {
                Map<String, Object> item = new HashMap<>();
                item.put("product_id", p.getProductId());
                item.put("product_name", p.getProductName());
                //item.put("quantity", p.getVolume());
                item.put("volume", p.getVolume());

                temp.add(item);
            }

            ObjectMapper mapper = new ObjectMapper();
            products = mapper.writeValueAsString(temp);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        values.put(COLUMN_PRODUCTS, products);
        values.put(COLUMN_STORE_DB_ID, order.getStoreDbId());

    }

    public static void setValues(Cursor cursor, Order order) {

        order.setId(cursor.getLong((cursor.getColumnIndex(COLUMN_ID))));
        order.setOrderId(cursor.getString((cursor.getColumnIndex(COLUMN_ORDER_ID))));
        order.setOrderDate(cursor.getString((cursor.getColumnIndex(COLUMN_ORDER_DATE))));
        order.setDeliveryDate(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_DATE)));
        order.setStoreId(cursor.getString((cursor.getColumnIndex(COLUMN_STORE_ID))));
        order.setStoreName(cursor.getString((cursor.getColumnIndex(COLUMN_STORE_NAME))));
        order.setDistributorId(cursor.getString((cursor.getColumnIndex(COLUMN_DISTRIBUTOR_ID))));
        order.setDistributorName(
                cursor.getString((cursor.getColumnIndex(COLUMN_DISTRIBUTOR_NAME))));
        order.setSyncStatus(cursor.getString((cursor.getColumnIndex(COLUMN_SYNC_STATUS))));

        String productsStr = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTS));

        ObjectMapper mapper = new ObjectMapper();

        try {
            List<ItemPrice> prices =
                    mapper.readValue(productsStr, new TypeReference<List<ItemPrice>>() {
                    });
            order.setPrices(prices);
        } catch (IOException e) {
            Log.e(TAG, "productsStr = " + productsStr);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        order.setStoreDbId(cursor.getInt((cursor.getColumnIndex(COLUMN_STORE_DB_ID))));
    }

    public void addOrder(Date deliveryDate, Integer storeDbId, String storeId, String storeName,
                         List<ItemPrice> orders) {
        ContentValues values = setValues(deliveryDate, storeId, storeName,
                                         orders, Constants.SYNC_STATUS_PENDING);
        Log.d(TAG, "storeDbId = " + storeDbId);
        Log.d(TAG, "values = " + values);
        values.put(COLUMN_STORE_DB_ID, storeDbId);
        contentResolver.insert(URI, values);
    }

    public void update(@NonNull ContentValues values, String selection, String[] selectionArgs) {
        contentResolver.update(URI, values, selection, selectionArgs);
    }

    public void updateOrder(long id, ContentValues values) {
        if (values != null) {
            Uri uri = Uri.parse(URI + "/" + id);
            contentResolver.update(uri, values, null, null);
        }
    }

    public void updateOrderStatus(long id, String status) {
        ContentValues values = new ContentValues();
        values.put(OrderTable.COLUMN_SYNC_STATUS, status);
        updateOrder(id, values);
    }

    public void setSent(long id, String orderId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderId);
        values.put(COLUMN_SYNC_STATUS, Constants.SYNC_STATUS_SENT);
        updateOrder(id, values);
    }

    public void setFailed(long id) {
        updateOrderStatus(id, Constants.SYNC_STATUS_FAILED);
    }

    public void setPending(long id) {
        updateOrderStatus(id, Constants.SYNC_STATUS_PENDING);
    }

    public List<Order> getPendingOrders() {
        Cursor c = contentResolver.query(
                URI,
                ALL_COLUMNS,
                COLUMN_SYNC_STATUS + " = ?",
                new String[]{Constants.SYNC_STATUS_PENDING},
                COLUMN_ORDER_DATE);

        assert c != null;
        List<Order> orders = Parser.cursorToOrderList(c);
        c.close();

        return orders;
    }

    public List<Order> getAll() {

        List<Order> orders = new ArrayList<>();

        String sortOrder = COLUMN_ORDER_DATE + " desc";
        Cursor c = contentResolver.query(
                URI,
                ALL_COLUMNS,
                null,
                null,
                sortOrder);

        if (c == null) {
            return orders;
        }

        if (c.moveToFirst()) {
            do {
                Order s = new Order();
                setValues(c, s);
                orders.add(s);
            } while (c.moveToNext());
        }
        c.close();


        return orders;
    }

    public Order getByOrderId(String orderId) {
        Cursor c = contentResolver.query(
                URI,
                ALL_COLUMNS,
                COLUMN_ORDER_ID + " = ?",
                new String[]{orderId},
                null);

        if (c == null) {
            return null;
        }

        Order o = null;
        if (c.getCount() > 0 && c.moveToFirst()) {
            o = new Order();
            setValues(c, o);
        }
        c.close();

        return o;
    }

    public void syncInsert(Order order) {

        if (Constants.DEBUG) {
            Log.d(TAG, "insert order");
        }

        order.setSyncStatus(Constants.SYNC_STATUS_SENT);
        ContentValues values = new ContentValues();
        setValues(order, values);
        contentResolver.insert(URI, values);
    }

    public boolean isExists(String orderId) {
        if (Constants.DEBUG) {
            Log.d(TAG, "get order, order_Id = " + orderId);
        }

        Cursor c = contentResolver.query(URI, new String[]{COLUMN_ID},
                                         COLUMN_ORDER_ID + " = ?", new String[]{orderId}, null);

        boolean exists = c != null && c.moveToFirst();

        if (c != null) {
            c.close();
        }

        return exists;
    }

    public void save(Order order) {
        Order o = getByOrderId(order.getOrderId());

        if (o == null) {
            syncInsert(order);
        } else {
            if (o.getSyncStatus().equals(Constants.SYNC_STATUS_SENT)) {
                ContentValues values = new ContentValues();
                setValues(order, values);
                updateOrder(o.getId(), values);
            }
        }
    }

    public void deleteByStoreId(String[] storeIds) {
        String where = COLUMN_STORE_ID + " in (";

        for (int i = 0; i < storeIds.length; i++) {
            if (i > 0) {
                where += ",";
            }
            where += "?";
            i++;
        }

        where += ")";

        contentResolver.delete(URI, where, storeIds);
    }

    public void deleteByOrderId(String[] orderIds) {
        String where = COLUMN_ORDER_ID + " in (";

        for (int i = 0; i < orderIds.length; i++) {
            if (i > 0) {
                where += ",";
            }
            where += "?";
            i++;
        }

        where += ")";

        contentResolver.delete(URI, where, orderIds);
    }

    /**
     * get Limited Pending Orders
     * @param limit
     * @return
     */
    public List<Order> getPendingOrders(String limit) {
        Cursor c = contentResolver.query(
                URI.buildUpon().appendQueryParameter("limit",
                        limit).build(),
                ALL_COLUMNS,
                COLUMN_SYNC_STATUS + " = ?",
                new String[]{Constants.SYNC_STATUS_PENDING},
                COLUMN_ORDER_DATE + " DESC");

        assert c != null;
        List<Order> orders = Parser.cursorToOrderList(c);
        c.close();

        return orders;
    }
}
