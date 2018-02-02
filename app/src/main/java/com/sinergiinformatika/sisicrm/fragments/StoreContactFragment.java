package com.sinergiinformatika.sisicrm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.data.models.Religion;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

public class StoreContactFragment extends Fragment {

    private static final String TAG = StoreContactFragment.class.getSimpleName();
    public EditText mPhoneMobileView, mPhoneView, mOwnerNameView, mOwnerBirthDateView,
            mStoreInformationView;
    public Spinner mOwnerReligionView;
    private Context context;
    private View mRootView;
    private LabelValueAdapter mReligionAdapter;
    private Store mStoreEdit;

    public StoreContactFragment() {
        // Required empty public constructor
    }

    public static StoreContactFragment newInstance(Store s) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA_STORE, s);

        StoreContactFragment fragment = new StoreContactFragment();
        fragment.setArguments(args);
        return fragment;
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

        mRootView = inflater.inflate(R.layout.fragment_store_contact, container, false);
        mOwnerNameView = (EditText) mRootView.findViewById(R.id.store_edit_owner_name);
        mPhoneView = (EditText) mRootView.findViewById(R.id.store_edit_phone_number);
        mPhoneMobileView = (EditText) mRootView.findViewById(R.id.store_edit_hp);

        mOwnerReligionView = (Spinner) mRootView.findViewById(R.id.store_edit_owner_religion);
        mReligionAdapter = new LabelValueAdapter(context, 0, getReligions(),
                                                 context.getString(R.string.hint_religion));
        mOwnerReligionView.setAdapter(mReligionAdapter);
        mOwnerReligionView.setSelection(getReligions().size());

        mOwnerBirthDateView = (EditText) mRootView.findViewById(R.id.store_edit_birth_date);
        mStoreInformationView = (EditText) mRootView.findViewById(R.id.store_edit_descrtiption);

        //dummy();

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mStoreEdit != null) {
            int religionIdx = -1;
            LabelValueAdapter religionAdapter =
                    (LabelValueAdapter) mOwnerReligionView.getAdapter();

            for (int i = 0; i < religionAdapter.getCount(); i++) {
                if (Integer.valueOf(religionAdapter.getItem(i).getValue()).equals(
                        mStoreEdit.getOwnerReligionCode())) {
                    religionIdx = i;
                }
            }

            if (religionIdx > -1) {
                mOwnerReligionView.setSelection(religionIdx);
            }

            WidgetUtil.setValue(mOwnerNameView, mStoreEdit.getOwnerName());
            WidgetUtil.setValue(
                    mStoreInformationView, mStoreEdit.getInformation());
            WidgetUtil.setValue(
                    mOwnerBirthDateView, mStoreEdit.getOwnerBirthDate());
            WidgetUtil.setValue(mPhoneView, mStoreEdit.getPhone());
            WidgetUtil.setValue(mPhoneMobileView, mStoreEdit.getPhoneMobile());
        }
    }

    private List<Religion> getReligions() {
        List<Religion> religions = new ArrayList<Religion>();
        religions.add(new Religion(Religion.ISLAM, getString(R.string.label_islam)));
        religions.add(new Religion(Religion.KRISTEN, getString(R.string.label_kristen)));
        religions.add(new Religion(Religion.KATOLIK, getString(R.string.label_katolik)));
        religions.add(new Religion(Religion.HINDU, getString(R.string.label_hindu)));
        religions.add(new Religion(Religion.BUDHA, getString(R.string.label_budha)));
        religions.add(new Religion(Religion.KHONGHUCU, getString(R.string.label_khonghucu)));
        return religions;
    }

    /*private void dummy() {
        mPhoneView.setText("022676767");
        mPhoneMobileView.setText("082121486142");
        mOwnerNameView.setText("Wendi Gunawan");
        mOwnerBirthDateView.setText("1983-11-11");
        mStoreInformationView.setText("new mStoreEdit information");
    }*/

}
