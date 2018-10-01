package com.example.android.myreddits.adapter;

/**
 * Created by aditi on 9/22/2018.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.myreddits.R;
import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends CursorAdapter {
    public CommentsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        CommentHolder holder;
        View v = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        holder = new CommentHolder(v);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CommentHolder holder = (CommentHolder) view.getTag();
        long t = (long) cursor.getInt(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_CREATED));
        holder.time.setText(Util.getTime(t));
        holder.content.setText(cursor.getString(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_CONTENT)));
        holder.score.setText(cursor.getString(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_SCORE)));
        holder.username.setText(cursor.getString(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_AUTHOR)));

    }

    static class CommentHolder {
        @BindView(R.id.comment_time)
        TextView time;
        @BindView(R.id.comment_content)
        TextView content;
        @BindView(R.id.comment_score)
        TextView score;
        @BindView(R.id.comment_username)
        TextView username;

        CommentHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
