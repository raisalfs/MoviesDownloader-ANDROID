package com.rafslab.movie.dl.controller;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;
import com.rafslab.movie.dl.utils.AntiReverseUtils;
import com.rafslab.movie.dl.utils.BaseUtils;

public class AppController extends Application {
    public static final String NIGHT_MODE = "day_night";
    private boolean isNightModeEnabled = false;

    private static AppController singleton = null;

    public static AppController getInstance(){
        if (singleton == null) {
            singleton = new AppController();
        }
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        singleton = this;
        BaseUtils.errorReporter(this);
    }
}
