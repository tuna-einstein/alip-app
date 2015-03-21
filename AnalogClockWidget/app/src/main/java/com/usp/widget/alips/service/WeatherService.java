package com.usp.widget.alips.service;

import android.location.Location;

import com.google.gson.annotations.SerializedName;
import com.usp.widget.alips.WeatherInfo;

import org.apache.http.HttpException;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by umasankar on 3/8/15.
 */
public class WeatherService {

    @Inject RestAdapter restAdapter;

    private interface OpenWeatherMapWebService {
        @GET("/weather?units=metric")
        Observable<CurrentWeatherDataEnvelope> fetchCurrentWeather(
                @Query("lon") double longitude,
                @Query("lat") double latitude);
    }

    public Observable<WeatherInfo> fetchCurrentWeather(final Location location) {
        return restAdapter.create(OpenWeatherMapWebService.class).fetchCurrentWeather(
                location.getLongitude(), location.getLatitude()).map(
                new Func1<CurrentWeatherDataEnvelope, WeatherInfo>() {
            @Override
            public WeatherInfo call(CurrentWeatherDataEnvelope currentWeatherDataEnvelope) {
                WeatherInfo info = new WeatherInfo();
                info.humidity = currentWeatherDataEnvelope.main.humidity;
                info.pressure = currentWeatherDataEnvelope.main.pressure;
                info.temperature = currentWeatherDataEnvelope.main.temp;
                info.location = location;
                return info;
            }
        });
    }


    /**
     * Base class for results returned by the weather web service.
     */
    private class WeatherDataEnvelope {
        @SerializedName("cod")
        private int httpCode;

        class Weather {
            public String description;
        }

        /**
         * The web service always returns a HTTP header code of 200 and communicates errors
         * through a 'cod' field in the JSON payload of the response body.
         */
        public Observable filterWebServiceErrors() {
            if (httpCode == 200) {
                return Observable.just(this);
            } else {
                return Observable.error(
                        new HttpException("There was a problem fetching the weather data."));
            }
        }
    }

    /**
     * Data structure for current weather results returned by the web service.
     */
    private class CurrentWeatherDataEnvelope extends WeatherDataEnvelope {
        @SerializedName("name")
        public String locationName;
        @SerializedName("dt")
        public long timestamp;
        public ArrayList<Weather> weather;
        public Main main;

        class Main {
            public float temp;
            public float temp_min;
            public float temp_max;
            public float humidity;
            public float pressure;
        }
    }
}
