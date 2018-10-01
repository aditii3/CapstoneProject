package com.example.android.myreddits.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aditi on 9/4/2018.
 */

public class IntroManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private static final String SHARED_PREFERENCES = "welcome";
    private static final String FIRST_LAUNCH = "first_launch";

    public IntroManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setFirstLaunch(boolean firstLaunch) {
        editor.putBoolean(FIRST_LAUNCH, firstLaunch);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }

}
