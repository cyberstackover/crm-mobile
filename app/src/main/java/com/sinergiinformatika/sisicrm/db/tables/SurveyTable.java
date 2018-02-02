package com.sinergiinformatika.sisicrm.db.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemImage;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wendi on 07-Jan-15.
 */
public class SurveyTable {

    public static final String TABLE_NAME = "survey";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SURVEY_ID = "survey_id";
    public static final String COLUMN_SURVEY_DATE = "survey_date";
    public static final String COLUMN_CHECK_IN = "survey_check_in";
    public static final String COLUMN_CHECK_IN_LATITUDE = "survey_check_in_latitude";
    public static final String COLUMN_CHECK_IN_LONGITUDE = "survey_check_in_longitude";
    public static final String COLUMN_CHECK_OUT = "survey_check_out";
    public static final String COLUMN_CHECK_OUT_LATITUDE = "survey_check_out_latitude";
    public static final String COLUMN_CHECK_OUT_LONGITUDE = "survey_check_out_longitude";
    public static final String COLUMN_AGENDA_DB_ID = "agenda_db_id";//untuk join ke table agenda
    public static final String COLUMN_STORE_ID = "store_id";
    public static final String COLUMN_STORE_NAME = "store_name";
    public static final String COLUMN_PRODUCTS = "products";//json string
    public static final String COLUMN_COMPLAINS = "complains";//json string
    public static final String COLUMN_COMPETITORS = "competitor_programs";//json string
    public static final String COLUMN_COMPLAIN_NOTES = "complain_note";//json string
    public static final String COLUMN_IMAGES = "images";//json string
    public static final String COLUMN_COMPETITOR_NOTES = "competitor_notes";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_MODIFIED_DATE = "modified_date";
    public static final String COLUMN_IS_SURVEY = "is_survey";
    public static final String COLUMN_IS_PHOTO = "is_photo";
    public static final String COLUMN_IS_COMPLAIN = "is_complain";
    public static final String COLUMN_IS_COMPETITOR = "is_competitor";
    public static final String COLUMN_IS_COMPETITOR_NOTES = "is_competitor_notes";
    public static final String COLUMN_SYNC_STATUS = "sync_status";
    public static final String COLUMN_STATUS_DATA = "status_data";
    public static final String COLUMN_STATUS_IMAGE = "status_image";
    public static final String COLUMN_PLAN_DATE = "plan_date";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_SURVEY_ID, COLUMN_SURVEY_DATE, COLUMN_AGENDA_DB_ID,
            COLUMN_PRODUCTS, COLUMN_COMPLAINS, COLUMN_COMPETITORS, COLUMN_IMAGES,
            COLUMN_SYNC_DATE, COLUMN_MODIFIED_DATE, COLUMN_IS_COMPETITOR,
            COLUMN_IS_SURVEY, COLUMN_IS_PHOTO, COLUMN_IS_COMPLAIN, COLUMN_IS_COMPETITOR_NOTES,
            COLUMN_COMPLAIN_NOTES, COLUMN_COMPETITOR_NOTES, COLUMN_SYNC_STATUS, COLUMN_CHECK_IN,
            COLUMN_CHECK_IN_LATITUDE, COLUMN_CHECK_IN_LONGITUDE, COLUMN_CHECK_OUT,
            COLUMN_CHECK_OUT_LATITUDE, COLUMN_CHECK_OUT_LONGITUDE, COLUMN_STORE_ID,
            COLUMN_STORE_NAME, COLUMN_PLAN_DATE, COLUMN_STATUS_DATA, COLUMN_STATUS_IMAGE
    };

    private static final String CREATE_TABLE;
    private static final String TAG = SurveyTable.class.getSimpleName();
    private static final Uri URI = CRMContentProvider.URI_SURVEY;

    static {
        CREATE_TABLE = new StringBuilder()
                .append("create table ")
                .append(TABLE_NAME)
                .append(" (")
                .append(COLUMN_ID)
                .append(" integer primary key, ")
                .append(COLUMN_SURVEY_ID)
                .append(" text, ")
                .append(COLUMN_SURVEY_DATE)
                .append(" text, ")
                .append(COLUMN_CHECK_IN)
                .append(" text, ")
                .append(COLUMN_CHECK_IN_LATITUDE)
                .append(" real, ")
                .append(COLUMN_CHECK_IN_LONGITUDE)
                .append(" real, ")
                .append(COLUMN_CHECK_OUT)
                .append(" text, ")
                .append(COLUMN_CHECK_OUT_LATITUDE)
                .append(" real, ")
                .append(COLUMN_CHECK_OUT_LONGITUDE)
                .append(" real, ")
                .append(COLUMN_STORE_ID)
                .append(" text, ")
                .append(COLUMN_STORE_NAME)
                .append(" text, ")
                .append(COLUMN_AGENDA_DB_ID)
                .append(" integer, ")
                .append(COLUMN_PRODUCTS)
                .append(" text, ")
                .append(COLUMN_IMAGES)
                .append(" text, ")
                .append(COLUMN_COMPLAINS)
                .append(" text, ")
                .append(COLUMN_COMPLAIN_NOTES)
                .append(" text, ")
                .append(COLUMN_COMPETITORS)
                .append(" text, ")
                .append(COLUMN_COMPETITOR_NOTES)
                .append(" text, ")
                .append(COLUMN_IS_SURVEY)
                .append(" integer, ")
                .append(COLUMN_IS_PHOTO)
                .append(" integer, ")
                .append(COLUMN_IS_COMPLAIN)
                .append(" integer, ")
                .append(COLUMN_IS_COMPETITOR)
                .append(" integer, ")
                .append(COLUMN_IS_COMPETITOR_NOTES)
                .append(" integer, ")
                .append(COLUMN_SYNC_DATE)
                .append(" text, ")
                .append(COLUMN_MODIFIED_DATE)
                .append(" text, ")
                .append(COLUMN_SYNC_STATUS)
                .append(" text, ")
                .append(COLUMN_STATUS_DATA)
                .append(" text, ")
                .append(COLUMN_STATUS_IMAGE)
                .append(" text, ")
                .append(COLUMN_PLAN_DATE)
                .append(" text);")
                .toString();
    }

    private ContentResolver contentResolver;

    public SurveyTable(Context context) {
        this.contentResolver = context.getContentResolver();
    }


    public SurveyTable(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 48) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } else if (oldVersion < 54) {
            Log.w(TAG, "Upgrading database from version "
                       + oldVersion + " to " + newVersion);
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_STATUS_DATA + " TEXT");
            db.execSQL(
                    "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_STATUS_IMAGE + " TEXT");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_STATUS_DATA + " = " +
                       COLUMN_SYNC_STATUS + ", " + COLUMN_STATUS_IMAGE + " = " +
                       COLUMN_SYNC_STATUS);
        }
    }

    /**
     * @param cursor cursor pointing to db row
     * @param survey target object
     */
    public static void setValues(Cursor cursor, Survey survey) {
        survey.setId(cursor.getInt((cursor.getColumnIndex(COLUMN_ID))));
        survey.setSurveyId(cursor.getString((cursor.getColumnIndex(COLUMN_SURVEY_ID))));
        survey.setAgendaDbId(cursor.getInt((cursor.getColumnIndex(COLUMN_AGENDA_DB_ID))));
        survey.setSyncDate(cursor.getString(cursor.getColumnIndex(COLUMN_SYNC_DATE)));
        survey.setModifiedDate(cursor.getString(cursor.getColumnIndex(COLUMN_MODIFIED_DATE)));
        survey.setComplainNote(cursor.getString(cursor.getColumnIndex(COLUMN_COMPLAIN_NOTES)));

        survey.setIsSurvey(false);
        if (cursor.getInt((cursor.getColumnIndex(COLUMN_IS_SURVEY))) == Constants.FLAG_TRUE) {
            survey.setIsSurvey(true);
        }

        survey.setIsPhoto(false);
        if (cursor.getInt((cursor.getColumnIndex(COLUMN_IS_PHOTO))) == Constants.FLAG_TRUE) {
            survey.setIsPhoto(true);
        }

        survey.setIsComplain(false);
        if (cursor.getInt((cursor.getColumnIndex(COLUMN_IS_COMPLAIN))) == Constants.FLAG_TRUE) {
            survey.setIsComplain(true);
        }

        survey.setIsCompetitor(false);
        if (cursor.getInt((cursor.getColumnIndex(COLUMN_IS_COMPETITOR))) == Constants.FLAG_TRUE) {
            survey.setIsCompetitor(true);
        }

        String productsStr = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTS));
        String complainsStr = cursor.getString(cursor.getColumnIndex(COLUMN_COMPLAINS));
        String imagesStr = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES));
        String competitorStr = cursor.getString(cursor.getColumnIndex(COLUMN_COMPETITORS));

        ObjectMapper mapper = new ObjectMapper();

        try {
            List<ItemPrice> prices =
                    mapper.readValue(productsStr, new TypeReference<List<ItemPrice>>() {
                    });
            survey.setPrices(prices);
        } catch (IOException e) {
            if (Constants.DEBUG) {
                Log.e(TAG, "productsStr = " + productsStr);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            List<ItemComplain> complains =
                    mapper.readValue(complainsStr, new TypeReference<List<ItemComplain>>() {
                    });
            survey.setComplains(complains);
        } catch (IOException e) {
            if (Constants.DEBUG) {
                Log.d(TAG, "complainsStr = " + complainsStr);
            }

        } catch (Exception e) {
            if (Constants.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        try {
            List<String> imageUris = mapper.readValue(imagesStr, new TypeReference<List<String>>() {
            });

            List<ItemImage> images = new ArrayList<>();
            for (String imgUri : imageUris) {
                ItemImage img = new ItemImage();
                img.setImageUri(imgUri);
                images.add(img);
            }
            survey.setImages(images);

        } catch (IOException e) {
            if (Constants.DEBUG) {
                Log.d(TAG, "imagesStr = " + imagesStr);
            }
        } catch (Exception e) {
            if (Constants.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        try {
            List<ItemCompetitor> competitors =
                    mapper.readValue(competitorStr, new TypeReference<List<ItemCompetitor>>() {
                    });

            survey.setCompetitorPrograms(competitors);

            String complainNotesStr =
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPLAIN_NOTES));
            String competitorNotesStr =
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPETITOR_NOTES));
            List<ItemNote> complainNotes =
                    mapper.readValue(complainNotesStr, new TypeReference<List<ItemNote>>() {
                    });
            List<ItemNote> competitorNotes =
                    mapper.readValue(competitorNotesStr, new TypeReference<List<ItemNote>>() {
                    });

            survey.addNotes(complainNotes);
            survey.addNotes(competitorNotes);

            if (Constants.DEBUG) {
                Log.d(TAG, "query --> competitors: " + competitorStr);
                Log.d(TAG, "query --> notes: " + survey.getNotes().size());
            }
        } catch (IOException e) {
            if (Constants.DEBUG) {
                Log.d(TAG, "competitorStr = " + competitorStr);
            }

        } catch (Exception e) {
            if (Constants.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        survey.setCompetitorNotes(cursor.getString(cursor.getColumnIndex(COLUMN_COMPETITOR_NOTES)));

        survey.setStoreId(cursor.getString(cursor.getColumnIndex(COLUMN_STORE_ID)));
        survey.setStoreName(cursor.getString(cursor.getColumnIndex(COLUMN_STORE_NAME)));
        survey.setSyncStatus(cursor.getString(cursor.getColumnIndex(COLUMN_SYNC_STATUS)));
        survey.setStatusData(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_DATA)));
        survey.setStatusImage(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_IMAGE)));
        survey.setSurveyDate(cursor.getString(cursor.getColumnIndex(COLUMN_SURVEY_DATE)));
        survey.setCheckIn(cursor.getString(cursor.getColumnIndex(COLUMN_CHECK_IN)));
        survey.setCheckInlatitude(
                cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_IN_LATITUDE)));
        survey.setCheckInLongitude(
                cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_IN_LONGITUDE)));
        survey.setCheckOut(cursor.getString(cursor.getColumnIndex(COLUMN_CHECK_OUT)));
        survey.setCheckOutLatitude(
                cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_OUT_LATITUDE)));
        survey.setCheckOutLongitude(
                cursor.getDouble(cursor.getColumnIndex(COLUMN_CHECK_OUT_LONGITUDE)));
        survey.setPlanDate(cursor.getString(cursor.getColumnIndex(COLUMN_PLAN_DATE)));
    }

    /**
     * @param survey source data
     * @param values target ContentValues object
     */
    public static void setValues(Survey survey, ContentValues values) {

        values.put(COLUMN_SURVEY_ID, survey.getSurveyId());
        values.put(COLUMN_SURVEY_DATE, survey.getSurveyDate());
        values.put(COLUMN_AGENDA_DB_ID, survey.getAgendaDbId());
        values.put(COLUMN_SYNC_DATE, survey.getSyncDate());
        values.put(COLUMN_MODIFIED_DATE, survey.getModifiedDate());

        ObjectMapper mapper = new ObjectMapper();

        int isSurvey = Constants.FLAG_FALSE;

        try {
            List<Map<String, Object>> products = new ArrayList<>();

            for (ItemPrice p : survey.getPrices()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_id", p.getProductId());
                product.put("product_name", p.getProductName());
                product.put("product_package", p.getProductPackage());
                product.put("price", p.getPrice());
                product.put("price_purchase", p.getPricePurchase());
                product.put("volume", p.getVolume());
                product.put("stock", p.getStock());
                product.put("term_of_payment", p.getTermOfPayment());

                products.add(product);
            }

            String productsStr = mapper.writeValueAsString(products);
            values.put(COLUMN_PRODUCTS, productsStr);

            if (products.size() > 0) {
                isSurvey = Constants.FLAG_TRUE;
            }

        } catch (Exception e) {
            if (Constants.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        int isComplain = Constants.FLAG_FALSE;
        int isCompetitor = Constants.FLAG_FALSE;

        try {
            List<Map<String, Object>> complains = new ArrayList<>();

            for (ItemComplain com : survey.getComplains()) {
                Map<String, Object> complain = new HashMap<>();
                complain.put("product_id", com.getProductId());
                complain.put("product_name", com.getProductName());
                complain.put("complain_id", com.getComplainId());
                complain.put("complain_text", com.getComplain());
                complain.put("checked", com.isChecked());
                complains.add(complain);
            }

            String complainsStr = mapper.writeValueAsString(complains);

            values.put(COLUMN_COMPLAINS, complainsStr);

            if (complains.size() > 0) {
                isComplain = Constants.FLAG_TRUE;
            }

            List<Map<String, Object>> competitors = new ArrayList<>();

            for (ItemCompetitor com : survey.getCompetitorPrograms()) {
                Map<String, Object> competitor = new HashMap<>();
                competitor.put("product_id", com.getProductId());
                competitor.put("product_name", com.getProductName());
                competitor.put("program_id", com.getProgramId());
                competitor.put("program_name", com.getProgramName());
                competitor.put("checked", com.getChecked());
                competitor.put("unit_name",
                               TextUtils.isEmpty(com.getUnitName()) ? "" : com.getUnitName());
                competitor.put("unit_value",
                               TextUtils.isEmpty(com.getUnitName()) ? "" : com.getUnitValue());
                competitors.add(competitor);
            }

            String competitorsStr = mapper.writeValueAsString(competitors);
            values.put(COLUMN_COMPETITORS, competitorsStr);

            if (competitors.size() > 0) {
                isCompetitor = Constants.FLAG_TRUE;
            }

            List<Map<String, String>> complainNotes = new ArrayList<>();
            List<Map<String, String>> competitorNotes = new ArrayList<>();

            for (ItemNote note : survey.getNotes()) {
                Map<String, String> noteItem = new HashMap<>();
                noteItem.put("product_id", note.getProductId());
                noteItem.put("note_type", note.getNoteType());
                noteItem.put("note", note.getNote());
                if (note.getNoteType().equals(Constants.NOTE_TYPE_COMPLAIN)) {
                    complainNotes.add(noteItem);
                } else {
                    competitorNotes.add(noteItem);
                }
            }

            String complainNotesStr = mapper.writeValueAsString(complainNotes);
            String competitorNotesStr = mapper.writeValueAsString(competitorNotes);

            values.put(COLUMN_COMPLAIN_NOTES, complainNotesStr);
            values.put(COLUMN_COMPETITOR_NOTES, competitorNotesStr);

            if (Constants.DEBUG) {
                Log.d(TAG, "insert/update --> competitors: " + competitorsStr);
                Log.d(TAG, "insert/update --> complain notes: " + complainNotesStr);
                Log.d(TAG, "insert/update --> competitor notes: " + competitorNotesStr);
            }
        } catch (Exception e) {
            if (Constants.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        int isPhoto = Constants.FLAG_FALSE;

        try {
            List<String> imageUris = new ArrayList<>();
            for (ItemImage img : survey.getImages()) {
                imageUris.add(img.getImageUri());
            }
            String imageUrisStr = mapper.writeValueAsString(imageUris);

            values.put(COLUMN_IMAGES, imageUrisStr);

            if (imageUris.size() > 0) {
                isPhoto = Constants.FLAG_TRUE;
            }

        } catch (Exception e) {
            if (Constants.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        values.put(COLUMN_IS_SURVEY, isSurvey);
        values.put(COLUMN_IS_PHOTO, isPhoto);
        values.put(COLUMN_IS_COMPLAIN, isComplain);
        values.put(COLUMN_IS_COMPETITOR, isCompetitor);

        values.put(COLUMN_STORE_ID, survey.getStoreId());
        values.put(COLUMN_STORE_NAME, survey.getStoreName());
        values.put(COLUMN_SYNC_STATUS, survey.getSyncStatus());
        values.put(COLUMN_STATUS_DATA, survey.getSyncStatus());

        if (isPhoto == Constants.FLAG_TRUE) {
            values.put(COLUMN_STATUS_IMAGE,
                       TextUtils.isEmpty(survey.getStatusImage()) ? survey.getSyncStatus() :
                               survey.getStatusImage());
        } else {
            values.put(COLUMN_STATUS_IMAGE, Constants.SYNC_STATUS_SENT);
        }

        values.put(COLUMN_CHECK_IN, survey.getCheckIn());
        values.put(COLUMN_CHECK_IN_LATITUDE, survey.getCheckInLatitude());
        values.put(COLUMN_CHECK_IN_LONGITUDE, survey.getCheckInLongitude());
        values.put(COLUMN_CHECK_OUT, survey.getCheckOut());
        values.put(COLUMN_CHECK_OUT_LATITUDE, survey.getCheckOutLatitude());
        values.put(COLUMN_CHECK_OUT_LONGITUDE, survey.getCheckOutLongitude());

        values.put(COLUMN_PLAN_DATE, survey.getPlanDate());
    }

    public int insert(Survey survey) {

        if (Constants.DEBUG) {
            Log.d(TAG, "insert survey");
        }

        int newId = 0;
        survey.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));
        ContentValues values = new ContentValues();
        setValues(survey, values);
        Uri uri = contentResolver.insert(URI, values);
        try {
            if (uri != null) {
                newId = Integer.parseInt(uri.getLastPathSegment());
                survey.setId(newId);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return newId;
    }

    public void updateById(int id, Survey survey) {

        if (Constants.DEBUG) {
            Log.d(TAG, "update survey, id = " + id);
            Log.d(TAG, "survey.getSyncStatus() = " + survey.getSyncStatus());
        }

        survey.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));
        ContentValues values = new ContentValues();
        setValues(survey, values);
        Uri uri = Uri.parse(CRMContentProvider.URI_SURVEY + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    public List<Survey> getAll() {

        List<Survey> surveyList = new ArrayList<>();

        String sortOrder = COLUMN_SURVEY_DATE + " desc";
        Cursor c = contentResolver.query(URI, ALL_COLUMNS, null, null, sortOrder);
        if (c == null) {
            return surveyList;
        }

        if (c.moveToFirst()) {
            do {
                Survey s = new Survey();
                setValues(c, s);
                surveyList.add(s);
            } while (c.moveToNext());
        }
        c.close();

        return surveyList;
    }

    public int syncInsert(Survey survey) {
        survey.setSyncStatus(Constants.SYNC_STATUS_SENT);
        survey.setSyncDate(DateUtil.formatDBDateTime(DateUtil.now()));
        survey.setModifiedDate(DateUtil.formatDBDateTime(DateUtil.now()));

        Survey s = getBySurveyId(survey.getSurveyId());
        if (s != null) {
            if (s.getSyncStatus().equals(Constants.SYNC_STATUS_SENT)) {
                updateById(s.getId(), survey);
            }

            return s.getId();
        }

        return insert(survey);
    }

    public Survey getBySurveyId(String surveyId) {

        if (Constants.DEBUG) {
            Log.d(TAG, "get survey, surveyId = " + surveyId);
        }

        Cursor c = contentResolver.query(URI, ALL_COLUMNS, COLUMN_SURVEY_ID + " = ?",
                                         new String[]{surveyId}, null);

        if (c != null && c.moveToFirst()) {
            Survey s = new Survey();
            setValues(c, s);
            c.close();
            return s;
        }

        return null;
    }

    public Survey getById(long id) {

        if (Constants.DEBUG) {
            Log.d(TAG, "get survey, id = " + id);
        }

        String selections = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor c = contentResolver.query(URI, ALL_COLUMNS, selections, selectionArgs, null);
        if (c == null) {
            return null;
        }

        if (c.moveToFirst()) {
            do {
                int existId = c.getInt((c.getColumnIndex(COLUMN_ID)));
                if (existId == id) {
                    Survey s = new Survey();
                    setValues(c, s);
                    c.close();
                    return s;
                }
            } while (c.moveToNext());
        }
        c.close();

        return null;
    }

    public Survey getByAgendaId(long agendaId) {
        Survey survey = null;
        Cursor c = contentResolver
                .query(URI, ALL_COLUMNS, COLUMN_AGENDA_DB_ID + " = " + agendaId, null,
                       COLUMN_SURVEY_DATE + " DESC");

        if (c != null) {
            if (c.moveToFirst()) {
                survey = new Survey();
                setValues(c, survey);
            }

            c.close();
        }

        return survey;
    }

    /**
     * get All Pending Surveys
     * @return
     */
    public List<Survey> getPendingSurveyList() {
        return listPendingSurveyList(null);
    }

    /**
     * get Limited Pending Surveys
     * @param limit
     * @return
     */
    public List<Survey> getPendingSurveyList(String limit) {
        return listPendingSurveyList(limit);
    }

    /**
     * List Pending Survey
     * @param limit
     * @return
     */
    private List<Survey> listPendingSurveyList(String limit) {

        //String selections = COLUMN_SURVEY_ID+" is null";
        String selections = COLUMN_SYNC_STATUS + " = ?";
        String[] args = new String[]{Constants.SYNC_STATUS_PENDING};

        List<Survey> surveyList = new ArrayList<>();
        Cursor c = null;
        if(limit != null){
            c = contentResolver.query(URI.buildUpon().appendQueryParameter("limit",
                    limit).build(), ALL_COLUMNS, selections, args, COLUMN_MODIFIED_DATE + " DESC");
        } else {
            c = contentResolver.query(URI, ALL_COLUMNS, selections, args, null);
        }

        if (c == null) {
            return surveyList;
        }

        if (c.moveToFirst()) {
            do {
                Survey s = new Survey();
                setValues(c, s);
                surveyList.add(s);
                //break;//TODO sementara, lagi cari solusi yang lebih baik untuk submit survey
                // yang pending (survey_id is null)
            } while (c.moveToNext());
        }

        c.close();

        return surveyList;
    }

    /**
     * get All Pending SurveyPhotos
     * @return
     */
    public List<Survey> getPendingSurveyPhotos() {
        return listPendingSurveyPhotos(null);
    }

    /**
     * get Limited Pending SurveyPhotos
     * @param limit
     * @return
     */
    public List<Survey> getPendingSurveyPhotos(String limit) {
        return listPendingSurveyPhotos(limit);
    }

    /**
     * List Pending Survey Photos
     * @param limit
     * @return
     */
    private List<Survey> listPendingSurveyPhotos(String limit) {
        String selections = COLUMN_STATUS_IMAGE + " = ? OR " + COLUMN_STATUS_IMAGE + " = ?";
        String[] args = new String[]{Constants.SYNC_STATUS_PENDING, Constants.SYNC_STATUS_FAILED};

        List<Survey> surveyList = new ArrayList<>();
        Cursor c = null;
        if(limit != null){
            c = contentResolver.query(URI.buildUpon().appendQueryParameter("limit",
                    limit).build(), ALL_COLUMNS, selections, args, COLUMN_MODIFIED_DATE + " DESC");
        } else {
            c = contentResolver.query(URI, ALL_COLUMNS, selections, args, null);
        }

        if (c == null) {
            return surveyList;
        }

        if (c.moveToFirst()) {
            do {
                Survey s = new Survey();
                setValues(c, s);
                surveyList.add(s);
            } while (c.moveToNext());
        }

        c.close();

        return surveyList;
    }

    public void sent(int id, String surveyId) {
        updateSyncStatus(id, Constants.SYNC_STATUS_SENT, surveyId);
    }

    public void dataPending(int id) {
        updateDataStatus(id, Constants.SYNC_STATUS_PENDING, null);
    }

    public void dataSending(int id) {
        updateDataStatus(id, Constants.SYNC_STATUS_SENDING, null);
    }

    public void dataSent(int id, String surveyId) {
        updateDataStatus(id, Constants.SYNC_STATUS_SENT, surveyId);
    }

    public void dataFailed(int id) {
        updateDataStatus(id, Constants.SYNC_STATUS_FAILED, null);
    }

    public void imagePending(int id) {
        updateImageStatus(id, Constants.SYNC_STATUS_PENDING);
    }

    public void imageSending(int id) {
        updateImageStatus(id, Constants.SYNC_STATUS_SENDING);
    }

    public void imageSent(int id) {
        updateImageStatus(id, Constants.SYNC_STATUS_SENT);
    }

    public void imageFailed(int id) {
        updateImageStatus(id, Constants.SYNC_STATUS_FAILED);
    }

    public void pending(int id) {
        updateSyncStatus(id, Constants.SYNC_STATUS_PENDING);
    }

    public void sending(int id) {
        updateSyncStatus(id, Constants.SYNC_STATUS_SENDING);
    }

    public void failed(int id) {
        updateSyncStatus(id, Constants.SYNC_STATUS_FAILED);
    }

    private void updateDataStatus(int id, String syncStatus, String surveyId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MODIFIED_DATE, DateUtil.formatDBDateTime(DateUtil.now()));
        values.put(COLUMN_STATUS_DATA, syncStatus);
        if (surveyId != null) {
            values.put(COLUMN_SURVEY_ID, surveyId);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    private void updateImageStatus(int id, String syncStatus) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MODIFIED_DATE, DateUtil.formatDBDateTime(DateUtil.now()));
        values.put(COLUMN_STATUS_IMAGE, syncStatus);

        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    private void updateSyncStatus(int id, String syncStatus) {
        updateSyncStatus(id, syncStatus, null);
    }

    private void updateSyncStatus(int id, String syncStatus, String surveyId) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_MODIFIED_DATE, DateUtil.formatDBDateTime(DateUtil.now()));
        values.put(COLUMN_SYNC_STATUS, syncStatus);
        if (surveyId != null) {
            values.put(COLUMN_SURVEY_ID, surveyId);
        }

        Uri uri = Uri.parse(URI + "/" + id);
        contentResolver.update(uri, values, null, null);
    }

    public boolean isExists(String surveyId) {
        if (Constants.DEBUG) {
            Log.d(TAG, "get survey, survey_Id = " + surveyId);
        }

        Cursor c = contentResolver.query(URI, new String[]{COLUMN_ID}, COLUMN_SURVEY_ID + " = ?",
                                         new String[]{surveyId}, null);

        boolean exists = c != null && c.moveToFirst();

        if (c != null) {
            c.close();
        }

        return exists;
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

    public void update(@NonNull ContentValues values, String selection, String[] selectionArgs) {
        contentResolver.update(URI, values, selection, selectionArgs);
    }
}
