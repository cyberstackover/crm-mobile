package com.sinergiinformatika.sisicrm.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.R;

/**
 * A simple factory that returns dummy views to the Tabhost
 *
 * @author mwho
 */
public class TabFactory implements TabHost.TabContentFactory {

    private final Context mContext;

    /**
     * @param context application context
     */
    public TabFactory(Context context) {
        mContext = context;
    }

    public static View createTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);
        TextView tabTitle = (TextView) view.findViewById(R.id.textTabTitle);
        tabTitle.setText(title.toUpperCase());

        return view;
    }

    public static View createTabNavIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_nav_indicator, null);
        TextView tabTitle = (TextView) view.findViewById(R.id.textTabTitle);
        tabTitle.setText(title.toUpperCase());

        return view;
    }

    /**
     * (non-Javadoc)
     *
     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
     */
    public View createTabContent(String tag) {
        View v = new View(mContext);
        v.setMinimumWidth(0);
        v.setMinimumHeight(0);
        return v;
    }

}
