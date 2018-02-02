package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wendi on 02-Jan-15.
 */
public class StoreListAdapter extends ArrayAdapter<Store> {

    private static final String TAG = StoreListAdapter.class.getSimpleName();

    private List<Store> stores;
    private Context context;
    private double currentLatitude;
    private double currentLongitude;
    private boolean showCheckInDateInsteadOfDistance;

    public StoreListAdapter(Context context,
                            int resource,
                            List<Store> stores,
                            double currentLatitude,
                            double currentLongitude) {

        super(context, resource, stores);
        this.context = context;
        this.stores = new ArrayList<>(stores);
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.showCheckInDateInsteadOfDistance = false;
    }

    public StoreListAdapter(Context context,
                            int resource,
                            List<Store> stores,
                            double currentLatitude,
                            double currentLongitude,
                            boolean showCheckInDateInsteadOfDistance) {

        super(context, resource, stores);
        this.context = context;
        this.stores = new ArrayList<>(stores);
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.showCheckInDateInsteadOfDistance = showCheckInDateInsteadOfDistance;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    @Override
    public int getCount() {
        if (stores != null) {
            return stores.size();
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }


    @Override
    public void addAll(Collection<? extends Store> collection) {
        stores.addAll(collection);
    }

    @Override
    public void clear() {
        stores = new ArrayList<>();
    }

    @Override
    public Store getItem(int position) {
        return stores.get(position);
    }

    @Override
    public int getPosition(Store item) {
        return stores.indexOf(item);
    }

    @Override
    public void add(Store store) {
        if (isEmpty()) {
            stores = new ArrayList<>();
        }
        stores.add(store);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_store, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.storeNameView = (TextView) convertView.findViewById(R.id.row_store_name);
            viewHolder.storeAddressView =
                    (TextView) convertView.findViewById(R.id.row_store_address);
            viewHolder.cityView = (TextView) convertView.findViewById(R.id.row_store_city);
            viewHolder.cpView = (TextView) convertView.findViewById(R.id.row_store_cp);
            viewHolder.storeDateView = (TextView) convertView.findViewById(R.id.row_store_date);
            viewHolder.distanceView = (TextView) convertView.findViewById(R.id.row_store_distance);
            viewHolder.storeCategoryImgView =
                    (TextView) convertView.findViewById(R.id.row_store_priority_img);
            viewHolder.headerView = (TextView) convertView.findViewById(R.id.txt_item_header);
            viewHolder.storeStatus = (TextView) convertView.findViewById(R.id.row_store_status);
            viewHolder.dataStatus = (TextView) convertView.findViewById(R.id.row_store_data_status);
            viewHolder.headerLayout = convertView.findViewById(R.id.row_list_header);
            viewHolder.agendaBtn = (Button) convertView.findViewById(R.id.row_store_agenda_btn);
            viewHolder.orderBtn = (Button) convertView.findViewById(R.id.row_store_order_btn);
            viewHolder.reuploadBtn = (Button) convertView.findViewById(R.id.row_store_reupload_btn);

            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Store s = getItem(position);

        viewHolder.agendaBtn.setTag(R.string.tag_store, s);
        viewHolder.orderBtn.setTag(R.string.tag_store, s);
        viewHolder.reuploadBtn.setTag(R.string.tag_store, s);

        if (!User.getInstance(getContext()).isAreaManager() &&
            !User.getInstance(getContext()).isSales()) {
            viewHolder.agendaBtn.setVisibility(View.GONE);
        } else {
            viewHolder.agendaBtn.setVisibility(View.VISIBLE);
        }

        if (User.getInstance(getContext()).isAreaManager()) {
            viewHolder.orderBtn.setVisibility(View.INVISIBLE);
        }

        if (!s.isHeader()) {
            viewHolder.headerLayout.setVisibility(View.GONE);
        } else {
            viewHolder.headerLayout.setVisibility(View.VISIBLE);
            viewHolder.headerView.setText(s.getHeaderName());
        }

        if (s.getCategoryCode() == 0) {
            if (s.getCategoryLabel().equals(StoreCategory.LABEL_PLATINUM.toLowerCase())) {
                s.setCategoryCode(StoreCategory.CODE_PLATINUM);
            } else if (s.getCategoryLabel().equals(StoreCategory.LABEL_GOLD.toLowerCase())) {
                s.setCategoryCode(StoreCategory.CODE_GOLD);
            } else {
                s.setCategoryCode(StoreCategory.CODE_SILVER);
            }
        }

        switch (s.getCategoryCode()) {
            case StoreCategory.CODE_PLATINUM:
                viewHolder.storeCategoryImgView.setText(R.string.icon_bookmark);
                viewHolder.storeCategoryImgView
                        .setTextColor(context.getResources().getColor(R.color.text_dark_primary));
                break;
            case StoreCategory.CODE_GOLD:
                viewHolder.storeCategoryImgView.setText(R.string.icon_bookmark);
                viewHolder.storeCategoryImgView
                        .setTextColor(context.getResources().getColor(R.color.text_yellow));
                break;
            default:
                viewHolder.storeCategoryImgView.setText(R.string.icon_bookmark_o);
                viewHolder.storeCategoryImgView
                        .setTextColor(context.getResources().getColor(R.color.text_dark_primary));
                break;
        }

        String storeStatus = s.getStatus();
        if (storeStatus.equalsIgnoreCase(Constants.STORE_STATUS_ACTIVE)) {
            viewHolder.storeStatus.setText(R.string.icon_check_circle);
            viewHolder.storeStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_active));
        } else if (storeStatus.equalsIgnoreCase(Constants.STORE_STATUS_VERIFIED)) {
            viewHolder.storeStatus.setText(R.string.icon_check_circle_o);
            viewHolder.storeStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_success));
        } else {
            viewHolder.storeStatus.setText(R.string.icon_circle_o);
            viewHolder.storeStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
        }

        String dataStatus = s.getSyncStatus();
        if (Constants.DEBUG) {
            Log.d(TAG, String.format("store: %s | status: %s", s.getName(), dataStatus));
            Log.d(TAG, String.format("create date: %s | modification date: %s", s.getCreated(),
                                     s.getModifiedDate()));
        }

        viewHolder.dataStatus.setVisibility(View.VISIBLE);
        if (dataStatus.equalsIgnoreCase(Constants.SYNC_STATUS_SENT)) {
            viewHolder.dataStatus.setText(R.string.icon_upload);
            viewHolder.dataStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_success));
            viewHolder.reuploadBtn.setVisibility(View.GONE);
        } else if (dataStatus.equalsIgnoreCase(Constants.SYNC_STATUS_FAILED)) {
            viewHolder.dataStatus.setText(R.string.icon_warning);
            viewHolder.dataStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_failed));
            viewHolder.reuploadBtn.setVisibility(View.VISIBLE);
        } else if (dataStatus.equalsIgnoreCase(Constants.SYNC_STATUS_SENDING)) {
            viewHolder.dataStatus.setText(R.string.icon_wifi);
            viewHolder.dataStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
            viewHolder.reuploadBtn.setVisibility(View.GONE);
        } else if (dataStatus.equalsIgnoreCase(Constants.SYNC_STATUS_PENDING)) {
            viewHolder.dataStatus.setText(R.string.icon_upload);
            viewHolder.dataStatus
                    .setTextColor(context.getResources().getColor(R.color.icon_status_neutral));
            viewHolder.reuploadBtn.setVisibility(View.GONE);
        } else {
            viewHolder.dataStatus.setVisibility(View.GONE);
            viewHolder.reuploadBtn.setVisibility(View.GONE);
        }

        String phoneDefault = s.getPhoneDefault();
        if (!phoneDefault.isEmpty()) {
            phoneDefault = "(" + phoneDefault + ")";
        }

        viewHolder.storeNameView.setText(s.getName());
        viewHolder.storeAddressView.setText(s.getStreet());
        viewHolder.cityView.setText(s.getCity());
