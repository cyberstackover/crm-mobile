package com.sinergiinformatika.sisicrm.fragments;


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
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.db.tables.CompetitorTable;
import com.sinergiinformatika.sisicrm.db.tables.ProductTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurveyCompetitorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyCompetitorFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_READ_ONLY = "read_only";
    private static final String ARG_PROGRAMS = "programs";
    private static final String ARG_NOTES = "notes";
    private static final String TAG = SurveyCompetitorFragment.class.getSimpleName();

    private boolean mReadOnly;
    private Context context;
    private LinearLayout rowHolder;
    private Map<String, ItemNote> competitorNotes;
    private Multimap<String, ItemCompetitor> productProgramsMap;
    private List<ItemCompetitor> mPrograms;
    private LabelValueAdapter productAdapter;


    public SurveyCompetitorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param competitorPrograms List of competitor programs.
     * @param readOnly           Flag whether this is an input view or detail view.
     * @return A new instance of fragment SurveyCompetitorFragment.
     */
    public static SurveyCompetitorFragment newInstance(
            ArrayList<ItemCompetitor> competitorPrograms, ArrayList<ItemNote> itemNotes,
            boolean readOnly) {
        SurveyCompetitorFragment fragment = new SurveyCompetitorFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_READ_ONLY, readOnly);
        args.putParcelableArrayList(ARG_PROGRAMS, competitorPrograms);
        args.putParcelableArrayList(ARG_NOTES, itemNotes);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        ProductTable productTable = new ProductTable(context);
        List<ItemPrice> productList = productTable.getAll(
                ProductTable.COLUMN_IS_COMPETITOR + "!= 0", null);
        List<LabelValue> products = new ArrayList<>();

        for (ItemPrice product : productList) {
            products.add(new LabelValue(product.getProductId(), product.getProductName()));
        }

        productAdapter = new LabelValueAdapter(context, 0, products);

        CompetitorTable competitorTable = new CompetitorTable(context);
        mPrograms = competitorTable.getAll(null, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productProgramsMap = HashMultimap.create();
        competitorNotes = new HashMap<>();

        if (getArguments() != null) {
            mReadOnly = getArguments().getBoolean(ARG_READ_ONLY);
            List<ItemCompetitor> tempCompetitors =
                    getArguments().getParcelableArrayList(ARG_PROGRAMS);
            List<ItemNote> tempNotes = getArguments().getParcelableArrayList(ARG_NOTES);

            if (tempCompetitors != null) {
                for (ItemCompetitor item : tempCompetitors) {
                    productProgramsMap.put(item.getProductId(), item);
                }
            }

            if (tempNotes != null) {
                for (ItemNote item : tempNotes) {
                    competitorNotes.put(item.getProductId(), item);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_competitor, container, false);

        rowHolder = (LinearLayout) view.findViewById(R.id.survey_competitor_row_holder);
        Button resetBtn = (Button) view.findViewById(R.id.survey_competitor_reset_btn);
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
        if (id == R.id.survey_competitor_reset_btn) {
            resetForm();
        }
    }

    public void addItemRow() {
        if (Constants.DEBUG) Log.d(TAG, "adding initial item");
        View child =
                LayoutInflater.from(context).inflate(R.layout.row_survey_toggle, rowHolder, false);
        setupRow(child, null, mPrograms);
        rowHolder.addView(child);
    }

    public void addItemRow(String productId, List<ItemCompetitor> mPrograms) {
        if (Constants.DEBUG) Log.d(TAG, "adding item for: " + productId);
        View child =
                LayoutInflater.from(context).inflate(R.layout.row_survey_toggle, rowHolder, false);
        setupRow(child, productId, mPrograms);
        rowHolder.addView(child, 0);
    }

    public void setupRow(View child, String productId, List<ItemCompetitor> programs) {
        if (Constants.DEBUG) Log.d(TAG, "setting up row for: " + productId);

        Spinner productSpinner = (Spinner) child.findViewById(R.id.row_toggle_spinner);
        TextView productText = (TextView) child.findViewById(R.id.row_toggle_product_text);
        Button addBtn = (Button) child.findViewById(R.id.row_toggle_add_button);
        View itemParent = child.findViewById(R.id.row_toggle_item_parent);
        LinearLayout itemHolder = (LinearLayout) child.findViewById(R.id.row_toggle_item_holder);
        EditText noteEdit = (EditText) child.findViewById(R.id.row_toggle_notes_edit);
        TextView noteText = (TextView) child.findViewById(R.id.row_toggle_notes_text);

        productSpinner.setAdapter(productAdapter);
        addBtn.setTag(R.string.tag_action, R.string.action_competitor);

        if (context instanceof SurveyActivity) {
            addBtn.setOnClickListener((SurveyActivity) context);
        } else {
            addBtn.setVisibility(View.GONE);
        }

        for (ItemCompetitor program : programs) {
            if (Constants.DEBUG) {
                Log.d(TAG,
                      String.format("setting up toggle button '%s' status %d",
                                    program.getProgramName(), program.getChecked()));
            }

            View item = LayoutInflater.from(context)
                                      .inflate(R.layout.row_toggle_item, itemHolder, false);
            TextView programId = (TextView) item.findViewById(R.id.row_item_id);
            TextView programText = (TextView) item.findViewById(R.id.row_item_text);
            ToggleButton toggleButton = (ToggleButton) item.findViewById(R.id.row_item_check);
            EditText unitText = (EditText) item.findViewById(R.id.row_item_unit);

            programId.setText(program.getProgramId());
            programText.setText(program.getProgramName());

            if (program.getChecked() == Constants.FLAG_TRUE) {
                toggleButton.setChecked(true);
                if (Constants.DEBUG) Log.d(TAG, "checked");
            } else {
                toggleButton.setChecked(false);
                if (Constants.DEBUG) Log.d(TAG, "unchecked");
            }
            toggleButton.invalidate();

            if (!TextUtils.isEmpty(program.getUnitName())) {
                unitText.setVisibility(View.VISIBLE);
                unitText.setEnabled(true);
                unitText.setHint(program.getUnitName());
                unitText.setText(
                        TextUtils.isEmpty(program.getUnitValue()) ? "" : program.getUnitValue());
                unitText.setTag(R.string.tag_extra_id, program.getUnitName());
            } else {
                unitText.setVisibility(View.INVISIBLE);
                unitText.setText("");
                unitText.setTag(R.string.tag_extra_id, "");
            }

            if (mReadOnly) {
                toggleButton.setEnabled(false);
                unitText.setEnabled(false);
            } else {
                toggleButton.setEnabled(true);
            }

            itemHolder.addView(item);
        }

        if (productId != null) {
            int index = productAdapter.getPositionFromItemId(productId);
            if (index > -1) {
                if (Constants.DEBUG) {
                    Log.d(TAG, "product name: " + productAdapter.getItem(index).getLabel());
                }

                productSpinner.setSelection(index);
                productText.setText(productAdapter.getItem(index).getLabel());
            }

            productSpinner.setVisibility(View.GONE);
            productText.setVisibility(View.VISIBLE);
            addBtn.setText(R.string.icon_delete);
            addBtn.setTag(context.getString(R.string.tag_remove));
            itemParent.setVisibility(View.VISIBLE);

            if (competitorNotes.containsKey(productId)) {
                String note = competitorNotes.get(productId).getNote();
                noteEdit.setText(note);
                noteText.setText(
                        String.format("%s: %s", context.getString(R.string.label_notes), note));
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

        if (Constants.DEBUG) Log.d(TAG, String.format("removing '%s' from view", productId));

        productProgramsMap.removeAll(productId);
        competitorNotes.remove(productId);
        rowHolder.removeView(child);
    }

    public Multimap<String, ItemCompetitor> getProductProgramsMap() {
        if (Constants.DEBUG) {
            Log.d(TAG, "competitor programs map size: " + productProgramsMap.size());
        }

        for (int i = 0; i < rowHolder.getChildCount(); i++) {
            View rowView = rowHolder.getChildAt(i);
            Button addBtn = (Button) rowView.findViewById(R.id.row_toggle_add_button);

            if (addBtn.getTag().equals(context.getString(R.string.tag_add))) {
                continue;
            }

            Spinner productSpinner = (Spinner) rowView.findViewById(R.id.row_toggle_spinner);
            LinearLayout itemHolder =
                    (LinearLayout) rowView.findViewById(R.id.row_toggle_item_holder);
            LabelValue selectedItem = (LabelValue) productSpinner.getSelectedItem();
            String productId = selectedItem.getValue();
            String productName = selectedItem.getLabel();

            if (productProgramsMap.containsKey(productId)) {
                productProgramsMap.removeAll(productId);
            }

            for (int j = 0; j < itemHolder.getChildCount(); j++) {
                View itemView = itemHolder.getChildAt(j);
                TextView itemId = (TextView) itemView.findViewById(R.id.row_item_id);
                TextView itemName = (TextView) itemView.findViewById(R.id.row_item_text);
                ToggleButton itemValue =
                        (ToggleButton) itemView.findViewById(R.id.row_item_check);
                EditText itemUnit = (EditText) itemView.findViewById(R.id.row_item_unit);

                int value = (itemValue.isChecked() ? Constants.FLAG_TRUE : Constants.FLAG_FALSE);
                ItemCompetitor item = new ItemCompetitor(productId, productName,
                                                         itemId.getText().toString(),
                                                         itemName.getText().toString(), value);

                item.setUnitName((String) itemUnit.getTag(R.string.tag_extra_id));
                item.setUnitValue(itemUnit.getText().toString());

                if (Constants.DEBUG) {
                    Log.d(TAG, String.format("competitor program value for %s --> %d",
                                             item.getProgramName(), item.getChecked()));
                }

                productProgramsMap.put(productId, item);
            }
        }

        if (Constants.DEBUG) {
            Log.d(TAG, "competitor programs map size: " + productProgramsMap.size());
        }

        return productProgramsMap;
    }

    public void setProductProgramsMap(Multimap<String, ItemCompetitor> productProgramsMap) {
        if (productProgramsMap != null) {
            this.productProgramsMap = productProgramsMap;
        } else {
            this.productProgramsMap = HashMultimap.create();
        }
    }

    public Map<String, ItemNote> getCompetitorNotes() {
        for (int i = 0; i < rowHolder.getChildCount(); i++) {
            View rowView = rowHolder.getChildAt(i);
            Button addBtn = (Button) rowView.findViewById(R.id.row_toggle_add_button);

            if (addBtn.getTag().equals(context.getString(R.string.tag_add))) {
                continue;
            }

            Spinner productSpinner = (Spinner) rowView.findViewById(R.id.row_toggle_spinner);
            LabelValue selectedItem = (LabelValue) productSpinner.getSelectedItem();
            String productId = selectedItem.getValue();
            EditText noteEdit = (EditText) rowView.findViewById(R.id.row_toggle_notes_edit);
            String note = noteEdit.getText().toString();

            if (competitorNotes.containsKey(productId)) {
                competitorNotes.remove(productId);
            }

            if (Constants.DEBUG) {
                Log.d(TAG, String.format("adding note '%s' to %s", note, productId));
            }
            competitorNotes
                    .put(productId, new ItemNote(productId, Constants.NOTE_TYPE_COMPETITOR, note));
        }

        return competitorNotes;
    }

    public void setCompetitorNotes(Map<String, ItemNote> competitorNotes) {
        if (competitorNotes != null) {
            this.competitorNotes = competitorNotes;
        } else {
            this.competitorNotes = new HashMap<>();
        }
    }

    public void updateState() {
        if (productProgramsMap != null) {
            if (Constants.DEBUG) {
                Log.d(TAG, "competitor programs map size: " + productProgramsMap.size());
            }
            for (String key : productProgramsMap.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    addItemRow(key, new ArrayList<>(productProgramsMap.get(key)));
                }
            }
        } else {
            if (Constants.DEBUG) Log.d(TAG, "competitor programs map is null");
        }
    }

    public void resetForm() {
        if (Constants.DEBUG) Log.d(TAG, "resetting form");

        for (int i = rowHolder.getChildCount() - 1; i >= 0; i--) {
            View rowView = rowHolder.getChildAt(i);
            View addBtn = rowView.findViewById(R.id.row_toggle_add_button);

            if (addBtn.getTag().equals(context.getString(R.string.tag_remove))) {
                rowHolder.removeViewAt(i);
            }
        }

        productProgramsMap.clear();
        competitorNotes.clear();
    }
}
