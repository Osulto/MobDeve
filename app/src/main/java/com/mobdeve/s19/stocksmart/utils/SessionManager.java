package com.mobdeve.s19.stocksmart.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "StockSmartPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_BUSINESS_ID = "businessId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_OFFLINE_MODE = "offlineMode";


    private SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createLoginSession(String userId, String username) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public void setBusinessId(long businessId) {
        editor.putLong(KEY_BUSINESS_ID, businessId);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public long getBusinessId() {
        return pref.getLong(KEY_BUSINESS_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public void setOfflineMode(boolean enabled) {
        editor.putBoolean(KEY_OFFLINE_MODE, enabled);
        editor.commit();
    }

    public boolean isOfflineMode() {
        return pref.getBoolean(KEY_OFFLINE_MODE, false);
    }


    public void logout() {
        editor.clear();
        editor.commit();
    }
}