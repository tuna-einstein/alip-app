package com.usp.dagger;

import android.app.Activity;
import android.content.Context;

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
 * Created by umasankar on 3/7/15.
 */
public class DaggerActivity extends Activity {

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        // extend the application-scope object graph with the modules for this activity
        DaggerApplication application = ((DaggerApplication) getApplication());
        ObjectGraph objectGraph = application.getObjectGraph().plus(getModules().toArray());

        // now we can inject ourselves
        objectGraph.inject(this);

        // note: we do the graph setup and injection before calling super.onCreate so that InjectingFragments
        // associated with this InjectingActivity can do their graph setup and injection in their
        // onAttach override.
        super.onCreate(savedInstanceState);

    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new DaggerActivityModule(this));
        return result;
    }

    @Module(library = true)
    public static class DaggerActivityModule {
        private android.app.Activity mActivity;

        public DaggerActivityModule(android.app.Activity activity) {
            mActivity = activity;
        }

        /**
         * Provides the Activity Context
         *
         * @return the Activity Context
         */
        @Provides
        @Singleton
        @ActivityScope
        public android.content.Context provideActivityContext() {
            return (Context) mActivity;
        }

        /**
         * Provides the Activity
         *
         * @return the Activity
         */
        @Provides
        public android.app.Activity provideActivity() {
            return mActivity;
        }


    }
}
