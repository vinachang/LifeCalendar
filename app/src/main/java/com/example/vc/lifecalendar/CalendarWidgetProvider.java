package com.example.vc.lifecalendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.vc.lifecalendar.util.PreferenceHelper;
import com.example.vc.lifecalendar.util.Util;

public class CalendarWidgetProvider extends AppWidgetProvider {
    private static String TAG = "WidgetProvider";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                CalendarWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = getLifeCalendarRemoteViews(context, widgetId, appWidgetManager.getAppWidgetOptions(widgetId));
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            PreferenceHelper.removePreference(context, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        RemoteViews remoteViews = getLifeCalendarRemoteViews(context, appWidgetId, newOptions);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private RemoteViews getLifeCalendarRemoteViews(Context context, int appWidgetId, Bundle options) {
        Bundle bundle = PreferenceHelper.getPreferenceBundle(context, appWidgetId);
        if (bundle == null) {
            return new RemoteViews(context.getPackageName(), R.layout.no_pref_available_view);
        }
        LifeCalendarView lifeCalendarView = new LifeCalendarView(context, bundle);
//        lifeCalendarView.setDisplay(LifeCalendarView.DISPLAY.CURRENT_YEAR);
        int width = (int) Util.convertDpToPixel(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH), context);
        int height = (int) Util.convertDpToPixel(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT), context);

        lifeCalendarView.measure(width, height);
        lifeCalendarView.layout(0, 0, width, height);
        lifeCalendarView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(lifeCalendarView.getDrawingCache());
        lifeCalendarView.setDrawingCacheEnabled(false);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        remoteViews.setImageViewBitmap(R.id.image, bitmap);
        return remoteViews;
    }

}
