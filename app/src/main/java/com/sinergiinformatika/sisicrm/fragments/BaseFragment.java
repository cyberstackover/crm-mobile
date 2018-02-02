package com.sinergiinformatika.sisicrm.fragments;

import android.support.v4.app.Fragment;

import java.util.Locale;

/**
 * Created by Mark on 12/23/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class BaseFragment extends Fragment {
    private String title;

    public String getTitle() {
        Locale l = Locale.getDefault();
        if (title != null)
            return title.toUpperCase(l);

        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
