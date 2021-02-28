package com.rafslab.movie.dl.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.Cast;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ThumbnailDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
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
        setContentView(R.layout.activity_thumbnail_details);
        initViews();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
        Cast cast = (Cast) getIntent().getSerializableExtra("cast");
        if (cast != null) {
            toolbar.setTitle(cast.getReal_name());
        }
    }

    private void initViews(){
        toolbar = findViewById(R.id.toolbar);
    }
}