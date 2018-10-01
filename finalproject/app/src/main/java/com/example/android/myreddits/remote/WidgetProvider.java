package com.example.android.myreddits.remote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.myreddits.R;
import com.example.android.myreddits.service.StackWidgetService;
import com.example.android.myreddits.ui.PostCommentsActivity;
import com.example.android.myreddits.utils.Constants;

/**
 * Created by aditi on 9/24/2018.
 */

public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stack_widget);
            setUpRemoteCollectionView(context, rv, appWidgetId);
            setCollectionItemOnClickIntent(context, rv, appWidgetId);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void setUpRemoteCollectionView(Context context, RemoteViews widgetLayoutRemoteView,
                                           int widgetId) {
        Intent intent = new Intent(context, StackWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        widgetLayoutRemoteView.setRemoteAdapter(R.id.stack_view, intent);
        widgetLayoutRemoteView.setEmptyView(R.id.stack_view, R.id.empty_view);
    }

    private void setCollectionItemOnClickIntent(Context context, RemoteViews widgetLayoutRemoteView,
                                                int widgetId) {
        Intent intent = new Intent(context, PostCommentsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Constants.WIDGET_POST);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        widgetLayoutRemoteView.setPendingIntentTemplate(R.id.stack_view, pendingIntent);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
