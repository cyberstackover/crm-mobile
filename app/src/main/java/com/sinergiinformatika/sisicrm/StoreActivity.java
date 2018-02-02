package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.data.models.Religion;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.dialogs.DatePickerFragment;
import com.sinergiinformatika.sisicrm.fragments.StoreContactFragment;
import com.sinergiinformatika.sisicrm.fragments.StoreInfoFragment;
import com.sinergiinformatika.sisicrm.fragments.StoreLocationFragment;
import com.sinergiinformatika.sisicrm.utils.Formater;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;


public class StoreActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = StoreActivity.class.getSimpleName();

    //    private boolean editMode;
    private User currentUser = null;
    private Store mStoreEdit;
    private StoreTable mStoreTable;
    private StoreInfoFragment mStoreInfoFragment;
    private StoreLocationFragment mStoreLocationFragment;
    private StoreContactFragment mStoreContactFragment;
    private View mButtonHolder2, mButtonHolder1, mSpaceBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);
        setContentView(R.layout.activity_store);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentUser = User.getInstance(this);
        mStoreTable = new StoreTable(this);
        mStoreEdit = getIntent().getParcelableExtra(Constants.EXTRA_STORE);

        if (savedInstanceState != null) {
            mStoreEdit = savedInstanceState.getParcelable(Constants.EXTRA_STORE);
        }

        if (mStoreEdit == null) {
//            editMode = false;
            mStoreInfoFragment = new StoreInfoFragment();
            mStoreLocationFragment = new StoreLocationFragment();
            mStoreContactFragment = new StoreContactFragment();
        } else {
//            editMode = true;
            mStoreInfoFragment = StoreInfoFragment.newInstance(mStoreEdit);
            mStoreLocationFragment = StoreLocationFragment.newInstance(mStoreEdit);
            mStoreContactFragment = StoreContactFragment.newInstance(mStoreEdit);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container_top, mStoreInfoFragment);
        ft.commit();

        mButtonHolder1 = findViewById(R.id.button_holder_1);
        mButtonHolder2 = findViewById(R.id.button_holder_2);
        mSpaceBottom = findViewById(R.id.space_bottom);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mStoreEdit == null) {
            mStoreEdit = new Store();
        }/* else {
            setValues();
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.EXTRA_STORE, mStoreEdit);
        super.onSaveInstanceState(outState);
    }

    public void cancel(View view) {
        finish();
    }

    /*private void setValues() {
        Toast.makeText(this, "set values", Toast.LENGTH_SHORT).show();
        int categoryIdx = 0, distributorIdx = -1;
        LabelValueAdapter categoryAdapter =
                (LabelValueAdapter) mStoreInfoFragment.mStoreCategoryView.getAdapter();
        DistributorArrayAdapter distributorAdapter =
                (DistributorArrayAdapter) mStoreInfoFragment.distributorSpinner.getAdapter();

        for (int i = 0; i < categoryAdapter.getCount(); i++) {
            if (Integer.valueOf(categoryAdapter.getItem(i).getValue())
                       .equals(mStoreEdit.getCategoryCode())) {
                categoryIdx = i;
            }
        }

        if (!TextUtils.isEmpty(mStoreEdit.getDistributorId())) {
            for (int i = 0; i < distributorAdapter.getCount(); i++) {
                if (distributorAdapter.getItem(i).getId().equals(mStoreEdit.getDistributorId())) {
                    distributorIdx = i;
                }
            }
        }

        if (distributorIdx >= 0) {
            mStoreInfoFragment.distributorSpinner.setSelection(distributorIdx);
            mStoreInfoFragment.setDistributorId(
                    ((Distributor) mStoreInfoFragment.distributorSpinner.getSelectedItem())
                            .getId());
        }

        if (!TextUtils.isEmpty(mStoreEdit.getPhoto())) {
            File imageFile = new File(mStoreEdit.getPhoto());
            Bitmap imageBitmap = null;

            if (imageFile.exists()) {
                imageBitmap = ImageDecodeUtil
                        .decodeFile(this, imageFile.getAbsolutePath(), imageFile.getAbsolutePath());
            } else if (mStoreEdit.getPhoto().trim().length() > "http://".length()) {
                imageBitmap = ImageDecodeUtil
                        .decodeUrl(this, ImageDecodeUtil.getImageIdFromUri(mStoreEdit.getPhoto()),
                                   ImageCacheManager.IMAGE_TYPE_ITEM_THUMB,
                                   mStoreEdit.getPhoto());
            }

            if (imageBitmap != null) {
                mStoreInfoFragment.mStorePhotoView.setImageBitmap(imageBitmap);
            }
        }

        WidgetUtil.setValue(mStoreInfoFragment.mStoreNameView, mStoreEdit.getName());
        WidgetUtil.setValue(mStoreInfoFragment.mCoordinateView,
                            String.format("%s,%s", mStoreEdit.getLongitude(),
                                          mStoreEdit.getLatitude()));
        mStoreInfoFragment.mStoreCategoryView.setSelection(categoryIdx);
        WidgetUtil.setValue(mStoreInfoFragment.mCapacityView, mStoreEdit.getCapacity());
        mStoreInfoFragment.mImagePath = mStoreEdit.getPhoto();
    }*/

    private boolean validate(Store store, int page) {

        View focusView;

        if (page == 1) {
            /*if (null == store.getPhoto()) {
                Toast.makeText(StoreActivity.this, getString(R.string.error_empty_store_image),
                               Toast.LENGTH_SHORT).show();
                return false;
            }*/

            if (TextUtils.isEmpty(store.getName()) || store.getName().equals("-")) {
                mStoreInfoFragment.mStoreNameView
                        .setError(getString(R.string.error_field_required));
                focusView = mStoreInfoFragment.mStoreNameView;
                focusView.requestFocus();
                return false;
            }

            if (store.getLatitude() == null
                || store.getLongitude() == null
                || store.getLatitude() == 0
                || store.getLongitude() == 0) {

                mStoreInfoFragment.mCoordinateView
                        .setError(getString(R.string.error_field_required));
                focusView = mStoreInfoFragment.mCoordinateView;
                focusView.requestFocus();
                return false;

            }

            if (store.getCapacity() == null || store.getCapacity() == 0) {
                mStoreInfoFragment.mCapacityView.setError(getString(R.string.error_field_required));
                focusView = mStoreInfoFragment.mCapacityView;
                focusView.requestFocus();
                return false;
            }


        } else if (page == 2) {


            if (TextUtils.isEmpty(store.getProvinceId())) {
                mStoreLocationFragment.mProvinceView
                        .setError(getString(R.string.error_field_required));
                focusView = mStoreLocationFragment.mProvinceView;
                focusView.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(store.getCityId())) {
                mStoreLocationFragment.mCityView.setError(getString(R.string.error_field_required));
                focusView = mStoreLocationFragment.mCityView;
                focusView.requestFocus();
                return false;
            }


            if (TextUtils.isEmpty(store.getSubdistrictId())) {
                mStoreLocationFragment.mSubdistrictView
                        .setError(getString(R.string.error_field_required));
                focusView = mStoreLocationFragment.mSubdistrictView;
                focusView.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(store.getStreet()) || store.getStreet().equals("-")) {
                mStoreLocationFragment.mStreetView
                        .setError(getString(R.string.error_field_required));
                focusView = mStoreLocationFragment.mStreetView;
                focusView.requestFocus();
                return false;
            }

            /*if (TextUtils.isEmpty(store.getZipcode())) {
                mStoreLocationFragment.mZipcodeView.setError(getString(R.string
                .error_field_required));
                focusView = mStoreLocationFragment.mZipcodeView;
                focusView.requestFocus();
                return false;
            }

            if (store.getZipcode().length() != Constants.ZIP_CODE_LENGTH) {
                mStoreLocationFragment.mZipcodeView.setError(getString(R.string
                .error_field_length_must_equal_to, getString(R.string.label_zip_code), Constants
                .ZIP_CODE_LENGTH));
                focusView = mStoreLocationFragment.mZipcodeView;
                focusView.requestFocus();
                return false;
            }*/


            if (TextUtils.isEmpty(store.getOwnerName()) || store.getOwnerName().equals("-")) {
                mStoreContactFragment.mOwnerNameView
                        .setError(getString(R.string.error_field_required));
                focusView = mStoreContactFragment.mOwnerNameView;
                focusView.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(store.getPhone()) || store.getPhone().equals("-")) {
                mStoreContactFragment.mPhoneView.setError(getString(R.string.error_field_required));
                focusView = mStoreContactFragment.mPhoneView;
                focusView.requestFocus();
                return false;
            }

        }

        return true;
    }

    public void save(View view) {

        if (currentUser == null) {
            currentUser = User.getInstance(this);
        }

        Integer religionCode = Integer.valueOf(
                ((LabelValue) mStoreContactFragment.mOwnerReligionView.getSelectedItem())
                        .getValue());

        mStoreEdit.setProvince(WidgetUtil.getValue(mStoreLocationFragment.mProvinceView));
        mStoreEdit.setCity(WidgetUtil.getValue(mStoreLocationFragment.mCityView));
        mStoreEdit.setSubdistrict(WidgetUtil.getValue(mStoreLocationFragment.mSubdistrictView));

        mStoreEdit.setStreet(WidgetUtil.getValue(mStoreLocationFragment.mStreetView));
        mStoreEdit.setZipcode(WidgetUtil.getValue(mStoreLocationFragment.mZipcodeView));
        mStoreEdit.setPhoneMobile(WidgetUtil.getValue(mStoreContactFragment.mPhoneMobileView));
        mStoreEdit.setPhone(WidgetUtil.getValue(mStoreContactFragment.mPhoneView));
        mStoreEdit.setOwnerName(WidgetUtil.getValue(mStoreContactFragment.mOwnerNameView));
        mStoreEdit
                .setOwnerBirthDate(WidgetUtil.getValue(mStoreContactFragment.mOwnerBirthDateView));
        if (religionCode > -1) {
            mStoreEdit.setOwnerReligionCode(religionCode);
        }
        mStoreEdit.setOwnerReligionLabel(Religion.getLabel(mStoreEdit.getOwnerReligionCode()));
        mStoreEdit.setInformation(WidgetUtil.getValue(mStoreContactFragment.mStoreInformationView));

        if (mStoreLocationFragment.getSelectedProvince() != null) {
            mStoreEdit.setProvinceId(mStoreLocationFragment.getSelectedProvince().getValue());
        }

        if (mStoreLocationFragment.getSelectedCity() != null) {
            mStoreEdit.setCityId(mStoreLocationFragment.getSelectedCity().getValue());
        }

        if (mStoreLocationFragment.getSelectedSubdistrict() != null) {
            mStoreEdit.setSubdistrictId(mStoreLocationFragment.getSelectedSubdistrict().getValue());
        }

        int page = 2; //page 1 tidak perlu divalidasi karna sudah divalidasi saat klik tombol next

        if (validate(mStoreEdit, page)) {
            try {
                mStoreTable.save(mStoreEdit);

                Toast.makeText(StoreActivity.this, getString(R.string.message_save_success),
                               Toast.LENGTH_LONG).show();

                if (getParent() != null) {
                    getParent().setResult(Activity.RESULT_OK);
                } else {
                    setResult(Activity.RESULT_OK);
                }

                finish();

            } catch (Exception e) {
                Toast.makeText(StoreActivity.this, getString(R.string.error_save_failed),
                               Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        //TODO format nya perlu di ubah ke dd/MM/yyyy atau dd MMMM yyyy
        mStoreContactFragment.mOwnerBirthDateView
                .setText(String.format("%d-%02d-%02d", year, month + 1, day));
    }

    public void invokeDatePicker(View view) {
        DatePickerFragment fragment = DatePickerFragment.newInstance(this, null, null);
        fragment.show(getSupportFragmentManager(), "STORE_ADD");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void next(View view) {

        mStoreEdit.setName(WidgetUtil.getValue(mStoreInfoFragment.mStoreNameView));
        mStoreEdit.setCategoryCode(Integer.valueOf(
                ((LabelValue) mStoreInfoFragment.mStoreCategoryView.getSelectedItem()).getValue()));
        mStoreEdit.setCategoryLabel(StoreCategory.getLabel(mStoreEdit.getCategoryCode()));
        try {
            mStoreEdit.setCapacity(Integer.parseInt(WidgetUtil.getValue(
                    mStoreInfoFragment.mCapacityView)));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (!TextUtils.isEmpty(mStoreInfoFragment.getDistributorId())) {
            mStoreEdit.setDistributorId(mStoreInfoFragment.getDistributorId());
        }

        mStoreEdit.setNfcId(WidgetUtil.getValue(mStoreInfoFragment.mNfcIdView));
        String location = WidgetUtil.getValue(mStoreInfoFragment.mCoordinateView);

        Double lng = null, lat = null;
        try {
            String[] locationTokens = location.split(",");
            lng = Formater.doubleValue(locationTokens[0]);
            lat = Formater.doubleValue(locationTokens[1]);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage(), e);
        }

        mStoreEdit.setLongitude(lng);
        mStoreEdit.setLatitude(lat);
        mStoreEdit.setPhoto(mStoreInfoFragment.mImagePath);

        int page = 1;

        if (validate(mStoreEdit, page)) {

            if (null == mStoreLocationFragment) {
                if (TextUtils.isEmpty(mStoreEdit.getProvinceId())) {
                    mStoreLocationFragment = new StoreLocationFragment();
                } else {
                    mStoreLocationFragment = StoreLocationFragment.newInstance(mStoreEdit);
                }
            }

            if (null == mStoreContactFragment) {
                if (TextUtils.isEmpty(mStoreEdit.getOwnerName())) {
                    mStoreContactFragment = new StoreContactFragment();
                } else {
                    mStoreContactFragment = StoreContactFragment.newInstance(mStoreEdit);
                }
            }

            mButtonHolder2.setVisibility(View.VISIBLE);
            mButtonHolder1.setVisibility(View.GONE);
            mSpaceBottom.setVisibility(View.VISIBLE);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container_top, mStoreLocationFragment);
            ft.add(R.id.container_bottom, mStoreContactFragment);
            ft.commit();
            fm.executePendingTransactions();
        }
    }

    public void back(View view) {

        mButtonHolder2.setVisibility(View.GONE);
        mButtonHolder1.setVisibility(View.VISIBLE);
        mSpaceBottom.setVisibility(View.GONE);

        mStoreInfoFragment.mImagePath = mStoreEdit.getPhoto();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_top, mStoreInfoFragment);
        ft.remove(mStoreContactFragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
