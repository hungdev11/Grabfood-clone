package com.app.grabfoodapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationStorage {
    private static final String PREF_NAME = "LocationPrefs";

    public static void saveLocation(Context context, double lat, double lon) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("latitude", String.valueOf(lat));
        editor.putString("longitude", String.valueOf(lon));
        editor.apply();
    }

    public static double getLatitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.parseDouble(prefs.getString("latitude", "0"));
    }

    public static double getLongitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.parseDouble(prefs.getString("longitude", "0"));
    }
}

