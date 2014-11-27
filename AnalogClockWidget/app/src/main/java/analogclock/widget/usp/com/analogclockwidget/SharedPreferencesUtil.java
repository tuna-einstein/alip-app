package analogclock.widget.usp.com.analogclockwidget;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class SharedPreferencesUtil {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String STREET = "street";


    private SharedPreferencesUtil() {}

    /**
     * Package protected helper method that returns the shared preferences
     * object associated with the application context.
     */
    private static SharedPreferences getPrefs() {
        return PreferenceManager
                .getDefaultSharedPreferences(CustomApplication.getAppContext());
    }

    public static float getLatitude() {
        return getPrefs().getFloat(LATITUDE, 0.0f);
    }

    public static void setLatitude(float latitude) {
        getPrefs().edit().putFloat(LATITUDE, latitude).apply();
    }

    public static float getLongitude() {
        return getPrefs().getFloat(LONGITUDE, 0.0f);
    }

    public static void setLongitude(float longitude) {
        getPrefs().edit().putFloat(LONGITUDE, longitude).apply();
    }

    public static String getCountry() {
        return getPrefs().getString(COUNTRY, "");
    }

    public static void setCountry(String country) {
        getPrefs().edit().putString(COUNTRY, country).apply();
    }

    public static String getCity() {
        return getPrefs().getString(CITY, "please wait...");
    }

    public static void setCity(String city) {
        getPrefs().edit().putString(CITY, city).apply();
    }

    public static String getStreet() {
        return getPrefs().getString(STREET, "");
    }

    public static void setStreet(String street) {
        getPrefs().edit().putString(STREET, street).apply();
    }
}