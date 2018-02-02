package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Survey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wendi on 05-Jan-15.
 */
public class HistorySurveyAdapter extends ArrayAdapter<Survey> {

    private static final String TAG = HistorySurveyAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private List<Survey> surveyList;
    private List<Survey> groupSurveyList;
    private Context context;

    public HistorySurveyAdapter(Context context,
                                int resource,
                                List<Survey> surveyList) {

        super(context, resource, surveyList);
        this.context = context;
        this.surveyList = surveyList;
        grouping();
    }

    @Override
    public int getCount() {
        if (groupSurveyList != null) {
            return groupSurveyList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public Survey getItem(int position) {
        return groupSurveyList.get(position);
    }

    @Override
    public int getPosition(Survey item) {
        return groupSurveyList.indexOf(item);
    }

    @Override
    public void add(Survey item) {
        surveyList.add(item);
    }

    @Override
    public void addAll(Collection<? extends Survey> collection) {
        surveyList.addAll(collection);
    }

    @Override
    public void clear() {
        surveyList = new ArrayList<>();
        groupSurveyList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Survey s = getItem(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            ViewHolder viewHolder = new ViewHolder();

            if (itemViewType == VIEW_TYPE_HEADER) {
                // If its a section ?
                convertView = LayoutInflater.from(context).inflate(R.layout.row_list_header, parent, false);

                viewHolder.headerView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.isHeader = true;
            } else {
                // Regular row
                convertView = LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);

                viewHolder.isHeader = false;
                viewHolder.storeNameView = (TextView) convertView.findViewById(R.id.row_history_store_name);
                viewHolder.listIconView = (TextView) convertView.findViewById(R.id.row_history_list_icon);
                viewHolder.imageIconView = (TextView) convertView.findViewById(R.id.row_history_image_icon);
                viewHolder.complainIconView = (TextView) convertView.findViewById(R.id.row_history_comment_icon);
                viewHolder.competitorIconView = (TextView) convertView.findViewById(R.id.row_history_competitor_icon);
                viewHolder.historyDateView = (TextView) convertView.findViewById(R.id.row_history_date);
            }

            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (itemViewType == VIEW_TYPE_HEADER) {
            viewHolder.headerView.setText(s.getCheckInDateDDMMMMYYYY());
        } else {

            String storeName = s.getStoreName();
            if (storeName == null || storeName.trim().length() == 0) {
                storeName = context.getString(R.string.label_noname);
            }

            viewHolder.storeNameView.setText(storeName);
            viewHolder.historyDateView.setText(context.getString(R.string.text_history_survey_date, s.formatCheckIn(Constants.HISTORY_FORMAT_DATE)));

            viewHolder.listIconView.setVisibility(View.VISIBLE);
            viewHolder.imageIconView.setVisibility(View.VISIBLE);
            viewHolder.complainIconView.setVisibility(View.VISIBLE);
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

            if (!s.isCompetitors()
                    && !s.isCompetitorNotes()
                    && (s.getCompetitorPrograms() == null || s.getCompetitorPrograms().size() == 0)
                    && s.getNotes() == null) {
                viewHolder.competitorIconView.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private void grouping() {

        groupSurveyList = new ArrayList<>(0);
        List<Survey> tempSurveyList = new ArrayList<>(0);
        tempSurveyList.addAll(surveyList);
        Collections.sort(tempSurveyList, new Comparator<Survey>() {
            @Override
            public int compare(Survey o1, Survey o2) {
                return o2.getCheckIn().compareTo(o1.getCheckIn());
            }
        });

        String header = "";
        for (Survey s : tempSurveyList) {

            if (!header.equals(s.getCheckInDateYYYYMMDD())) {
                header = s.getCheckInDateYYYYMMDD();
                Survey sHeader = new Survey();
                sHeader.setHeader(true);
                sHeader.setCheckIn(s.getCheckIn());
                groupSurveyList.add(sHeader);
            }

            groupSurveyList.add(s);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        grouping();
        super.notifyDataSetChanged();
    }


    static class ViewHolder {
        TextView storeNameView, imageIconView, listIconView, complainIconView, competitorIconView,
                headerView, historyDateView;
        boolean isHeader;
    }

}
