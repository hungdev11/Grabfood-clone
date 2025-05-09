package com.app.grabfoodapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "JwtTokenPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String tokenString) {
        // Parse the combined token format: userId#actualToken
        String[] parts = tokenString.split("#", 2);
        if (parts.length == 2) {
            String userId = parts[0];
            String token = parts[1];

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USER_ID, userId);
            editor.putString(KEY_TOKEN, token);
            editor.apply();
        } else {
            // Fallback if token doesn't follow expected format
            prefs.edit().putString(KEY_TOKEN, tokenString).apply();
        }
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void deleteToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    public boolean hasToken() {
        return getToken() != null;
    }
    public void logout() {
        deleteToken();  // You already have this method
    }
}