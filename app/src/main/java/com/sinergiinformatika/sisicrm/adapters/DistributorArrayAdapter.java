package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Distributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 1/7/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class DistributorArrayAdapter extends ArrayAdapter<Distributor> implements SpinnerAdapter {
    private String hint;
    private List<Distributor> distributors;
    private Context context;

    public DistributorArrayAdapter(Context context, int resource, int textViewResourceId,
                                   List<Distributor> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.distributors = objects;
        this.hint = null;
    }

    public DistributorArrayAdapter(Context context, int resource, int textViewResourceId,
                                   @NonNull List<Distributor> objects, String hint) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.distributors = objects;

        if (!TextUtils.isEmpty(hint)) {
            this.hint = hint;
            distributors.add(new Distributor("", hint));
        } else {
            this.hint = null;
        }
    }

    @Override
    public Distributor getItem(int position) {
        if (getCount() > 0 && position >= 0 && position < distributors.size()) {
            return distributors.get(position);
        }
        return null;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
            textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }

        textView.setText(distributors.get(position).getName());

        return convertView;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (distributors != null) {
            count = distributors.size();
        }

        if (hint == null) {
            return count;
        }

        return count > 0 ? count - 1 : count;
    }

    @Override
    public void add(Distributor distributor) {
        if (distributors == null) {
            distributors = new ArrayList<>();
        }

        if (hint == null || distributors.size() == 0) {
            distributors.add(distributor);
        } else {
            distributors.add(distributors.size() - 1, distributor);
        }
    }

    public void setHint(String hint) {
        if (this.hint != null) {
            Distributor d = distributors.get(distributors.size() - 1);
            d.setName(hint);
        } else {
            distributors.add(new Distributor("", hint));
        }

        this.hint = hint;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_spinner_item, parent, false);
            textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }

        if (hint != null && position == distributors.size() - 1) {
            textView.setTextColor(context.getResources().getColor(R.color.text_dark_secondary));
        }
        textView.setText(getItem(position).getName());

        return convertView;
    }

    public int getPositionByDistributorId(String id) {
        if (distributors != null) {
            for (int i = 0; i < distributors.size(); i++) {
                Distributor d = distributors.get(i);
                if (d.getId().equals(id)) {
                    return i;
                }
            }
        }

        return -1;
    }
}
