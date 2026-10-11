package com.example.campusfix.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "campus_fix_prefs";
    private static final String KEY_ADMIN_MODE = "key_admin_mode";
    private static final String KEY_USER_NAME = "key_user_name";
    private static final String KEY_USER_EMAIL = "key_user_email";
    private static final String KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isAdminMode() {
        return prefs.getBoolean(KEY_ADMIN_MODE, false);
    }

    public void setAdminMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_ADMIN_MODE, enabled).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Student User");
    }

    public void setUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "student@campus.edu");
    }

    public void setUserEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }
}
