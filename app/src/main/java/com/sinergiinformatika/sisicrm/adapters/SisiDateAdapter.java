package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Created by Mark on 3/31/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SisiDateAdapter extends CaldroidGridAdapter {

    private static final String TAG = SisiDateAdapter.class.getSimpleName();

    private Context context;
    private List<String> agendaDates;

    public SisiDateAdapter(Context context, int month, int year,
                           HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
        this.context = context;

        agendaDates = new ArrayList<>();
        Object obj = extraData.get(Constants.EXTRA_AGENDA_DATES);
        if (obj != null && obj instanceof List) {
            for (Object o : (List) obj) {
                if (o instanceof String)
                    agendaDates.add((String) o);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_date, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.dateText = (TextView) convertView.findViewById(R.id.date_text);
            vh.selectionImage = (ImageView) convertView.findViewById(R.id.date_selection_image);
            vh.signImage = (ImageView) convertView.findViewById(R.id.date_sign_image);
            convertView.setTag(vh);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        DateTime dateTime = getDatetimeList().get(position);
        String dateTimeStr = dateTime.format("YYYY-MM-DD");

        // Set text
        viewHolder.dateText.setText(String.format("%d", dateTime.getDay()));

        if (dateTime.getMonth() != month) {
            viewHolder.dateText.setVisibility(View.INVISIBLE);
            viewHolder.selectionImage.setVisibility(View.INVISIBLE);
            viewHolder.signImage.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.dateText.setVisibility(View.VISIBLE);
            viewHolder.selectionImage.setVisibility(View.VISIBLE);
            viewHolder.signImage.setVisibility(View.VISIBLE);

            boolean agendaExists = false;
            if (agendaDates.contains(dateTimeStr)) {
                agendaExists = true;
                if (Constants.DEBUG) Log.d(TAG, "agenda EXISTS for: " + dateTimeStr);
            } else {
                viewHolder.signImage.setImageResource(android.R.color.transparent);
                if (Constants.DEBUG) Log.d(TAG, "NO agenda for: " + dateTimeStr);
            }

            if (dateTime.equals(getToday())) {
                viewHolder.dateText.setTextAppearance(context, R.style.TextRed_Small_Bold);
                if (agendaExists)
                    viewHolder.signImage.setImageResource(R.drawable.stroke_date_agenda_today);
            } else {
                viewHolder.dateText.setTextAppearance(context, R.style.TextPrimary_Small);
                if (agendaExists)
                    viewHolder.signImage.setImageResource(R.drawable.stroke_date_agenda);
            }
        }

        boolean shouldResetDisabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDatesMap
                .containsKey(dateTime))) {

            viewHolder.dateText.setTextColor(CaldroidFragment.disabledTextColor);
        } else {
            shouldResetDisabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
            /*if (CaldroidFragment.selectedBackgroundDrawable != -1) {
                viewHolder.selectionImage.setImageResource(CaldroidFragment.selectedBackgroundDrawable);
            } else {
                viewHolder.selectionImage.setImageResource(R.drawable.bg_date_selected);
            }*/
            viewHolder.selectionImage.setImageResource(R.drawable.bg_date_selected);
        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetSelectedView) {
            viewHolder.selectionImage.setImageResource(android.R.color.transparent);
        }

        if (shouldResetDisabledView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                viewHolder.dateText.setTextAppearance(context, R.style.TextRed_Small_Bold);
            } else {
                if (dateTime.getMonth() != month) {
                    viewHolder.dateText.setTextAppearance(context, R.style.TextSecondary_Small);
                } else {
                    viewHolder.dateText.setTextAppearance(context, R.style.TextPrimary_Small);
                }
            }
        }

        return convertView;
    }

    @Override
    public void setExtraData(HashMap<String, Object> extraData) {
        super.setExtraData(extraData);

        agendaDates.clear();
        Object obj = extraData.get(Constants.EXTRA_AGENDA_DATES);
        if (obj != null && obj instanceof List) {
            for (Object o : (List) obj) {
                if (o instanceof String)
                    agendaDates.add((String) o);
            }
        }
    }

    private class ViewHolder {
        ImageView selectionImage, signImage;
        TextView dateText;
    }
}
