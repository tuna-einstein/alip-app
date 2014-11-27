package analogclock.widget.usp.com.analogclockwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {

    private static final String SYNC_CLICKED = "CLICKED_ON_DIAL";

    @Override
    public void onUpdate(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context,
                ClockWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.clock_widget_layout);
            remoteViews.setOnClickPendingIntent(
                    R.id.clock_dial,
                    getPendingSelfIntent(context, SYNC_CLICKED));
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SYNC_CLICKED.equals(intent.getAction())) {
            Log.v("umasankar", "widget clicked");
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}