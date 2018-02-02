package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.data.models.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 12/31/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class StoreNameArrayAdapter extends ArrayAdapter<Store> implements Filterable {
    List<Store> stores, filteredStores;
    Context context;
    StoreFilter filter;

    public StoreNameArrayAdapter(Context context, int resource, List<Store> objects) {
        super(context, resource, objects);

        this.context = context;
        stores = objects;
        filteredStores = new ArrayList<>(objects);
        filter = new StoreFilter();
    }

    @Override
    public int getCount() {
        if (filteredStores != null)
            return filteredStores.size();

        return 0;
    }

    @Override
    public Store getItem(int position) {
        return filteredStores.get(position);
    }

    @Override
    public int getPosition(Store item) {
        return filteredStores.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(filteredStores.get(position).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class ViewHolder {
        TextView text;

        private ViewHolder(View rootView) {
            text = (TextView) rootView.findViewById(android.R.id.text1);
        }
    }

    private class StoreFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<Store> foundStores = new ArrayList<>();

            if (charSequence != null) {
                for (Store store : stores) {
                    if (store.getName().toLowerCase().contains(
                            charSequence.toString().toLowerCase())) {
                        foundStores.add(store);
                    }
                }
            }

            results.values = foundStores;
            results.count = foundStores.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults != null && filterResults.count > 0) {
                if (filterResults.values instanceof List) {
                    filteredStores.clear();
                    for (Object obj : (List<?>) filterResults.values) {
                        if (obj instanceof Store)
                            filteredStores.add((Store) obj);
                    }
                }
            }
        }
    }
}
