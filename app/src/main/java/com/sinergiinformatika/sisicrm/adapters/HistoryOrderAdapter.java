package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wendi on 05-Jan-15.
 */
public class HistoryOrderAdapter extends ArrayAdapter<Order> {

    private static final String TAG = HistoryOrderAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private List<Order> orders;
    private List<Order> groupOrders;
    private Context context;

    public HistoryOrderAdapter(Context context,
                               int resource,
                               List<Order> orders) {

        super(context, resource, orders);
        this.context = context;
        this.orders = orders;
        grouping();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        if (groupOrders != null) {
            return groupOrders.size();
        }
        return 0;
    }

    @Override
    public Order getItem(int position) {
        return groupOrders.get(position);
    }

    @Override
    public int getPosition(Order item) {
        return groupOrders.indexOf(item);
    }

    @Override
    public void add(Order item) {
        orders.add(item);

    }

    @Override
    public void addAll(Collection<? extends Order> collection) {
        orders.addAll(collection);
    }

    @Override
    public void clear() {
        orders = new ArrayList<Order>();
        groupOrders = new ArrayList<Order>();
        //super.clear();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Order o = getItem(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            ViewHolder viewHolder = new ViewHolder();

            if (itemViewType == VIEW_TYPE_HEADER) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_list_header, parent, false);

                viewHolder.headerView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.isHeader = true;
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);

                viewHolder.storeNameView = (TextView) convertView.findViewById(R.id.row_history_store_name);
                viewHolder.listIconView = (TextView) convertView.findViewById(R.id.row_history_list_icon);
                viewHolder.imageIconView = (TextView) convertView.findViewById(R.id.row_history_image_icon);
                viewHolder.complainIconView = (TextView) convertView.findViewById(R.id.row_history_comment_icon);
                viewHolder.historyDateView = (TextView) convertView.findViewById(R.id.row_history_date);
                viewHolder.isHeader = false;
            }

            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (itemViewType == VIEW_TYPE_HEADER) {
            viewHolder.headerView.setText(o.getOrderDateInDDMMMMYYYY());
        } else {
            viewHolder.storeNameView.setText(o.getStoreName());
            viewHolder.historyDateView.setText(context.getString(R.string.text_history_order_date, o.formatOrderDate(Constants.HISTORY_FORMAT_DATE)));
            viewHolder.listIconView.setVisibility(View.GONE);
            viewHolder.imageIconView.setVisibility(View.GONE);
            viewHolder.complainIconView.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void grouping() {

        groupOrders = new ArrayList<Order>(0);
        List<Order> tempOrders = new ArrayList<Order>(0);
        tempOrders.addAll(orders);
        Collections.sort(tempOrders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getOrderDate().compareTo(o1.getOrderDate());
            }
        });

        String header = "";
        for (Order o : tempOrders) {

            if (!header.equals(o.getOrderDateInYYYYMMDD())) {
                header = o.getOrderDateInYYYYMMDD();
                Order oHeader = new Order();
                oHeader.setHeader(true);
                oHeader.setOrderDate(o.getOrderDate());
                groupOrders.add(oHeader);
            }

            groupOrders.add(o);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        grouping();
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView storeNameView, imageIconView, listIconView, complainIconView, headerView, historyDateView;
        boolean isHeader;
    }
}

