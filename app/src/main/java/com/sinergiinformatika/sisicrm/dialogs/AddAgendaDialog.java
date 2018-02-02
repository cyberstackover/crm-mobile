package com.sinergiinformatika.sisicrm.dialogs;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.StoreNameArrayAdapter;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.db.tables.AgendaTable;
import com.sinergiinformatika.sisicrm.utils.DateUtil;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAgendaDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener,
        View.OnClickListener {

    long agendaId;
    List<Store> stores;
    Store selectedStore = null;
    Date planDate;
    Context context;
    AutoCompleteTextView storeEdit;
    TextView dateEdit;
    android.support.v4.app.Fragment parent;

    LoadingDialog loadingDialog = null;

    public AddAgendaDialog() {
        // Required empty public constructor
    }

    public static AddAgendaDialog newInstance(List<Store> stores,
                                              android.support.v4.app.Fragment parent) {
        AddAgendaDialog dialog = new AddAgendaDialog();
        dialog.stores = stores;
        dialog.parent = parent;
        return dialog;
    }

    public static AddAgendaDialog newInstance(List<Store> stores, Store store, Date date, long id,
                                              android.support.v4.app.Fragment parent) {
        AddAgendaDialog dialog = new AddAgendaDialog();
        dialog.agendaId = id;
        dialog.stores = stores;
        dialog.selectedStore = store;
        dialog.planDate = date;
        dialog.parent = parent;
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_add_agenda_dialog);
        dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;

        Button saveBtn = (Button) dialog.findViewById(R.id.add_agenda_save_btn);
        Button cancelBtn = (Button) dialog.findViewById(R.id.add_agenda_cancel_btn);
        dateEdit = (TextView) dialog.findViewById(R.id.add_agenda_date_edit);
        storeEdit = (AutoCompleteTextView) dialog.findViewById(
                R.id.add_agenda_store_edit);

        if (planDate == null) {
            planDate = Calendar.getInstance().getTime();
        }
        dateEdit.setText(DateUtil.formatDayDate(planDate));

        if (selectedStore != null) {
            storeEdit.setText(selectedStore.getName());
            storeEdit.setEnabled(false);
        }

        if (stores == null || stores.size() == 0) {
            Toast.makeText(context, R.string.message_no_store, Toast.LENGTH_LONG).show();
            saveBtn.setEnabled(false);
        }

        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        dateEdit.setOnClickListener(this);
        setupStoreAutoComplete();

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        planDate = cal.getTime();
        String selectedDate = DateUtil.formatDayDate(planDate);
        dateEdit.setText(selectedDate);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_agenda_save_btn:
                save();
                break;
            case R.id.add_agenda_cancel_btn:
                dismiss();
                break;
            case R.id.add_agenda_date_edit:
                showDatePicker();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(
                this, Calendar.getInstance().getTime(), null);

        if (planDate != null)
            datePickerFragment.date = planDate;

        datePickerFragment.setCancelable(true);
        datePickerFragment.show(
                getChildFragmentManager(), getString(R.string.tag_date_picker));
    }

    private void setupStoreAutoComplete() {
        List<Store> stores;
        if (this.stores != null) {
            stores = this.stores;
        } else {
            stores = new ArrayList<>();
        }

        final StoreNameArrayAdapter storeNameArrayAdapter = new StoreNameArrayAdapter(context, 0, stores);
        storeEdit.setAdapter(storeNameArrayAdapter);
        storeEdit.setThreshold(1);
        storeEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStore = storeNameArrayAdapter.getItem(i);
                storeEdit.setText(selectedStore.getName());
            }
        });
    }

    private void save() {
        if (formValidate()) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog();
                loadingDialog.show(getChildFragmentManager(), Constants.TAG_LOADING);
            }

            String date = DateUtil.formatDBDateOnly(planDate);
            ContentValues values = new ContentValues();
            values.put(AgendaTable.COLUMN_AGENDA_DATE, date);
            values.put(AgendaTable.COLUMN_STORE_ID, selectedStore.getId());

            if (agendaId == 0) {
                Uri uri = context.getContentResolver().insert(CRMContentProvider.URI_AGENDA, values);
                if (uri != null) {
                    Toast.makeText(context, R.string.message_save_success, Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            } else {
                Uri uri = Uri.parse(CRMContentProvider.URI_AGENDA + "/" + agendaId);
                int affected = context.getContentResolver().update(uri, values, null, null);
                if (affected == 1) {
                    Toast.makeText(context, R.string.message_save_success, Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(context, R.string.message_save_failed, Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        }
    }

    private boolean formValidate() {
        boolean valid = true;
        View focusView = null;

        dateEdit.setError(null);
        storeEdit.setError(null);

        if (selectedStore == null) {
            storeEdit.setError(context.getString(R.string.error_invalid_store));
            focusView = storeEdit;
            valid = false;
        }

        if (WidgetUtil.getValue(storeEdit).isEmpty()) {
            storeEdit.setError(getString(R.string.error_field_required));
            focusView = storeEdit;
            valid = false;
        }

        if (planDate == null) {
            dateEdit.setError(context.getString(R.string.error_invalid_date));
            focusView = dateEdit;
            valid = false;
        }

//        if (WidgetUtil.getValue(dateEdit).isEmpty()) {
//            dateEdit.setError(getString(R.string.error_field_required));
//            focusView = dateEdit;
//            valid = false;
//        }

        if (!valid)
            focusView.requestFocus();

        return valid;
    }
}
