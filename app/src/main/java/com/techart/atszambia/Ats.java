package com.techart.atszambia;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.techart.atszambia.utils.TypefaceUtil;

/**
 * Created by Kelvin on 27/09/2017.
 */

public class Ats extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TypefaceUtil.overrideFonts(getApplicationContext());
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
