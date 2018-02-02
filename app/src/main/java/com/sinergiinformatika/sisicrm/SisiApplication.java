package com.sinergiinformatika.sisicrm;

import android.app.Application;
import android.util.Log;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by Mark on 12/18/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SisiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                if (Constants.DEBUG) Log.v(getClass().getSimpleName(), "menu key");
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }
}
