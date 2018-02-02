package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.data.models.Agenda;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.db.tables.ComplainTable;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.dialogs.LoadingDialog;
import com.sinergiinformatika.sisicrm.fragments.SurveyDetailFragment;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import java.util.ArrayList;


public class SurveyActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = SurveyActivity.class.getSimpleName();

    private SurveyDetailFragment mFragment;
    private Survey mSurveyEdit;
    private User currentUser;
    private SurveyTable mSurveyTable;
    private AgendaTable mAgendaTable;
    private ComplainTable mComplainTable;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);

        setContentView(R.layout.activity_survey_detail);

        currentUser = User.getInstance(this);
        loading = new LoadingDialog();
        mSurveyTable = new SurveyTable(this);
        mAgendaTable = new AgendaTable(this);
        mComplainTable = new ComplainTable(this);

        mSurveyEdit = getIntent().getParcelableExtra(Constants.EXTRA_SURVEY);
        int action = getIntent().getIntExtra(Constants.EXTRA_ACTION, Constants.ACTION_ADD);

        Agenda agenda = mAgendaTable.getById(mSurveyEdit.getAgendaDbId());
        if (agenda == null) {
            agenda = new Agenda();
        } else {
            mSurveyEdit.setStoreId(agenda.getStoreId());
            mSurveyEdit.setStoreName(agenda.getStoreName());
        }

        if (mSurveyEdit.getSurveyId() == null ||
            !mSurveyTable.isExists(mSurveyEdit.getSurveyId())) {

//            mSurveyEdit.setStoreId(null);//new survey, belum di sync ke API
            mSurveyEdit.setCheckInNfc(false);
            mSurveyEdit.setCheckIn(agenda.getCheckInDateTime());
            mSurveyEdit.setCheckInTime(agenda.getCheckInDateTime());
            mSurveyEdit.setCheckInLongitude(agenda.getCheckInLong());
            mSurveyEdit.setCheckInlatitude(agenda.getCheckInLat());

            mSurveyEdit.setCheckOut(agenda.getCheckOutDateTime());
            mSurveyEdit.setCheckOutLongitude(agenda.getCheckOutLong());
            mSurveyEdit.setCheckOutLatitude(agenda.getCheckOutLat());

            if (mSurveyEdit.getCheckOut() == null ||
                mSurveyEdit.getCheckOut().trim().length() == 0) {
                mSurveyEdit.setCheckOut(mSurveyEdit.getCheckInTime());
            }

            if (mSurveyEdit.getCheckOutLatitude() == 0.0) {
                mSurveyEdit.setCheckOutLatitude(mSurveyEdit.getCheckInLatitude());
            }

            if (mSurveyEdit.getCheckOutLongitude() == 0.0) {
                mSurveyEdit.setCheckOutLongitude(mSurveyEdit.getCheckInLongitude());
            }

            mSurveyEdit.setPlanDate(agenda.getDate());

            if (mSurveyEdit.getComplains() == null || mSurveyEdit.getComplains().size() == 0) {
                mSurveyEdit.setComplains(mComplainTable.getAll(null, null));
            }

        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (action == Constants.ACTION_EDIT) {
                actionBar.setTitle(R.string.title_edit_survey);
            }
        }

        mFragment = SurveyDetailFragment.newInstance(mSurveyEdit, false);

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
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean validate(Survey survey) {
        if (survey.getPrices() == null || survey.getPrices().size() == 0) {
            Toast.makeText(this, getString(R.string.error_min_one), Toast.LENGTH_LONG).show();
            return false;
        }

        for (ItemPrice ip : survey.getPrices()) {

            if (Constants.DEBUG) {
                Log.d(TAG, String.format("%s %s %s", ip.getPrice(), ip.getVolume(), ip.getStock()));
            }

            if (ip.getPrice() <= 0.0) {
                Toast.makeText(this, String.format(
                        getString(R.string.error_field_must_greater_than_or_equal_to),
                        getString(R.string.label_selling_price), "0"), Toast.LENGTH_LONG).show();
                return false;
            }

            if (ip.getPrice() < ip.getPricePurchase()) {
                Toast.makeText(this, "Harga jual harus lebih tinggi dari harga beli",
                               Toast.LENGTH_SHORT).show();
                return false;
            }

            if (ip.getPrice() - ip.getPricePurchase() > ip.getPricePurchase() / 2) {
                Toast.makeText(this, "Harga jual tidak boleh lebih dari 150% harga beli",
                               Toast.LENGTH_SHORT).show();
                return false;
            }

            if (ip.getVolume() < 0.0) {
                Toast.makeText(this, String.format(
                        getString(R.string.error_field_must_greater_than_or_equal_to),
                        getString(R.string.label_volume), "0"), Toast.LENGTH_LONG).show();
                return false;
            }

            if (ip.getStock() < 0.0) {
                Toast.makeText(this, String.format(
                        getString(R.string.error_field_must_greater_than_or_equal_to),
                        getString(R.string.label_stock), "0"), Toast.LENGTH_LONG).show();
                return false;
            }

        }

        return true;
    }

    public void save(View view) {

        if (Constants.DEBUG) {
            Log.d(TAG, "save");
        }

        try {

            //get data from active fragment
            switch (mFragment.mCurrentPosition) {
                case 0:
                    mFragment.mPrices = mFragment.mPriceFragment.getPrices();
                    break;
                case 1:
                    mFragment.mImages = mFragment.mImageFragment.getImages();
                    break;
                case 2:
                    mFragment.mComplains = mFragment.mComplainFragment.getComplains();
                    mFragment.mComplainNotes = mFragment.mComplainFragment.getComplainNotes();
                    break;
                case 3:
                    mFragment.mCompetitorPrograms =
                            mFragment.mRivalFragment.getProductProgramsMap();
                    mFragment.mCompetitorNotes = mFragment.mRivalFragment.getCompetitorNotes();
                    break;
            }

            mSurveyEdit.setPrices(mFragment.mPrices);
            mSurveyEdit.setImages(mFragment.mImages);
            if (mFragment.mComplains != null) {
                mSurveyEdit.setComplains(new ArrayList<>(mFragment.mComplains.values()));
            }
            if (mFragment.mComplainNotes != null) {
                mSurveyEdit.addNotes(new ArrayList<>(mFragment.mComplainNotes.values()));
            }

            if (mFragment.mCompetitorPrograms != null) {
                mSurveyEdit.setCompetitorPrograms(
                        new ArrayList<>(mFragment.mCompetitorPrograms.values()));
            }
            if (mFragment.mCompetitorNotes != null) {
                mSurveyEdit.addNotes(new ArrayList<>(mFragment.mCompetitorNotes.values()));
            }

            if (Constants.DEBUG) {
                Log.d(TAG, "competitors: " + mSurveyEdit.getCompetitorPrograms().size());
            }
            if (Constants.DEBUG) Log.d(TAG, "notes: " + mSurveyEdit.getNotes().size());

            if (validate(mSurveyEdit)) {
                mSurveyEdit.setSurveyDate(DateUtil.formatDBDateTime(DateUtil.now()));
                mSurveyEdit.setSyncStatus(Constants.SYNC_STATUS_PENDING);

                if (mSurveyEdit.getId() != null && mSurveyEdit.getId() > 0) {
                    if (!TextUtils.isEmpty(mSurveyEdit.getSyncStatus())
                        && mSurveyEdit.getSyncStatus().equals(Constants.SYNC_STATUS_SENDING)) {
                        Toast.makeText(this, R.string.error_data_is_sending, Toast.LENGTH_LONG)
                             .show();
                    } else {
                        mSurveyTable.updateById(mSurveyEdit.getId(), mSurveyEdit);
                    }
                } else {
                    int id = mSurveyTable.insert(mSurveyEdit);
                    mAgendaTable.updateSurveyDbId(mSurveyEdit.getAgendaDbId(), id);
                }

                Toast.makeText(SurveyActivity.this, getString(R.string.message_save_success),
                               Toast.LENGTH_LONG).show();
                finish();
            } else {
                if (mFragment.mCurrentPosition > 0) {
                    mFragment.changePage(0);
                }
            }
        } catch (Exception e) {
            Toast.makeText(SurveyActivity.this, getString(R.string.error_save_failed),
                           Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void prev(View view) {
        int page = mFragment.mCurrentPosition - 1;
        mFragment.mViewPager.setCurrentItem(page);
        mFragment.changePage(page);
    }

    public void next(View view) {
        if (mFragment.mCurrentPosition == 0) {
            mSurveyEdit.setPrices(mFragment.mPriceFragment.getPrices());
        }
        if (mFragment.mCurrentPosition != 0 || validate(mSurveyEdit)) {
            int page = mFragment.mCurrentPosition + 1;
            mFragment.mViewPager.setCurrentItem(page);
            mFragment.changePage(page);
        }
    }

    /*public void showLoading() {
        loading.setCancelable(false);
        loading.show(getSupportFragmentManager(), "loading");
    }

    public void hideLoading() {
        if (loading != null) {
            loading.dismiss();
        }

    }*/

    @Override
    public void onClick(View view) {
        int action = (int) view.getTag(R.string.tag_action);
        if (action == R.string.action_competitor) {
            toggleCompetitor(view);
        } else if (action == R.string.action_complain) {
            toggleComplain(view);
        }
    }

    public void toggleComplain(View view) {
        Button button = (Button) view;
        View parent = (View) view.getParent().getParent();

        if (button.getTag().equals(getString(R.string.tag_add))) {
            button.setTag(getString(R.string.tag_remove));
            button.setText(R.string.icon_delete);

            Spinner productSpinner = (Spinner) parent.findViewById(R.id.row_toggle_spinner);
            TextView productText = (TextView) parent.findViewById(R.id.row_toggle_product_text);
            View itemParent = parent.findViewById(R.id.row_toggle_item_parent);

            productText.setText(((LabelValue) productSpinner.getSelectedItem()).getLabel());
            productText.setVisibility(View.VISIBLE);
            productSpinner.setVisibility(View.GONE);
            itemParent.setVisibility(View.VISIBLE);

            mFragment.mComplainFragment.addItemRow();
        } else {
            mFragment.mComplainFragment.removeItemRow(parent);
        }
    }

    public void toggleCompetitor(View view) {
        Button button = (Button) view;
        View parent = (View) view.getParent().getParent();

        if (button.getTag().equals(getString(R.string.tag_add))) {
            button.setTag(getString(R.string.tag_remove));
            button.setText(R.string.icon_delete);

            Spinner productSpinner = (Spinner) parent.findViewById(R.id.row_toggle_spinner);
            TextView productText = (TextView) parent.findViewById(R.id.row_toggle_product_text);
            View itemParent = parent.findViewById(R.id.row_toggle_item_parent);

            productText.setText(((LabelValue) productSpinner.getSelectedItem()).getLabel());
            productText.setVisibility(View.VISIBLE);
            productSpinner.setVisibility(View.GONE);
            itemParent.setVisibility(View.VISIBLE);

            mFragment.mRivalFragment.addItemRow();
        } else {
            mFragment.mRivalFragment.removeItemRow(parent);
        }
    }
}
