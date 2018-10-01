package com.example.android.myreddits.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myreddits.R;
import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.RedditRestClient;
import com.example.android.myreddits.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by aditi on 9/16/2018.
 */

public class SearchAdapter extends CursorAdapter {
    private RedditRestClient client;

    public SearchAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        SearchViewHolder holder;
        View v = LayoutInflater.from(context).inflate(R.layout.search_result_item, parent, false);
        holder = new SearchViewHolder(v);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final SearchViewHolder holder = (SearchViewHolder) view.getTag();
        holder.subredditDesc.setText(cursor.getString(cursor.getColumnIndex(RedditContract.SearchEntry.COLUMN_DESC)));
        holder.subredditName.setText(cursor.getString(cursor.getColumnIndex(RedditContract.SearchEntry.COLUMN_NAME)));
        holder.subredditSubscribers.setText(String.format(context.getString(R.string.subscribers), cursor.getString(cursor.getColumnIndex(RedditContract.SearchEntry.COLUMN_NUM_SUBSCRIBERS))));
        holder.subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                String s = holder.subredditName.getText().toString();
                cv.put(RedditContract.FavsEntry.COLUMN_NAME, s);
                context.getContentResolver().insert(RedditContract.FavsEntry.CONTENT_URI, cv);
                client = new RedditRestClient(context);

                if (Util.isNetworkAvailable(context)) {
                    client.getSubredditPosts(s);
                    Toast.makeText(context, "Subscribed to " + s, Toast.LENGTH_SHORT).show();
                    context.getContentResolver().delete(RedditContract.SearchEntry.buildUriWithRowId(cursor.getColumnIndex(RedditContract.SearchEntry.COLUMN_NAME)), RedditContract.SearchEntry.COLUMN_NAME + "=?", new String[]{holder.subredditName.getText().toString()});
                    context.getContentResolver().notifyChange(RedditContract.SearchEntry.CONTENT_URI, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    static class SearchViewHolder {
        @BindView(R.id.search_name_tv)
        TextView subredditName;
        @BindView(R.id.search_desc_tv)
        TextView subredditDesc;
        @BindView(R.id.search_subscribers_tv)
        TextView subredditSubscribers;
        @BindView(R.id.subscribe_btn)
        Button subscribe;

        public SearchViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }
}
