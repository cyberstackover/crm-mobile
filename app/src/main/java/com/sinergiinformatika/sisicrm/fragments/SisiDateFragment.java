package com.sinergiinformatika.sisicrm.fragments;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.SisiDateAdapter;

/**
 * Created by Mark on 3/31/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SisiDateFragment extends CaldroidFragment {

    @Override
    protected int getGridViewRes() {
        return R.layout.grid_date;
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new SisiDateAdapter(getActivity(), month, year, getCaldroidData(), getExtraData());
    }
}
