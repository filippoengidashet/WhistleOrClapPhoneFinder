package org.dalol.phonefinder.model.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import org.dalol.phonefinder.model.constant.Constant;

/**
 * Created by Filippo-TheAppExpert on 7/19/2015.
 */
public class SharedPreferenceUtils {

    public static final String TAG = SharedPreferenceUtils.class.getSimpleName();

    public static void save(Context context, String key, String value) {
        if(context == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(Constant.PACKAGE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(Context context, String key) {
        if(context == null) {
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences(Constant.PACKAGE_NAME,
                Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
}