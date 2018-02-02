package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Mark on 11/3/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class DisplayScreen {
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display newDisplay = windowManager.getDefaultDisplay();
        Point size = new Point();
        newDisplay.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display newDisplay = windowManager.getDefaultDisplay();
        Point size = new Point();
        newDisplay.getSize(size);
        return size.y;
    }
}
