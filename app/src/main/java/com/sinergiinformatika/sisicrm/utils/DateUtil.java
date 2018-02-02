package com.sinergiinformatika.sisicrm.utils;

import android.text.TextUtils;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wendi on 29-Dec-14.
 */
public class DateUtil {

    public static Date parse(String dateStr, String format) {
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                Log.e("DateUtil.parse", e.getMessage(), e);
            }
        }
        return null;
    }

    public static Date parse(String dateStr, boolean dateOnly) {
        if (dateOnly) {
            return parse(dateStr, Constants.DATE_DEFAULT_FORMAT);
        } else {
            return parse(dateStr, Constants.DATE_TIME_DEFAULT_FORMAT);
        }
    }

    public static Date parse(String dateStr) {
        return parse(dateStr, true);
    }

    /**
     * @param date   Date to format.
     * @param format Format string.
     * @return Formatted date.
     */
    public static String format(Date date, String format) {
        return format(date, format, new Locale(Constants.DEFAULT_LOCALE));
    }

    public static String format(Date date, String format, Locale locale) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
            return dateFormat.format(date);
        } catch (Exception e) {
            Log.e("DateUtil.parse", e.getMessage(), e);
        }

        return null;
    }

    public static String formatDBDateOnly(Date date) {
        return format(date, Constants.DATE_DEFAULT_FORMAT);
    }

    public static String formatDBDateTime(Date date) {
        return format(date, Constants.DATE_TIME_DEFAULT_FORMAT);
    }

    public static String formatDayDate(Date date) {
        return format(date, Constants.DATE_DAY_FORMAT);
    }

    public static String formatMonthDate(Date date) {
        return format(date, Constants.DATE_MONTH_FORMAT);
    }

    public static String formatImageDate(Date date) {
        return format(date, Constants.DATE_IMAGE_FORMAT);
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static Date daysAgo(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1 * day);
        return cal.getTime();
    }

    public static long diffDays(Date dateStart, Date dateEnd) {
        long diff = dateEnd.getTime() - dateStart.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }
}
