package com.example.android.myreddits.remote;

import android.content.Context;
import android.database.Cursor;

import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.Util;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aditi on 9/29/2018.
 */

class RemotePost {
    public synchronized static void refreshAllPosts(Context context) {
        try {
            ArrayList<String> subreddits = new ArrayList<>();

            Cursor cursor = context.getContentResolver().query(RedditContract.FavsEntry.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    subreddits.add(cursor.getString(cursor.getColumnIndex("name")));
                }
                cursor.close();
            }
            if (subreddits.size() > 0)
                Util.deletePostsTable(context);
            for (String subreddit : subreddits) {
                String url = "https://www.reddit.com/r/" + subreddit + "/top" + ".json";
                URL u = new URL(url);
                String r = Util.getResponseFromHttpUrl(u);
                JSONObject subredditJson = new JSONObject(r);
                Util.addPostsInDb(context, subredditJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
