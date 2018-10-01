package com.example.android.myreddits.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.android.myreddits.R;
import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.Constants;
import com.example.android.myreddits.utils.Util;

/**
 * Created by aditi on 9/24/2018.
 */

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor cursor;
    private Context context;
    private int appWidgetId;


    public StackRemoteViewsFactory(Context context, Intent i) {
        this.context = context;
        appWidgetId = i.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) cursor.close();
        String[] p = {RedditContract.PostEntry._ID, RedditContract.PostEntry.COLUMN_IMG_URL, RedditContract.PostEntry.COLUMN_TITLE, RedditContract.PostEntry.COLUMN_CREATED, RedditContract.PostEntry.COLUMN_SUBREDDIT_NAME};
        cursor = context.getContentResolver().query(RedditContract.PostEntry.CONTENT_URI, p, null, null, null);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) cursor.close();
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0 && cursor.moveToFirst() && cursor.moveToPosition(position)) {
            cursor.moveToPosition(position);
            try {
                Bitmap bitmap = Glide.with(context)
                        .asBitmap()
                        .load(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_IMG_URL)))
                        .submit(200, 100)
                        .get();

                remoteViews.setImageViewBitmap(R.id.widget_iv, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            remoteViews.setTextViewText(R.id.widget_title, cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_TITLE)));
            long t = (long) cursor.getInt(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_CREATED));
            remoteViews.setTextViewText(R.id.widget_time, Util.getTime(t));
            remoteViews.setTextViewText(R.id.widget_name, cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_SUBREDDIT_NAME)));
            Intent i = new Intent();
            i.putExtra(Constants.WIDGET_NAME, cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_SUBREDDIT_NAME)));
            i.putExtra(Constants.WIDGET_ID, cursor.getInt(cursor.getColumnIndex(RedditContract.PostEntry._ID)));
            remoteViews.setOnClickFillInIntent(R.id.widget_ll, i);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
