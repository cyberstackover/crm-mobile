package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.data.models.LabelValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wendi on 26-Dec-14.
 */
public class LabelValueAdapter extends ArrayAdapter<LabelValue> implements Filterable {

    private static final String TAG = LabelValueAdapter.class.getSimpleName();

    private List<? extends LabelValue> items;
    private List<LabelValue> filteredItems;
    private Context context;
    private Filter filter;
    private int resource;
    private String hint;

    public LabelValueAdapter(Context context, int resource, List<? extends LabelValue> items) {

        super(context, resource, (List<LabelValue>) items);

        this.context = context;
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        this.filter = new LabelValueFilter();
        this.hint = null;
        if (resource == 0) {
            this.resource = android.R.layout.simple_spinner_item;
        } else {
            this.resource = resource;
        }

    }

    public LabelValueAdapter(Context context, int resource,
                             List<? extends LabelValue> items,
                             String hint) {
        super(context, resource, (List<LabelValue>) items);
        this.items = items;
        this.context = context;
        this.filteredItems = new ArrayList<>(items);
        this.filter = new LabelValueFilter();

        if (!TextUtils.isEmpty(hint)) {
            this.hint = hint;
            this.filteredItems.add(new LabelValue("-1", hint, true));
        } else {
            this.hint = null;
        }

        if (resource == 0) {
            this.resource = android.R.layout.simple_spinner_item;
        } else {
            this.resource = resource;
        }
    }

    @Override
    public int getCount() {
        int count = filteredItems != null ? filteredItems.size() : 0;

        if (hint == null) {
            return count;
        }

//        return filteredItems != null ? filteredItems.size() : 0;
        return count > 0 ? count - 1 : count;
    }

    @Override
    public LabelValue getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        textView = (TextView) convertView;
        textView.setText(getItem(position).getLabel());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        textView = (TextView) convertView;
        //textView.setText(filteredItems.get(position).getLabel());
        textView.setText(getItem(position).getLabel());

        if (getItem(position).isPlaceHolder()) {
            textView.setTextColor(context.getResources().getColor(android.R.color.tertiary_text_dark));
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public int getPositionFromItemId(String value) {
        for (int i = 0; i < getCount(); i++) {
            if (items.get(i).getValue().equals(value))
                return i;
        }

        return -1;
    }

    private class LabelValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            List<LabelValue> found = new ArrayList<>();

            if (charSequence != null) {
                for (LabelValue lv : items) {
                    if (lv.getLabel().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        found.add(lv);
                    }
                }
            } else {
                found = new ArrayList<>(items);
            }

            results.count = found.size();
            results.values = found;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            if (filterResults != null && filterResults.count > 0) {
                if (filterResults.values instanceof List) {
                    filteredItems.clear();
                    for (Object obj : (List) filterResults.values) {
                        if (obj instanceof LabelValue) {
                            filteredItems.add((LabelValue) obj);
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }
}
