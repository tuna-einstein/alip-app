package com.usp.widget.alips;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;

import analogclock.widget.usp.com.alips.R;

public class DetailInfoActivity extends Activity
        implements SensorListener.EnvironmentListener {
    private static final char DEGREE = '\u00B0';

    private static final int SAVE=0;
    private static final int SHARE=1;

    private SensorListener mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_extra_info);
        registerForContextMenu(findViewById(R.id.root_container));
        mSensorListener = new SensorListener(this);
        ToggleButton button = (ToggleButton) findViewById(R.id.toggle_button);

        if (SharedPreferencesUtil.showMoreDetails()) {
            button.setChecked(true);
            findViewById(R.id.details_container).setVisibility(View.VISIBLE);
        } else {
            button.setChecked(false);
            findViewById(R.id.details_container).setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Is the toggle on?
                boolean on = ((ToggleButton) v).isChecked();

                if (on) {
                    findViewById(R.id.details_container).setVisibility(View.VISIBLE);
                    SharedPreferencesUtil.setMoreDetails(true);
                } else {
                    findViewById(R.id.details_container).setVisibility(View.GONE);
                    SharedPreferencesUtil.setMoreDetails(false);
                }
            }
        });
        AppRater.appLaunched(this);
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
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                Uri screenShotUri = Uri.parse(path);
                Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent1.putExtra(Intent.EXTRA_STREAM, screenShotUri);
                emailIntent1.setType("image/png");
                startActivity(Intent.createChooser(emailIntent1, "Saved in Gallery.. Now Share via"));

                break;
            case SAVE:
                saveImage();
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        finish();
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
    public void onLocationChanged(MyLocation location) {
        if (location == null) {
            return;
        }
        TextView locationView = (TextView) findViewById(R.id.line_address_txt);
        locationView.setText(
                String.format("%s (%s, %s)", location.streetAddress, location.city, location.country));

        TextView longView = (TextView) findViewById(R.id.longitude_txt);
        longView.setText(String.format(": %.2f", location.longitude));

        TextView langView = (TextView) findViewById(R.id.latitude_txt);
        langView.setText(String.format(": %.2f", location.latitude));
    }

    @Override
    public void onPressureChanged(double pressure) {
        TextView pressureView = (TextView) findViewById(R.id.pressure_txt);
        pressureView.setText(String.format(": %.2f hPa", pressure));
    }

    @Override
    public void onTemperatureChanged(double temperature) {
        TextView temperatureView = (TextView) findViewById(R.id.temperature_txt);
        temperatureView.setText(
                String.format(": %.2f" + DEGREE + " C", temperature));
    }

    @Override
    public void onHumidityChanged(double humidity) {
        TextView humidityView = (TextView) findViewById(R.id.humidity_txt);
        humidityView.setText(String.format(": %.2f%%", humidity));

    }

    @Override
    public void onLightChanged(double light) {
        TextView humidityView = (TextView) findViewById(R.id.light_txt);
        humidityView.setText(String.format(": %.2f lx", light));
    }
}
