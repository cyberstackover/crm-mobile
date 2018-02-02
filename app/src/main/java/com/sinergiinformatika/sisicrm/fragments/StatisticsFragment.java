package com.sinergiinformatika.sisicrm.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.MainActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.ServerErrorUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.views.GaugeView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

    private static final String TAG = StatisticsFragment.class.getSimpleName();

    private Context context;
    private TextView orderNote, orderCountText, visitNote, visitCountText;
    private View loadingHolder;
    private GaugeView orderGauge, visitGauge;
    private RestResponseHandler responseHandler;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        this.context = context;
        responseHandler = new RestResponseHandler(StatisticsFragment.this.context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (Constants.DEBUG) Log.d(TAG, response.toString());

                try {
                    if (isSuccess(response)) {
                        JSONObject data = getData(response);
                        JSONObject survey = data.getJSONObject("survey");
                        JSONObject order = data.getJSONObject("order");
                        int visited = survey.getInt("visited_count");
                        int visitedStore = survey.getInt("visited_store");
                        int storeCount = survey.getInt("store_count");
                        int approved = order.getInt("volume_approved");
                        int total = order.getInt("volume_total");
                        int orderPercentage = (int) ((approved * 100.0f) / total);
                        int visitPercentage = (int) ((visitedStore * 100.0f) / storeCount);
                        float orderDegree = percentageToDegree(orderPercentage);
                        float visitDegree = percentageToDegree(visitPercentage);
                        String size = order.getString("size");

                        Spanned orderNoteStr = Html.fromHtml(
                                StatisticsFragment.this.context.getString(
                                        R.string.message_statistics_note_order,
                                        String.valueOf(approved),
                                        String.valueOf(total),
                                        size));
                        Spanned visitNoteStr = Html.fromHtml(
                                StatisticsFragment.this.context.getString(
                                        R.string.message_statistics_note_visit,
                                        String.valueOf(visited),
                                        String.valueOf(visitedStore),
                                        String.valueOf(storeCount)));

                        if (Constants.DEBUG) {
                            Log.d(TAG, "order sweep: " + orderDegree);
                            Log.d(TAG, "survey sweep: " + visitDegree);
                            Log.d(TAG, "order note: " + orderNoteStr);
                            Log.d(TAG, "survey note: " + visitNoteStr);
                        }

                        visitGauge.setSweep(visitDegree);
                        visitNote.setText(visitNoteStr);
                        visitCountText.setText(String.valueOf(visitedStore));
                        orderGauge.setSweep(orderDegree);
                        orderNote.setText(orderNoteStr);
                        orderCountText.setText(String.format("%d%%", orderPercentage));
                    } else {
                        if (isVisible()) {
                            String errMsg = ServerErrorUtil.getErrorMessage(
                                    StatisticsFragment.this.context, getErrorCode(response));
                            Toast.makeText(
                                    StatisticsFragment.this.context, errMsg, Toast.LENGTH_LONG)
                                 .show();
                            if (getErrorCode(response) == Constants.ERROR_CODE_SESSION_EXPIRED) {
                                if (User.rePostLogin(context, false)) {
                                    refresh();
                                } else {
                                    if (StatisticsFragment.this.context instanceof MainActivity) {
                                        MainActivity mainActivity =
                                                (MainActivity) StatisticsFragment.this.context;
                                        mainActivity.attemptLogout(null);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void onStart() {
                loadingHolder.setVisibility(View.VISIBLE);
                super.onStart();
            }

            @Override
            public void onFinish() {
                loadingHolder.setVisibility(View.GONE);
                super.onFinish();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        TextView monthLabel = (TextView) view.findViewById(R.id.statistics_month_label);
        View visitHeader = view.findViewById(R.id.statistics_header_visit);
        View orderHeader = view.findViewById(R.id.statistics_header_order);
        TextView visitHeaderText = (TextView) visitHeader.findViewById(R.id.txt_item_header);
        TextView orderHeaderText = (TextView) orderHeader.findViewById(R.id.txt_item_header);
        loadingHolder = view.findViewById(R.id.statistics_loading_holder);
        visitGauge = (GaugeView) view.findViewById(R.id.statistics_gauge_visit);
        orderGauge = (GaugeView) view.findViewById(R.id.statistics_gauge_order);
        visitNote = (TextView) view.findViewById(R.id.statistics_note_visit);
        orderNote = (TextView) view.findViewById(R.id.statistics_note_order);
        visitCountText = (TextView) view.findViewById(R.id.statistics_amount_visit);
        orderCountText = (TextView) view.findViewById(R.id.statistics_amount_order);

        // Fill labels
        monthLabel.setText(DateUtil.formatMonthDate(Calendar.getInstance().getTime()));
        visitHeaderText.setText(getString(R.string.label_visit_number));
        orderHeaderText.setText(getString(R.string.label_approved_order));
        visitNote.setText(getString(
                R.string.message_statistics_note_visit,
                getString(R.string.hypen),
                getString(R.string.hypen),
                getString(R.string.hypen)));
        orderNote.setText(getString(
                R.string.message_statistics_note_order,
                getString(R.string.hypen),
                getString(R.string.hypen),
                getString(R.string.hypen)));
        visitCountText.setText(R.string.hypen);
        orderCountText.setText(R.string.hypen);

        setupGaugeParams();
        refresh();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            refresh();
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    private void refresh() {
        if (responseHandler != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE, 1);
            RestClient.getInstance(context, responseHandler)
                      .getStatistics(
                              User.getInstance(context).getToken(),
                              cal.getTime(),
                              Calendar.getInstance().getTime());
        }
    }

    private void setupGaugeParams() {
        float baseStrokeWidth = getResources().getDimension(R.dimen.gauge_base_stroke_width);
        float strokeWidth = getResources().getDimension(R.dimen.gauge_stroke_width);

        visitGauge.setStrokeWidth(strokeWidth);
        visitGauge.setBaseStrokeWidth(baseStrokeWidth);
        visitGauge.setColorResId(R.color.gauge_color);

        orderGauge.setStrokeWidth(strokeWidth);
        orderGauge.setBaseStrokeWidth(baseStrokeWidth);
        orderGauge.setColorResId(R.color.gauge_color);
    }

    private float percentageToDegree(int percentage) {
        return (percentage / 100.0f) * 360;
    }
}
