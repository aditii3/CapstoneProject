package com.example.android.myreddits.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by aditi on 9/30/2018.
 */

public class Analytics {
    public static void logUserSearch(Context context, String search) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, search);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Param.SEARCH_TERM, bundle);
    }
}
