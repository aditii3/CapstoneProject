package com.example.android.myreddits.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

import com.example.android.myreddits.data.RedditContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * Created by aditi on 9/16/2018.
 */

public class Util {
    public static void addSearchInDb(Context c, JSONObject response) {
        ContentResolver cr = c.getContentResolver();
        ArrayList<ContentValues> list = null;
        try {
            list = new ArrayList<>();
            JSONArray children = response.getJSONObject("data").getJSONArray("children");
            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.getJSONObject(i).getJSONObject("data");
                ContentValues cv = new ContentValues();
                cv.put(RedditContract.SearchEntry.COLUMN_NAME, child.getString("display_name"));
                cv.put(RedditContract.SearchEntry.COLUMN_DESC, child.getString("public_description"));
                cv.put(RedditContract.SearchEntry.COLUMN_NUM_SUBSCRIBERS, child.getString("subscribers"));
                list.add(cv);

            }
            if (list != null && list.size() > 0) {
                ContentValues[] contentValuesFixedArray = new ContentValues[list.size()];
                list.toArray(contentValuesFixedArray);
                deleteSearchEntry(c);
                cr.bulkInsert(RedditContract.SearchEntry.CONTENT_URI, contentValuesFixedArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static void deleteSearchEntry(Context c) {
        c.getContentResolver().delete(RedditContract.SearchEntry.CONTENT_URI, null, null);
    }

    public static String getTime(long d) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(tz);
        return sdf.format(new Date(d * 1000));

    }

    public static Point getDisplaySize(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void addPostsInDb(Context c, JSONObject response) {
        ContentResolver cr = c.getContentResolver();
        ArrayList<ContentValues> list = null;

        try {
            list = new ArrayList<ContentValues>();
            JSONArray children = response.getJSONObject("data").getJSONArray("children");
            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.getJSONObject(i).getJSONObject("data");
                ContentValues cv = new ContentValues();
                cv.put(RedditContract.PostEntry.COLUMN_ID, child.getString("id"));
                cv.put(RedditContract.PostEntry.COLUMN_COMMENT_COUNT, child.getInt("num_comments"));
                cv.put(RedditContract.PostEntry.COLUMN_AUTHOR, child.getString("author"));
                cv.put(RedditContract.PostEntry.COLUMN_SUBREDDIT_NAME, child.getString("subreddit"));
                cv.put(RedditContract.PostEntry.COLUMN_SUBREDDIT_ID, child.getString("subreddit_id"));
                cv.put(RedditContract.PostEntry.COLUMN_DOMAIN, child.getString("domain"));
                cv.put(RedditContract.PostEntry.COLUMN_SCORE, child.getInt("score"));
                cv.put(RedditContract.PostEntry.COLUMN_CREATED, child.getInt("created_utc"));
                cv.put(RedditContract.PostEntry.COLUMN_PERMALINK, child.getString("permalink"));
                cv.put(RedditContract.PostEntry.COLUMN_URL, child.getString("url"));
                cv.put(RedditContract.PostEntry.COLUMN_THUMBNAIL, child.getString("thumbnail"));
                cv.put(RedditContract.PostEntry.COLUMN_TITLE, child.getString("title"));
                JSONArray images = child.getJSONObject("preview").getJSONArray("images");
                JSONObject o = images.getJSONObject(0).getJSONObject("source");
                cv.put(RedditContract.PostEntry.COLUMN_IMG_URL, o.getString("url"));
                list.add(cv);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (list.size() > 0 && list != null) {
            ContentValues[] contentValuesFixedArray = new ContentValues[list.size()];
            list.toArray(contentValuesFixedArray);
            cr.bulkInsert(RedditContract.PostEntry.CONTENT_URI, contentValuesFixedArray);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void deletePostsTable(Context c) {
        c.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, null, null);
    }

    public static void deletePost(Context c, long id) {
        c.getContentResolver().delete(RedditContract.PostEntry.buildUriWithRowId(id), null, null);

    }

    public static void deleteComments(Context c) {
        c.getContentResolver().delete(RedditContract.CommentEntry.CONTENT_URI, null, null);

    }

    public static void deletePostWithPath(Context c, String name) {
        c.getContentResolver().delete(RedditContract.PostEntry.buildUriWithSubpath(name), null, null);
    }

    public static void deleteFav(Context c, long id) {
        c.getContentResolver().delete(RedditContract.FavsEntry.buildUriWithRowId(id), null, null);
    }

    public static void addCommTest(Context c, JSONArray res) {

        try {
            JSONArray comments = res.getJSONObject(1).getJSONObject("data")
                    .getJSONArray("children");
            for (int i = 0; i < comments.length(); i++) {
                JSONObject cmnt = comments.getJSONObject(i).getJSONObject("data");
                get(2, cmnt, c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void get(int l, JSONObject jsonObject, Context context) {
        ContentResolver cr = context.getContentResolver();
        ArrayList<ContentValues> list = null;
        try {
            list = new ArrayList<>();
            if (!jsonObject.isNull("author")) {
                ContentValues cv = new ContentValues();
                cv.put(RedditContract.CommentEntry.COLUMN_ID, jsonObject.getString("id"));
                cv.put(RedditContract.CommentEntry.COLUMN_AUTHOR, jsonObject.getString("author"));
                cv.put(RedditContract.CommentEntry.COLUMN_SCORE, jsonObject.getString("score"));
                cv.put(RedditContract.CommentEntry.COLUMN_CONTENT, jsonObject.getString("body"));
                cv.put(RedditContract.CommentEntry.COLUMN_CREATED, jsonObject.getInt(RedditContract.CommentEntry.COLUMN_CREATED));
                list.add(cv);
                if (!jsonObject.isNull("replies") &&
                        (jsonObject.get("replies") instanceof JSONObject)) {
                    JSONObject replies = jsonObject.getJSONObject("replies");
                    if (!replies.isNull("data")) {
                        JSONObject data = replies.getJSONObject("data");
                        if (!data.isNull("children")) {
                            JSONArray children = data.getJSONArray("children");
                            for (int i = 0; i < children.length(); i++) {
                                get(l + 1, children.getJSONObject(i).getJSONObject("data"), context);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (list.size() > 0 && list != null) {
            ContentValues[] contentValuesFixedArray = new ContentValues[list.size()];
            list.toArray(contentValuesFixedArray);
            cr.bulkInsert(RedditContract.CommentEntry.CONTENT_URI, contentValuesFixedArray);
        }
    }


    public static void addCommentsInDb(Context context, JSONArray response, String id) {
        ContentResolver cr = context.getContentResolver();
        ArrayList<ContentValues> list = null;
        try {
            list = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject child = response.getJSONObject(i).getJSONObject("data");

                ContentValues cv = new ContentValues();
                cv.put(RedditContract.CommentEntry.COLUMN_MAIN, id);
                cv.put(RedditContract.CommentEntry.COLUMN_ID, child.getString("id"));
                cv.put(RedditContract.CommentEntry.COLUMN_AUTHOR, child.getString("author"));
                cv.put(RedditContract.CommentEntry.COLUMN_SCORE, child.getString("score"));
                cv.put(RedditContract.CommentEntry.COLUMN_CONTENT, child.getString("body"));
                cv.put(RedditContract.CommentEntry.COLUMN_CREATED, child.getInt(RedditContract.CommentEntry.COLUMN_CREATED));
                list.add(cv);
                if (child.has("replies")) {
                    try {
                        if (!child.isNull("replies")) {
                            JSONObject replies = child.getJSONObject("replies");

                            if (!replies.isNull("data")) {
                                JSONObject data = replies.getJSONObject("data");
                                if (!data.isNull("children")) {
                                    JSONArray children = data.getJSONArray("children");
                                    for (int j = 0; j < children.length(); j++) {
                                        addCommentsInDb(context, children, child.getString(RedditContract.CommentEntry.COLUMN_MAIN));

                                    }

                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            ContentValues[] contentValuesFixedArray = new ContentValues[list.size()];
            list.toArray(contentValuesFixedArray);
            cr.bulkInsert(RedditContract.CommentEntry.CONTENT_URI, contentValuesFixedArray);
            cr.notifyChange(RedditContract.CommentEntry.CONTENT_URI, null);
        }

    }
}
