package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * Created by Mark on 9/29/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class TypeFaceModifier {
    private static final String TAG = "TypeFace";
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }

    public static void overrideFont(Context context, String defaultFontNameToOverride,
                                    String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = get(context, customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class
                    .getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Log.e(TAG,
                    "Can not set custom font " + customFontFileNameInAssets +
                            " instead of " + defaultFontNameToOverride, e);
        }
    }

    public static void overrideWithFontAwesome(Context context, String fontToOverride) {
        overrideFont(context, fontToOverride, "fonts/fontawesome.ttf");
    }
}