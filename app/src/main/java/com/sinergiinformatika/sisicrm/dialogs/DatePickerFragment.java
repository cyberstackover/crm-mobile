package com.sinergiinformatika.sisicrm.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.sinergiinformatika.sisicrm.R;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePickerDialog.OnDateSetListener listener;
    String tag;
    Date date;
    Date minDate;
    Date maxDate;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener,
                                                 Date minDate, Date maxDate) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.listener = listener;
        fragment.minDate = minDate;
        fragment.maxDate = maxDate;

        return fragment;
    }

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener,
                                                 Date date) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.listener = listener;
//        fragment.tag = tag;
        fragment.date = date;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        if (date != null)
            c.setTime(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog
        final DatePickerDialog dialog = new DatePickerDialog(getActivity(), null, year, month, day);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.action_done),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        DatePicker dp = dialog.getDatePicker();
                        onDateSet(dp,
                                dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                    }
                });

        if (minDate != null) {
            // Set minimum date
            dialog.getDatePicker().setMinDate(minDate.getTime());
        }

        if (maxDate != null) {
            // Set maximum date
            dialog.getDatePicker().setMaxDate(maxDate.getTime());
        }

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.v(((Object) this).getClass().getName(), "user selected " + day + "/" + month + "/" + year);

        if (tag != null)
            datePicker.setTag(tag);

        listener.onDateSet(datePicker, year, month, day);
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
