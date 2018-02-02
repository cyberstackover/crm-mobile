package com.sinergiinformatika.sisicrm.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.ItemPriceAdapter;
import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyPriceFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SurveyPriceFragment.class.getSimpleName();
    public ListView mPriceListView;
    public ItemPriceAdapter mItemPriceAdapter;
    public LabelValueAdapter productAdapter;
    public LabelValueAdapter packageAdapter;
    private Context context;
    private boolean mReadOnly;
    private List<ItemPrice> mPrices;
    private List<ItemPrice> mProducts;
    private View mRootView;
    private Button mBtnAdd;
    private LinearLayout mItemPriceHolder;
    private LinearLayout mRowPriceHolder;
    private Spinner mCementSizeView;
    private Spinner mProductNameEditView;
    private ArrayAdapter<String> mUnitAdapter;
    private ArrayAdapter<String> productPackageAdapter;
    private ProductTable mProductTable;
    private Button mBtnResetView;

    public SurveyPriceFragment() {
        // Required empty public constructor
    }

    public static SurveyPriceFragment newInstance(List<ItemPrice> prices, boolean readOnly) {
        SurveyPriceFragment fragment = new SurveyPriceFragment();
        Bundle args = new Bundle();

        if (prices != null)
            args.putParcelableArrayList(Constants.ARG_DATA, new ArrayList<Parcelable>(prices));
        args.putBoolean(Constants.ARG_READ_ONLY, readOnly);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if (args != null) {
            mReadOnly = args.getBoolean(Constants.ARG_READ_ONLY);
            mPrices = args.getParcelableArrayList(Constants.ARG_DATA);
        }

        if (mProductTable == null) {
            mProductTable = new ProductTable(this.context);
            mProducts = mProductTable.getAll(null, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_survey_price, container, false);

        mRowPriceHolder = (LinearLayout) mRootView.findViewById(R.id.row_price_edit_holder);
        mBtnResetView = (Button) mRootView.findViewById(R.id.survey_price_reset_btn);

        if (mReadOnly) {
            mRowPriceHolder.setVisibility(View.GONE);
            mBtnResetView.setVisibility(View.INVISIBLE);
        } else {
            mBtnResetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetForm();
                }
            });
        }

        mItemPriceHolder = (LinearLayout) mRootView.findViewById(R.id.item_price_holder);
        mProductNameEditView = (Spinner) mRootView.findViewById(R.id.row_cement_name);
        mCementSizeView = (Spinner) mRootView.findViewById(R.id.row_cement_size);

        List<LabelValue> labelValues = new ArrayList<>();
        for (ItemPrice p : mProducts) {
            labelValues.add(new LabelValue(p.getProductId(), p.getProductName()));
        }

        productAdapter = new LabelValueAdapter(context, 0, labelValues);
        mProductNameEditView.setAdapter(productAdapter);

        labelValues = new ArrayList<>();
        labelValues.add(new LabelValue(Constants.PRODUCT_PACKAGE_40));
        labelValues.add(new LabelValue(Constants.PRODUCT_PACKAGE_50));
        packageAdapter = new LabelValueAdapter(context, 0, labelValues);
        mCementSizeView.setAdapter(packageAdapter);


        //TODO masih di-hardcode
        List<String> units = new ArrayList<>();
        units.add("Zak");
        units.add("Ton");

        mUnitAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, units);
        mUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBtnAdd = (Button) mRootView.findViewById(R.id.btn_add_price);
        mBtnAdd.setOnClickListener(this);

        addAllToContainer();

        return mRootView;
    }

    private void resetForm() {
        mItemPriceHolder.removeAllViews();
    }

    public void addToContainer(ItemPrice price) {

        final View mItemPriceView = LayoutInflater.from(context).inflate(R.layout.row_survey_price, mItemPriceHolder, false);

        TextView mProductNameView = (TextView) mItemPriceView.findViewById(R.id.row_price_name);
        Button mBtnDelete = (Button) mItemPriceView.findViewById(R.id.row_price_delete_btn);

        View v1 = mItemPriceView.findViewById(R.id.row_price_field);
        View v2 = mItemPriceView.findViewById(R.id.row_price_field2);

        v1.setVisibility(mReadOnly ? View.GONE : View.VISIBLE);
        v2.setVisibility(mReadOnly ? View.VISIBLE : View.GONE);

        if (!mReadOnly) {
            LabelValue lv = ((LabelValue) mProductNameEditView.getSelectedItem());
            if (price.getProductName() == null || price.getProductName().trim().length() == 0) {
                price.setProductName(lv.getLabel());
            }

            if (price.getProductId() == null || price.getProductId().trim().length() == 0) {
                price.setProductId(lv.getValue());
            }

            ItemPrice product = mProductTable.getByProductId(price.getProductId());
//            String productPackage = product.getProductWeight();
            price.setProductPackage(product.getProductWeight());
            price.setProductWeight(product.getProductWeight());

            TextView mProductIdView = (TextView) mItemPriceView.findViewById(R.id.row_product_id);
            TextView mSizeView = (TextView) mItemPriceView.findViewById(R.id.row_price_size);
            EditText mPriceView = (EditText) mItemPriceView.findViewById(R.id.row_price_edit);
            EditText mPurchasePriceView = (EditText) mItemPriceView.findViewById(R.id.row_buy_price_edit);
            EditText mTOPView = (EditText) mItemPriceView.findViewById(R.id.row_price_top_edit);
            mTOPView.setVisibility(View.VISIBLE);
            Spinner mTOPSpinner = (Spinner) mItemPriceView.findViewById(R.id.spinner_price_top_edit);
            mTOPSpinner.setVisibility(View.GONE);
            EditText mVolView = (EditText) mItemPriceView.findViewById(R.id.row_price_vol_edit);
            Spinner mVolUnitView = (Spinner) mItemPriceView.findViewById(R.id.row_price_vol_unit_spinner);
            EditText mStockView = (EditText) mItemPriceView.findViewById(R.id.row_price_stock_edit);
            mVolUnitView.setAdapter(mUnitAdapter);
            Spinner mStockUnitView = (Spinner) mItemPriceView.findViewById(R.id.row_price_stock_unit_spinner);
            mStockUnitView.setAdapter(mUnitAdapter);
            mBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemPriceHolder.removeView(mItemPriceView);
                }
            });

            mProductIdView.setText(price.getProductId());
            mSizeView.setText(price.getProductPackage());

            if (price.getPrice() > 0) {
                mPriceView.setText(price.getPriceFmt());
            }

            if (price.getPricePurchase() > 0) {
                mPurchasePriceView.setText(price.getPurchasePriceFmt());
            }

            if (price.getTermOfPayment() > 0) {
                mTOPView.setText(String.valueOf(price.getTermOfPayment()));

                /*ArrayAdapter adapter = (ArrayAdapter) mTOPSpinner.getAdapter();
                for (int position = 0; position < adapter.getCount(); position++) {
                    int val = -1;

                    try {
                        System.out.println(adapter.getItem(position).toString() + " == " + price.getTermOfPayment());
                        val = Integer.parseInt(adapter.getItem(position).toString());
                    } catch (NumberFormatException e) {

                    }

                    if (val == price.getTermOfPayment()) {
                        mTOPSpinner.setSelection(position);
                        break;
                    }
                }*/
            }

            if (price.getVolume() > 0) {
                mVolView.setText(price.getVolumeFmt());
            }

            if (price.getStock() > 0) {
                mStockView.setText(price.getStockFmt());
            }

            if (price.getVolumenUnit() != null && Constants.UNIT_TON.equals(price.getVolumenUnit().toLowerCase())) {
                mVolUnitView.setSelection(1);//0 = sak, 1 = ton
            }

            if (price.getStockUnit() != null && Constants.UNIT_TON.equals(price.getStockUnit().toLowerCase())) {
                mStockUnitView.setSelection(1);//0 = sak, 1 = ton
            }

        } else {
            mBtnDelete.setVisibility(View.GONE);

            TextView mPriceView = (TextView) mItemPriceView.findViewById(R.id.row_price_view);
            TextView mPurchasePriceView = (TextView) mItemPriceView.findViewById(R.id.row_buy_price_view);
            TextView mVolumeView = (TextView) mItemPriceView.findViewById(R.id.row_price_volume_view);
            TextView mStockView = (TextView) mItemPriceView.findViewById(R.id.row_stock_view);
            TextView mTOPView = (TextView) mItemPriceView.findViewById(R.id.row_price_top_view);
            Spinner mTOPSpinner = (Spinner) mItemPriceView.findViewById(R.id.spinner_price_top_edit);
            mTOPSpinner.setVisibility(View.GONE);

            mPriceView.setText(price.getPriceFmt());
            mVolumeView.setText(price.getVolumeFmt());
            mStockView.setText(price.getStockFmt());

            if (price.getPricePurchase() > 0) {
                mPurchasePriceView.setText(price.getPurchasePriceFmt());
            } else {
                mPurchasePriceView.setText("-");
            }

            if (price.getTermOfPayment() > 0) {
                mTOPView.setText(String.valueOf(price.getTermOfPayment()));
            } else {
                mTOPView.setText("-");
            }
        }

        mProductNameView.setText(price.getProductName());

        mItemPriceHolder.addView(mItemPriceView);

    }

    @Override
    public void onClick(View v) {
        addToContainer(new ItemPrice());
    }

    public List<ItemPrice> getPrices() {

        if (!mReadOnly) {
            List<ItemPrice> prices = new ArrayList<ItemPrice>();

            if (mItemPriceHolder == null) {
                return prices;
            }

            for (int i = 0; i < mItemPriceHolder.getChildCount(); i++) {

                View mItemPriceView = mItemPriceHolder.getChildAt(i);

                TextView mProductNameView = (TextView) mItemPriceView.findViewById(R.id.row_price_name);
                TextView mProductIdView = (TextView) mItemPriceView.findViewById(R.id.row_product_id);
                TextView mSizeView = (TextView) mItemPriceView.findViewById(R.id.row_price_size);
                EditText mPriceView = (EditText) mItemPriceView.findViewById(R.id.row_price_edit);
                EditText mPricePurchaseEdit = (EditText) mItemPriceView.findViewById(R.id.row_buy_price_edit);
                EditText mTOPEdit = (EditText) mItemPriceView.findViewById(R.id.row_price_top_edit);
                Spinner mTOPSpinner = (Spinner) mItemPriceView.findViewById(R.id.spinner_price_top_edit);
                EditText mVolView = (EditText) mItemPriceView.findViewById(R.id.row_price_vol_edit);
                Spinner mVolUnitView = (Spinner) mItemPriceView.findViewById(R.id.row_price_vol_unit_spinner);
                EditText mStockView = (EditText) mItemPriceView.findViewById(R.id.row_price_stock_edit);
                Spinner mStockUnitView = (Spinner) mItemPriceView.findViewById(R.id.row_price_stock_unit_spinner);

                String productName = WidgetUtil.getValue(mProductNameView);
                String productId = WidgetUtil.getValue(mProductIdView);
                String productPackage = WidgetUtil.getValue(mSizeView);

                double price = -1;
                try {
                    price = Double.parseDouble(WidgetUtil.getValue(mPriceView));
                } catch (NumberFormatException e) {
                    if (Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
                }

                double purchasePrice = -1;
                try {
                    purchasePrice = Double.parseDouble(WidgetUtil.getValue(mPricePurchaseEdit));
                } catch (NumberFormatException e) {
                    if (Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
                }

                int termOfPayment = -1;
                try {
                    termOfPayment = Integer.parseInt(WidgetUtil.getValue(mTOPEdit));
                    //termOfPayment = Integer.parseInt(mTOPSpinner.getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    if (Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
                }

                double vol = -1;
                try {
                    vol = Double.parseDouble(WidgetUtil.getValue(mVolView));
                } catch (NumberFormatException e) {
                    if (Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
                }

                double stock = -1;
                try {
                    stock = Double.parseDouble(WidgetUtil.getValue(mStockView));
                } catch (NumberFormatException e) {
                    if (Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
                }

                /*String volUnit = ((LabelValue) mVolUnitView.getSelectedItem()).getValue();
                String stockUnit = ((LabelValue) mStockUnitView.getSelectedItem()).getValue();*/

                String volUnit = Constants.UNIT_SAK;
                if (Constants.UNIT_TON.equalsIgnoreCase(mVolUnitView.getSelectedItem().toString())) {
                    volUnit = Constants.UNIT_TON;
                }

                String stockUnit = Constants.UNIT_SAK;
                if (Constants.UNIT_TON.equalsIgnoreCase(mStockUnitView.getSelectedItem().toString())) {
                    stockUnit = Constants.UNIT_TON;
                }

                ItemPrice p = new ItemPrice();
                p.setProductName(productName);
                p.setProductId(productId);
                p.setProductPackage(productPackage);
                p.setProductWeight(productPackage);
                p.setPrice(price);
                p.setPricePurchase(purchasePrice);
                p.setVolume(vol);
                p.setStock(stock);
                p.setVolumenUnit(volUnit);
                p.setStockUnit(stockUnit);
                p.setTermOfPayment(termOfPayment);

                prices.add(p);
            }

            return prices;
        }

        return mPrices;
    }

    public void addPrice(ItemPrice price) {
        if (mPrices == null) {
            mPrices = new ArrayList<ItemPrice>();
        }
        mPrices.add(price);
    }

    public void addAllToContainer() {
        if (mPrices != null) {
            for (ItemPrice im : mPrices) {
                addToContainer(im);
            }
        }
    }

    public int getChildCount() {
        if (mItemPriceHolder != null) {
            return mItemPriceHolder.getChildCount();
        }
        return -1;
    }
}
