package analogclock.widget.usp.com.analogclockwidget;

import android.app.Application;
import android.content.Context;

public class CustomApplication extends Application {

    private static Context sContext;

    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sContext;
    }
}