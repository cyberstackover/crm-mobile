package com.sinergiinformatika.sisicrm.utils;

import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.R;

/**
 * Created by wendi on 30-Dec-14.
 */
public class WidgetUtil {

    /*
    TODO method getValue perlu diperbaiki.
    sebetulnya cukup menggunakan class TextView sebagai parameter karna class EditText dan class AutoCompleteTextView
    adalah class turunan dari TextView
    */

    public static String getValue(TextView editText, String defaultValue) {
        if(editText.getText() == null){
            return defaultValue;
        }
        return editText.getText().toString().trim();
    }

    public static String getValue(TextView editText) {
        return getValue(editText, "");
    }

    public static String getValue(EditText editText) {
        return getValue((TextView)editText);
    }

    public static String getValue(AutoCompleteTextView editText) {
        return getValue(editText, "");
    }

    public static void setValue(TextView textView, String value) {
        if (!TextUtils.isEmpty(value)) {
            textView.setText(value);
        } else {
            textView.setText(R.string.hypen);
        }
    }

    public static void setValue(TextView textView, Integer value) {
        if (value != null) {
            textView.setText(value.toString());
        } else {
            textView.setText(R.string.hypen);
        }
    }

    public static void concatenateValue(TextView textView, String value) {
        if (!TextUtils.isEmpty(value)) {
            textView.setText(textView.getText() + " " + value);
        }
    }
}
