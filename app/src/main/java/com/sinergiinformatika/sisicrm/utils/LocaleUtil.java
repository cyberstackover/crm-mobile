package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Mark on 1/7/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class LocaleUtil {
    public static void changeLocale(Context context, String localeStr) {
        Locale locale = new Locale(localeStr);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(configuration, null);
    }
}
