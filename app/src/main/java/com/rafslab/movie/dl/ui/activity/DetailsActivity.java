package com.rafslab.movie.dl.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.CastAdapter;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.adapter.ChipsAdapter;
import com.rafslab.movie.dl.adapter.SliderAdapter;
import com.rafslab.movie.dl.database.DatabaseContract;
import com.rafslab.movie.dl.database.FavoritesDbHelper;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.ChipsReceived;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.spring.SpringAnimationType;
import com.rafslab.movie.dl.spring.SpringyAnimator;
import com.rafslab.movie.dl.ui.fragment.sheet.ViewDownload;
import com.rafslab.movie.dl.utils.BaseUtils;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vickykdv.circlerectimageview.CircleRectImage;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;
import static com.rafslab.movie.dl.database.DatabaseContract.FavoritesEntry.COLUMN_TITLE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class DetailsActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private ImageView poster;
    private SliderView cover;
    AppCompatButton showDownloadItems;
    private RecyclerView castList, similarList, categoriesList;
    private TextView title, description, ratingText, itemCount, castText;
    private AppCompatRatingBar ratingBar;
    private Toolbar toolbar;
    private NestedScrollView scrollable;
    private View statBar;
    private RealtimeBlurView blur;
    private ConstraintLayout castViewMore;

    private FrameLayout saveBookmark;
    private FavoritesDbHelper dbHelper;
    final ContentValues bookValues = new ContentValues();
    private SQLiteDatabase database;
    private String name, currentMovie;
    private ChildData childData;
    static final String key = "isFavorite";
    private int position ;
    private int secondPosition;
    private View counter;
    private int identifyDownloadable;
    private AppBarLayout floatingDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int positionTheme = Integer.parseInt(Objects.requireNonNull(mPrefs.getString(NIGHT_MODE, "2")));
        switch (positionTheme){
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
        setContentView(R.layout.activity_details);
        initViews();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
        childData = (ChildData) getIntent().getSerializableExtra("childData");
        position = Objects.requireNonNull(getIntent().getExtras()).getInt("position_db");
        secondPosition = Objects.requireNonNull(getIntent().getExtras()).getInt("position");
        int downloadPosition = Objects.requireNonNull(getIntent().getExtras()).getInt("position_download");
        identifyDownloadable = getIntent().getIntExtra("downloadable", 0);
        showDownloadItems.setOnClickListener(v->{
            if (childData != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_STORAGE_CODE);
                    } else {
                        if (identifyDownloadable != 0) {
                            ViewDownload viewDownload = new ViewDownload();
                            Bundle args = new Bundle();
                            args.putSerializable("data", childData);
                            args.putInt("position_download", downloadPosition);
                            viewDownload.setArguments(args);
                            viewDownload.show(getSupportFragmentManager(), viewDownload.getTag());
                        } else BaseUtils.showMessage(this, "Coming soon", Toast.LENGTH_SHORT);
                    }
                }
            }
        });
        if (childData != null) {
            setCategoriesList(categoriesList, childData);
            Glide.with(this).load(childData.getPoster()).placeholder(R.drawable.loading_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.1f).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(poster);
            List<Cast> casts = childData.getCastList();
            if (casts != null) {
                getCastList(casts);
                if (casts.get(0).getName().equals("null")) {
                    castText.setVisibility(View.GONE);
                    castList.setVisibility(View.GONE);
                } else {
                    castText.setVisibility(View.VISIBLE);
                    castList.setVisibility(View.VISIBLE);
                }
            }
            title.setText(childData.getTitle());
            description.setText(childData.getDescription());
            ratingBar.setRating((float) ((float) childData.getRating() * 0.5));
            String rating = childData.getRating() + "";
            ratingText.setText(rating);
            String path = "latest-updated";
            currentMovie = childData.getTitle().toLowerCase();
            getSimilarData(path, similarList, childData);
            name = childData.getTitle();
            getCoverList(childData);
            setWritableDatabase(childData);
            castViewMore.setOnClickListener(v-> {
                Uri uri = Uri.parse(childData.getCastDetails());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
        }
        blur.setBlurRadius(20);
        blur.setOverlayColor(Color.parseColor("#30ffffff"));
        Rect scrollBounds = new Rect();
        scrollable.getHitRect(scrollBounds);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollable.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int statusBarHeight = getStatusBarHeight(this);
                int color = ContextCompat.getColor(this, R.color.colorPrimary);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color) & 0xFF;
                double scrollYPercent = ((((float) scrollY) / ((float) (counter.getHeight() - scrollBounds.height() + statusBarHeight))));
                if (scrollYPercent >= 0 && scrollYPercent <= 1) {
                    toolbar.setBackgroundColor(Color.argb((int)(255 * scrollYPercent), r, g, b));
                    statBar.setBackgroundColor(Color.argb((int)(255 * scrollYPercent), r, g, b));
                }
                if (counter != null) {
                    if (!(counter.getLocalVisibleRect(scrollBounds))) {
                        title.setVisibility(View.GONE);
                        toolbar.setTitle(childData.getTitle());
                        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
                    } else {
                        if (counter.getLocalVisibleRect(scrollBounds) || scrollBounds.height() < counter.getHeight()) {
                            title.setVisibility(View.VISIBLE);
                            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.transparent));
                        }
                    }
                }
            });
        } else {
            scrollable.getViewTreeObserver().addOnScrollChangedListener(() -> {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int scrollY = scrollable.getScrollY();
                int scrollContentHeight = scrollable.getChildAt(0).getHeight();
                int screenHeight = displayMetrics.heightPixels;
                int statusBarHeight = getStatusBarHeight(this);

                int color = getResources().getColor(R.color.colorPrimary);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color) & 0xFF;

                int color2 = getResources().getColor(R.color.white);
                int r2 = (color2 >> 16) & 0xFF;
                int g2 = (color2 >> 8) & 0xFF;
                int b2 = (color2) & 0xFF;

                int color3 = getResources().getColor(R.color.colorPrimaryHalf);
                int r3 = (color3 >> 16) & 0xFF;
                int g3 = (color3 >> 8) & 0xFF;
                int b3 = (color3) & 0xFF;

                double percent = ((((float) scrollY) / ((float) (scrollContentHeight - screenHeight + statusBarHeight))));
                if (percent >= 0 && percent <= 1){
                    toolbar.setBackgroundColor(Color.argb((int) (255 * percent), r, g, b));
                    statBar.setBackgroundColor(Color.argb((int) (255 * percent), r3, g3, b3));
                    if (childData != null && actionBar != null) {
                        actionBar.setDisplayShowTitleEnabled(false);
                        toolbar.setTitle(childData.getTitle());
                        toolbar.setTitleTextColor(Color.argb((int) (255 * percent), r2, g2, b2));
                    }
                }
            });
        }
    }

    private void getCoverList(ChildData childData) {
        cover.setSliderAdapter(new SliderAdapter(this, childData.getCoverArrays(), true));
        cover.setIndicatorAnimation(IndicatorAnimationType.WORM);
        cover.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        cover.setIndicatorSelectedColor(Color.WHITE);
        cover.setIndicatorUnselectedColor(Color.GRAY);
        cover.setScrollTimeInSec(3);
        cover.startAutoCycle();
    }
    private void setWritableDatabase(ChildData data){
        dbHelper = new FavoritesDbHelper(this);
        database = dbHelper.getWritableDatabase();
        Gson gson = new Gson();
        String castListText = gson.toJson(data.getCastList());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST, castListText);
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_ID, data.getId());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_TITLE, data.getTitle());

        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE, data.getSecondTitle());

        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_STATUS, data.getStatus());
        String coverList = gson.toJson(data.getCoverArrays());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST, coverList);
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_COUNTRY, data.getCountry());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING, data.getContentRating());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT, data.getSeasonCount());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_PROGRESS, data.getProgress());
        String downloadList = gson.toJson(data.getDownloads());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST, downloadList);

        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION, data.getDescription());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES, data.getCategories());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_POSTER, data.getPoster());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE, data.getSubtitle());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION, data.getSubtitleRegion());

        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT, data.getEpsCount());

        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_TRAILER, data.getTrailer());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_RATING, data.getRating());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_DURATION, data.getDuration());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_TYPE, data.getType());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_RELEASE, data.getRelease());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_IMBD, data.getMovieDetails());
        bookValues.put(DatabaseContract.FavoritesEntry.COLUMN_PINNED, data.isPinned());
        addBookmark();
    }
    private void addBookmark() {
        MaterialFavoriteButton favoriteButton;
        if (Exist(name)){
            favoriteButton = new MaterialFavoriteButton.Builder(this)
                    .favorite(true)
                    .animateFavorite(true)
                    .color(MaterialFavoriteButton.STYLE_WHITE)
                    .type(MaterialFavoriteButton.STYLE_HEART)
                    .rotationDuration(400)
                    .create();
            dbHelper = new FavoritesDbHelper(DetailsActivity.this);
            saveBookmark.addView(favoriteButton);
            favoriteButton.setOnFavoriteChangeListener((buttonView, isFavorite) -> {
                animateView(buttonView);
                if (isFavorite){
                    saveFavorites();
                    SharedPreferences.Editor mPrefsEditor;
                    mPrefsEditor = getSharedPreferences("favorite", MODE_PRIVATE).edit();
                    mPrefsEditor.putBoolean(key, true);
                    mPrefsEditor.apply();
                    snackBarAddedFavorite(favoriteButton);
                } else {
                    SharedPreferences.Editor mPrefsEditor;
                    dbHelper = new FavoritesDbHelper(DetailsActivity.this);
                    dbHelper.deleteFavorites(position);
                    mPrefsEditor = getSharedPreferences("favorite", MODE_PRIVATE).edit();
                    mPrefsEditor.putBoolean(key, false);
                    mPrefsEditor.apply();
                    messageRemoved(favoriteButton, childData.getTitle() + " removed from Bookmark ");
                }
            });

        } else {
            favoriteButton = new MaterialFavoriteButton.Builder(this)
                    .animateFavorite(true)
                    .color(MaterialFavoriteButton.STYLE_WHITE)
                    .type(MaterialFavoriteButton.STYLE_HEART)
                    .rotationDuration(400)
                    .create();
            saveBookmark.addView(favoriteButton);
            favoriteButton.setOnFavoriteChangeListener((buttonView, favorite) -> {
                animateView(buttonView);
                if (favorite){
                    saveFavorites();
                    SharedPreferences.Editor mPrefsEditor;
                    mPrefsEditor = getSharedPreferences("favorite", MODE_PRIVATE).edit();
                    mPrefsEditor.putBoolean(key, true);
                    mPrefsEditor.apply();
                    snackBarAddedFavorite(favoriteButton);
                } else {
                    SharedPreferences.Editor mPrefsEditor;
                    dbHelper = new FavoritesDbHelper(DetailsActivity.this);
                    dbHelper.deleteFavorites(position);
                    mPrefsEditor = getSharedPreferences("favorite", MODE_PRIVATE).edit();
                    mPrefsEditor.putBoolean(key, false);
                    mPrefsEditor.apply();
                    messageRemoved(favoriteButton, childData.getTitle() + " removed from Bookmark ");
                }
            });
        }
    }

    private void messageRemoved(View view, String body){
       Snackbar snackbar = Snackbar.make(view, body, com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT);
       snackbar.setAnchorView(floatingDownload);
       snackbar.show();
    }
    private void snackBarAddedFavorite(MaterialFavoriteButton favoriteButton){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view,"", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        @SuppressLint("InflateParams") View snackView = inflater.inflate(R.layout.message_added_bookmark,null);
        ((TextView)snackView.findViewById(R.id.movie_added)).setText(childData.getTitle());
        String message = " "+ getString(R.string.added_bookmark);
        ((TextView)snackView.findViewById(R.id.snackbar_text)).setText(message);
        snackView.findViewById(R.id.snackbar_action).setOnClickListener(v->{
            dbHelper = new FavoritesDbHelper(DetailsActivity.this);
            dbHelper.deleteFavorites(position);
            messageRemoved(snackView, childData.getTitle() + " removed from Bookmark ");
            favoriteButton.setFavorite(false);
        });
        final CircleRectImage poster = snackView.findViewById(R.id.snackbar_image);
        Glide.with(this).load(childData.getPoster()).into(poster);
        snackbar.setAnchorView(floatingDownload);
        layout.addView(snackView,0);
        snackbar.show();
    }

    private void saveFavorites() {
        dbHelper = new FavoritesDbHelper(this);
        ChildData data = new ChildData();
        data.setId(childData.getId());
        data.setTitle(childData.getTitle());
        data.setPoster(childData.getPoster());
        data.setCastData(childData.getCastData());
        data.setCastList(childData.getCastList());

        data.setSecondTitle(childData.getSecondTitle());

        data.setStatus(childData.getStatus());
        data.setCoverArrays(childData.getCoverArrays());
        data.setCountry(childData.getCountry());
        data.setContentRating(childData.getContentRating());
        data.setSeasonCount(childData.getSeasonCount());
        data.setProgress(childData.getProgress());
        data.setDownloads(childData.getDownloads());
        data.setEpsCount(childData.getEpsCount());

        data.setDescription(childData.getDescription());

        data.setCategories(childData.getCategories());
        data.setPoster(childData.getPoster());
        data.setSubtitle(childData.getSubtitle());
        data.setSubtitleRegion(childData.getSubtitleRegion());
        data.setTrailer(childData.getTrailer());
        data.setRating(childData.getRating());
        data.setDuration(childData.getDuration());
        data.setType(childData.getType());
        data.setRelease(childData.getRelease());
        data.setMovieDetails(childData.getMovieDetails());
        data.setPinned(childData.isPinned());
        dbHelper.addFavorites(data);
    }
    public boolean Exist(String searchItem){
        String[] item = {
                DatabaseContract.FavoritesEntry._ID,
                DatabaseContract.FavoritesEntry.COLUMN_ID,
                DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_STATUS,
                DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_COUNTRY,
                DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_PROGRESS,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION,
                DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES,
                DatabaseContract.FavoritesEntry.COLUMN_POSTER,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_TRAILER,
                DatabaseContract.FavoritesEntry.COLUMN_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_DURATION,
                DatabaseContract.FavoritesEntry.COLUMN_TYPE,
                DatabaseContract.FavoritesEntry.COLUMN_RELEASE,
                DatabaseContract.FavoritesEntry.COLUMN_IMBD,
                DatabaseContract.FavoritesEntry.COLUMN_PINNED
        };
        String selection = COLUMN_TITLE + " =?";
        String[] selectionArgs = {searchItem};
        String limit = "1";
        Cursor cursor = database.query(DatabaseContract.FavoritesEntry.TABLE_NAME, item, selection, selectionArgs, null, null, null, limit);
        boolean exist = (cursor.getCount()>0);
        cursor.close();
        return exist;
    }

    public static int getStatusBarHeight(final Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
    }
    private void initViews(){
        showDownloadItems = findViewById(R.id.download_container);
        castList = findViewById(R.id.cast_list);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        categoriesList = findViewById(R.id.categories);
        poster = findViewById(R.id.poster);
        cover = findViewById(R.id.cover);
        ratingBar = findViewById(R.id.rating_details);
        ratingText = findViewById(R.id.rating_text);
        toolbar = findViewById(R.id.toolbar);
        scrollable = findViewById(R.id.scrollable);
        similarList = findViewById(R.id.similar_list);
        statBar = findViewById(R.id.stat_bar);
        blur = findViewById(R.id.blur);
        itemCount = findViewById(R.id.item_count);
        saveBookmark = findViewById(R.id.save);
        counter = findViewById(R.id.counter);
        castText = findViewById(R.id.cast_text);
        castViewMore = findViewById(R.id.cast_more);
        floatingDownload = findViewById(R.id.floating_download);
    }

    private void getCastList(List<Cast> cast){
        castList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castList.setAdapter(new CastAdapter(this, cast));
    }
    private void getSimilarData(String path, RecyclerView recyclerView, ChildData childData){
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + path
                + CipherClient.END();
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ChildData> childDataList = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ChildData data = new ChildData();
                                data.setId(object.getInt("id"));
                                data.setTitle(object.getString("title"));
                                data.setSecondTitle(object.getString("2ndTitle"));
                                data.setStatus(object.getString("status"));
                                List<CoverArray> coverArrays = new ArrayList<>();
                                JSONArray array = object.getJSONArray("cover");
                                for (int coverPost = 0; coverPost<array.length(); coverPost++){
                                    JSONObject coverObject = array.getJSONObject(coverPost);
                                    CoverArray coverArray = new CoverArray();
                                    coverArray.setImage(coverObject.getString("image"));
                                    coverArrays.add(coverArray);
                                }
                                data.setCoverArrays(coverArrays);
                                data.setDescription(object.getString("description"));
                                data.setCategories(object.getString("categories"));
                                data.setTags(object.getString("tags"));
                                data.setCountry(object.getString("country"));
                                data.setContentRating(object.getString("contentRating"));
                                data.setProgress(object.getString("progress"));
                                data.setEpsCount(object.getInt("epsCount"));
                                data.setSeasonCount(object.getInt("seasonCount"));
                                data.setPoster(object.getString("poster"));
                                data.setSubtitle(object.getString("subtitle"));
                                data.setSubtitleRegion(object.getString("subtitleRegion"));
                                List<Download> downloads = new ArrayList<>();
                                JSONArray downloadArray = object.getJSONArray("download");
                                for (int downloadsPos = 0; downloadsPos <downloadArray.length(); downloadsPos++){
                                    Download dataDownload = new Download();
                                    JSONObject dataObject = downloadArray.getJSONObject(downloadsPos);
                                    dataDownload.setName(dataObject.getString("name"));
                                    dataDownload.setItemCount(dataObject.getInt("count"));
                                    Resolution resolution = new Resolution();
                                    JSONObject resolutionObject = dataObject.getJSONObject("resolution");
                                    JSONArray resolutionArray = resolutionObject.getJSONArray("value");
                                    List<ResolutionValue> resolutionValues = new ArrayList<>();
                                    for (int resolutionPos = 0; resolutionPos <resolutionArray.length(); resolutionPos++){
                                        JSONObject resolutionValuesObject = resolutionArray.getJSONObject(resolutionPos);
                                        ResolutionValue resolutionValue = new ResolutionValue();
                                        resolutionValue.setName(resolutionValuesObject.getString("name"));
                                        resolutionValue.setId(resolutionPos);
                                        JSONObject valuesObject = resolutionValuesObject.getJSONObject("value");
                                        ResolutionValue.Value value = new ResolutionValue.Value();
                                        value.setEpisode(valuesObject.getString("episode"));
                                        value.setBatch(valuesObject.getString("batch"));
                                        resolutionValue.setValues(value);
                                        resolutionValues.add(resolutionValue);
                                    }
                                    resolution.setResolutionValues(resolutionValues);
                                    dataDownload.setResolution(resolution);
                                    downloads.add(dataDownload);
                                }
                                data.setDownloads(downloads);
                                data.setTrailer(object.getString("trailer"));
                                data.setRating(object.getDouble("rating"));
                                data.setDuration(object.getString("duration"));
                                data.setType(object.getString("type"));
                                data.setDownloadable(object.getInt("downloadable"));
                                data.setRelease(object.getString("release"));
                                List<Cast> castList = new ArrayList<>();
                                JSONArray castArray = object.getJSONArray("cast");
                                for (int castPos = 0; castPos <castArray.length(); castPos++){
                                    JSONObject castObject = castArray.getJSONObject(castPos);
                                    Cast cast = new Cast();
                                    cast.setName(castObject.getString("name"));
                                    cast.setReal_name(castObject.getString("realName"));
                                    cast.setProfile(castObject.getString("profile"));
                                    cast.setCover(castObject.getString("cover"));
                                    cast.setAge(castObject.getInt("age"));
                                    JSONArray socialArray = castObject.getJSONArray("socialMedia");
                                    List<Cast.SocialMedia> socialMediaList = new ArrayList<>();
                                    for (int socialPost = 0; socialPost< socialArray.length(); socialPost++){
                                        JSONObject socialObject = socialArray.getJSONObject(socialPost);
                                        Cast.SocialMedia socialMedia = new Cast.SocialMedia();
                                        socialMedia.setType(socialObject.getString("type"));
                                        socialMedia.setName(socialObject.getString("name"));
                                        socialMedia.setValue(socialObject.getString("value"));
                                        socialMediaList.add(socialMedia);
                                    }
                                    cast.setSocialMedia(socialMediaList);
                                    cast.setBorn(castObject.getString("born"));
                                    cast.setSex(castObject.getString("gender"));
                                    castList.add(cast);
                                    data.setCastData(cast);
                                }
                                data.setCastList(castList);
                                data.setCastDetails(object.getString("castDetails"));
                                data.setMovieDetails(object.getString("movieDetails"));
                                childDataList.add(data);
                                if (currentMovie.equals(data.getTitle().toLowerCase())) {
                                    childDataList.remove(data);
                                }
                                setChildDataList(recyclerView, childDataList, childData);
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }
    @SuppressLint("SetTextI18n")
    private void setChildDataList(RecyclerView recyclerView, List<ChildData> childData, ChildData data){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChildAdapter adapter = new ChildAdapter(this, true, true);
        List<ChildData> filteredItems = BaseUtils.filterOnlyCategories(childData, data.getCategories());
        adapter.setFilter(filteredItems);

        recyclerView.setAdapter(adapter);
        adapter.sort("By Title");
        adapter.addAll(filteredItems);
        itemCount.setVisibility(View.GONE);
    }
    private void setCategoriesList(RecyclerView categoriesList, ChildData data){
        String[] categories = data.getCategories().split(",");
        List<String> categories2 = Arrays.asList(categories);
        List<ChipsReceived> categories3 = new ArrayList<>();
        for (int i = 0; i<categories2.size(); i++){
            ChipsReceived category = new ChipsReceived();
            category.setCategories(categories2.get(i));
            categories3.add(category);
        }
        categoriesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesList.setAdapter(new ChipsAdapter(this, categories3, true, true));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (identifyDownloadable != 0) {
                    ViewDownload viewDownload = new ViewDownload();
                    Bundle args = new Bundle();
                    args.putSerializable("data", childData);
                    args.putInt("position", secondPosition);
                    viewDownload.setArguments(args);
                    viewDownload.show(getSupportFragmentManager(), viewDownload.getTag());
                } else {
                    BaseUtils.showMessage(this, "Coming soon", Toast.LENGTH_SHORT);
                }
            } else {
                BaseUtils.showMessage(this, "Please Allow Permission Storage", Toast.LENGTH_LONG);
            }
        }
    }
    private void animateView(View view){
        SpringyAnimator springHelper = new SpringyAnimator(SpringAnimationType.SCALEXY,100,4,0,1);
        springHelper.startSpring(view);
    }
}