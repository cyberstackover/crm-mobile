package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;

/**
 * Created by wendi on 05-Jan-15.
 */
public class StoreCursorAdapter extends SimpleCursorAdapter {

    private static final String TAG = StoreCursorAdapter.class.getSimpleName();

    private Context context;

    public StoreCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_store, parent, false);

        ViewHolder holder = new ViewHolder();

        //holder.mHeaderView = (TextView)view.findViewById(R.id.txt_item_header);
        holder.mStoreNameView = (TextView) view.findViewById(R.id.row_store_name);
        holder.mStoreAddressView = (TextView) view.findViewById(R.id.row_store_address);
        holder.mCityView = (TextView) view.findViewById(R.id.row_store_city);
        holder.mCpView = (TextView) view.findViewById(R.id.row_store_cp);
        holder.mStoreDateView = (TextView) view.findViewById(R.id.row_store_date);
        holder.mDistanceView = (TextView) view.findViewById(R.id.row_store_distance);

        view.setTag(holder);
        bindView(view, context, cursor);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();
        Store s = new Store();
        StoreTable.setValues(cursor, s);

        holder.mStoreNameView.setText(s.getName());
        holder.mStoreAddressView.setText(s.getStreet());
        holder.mCityView.setText(s.getCity());
        holder.mCpView.setText(s.getOwnerNameAndPhone());
        holder.mStoreDateView.setText(s.getDateAndMonth());

    }

    private class ViewHolder {
        TextView mStoreNameView, mStoreAddressView, mCityView, mCpView, mStoreDateView, mDistanceView, mHeaderView;
    }
}