//        viewHolder.cpView.setText(s.getOwnerNameAndPhone());
        viewHolder.cpView.setText(phoneDefault);
        viewHolder.storeDateView.setText(s.getDateAndMonth());

        if (showCheckInDateInsteadOfDistance) {
            Log.d(TAG, "date: " + s.getLastCheckIn());
            Log.d(TAG, "date and month: " + s.getDateAndMonth());
            Log.d(TAG, "date YYYYMMDD: " + s.getLastCheckInYYYYMMDD());
//            WidgetUtil.setValue(viewHolder.distanceView, s.getLastCheckInDDMMMMYYYY());

            if (!TextUtils.isEmpty(s.getCreated())) {
                WidgetUtil.setValue(viewHolder.distanceView,
                                    DateUtil.format(DateUtil.parse(s.getCreated(), false),
                                                    Constants.DATE_HISTORY_FORMAT));
            } else {
                viewHolder.distanceView.setText("-");
            }
        } else {
            viewHolder.distanceView
                    .setText(s.getStoreDistanceFmt(currentLatitude, currentLongitude));
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d(TAG, "data set changed");
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView storeNameView;
        TextView storeAddressView;
        TextView cityView;
        TextView cpView;
        TextView storeDateView;
        TextView distanceView;
        TextView storeCategoryImgView;
        TextView headerView;
        TextView storeStatus;
        TextView dataStatus;
        Button orderBtn;
        Button agendaBtn;
        Button reuploadBtn;
        View headerLayout;
    }

}
