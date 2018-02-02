package com.sinergiinformatika.sisicrm.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.OrderDetailActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.SurveyDetailActivity;
import com.sinergiinformatika.sisicrm.adapters.HistoryOrderAdapter;
import com.sinergiinformatika.sisicrm.adapters.HistorySurveyAdapter;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.Network;
import com.sinergiinformatika.sisicrm.utils.TabFactory;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class HistoryFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private Context context;
    private View viewFragment;
    private TabHost mTabHost;
    private SearchView mSearchView;
    private User currentUser;
    private String mQueryString;
    private Toast mToast = null;
    private HistorySurveyFragment historySurveyFragment;
    private HistoryOrderFragment historyOrderFragment;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setTitle(getString(R.string.icon_history));
        context = activity;
        currentUser = User.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_history, container, false);

        mSearchView = (SearchView) viewFragment.findViewById(R.id.row_history_search);
        mSearchView.setOnQueryTextListener(this);

        mTabHost = (TabHost) viewFragment.findViewById(R.id.tabHost2);
        mTabHost.setup();

        TabHost.TabSpec tabOrder = mTabHost.newTabSpec(getString(R.string.tag_order));
        tabOrder.setIndicator(TabFactory.createTabIndicator(context, getString(R.string.title_fragment_order)));
        tabOrder.setContent(R.id.tab_order);

        TabHost.TabSpec tabSurvey = mTabHost.newTabSpec(getString(R.string.tag_survey_list));
        tabSurvey.setIndicator(TabFactory.createTabIndicator(context, getString(R.string.label_survey)));
        tabSurvey.setContent(R.id.tab_survey);

        mTabHost.addTab(tabSurvey);
        mTabHost.addTab(tabOrder);

        historySurveyFragment  = HistorySurveyFragment.newInstance();
        historyOrderFragment = HistoryOrderFragment.newInstance();

        if(savedInstanceState == null){
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.tab_survey, historySurveyFragment);
            ft.add(R.id.tab_order, historyOrderFragment);
            ft.commit();
        }

        return viewFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSearchView != null) {
            mSearchView.clearFocus();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Constants.DEBUG) {
            Log.d(TAG, "onQueryTextSubmit, query = " + query);
        }

        historySurveyFragment.search(query);
        historyOrderFragment.search(query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        if (Constants.DEBUG) {
            Log.d(TAG, "onQueryTextChange, query = " + query);
        }

        historySurveyFragment.search(query);
        historyOrderFragment.search(query);

        return false;
    }

    private void showToast(String message) {
        if (mToast == null || !mToast.getView().isShown()) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

}
