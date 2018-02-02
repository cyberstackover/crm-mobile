package com.sinergiinformatika.sisicrm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.MainActivity;
import com.sinergiinformatika.sisicrm.OrderActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.StoreDetailActivity;
import com.sinergiinformatika.sisicrm.adapters.StoreListAdapter;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.ItemHeader;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.dialogs.ActionButtonsDialog;
import com.sinergiinformatika.sisicrm.dialogs.AddAgendaDialog;
import com.sinergiinformatika.sisicrm.utils.CollectionUtil;
import com.sinergiinformatika.sisicrm.utils.GPSTracker;
import com.sinergiinformatika.sisicrm.utils.TabFactory;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
TODO: perlu perbaikan code
1. Terkait variable currentLatitude dan currentLongitude
2. method rebuildAdapter

 */
public class StoreListFragment extends Fragment
        implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener,
                   View.OnClickListener, TabHost.OnTabChangeListener,
                   LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = StoreListFragment.class.getSimpleName();
    public List<Store> mStores = null;
    public SearchView mSearchView;
    private Context context;
    private TabHost tabHost;
    private User currentUser;
    private View viewFragment;
    private ListView priorityListView;
    private ListView distanceListView;
    private ListView lastCheckInListView;
    private ListView newStoreListView;
    private StoreListAdapter priorityAdapter;
    private StoreListAdapter distanceAdapter;
    private StoreListAdapter lastCheckInAdapter;
    private StoreListAdapter newStoreAdapter;
    private StoreTable storeTable;
    private ActionButtonsDialog buttonsDialog;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private Store mSelectedItem;
    private GPSTracker mGpsTracker;
    private boolean mProcessingStore = false;
    private View mLoadingHolderView, mStoreListHolder, mNewStoreListHolder;
    private TextView mStoreListBlankText;
    private TextView mNewStoreListBlankText;
    private HorizontalScrollView storeListTabScroll;
    private SimpleCursorAdapter mCursorAdapter;
    private SwipeRefreshLayout pRefreshLayout;
    private SwipeRefreshLayout dRefreshLayout;
    private SwipeRefreshLayout lRefreshLayout;
    private SwipeRefreshLayout nRefreshLayout;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.DEBUG) Log.d(TAG, "store uploaded");
            populateStores();
            rebuildNewStoreAdapter();
        }
    };

    public StoreListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        viewFragment = inflater.inflate(R.layout.fragment_store_list, container, false);

        mStoreListBlankText = (TextView) viewFragment.findViewById(R.id.store_list_blank_text);
        mStoreListHolder = viewFragment.findViewById(R.id.store_list_holder);
        mLoadingHolderView = viewFragment.findViewById(R.id.loading_holder);

        mNewStoreListBlankText =
                (TextView) viewFragment.findViewById(R.id.new_store_list_blank_text);
        mNewStoreListHolder = viewFragment.findViewById(R.id.newstore_holder);

        mSearchView = (SearchView) viewFragment.findViewById(R.id.store_list_search);
        mSearchView.setOnQueryTextListener(this);

        storeListTabScroll =
                (HorizontalScrollView) viewFragment.findViewById(R.id.store_list_tab_scroll);

        tabHost = (TabHost) viewFragment.findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.d(TAG, "onTabChanged, isEmptyStore = " + isEmptyStore());

                if (isEmptyStore()) {
                    showBlankText();
                }/*else if(getString(R.string.tag_newstore).equals(tabId) && isNewStoreListEmpty()){
                    showNewStoreBlankText();
                }*/
            }
        });

        TabHost.TabSpec tabPriority = tabHost.newTabSpec(getString(R.string.tag_priority));
        tabPriority.setIndicator(
                TabFactory.createTabIndicator(context, getString(R.string.title_priority)));
        tabPriority.setContent(R.id.store_list_p_refresh);

        TabHost.TabSpec tabDistance = tabHost.newTabSpec(getString(R.string.tag_distance));
        tabDistance.setIndicator(
                TabFactory.createTabIndicator(context, getString(R.string.title_distance)));
        tabDistance.setContent(R.id.store_list_d_refresh);

        TabHost.TabSpec tabLastCheckIn = tabHost.newTabSpec(getString(R.string.tag_date));
        tabLastCheckIn.setIndicator(
                TabFactory.createTabIndicator(context, getString(R.string.title_date)));
        tabLastCheckIn.setContent(R.id.store_list_l_refresh);

        priorityListView = (ListView) viewFragment.findViewById(R.id.listview_priority);
        setStoreListAdapter(priorityListView, priorityAdapter, new ArrayList<Store>(), false);
        priorityListView.setOnItemClickListener(this);

        TabHost.TabSpec tabNewStore = tabHost.newTabSpec(getString(R.string.tag_newstore));
        tabNewStore.setIndicator(
                TabFactory.createTabIndicator(context, getString(R.string.title_newstore)));
        tabNewStore.setContent(R.id.newstore_holder);

        tabHost.setOnTabChangedListener(this);
        tabHost.addTab(tabDistance);
        tabHost.addTab(tabLastCheckIn);
        tabHost.addTab(tabNewStore);
        tabHost.addTab(tabPriority);

