package com.pb.app.widgetclock;

import static android.content.Context.MODE_PRIVATE;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Date;

public class ClockWidgetProvider extends AppWidgetProvider implements WidgetUpdatedInterface {
    public static final String CLICK_ACTION_ALARM = "CLICK_ACTION_ALARM";
    public static final String CLICK_ACTION_CALENDAR = "CLICK_ACTION_CALENDAR";

    WidgetViewCreator widgetViewCreator;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Log.i("xmg", "########################  action="+action);
        if (CLICK_ACTION_ALARM.equals(action)) {
            //进入闹铃
            Intent i = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (CLICK_ACTION_CALENDAR.equals(action)) {
            //进入日历
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, new Date().getTime());
            Intent calIntent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
            calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(calIntent);
        } else {
            super.onReceive(context, intent);
        }
    }

    private void redrawWidgetFromData(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        widgetViewCreator = new WidgetViewCreator(this,context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_main), MODE_PRIVATE);
        widgetViewCreator.onSharedPreferenceChanged(sharedPreferences,"");
        RemoteViews views = widgetViewCreator.createWidgetRemoteView();
//        Log.i("xmg", "onUpdate  redrawWidgetFromData");

        Intent intent = new Intent(context, ClockWidgetProvider.class);
        intent.setAction(CLICK_ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.clock, pendingIntent);

        intent = new Intent(context, ClockWidgetProvider.class);
        intent.setAction(CLICK_ACTION_CALENDAR);
        pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.date, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                ClockWidgetProvider.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            redrawWidgetFromData(context, appWidgetManager, widgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
//        Log.i("xmg", "onAppWidgetOptionsChanged");
        redrawWidgetFromData(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void widgetDataUpdated() {

    }
}