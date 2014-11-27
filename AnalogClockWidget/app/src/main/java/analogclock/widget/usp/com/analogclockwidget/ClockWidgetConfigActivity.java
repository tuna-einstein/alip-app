package analogclock.widget.usp.com.analogclockwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;


public class ClockWidgetConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

     //   initializeAppWidget();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        RemoteViews views = new RemoteViews(getPackageName(),
//                R.layout.clock_widget_layout);
//        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void initializeAppWidget(){
        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);;

        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
}
