package com.sinergiinformatika.sisicrm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.data.models.City;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.data.models.Province;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.Subdistrict;
import com.sinergiinformatika.sisicrm.db.tables.CityTable;
import com.sinergiinformatika.sisicrm.db.tables.ProvinceTable;
import com.sinergiinformatika.sisicrm.db.tables.SubdistrictTable;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreLocationFragment extends Fragment {

    private static final String TAG = StoreLocationFragment.class.getSimpleName();
    public AutoCompleteTextView mProvinceView, mCityView, mSubdistrictView;
    //public EditText mProvinceView, mCityView, mSubdistrictView;
    public EditText mStreetView, mZipcodeView;
    private Context context;
    private View mRootView;
    private LabelValueAdapter mProvinceAdapter;
    private LabelValueAdapter mCityAdapter;
    private LabelValueAdapter mSubdistrictAdapter;
    private LabelValue selectedProvince;
    private LabelValue selectedCity;
    private LabelValue selectedSubdistrict;

    private ProvinceTable mProvinceTable;
    private CityTable mCityTable;
    private SubdistrictTable mSubdistrictTable;
    private View.OnFocusChangeListener autoCompleteFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ((AutoCompleteTextView) v).showDropDown();
            }
        }
    };
    private List<LabelValue> mProvinces = null;
    private Map<String, List<LabelValue>> mCities = null;
    private Map<String, List<LabelValue>> mSubdistricts = null;
    private Store mStoreEdit;

    public StoreLocationFragment() {
        // Required empty public constructor
    }

    public static StoreLocationFragment newInstance(Store s) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA_STORE, s);

        StoreLocationFragment fragment = new StoreLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LabelValue getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(
            LabelValue selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public LabelValue getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(LabelValue selectedCity) {
        this.selectedCity = selectedCity;
    }

    public LabelValue getSelectedSubdistrict() {
        return selectedSubdistrict;
    }

    public void setSelectedSubdistrict(
            LabelValue selectedSubdistrict) {
        this.selectedSubdistrict = selectedSubdistrict;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (getArguments() != null) {
            mStoreEdit = getArguments().getParcelable(Constants.EXTRA_STORE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_store_location, container, false);

        mProvinceTable = new ProvinceTable(context);
        mCityTable = new CityTable(context);
        mSubdistrictTable = new SubdistrictTable(context);

        mProvinceView = (AutoCompleteTextView) mRootView.findViewById(R.id.store_edit_province);
        mProvinceView.setThreshold(0);
        mCityView = (AutoCompleteTextView) mRootView.findViewById(R.id.store_edit_city);
        mCityView.setThreshold(0);
        mSubdistrictView = (AutoCompleteTextView) mRootView.findViewById(R.id.store_edit_subdistrict);
        mSubdistrictView.setThreshold(0);

        /*mProvinceView = (EditText) mRootView.findViewById(R.id.store_edit_province);
        mCityView = (EditText) mRootView.findViewById(R.id.store_edit_city);
        mSubdistrictView = (EditText) mRootView.findViewById(R.id.store_edit_subdistrict);*/

        mStreetView = (EditText) mRootView.findViewById(R.id.store_edit_address);
        mZipcodeView = (EditText) mRootView.findViewById(R.id.store_edit_zipcode);
        mZipcodeView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                //set max char = Constants.ZIP_CODE_LENGTH, jika lebih hapus char ke 6 dst
                if (s.length() > Constants.ZIP_CODE_LENGTH) {
                    s.delete(s.length() - 1, s.length());
                }

            }
        });

        mProvinceAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, getProvinces());
        mProvinceView.setAdapter(mProvinceAdapter);
        mProvinceView.setOnFocusChangeListener(autoCompleteFocusListener);
        mProvinceView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProvinceAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (selectedProvince != null && !s.toString().equalsIgnoreCase(selectedProvince.getLabel())) {
                    mCityView.setText("");
                    mCityAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, new ArrayList<LabelValue>());
                    mCityView.setAdapter(mCityAdapter);
                    selectedCity = null;

                    mSubdistrictView.setText("");
                    mSubdistrictAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, new ArrayList<LabelValue>());
                    mSubdistrictView.setAdapter(mSubdistrictAdapter);
                }
            }
        });

        mProvinceView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedProvince = mProvinceAdapter.getItem(position);
                mProvinceView.setText(selectedProvince.getLabel());

                mCityView.setText("");
                mCityAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, getCities(getSelectedProvince().getValue()));
                mCityView.setAdapter(mCityAdapter);
                selectedCity = null;

                mSubdistrictView.setText("");
                mSubdistrictAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, new ArrayList<LabelValue>());
                mSubdistrictView.setAdapter(mSubdistrictAdapter);
                selectedSubdistrict = null;

            }
        });

        mCityAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, new ArrayList<LabelValue>());
        mCityView.setAdapter(mCityAdapter);
        mCityView.setOnFocusChangeListener(autoCompleteFocusListener);
        mCityView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (selectedCity != null && !s.toString().equalsIgnoreCase(selectedCity.getLabel())) {
                    mSubdistrictView.setText("");
                    mSubdistrictAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, new ArrayList<LabelValue>());
                    mSubdistrictView.setAdapter(mSubdistrictAdapter);
                    selectedSubdistrict = null;
                }
            }
        });


        mCityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = mCityAdapter.getItem(position);
                mCityView.setText(selectedCity.getLabel());
                mSubdistrictView.setText("");

                mSubdistrictAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, getSubdistricts(getSelectedCity().getValue()));
                mSubdistrictView.setAdapter(mSubdistrictAdapter);
            }
        });

        mSubdistrictAdapter = new LabelValueAdapter(context, android.R.layout.simple_list_item_1, new ArrayList<LabelValue>());
        mSubdistrictView.setAdapter(mSubdistrictAdapter);
        mSubdistrictView.setOnFocusChangeListener(autoCompleteFocusListener);
        mSubdistrictView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSubdistrictAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSubdistrictView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSubdistrict = mSubdistrictAdapter.getItem(position);
                mSubdistrictView.setText(selectedSubdistrict.getLabel());
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mStoreEdit != null) {
            if (!TextUtils.isEmpty(mStoreEdit.getProvinceId())) {
                setSelectedProvince(
                        new LabelValue(mStoreEdit.getProvinceId(), mStoreEdit.getProvince()));
                WidgetUtil.setValue(mProvinceView, mStoreEdit.getProvince());
            }

            if (!TextUtils.isEmpty(mStoreEdit.getCityId())) {
                setSelectedCity(
                        new LabelValue(mStoreEdit.getCityId(), mStoreEdit.getCity()));
                WidgetUtil.setValue(mCityView, mStoreEdit.getCity());
            }

            if (!TextUtils.isEmpty(mStoreEdit.getSubdistrictId())) {
                setSelectedSubdistrict(
                        new LabelValue(mStoreEdit.getSubdistrictId(), mStoreEdit.getSubdistrict()));
                WidgetUtil.setValue(
                        mSubdistrictView, mStoreEdit.getSubdistrict());
            }

            WidgetUtil.setValue(mStreetView, mStoreEdit.getStreet());
            WidgetUtil.setValue(mZipcodeView, mStoreEdit.getZipcode());
        }
    }

    private List<LabelValue> getProvinces() {

        if (mProvinces == null) {
            List<Province> provinces = mProvinceTable.getAll();
            mProvinces = new ArrayList<>();
            for (Province p : provinces) {
                mProvinces.add(new LabelValue(p.getProvinceId(), p.getProvinceName()));
            }
        }

        return mProvinces;
    }

    private List<LabelValue> getCities(String selectedProvinceId) {

        if (mCities == null) {
            mCities = new HashMap<>();
        }

        if (!mCities.containsKey(selectedProvinceId)) {
            List<City> cities = mCityTable.findByProvinceId(selectedProvinceId);

            List<LabelValue> cities2 = new ArrayList<>();
            for (City c : cities) {
                cities2.add(new LabelValue(c.getCityId(), c.getCityName()));
            }
            mCities.put(selectedProvinceId, cities2);
        }

        return mCities.get(selectedProvinceId);

    }

    private List<LabelValue> getSubdistricts(String selectedCityId) {

        if (mSubdistricts == null) {
            mSubdistricts = new HashMap<>();
        }

        if (!mSubdistricts.containsKey(selectedCityId)) {
            List<Subdistrict> subdistricts = mSubdistrictTable.findByCityId(selectedCityId);
            List<LabelValue> subdistricts2 = new ArrayList<>();
            for (Subdistrict c : subdistricts) {
                subdistricts2.add(new LabelValue(c.getSubdistrictId(), c.getSubdistrictName()));
            }
            mSubdistricts.put(selectedCityId, subdistricts2);
        }

        return mSubdistricts.get(selectedCityId);

    }
}

