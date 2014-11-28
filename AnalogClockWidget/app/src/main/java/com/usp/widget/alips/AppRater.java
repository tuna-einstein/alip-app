package com.usp.widget.alips;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import analogclock.widget.usp.com.alips.R;

public class AppRater {

    private final static int LAUNCHES_UNTIL_PROMPT = 5;

    private static final String PREF_NAME = "a_lip_app_rater";
    private static final String DO_NOT_SHOW_AGAIN = "do_not_show_again";
    private static final String LAUNCH_COUNT = "launch_count";


    public static void appLaunched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(DO_NOT_SHOW_AGAIN, false)) {
            return ;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong(LAUNCH_COUNT, 0) + 1;
        editor.putLong(LAUNCH_COUNT, launch_count);

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            showRateDialog(mContext);
            editor.putLong(LAUNCH_COUNT, 0);
        }
        editor.commit();
    }

    private static void showRateDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.rta_dialog_title);
        builder.setMessage(R.string.rta_dialog_message);
        builder.setPositiveButton(R.string.rta_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String appPackage = context.getPackageName();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                context.startActivity(intent);
                setPref(context);
            }
        });
        builder.setNeutralButton(R.string.rta_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearSharedPreferences(context);
            }
        });
        builder.setNegativeButton(R.string.rta_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPref(context);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                clearSharedPreferences(context);
            }
        });
        builder.create().show();
    }

    private static void setPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(DO_NOT_SHOW_AGAIN, true);
        editor.commit();
    }

    private static void clearSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(DO_NOT_SHOW_AGAIN);
        editor.remove(LAUNCH_COUNT);
        editor.commit();
    }
}