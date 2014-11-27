package analogclock.widget.usp.com.analogclockwidget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class DetailInfoActivity extends Activity
        implements SensorListener.CompassListener, SensorListener.LocationListener{
    private static final char DEGREE = '\u00B0';

    private static final int SAVE=0;
    private static final int SHARE=1;

    private ProgressDialog mProgressDialog;
    private SensorListener mSensorListener;
    private MyLocation myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorListener = new SensorListener(this, this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }

    @Override
    public void onCreateContextMenu(
            ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, SAVE, Menu.NONE, "save");
        menu.add(Menu.NONE, SHARE, Menu.NONE, "save & share");

    }

    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case SHARE:
                String path = saveImage();
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG);
                Uri screenShotUri = Uri.parse(path);
                Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent1.putExtra(Intent.EXTRA_STREAM, screenShotUri);
                emailIntent1.setType("image/png");
                startActivity(Intent.createChooser(emailIntent1, "Saved in Gallery.. Now Share via"));

                break;
            case SAVE:
                saveImage();
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorListener.register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorListener.unRegister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String saveImage() {
        Bitmap bitmap = takeScreenShot();
        return MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
    }

    private Bitmap takeScreenShot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootView.getDrawingCache();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return bitmap;
    }

    @Override
    public void onDirectionChange(RotateAnimation animation) {
        if (findViewById(R.id.compass_container) != null) {
            findViewById(R.id.compass_container).startAnimation(animation);
        }
    }

    @Override
    public void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    public void onPostExecute(MyLocation location) {
        myLocation = location;

        if (location == null) {
            mProgressDialog.dismiss();
            return;
        }
        View view = findViewById(R.id.root_container);
        if (view == null) {
            setContentView(R.layout.activity_on_click_extra_info);
            view = findViewById(R.id.root_container);
            registerForContextMenu(view);
            AppRater.appLaunched(this);
        }

        if (location.weatherInfo != null) {
            if (location.weatherInfo.bitmap != null) {
                ImageView imageView = (ImageView)findViewById(R.id.weather_icon);
                imageView.setImageBitmap(location.weatherInfo.bitmap);
            }
            TextView pressureView = (TextView) findViewById(R.id.pressure_txt);
            pressureView.setText(String.format("%.2f hPa", location.weatherInfo.pressure));

            TextView humidityView = (TextView) findViewById(R.id.humidity_txt);
            humidityView.setText(String.format("%.2f%%", location.weatherInfo.humidity));

            TextView temperatureView = (TextView) findViewById(R.id.temperature_txt);
            double temperature = location.weatherInfo.temperature;
            temperatureView.setText(
                    String.format("%.2f" + DEGREE + "F (%.2f" + DEGREE + " C)", temperature * 9 / 5 + 32, temperature));

            double windSpeed = location.weatherInfo.windSpeed;
            TextView windView = (TextView) findViewById(R.id.wind_txt);
            windView.setText(String.format("%.2f miles/sec, %s \n(%.2f km/s)", windSpeed/1.6,
                    location.weatherInfo.windDirection + DEGREE, windSpeed));
        }
        TextView locationView = (TextView) findViewById(R.id.city_field);
        TextView lineAddressView = (TextView) findViewById(R.id.line_address_txt);
        locationView.setText(
                String.format("%s, %s",
                        location.city, location.country));
        lineAddressView.setText(location.streetAddress);

        TextView latLongView = (TextView) findViewById(R.id.lat_long_txt);
        latLongView.setText(String.format("(%.2f,  %.2f)",
                location.latitude, location.longitude));
        mProgressDialog.dismiss();
    }
}
