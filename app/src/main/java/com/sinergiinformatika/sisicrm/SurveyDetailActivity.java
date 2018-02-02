package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.fragments.SurveyDetailFragment;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import java.util.Calendar;


public class SurveyDetailActivity extends FragmentActivity {

    private static final String TAG = SurveyDetailActivity.class.getSimpleName();
    private SurveyDetailFragment mFragment;
    private Survey mSurveyEdit;
    private User currentUser;
    private SurveyTable mSurveyTable;
    private AgendaTable mAgendaTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);

        setContentView(R.layout.activity_survey_detail);

        currentUser = User.getInstance(this);

        mSurveyTable = new SurveyTable(this);
        mAgendaTable = new AgendaTable(this);

        mSurveyEdit = getIntent().getParcelableExtra(Constants.EXTRA_SURVEY);

        mSurveyEdit = mSurveyTable.getById(mSurveyEdit.getId());

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFragment = SurveyDetailFragment.newInstance(mSurveyEdit, true);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_container, mFragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean handled = false;
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                handled = true;
                break;
            case R.id.action_edit:
                //Survey selectedItem = mSurveyTable.getById(mSurveyEdit.getId());
                Intent intent = new Intent(this, SurveyActivity.class);
                intent.putExtra(Constants.EXTRA_SURVEY, (Parcelable) mSurveyEdit);
                intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_EDIT);
                startActivity(intent);
                finish();
                handled = true;
                break;
            default:
                break;
        }

        return handled;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_survey_detail, menu);

        MenuItem editMenu = menu.findItem(R.id.action_edit).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_pencil)
                        .colorRes(R.color.text_action_bar).actionBarSize());

        if (Constants.DEBUG) Log.d(TAG, "survey date: " + mSurveyEdit.getSurveyDateDDMMMMYYYY());
        if (!mSurveyEdit.getSurveyDateYYYYMMDD().equals(
                DateUtil.format(Calendar.getInstance().getTime(), Constants.DATE_DEFAULT_FORMAT))) {
            editMenu.setVisible(false);
        } else {
            editMenu.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }


    public void cancel(View view) {
        finish();
    }

}
