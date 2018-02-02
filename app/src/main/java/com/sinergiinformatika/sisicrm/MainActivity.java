package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Scroller;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.adapters.SurveyPagerAdapter;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.Parser;
import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.dialogs.AddAgendaDialog;
import com.sinergiinformatika.sisicrm.dialogs.ChangePasswordFragment;
import com.sinergiinformatika.sisicrm.fragments.HistoryFragment;
import com.sinergiinformatika.sisicrm.fragments.ProfileFragment;
import com.sinergiinformatika.sisicrm.fragments.StatisticsFragment;
import com.sinergiinformatika.sisicrm.fragments.StoreListFragment;
import com.sinergiinformatika.sisicrm.fragments.SurveyAgendaFragment;
import com.sinergiinformatika.sisicrm.utils.Calculator;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.GPSTracker;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.PushDataServices;
import com.sinergiinformatika.sisicrm.utils.SyncUtil;
import com.sinergiinformatika.sisicrm.utils.TabFactory;
import com.sinergiinformatika.sisicrm.utils.TypeFaceModifier;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends FragmentActivity
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //    private static final int TAB_POSITION_STORE_LIST = 1;
    //List<Store> stores;
    TabHost tabHost;
    ViewPager viewPager;
    SurveyPagerAdapter pagerAdapter;
    private boolean syncInProgress = false;
    private boolean pushInProgress = false;
    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.DEBUG) Log.i(TAG, "sync progress update");
            int progress = intent.getIntExtra(
                    Constants.EXTRA_SYNC_PROGRESS, Constants.SYNC_PROGRESS_FINISHED);

            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("syncInProgress", progress == Constants.SYNC_PROGRESS_STARTED);
            editor.commit();
            setSyncInProgress(progress == Constants.SYNC_PROGRESS_STARTED);
        }
    };
    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.DEBUG) Log.i(TAG, "push progress update");
            int progress = intent.getIntExtra(
                    Constants.EXTRA_SYNC_PROGRESS, Constants.SYNC_PROGRESS_FINISHED);

            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("pushInProgress", progress == Constants.SYNC_PROGRESS_STARTED);
            editor.commit();
            setPushInProgress(progress == Constants.SYNC_PROGRESS_STARTED);
        }
    };

    //    Map<String, Integer> map = null;
    private User currentUser;
    private SurveyTable surveyTable;
    private StoreTable storeTable;
    //    private AgendaTable mAgendaTable;
    private GPSTracker gpsTracker;
    private StoreListFragment storeListFragment;
