package com.sinergiinformatika.sisicrm.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.OrderActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.StoreDetailActivity;
import com.sinergiinformatika.sisicrm.SurveyActivity;
import com.sinergiinformatika.sisicrm.SurveyDetailActivity;
import com.sinergiinformatika.sisicrm.adapters.AgendaCursorAdapter;
import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.dialogs.ActionButtonsDialog;
import com.sinergiinformatika.sisicrm.dialogs.DatePickerFragment;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyAgendaFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener,
                   AdapterView.OnItemClickListener, View.OnClickListener,
                   DatePickerDialog.OnDateSetListener {
    private static final String TAG = SurveyAgendaFragment.class.getSimpleName();

    private Calendar cal;
    private Date today;
    private List<String> agendaDates;
    private Context context;
    private Button blankActionBtn;
    private TextView dateLabel, blankTitle, blankSubtitle;
    private ListView agendaList;
    private View loadingView, blankHolder;
    private ActionButtonsDialog buttonsDialog;
    private AgendaCursorAdapter cursorAdapter;
    private StoreTable storeTable = null;
    private SisiDateFragment dateFragment;

    public SurveyAgendaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        setTitle(getString(R.string.icon_list));
        storeTable = new StoreTable(this.context);
        cal = Calendar.getInstance();
        today = cal.getTime();

        AgendaTable agendaTable = new AgendaTable(this.context);
        agendaDates = agendaTable.getAgendaDates(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_agenda, container, false);
        blankHolder = view.findViewById(R.id.survey_agenda_blank_holder);
        dateLabel = (TextView) view.findViewById(R.id.survey_list_date_label);
        agendaList = (ListView) view.findViewById(R.id.survey_agenda_list);
        blankTitle = (TextView) view.findViewById(R.id.survey_agenda_blank_text);
        blankSubtitle = (TextView) view.findViewById(R.id.survey_agenda_blank_notes);
        blankActionBtn = (Button) view.findViewById(R.id.survey_agenda_blank_button);
        loadingView = inflater.inflate(R.layout.loading, agendaList, false);

        view.post(new Runnable() {
            @Override
            public void run() {
                initCalendar();
            }
        });

        dateLabel.setText(DateUtil.formatDayDate(cal.getTime()));
        dateLabel.setOnClickListener(this);
        agendaList.addFooterView(loadingView);
        agendaList.setOnItemClickListener(this);
        agendaList.setOnItemLongClickListener(this);

        cursorAdapter = new AgendaCursorAdapter(context, R.layout.row_survey_agenda, null,
                                                AgendaTable.COMPLETE_COLUMN, new int[]{}, 0);

        agendaList.setAdapter(cursorAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Loader<Cursor> cursorLoader = getLoaderManager().getLoader(0);
        if (cursorLoader != null && !cursorLoader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AgendaTable agendaTable = new AgendaTable(context);
            agendaDates =
                    agendaTable.getAgendaDates(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));

            if (Constants.DEBUG) {
                for (String temp : agendaDates) {
                    Log.d(TAG, "agenda entry for: " + temp);
                }
            }

            if (dateFragment != null) {
                if (Constants.DEBUG) Log.d(TAG, "refreshing calendar");
                HashMap<String, Object> extraData = new HashMap<>();
                extraData.put(Constants.EXTRA_AGENDA_DATES, agendaDates);
                dateFragment.setExtraData(extraData);
                dateFragment.refreshView();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onPause();
        Loader<Cursor> cursorLoader = getLoaderManager().getLoader(0);
        if (cursorLoader != null && !cursorLoader.isReset()) {
            cursorLoader.reset();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (Constants.DEBUG) Log.d(TAG, "creating loader...");
        if (loadingView == null) {
            loadingView = LayoutInflater.from(context).inflate(R.layout.loading, agendaList, false);
            agendaList.addFooterView(loadingView);
        }


//        String sortOrder = StoreTable.TABLE_NAME + "." + StoreTable.COLUMN_STORE_NAME;
        return new CursorLoader(context, CRMContentProvider.URI_AGENDA,
                                AgendaTable.COMPLETE_COLUMN,
                                AgendaTable.COLUMN_AGENDA_DATE + " = ?",
                                new String[]{DateUtil.formatDBDateOnly(cal.getTime())}, null);

        /*return new CursorLoader(context, CRMContentProvider.URI_SURVEY,
                SurveyTable.ALL_COLUMNS, SurveyTable.COLUMN_PLAN_DATE + " = ?",
                new String[]{DateUtil.formatDBDateOnly(cal.getTime())},
                SurveyTable.COLUMN_STORE_NAME);*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (Constants.DEBUG) Log.d(TAG, "load finished");
        if (loadingView != null) {
            agendaList.removeFooterView(loadingView);
            loadingView = null;
        }

        cursorAdapter.swapCursor(cursor);
        if (cursor.getCount() == 0) {
            if (cal.getTime().before(today)) {
                blankTitle.setText(R.string.message_blank_agenda_past);
                blankSubtitle.setVisibility(View.GONE);
                blankActionBtn.setVisibility(View.GONE);
            } else {
                blankTitle.setText(R.string.message_blank_agenda);
                blankSubtitle.setVisibility(View.VISIBLE);
                blankActionBtn.setVisibility(View.VISIBLE);
            }

            blankHolder.setVisibility(View.VISIBLE);
        } else {
            blankHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (Constants.DEBUG) Log.d(TAG, "agenda id: " + l);
        List<String> actions = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Button actionBtn = (Button) view.findViewById(R.id.row_agenda_action_btn);
        TextView storeIdText = (TextView) view.findViewById(R.id.row_agenda_store_id);
        Long storeId = null;

        try {
            storeId = Long.valueOf(storeIdText.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, "error converting store id: " + e.getMessage(), e);
        }

        if (actionBtn.getTag(R.string.tag_action).equals(Constants.ACTION_CHECK_IN)) {
            actions.add(Constants.ACTION_DELETE);
            labels.add(getString(R.string.action_delete));
        }

        boolean isAllowSurveyWithoutCheckIn =
                User.getInstance(context).isAllowSurveyWithoutCheckIn();
        Cursor cursor = (Cursor) cursorAdapter.getItem(i);
        String agendaDate = cursor.getString(
                cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_AGENDA_DATE));

        if (agendaDate.equals(DateUtil.formatDBDateOnly(DateUtil.now())) &&
            (isAllowSurveyWithoutCheckIn ||
             actionBtn.getTag(R.string.tag_action).equals(Constants.ACTION_CHECK_OUT))) {
            actions.add(Constants.ACTION_SURVEY);
            labels.add(getString(R.string.label_survey));
        }

        if (!User.getInstance(context).isAreaManager()) {
            actions.add(Constants.ACTION_ORDER);
            labels.add(getString(R.string.label_order));
        }

        actions.add(Constants.ACTION_DETAIL);
        labels.add(getString(R.string.action_detail));

        if (storeId == null) {
            buttonsDialog = ActionButtonsDialog.newInstance(l, actions, labels, this);
        } else {
            buttonsDialog = ActionButtonsDialog.newInstance(l, storeId, actions, labels, this);
        }
        buttonsDialog.show(getChildFragmentManager(), Constants.TAG_ACTION_BUTTONS);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Button actionBtn = (Button) view.findViewById(R.id.row_agenda_action_btn);
        Cursor cursor = (Cursor) cursorAdapter.getItem(i);
        String agendaDate = cursor.getString(
                cursor.getColumnIndexOrThrow(AgendaTable.COLUMN_AGENDA_DATE));

        if (agendaDate.equals(DateUtil.formatDBDateOnly(DateUtil.now())) &&
            actionBtn.getTag(R.string.tag_action).equals(Constants.ACTION_CHECK_OUT)) {
            Agenda agenda = new Agenda();
            AgendaTable.setValues(cursor, agenda);
            addSurvey(agenda);
        } else {
            TextView storeIdText = (TextView) view.findViewById(R.id.row_agenda_store_id);
            int storeId = 0;
            try {
                storeId = Integer.valueOf(storeIdText.getText().toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, "error converting store id: " + e.getMessage(), e);
            }

            storeDetail(storeId);
        }
    }

    @Override
    public void onClick(View view) {
        String action = (String) view.getTag(R.string.tag_action);
        Long id = (Long) view.getTag(R.string.tag_object_id);
        Long extraId = (Long) view.getTag(R.string.tag_extra_id);

        if (action != null && id != null) {
            if (action.equals(Constants.ACTION_DELETE)) {
                Uri uri = Uri.parse(CRMContentProvider.URI_AGENDA + "/" + id);
                int affected = context.getContentResolver().delete(uri, null, null);
                if (affected == 1) {
                    Toast.makeText(context, R.string.message_delete_succeeded, Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Toast.makeText(context, R.string.message_delete_failed, Toast.LENGTH_SHORT)
                         .show();
                }
            }
            if (action.equals(Constants.ACTION_ORDER)) {
                long storeId = extraId != null ? extraId : id;
                StoreTable storeTable = new StoreTable(context);
                Store store = storeTable.getById((int) storeId);

                if (store != null) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra(Constants.EXTRA_STORE, (Parcelable) store);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.error_invalid_store, Toast.LENGTH_LONG).show();
                }
            }
            if (action.equals(Constants.ACTION_DETAIL)) {
                long storeId = extraId != null ? extraId : id;
                storeDetail(storeId);
            }

            if (action.equals(Constants.ACTION_SURVEY)) {
                AgendaTable agendaTable = new AgendaTable(context);
                Agenda agenda = agendaTable.getById(id.intValue());

                if (agenda == null) {
                    Toast.makeText(context, R.string.error_no_selected_agenda, Toast.LENGTH_SHORT)
                         .show();
                } else {
                    addSurvey(agenda);
                }

                /*Intent intent = new Intent(context, SurveyActivity.class);

                Survey survey = new Survey();
//                long storeId = extraId != null ? extraId : id;
//                StoreTable storeTable = new StoreTable(context);
//                Store store = storeTable.getById((int) storeId);
                survey.setAgendaDbId(id.intValue());
                intent.putExtra(Constants.EXTRA_SURVEY, (Parcelable) survey);

                startActivity(intent);*/
            }

            if (buttonsDialog != null) {
                buttonsDialog.dismiss();
                buttonsDialog = null;
            }
        } else {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.survey_list_date_label:
                    DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(
                            this, Calendar.getInstance().getTime(), null);
                    datePickerFragment.show(
                            getChildFragmentManager(), getString(R.string.tag_date_picker));
                    break;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        cal.set(year, month, day);
        dateLabel.setText(DateUtil.formatDayDate(cal.getTime()));

        Loader<Cursor> cursorLoader = getLoaderManager().getLoader(0);
        if (cursorLoader != null && !cursorLoader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }

        if (datePicker != null && dateFragment != null) {
            Date selectedDate = cal.getTime();

            dateFragment.clearSelectedDates();
            dateFragment.setSelectedDates(selectedDate, selectedDate);
            dateFragment.moveToDate(selectedDate);
            dateFragment.refreshView();
        }
    }

    public void addSurvey(Agenda agenda) {
        SurveyTable surveyTable = new SurveyTable(context);
        Survey survey = surveyTable.getByAgendaId(agenda.getId());

        if (survey == null) {
            Intent intent = new Intent(context, SurveyActivity.class);
            survey = new Survey();
            //StoreTable storeTable = new StoreTable(context);
            //Store store = storeTable.getById((int)agenda.getStoreDbId());
            survey.setAgendaDbId((int) agenda.getId());
            survey.setStoreName(agenda.getStoreName());
            intent.putExtra(Constants.EXTRA_SURVEY, (Parcelable) survey);
            startActivity(intent);
        } else {
            Intent intent = new Intent(context, SurveyDetailActivity.class);
            intent.putExtra(Constants.EXTRA_SURVEY, (Parcelable) survey);
            startActivity(intent);
        }
    }

    private void storeDetail(long storeId) {
        Store store = storeTable.getById((int) storeId);
        if (store != null) {
            Intent intent = new Intent(context, StoreDetailActivity.class);
            //intent.putExtra(Constants.EXTRA_STORE, (Parcelable) store);
            intent.putExtra(Constants.EXTRA_STORE_DB_ID, store.getId());
            startActivity(intent);
        } else {
            Toast.makeText(context, R.string.error_invalid_store, Toast.LENGTH_LONG).show();
        }
    }

    private void initCalendar() {
        dateFragment = new SisiDateFragment();

        ArrayList<String> selectedDates = new ArrayList<>();
        selectedDates.add(DateUtil.format(cal.getTime(), "yyyy-MM-dd"));

        HashMap<String, Object> extraData = new HashMap<>();
        extraData.put(Constants.EXTRA_AGENDA_DATES, agendaDates);

        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, false);
        args.putStringArrayList(CaldroidFragment.SELECTED_DATES, selectedDates);

        dateFragment.setArguments(args);
        dateFragment.setExtraData(extraData);
        dateFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                dateFragment.clearSelectedDates();
                dateFragment.setSelectedDates(date, date);
                dateFragment.moveToDate(date);
                dateFragment.refreshView();

                Calendar c = Calendar.getInstance();
                c.setTime(date);
                SurveyAgendaFragment.this.onDateSet(
                        null, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            }

            @Override
            public void onCaldroidViewCreated() {
                Resources r = context.getResources();
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        r.getDimension(R.dimen.content_horizontal_margin_smaller_even),
                        r.getDisplayMetrics()
                );

                TextView monthTitleText = dateFragment.getMonthTitleTextView();
                if (monthTitleText != null) {
                    monthTitleText.setAllCaps(false);
                    monthTitleText.setPadding(px, px, px, px);
                    monthTitleText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                }

                Button rightButton = dateFragment.getRightArrowButton();
                if (rightButton != null) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                            rightButton.getLayoutParams();

                    layoutParams.height = 80;
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.setMargins(px, 0, px, px);

                    rightButton.setLayoutParams(layoutParams);
                    rightButton.setText(context.getString(R.string.label_today));
                    rightButton.setTextAppearance(context, R.style.TextSecondary_Small);
                    rightButton.setBackgroundResource(R.drawable.button_outline_grey);
                    rightButton.setVisibility(View.VISIBLE);
                    rightButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSelectDate(today, null);
                        }
                    });
                }

                Button leftButton = dateFragment.getLeftArrowButton();
                if (leftButton != null) {
                    leftButton.setVisibility(View.GONE);
                }
            }
        });

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.add(R.id.agenda_calendar, dateFragment);

        if (isVisible()) {
            t.commit();
        }
    }
}
