package com.app.grabfoodapp;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class GrabFoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}