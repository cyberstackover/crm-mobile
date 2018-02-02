package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.util.Calendar;

/**
 * Created by Mark on 1/5/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class AgendaCursorAdapter extends SimpleCursorAdapter {
    private static final String TAG = AgendaCursorAdapter.class.getSimpleName();
    Context context;

    public AgendaCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                               int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor) {
        if (Constants.DEBUG) {
            Log.d(
                    TAG, "agenda id: " +
                         cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_ID)));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        Integer storeCat =
                cursor.getInt(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_CATEGORY_CODE));
        switch (storeCat) {
            case StoreCategory.CODE_PLATINUM:
                holder.storeCat.setText(R.string.icon_bookmark);
                holder.storeCat
                        .setTextColor(context.getResources().getColor(R.color.text_dark_primary));
                break;
            case StoreCategory.CODE_GOLD:
                holder.storeCat.setText(R.string.icon_bookmark);
                holder.storeCat.setTextColor(context.getResources().getColor(R.color.text_yellow));
                break;
            default:
                holder.storeCat.setText(R.string.icon_bookmark_o);
                holder.storeCat
                        .setTextColor(context.getResources().getColor(R.color.text_dark_primary));
                break;
        }

        String agendaDate = cursor.getString(
                cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_AGENDA_DATE));
        String checkIn, checkOut;
        checkIn = cursor.getString(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_IN));
        checkOut = cursor.getString(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_CHECK_OUT));
        if (checkIn == null) {
            holder.actionBtn.setText(R.string.action_check_in);
            holder.actionBtn.setBackgroundResource(R.drawable.button_green);
            holder.actionBtn.setTextColor(context.getResources().getColor(R.color.text_light));
            holder.actionBtn.setEnabled(true);
            holder.actionBtn.setTag(R.string.tag_action, Constants.ACTION_CHECK_IN);
        } else if (checkOut == null) {
            holder.actionBtn.setText(R.string.action_check_out);
            holder.actionBtn.setBackgroundResource(R.drawable.button_red);
            holder.actionBtn.setTextColor(context.getResources().getColor(R.color.text_light));
            holder.actionBtn.setEnabled(true);
            holder.actionBtn.setTag(R.string.tag_action, Constants.ACTION_CHECK_OUT);
        } else {
            holder.actionBtn.setText(R.string.icon_check);
            holder.actionBtn.setBackgroundResource(R.drawable.button_green);
            holder.actionBtn
                    .setTextColor(context.getResources().getColor(R.color.btn_green_active));
            holder.actionBtn.setEnabled(false);
            holder.actionBtn.setTag(R.string.tag_action, "");
        }
        if (agendaDate.equals(DateUtil.formatDBDateOnly(Calendar.getInstance().getTime())) ||
            checkIn != null) {
            holder.actionBtn.setVisibility(View.VISIBLE);
        } else {
            holder.actionBtn.setVisibility(View.GONE);
        }

        holder.actionBtn.setTag(
                R.string.tag_object_id,
                cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_ID)));
        holder.storeId.setText(String.valueOf(
                cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_STORE_ID))));
        holder.storeName.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_STORE_NAME)));
        holder.storeAddress.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_STREET)));
        holder.storeCity.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_CITY)));
        holder.storeOwnerName.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_OWNER_NAME)));
        holder.storeOwnerMobile.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_PHONE_MOBILE)));
        holder.storeLastCheckIn.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(StoreTable.COLUMN_LAST_CHECKIN)));

        int surveyDbId =
                cursor.getInt(cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_SURVEY_DB_ID));
        if (Constants.DEBUG) Log.d(TAG, "survey id: " + surveyDbId);

        int isSurvey = cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_SURVEY));
        if (Constants.DEBUG) Log.d(TAG, "survey entries: " + isSurvey);
        if (isSurvey == Constants.FLAG_TRUE) {
            holder.iconList.setVisibility(View.VISIBLE);
        } else {
            holder.iconList.setVisibility(View.GONE);
        }

        int isPhoto = cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_PHOTO));
        if (Constants.DEBUG) Log.d(TAG, "survey photos: " + isPhoto);
        if (isPhoto == Constants.FLAG_TRUE) {
            holder.iconCamera.setVisibility(View.VISIBLE);
        } else {
            holder.iconCamera.setVisibility(View.GONE);
        }

        int isComplain =
                cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_COMPLAIN));
        if (Constants.DEBUG) Log.d(TAG, "survey complains: " + isComplain);
        if (isComplain == Constants.FLAG_TRUE) {
            holder.iconComment.setVisibility(View.VISIBLE);
        } else {
            holder.iconComment.setVisibility(View.GONE);
        }

        String competitors =
                cursor.getString(cursor.getColumnIndex(SurveyTable.COLUMN_COMPETITORS));
        int isCompetitor =
                cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_COMPETITOR));
        int isCompetitorNotes =
                cursor.getInt(cursor.getColumnIndexOrThrow(SurveyTable.COLUMN_IS_COMPETITOR_NOTES));

        boolean noCompetitorData = true;
        if (!TextUtils.isEmpty(competitors)) {
            if (!competitors.equals("[]")) {
                noCompetitorData = false;
            }
            /*try {
                JSONArray arr = new JSONArray(competitors);
                if (arr.length() > 0)
                    noCompetitorData = false;
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }*/
        }

        if (Constants.DEBUG) Log.d(TAG, "survey competitor: " + isCompetitor);
        if (isCompetitor == Constants.FLAG_TRUE
            || isCompetitorNotes == Constants.FLAG_TRUE
            || !noCompetitorData) {
            holder.iconCompetitor.setVisibility(View.VISIBLE);
        } else {
            holder.iconCompetitor.setVisibility(View.GONE);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_survey_agenda, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.storeId = (TextView) view.findViewById(R.id.row_agenda_store_id);
        holder.storeName = (TextView) view.findViewById(R.id.row_agenda_store_name);
        holder.storeCat = (TextView) view.findViewById(R.id.row_agenda_store_cat);
        holder.storeAddress = (TextView) view.findViewById(R.id.row_agenda_store_address);
        holder.storeCity = (TextView) view.findViewById(R.id.row_agenda_store_city);
        holder.storeOwnerName = (TextView) view.findViewById(R.id.row_agenda_store_owner_name);
        holder.storeOwnerMobile = (TextView) view.findViewById(R.id.row_agenda_store_owner_mobile);
        holder.storeLastCheckIn = (TextView) view.findViewById(R.id.row_agenda_time);
        holder.actionBtn = (Button) view.findViewById(R.id.row_agenda_action_btn);

        holder.iconList = (TextView) view.findViewById(R.id.row_agenda_ic_list);
        holder.iconCamera = (TextView) view.findViewById(R.id.row_agenda_ic_camera);
        holder.iconComment = (TextView) view.findViewById(R.id.row_agenda_ic_comment);
        holder.iconCompetitor = (TextView) view.findViewById(R.id.row_agenda_ic_competitor);

        view.setTag(holder);
        //bindView(view, context, cursor);

        return view;
    }

    private class ViewHolder {
        TextView storeId, storeName, storeCat, storeAddress, storeCity, storeOwnerName,
                storeOwnerMobile, storeLastCheckIn,
                iconList, iconCamera, iconComment, iconCompetitor;
        Button actionBtn;
    }
}
