package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wendi on 26-Feb-15.
 */
public class SurveyCursorAdapter extends SimpleCursorAdapter implements Filterable {

    private static final String TAG = SurveyCursorAdapter.class.getSimpleName();
    private Context context;
    private Set<String> dates = new HashSet<>();
    private Map<Integer, Boolean> flagHeader = new HashMap<>();
    private StoreTable storeTable;
    private AgendaTable agendaTable;

    public SurveyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                               int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.storeTable = new StoreTable(context);
        this.agendaTable = new AgendaTable(context);
    }

    @Override
    public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Survey survey = new Survey();
        //SurveyTable.setValues(cursor, survey);

        survey.setId(cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_ID))));
        survey.setSurveyId(cursor.getString((cursor.getColumnIndex(SurveyTable.COLUMN_SURVEY_ID))));
        survey.setSyncDate(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_SYNC_DATE)));
        survey.setStoreId(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_STORE_ID)));
        survey.setStoreName(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_STORE_NAME)));
        survey.setSyncStatus(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_SYNC_STATUS)));
        survey.setStatusData(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_STATUS_DATA)));
        survey.setStatusImage(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_STATUS_IMAGE)));
        survey.setSurveyDate(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_SURVEY_DATE)));
        survey.setCheckIn(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_CHECK_IN)));
        survey.setPlanDate(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_PLAN_DATE)));
        survey.setIsSurvey(false);
        if (cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_SURVEY))) == Constants.FLAG_TRUE) {
            survey.setIsSurvey(true);
        }

        survey.setIsPhoto(false);
        if (cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_PHOTO))) == Constants.FLAG_TRUE) {
            survey.setIsPhoto(true);
        }

        survey.setIsComplain(false);
        if (cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_COMPLAIN))) == Constants.FLAG_TRUE) {
            survey.setIsComplain(true);
        }

        survey.setIsCompetitor(false);
        if (cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_COMPETITOR))) == Constants.FLAG_TRUE) {
            survey.setIsCompetitor(true);
        }

