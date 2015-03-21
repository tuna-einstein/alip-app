package com.usp.widget.alips;

import android.content.Context;

import com.usp.dagger.*;

import java.util.List;

public class CustomApplication extends com.usp.dagger.DaggerApplication {

    private static Context sContext;

    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    protected List<Object> getModules() {
        List modules = super.getModules();
        modules.add(new AppModule());
        return modules;
    }
}