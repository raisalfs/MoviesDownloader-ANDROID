package com.rafslab.movie.dl.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.ui.fragment.SettingsFragment;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int position = Integer.parseInt(mPrefs.getString(NIGHT_MODE, "2"));
        switch (position){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
        SettingsFragment fragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment, fragment).commit();
    }
    public static void popBackToStack(Context context){
        ((SettingsActivity)context).getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}