//    private Map<Integer, Survey> pendingSurveyList = new HashMap<>();
//    private Map<Integer, Store> pendingStoreList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(this, Constants.DEFAULT_LOCALE);
        TypeFaceModifier.overrideWithFontAwesome(getApplicationContext(), "SERIF");
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        gpsTracker = new GPSTracker(this);
//        mAgendaTable = new AgendaTable(this);
        surveyTable = new SurveyTable(this);
        storeTable = new StoreTable(this);
        currentUser = User.getInstance(this);

        initTab();
        initViewPager();

        // Create account, if needed
        SyncUtil.createSyncAccount(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(
                syncReceiver, new IntentFilter(Constants.INTENT_ACTION_SYNC));
        getApplicationContext().registerReceiver(
                pushReceiver, new IntentFilter(Constants.INTENT_ACTION_PUSH));

        if (gpsTracker != null) {
            gpsTracker.getLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(syncReceiver);
        getApplicationContext().unregisterReceiver(pushReceiver);

        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }
    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    public boolean isPushInProgress() {
        return pushInProgress;
    }

    public void setSyncInProgress(boolean syncInProgress) {
        this.syncInProgress = syncInProgress;

        if (pagerAdapter.getItem(viewPager.getCurrentItem()) instanceof ProfileFragment) {
            ((ProfileFragment) pagerAdapter.getItem(viewPager.getCurrentItem()))
                    .setSyncInProgress(syncInProgress);
        }
    }

    public void setPushInProgress(boolean pushInProgress) {
        this.pushInProgress = pushInProgress;

        if (pagerAdapter.getItem(viewPager.getCurrentItem()) instanceof ProfileFragment) {
            ((ProfileFragment) pagerAdapter.getItem(viewPager.getCurrentItem()))
                    .setPushInProgress(pushInProgress);
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.container);
        List<Fragment> fragments = new ArrayList<>();
        storeListFragment = new StoreListFragment();

        fragments.add(storeListFragment);
        fragments.add(new HistoryFragment());

        String role = User.getInstance(this).getRoleName();
        if (role.equalsIgnoreCase(Constants.ROLE_NAME_AM)
            || role.equalsIgnoreCase(Constants.ROLE_NAME_SALES)) {
            fragments.add(0, new SurveyAgendaFragment());
            fragments.add(new StatisticsFragment());
        }

        fragments.add(new ProfileFragment());

        if (Constants.DEBUG) Log.i(TAG, "fragment count: " + fragments.size());

        pagerAdapter = new SurveyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initTab() {
        tabHost = (TabHost) findViewById(R.id.tabNavHost);
        tabHost.setup();

        String role = User.getInstance(this).getRoleName();
        TabHost.TabSpec surveyAgendaTab = null;
        if (role.equalsIgnoreCase(Constants.ROLE_NAME_AM)
            || role.equalsIgnoreCase(Constants.ROLE_NAME_SALES)) {
            surveyAgendaTab =
                    tabHost.newTabSpec(getString(R.string.tag_survey_list));
            surveyAgendaTab.setContent(new TabFactory(this));
            surveyAgendaTab.setIndicator(
                    TabFactory.createTabNavIndicator(this, getString(R.string.icon_list)));
        }

        TabHost.TabSpec storeListTab = tabHost.newTabSpec(getString(R.string.tag_store_list));
        storeListTab.setContent(new TabFactory(this));
        storeListTab.setIndicator(
                TabFactory.createTabNavIndicator(this, getString(R.string.icon_briefcase)));
        TabHost.TabSpec surveyHistoryTab =
                tabHost.newTabSpec(getString(R.string.tag_survey_history));
        surveyHistoryTab.setContent(new TabFactory(this));
        surveyHistoryTab.setIndicator(
                TabFactory.createTabNavIndicator(this, getString(R.string.icon_history)));

        TabHost.TabSpec statisticsTab = null;
        if (role.equalsIgnoreCase(Constants.ROLE_NAME_AM)
            || role.equalsIgnoreCase(Constants.ROLE_NAME_SALES)) {
            statisticsTab = tabHost.newTabSpec(getString(R.string.tag_statistics));
            statisticsTab.setContent(new TabFactory(this));
            statisticsTab.setIndicator(
                    TabFactory.createTabNavIndicator(this, getString(R.string.icon_bar_chart)));
        }

        TabHost.TabSpec profileTab = tabHost.newTabSpec(getString(R.string.tag_profile));
        profileTab.setContent(new TabFactory(this));
        profileTab.setIndicator(
                TabFactory.createTabNavIndicator(this, getString(R.string.icon_user)));

        if (surveyAgendaTab != null) {
            tabHost.addTab(surveyAgendaTab);
        }

        tabHost.addTab(storeListTab);
        tabHost.addTab(surveyHistoryTab);

        if (statisticsTab != null) {
            tabHost.addTab(statisticsTab);
        }

        tabHost.addTab(profileTab);
        tabHost.setOnTabChangedListener(this);
    }

    public void attemptLogout(View view) {
        List<Store> pendingStores = storeTable.getPendingStores();
        List<Survey> pendingSurveys = surveyTable.getPendingSurveyList();
        List<Order> pendingOrders = (new OrderTable(getContentResolver())).getPendingOrders();
        final boolean[] ok = new boolean[]{false};

        if (pendingStores.isEmpty() && pendingSurveys.isEmpty() && pendingOrders.isEmpty()) {
            ok[0] = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_logout_pending_exist)
                   .setPositiveButton(R.string.label_confirm_positive,
                                      new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              ok[0] = true;
                                              dialog.dismiss();
                                              logout();
                                          }
                                      })
                   .setNegativeButton(R.string.label_confirm_negative,
                                      new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              ok[0] = false;
                                              dialog.cancel();
                                              dialog.dismiss();
                                          }
                                      }).show();
        }

        if (ok[0]) {
            logout();
        }
    }

    private void logout() {
        RestClient.cancelRequests(this);
        SyncUtil.stopSync();
        User.getInstance(this).signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    public void triggerSync(View view) {
        if(isSyncInProgress() || isPushInProgress()){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(StoreTable.COLUMN_SYNC_STATUS, Constants.SYNC_STATUS_PENDING);
        StoreTable storeTable = new StoreTable(getContentResolver());
        storeTable.update(values, StoreTable.COLUMN_SYNC_STATUS + " = ?",
                          new String[]{Constants.SYNC_STATUS_FAILED});

        values.clear();
        values.put(OrderTable.COLUMN_SYNC_STATUS, Constants.SYNC_STATUS_PENDING);
        SurveyTable surveyTable = new SurveyTable(getContentResolver());
        surveyTable.update(values, SurveyTable.COLUMN_SYNC_STATUS + " = ?",
                           new String[]{Constants.SYNC_STATUS_FAILED});

        values.clear();
        values.put(OrderTable.COLUMN_SYNC_STATUS, Constants.SYNC_STATUS_PENDING);
        OrderTable orderTable = new OrderTable(getContentResolver());
        orderTable.update(values, OrderTable.COLUMN_SYNC_STATUS + " = ?",
                          new String[]{Constants.SYNC_STATUS_FAILED});

        SyncUtil.triggerRefresh();
    }

    public void addStore(View view) {
        Intent intent = new Intent(this, StoreActivity.class);
        startActivityForResult(intent, Constants.REQ_CODE_ADD_STORE);
        //startActivity(intent);
        //finish();
    }

    public void addAgenda(View view) {
        List<Store> stores = storeTable.getAll();
        Store store = (Store) view.getTag(R.string.tag_store);
        Fragment fragment = pagerAdapter.getItem(viewPager.getCurrentItem());
        AddAgendaDialog dialog = AddAgendaDialog.newInstance(
                stores, store, DateUtil.now(), 0, fragment);
        dialog.show(getSupportFragmentManager(), getString(R.string.tag_add_agenda));
    }

    public void addOrder(View view) {
        Store store = (Store) view.getTag(R.string.tag_store);
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(Constants.EXTRA_STORE, (Parcelable) store);
        startActivity(intent);
    }

    public void toggleStoreCheck(View view) {

        String action = (String) view.getTag(R.string.tag_action);
        long id = (long) view.getTag(R.string.tag_object_id);
        Calendar cal = Calendar.getInstance();
        Uri uri = Uri.parse(CRMContentProvider.URI_AGENDA + "/" + id);
        Cursor cursor =
                getContentResolver().query(uri, AgendaTable.COMPLETE_COLUMN, null, null, null);

        if (cursor != null) {
            List<Agenda> agendas = Parser.cursorToJoinedAgendaList(cursor);
            if (agendas.size() > 0) {
                Agenda agenda = agendas.get(0);
                String now = DateUtil.formatDBDateTime(cal.getTime());

                double storeLong = agenda.getStoreLong();
                double storeLat = agenda.getStoreLat();
                double currentLong = 0.0;
                double currentLat = 0.0;
                if (gpsTracker.getLocation() != null) {
                    currentLong = gpsTracker.getLongitude();
                    currentLat = gpsTracker.getLatitude();
                }

                if (currentLong == 0.0 || currentLat == 0.0) {
                    Toast.makeText(this, R.string.error_invalid_location, Toast.LENGTH_LONG).show();
                    return;
                }

                String colDateTime, colLong, colLat;
                if (action.equals(Constants.ACTION_CHECK_OUT)) {
                    ContentValues values = new ContentValues();
                    colDateTime = AgendaTable.COLUMN_CHECK_OUT;
                    colLong = AgendaTable.COLUMN_CHECK_OUT_LONG;
                    colLat = AgendaTable.COLUMN_CHECK_OUT_LAT;

                    values.put(colDateTime, now);
                    values.put(colLong, currentLong);
                    values.put(colLat, currentLat);

                    int affected = getContentResolver().update(uri, values, null, null);
                    if (affected < 1) {
                        Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_LONG)
                             .show();
                    } else if (affected > 1) {
                        Log.wtf(TAG, "more than one row updated");
                    }

                    try {
                        Survey survey = surveyTable.getById(agenda.getSurveyDbId());
                        if (survey != null) {
                            survey.setStoreId(agenda.getStoreId());
                            survey.setCheckInNfc(false);
                            survey.setPlanDate(agenda.getDate());
                            survey.setCheckInTime(agenda.getCheckInDateTime());
                            survey.setCheckInLongitude(agenda.getCheckInLong());
                            survey.setCheckInlatitude(agenda.getCheckInLat());
                            survey.setCheckOut(now);
                            survey.setCheckOutLongitude(currentLong);
                            survey.setCheckOutLatitude(currentLat);

                            surveyTable.updateById(survey.getId(), survey);
                                /*String token = User.getInstance(MainActivity.this).getToken();
                                Map<Integer, Survey> surveyList = new HashMap<Integer, Survey>();
                                surveyList.put(Integer.valueOf(survey.getId()), survey);
                                RestClient.getInstance(MainActivity.this,
                                                       new SubmitPendingSurveyResponseHandler(
                                                               MainActivity.this, surveyList,
                                                               survey.getId()))
                                          .postSurvey(token, survey);*/
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                } else {
                    Double distance;
                    if (storeLat != 0 && storeLong != 0) {
                        distance =
                                Calculator.distance(storeLat, storeLong, currentLat, currentLong);
                    } else {
                        distance = 0.0;
                    }

                    if (distance != null && distance <= currentUser.getMaxCheckInDistance()) {
                        ContentValues values = new ContentValues();

                        colDateTime = AgendaTable.COLUMN_CHECK_IN;
                        colLong = AgendaTable.COLUMN_CHECK_IN_LONG;
                        colLat = AgendaTable.COLUMN_CHECK_IN_LAT;

                        values.put(colDateTime, now);
                        values.put(colLong, currentLong);
                        values.put(colLat, currentLat);

                        int affected = getContentResolver().update(uri, values, null, null);
                        if (affected < 1) {
                            Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_LONG)
                                 .show();
                        } else if (affected > 1) {
                            Log.wtf(TAG, "more than one row updated");
                        }
                    } else {
                        Toast.makeText(this, R.string.error_distance_from_store, Toast.LENGTH_LONG)
                             .show();
                    }
                }
            }
        }
    }

    public void goToStoreList(View view) {
        tabHost.setCurrentTab(1);
    }

    @Override
    public void onTabChanged(String s) {
        int position = tabHost.getCurrentTab();
        viewPager.setCurrentItem(position);

        //diubah menggunakan sync adapter
        /*if (Network.isConnected(this)) {
            submitPendingSurvey();
            submitPendingStore();
        }*/

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == Constants.REQ_CODE_ADD_STORE && resultCode == Activity.RESULT_OK) {
            storeListFragment.populateStores();
            storeListFragment.rebuildNewStoreAdapter();
        } else if (requestCode == Constants.REQ_CODE_STORE_DETAIL) {
            Log.d(TAG, "store detail here");
            storeListFragment.populateStores();
            storeListFragment.rebuildAdapter();
        }*/
    }

    public void changePassword(View view) {
        ChangePasswordFragment passwordFragment = new ChangePasswordFragment();
        passwordFragment.show(getSupportFragmentManager(), getString(R.string.tag_change_password));
    }

    public void reUploadStore(final View view) {
        final Store store = (Store) view.getTag(R.string.tag_store);
        final View progressBar = ((View) view.getParent()).findViewById(R.id.row_store_progress);
        final TextView dataStatus = (TextView) ((View) view.getParent()).findViewById(
                R.id.row_store_data_status);

        RestClient.getInstance(this, new RestResponseHandler(true, TAG, this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (Constants.DEBUG) Log.d(TAG, "store response: " + response.toString(4));
                    if (isSuccess(response)) {
                        JSONObject data = getData(response);
                        String storeId = data.getString("store_id");
                        storeTable.sent(store.getId(), storeId);

                        if (dataStatus != null) {
                            dataStatus.setText(R.string.icon_upload);
                            dataStatus.setTextColor(
                                    MainActivity.this.getResources()
                                                     .getColor(R.color.icon_status_success));
                        }
                    } else {
                        if (getErrorCode(response) == Constants.ERROR_CODE_SESSION_EXPIRED) {
                            User.rePostLogin(MainActivity.this, false);
                            storeTable.pending(store.getId());
                            Toast.makeText(MainActivity.this, R.string.error_store_upload +
                                                              ". Silakan coba sesaat lagi.",
                                           Toast.LENGTH_LONG)
                                 .show();
                            view.setVisibility(View.VISIBLE);

                            if (dataStatus != null) {
                                dataStatus.setText(R.string.icon_upload);
                                dataStatus.setTextColor(
                                        MainActivity.this.getResources()
                                                         .getColor(R.color.icon_status_neutral));
                            }
                        } else {
                            if (Constants.DEBUG) Log.e(TAG, response.toString());
                            throw new Exception("Store upload failed");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);

                    view.setVisibility(View.VISIBLE);
                    storeTable.failed(store.getId());
                    Toast.makeText(MainActivity.this, R.string.error_store_upload,
                                   Toast.LENGTH_SHORT)
                         .show();

                    if (dataStatus != null) {
                        dataStatus.setText(R.string.icon_warning);
                        dataStatus.setTextColor(
                                MainActivity.this.getResources()
                                                 .getColor(R.color.icon_status_failed));
                    }
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                storeTable.sending(store.getId());
                view.setEnabled(false);
                view.setVisibility(View.GONE);

                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                if (dataStatus != null) {
                    dataStatus.setText(R.string.icon_wifi);
                    dataStatus.setTextColor(
                            MainActivity.this.getResources().getColor(R.color.icon_status_neutral));
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                view.setEnabled(true);

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }).postStore(User.getInstance(this).getToken(), store);
    }

    public void showSurveyPopup(View view) {
        final Survey survey = (Survey) view.getTag(R.string.tag_survey);
        final String storeId = (String) view.getTag(R.string.tag_store);
        final Long storeDbId = (Long) view.getTag(R.string.tag_store_db_id);
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_history_survey, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;

                switch (item.getItemId()) {
                    case R.id.action_detail_store:
                        intent = new Intent(MainActivity.this, StoreDetailActivity.class);

                        if(storeDbId != null){
                            intent.putExtra(Constants.EXTRA_STORE_DB_ID, storeDbId.intValue());
                        }else{
                            intent.putExtra(Constants.EXTRA_STORE_ID, storeId);
                        }
                        break;
                    case R.id.action_detail_survey:
                        intent = new Intent(MainActivity.this, SurveyDetailActivity.class);
                        intent.putExtra(Constants.EXTRA_SURVEY, (Parcelable) survey);
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                }

                return true;
            }
        });
        popupMenu.show();
    }

    public void reUploadSurvey(final View view) {
        final View progress = ((View) view.getParent()).findViewById(R.id.history_survey_progress);
        final Survey survey = (Survey) view.getTag(R.string.tag_survey);

        final JsonHttpResponseHandler photoHandler = new RestResponseHandler(true, TAG, this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (isSuccess(response)) {
                        surveyTable.imageSent(survey.getId());
                    } else {
                        onFailure(getString(R.string.error_upload_survey_photo),
                                  "response: " + response.toString(),
                                  new Exception("Response error"));
                    }
                } catch (JSONException e) {
                    if (Constants.DEBUG) Log.e(super.tag, e.getMessage(), e);
                    onFailure(getString(R.string.error_upload_survey_photo),
                              "response: " + response.toString(),
                              new Exception("Response error"));
                }
            }

            @Override
            public void onFailure(String message, String log, Throwable throwable) {
                super.onFailure(message, log, throwable);
                surveyTable.imageFailed(survey.getId());
            }

            @Override
            public void onStart() {
                surveyTable.imageSending(survey.getId());
                view.setVisibility(View.GONE);
                if (progress != null) {
                    progress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
            }
        };

        JsonHttpResponseHandler surveyHandler = new RestResponseHandler(true, TAG, this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (isSuccess(response)) {
                        String surveyId = getData(response).getString("survey_id");
                        surveyTable.sent(survey.getId(), surveyId);
                        surveyTable.dataSent(survey.getId(), surveyId);

                        if (survey.getStatusImage().equals(Constants.SYNC_STATUS_PENDING) ||
                            survey.getStatusImage().equals(Constants.SYNC_STATUS_FAILED)) {
                            RestClient.getInstance(MainActivity.this, photoHandler)
                                      .postSurveyPhotos(
                                              User.getInstance(MainActivity.this).getToken(),
                                              survey);
                        }
                    } else {
                        onFailure(getString(R.string.error_upload_survey),
                                  "response: " + response.toString(),
                                  new Exception("Response error"));
                    }
                } catch (JSONException e) {
                    if (Constants.DEBUG) Log.e(super.tag, e.getMessage(), e);
                    onFailure(getString(R.string.error_upload_survey),
                              "response: " + response.toString(),
                              new Exception("Error parsing JSON response"));
                }
            }

            @Override
            public void onFailure(String message, String log, Throwable throwable) {
                super.onFailure(message, log, throwable);
                surveyTable.failed(survey.getId());
                surveyTable.dataFailed(survey.getId());
            }

            @Override
            public void onStart() {
                surveyTable.sending(survey.getId());
                surveyTable.dataSending(survey.getId());
                view.setVisibility(View.GONE);
                if (progress != null) {
                    progress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
            }
        };

        if (!TextUtils.isEmpty(survey.getCheckOut())) {
            if (survey.getStatusData().equals(Constants.SYNC_STATUS_PENDING) ||
                survey.getStatusData().equals(Constants.SYNC_STATUS_FAILED)) {
                RestClient.getInstance(this, surveyHandler)
                          .postSurvey(User.getInstance(this).getToken(), survey);
            } else if (survey.getStatusImage().equals(Constants.SYNC_STATUS_PENDING) ||
                       survey.getStatusImage().equals(Constants.SYNC_STATUS_FAILED)) {
                RestClient.getInstance(this, photoHandler)
                          .postSurveyPhotos(User.getInstance(this).getToken(), survey);
            }
        } else {
            Toast.makeText(this, R.string.error_not_checked_out, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Procedure Push Data to Server
     * @param view
     */
    public void triggerPushData(View view) {

        if(isSyncInProgress() || isPushInProgress()){
            return;
        }

        Handler mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                String info = (String) msg.obj;
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exception Info")
                        .setMessage(info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                //textView.setMaxLines(5);
                textView.setScroller(new Scroller(MainActivity.this));
                textView.setVerticalScrollBarEnabled(true);
                textView.setMovementMethod(new ScrollingMovementMethod());
                textView.setTextIsSelectable(true);
                textView.setEnabled(false);
                textView.setEnabled(true);
            }
        };

        Thread thread = new Thread(new PushDataServices(getApplicationContext(), mHandler));
        thread.start();

    }
}
