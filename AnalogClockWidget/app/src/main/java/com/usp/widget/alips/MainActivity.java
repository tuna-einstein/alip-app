package com.usp.widget.alips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * First activity that gets invoked from the widget.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("umasankar", "Main activity");
        super.onCreate(savedInstanceState);
        if (isConnectedNetwork()) {
            Intent activityIntent = new Intent(this, DetailInfoActivity.class);
            startActivity(activityIntent);
        } else {
            Log.w("umasankar", "No connection found");
            Toast.makeText(this, "No network connection found...", Toast.LENGTH_LONG).show();
        }
        finish();
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
