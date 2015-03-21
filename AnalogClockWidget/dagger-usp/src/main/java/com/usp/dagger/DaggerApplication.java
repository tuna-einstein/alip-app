package com.usp.dagger;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Manages an ObjectGraph on behalf of an ApplicationScope.
 */
public abstract class DaggerApplication extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize object graph and inject this
        mObjectGraph = ObjectGraph.create(getModules().toArray());

        // debug mode stuff
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 1) {
            mObjectGraph.validate(); // validate dagger's object graph
        }
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new ApplicationModule(this));
        return result;
    }

    @Module(library = true)
    public static class ApplicationModule {
        DaggerApplication mApp;

        public ApplicationModule(DaggerApplication app) {
            mApp = app;
        }

        /**
         * Provides the ApplicationScope Context associated with this module's graph.
         *
         * @return the ApplicationScope Context
         */
        @Provides
        @Singleton
        @ApplicationScope
        public Context provideApplicationContext() {
            return mApp.getApplicationContext();
        }

        /**
         * Provides the ApplicationScope
         *
         * @return the ApplicationScope
         */
        @Provides
        @Singleton
        public DaggerApplication provideApplication() {
            return mApp;
        }
    }
}
