package com.sinergiinformatika.sisicrm.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.SurveyActivity;
import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.db.tables.ComplainTable;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyComplainFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_READ_ONLY = "read_only";
    private static final String ARG_COMPLAINS = "complains";
    private static final String ARG_NOTES = "notes";
    private static final String TAG = SurveyComplainFragment.class.getSimpleName();

    private boolean mReadOnly;
    private Context context;
    private LinearLayout rowHolder;
    private Map<String, ItemNote> complainNotes;
    private Multimap<String, ItemComplain> complainMap;
    private List<ItemComplain> mComplains;
    private LabelValueAdapter productAdapter;


    public SurveyComplainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemComplains List of complain programs.
     * @param readOnly      Flag whether this is an input view or detail view.
     * @return A new instance of fragment SurveyComplainFragment.
     */
    public static SurveyComplainFragment newInstance(
            ArrayList<ItemComplain> itemComplains, ArrayList<ItemNote> itemNotes, boolean readOnly) {
        SurveyComplainFragment fragment = new SurveyComplainFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_READ_ONLY, readOnly);
        args.putParcelableArrayList(ARG_COMPLAINS, itemComplains);
        args.putParcelableArrayList(ARG_NOTES, itemNotes);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;

        ProductTable productTable = new ProductTable(activity);
        List<ItemPrice> productList = productTable.getAll(
                ProductTable.COLUMN_IS_COMPETITOR + "= 0", null);
        List<LabelValue> products = new ArrayList<>();

        for (ItemPrice product : productList) {
            products.add(new LabelValue(product.getProductId(), product.getProductName()));
        }

        productAdapter = new LabelValueAdapter(activity, 0, products);

        ComplainTable complainTable = new ComplainTable(activity);
        mComplains = complainTable.getAll(null, null);
        for (ItemComplain complain : mComplains) {
            Log.d(TAG, String.format("complain: %s | %s", complain.getComplainId(), complain.getComplain()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        complainMap = HashMultimap.create();
        complainNotes = new HashMap<>();

        if (getArguments() != null) {
            mReadOnly = getArguments().getBoolean(ARG_READ_ONLY);
            List<ItemComplain> tempComplains = getArguments().getParcelableArrayList(ARG_COMPLAINS);
            List<ItemNote> tempNotes = getArguments().getParcelableArrayList(ARG_NOTES);

            for (ItemComplain item : tempComplains) {
                complainMap.put(item.getProductId(), item);
            }

            for (ItemNote item : tempNotes) {
                complainNotes.put(item.getProductId(), item);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_complaints, container, false);

        rowHolder = (LinearLayout) view.findViewById(R.id.survey_complain_row_holder);
        Button resetBtn = (Button) view.findViewById(R.id.survey_complain_reset_btn);
        resetBtn.setOnClickListener(this);

        if (mReadOnly) {
            resetBtn.setVisibility(View.INVISIBLE);
        } else {
            addItemRow();
        }

        return view;
    }

    @Override
    public void onResume() {
        updateState();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.survey_complain_reset_btn) {
            resetForm();
        }
    }

    public void addItemRow() {
        if (Constants.DEBUG) Log.d(TAG, "adding initial item");
        View child = LayoutInflater.from(context).inflate(R.layout.row_survey_toggle, rowHolder, false);
        setupRow(child, null, mComplains);
        rowHolder.addView(child);
    }

    public void addItemRow(String productId, List<ItemComplain> complains) {
        if (Constants.DEBUG) Log.d(TAG, "adding item for: " + productId);
        View child = LayoutInflater.from(context).inflate(R.layout.row_survey_toggle, rowHolder, false);
        setupRow(child, productId, complains);
        rowHolder.addView(child, 0);
    }

    public void setupRow(View child, String productId, List<ItemComplain> complains) {
        if (Constants.DEBUG) Log.d(TAG, "setting up row for: " + productId);

        Spinner productSpinner = (Spinner) child.findViewById(R.id.row_toggle_spinner);
        TextView productText = (TextView) child.findViewById(R.id.row_toggle_product_text);
        Button addBtn = (Button) child.findViewById(R.id.row_toggle_add_button);
        View itemParent = child.findViewById(R.id.row_toggle_item_parent);
        LinearLayout itemHolder = (LinearLayout) child.findViewById(R.id.row_toggle_item_holder);
        EditText noteEdit = (EditText) child.findViewById(R.id.row_toggle_notes_edit);
        TextView noteText = (TextView) child.findViewById(R.id.row_toggle_notes_text);

        productSpinner.setAdapter(productAdapter);
        addBtn.setTag(R.string.tag_action, R.string.action_complain);

        if (context instanceof SurveyActivity) {
            addBtn.setOnClickListener((SurveyActivity) context);
        } else {
            addBtn.setVisibility(View.GONE);
        }

        for (ItemComplain complain : complains) {
            if (Constants.DEBUG)
                Log.d(TAG,
                        String.format("setting up toggle button '%s' status %d",
                                complain.getComplain(), complain.getChecked()));

            View item = LayoutInflater.from(context).inflate(R.layout.row_toggle_item, itemHolder, false);
            TextView complainIdText = (TextView) item.findViewById(R.id.row_item_id);
            TextView complainText = (TextView) item.findViewById(R.id.row_item_text);
            ToggleButton toggleButton = (ToggleButton) item.findViewById(R.id.row_item_check);

            complainIdText.setText(complain.getComplainId());
            complainText.setText(complain.getComplain());

            if (complain.getChecked() == Constants.FLAG_TRUE) {
                toggleButton.setChecked(true);
            } else {
                toggleButton.setChecked(false);
            }
            toggleButton.invalidate();

            if (mReadOnly) {
                toggleButton.setEnabled(false);
            } else {
                toggleButton.setEnabled(true);
            }

            itemHolder.addView(item);
        }

        if (productId != null) {
            int index = productAdapter.getPositionFromItemId(productId);
            if (index > -1) {
                if (Constants.DEBUG)
                    Log.d(TAG, "product name: " + productAdapter.getItem(index).getLabel());

                productSpinner.setSelection(index);
                productText.setText(productAdapter.getItem(index).getLabel());
            }

            productSpinner.setVisibility(View.GONE);
            productText.setVisibility(View.VISIBLE);
            addBtn.setText(R.string.icon_delete);
            addBtn.setTag(context.getString(R.string.tag_remove));
            itemParent.setVisibility(View.VISIBLE);

            if (complainNotes.containsKey(productId)) {
                String note = complainNotes.get(productId).getNote();
                noteEdit.setText(note);
                noteText.setText(context.getString(R.string.label_notes) + ": " + note);
            }
        } else {
            productSpinner.setVisibility(View.VISIBLE);
            productText.setVisibility(View.GONE);
            addBtn.setText(R.string.icon_add);
            addBtn.setTag(context.getString(R.string.tag_add));
            itemParent.setVisibility(View.GONE);
        }

        if (!mReadOnly) {
            noteEdit.setVisibility(View.VISIBLE);
            noteText.setVisibility(View.GONE);
        } else {
            noteEdit.setVisibility(View.GONE);
            noteText.setVisibility(View.VISIBLE);
        }
    }

    public void removeItemRow(View child) {
        Spinner productSpinner = (Spinner) child.findViewById(R.id.row_toggle_spinner);
        String productId = ((LabelValue) productSpinner.getSelectedItem()).getValue();
        complainMap.removeAll(productId);
        complainNotes.remove(productId);
        rowHolder.removeView(child);
    }

    public Map<String, ItemNote> getComplainNotes() {
        for (int i = 0; i < rowHolder.getChildCount(); i++) {
            View rowView = rowHolder.getChildAt(i);
            Button addBtn = (Button) rowView.findViewById(R.id.row_toggle_add_button);

            if (addBtn.getTag().equals(context.getString(R.string.tag_add)))
                continue;

            Spinner productSpinner = (Spinner) rowView.findViewById(R.id.row_toggle_spinner);
            LabelValue selectedItem = (LabelValue) productSpinner.getSelectedItem();
            String productId = selectedItem.getValue();
            EditText noteEdit = (EditText) rowView.findViewById(R.id.row_toggle_notes_edit);
            String note = noteEdit.getText().toString();

            if (complainNotes.containsKey(productId)) {
                complainNotes.remove(productId);
            }

            complainNotes.put(productId, new ItemNote(productId, Constants.NOTE_TYPE_COMPLAIN, note));
        }

        return complainNotes;
    }

    public void setComplainNotes(Map<String, ItemNote> complainNotes) {
        if (complainNotes != null) {
            this.complainNotes = complainNotes;
        } else {
            this.complainNotes = new HashMap<>();
        }
    }

    public Multimap<String, ItemComplain> getComplains() {
        if (Constants.DEBUG)
            Log.d(TAG, "complain map size: " + complainMap.size());

        for (int i = 0; i < rowHolder.getChildCount(); i++) {
            View rowView = rowHolder.getChildAt(i);
            Button addBtn = (Button) rowView.findViewById(R.id.row_toggle_add_button);

            if (addBtn.getTag().equals(context.getString(R.string.tag_add)))
                continue;

            Spinner productSpinner = (Spinner) rowView.findViewById(R.id.row_toggle_spinner);
            LinearLayout itemHolder = (LinearLayout) rowView.findViewById(R.id.row_toggle_item_holder);
            LabelValue selectedItem = (LabelValue) productSpinner.getSelectedItem();
            String productId = selectedItem.getValue();
            String productName = selectedItem.getLabel();

            if (complainMap.containsKey(productId)) {
                complainMap.removeAll(productId);
            }

            for (int j = 0; j < itemHolder.getChildCount(); j++) {
                View itemView = itemHolder.getChildAt(j);
                TextView itemId = (TextView) itemView.findViewById(R.id.row_item_id);
                TextView itemName = (TextView) itemView.findViewById(R.id.row_item_text);
                ToggleButton itemValue = (ToggleButton) itemView.findViewById(R.id.row_item_check);
                int value = (itemValue.isChecked() ? Constants.FLAG_TRUE : Constants.FLAG_FALSE);
                ItemComplain item = new ItemComplain(productId, productName,
                        itemId.getText().toString(), itemName.getText().toString(), value);

                if (Constants.DEBUG)
                    Log.d(TAG, String.format("complain value for %s --> %d",
                            item.getComplain(), item.getChecked()));

                complainMap.put(productId, item);
            }
        }

        if (Constants.DEBUG)
            Log.d(TAG, "complain map size: " + complainMap.size());

        return complainMap;
    }

    public void setComplainMap(Multimap<String, ItemComplain> complainMap) {
        if (complainMap != null) {
            this.complainMap = complainMap;
        } else {
            this.complainMap = HashMultimap.create();
        }
    }

    public void updateState() {
        if (complainMap != null) {
            if (Constants.DEBUG)
                Log.d(TAG + ".updateState", "complain map size: " + complainMap.size());
            for (String key : complainMap.keySet()) {
                if (!TextUtils.isEmpty(key))
                    addItemRow(key, new ArrayList<>(complainMap.get(key)));
            }
        } else {
            if (Constants.DEBUG) Log.d(TAG + ".updateState", "complain map is null");
        }
    }

    public void resetForm() {
        for (int i = rowHolder.getChildCount() - 1; i >= 0; i--) {
            View rowView = rowHolder.getChildAt(i);
            View addBtn = rowView.findViewById(R.id.row_toggle_add_button);

            if (addBtn.getTag().equals(context.getString(R.string.tag_remove)))
                rowHolder.removeViewAt(i);
        }

        complainMap.clear();
        complainNotes.clear();
    }
}
