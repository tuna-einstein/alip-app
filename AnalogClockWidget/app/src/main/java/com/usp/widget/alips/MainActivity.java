package com.usp.widget.alips;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.usp.dagger.*;
import com.usp.widget.alips.service.LocationService;
import com.usp.widget.alips.service.WeatherService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * First activity that gets invoked from the widget.
 */
public class MainActivity extends BaseActivity {

    @Inject
    LocationService locationService;
    @Inject
    WeatherService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("umasankar", "Main activity");
        super.onCreate(savedInstanceState);
        Observable<Location> location = locationService.getLocation();
        Observable<WeatherInfo> weatherInfo = location.flatMap(new Func1<Location, Observable<WeatherInfo>>() {
            @Override
            public Observable<WeatherInfo> call(Location location) {
                return weatherService.fetchCurrentWeather(location);
            }
        });
        weatherInfo.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WeatherInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.e("umasankar", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("umasankar", "" + e);
                    }

                    @Override
                    public void onNext(WeatherInfo weatherInfo) {
                        Log.e("umasankar", "" + weatherInfo);
                    }
                });

//        if (isConnectedNetwork()) {
//            Intent activityIntent = new Intent(this, DetailInfoActivity.class);
//            startActivity(activityIntent);
//        } else {
//            Log.w("umasankar", "No connection found");
//            Toast.makeText(this, "No network connection found...", Toast.LENGTH_LONG).show();
//        }
//        finish();
    }

    private boolean isConnectedNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
