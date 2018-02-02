package com.sinergiinformatika.sisicrm.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Mark on 12/22/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SurveyPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public SurveyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments == null || getCount() <= 0)
            return null;

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
