package com.usp.widget.alips;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class SensorListener implements LocationListener, SensorEventListener {

    private static final String LOG_PREFIX = "SensorListener";
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?lat=%1$f&lon=%2$f&units=metric";
    private static final String WEATHER_API_KEY = "41625033c7e354c95cdadd001d9e8c96";

    private static final Long MIN_TIME_BW_UPDATES_MILLIS = 5000L;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES_METERS = 2;


    private SensorManager mSensorManager;

    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mTemperatureMeter;
    private Sensor mPressureMeter;
    private Sensor mHumidityMeter;
    private Sensor mLightMeter;


    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    // record the compass picture angle turned
    private float mCurrentDegree = 0f;
    private LocationManager mLocationManager;
    private Context mContext;

    private final EnvironmentListener mListener;

    public SensorListener(EnvironmentListener listener) {
        mListener = listener;
    }

    public void unRegister() {
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
        mSensorManager.unregisterListener(this, mTemperatureMeter);
        mSensorManager.unregisterListener(this, mHumidityMeter);
        mSensorManager.unregisterListener(this, mPressureMeter);
        mSensorManager.unregisterListener(this, mLightMeter);

        mLocationManager.removeUpdates(this);
    }

    public void register(Context context) {
        mContext = context;
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mTemperatureMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mLightMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mHumidityMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mPressureMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);

        if (mTemperatureMeter != null) {
            mSensorManager.registerListener(this, mTemperatureMeter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.w("umasankar", "Temperature sensor not found");
        }

        if (mPressureMeter != null) {
            mSensorManager.registerListener(this, mPressureMeter, SensorManager.SENSOR_DELAY_UI);
        }

        if (mHumidityMeter != null) {
            mSensorManager.registerListener(this, mHumidityMeter, SensorManager.SENSOR_DELAY_UI);
        }
        if (mLightMeter != null) {
            mSensorManager.registerListener(this, mLightMeter, SensorManager.SENSOR_DELAY_UI);
        }

        boolean isGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES_MILLIS,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES_METERS, this);
        } else if (isGPSEnabled) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES_MILLIS,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES_METERS, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        new GetLocationTask().execute(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        } else if (event.sensor == mTemperatureMeter) {
            mListener.onTemperatureChanged(event.values[0]);
        } else if (event.sensor == mPressureMeter) {
            mListener.onPressureChanged(event.values[0]);
        } else if (event.sensor == mLightMeter) {
            mListener.onLightChanged(event.values[0]);
        } else if (event.sensor == mHumidityMeter) {
            mListener.onHumidityChanged(event.values[0]);
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);
            mCurrentDegree = -azimuthInDegress;
            mListener.onDirectionChange(ra);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public static interface EnvironmentListener {
        void onDirectionChange(RotateAnimation animation);
        void onLocationChanged(MyLocation location);
        void onPressureChanged(double pressure);
        void onTemperatureChanged(double temperature);
        void onHumidityChanged(double humidity);
        void onLightChanged(double light);
    }

    private class GetLocationTask extends AsyncTask<Location, Void, MyLocation> {

        private WeatherInfo mWeatherInfo;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MyLocation doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (Exception e) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
            }
            Address address;
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
            } else {
                return null;
            }
            MyLocation location = new MyLocation();
            location.city = address.getLocality();
            location.country = address.getCountryCode();
            location.latitude = address.getLatitude();
            location.longitude = address.getLongitude();
            location.streetAddress = address.getMaxAddressLineIndex() > 0 ?
                    address.getAddressLine(0) : "";

            if (mTemperatureMeter == null || mPressureMeter == null || mHumidityMeter == null) {
                mWeatherInfo = fetchWeather(loc);
            }
            return location;
        }

        @Override
        protected void onPostExecute(MyLocation location) {
            mListener.onLocationChanged(location);
            if (mWeatherInfo == null) {
                return;
            }
            if (mTemperatureMeter == null) {
                mListener.onTemperatureChanged(mWeatherInfo.temperature);
            }
            if (mHumidityMeter == null) {
                mListener.onHumidityChanged(mWeatherInfo.humidity);
            }
            if (mPressureMeter == null) {
                mListener.onPressureChanged(mWeatherInfo.pressure);
            }
        }
    }


    private WeatherInfo fetchWeather(Location location) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, location.getLatitude(), location.getLongitude()));
            Log.e(LOG_PREFIX, url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", WEATHER_API_KEY);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            JSONObject main = data.getJSONObject("main");

            WeatherInfo info = new WeatherInfo();
            info.humidity = main.getDouble("humidity");
            info.pressure = main.getDouble("pressure");
            info.temperature = main.getDouble("temp");
            return info;

        } catch (Exception e) {
            Log.e(LOG_PREFIX, e.toString());
            return null;
        }
    }
}
