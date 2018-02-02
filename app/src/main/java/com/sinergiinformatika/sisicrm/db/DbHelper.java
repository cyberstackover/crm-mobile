package com.sinergiinformatika.sisicrm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.CityTable;
import com.sinergiinformatika.sisicrm.db.tables.CompetitorTable;
import com.sinergiinformatika.sisicrm.db.tables.ComplainTable;
import com.sinergiinformatika.sisicrm.db.tables.DistributorTable;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;
import com.sinergiinformatika.sisicrm.db.tables.ProvinceTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SubdistrictTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;

/**
 * Created by wendi on 29-Dec-14.
 *
 * @author wendi
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 54;
    private static final String DB_NAME = "sisicrm.db";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        AgendaTable.onCreate(database);
        StoreTable.onCreate(database);
        DistributorTable.onCreate(database);
        SurveyTable.onCreate(database);
        ComplainTable.onCreate(database);
        ProductTable.onCreate(database);
        ProvinceTable.onCreate(database);
        CityTable.onCreate(database);
        SubdistrictTable.onCreate(database);
        OrderTable.onCreate(database);
        CompetitorTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(getClass().getName(), "upgrading from version " + oldVersion + " to " + newVersion);
        AgendaTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        StoreTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        DistributorTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        SurveyTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        ComplainTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        ProductTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        ProvinceTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        CityTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        SubdistrictTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        OrderTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        CompetitorTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

}
