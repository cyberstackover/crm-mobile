package com.sinergiinformatika.sisicrm.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.OrderActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.DistributorArrayAdapter;
import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;
import com.sinergiinformatika.sisicrm.dialogs.DatePickerFragment;
import com.sinergiinformatika.sisicrm.dialogs.LoadingDialog;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private static final String TAG = OrderFragment.class.getSimpleName();
    private static final String ARG_STORE_NAME = "store_name";

    public LinearLayout itemHolder;

    private String distributorId;
    private String distributorName;
    private Date orderDate;
    private List<LabelValue> products;
    private List<Distributor> distributors;
    private Context context;
    private TextView dateText;
    private LoadingDialog loading;
    private ProductTable productTable;

    private DistributorArrayAdapter adapter;

    public OrderFragment() {
        // Required empty public constructor
        products = new ArrayList<>();
    }

    public static OrderFragment newInstance(String storeName) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();

        args.putString(ARG_STORE_NAME, storeName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        distributors = ((OrderActivity) context).getDistributors();
        productTable = new ProductTable(this.context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderDate = Calendar.getInstance().getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String storeName = getArguments().getString(ARG_STORE_NAME);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        loading = new LoadingDialog();
        itemHolder = (LinearLayout) view.findViewById(R.id.order_item_holder);
        dateText = (TextView) view.findViewById(R.id.order_date);
        Spinner mProductNameEdit = (Spinner) view.findViewById(R.id.row_order_name_edit);

        List<ItemPrice> productsList = productTable.getAll(
                ProductTable.COLUMN_IS_COMPETITOR + " = 0", null);

        dateText.setText(String.format("%s %s", getString(R.string.icon_calendar),
                                       DateUtil.formatDayDate(orderDate)));
        dateText.setOnClickListener(this);

        products = new ArrayList<>();
        for (ItemPrice p : productsList) {
            products.add(new LabelValue(p.getProductId(), p.getProductName()));
        }

        if (products.size() == 0) {
            Toast.makeText(context, R.string.error_no_product_found, Toast.LENGTH_LONG).show();
        }

        LabelValueAdapter productAdapter = new LabelValueAdapter(context, 0, products);
        mProductNameEdit.setAdapter(productAdapter);

        if (distributors == null)
            distributors = ((OrderActivity) context).getDistributors();

        adapter = new DistributorArrayAdapter(context, 0, 0, distributors);
        Spinner distributorSpinner = (Spinner) view.findViewById(R.id.order_distributor_spinner);
        TextView storeNameText = (TextView) view.findViewById(R.id.order_store_name);

        if (storeName != null)
            storeNameText.setText(storeName);

        distributorSpinner.setAdapter(adapter);
        distributorSpinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Distributor distributor = adapter.getItem(i);
        if(distributor != null){
            distributorId = distributor.getId();
            distributorName = distributor.getName();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        distributorId = null;
    }

    public List<LabelValue> getProducts() {
        return products;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void showLoading() {
        loading.setCancelable(false);
        loading.show(getChildFragmentManager(), "loading");
    }

    public void hideLoading() {
        loading.dismiss();
    }

    public Date getOrderDate() {
        return orderDate;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.order_date) {
            showDatePicker();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        orderDate = cal.getTime();
        String selectedDate = DateUtil.formatDayDate(orderDate);
        WidgetUtil.setValue(dateText, getString(R.string.icon_calendar) + " " + selectedDate);
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(
                this, Calendar.getInstance().getTime(), null);

        if (orderDate != null)
            datePickerFragment.setDate(orderDate);

        datePickerFragment.setCancelable(true);
        datePickerFragment.show(
                getChildFragmentManager(), getString(R.string.tag_date_picker));
    }
}
