package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Order;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.dialogs.LoadingDialog;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;


public class OrderDetailActivity extends FragmentActivity {

    private static final String TAG = OrderDetailActivity.class.getSimpleName();
    private Order mOrder;
    private User mCurrentUser;
    private LinearLayout mItemHeaderHolderView;
    private TextView mStoreNameView;
    private LoadingDialog mLoading;
    private OrderTable mOrderTable;

    /*private void loadOrder(String orderId) {
        RestClient.getInstance(this, orderDetailHandler).getOrderDetail(mCurrentUser.getToken(), mOrder.getOrderId());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);
        setContentView(R.layout.activity_order_detail);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCurrentUser = User.getInstance(this);
        mOrder = getIntent().getParcelableExtra(Constants.EXTRA_ORDER);

        mLoading = new LoadingDialog();

        if (mOrder != null) {
            //loadOrder(mOrder.getOrderId());
            mStoreNameView = (TextView) findViewById(R.id.row_store_name);
            mStoreNameView.setText(mOrder.getStoreName());
            mItemHeaderHolderView = (LinearLayout) findViewById(R.id.row_item_holder);

            //for (ItemHeader<ItemPrice> itemHeader : itemHeaders) {

            View mItemHeaderView = LayoutInflater.from(OrderDetailActivity.this).inflate(
                    R.layout.row_order_detail_distributor, mItemHeaderHolderView, false);

            TextView mDistributorNameView = (TextView) mItemHeaderView.findViewById(R.id.row_item_value);
            //mDistributorNameView.setText(itemHeader.getHeaderName());
            mDistributorNameView.setText(mOrder.getDistributorName());

            TextView mDeliveryDateText = (TextView) mItemHeaderView.findViewById(R.id.row_date_value);
            WidgetUtil.setValue(mDeliveryDateText, mOrder.getDeliveryDateInEEEEDDMMYYYY());

            LinearLayout vItemDetailHolder = (LinearLayout) mItemHeaderView.findViewById(R.id.row_item_detail);

            //for (ItemPrice price : itemHeader.getItems()) {
            for (ItemPrice price : mOrder.getPrices()) {
                if (Constants.DEBUG) Log.d(TAG, "product=" + price.getProductName());
                View mItemDetailView = LayoutInflater.from(OrderDetailActivity.this).inflate(
                        R.layout.row_order_detail_product, vItemDetailHolder, false);

                TextView mProductNameView = (TextView) mItemDetailView.findViewById(R.id.row_product_name);
                TextView mQuantityView = (TextView) mItemDetailView.findViewById(R.id.row_product_quantity);
                TextView mUnitView = (TextView) mItemDetailView.findViewById(R.id.row_product_unit);

                mProductNameView.setText(price.getProductName());
                if (price.getVolume() > 0.0) {
                    mQuantityView.setText(String.valueOf(price.getVolume()));
                } else {
                    mQuantityView.setText(String.valueOf(price.getQuantity()));
                }

                mUnitView.setText(getString(R.string.label_unit_sak));

                vItemDetailHolder.addView(mItemDetailView);

            }

            mItemHeaderHolderView.addView(mItemHeaderView);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showLoading() {
        if (mLoading != null) {
            mLoading.setCancelable(false);
            mLoading.show(getSupportFragmentManager(), "loading");
        }
    }

    public void hideLoading() {
        if (mLoading != null) {
            mLoading.dismiss();
        }
    }


}
