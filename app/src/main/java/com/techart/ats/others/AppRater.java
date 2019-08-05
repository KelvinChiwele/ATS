package com.techart.ats.others;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.techart.ats.R;

import static android.content.Context.MODE_PRIVATE;

public final class AppRater {

    private AppRater() {
    }
    private final static String APP_PNAME = "com.techart.ats";// Package Name
    private final static String LAUNCH_COUNT = "launch_count";
    private final static String DATE_FIRST_LAUNCH = "date_first_launch";
    private final static String DO_NOT_SHOW_AGAIN = "do_not_show_again";
    private final static String NOT_NOW = "not_now";

    private final static int DAYS_UNTIL_PROMPT = 4;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 6;//Min number of launches

    public static void app_launched(Context mContext) {
        long launch_count;
        SharedPreferences prefs = mContext.getSharedPreferences(String.format("%s", mContext.getString(R.string.app_name)), MODE_PRIVATE);
        if (prefs.getBoolean(DO_NOT_SHOW_AGAIN, false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        if (editor != null && prefs.getBoolean(NOT_NOW, true)) {
            launch_count = 3;
            editor.putLong(LAUNCH_COUNT, launch_count);
            Long date_firstLaunch = prefs.getLong(DATE_FIRST_LAUNCH, -1);
            editor.putLong(DATE_FIRST_LAUNCH, date_firstLaunch);
            editor.apply();
            return;
        } else {
            // Increment launch counter
            launch_count = prefs.getLong(LAUNCH_COUNT, 0) + 1;
            editor.putLong(LAUNCH_COUNT, launch_count);
            editor.apply();
        }

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong(DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0 || date_firstLaunch == -1) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(DATE_FIRST_LAUNCH, date_firstLaunch);
            editor.apply();
        }
        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }
    }

    private static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_rate_app);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv = dialog.findViewById(R.id.tv_cancel);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean(DO_NOT_SHOW_AGAIN, true);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });

        TextView b1 = dialog.findViewById(R.id.tv_later);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean(NOT_NOW, true);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });

        TextView b2 = dialog.findViewById(R.id.tv_rate);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PNAME)));
                if (editor != null) {
                    editor.putBoolean(DO_NOT_SHOW_AGAIN, true);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}