//        oldBindView(s, viewHolder);
        updatedBindView(survey, viewHolder);
    }

    private Store getStore(String storeId) {

        if (TextUtils.isEmpty(storeId)) {
            return null;
        }

        String[] columns = {StoreTable.COLUMN_STORE_NAME, StoreTable.COLUMN_CATEGORY_CODE, StoreTable.COLUMN_CITY, StoreTable.COLUMN_STREET};

        Cursor c = context.getContentResolver().query(StoreTable.URI, columns, StoreTable.COLUMN_STORE_ID + " = ?", new String[]{storeId}, null);

        if (c != null && c.moveToFirst()) {
            Store s = new Store();
            s.setName(c.getString(c.getColumnIndex(StoreTable.COLUMN_STORE_NAME)));
            s.setCity(c.getString(c.getColumnIndex(StoreTable.COLUMN_CITY)));
            s.setCategoryCode(c.getInt(c.getColumnIndex(StoreTable.COLUMN_CATEGORY_CODE)));
            s.setStreet(c.getString(c.getColumnIndex(StoreTable.COLUMN_STREET)));
            c.close();
            return s;
        }

        return null;
    }

    /**
     * Bind Survey object to view holder.
     *
     * @param s          Survey object taken from DB.
     * @param viewHolder View holder object.
     */
    private void updatedBindView(Survey s, ViewHolder viewHolder) {

        Store store = null;

        Agenda agenda = agendaTable.getBySurveyDbId(s.getId());
        if (agenda != null) {
            store = storeTable.getById((int) agenda.getStoreDbId());
            viewHolder.actionButton.setTag(R.string.tag_store_db_id, agenda.getStoreDbId());
        }

        if(store == null){
            store = getStore(s.getStoreId());
        }

        String storeName = s.getStoreName();
        String storeAddress = null;
        Integer storeCategoryCode = 0;

        if (store != null) {
            storeName = store.getName();
            storeAddress = String.format("%s %s", store.getStreet(), store.getCity());
            storeCategoryCode = store.getCategoryCode();
        }

        if (TextUtils.isEmpty(storeName)) {
            storeName = context.getString(R.string.label_noname);
        }

        if (TextUtils.isEmpty(storeAddress)) {
            storeAddress = "-";
        }

        if (!dates.contains(s.getSurveyDateYYYYMMDD())) {
            dates.add(s.getSurveyDateYYYYMMDD());
            s.setHeader(true);
        }

        if (!flagHeader.containsKey(s.getId())) {
            flagHeader.put(s.getId(), s.isHeader());
        } else {
            s.setHeader(flagHeader.get(s.getId()));
        }

        if (s.isHeader()) {
            viewHolder.headerView.setText(s.getSurveyDateDDMMMMYYYY());
            viewHolder.headerContainerView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.headerContainerView.setVisibility(View.GONE);
        }


        viewHolder.idView.setText(String.valueOf(s.getId()));

        viewHolder.storeNameView.setText(storeName);

        viewHolder.storeAddressView.setText(storeAddress);
        viewHolder.historyDateView.setText(s.formatSurveyDate(Constants.HISTORY_FORMAT_DATE));

        viewHolder.actionButton.setTag(R.string.tag_store, s.getStoreId());
        viewHolder.actionButton.setTag(R.string.tag_survey, s);

        viewHolder.reUploadButton.setTag(R.string.tag_survey, s);
        viewHolder.reUploadButton.setVisibility(
                s.getSyncStatus().equals(Constants.SYNC_STATUS_FAILED) ||
                        s.getStatusData().equals(Constants.SYNC_STATUS_FAILED) ||
                        s.getStatusImage().equals(Constants.SYNC_STATUS_FAILED) ? View.VISIBLE : View.GONE);

        switch (storeCategoryCode) {
            case StoreCategory.CODE_PLATINUM:
                viewHolder.storeCategory.setText(R.string.icon_bookmark);
                viewHolder.storeCategory.setTextColor(context.getResources().getColor(R.color.text_dark_primary));
                break;
            case StoreCategory.CODE_GOLD:
                viewHolder.storeCategory.setText(R.string.icon_bookmark);
                viewHolder.storeCategory.setTextColor(context.getResources().getColor(R.color.text_yellow));
                break;
            default:
                viewHolder.storeCategory.setText(R.string.icon_bookmark_o);
                viewHolder.storeCategory.setTextColor(context.getResources().getColor(R.color.text_dark_primary));
                break;
        }

        if (Constants.DEBUG) {
            //Log.d(TAG, "store: " + s.getStoreName() + " | price list: " + s.getPrices().size() + " | is survey? " + s.getIsSurvey());
        }

        boolean isSurveyPrice = (s.getIsSurvey() || (s.getPrices() != null && !s.getPrices().isEmpty()));
        boolean isSurveyImage = s.getIsPhoto() || (s.getImages() != null && !s.getImages().isEmpty());
        boolean isSurveyComplain = s.getIsComplain() || (!TextUtils.isEmpty(s.getComplainNote())) && !s.getComplainNote().equals("[]");
        boolean isSurveyCompetitor = s.isCompetitors() || s.isCompetitorNotes();

        setIndicator(isSurveyPrice, s.getStatusData(), viewHolder.listStatusView);
        setIndicator(isSurveyImage, s.getStatusImage(), viewHolder.imageStatusView);
        setIndicator(isSurveyComplain, s.getStatusData(), viewHolder.complainStatusView);
        setIndicator(isSurveyCompetitor, s.getStatusData(), viewHolder.competitorStatusView);

    }

    private void setIndicator(boolean flag, String status, TextView tv) {

        if (!flag) {
            tv.setText(R.string.icon_circle_o);
            tv.setTextColor(context.getResources().getColor(R.color.icon_status_none));
            return;
        }

        tv.setText(R.string.icon_check_circle);
        tv.setTextColor(context.getResources().getColor(R.color.icon_status_none));

        switch (status) {
            case Constants.SYNC_STATUS_SENT:
                tv.setText(R.string.icon_check_circle);
                tv.setTextColor(context.getResources().getColor(R.color.icon_status_sent));
                break;
            case Constants.SYNC_STATUS_PENDING:
                tv.setText(R.string.icon_check_circle);
                tv.setTextColor(context.getResources().getColor(R.color.icon_status_none));
                break;
            case Constants.SYNC_STATUS_SENDING:
                tv.setText(R.string.icon_wifi);
                tv.setTextColor(context.getResources().getColor(R.color.icon_status_none));
                break;
            case Constants.SYNC_STATUS_FAILED:
                tv.setText(R.string.icon_remove_circle);
                tv.setTextColor(context.getResources().getColor(R.color.icon_status_failed));
                break;
            default:
                tv.setText(R.string.icon_check_circle);
                tv.setTextColor(context.getResources().getColor(R.color.icon_status_none));
                break;
        }
    }

    /**
     * Bind Survey object to view holder.
     *
     * @param s          Survey object taken from DB.
     * @param viewHolder View holder object.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private void oldBindView(Survey s, ViewHolder viewHolder) {
        /*s.setId(cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_ID))));
        s.setSurveyId(cursor.getString((cursor.getColumnIndex(SurveyTable.COLUMN_SYNC_STATUS))));
        s.setCheckIn(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_CHECK_IN)));
        s.setSurveyDate(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_SURVEY_DATE)));
        s.setStoreName(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_STORE_NAME)));
        s.setSyncStatus(cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_SYNC_STATUS)));
        s.setCompetitorNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_COMPETITOR_NOTES)));

        if (Constants.DEBUG) Log.d(TAG, "survey id: " + s.getId());

        s.setIsSurvey(false);
        int isSurvey = cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_SURVEY)));
        if (Constants.DEBUG) Log.d(TAG, "survey entries: " + isSurvey);
        if (isSurvey == Constants.FLAG_TRUE) {
            s.setIsSurvey(true);
        }

        s.setIsPhoto(false);
        int isPhoto = cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_PHOTO)));
        if (Constants.DEBUG) Log.d(TAG, "survey photos: " + isPhoto);
        if (isPhoto == Constants.FLAG_TRUE) {
            s.setIsPhoto(true);
        }

        s.setIsComplain(false);
        int isComplain = cursor.getInt((cursor.getColumnIndex(SurveyTable.COLUMN_IS_COMPLAIN)));
        if (Constants.DEBUG) Log.d(TAG, "survey complains: " + isComplain);
        if (isComplain == Constants.FLAG_TRUE) {
            s.setIsComplain(true);
        }*/

        if (!dates.contains(s.getSurveyDateYYYYMMDD())) {
            dates.add(s.getSurveyDateYYYYMMDD());
            s.setHeader(true);
        }

        if (!flagHeader.containsKey(s.getId())) {
            flagHeader.put(s.getId(), s.isHeader());
        } else {
            s.setHeader(flagHeader.get(s.getId()));
        }

        String storeName = s.getStoreName();
        if (storeName == null || storeName.trim().length() == 0) {
            storeName = context.getString(R.string.label_noname);
        }

        viewHolder.idView.setText(String.valueOf(s.getId()));
        viewHolder.storeNameView.setText(storeName);
        viewHolder.historyDateView.setText(context.getString(R.string.text_history_survey_date, s.formatSurveyDate(Constants.HISTORY_FORMAT_DATE)));
        viewHolder.listIconView.setVisibility(View.VISIBLE);
        viewHolder.imageIconView.setVisibility(View.VISIBLE);
        viewHolder.complainIconView.setVisibility(View.VISIBLE);
        viewHolder.headerContainerView.setVisibility(View.GONE);
        viewHolder.competitorIconView.setVisibility(View.VISIBLE);

        if (s.getIsSurvey() == null || !s.getIsSurvey()) {
            viewHolder.listIconView.setVisibility(View.GONE);
        }

        if (s.getIsPhoto() == null || !s.getIsPhoto()) {
            viewHolder.imageIconView.setVisibility(View.GONE);
        }

        if (s.getIsComplain() == null || !s.getIsComplain()) {
            viewHolder.complainIconView.setVisibility(View.GONE);
        }

        /*String competitors =
                cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_COMPETITORS));
        String competitorNotes =
                cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_COMPETITOR_NOTES));
        int isCompetitor =
                cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_COMPETITOR));
        int isCompetitorNotes =
                cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable
                .COLUMN_IS_COMPETITOR_NOTES));*/

        /*boolean noCompetitorData = true;
        if (!competitors.isEmpty()) {
            try {
                JSONArray arr = new JSONArray(competitors);
                if (arr.length() > 0) {
                    noCompetitorData = false;
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }*/

        /*if (Constants.DEBUG) {
            Log.d(TAG, String.format("competitor: [%d] %s | competitor note: [%d] %s", isCompetitor,
                                     competitors, isCompetitorNotes, competitorNotes));
        }
        if (isCompetitor == Constants.FLAG_FALSE
            && isCompetitorNotes == Constants.FLAG_FALSE
            && noCompetitorData) {
            viewHolder.competitorIconView.setVisibility(View.GONE);
        }*/

        if (!s.isCompetitors()
                && !s.isCompetitorNotes()
                && s.getCompetitorPrograms().isEmpty()) {
            viewHolder.competitorIconView.setVisibility(View.GONE);
        }

        viewHolder.syncStatusView.setVisibility(View.VISIBLE);
        if (Constants.SYNC_STATUS_SENT.equalsIgnoreCase(s.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_upload);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_success));
        } else if (Constants.SYNC_STATUS_FAILED.equalsIgnoreCase(s.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_warning);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_failed));
        } else if (Constants.SYNC_STATUS_SENDING.equalsIgnoreCase(s.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_wifi);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
        } else if (Constants.SYNC_STATUS_PENDING.equalsIgnoreCase(s.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_upload);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
        } else {
            viewHolder.syncStatusView.setVisibility(View.GONE);
        }

        if (s.isHeader()) {
            viewHolder.headerView.setText(s.getSurveyDateDDMMMMYYYY());
            viewHolder.headerContainerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        /*View convertView =
                LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);*/
        View convertView =
                LayoutInflater.from(context).inflate(R.layout.row_history_survey, parent, false);
//        ViewHolder viewHolder = oldNewView(convertView);
        ViewHolder viewHolder = updatedNewView(convertView);

        convertView.setTag(viewHolder);

        return convertView;
    }

    /**
     * Map UI elements to view holder object using the updated layout (https://trello
     * .com/c/07E6CHaK).
     *
     * @param convertView Inflated view layout.
     * @return ViewHolder Mapped UI elements.
     */
    private ViewHolder updatedNewView(View convertView) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.isHeader = false;
        viewHolder.idView = (TextView) convertView.findViewById(R.id.row_history_data_id);
        viewHolder.storeCategory = (TextView) convertView.findViewById(R.id.history_store_category);
        viewHolder.storeNameView = (TextView) convertView.findViewById(R.id.row_history_store_name);
        viewHolder.storeAddressView =
                (TextView) convertView.findViewById(R.id.history_store_address);
        viewHolder.listStatusView =
                (TextView) convertView.findViewById(R.id.history_survey_price_status);
        viewHolder.imageStatusView =
                (TextView) convertView.findViewById(R.id.history_survey_image_status);
        viewHolder.complainStatusView =
                (TextView) convertView.findViewById(R.id.history_survey_complain_status);
        viewHolder.competitorStatusView =
                (TextView) convertView.findViewById(R.id.history_survey_competitor_status);
        viewHolder.actionButton = (Button) convertView.findViewById(R.id.history_action_button);
        viewHolder.historyDateView = (TextView) convertView.findViewById(R.id.row_history_date);
        viewHolder.headerContainerView = convertView.findViewById(R.id.row_list_header);
        viewHolder.headerView =
                (TextView) viewHolder.headerContainerView.findViewById(R.id.txt_item_header);
        viewHolder.reUploadButton =
                (Button) convertView.findViewById(R.id.history_survey_reupload_btn);

        return viewHolder;
    }

    /**
     * Map UI elements to view holder object.
     *
     * @param convertView Inflated view layout.
     * @return ViewHolder Mapped UI elements.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private ViewHolder oldNewView(View convertView) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.idView = (TextView) convertView.findViewById(R.id.row_history_data_id);
        viewHolder.storeNameView = (TextView) convertView.findViewById(R.id.row_history_store_name);
        viewHolder.listIconView = (TextView) convertView.findViewById(R.id.row_history_list_icon);
        viewHolder.imageIconView = (TextView) convertView.findViewById(R.id.row_history_image_icon);
        viewHolder.complainIconView =
                (TextView) convertView.findViewById(R.id.row_history_comment_icon);
        viewHolder.competitorIconView =
                (TextView) convertView.findViewById(R.id.row_history_competitor_icon);
        viewHolder.historyDateView = (TextView) convertView.findViewById(R.id.row_history_date);
        viewHolder.syncStatusView =
                (TextView) convertView.findViewById(R.id.row_history_sync_status);
        viewHolder.reUploadButton =
                (Button) convertView.findViewById(R.id.history_survey_reupload_btn);
        viewHolder.isHeader = false;
        viewHolder.headerContainerView = convertView.findViewById(R.id.row_list_header);
        viewHolder.headerView =
                (TextView) viewHolder.headerContainerView.findViewById(R.id.txt_item_header);

        return viewHolder;
    }

    @Override
    public void notifyDataSetChanged() {
        flagHeader.clear();
        dates.clear();
        super.notifyDataSetChanged();
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        String selection = null;
        String[] selectionArgs = null;
        if (constraint != null && constraint.toString().trim().length() > 0) {
            selection = "lower(" + SurveyTable.COLUMN_STORE_NAME + ") like ? ";
            selectionArgs = new String[]{"%" + constraint.toString().toLowerCase() + "%"};
        }

        String sortOrder = SurveyTable.COLUMN_SURVEY_DATE + " desc";

        return context.getContentResolver()
                .query(CRMContentProvider.URI_SURVEY, SurveyTable.ALL_COLUMNS, selection,
                        selectionArgs, sortOrder);
    }

    private static class ViewHolder {
        Button actionButton;
        Button reUploadButton;
        TextView storeCategory;
        TextView storeNameView;
        TextView storeAddressView;
        TextView imageIconView;
        TextView listIconView;
        TextView complainIconView;
        TextView competitorIconView;
        TextView imageStatusView;
        TextView listStatusView;
        TextView complainStatusView;
        TextView competitorStatusView;
        TextView headerView;
        TextView historyDateView;
        TextView idView;
        TextView syncStatusView;
        View headerContainerView;
        boolean isHeader;
    }
}