//        populateStores();

        distanceListView = (ListView) viewFragment.findViewById(R.id.listview_distance);
        //setStoreListAdapter(distanceListView, distanceAdapter, new ArrayList<Store>());
        distanceListView.setOnItemClickListener(this);

        lastCheckInListView = (ListView) viewFragment.findViewById(R.id.listview_lastcheckin);
        //setStoreListAdapter(lastCheckInListView, lastCheckInAdapter, new ArrayList<Store>());
        lastCheckInListView.setOnItemClickListener(this);

        newStoreListView = (ListView) viewFragment.findViewById(R.id.listview_newstore);
        //setStoreListAdapter(newStoreListView, newStoreAdapter, new ArrayList<Store>());
        newStoreListView.setOnItemClickListener(this);

        SwipeRefreshLayout.OnRefreshListener swipeRefreshListener =
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (getActivity() != null &&
                            ((MainActivity) getActivity()).isSyncInProgress()) {
                            Toast.makeText(context, R.string.error_sync_in_progress,
                                           Toast.LENGTH_SHORT).show();
                            return;
                        }

                        RestResponseHandler storeListHandler = new RestResponseHandler(context) {
                            @Override
                            public void onStart() {
                                super.onStart();
                                pRefreshLayout.setRefreshing(true);
                                dRefreshLayout.setRefreshing(true);
                                lRefreshLayout.setRefreshing(true);
                                nRefreshLayout.setRefreshing(true);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  JSONObject response) {
                                try {
                                    if (Constants.DEBUG) {
                                        Log.d(TAG, "store response = " + response.toString(4));
                                    }

                                    if (isSuccess(response)) {
                                        JSONObject dataObj = getData(response);
                                        JSONArray data = dataObj.getJSONArray("list");
                                        StoreTable mStoreTable = new StoreTable(context);

                                        for (int i = 0; i < data.length(); i++) {
                                            JSONObject datum = data.getJSONObject(i);
                                            Store s = new Store();
                                            Parser.setValues(datum, s);
                                            s.setSyncStatus(Constants.SYNC_STATUS_SENT);
                                            mStoreTable.syncSave(s);
                                        }

                                        restartLoader();
                                    } else {
                                        throw new Exception(
                                                context.getString(R.string.error_download_store));
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "store response = " + response.toString());
                                    Log.e(TAG, e.getMessage(), e);
                                }

                                pRefreshLayout.setRefreshing(false);
                                dRefreshLayout.setRefreshing(false);
                                lRefreshLayout.setRefreshing(false);
                                nRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  Throwable throwable,
                                                  JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                pRefreshLayout.setRefreshing(false);
                                dRefreshLayout.setRefreshing(false);
                                lRefreshLayout.setRefreshing(false);
                                nRefreshLayout.setRefreshing(false);
                            }
                        };

                        RestClient.getInstance(context, storeListHandler).getStoreList(
                                User.getInstance(context).getToken(), Constants.STORE_TYPE_RESELLER,
                                null, true);
                    }
                };
        pRefreshLayout = (SwipeRefreshLayout) viewFragment.findViewById(R.id.store_list_p_refresh);
        pRefreshLayout.setOnRefreshListener(swipeRefreshListener);
        dRefreshLayout = (SwipeRefreshLayout) viewFragment.findViewById(R.id.store_list_d_refresh);
        dRefreshLayout.setOnRefreshListener(swipeRefreshListener);
        lRefreshLayout = (SwipeRefreshLayout) viewFragment.findViewById(R.id.store_list_l_refresh);
        lRefreshLayout.setOnRefreshListener(swipeRefreshListener);
        nRefreshLayout = (SwipeRefreshLayout) viewFragment.findViewById(R.id.store_list_n_refresh);
        nRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        return viewFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProcessingStore = false;

        LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver, new IntentFilter(Constants.DATA_UPLOADED_INTENT_ACTION));

        setCurrentLocation();
        restartLoader();
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);

        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && getView() != null) {
            rebuildAdapter();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_CODE_STORE_DETAIL) {
            Log.d(TAG, "store detail here");
//            populateStores();
//            rebuildAdapter();
            restartLoader();
        }
    }

    private void setCurrentLocation() {
        if (mGpsTracker == null) {
            mGpsTracker = new GPSTracker(context);
        }

        if (mGpsTracker.getLocation(true) != null) {
            currentLatitude = mGpsTracker.getLatitude();
            currentLongitude = mGpsTracker.getLongitude();

            if (Constants.DEBUG) {
                Log.i(TAG,
                      String.format("long: %.5f | lat: %.5f", currentLongitude, currentLatitude));
            }
        } else {
            Log.e(TAG, "location is null");
        }
    }

    private void setStoreListAdapter(ListView view, StoreListAdapter adapter, List<Store> stores,
                                     boolean flagCheckIn) {

        setCurrentLocation();

        if (adapter == null) {
            if (!flagCheckIn) {
                adapter =
                        new StoreListAdapter(context, 0, stores, currentLatitude, currentLongitude);
            } else {
                adapter =
                        new StoreListAdapter(context, 0, stores, currentLatitude, currentLongitude,
                                             true);
            }
            view.setAdapter(adapter);
        } else {
            adapter.setCurrentLatitude(currentLatitude);
            adapter.setCurrentLongitude(currentLongitude);
            adapter.clear();
            adapter.addAll(stores);
            adapter.notifyDataSetChanged();
            view.invalidate();
        }
    }

    public void rebuildAdapter() {
        rebuildAdapter(mStores);
    }

    public void rebuildNewStoreAdapter() {
        if (!isEmptyStore()) {
            setStoreListAdapter(newStoreListView, newStoreAdapter,
                                groupStores(mStores, GroupBy.NEW_STORE, false), false);
        }

        pRefreshLayout.setRefreshing(false);
        dRefreshLayout.setRefreshing(false);
        lRefreshLayout.setRefreshing(false);
        nRefreshLayout.setRefreshing(false);
    }

    //TODO sementara pake cara ini dulu, next mungkin bisa dicoba pake CursorAdapter
    public void rebuildAdapter(List<Store> stores) {
        if (Constants.DEBUG) {
            Log.d(TAG, "stores is null = " + (stores == null));
            Log.d(TAG, "mProcessingStore = " + mProcessingStore);
        }

        if (!mProcessingStore && stores != null) {
            setStoreListAdapter(priorityListView, priorityAdapter,
                                groupStores(stores, GroupBy.PRIORITY), false);
            setStoreListAdapter(distanceListView, distanceAdapter,
                                groupStores(stores, GroupBy.DISTANCE), false);
            setStoreListAdapter(lastCheckInListView, lastCheckInAdapter,
                                groupStores(stores, GroupBy.LAST_CHECK_IN), true);
            setStoreListAdapter(newStoreListView, newStoreAdapter,
                                groupStores(stores, GroupBy.NEW_STORE, false), false);
        }

        pRefreshLayout.setRefreshing(false);
        dRefreshLayout.setRefreshing(false);
        lRefreshLayout.setRefreshing(false);
        nRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        currentUser = User.getInstance(this.context);
        mGpsTracker = new GPSTracker(this.context);
        storeTable = new StoreTable(this.context);
//        populateStores();
    }

    public void populateStores() {
        if (storeTable == null) {
            storeTable = new StoreTable(context);
        }

        mStores = storeTable.getAll();
    }

    public boolean isEmptyStore() {
        return mStores == null || mStores.size() == 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedItem = (Store) parent.getAdapter().getItem(position);
        Log.d(TAG, "mSelectedItem.getStatus() = " + mSelectedItem.getStatus());
        viewStoreDetail();
    }

    @Override
    public void onClick(View view) {

        String action = (String) view.getTag(R.string.tag_action);

        if (Constants.ACTION_AGENDA.equals(action)) {
            mProcessingStore = true;
            AddAgendaDialog.newInstance(mStores, mSelectedItem, null, 0, StoreListFragment.this)
                           .show(getChildFragmentManager(), "AGENDA_ADD");
        } else if (Constants.ACTION_ORDER.equals(action)) {
            mProcessingStore = true;
            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra(Constants.EXTRA_STORE, (Parcelable) mSelectedItem);
            startActivity(intent);
        } else if (Constants.ACTION_DETAIL.equals(action)) {
            viewStoreDetail();
        }

        if (buttonsDialog != null) {
            buttonsDialog.dismiss();
            buttonsDialog = null;
        }
    }

    private void viewStoreDetail() {
        mProcessingStore = true;
        Intent intent = new Intent(context, StoreDetailActivity.class);
        intent.putExtra(Constants.EXTRA_STORE_DB_ID, mSelectedItem.getId());
        startActivityForResult(intent, Constants.REQ_CODE_STORE_DETAIL);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Constants.DEBUG) {
            Log.d(TAG, "onQueryTextSubmit query = " + query);
        }
        findStores(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (Constants.DEBUG) {
            Log.d(TAG, "onQueryTextChange query = " + query);
        }
        findStores(query);
        return false;
    }

    //TODO search store perlu diperbaiki
    public void findStores(String queryString) {

        if (queryString == null || queryString.trim().length() == 0) {
            populateStores();
        }

        if (isEmptyStore()) {
            return;
        }

        List<Store> filteredStores = new ArrayList<>();

        for (Store s : mStores) {
            String storeName = s.getName().toLowerCase();
            String cityName = s.getCity().toLowerCase();
            if (queryString != null && (storeName.contains(queryString.toLowerCase()) ||
                                        cityName.contains(queryString.toLowerCase()))) {

                try {
                    Store sClone = (Store) s.clone();
                    filteredStores.add(sClone);
                } catch (CloneNotSupportedException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }

        rebuildAdapter(filteredStores);
    }

    public List<Store> groupStores(List<Store> stores, GroupBy groupBy) {
        return groupStores(stores, groupBy, true);
    }

    public List<Store> groupStores(List<Store> stores, GroupBy groupBy, boolean isActive) {

        List<ItemHeader<Store>> itemHeaders = null;
        if (GroupBy.PRIORITY.equals(groupBy)) {
            itemHeaders = CollectionUtil.groupStoreListByPriority(stores);
        } else if (GroupBy.DISTANCE.equals(groupBy)) {
            setCurrentLocation();
            itemHeaders = CollectionUtil
                    .groupStoreListByDistance(stores, currentLatitude, currentLongitude);
        } else if (GroupBy.LAST_CHECK_IN.equals(groupBy)) {
//            itemHeaders = CollectionUtil.groupStoreListByLastCheckIn(stores);
            itemHeaders = CollectionUtil.groupStoreListByCreateDate(context, stores);
        } else if (GroupBy.NEW_STORE.equals(groupBy)) {
            setCurrentLocation();
            itemHeaders =
                    CollectionUtil.populateNewStores(stores, currentLatitude, currentLongitude);
        }

        if (itemHeaders == null) {
            return new ArrayList<>();
        }

        List<Store> groupList = new ArrayList<>();
        for (ItemHeader ih : itemHeaders) {
            int i = 1;
            for (Store s : (List<Store>) ih.getItems()) {
                if (i == 1 && isActive) {
                    s.setHeader(true);
                    s.setHeaderName(ih.getHeaderName());
                }

                boolean add = true;
                if (!isActive && Constants.STORE_STATUS_ACTIVE.equals(s.getStatus())) {
                    add = false;
                }

                if (add) {
                    groupList.add(s);
                }

                i++;
            }
        }

        return groupList;
    }

    @Override
    public void onTabChanged(String s) {
        int pos = tabHost.getCurrentTab();
        View tabView = tabHost.getTabWidget().getChildTabViewAt(pos);
        int scrollPos =
                tabView.getLeft() - (storeListTabScroll.getWidth() - tabView.getWidth()) / 2;
        storeListTabScroll.smoothScrollTo(scrollPos, 0);
    }

    /*public void showLoading() {

        if (mLoadingHolderView != null) {
            mLoadingHolderView.setVisibility(View.VISIBLE);
            hideBlankText();
            hideStoreListHolder();
        }
    }*/

    public void hideLoading() {
        mLoadingHolderView.setVisibility(View.GONE);
    }

    public void showBlankText() {

        if (mStoreListBlankText != null) {
            mStoreListBlankText.setVisibility(View.VISIBLE);
            hideLoading();
            hideStoreListHolder();
        }
    }

    /*public void hideBlankText() {
        mStoreListBlankText.setVisibility(View.GONE);
    }

    public void showStoreListHolder() {

        if (mStoreListHolder != null) {
            mStoreListHolder.setVisibility(View.VISIBLE);
            hideBlankText();
            hideLoading();
        }
    }*/

    public void hideStoreListHolder() {
        mStoreListHolder.setVisibility(View.GONE);
    }

    /*public void showNewStoreBlankText() {
        if (mNewStoreListBlankText != null) {
            mNewStoreListBlankText.setVisibility(View.VISIBLE);
            hideNewStoreListView();
        }
    }

    public void hideNewStoreListView() {
        newStoreListView.setVisibility(View.GONE);
    }

    public void showNewStoreListHView() {

        if (newStoreListView != null) {
            newStoreListView.setVisibility(View.VISIBLE);
            hideNewStoreBlankText();
        }
    }

    public void hideNewStoreBlankText() {
        mNewStoreListBlankText.setVisibility(View.GONE);
    }*/

    private void restartLoader() {
        pRefreshLayout.setRefreshing(true);
        dRefreshLayout.setRefreshing(true);
        lRefreshLayout.setRefreshing(true);
        nRefreshLayout.setRefreshing(true);

        Loader<Cursor> cursorLoader = getLoaderManager().getLoader(0);
        if (cursorLoader != null && !cursorLoader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mCursorAdapter.swapCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, CRMContentProvider.URI_STORES,
                                new String[]{StoreTable.COLUMN_ID, StoreTable.COLUMN_STORE_ID,
                                        StoreTable.COLUMN_SYNC_STATUS},
                                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //mCursorAdapter.swapCursor(cursor);
        ///Toast.makeText(context, "Store DB", Toast.LENGTH_LONG).show();
        populateStores();
        rebuildAdapter();
    }

    public enum GroupBy {
        PRIORITY, DISTANCE, LAST_CHECK_IN, NEW_STORE
    }
}
