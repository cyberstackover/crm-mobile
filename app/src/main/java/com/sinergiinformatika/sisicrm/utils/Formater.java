package com.sinergiinformatika.sisicrm.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by wendi on 29-Dec-14.
 */
public class Formater {
    public static double doubleValue(String value){

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        try {
            return nf.parse(value).doubleValue();
        }catch (ParseException e){
        }

        nf = NumberFormat.getNumberInstance(Locale.getDefault());
        try {
            return nf.parse(value).doubleValue();
        }catch (ParseException e){
        }

        return 0;
    }

    public static String doubleValue(double value){
        String result = "0";

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        try {
            return nf.format(value);
        }catch (Exception e){
        }

        nf = NumberFormat.getNumberInstance(Locale.getDefault());
        try {
            return nf.format(value);
        }catch (Exception e){
        }


        return "0";
    }

    public static final String LOCATION_FORMAT = "%f,%f";

    public static String longlat(double longitude, double latitude){
        return String.format(Locale.ENGLISH, LOCATION_FORMAT, longitude, latitude);
    }
}
