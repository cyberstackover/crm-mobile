package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.db.tables.OrderTable;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.fragments.OrderFragment;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderActivity extends FragmentActivity {

    private static final String TAG = OrderActivity.class.getSimpleName();
    private List<Distributor> distributors;
    private Store store;
    private OrderFragment fragment;
    private RestResponseHandler postHandler = new RestResponseHandler(this) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if (Constants.DEBUG) Log.d(TAG, response.toString(4));
                if (isSuccess(response)) {
                    Toast.makeText(
                            OrderActivity.this,
                            R.string.message_save_success,
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(
                            OrderActivity.this,
                            R.string.message_save_failed,
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e(TAG, "error parsing json: " + e.getMessage(), e);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            fragment.showLoading();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            fragment.hideLoading();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);
        setContentView(R.layout.activity_order);

        store = getIntent().getParcelableExtra(Constants.EXTRA_STORE);

        Log.d(TAG, "store.getId() = " + store.getId());

        fragment = OrderFragment.newInstance(store.getName());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        String userRole = User.getInstance(this).getRoleName();

        if (userRole.equalsIgnoreCase(Constants.ROLE_NAME_SALES)) {
            String distributorId = User.getInstance(this).getDistributorId();
            String distributor = User.getInstance(this).getDistributor();
            distributors = new ArrayList<>();

            if (!distributorId.isEmpty() && !distributor.isEmpty()) {
                distributors.add(new Distributor(distributorId, distributor));
            } else {
                Toast.makeText(
                        this,
                        getString(R.string.error_distributor_list),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            getDistributorsFromDB();
        }

        if (fragment.isVisible()) {
            Button saveBtn = (Button) findViewById(R.id.order_save_button);
            if (distributors == null || distributors.size() == 0) {
                saveBtn.setEnabled(false);
            }
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void order(View view) {
        if (distributors.isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.error_distributor_list),
                    Toast.LENGTH_LONG).show();
            return;
        }

        String distributorId = fragment.getDistributorId();
        String distributorName = fragment.getDistributorName();
        Date orderDate = fragment.getOrderDate();
        List<ItemPrice> orders = new ArrayList<>();

        int childCount = fragment.itemHolder.getChildCount();

        //childCount - 1, karna field amount yang terakhir untuk form input awal


        for (int i = 0; i < childCount; i++) {
            View child = fragment.itemHolder.getChildAt(i);
            Spinner productSpinner = (Spinner) child.findViewById(R.id.row_order_name_edit);
            EditText productAmount = (EditText) child.findViewById(R.id.row_order_amount_edit);
            LabelValue selectedProduct = (LabelValue) productSpinner.getSelectedItem();
            ItemPrice order = new ItemPrice();
            View focusView = null;

            boolean ok = true;

            if (WidgetUtil.getValue(productAmount).isEmpty()) {
                ok = false;
                //productAmount.setError(getString(R.string.error_field_required));
                //focusView = productSpinner;
            }

            if (ok) {
                order.setDistributorId(distributorId);
                order.setDistributorName(distributorName);
                order.setProductId(selectedProduct.getValue());
                order.setProductName(selectedProduct.getLabel());
                order.setVolume(Integer.valueOf(WidgetUtil.getValue(productAmount)));
                orders.add(order);
            } /*else {
                focusView.requestFocus();
                return;
            }*/
        }

        if (orders.size() == 0) {
            Toast.makeText(this, getString(R.string.error_min_one), Toast.LENGTH_LONG).show();
        } else {
            if (Constants.DEBUG) Log.d(TAG, "order data count: " + orders.size());

            OrderTable orderTable = new OrderTable(getContentResolver());
            orderTable.addOrder(orderDate, store.getId(), store.getStoreId(), store.getName(), orders);
            Toast.makeText(
                    OrderActivity.this,
                    R.string.message_save_success,
                    Toast.LENGTH_SHORT).show();
            finish();
//                RestClient.getInstance(this, postHandler).postOrder(
//                        User.getInstance(this).getToken(), orderDate, store.getStoreId(), orders);
        }
    }

    public void finish(View view) {
        finish();
    }

    public void toggleOrder(View view) {
        if (Constants.DEBUG) Log.v(TAG, "button clicked");

        Button thisBtn = (Button) view;
        View parentView = (View) view.getParent().getParent();

        if (view.getTag().equals(getString(R.string.tag_add))) {
            if (parentView != null) {
                boolean ok;
                Spinner productNameEdit = (Spinner) parentView.findViewById(
                        R.id.row_order_name_edit);
                EditText productAmountEdit = (EditText) parentView.findViewById(
                        R.id.row_order_amount_edit);

                String amountStr = WidgetUtil.getValue(productAmountEdit);
                int amount = -1;
                try {
                    amount = Integer.parseInt(amountStr);
                } catch (NumberFormatException e) {
                    if (amountStr != null)
                        Log.e(TAG, e.getMessage(), e);
                }

                ok = productNameEdit.getSelectedItem() != null && amount > 0;

                if (!ok) {
                    if (amount <= 0) {
                        productAmountEdit.setError(
                                getString(
                                        R.string.error_field_must_greater_than,
                                        "",
                                        "0"));
                        productAmountEdit.requestFocus();
                    }
                    return;
                }

                TextView productName = (TextView) parentView.findViewById(R.id.row_order_name);

                view.setTag(getString(R.string.tag_remove));
                thisBtn.setText(R.string.icon_delete);
                productName.setText(((LabelValue) productNameEdit.getSelectedItem()).getLabel());
                productNameEdit.setVisibility(View.GONE);
                productName.setVisibility(View.VISIBLE);

                View child = LayoutInflater.from(this).inflate(
                        R.layout.row_order, fragment.itemHolder, false);
                Spinner textView = (Spinner) child.findViewById(R.id.row_order_name_edit);
                EditText editText = (EditText) child.findViewById(R.id.row_order_amount_edit);
                editText.requestFocus();

                textView.setAdapter(new LabelValueAdapter(this, 0, fragment.getProducts()));
                fragment.itemHolder.addView(child);
            } else {
                if (Constants.DEBUG)
                    Log.e(TAG, "button add/remove parent is null");
            }
        } else {
            if (parentView != null) {
                view.setTag(getString(R.string.tag_add));
                thisBtn.setText(R.string.icon_add);
                fragment.itemHolder.removeView(parentView);
            } else {
                if (Constants.DEBUG)
                    Log.e(TAG, "button add/remove parent is null");
            }
        }
    }

    public List<Distributor> getDistributors() {
        return distributors;
    }

    private void getDistributorsFromDB() {
        distributors = (new StoreTable(getContentResolver())).getDistributors(null, null);
        if (distributors.isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.error_distributor_list),
                    Toast.LENGTH_LONG).show();
        }
    }
}
