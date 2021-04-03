package com.rafslab.movie.dl.controller;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.rafslab.movie.dl.utils.BaseUtils;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */
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
