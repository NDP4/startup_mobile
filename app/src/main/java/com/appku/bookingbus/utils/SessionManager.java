package com.appku.bookingbus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String PREF_NAME = "BookingBusSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_LOGIN_TIME = "loginTime";
    private static final String KEY_BIOMETRIC_ENABLED = "biometricEnabled";
    private static final String KEY_LAST_EMAIL = "lastEmail";
    private static final String KEY_LAST_USERNAME = "lastUsername";
    private static final String KEY_LAST_TOKEN = "lastToken";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveAuthToken(String token, String userName, String email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());


        // Save last login data
        editor.putString(KEY_LAST_EMAIL, email);
        editor.putString(KEY_LAST_USERNAME, userName);
        editor.putString(KEY_LAST_TOKEN, token);

        editor.apply();
    }

    public void setBiometricEnabled(boolean enabled) {
        Log.d("SessionManager", "Setting biometric enabled: " + enabled);
        editor.putBoolean(KEY_BIOMETRIC_ENABLED, enabled);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
    public String getLastLoginEmail() {
        return prefs.getString(KEY_LAST_EMAIL, null);
    }

    public String getLastLoginUsername() {
        return prefs.getString(KEY_LAST_USERNAME, null);
    }

    public String getLastLoginToken() {
        return prefs.getString(KEY_LAST_TOKEN, null);
    }

    public boolean isLoggedIn() {
        String token = getToken();
        if (token == null) return false;

        // Check if token is expired (1 week = 7 * 24 * 60 * 60 * 1000 = 604800000 milliseconds)
        long loginTime = prefs.getLong(KEY_LOGIN_TIME, 0);
        long currentTime = System.currentTimeMillis();
        boolean isValid = currentTime - loginTime < 604800000;

        // If token is expired, clear the session
        if (!isValid) {
            logout();
            return false;
        }

        return true;
    }
    public boolean isBiometricEnabled() {
        boolean enabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
        Log.d("SessionManager", "Getting biometric enabled: " + enabled);
        return enabled;
    }

    public void logout() {
        // Simpan data biometric dan login terakhir sebelum clear
        boolean biometricEnabled = isBiometricEnabled();
        String lastEmail = getLastLoginEmail();
        String lastUsername = getLastLoginUsername();
        String lastToken = getLastLoginToken();

        // Clear semua preferensi
        editor.clear();
        editor.apply();

        // Kembalikan pengaturan yang perlu dipertahankan
        setBiometricEnabled(biometricEnabled);
        editor.putString(KEY_LAST_EMAIL, lastEmail);
        editor.putString(KEY_LAST_USERNAME, lastUsername);
        editor.putString(KEY_LAST_TOKEN, lastToken);
        editor.apply();
    }
}