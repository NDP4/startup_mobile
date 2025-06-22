package com.appku.bookingbus.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    private static final String PREF_NAME = "BookingBusCache";
    private final SharedPreferences prefs;

    public CacheManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return prefs.getString(key, null);
    }

    public void saveLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return prefs.getLong(key, 0);
    }
}
