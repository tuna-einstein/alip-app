package com.usp.widget.alips;

import android.content.Context;
import android.location.LocationManager;

import com.usp.dagger.*;
import com.usp.dagger.DaggerApplication;
import com.usp.widget.alips.service.LocationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Define bindings that are useful in application level.
 */
@Module(library = true, complete = false)
public class AppModule {
    // We are implementing against version 2.5 of the Open Weather Map web service.
    private static final String WEB_SERVICE_BASE_URL = "http://api.openweathermap.org/data/2.5";
    private static final String WEATHER_API_KEY = "41625033c7e354c95cdadd001d9e8c96";

    @Provides @Singleton
    public LocationManager getLocationManager(@ApplicationScope Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    public RestAdapter getRestAdapter() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Accept", "application/json");
                request.addHeader("x-api-key", WEATHER_API_KEY);
            }
        };
        // give access to the rest api to the entire app
        return new RestAdapter.Builder()
                .setEndpoint(WEB_SERVICE_BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }
}
