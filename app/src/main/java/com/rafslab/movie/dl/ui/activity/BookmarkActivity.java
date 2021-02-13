package com.rafslab.movie.dl.ui.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.database.FavoritesDbHelper;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class BookmarkActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    FavoritesDbHelper dbHelper;
    private List<ChildData> childDataList = new ArrayList<>();
    private ChildAdapter adapter2;
    private ProgressBar progressBar;
    private LottieAnimationView isEmptyItem;
    private ConstraintLayout emptyContainer;
    private static int num = 0;
    private ImageButton changeAnim;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int position = Integer.parseInt(Objects.requireNonNull(mPrefs.getString(NIGHT_MODE, "2")));
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
        setContentView(R.layout.activity_bookmark);
        initViews();
        dbHelper = FavoritesDbHelper.getHelper(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.bookmark);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
        loadBookmark(childDataList);
        refreshLayout.setOnRefreshListener(() -> {
            loadBookmark(childDataList);
            new Handler().postDelayed(()->{
                refreshLayout.setRefreshing(false);
                checkFavorites();
                if (childDataList.isEmpty()){
                    emptyContainer.setVisibility(View.VISIBLE);
                    isEmptyItem.setAnimation(R.raw.shark_170);
                    isEmptyItem.playAnimation();
                } else {
                    emptyContainer.setVisibility(View.GONE);
                    isEmptyItem.pauseAnimation();
                }
            },300);
        });
        if (dbHelper.isAvailable()) {
            emptyContainer.setVisibility(View.GONE);
            isEmptyItem.pauseAnimation();
        } else {
            emptyContainer.setVisibility(View.VISIBLE);
            isEmptyItem.setAnimation(R.raw.shark_170);
            isEmptyItem.playAnimation();
        }
//        changeAnim.setOnClickListener(v->{
//            if (num == 0) {
//                isEmptyItem.setAnimation(R.raw.write_contacts_fab_icon_reverse);
//                isEmptyItem.playAnimation();
//                num = 1;
//            } else if (num == 1) {
//                isEmptyItem.setAnimation(R.raw.shark_170);
//                isEmptyItem.playAnimation();
//                num = 0;
//            }
//        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViews(){
        recyclerView = findViewById(R.id.bookmark_list);
        refreshLayout = findViewById(R.id.refresh);
        progressBar = findViewById(R.id.progress_bar);
        isEmptyItem = findViewById(R.id.empty);
        toolbar = findViewById(R.id.toolbar);
        emptyContainer = findViewById(R.id.no_bookmark);
        changeAnim = findViewById(R.id.change_anim);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        loadBookmark(childDataList);
    }

    private void checkFavorites(){
        loadBookmark(childDataList);
    }
    private void loadBookmark(List<ChildData> dataList){
        adapter2 = new ChildAdapter(this, dataList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
        getAllFavorites();
        dbHelper = new FavoritesDbHelper(this);
        progressBar.setVisibility(View.GONE);

    }
    @SuppressLint("StaticFieldLeak")
    private void getAllFavorites(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                childDataList.clear();
                childDataList.addAll(dbHelper.getAllFavorites());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter2.notifyDataSetChanged();
            }
        }.execute();
    }
}
