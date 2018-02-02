package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wendi on 26-Feb-15.
 */
public class OrderCursorAdapter extends SimpleCursorAdapter implements Filterable {

    private static final String TAG = OrderCursorAdapter.class.getSimpleName();
    private Context context;
    private Set<String> dates = new HashSet<>();
    private Map<Long, Boolean> flagHeader = new HashMap<>();

    public OrderCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                              int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Order o = new Order();
        //OrderTable.setValues(cursor, o);
        o.setId(cursor.getLong((cursor.getColumnIndex(OrderTable.COLUMN_ID))));
        o.setOrderDate(cursor.getString((cursor.getColumnIndex(OrderTable.COLUMN_ORDER_DATE))));
        o.setStoreName(cursor.getString((cursor.getColumnIndex(OrderTable.COLUMN_STORE_NAME))));
        o.setSyncStatus(cursor.getString((cursor.getColumnIndex(OrderTable.COLUMN_SYNC_STATUS))));

        if (!dates.contains(o.getOrderDateInYYYYMMDD())) {
            dates.add(o.getOrderDateInYYYYMMDD());
            o.setHeader(true);
        }

        if (!flagHeader.containsKey(o.getId())) {
            flagHeader.put(o.getId(), o.isHeader());
        } else {
            o.setHeader(flagHeader.get(o.getId()));
        }

        //Log.d(TAG, String.format("######### %s =  %s", o.getId(), o.isHeader()));

        viewHolder.idView.setText(String.valueOf(o.getId()));
        viewHolder.storeNameView.setText(o.getStoreName());
        viewHolder.historyDateView.setText(
                context.getString(
                        R.string.text_history_order_date,
                        o.formatOrderDate(Constants.HISTORY_FORMAT_DATE)));
        viewHolder.listIconView.setVisibility(View.GONE);
        viewHolder.imageIconView.setVisibility(View.GONE);
        viewHolder.complainIconView.setVisibility(View.GONE);
        viewHolder.headerContainerView.setVisibility(View.GONE);

        viewHolder.syncStatusView.setVisibility(View.VISIBLE);
        if (Constants.SYNC_STATUS_SENT.equalsIgnoreCase(o.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_upload);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_success));
        } else if (Constants.SYNC_STATUS_FAILED.equalsIgnoreCase(o.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_warning);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_failed));
        } else if (Constants.SYNC_STATUS_SENDING.equalsIgnoreCase(o.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_wifi);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
        } else if (Constants.SYNC_STATUS_PENDING.equalsIgnoreCase(o.getSyncStatus())) {
            viewHolder.syncStatusView.setText(R.string.icon_upload);
            viewHolder.syncStatusView
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
        } else {
            viewHolder.syncStatusView.setVisibility(View.GONE);
        }

        if (o.isHeader()) {
            viewHolder.headerView.setText(o.getOrderDateInDDMMMMYYYY());
            viewHolder.headerContainerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View convertView =
                LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.idView = (TextView) convertView.findViewById(R.id.row_history_data_id);
        viewHolder.storeNameView = (TextView) convertView.findViewById(R.id.row_history_store_name);
        viewHolder.listIconView = (TextView) convertView.findViewById(R.id.row_history_list_icon);
        viewHolder.imageIconView = (TextView) convertView.findViewById(R.id.row_history_image_icon);
        viewHolder.complainIconView =
                (TextView) convertView.findViewById(R.id.row_history_comment_icon);
        viewHolder.historyDateView = (TextView) convertView.findViewById(R.id.row_history_date);
        viewHolder.syncStatusView =
                (TextView) convertView.findViewById(R.id.row_history_sync_status);

        viewHolder.isHeader = false;
        viewHolder.headerContainerView = convertView.findViewById(R.id.row_list_header);
        viewHolder.headerView =
                (TextView) viewHolder.headerContainerView.findViewById(R.id.txt_item_header);

        convertView.setTag(viewHolder);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        flagHeader.clear();
        dates.clear();
        super.notifyDataSetChanged();
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        String selection = null;
        String[] selectionArgs = null;
        if (constraint != null && constraint.toString().trim().length() > 0) {
            selection = "lower(" + OrderTable.COLUMN_STORE_NAME + ") like ? ";
            selectionArgs = new String[]{"%" + constraint.toString().toLowerCase() + "%"};
        }

        String sortOrder = OrderTable.COLUMN_ORDER_DATE + " desc";

        return context.getContentResolver()
                      .query(CRMContentProvider.URI_ORDERS, OrderTable.ALL_COLUMNS, selection,
                             selectionArgs, sortOrder);
    }

    private class ViewHolder {
        TextView storeNameView;
        TextView imageIconView;
        TextView listIconView;
        TextView complainIconView;
        TextView headerView;
        TextView historyDateView;
        TextView idView;
        TextView syncStatusView;
        View headerContainerView;
        boolean isHeader;
    }

}
