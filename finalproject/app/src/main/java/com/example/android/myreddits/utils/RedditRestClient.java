package com.example.android.myreddits.utils;

/**
 * Created by aditi on 9/13/2018.
 */

import android.content.Context;

import com.example.android.myreddits.data.RedditContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;


public class RedditRestClient {
    private static final String TAG = RedditRestClient.class.getSimpleName();
    private static final String BASE_URL = "https://www.reddit.com";
    private static final String BASE_URL_OAUTH = "https://oauth.reddit.com";
    private static String USER_AGENT = "Android/MyReddits:v1.0.0";
    private AsyncHttpClient client = new AsyncHttpClient();
    private static Context context;

    public RedditRestClient(Context c) {
        context = c;
    }

    public void get(boolean isOAuth, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(isOAuth, url), headers, params, responseHandler);

    }

    public void post(boolean isOAuth, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        client.post(context, getAbsoluteUrl(isOAuth, url), headers, params, null, responseHandler);
    }

    private String getAbsoluteUrl(boolean isOauth, String relUrl) {
        String url = "";
        if (isOauth) {
            url = BASE_URL_OAUTH + relUrl;
        } else {
            url = BASE_URL + relUrl;
        }
        return url;
    }

    public interface Refresher {
        void start();

        void end();
    }

    private Header[] getHeaders() {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("User-Agent", USER_AGENT);
        return headers;
    }

    public void searchSubredditName(String query, final Refresher refresher) {
        final String url = "/subreddits/search.json?q=" + query;

        get(false, url, getHeaders(), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Util.addSearchInDb(context, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });

    }

    public void getSubredditPosts(String subreddit) {
        String url = "/r/" + subreddit + "/top" + ".json";

        get(false, url, getHeaders(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Util.addPostsInDb(context, response);
            }
        });

    }

    public void getComments(final Refresher refresher, String subreddit, final String subredditId, final Context c) {
        String relUrl = "/r/" + subreddit + "/comments/" + subredditId + ".json";
        RequestParams params = new RequestParams();
        params.put("depth", 10);
        params.put("limit", 50);


        get(false, relUrl, getHeaders(), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    JSONArray comments = response.getJSONObject(1).optJSONObject("data").getJSONArray("children");
                    context.getContentResolver().delete(RedditContract.CommentEntry.CONTENT_URI, null, null);
                    Util.deleteComments(context);
                    Util.addCommentsInDb(context, comments, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }


}
