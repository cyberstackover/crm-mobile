package com.sinergiinformatika.sisicrm.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.MainActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.SurveyDetailActivity;
import com.sinergiinformatika.sisicrm.adapters.SurveyCursorAdapter;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.Network;
import com.sinergiinformatika.sisicrm.utils.ServerErrorUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistorySurveyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorySurveyFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = HistorySurveyFragment.class.getSimpleName();

    private User currentUser;
    private Context context;
    private SurveyCursorAdapter mCursorAdapter;
    private ListView mListView;
    private View mBlankTextHolder;
    private int mCurrentOffset = 0;
    private int mNextOffset = 0;
    private boolean mRefresh = false;
    private boolean mSynchronizing = false;
    private JsonHttpResponseHandler surveyListHandler;
    //    private int onScrollCounter = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SurveyTable mSurveyTable;
    private boolean isSwipeRefresh = false;
    private String mQueryString = null;

    public HistorySurveyFragment() {
        // Required empty public constructor
    }

    public static HistorySurveyFragment newInstance() {
        return new HistorySurveyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View viewFragment = inflater.inflate(R.layout.fragment_history_survey, container, false);

        mListView = (ListView) viewFragment.findViewById(R.id.history_survey_list);
        mBlankTextHolder = viewFragment.findViewById(R.id.history_survey_blank_text_holder);
        Button mBtnReload = (Button) viewFragment.findViewById(R.id.btn_survey_reload);
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) viewFragment.findViewById(R.id.history_survey_swipe_refresh);

        mCursorAdapter = new SurveyCursorAdapter(context, R.layout.row_history, null,
                                                 SurveyTable.ALL_COLUMNS, new int[]{}, 0);

        mListView.setAdapter(mCursorAdapter);
        mListView.setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwipeRefresh = true;
                reloadSurvey();
            }
        });

        mBtnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeRefreshLayout.setRefreshing(true);
                reloadSurvey();
            }
        });

        //mLoadingView = LayoutInflater.from(context).inflate(R.layout.loading, mListView, false);
        //mListView.addFooterView(mLoadingView);
        //mListView.setAdapter(mHistorySurveyAdapter);
        //mListView.removeFooterView(mLoadingView);

        return viewFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mSurveyTable = new SurveyTable(this.context);
        currentUser = User.getInstance(this.context);

        surveyListHandler = new RestResponseHandler(context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    if (Constants.DEBUG) {
                        Log.d(TAG, "survey response = " + response.toString());
                    }

                    if (mRefresh) {
                        mRefresh = false;
                    }

                    if (isSuccess(response)) {
                        JSONObject dataObj = getData(response);
                        JSONArray data = dataObj.getJSONArray("list");

                        List<Survey> surveyList = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            Survey s = new Survey();
                            Parser.setValues(datum, s);

                            if (!mSurveyTable.isExists(s.getSurveyId())) {
                                mSurveyTable.syncInsert(s);
                            }
                        }

                        if (surveyList.size() > 0) {
                            mNextOffset = dataObj.getInt("next_offset");
                        }
                    } else {
                        if (isVisible()) {
                            String errMsg = ServerErrorUtil.getErrorMessage(
                                    HistorySurveyFragment.this.context, getErrorCode(response));
                            Toast.makeText(
                                    HistorySurveyFragment.this.context, errMsg, Toast.LENGTH_LONG)
                                 .show();
                            if (getErrorCode(response) == Constants.ERROR_CODE_SESSION_EXPIRED) {
                                if (User.rePostLogin(HistorySurveyFragment.this.context)) {
                                    reloadSurvey();
                                } else {
                                    if (HistorySurveyFragment.this.context instanceof
                                            MainActivity) {
                                        MainActivity mainActivity =
                                                (MainActivity) HistorySurveyFragment.this.context;
                                        mainActivity.attemptLogout(null);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                showSurveyBlankText();
                mSynchronizing = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                surveyOnFailure();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONArray errorResponse) {
                surveyOnFailure();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                surveyOnFailure();
            }

            @Override
            public void onStart() {
           /* if(!mSwipeRefreshLayout.isRefreshing()){
                if (mRefresh) {
                    mBlankTextHolder.setVisibility(View.GONE);
                }
                mListView.addFooterView(mLoadingView, null, false);
            }*/
            }

            @Override
            public void onFinish() {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }/* else {
                mListView.removeFooterView(mLoadingView);
            }*/

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
//        onScrollCounter = 0;
        restartLoader();
    }

    private void restartLoader() {
        Loader<Cursor> cursorLoader = getLoaderManager().getLoader(0);
        if (cursorLoader != null && !cursorLoader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = null;
        String[] selectionArgs = null;
        if (mQueryString != null && mQueryString.trim().length() > 0) {
            selection = "lower(" + OrderTable.COLUMN_STORE_NAME + ") like ? ";
            selectionArgs = new String[]{"%" + mQueryString.toLowerCase() + "%"};
        }

        return new CursorLoader(context, CRMContentProvider.URI_SURVEY,
                                SurveyTable.ALL_COLUMNS, selection, selectionArgs,
                                SurveyTable.COLUMN_SURVEY_DATE + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
        if (cursor.getCount() == 0) {
            mBlankTextHolder.setVisibility(View.VISIBLE);
        } else {
            mBlankTextHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Survey selectedItem = new Survey();
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        SurveyTable.setValues(cursor, selectedItem);

        if (!selectedItem.isHeader()) {
            Intent intent = new Intent(context, SurveyDetailActivity.class);
            //Intent intent = new Intent(context, SurveyActivity.class);
            intent.putExtra(Constants.EXTRA_SURVEY, (Parcelable) selectedItem);
            startActivity(intent);
        }
    }

    public void search(String searchText) {
        if (isAdded()) {
            mQueryString = searchText;
            restartLoader();
        }
    }

    private void reloadSurvey() {
        mRefresh = true;
        loadSurvey(false);
    }

    private synchronized void loadSurvey(boolean isNextPage) {

        if (Constants.DEBUG) {
            Log.d(TAG, "loadSurvey, isNextPage = " + isNextPage);
        }

        if (Network.isConnected(context)) {

            if (!mSynchronizing) {

                mSynchronizing = true;

                if (isNextPage) {
                    mRefresh = false;
                    mCurrentOffset = mNextOffset;
                }

                if (mRefresh) {
                    mCurrentOffset = 0;
                    mNextOffset = 0;
                }

                Date startDate = null;
                Date endDate = null;
                if (isSwipeRefresh && mListView.getAdapter().getCount() > 0) {
                    Cursor cursor = (Cursor) mCursorAdapter.getItem(0);
                    if (cursor != null && cursor.getCount() > 0) {
                        Survey s = new Survey();
                        SurveyTable.setValues(cursor, s);
                        startDate = s.getCheckInDate();
                        endDate = DateUtil.now();
                    }
                }

                RestClient.getInstance(context, surveyListHandler)
                          .getSurveyList(currentUser.getToken(), currentUser.getUserId(), startDate,
                                         endDate, mCurrentOffset, null, mQueryString);
            }

        } else {
            showSurveyBlankText();
        }

    }

    private void surveyOnFailure() {
        if (isVisible()) {
            showSurveyBlankText();
        }
        mSynchronizing = false;
    }

    private void showSurveyBlankText() {
        /*if (mHistorySurveyAdapter.isEmpty()) {
            mBlankTextHolder.setVisibility(View.VISIBLE);
        } else {
            mBlankTextHolder.setVisibility(View.GONE);
        }*/
    }
}
