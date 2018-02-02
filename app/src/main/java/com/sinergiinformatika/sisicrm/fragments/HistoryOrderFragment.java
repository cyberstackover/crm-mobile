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
import com.sinergiinformatika.sisicrm.OrderDetailActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.OrderCursorAdapter;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.Order;
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

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryOrderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = HistoryOrderFragment.class.getSimpleName();

    private User currentUser;
    private Context context;
    private OrderCursorAdapter mCursorAdapter;
    private ListView mListView;
    private View mBlankTextHolder;
    private int mCurrentOffset = 0;
    private int mNextOffset = 0;
    private boolean mRefresh = false;
    private boolean mSynchronizing = false;
    private int onScrollCounter = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OrderTable mOrderTable;
    private JsonHttpResponseHandler orderListHandler;
    private boolean isSwipeRefresh = false;
    private String mQueryString = null;

    public HistoryOrderFragment() {
        // Required empty public constructor
    }

    public static HistoryOrderFragment newInstance() {
        return new HistoryOrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_history_order, container, false);

        mListView = (ListView) viewFragment.findViewById(R.id.history_order_list);
        mBlankTextHolder = viewFragment.findViewById(R.id.history_order_blank_text_holder);
        Button mBtnReload = (Button) viewFragment.findViewById(R.id.btn_order_reload);
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) viewFragment.findViewById(R.id.history_order_swipe_refresh);

        mCursorAdapter = new OrderCursorAdapter(context, R.layout.row_history, null,
                                                OrderTable.ALL_COLUMNS, new int[]{}, 0);

        mListView.setAdapter(mCursorAdapter);
        mListView.setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwipeRefresh = true;
                reloadOrder();
            }
        });

        mBtnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeRefreshLayout.setRefreshing(true);
                reloadOrder();
            }
        });

        //mLoadingView = LayoutInflater.from(context).inflate(R.layout.loading, mListView, false);
        //mListView.addFooterView(mLoadingView);
        //mListView.setAdapter(mHistoryOrderAdapter);
        //mListView.removeFooterView(mLoadingView);

        return viewFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mOrderTable = new OrderTable(this.context.getContentResolver());
        currentUser = User.getInstance(this.context);

        orderListHandler = new RestResponseHandler(context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    if (Constants.DEBUG) {
                        Log.d(TAG, "order response = " + response.toString());
                    }

                    if (mRefresh) {
                        mRefresh = false;
                    }

                    if (isSuccess(response)) {
                        JSONObject dataObj = getData(response);
                        JSONArray data = dataObj.isNull("list") ? new JSONArray("") :
                                dataObj.getJSONArray("list");

//                    List<Order> orders = new ArrayList<>();
                        OrderTable orderTable = new OrderTable(
                                HistoryOrderFragment.this.context.getContentResolver());

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject datum = data.getJSONObject(i);
                            Order o = new Order();
                            Parser.setValues(datum, o);
                            if (!mOrderTable.isExists(o.getOrderId())) {
                                o.setSyncStatus(Constants.SYNC_STATUS_SENT);
                            /*ContentValues values = new ContentValues();
                            OrderTable.setValues(o, values);
                            context.getContentResolver().insert(CRMContentProvider.URI_ORDERS,
                            values);*/
                                orderTable.save(o);
                            }
                        }

//                    if (orders.size() > 0) {
//                        mNextOffset = dataObj.getInt("next_offset");
//                    }
                        mNextOffset += data.length();
                    } else {
                        if (isVisible()) {
                            String errMsg = ServerErrorUtil.getErrorMessage(
                                    HistoryOrderFragment.this.context, getErrorCode(response));
                            Toast.makeText(
                                    HistoryOrderFragment.this.context, errMsg, Toast.LENGTH_LONG)
                                 .show();
                            if (getErrorCode(response) == Constants.ERROR_CODE_SESSION_EXPIRED) {
                                if (User.rePostLogin(HistoryOrderFragment.this.context)) {
                                    reloadOrder();
                                } else {
                                    if (HistoryOrderFragment.this.context instanceof MainActivity) {
                                        MainActivity mainActivity =
                                                (MainActivity) HistoryOrderFragment.this.context;
                                        mainActivity.attemptLogout(null);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                showOrderBlankText();
                mSynchronizing = false;

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                orderOnFailure();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                orderOnFailure();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONArray errorResponse) {
                orderOnFailure();
            }


            @Override
            public void onStart() {
            /*if(!mSwipeRefreshLayout.isRefreshing()){
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
                }/*else{
                orderListView.removeFooterView(mLoadingView);
            }*/
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        onScrollCounter = 0;
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (mQueryString != null && mQueryString.trim().length() > 0) {
            selection = "lower(" + SurveyTable.COLUMN_STORE_NAME + ") like ? ";
            selectionArgs = new String[]{"%" + mQueryString.toLowerCase() + "%"};
        }

        return new CursorLoader(context, CRMContentProvider.URI_ORDERS,
                                OrderTable.ALL_COLUMNS, selection, selectionArgs,
                                OrderTable.COLUMN_ORDER_DATE + " desc");
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

        Order selectedItem = new Order();
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        OrderTable.setValues(cursor, selectedItem);

        if (Constants.DEBUG) {
            Log.d(TAG, "delivery_date= " + selectedItem.getDeliveryDate());
            Log.d(TAG, "products= " + selectedItem.getPrices().get(0).getProductName());
        }

        if (!selectedItem.isHeader()) {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER, (Parcelable) selectedItem);
            startActivity(intent);
        }
    }

    public void search(String searchText) {
        if (isAdded()) {
            mQueryString = searchText;
            restartLoader();
        }

    }

    private void restartLoader() {
        Loader<Cursor> cursorLoader = getLoaderManager().getLoader(0);
        if (cursorLoader != null && !cursorLoader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    private void reloadOrder() {
        mRefresh = true;
        loadOrder(false);
    }

    private void loadOrder(boolean isNextPage) {

        if (Constants.DEBUG) {
            Log.d(TAG, "loadOrder, isNextPage = " + isNextPage);
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
                        Order s = new Order();
                        OrderTable.setValues(cursor, s);
                        startDate = s.getOrderDateInDate();
                        endDate = DateUtil.now();
                    }
                }

                RestClient.getInstance(context, orderListHandler).
                        getOrderList(currentUser.getToken(), currentUser.getUserId(), startDate,
                                     endDate, mCurrentOffset, null, mQueryString);
            }
        } else {
            showOrderBlankText();
        }
    }

    private void orderOnFailure() {

        if (Constants.DEBUG) {
            Log.d(TAG, "orderOnFailure");
        }

        if (isVisible()) {
            showOrderBlankText();
        }
        mSynchronizing = false;
    }

    private void showOrderBlankText() {
       /* if (mHistoryOrderAdapter.isEmpty()) {
            mBlankTextHolder.setVisibility(View.VISIBLE);
        } else {
            mBlankTextHolder.setVisibility(View.GONE);
        }*/
    }
}
