package com.rafslab.movie.dl.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rafslab.movie.dl.BuildConfig;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.ui.fragment.AccountFragment;
import com.rafslab.movie.dl.ui.fragment.HomeFragment;
import com.rafslab.movie.dl.ui.fragment.MoviesFragment;
import com.rafslab.movie.dl.ui.fragment.sheet.ViewCategories;
import com.rafslab.movie.dl.utils.BaseUtils;

import net.idik.lib.cipher.so.CipherClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.com.mauker.materialsearchview.MaterialSearchView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.joery.animatedbottombar.AnimatedBottomBar;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class HomeActivity extends AppCompatActivity {
    private static final int PERMISSION_STORAGE_CODE = 1000;
    private Toolbar toolbar;
    private AnimatedBottomBar bottomNavigation;
    private static final String currentVersion = BuildConfig.VERSION_NAME;
    private String title, description, changeLog, cover, versionValue, downloadValue, header;
    private int status, versionCode, countDownValue;
    private boolean isUpdate, isCountDown, isSearched;
    private MaterialSearchView searchBar;
    private Menu globalItem;
    private ImageView backgroundGradient;
    private LottieAnimationView dayNightSwitcher;
    boolean isThemeDay = true;
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    ConstraintLayout rootLayout;
    private ProgressBar progressBar;
    private FrameLayout fragmentHome;
    private int revealX;
    private int revealY;
    @SuppressLint("RestrictedApi")
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
        setContentView(R.layout.activity_home);
        initViews();
        backgroundGradient.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        String path = "response";
//        getLatestVersion(path);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new HomeFragment()).commit();
        setBottomNavigation(bottomNavigation);
        final Intent intent = getIntent();
        checkIntent(intent);
        rootLayout = findViewById(R.id.rootView);
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);
            revealX = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
    }
    private void showDialogBug(){
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitle("Bug");
        dialog.setContentText("Filter Rating & Categories");
        dialog.setConfirmButton("OK", SweetAlertDialog::dismissWithAnimation);
        dialog.showCancelButton(false);
        dialog.show();
    }
    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());
            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, finalRadius, 0);

        circularReveal.setDuration(400);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        circularReveal.start();
    }
    public static void BackgroundGradientAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
    private void showDialogUpdate(){
        boolean versionShame = currentVersion.equals(versionValue);
        if (!versionShame){
            String fileName = "RAFSLAB-" + versionValue + ".apk";
            SweetAlertDialog dialogUpdate = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            dialogUpdate.setTitle(title);
            dialogUpdate.setContentText(description);
            if (isUpdate) {
                dialogUpdate.setCancelable(false);
                dialogUpdate.setConfirmText("Download");
                dialogUpdate.setCancelText("Exit");
                dialogUpdate.setConfirmClickListener(sweetAlertDialog1 -> {
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, PERMISSION_STORAGE_CODE);
                        } else {
                            long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                            if (downloadFileRef != 0) {
                                BaseUtils.showMessage(this, "Memulai download...", Toast.LENGTH_SHORT);
                            } else {
                                BaseUtils.showMessage(this, "Download failed!", Toast.LENGTH_SHORT);
                            }
                            dialogUpdate.dismissWithAnimation();
                        }

                    } else {
                        long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                        if (downloadFileRef != 0) {
                            BaseUtils.showMessage(this, "Memulai download...", Toast.LENGTH_SHORT);
                        } else {
                            BaseUtils.showMessage(this, "Download failed!", Toast.LENGTH_SHORT);
                        }
                        dialogUpdate.dismissWithAnimation();
                    }

                });
                dialogUpdate.setCancelClickListener(sweetAlertDialog1 -> finishAffinity());
            } else {
                dialogUpdate.setCancelable(true);
                dialogUpdate.setConfirmText("Download");
                dialogUpdate.setCancelText("Cancel");
                dialogUpdate.setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
                dialogUpdate.setConfirmClickListener(sweetAlertDialog1 -> {
                    long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                    if (downloadFileRef != 0) {
                        BaseUtils.showMessage(this, "Memulai download...", Toast.LENGTH_SHORT);
                    } else {
                        BaseUtils.showMessage(this, "Download failed!", Toast.LENGTH_SHORT);
                    }
                    dialogUpdate.dismissWithAnimation();
                });
            }
            dialogUpdate.show();
        }
    }
    private void showDialogMaintenance(){
        SweetAlertDialog dialogMaintenance = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialogMaintenance.setTitle("Maintenance");
        dialogMaintenance.setContentText("Aplikasi sedang dalam perbaikan.");
        dialogMaintenance.setCancelable(false);
        dialogMaintenance.setConfirmText("Exit");
        dialogMaintenance.showCancelButton(false);
        dialogMaintenance.setConfirmClickListener(sweetAlertDialog -> System.exit(2));
        dialogMaintenance.show();
    }
    private void setBottomNavigation(AnimatedBottomBar bottomNavigation) {
        bottomNavigation.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                switch (i1){
                    case 0:
                        if (i >= 3){
                            bottomNavigation.removeTabById(R.id.search);
                            isSearched = true;
                        }
                        toolbar.setTitle(R.string.app_name);
                        toolbar.setNavigationIcon(null);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new HomeFragment()).commit();
                        break;
                    case 1:
                        if (i >= 3){
                            bottomNavigation.removeTabById(R.id.search);
                            isSearched = true;
                        }
                        toolbar.setTitle(R.string.app_name);
                        toolbar.setNavigationIcon(null);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new MoviesFragment()).commit();
                        break;
                    case 2:
                        if (i >= 3){
                            bottomNavigation.removeTabById(R.id.search);
                            isSearched = true;
                        }
                        toolbar.setNavigationIcon(null);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new AccountFragment()).addToBackStack(null).commit();
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });
    }
    public static void getBackPopStack(Context context){
        if (((HomeActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ((HomeActivity) context).getSupportFragmentManager().popBackStackImmediate();
        }
    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        searchBar = findViewById(R.id.search_bar);
        backgroundGradient = findViewById(R.id.background_gradient);
        progressBar = findViewById(R.id.progress_bar);
        fragmentHome = findViewById(R.id.fragment_home);
    }
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (searchBar.isOpen()) {
            searchBar.closeSearch();
        } else {
            if (count == 0 && bottomNavigation.getSelectedIndex() == 0) {
                super.onBackPressed();
            } else {
                bottomNavigation.selectTabAt(0, true);
            }
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            return false;
        }
        switch (item.getItemId()){
            case R.id.search:
                if (bottomNavigation.getTabCount() > 3) {
                    bottomNavigation.removeTabAt(3);
                }
                searchBar.openSearch();
                searchBar.adjustTintAlpha(0.8f);
                searchBar.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
                    @Override
                    public void onSearchViewOpened() {
                    }

                    @Override
                    public void onSearchViewClosed() {

                    }
                });
                searchBar.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(@NonNull String query) {
                        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                        intent.putExtra("query", query);
                        if (query.contains(",")) {
                            String[] queryArray = query.split(",");
                            List<String> queryList = new ArrayList<>(Arrays.asList(queryArray));
                            intent.putExtra("queryList", (Serializable) queryList);
                            startActivity(intent);
                        } else if (query.contains("-")) {
                            String[] queryArray = query.split("-");
                            double min = Double.parseDouble(queryArray[0]);
                            double max = Double.parseDouble(queryArray[1]);
                            intent.putExtra("type", "isRating");
                            intent.putExtra("min", min);
                            intent.putExtra("max", max);
                            startActivity(intent);
                        } else {
                            startActivity(intent);
                        }
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(@NonNull String newText) {
                        return false;
                    }
                });
                return false;
            case R.id.filter:
                ViewCategories filter = new ViewCategories();
                filter.show(getSupportFragmentManager(), filter.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static void enableMenuItem(Context context, @IdRes int id){
        Menu menu = ((HomeActivity)context).globalItem;
        menu.findItem(id).setVisible(true);
    }
    public static void disableMenuItem(Context context, @IdRes int id){
        Menu menu = ((HomeActivity)context).globalItem;
        menu.findItem(id).setVisible(false);
    }
    public static boolean checkMenuItem(Context context){
        Menu menu = ((HomeActivity) context).globalItem;
        return menu == null;
    }
    public static void showDialogUnderDevelopment(Context context){
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitle("Under Development");
        dialog.setContentText("Will be available soon.");
        dialog.setConfirmText("OK");
        dialog.showCancelButton(false);
        dialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        dialog.show();
    }
    private void getLatestVersion(String path){
        String URL = BaseUtils.getURLMaintenance(path);
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //get status
                            status = response.optInt("status");
                            JSONObject message = response.getJSONObject("message");
                            //get message body
                            JSONObject messageBody = message.getJSONObject("body");
                            title = messageBody.getString("title");
                            changeLog = messageBody.getString("changeLog");
                            description = messageBody.getString("description");
                            cover = messageBody.getString("cover");
                            //message header
                            header = message.getString("header");
                            //get update
                            isUpdate = response.getBoolean("update");
                            //get function
                            JSONObject functionObject = response.getJSONObject("function");
                            JSONObject versionObject = functionObject.getJSONObject("version");
                            versionValue = versionObject.getString("value");
                            versionCode = versionObject.getInt("code");

                            JSONObject downloadObject = functionObject.getJSONObject("download");
                            downloadValue = downloadObject.getString("value");

                            isCountDown = functionObject.getBoolean("countDown");
                            countDownValue = functionObject.getInt("value");
                            if (status == 1) {
                                showDialogUpdate();
                            } else if (status == 2) {
                                showDialogMaintenance();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    private long downloadFile(Uri uri, String fileName, String title, String description) {
        long downloadReference = 0;
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        try {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(title);
            request.setDescription("Downloading " + description);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.allowScanningByMediaScanner();
            request.setAllowedOverMetered(true);
            String fileStorageDestinationUri;
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                fileStorageDestinationUri = Environment.DIRECTORY_DOWNLOADS;
            } else {
                fileStorageDestinationUri = "RAFSLAB";
            }
            request.setDestinationInExternalPublicDir(fileStorageDestinationUri, fileName);
            if (downloadManager != null) {
                downloadReference = downloadManager.enqueue(request);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }
        return downloadReference;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        globalItem = menu;
        dayNightSwitcher = (LottieAnimationView) menu.findItem(R.id.day_night_item).getActionView();
        dayNightSwitcher.setMinAndMaxFrame(0, 36);
        dayNightSwitcher.setAnimation(R.raw.day_night);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(BaseUtils.IntegerToPixel(this, 26), BaseUtils.IntegerToPixel(this, 26));
        dayNightSwitcher.setLayoutParams(params);
        dayNightSwitcher.setOnClickListener(v->{
            if (isThemeDay) {
                dayNightSwitcher.setSpeed(1);
                dayNightSwitcher.playAnimation();
                BaseUtils.showMessage(this, "Light", Toast.LENGTH_SHORT);
                isThemeDay = false;
            } else {
                dayNightSwitcher.setSpeed(-1);
                dayNightSwitcher.playAnimation();
                BaseUtils.showMessage(this, "Dark", Toast.LENGTH_SHORT);
                isThemeDay = true;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String fileName = "RAFSLAB-" + versionValue + ".apk";
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                if (downloadFileRef != 0) {
                    BaseUtils.showMessage(this, "Memulai mengunduh...", Toast.LENGTH_SHORT);
                } else {
                    BaseUtils.showMessage(this, "Mengunduh gagal!", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    public void checkIntent(Intent intent){
        if (intent.hasExtra("click_action")) {
            progressBar.setVisibility(View.VISIBLE);
            fragmentHome.setVisibility(View.GONE);
            String path = Objects.requireNonNull(getIntent().getStringExtra("click_action")).replace(" ", "-").toLowerCase();
            getDataNotification(path);
        }
    }
    private void getDataNotification(String path){
        String URL = CipherClient.BASE_URL()
                + CipherClient.NOTIFICATION()
                + path
                + CipherClient.Extension();
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ChildData> dataFromNotification = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ChildData data = new ChildData();
                                data.setId(object.getInt("id"));
                                data.setTitle(object.getString("title"));
                                data.setSecondTitle(object.getString("2ndTitle"));
                                data.setStatus(object.getInt("status"));
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
                                    cast.setGender(castObject.getString("gender"));
                                    castList.add(cast);
                                    data.setCastData(cast);
                                }
                                data.setCastList(castList);
                                data.setCastDetails(object.getString("castDetails"));
                                data.setMovieDetails(object.getString("movieDetails"));
                                dataFromNotification.add(data);
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        new Handler().postDelayed(()-> {
                            progressBar.setVisibility(View.GONE);
                            Intent intent1 = new Intent(HomeActivity.this, DetailsActivity.class);
                            intent1.putExtra("childData", dataFromNotification.get(0));
                            startActivity(intent1);
                            finish();
                        }, 500);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
}