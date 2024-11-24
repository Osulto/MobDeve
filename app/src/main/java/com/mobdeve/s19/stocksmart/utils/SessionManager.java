package com.mobdeve.s19.stocksmart.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.mobdeve.s19.stocksmart.database.models.User;
import com.mobdeve.s19.stocksmart.database.dao.UserDao;


public class SessionManager {
    private static final String PREF_NAME = "StockSmartSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_BUSINESS_NAME = "businessName";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    private static final String KEY_BUSINESS_ID = "businessId";

    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_BUSINESS_NAME, user.getBusinessName());

        // Set business ID (either user's ID or existing business ID)
        long businessId = user.getId();  // Default to user's own ID
        User existingBusiness = new UserDao(context).getFirstUserByBusinessName(user.getBusinessName());
        if (existingBusiness != null && existingBusiness.getId() != user.getId()) {
            businessId = existingBusiness.getId();
        }
        editor.putLong(KEY_BUSINESS_ID, businessId);

        editor.commit();
    }

    public long getBusinessId() {
        return pref.getLong(KEY_BUSINESS_ID, getUserId());  // Default to user ID if no business ID set
    }

    public void setBusinessId(long businessId) {
        editor.putLong(KEY_BUSINESS_ID, businessId);
        editor.commit();
    }
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return pref.getLong(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getBusinessName() {
        return pref.getString(KEY_BUSINESS_NAME, null);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

}