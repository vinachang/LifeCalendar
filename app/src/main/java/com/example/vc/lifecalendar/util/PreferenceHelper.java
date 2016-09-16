package com.example.vc.lifecalendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PreferenceHelper {
    private static String mPreferenceName = "life_calendar";
    public static String LIFE_EXPECTANCY = "life_expectancy";
    public static String BIRTHDAY = "birthday";
    public static String DISPLAY = "display";
    private static String KEY_FORMAT = "%d_%s";

    public static void setPreference(Context context, int appWidgetId, Bundle bundle) {
        SharedPreferences settings = context.getSharedPreferences(mPreferenceName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(String.valueOf(appWidgetId), true);
        editor.putInt(String.format(KEY_FORMAT, appWidgetId, LIFE_EXPECTANCY),bundle.getInt(LIFE_EXPECTANCY));
        editor.putString(String.format(KEY_FORMAT, appWidgetId, BIRTHDAY), bundle.getString(BIRTHDAY));
        editor.putInt(String.format(KEY_FORMAT, appWidgetId, DISPLAY), bundle.getInt(DISPLAY));

        editor.commit();
    }

    public static Bundle getPreferenceBundle(Context context, int appWidgetId) {
        SharedPreferences settings = context.getSharedPreferences(mPreferenceName, 0);
        if (!settings.contains(String.valueOf(appWidgetId))) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(LIFE_EXPECTANCY, settings.getInt(String.format(KEY_FORMAT, appWidgetId, LIFE_EXPECTANCY), 0));
        bundle.putString(BIRTHDAY, settings.getString(String.format(KEY_FORMAT, appWidgetId, BIRTHDAY), null));
        bundle.putInt(DISPLAY, settings.getInt(String.format(KEY_FORMAT, appWidgetId, DISPLAY), 0));
        return bundle;
    }

    public static void removePreference(Context context, int appWidgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(mPreferenceName, 0).edit();
        editor.remove(String.valueOf(appWidgetId));
        editor.remove(String.format(KEY_FORMAT, appWidgetId, LIFE_EXPECTANCY));
        editor.remove(String.format(KEY_FORMAT, appWidgetId, BIRTHDAY));
        editor.remove(String.format(KEY_FORMAT, appWidgetId, DISPLAY));

        editor.commit();
    }

    public static Bundle createPreferenceBundle(int lifeExpectancy, String birthday, int display) {
        Bundle bundle = new Bundle();
        bundle.putInt(LIFE_EXPECTANCY, lifeExpectancy);
        bundle.putString(BIRTHDAY, birthday);
        bundle.putInt(DISPLAY, display);
        return bundle;
    }

}
