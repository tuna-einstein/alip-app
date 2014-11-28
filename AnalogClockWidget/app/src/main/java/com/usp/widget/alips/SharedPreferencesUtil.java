package com.usp.widget.alips;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class SharedPreferencesUtil {

    public static final String MORE_DETAILS = "more_details";


    private SharedPreferencesUtil() {}

    /**
     * Package protected helper method that returns the shared preferences
     * object associated with the application context.
     */
    private static SharedPreferences getPrefs() {
        return PreferenceManager
                .getDefaultSharedPreferences(CustomApplication.getAppContext());
    }

    public static boolean showMoreDetails() {
        return getPrefs().getBoolean(MORE_DETAILS, true);
    }

    public static void setMoreDetails(boolean moreDetails) {
        getPrefs().edit().putBoolean(MORE_DETAILS, moreDetails).apply();
    }
}