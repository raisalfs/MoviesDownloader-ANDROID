package com.rafslab.movie.dl.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.adapter.ChipsAdapter;
import com.rafslab.movie.dl.adapter.ResultTestAdapter;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.Categories;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.ChipsReceived;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.ui.fragment.sheet.ViewSorting;
import com.rafslab.movie.dl.utils.BaseUtils;
import com.rafslab.movie.dl.view.RecyclerItemDecoration;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;
import static com.rafslab.movie.dl.ui.fragment.sheet.ViewSorting.ORDER_KEY;
import static com.rafslab.movie.dl.ui.fragment.sheet.ViewSorting.SHARED_KEY;
import static com.rafslab.movie.dl.ui.fragment.sheet.ViewSorting.SORT_KEY;


public class ResultActivity extends AppCompatActivity {

    private final List<ChildData> dataMovies = new ArrayList<>();
    private final List<ChipsReceived> receiveList = new ArrayList<>();
    private List<String> queryList = new ArrayList<>();
    private List<ChildData> filtering = new ArrayList<>();

    private RecyclerView recyclerView, receivedQueryList;
    private Toolbar toolbar;
    private String sort, order, identity;
    private FloatingActionButton fabSorting;
    private LottieAnimationView noResultAnim;
    private ConstraintLayout noResultParent;
    private double min, max;
    private boolean isAll;
    private final String viewKey = "keyView";
    boolean isGrid = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
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
        setContentView(R.layout.activity_result);
        initViews();
        min = getIntent().getDoubleExtra("min", 0);
        max = getIntent().getDoubleExtra("max", 0);
        isAll = getIntent().getBooleanExtra("all", true);
        identity = getIntent().getStringExtra("identity");
        queryList = getIntent().getStringArrayListExtra("queryList");
        SharedPreferences preferences = getSharedPreferences(SHARED_KEY, MODE_PRIVATE);
        sort = preferences.getString(SORT_KEY, "Name");
        order = preferences.getString(ORDER_KEY, "Ascending");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
        LottieAnimationView userView = findViewById(R.id.change_view);
        userView.setAnimation(R.raw.grid_list);
        userView.setMinAndMaxFrame(80, 150);
        userView.setFrame(150);
        SharedPreferences viewPrefs = getSharedPreferences("keyViewPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor viewEditor = viewPrefs.edit();
        userView.setOnClickListener(v->{
            userView.setFrame(80);
            if (isGrid){
                userView.setSpeed(-1);
                List<Categories> categories = new ArrayList<>();
                for (int i = 0; i <queryList.size(); i++){
                    Categories data = new Categories();
                    data.setGenre(queryList.get(i));
                    categories.add(data);
                }
                receivedQueryList.setVisibility(View.GONE);
                viewEditor.putString(viewKey, "List");
                fabSorting.setVisibility(View.GONE);
                ResultTestAdapter adapter = new ResultTestAdapter(this, categories);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
                RecyclerItemDecoration decoration = new RecyclerItemDecoration(this, 1, true, getSectionCallback(categories));
                recyclerView.addItemDecoration(decoration);
                isGrid = false;
            } else {
                userView.setSpeed(1);
                receivedQueryList.setVisibility(View.VISIBLE);
                viewEditor.putString(viewKey, "Grid");
                fabSorting.setVisibility(View.VISIBLE);
                ChildAdapter adapter = new ChildAdapter(this, dataMovies);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                recyclerView.setAdapter(adapter);
                while (recyclerView.getItemDecorationCount() > 0){
                    recyclerView.removeItemDecorationAt(0);
                }
                isGrid = true;
            }
            viewEditor.apply();
            userView.playAnimation();
        });
//        boolean ratingCategories = receiveCategories != null && getIntent().hasExtra("min_rating") && getIntent().hasExtra("max_rating");
//        boolean onlyCategories = receiveCategories != null && !(getIntent().hasExtra("min_rating") && !(getIntent().hasExtra("max_rating")));
//        boolean onlyRating = receiveCategories == null;
//        boolean onlyComplete = getIntent().hasExtra("status") && status == 1;
//        boolean onlyOnGoing = getIntent().hasExtra("status") && status == 0;
//        boolean isAll = getIntent().hasCategory("status") && ratingCategories;
//        if (isAll){
//            String titleToolbar = "Rating: " + BaseUtils.formatSeekBar((float) min) + " ";
//            if (onlyComplete) {
//                toolbar.setTitle(titleToolbar + Html.fromHtml("&#10142") + " " + BaseUtils.formatSeekBar((float) max) + ", Complete");
//            } else {
//                toolbar.setTitle(titleToolbar + Html.fromHtml("&#10142") + " " + BaseUtils.formatSeekBar((float) max) + ", On Going");
//            }
//        } else {
//            if (onlyCategories) {
//                String titleToolbar = "Sort Only Categories";
//                toolbar.setTitle(titleToolbar);
//            }
//            if (onlyComplete){
//                String titleToolbar = "Status: Complete";
//                toolbar.setTitle(titleToolbar);
//            }
//            if (onlyOnGoing) {
//                String titleToolbar = "Status: On Going";
//                toolbar.setTitle(titleToolbar);
//            }
//            if (onlyRating) {
//                String titleToolbar = "Rating: " + BaseUtils.formatSeekBar((float) min) + " ";
//                toolbar.setTitle(titleToolbar + Html.fromHtml("&#10142") + " " + BaseUtils.formatSeekBar((float) max));
//            }
//            if (min == 0.0 && max == 10.0){
//                String titleToolbar;
//                if (onlyComplete){
//                    titleToolbar = "Status: Complete";
//                } else {
//                    if (onlyOnGoing) {
//                        titleToolbar = "Status: On Going";
//                    } else {
//                        titleToolbar = "Sort Only Categories";
//                    }
//                }
//                toolbar.setTitle(titleToolbar);
//            }
//        }
        firstView();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        if (identity != null) {
            if (identity.equals("fromDetail")) {
                menu.findItem(R.id.add_more).setVisible(false);
                toolbar.setTitle("Categories");
            }
        }
        return true;
    }
    private void firstView(){
        initViews();
        setReceivedCategoriesData(receiveList, queryList, receivedQueryList);
        setMovieDataList();
        fabSorting.setOnClickListener(v->{
            ViewSorting viewSorting = new ViewSorting();
            Bundle args = new Bundle();
            args.putSerializable("data", (Serializable) filtering);
            args.putString("context", "ResultActivity");
            viewSorting.setArguments(args);
            viewSorting.show(getSupportFragmentManager(), viewSorting.getTag());
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            return false;
        }
        if (item.getItemId() == R.id.add_more) {
            onBackPressed();
        }
        return true;
    }
    private RecyclerItemDecoration.SectionCallback getSectionCallback(final List<Categories> categories) {
        return new RecyclerItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0 || categories.get(position) != categories.get(position -1);
            }

            @Override
            public String getSectionHeaderName(int position) {
                return categories.get(position).getGenre();
            }
        };
    }
    public static void refreshItems(Context context, String sort, String order, List<ChildData> dataList){
        ((ResultActivity)context).sort(sort, order, dataList);
    }
    private void sort(String sort, String order, List<ChildData> data){
        ChildAdapter adapter = new ChildAdapter(this, true);
        if (sort.equals("Name")) {
            if (order.equals("Ascending")) {
                recyclerView.swapAdapter(adapter, false);
                adapter.sort("By Title");
                adapter.order("Ascending");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                recyclerView.swapAdapter(adapter, false);
                adapter.sort("By Title");
                adapter.order("Descending");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (order.equals("Ascending")) {
                recyclerView.swapAdapter(adapter, false);
                adapter.sort("By Rating");
                adapter.order("By Lowest");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                recyclerView.swapAdapter(adapter, false);
                adapter.sort("By Rating");
                adapter.order("By Highest");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void initViews(){
        recyclerView = findViewById(R.id.result_list);
        toolbar = findViewById(R.id.toolbar);
        receivedQueryList = findViewById(R.id.received_categories_list);
        fabSorting = findViewById(R.id.sorting);
        noResultAnim = findViewById(R.id.no_result_animation);
        noResultParent = findViewById(R.id.no_result);
    }
    private void setMovieDataList(){
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + CipherClient.DEFAULT()
                + CipherClient.Extension();
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i<response.length(); i++){
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
                                dataMovies.add(data);
                                if (identity.equals("fromDetail")) {
                                    filterFromDetail(recyclerView);
                                } else {
                                    filterAll(recyclerView);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    private void filterAll(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        list.setAdapter(adapter);
        filtering = BaseUtils.setFilterMultipleQuery(dataMovies, queryList, min, max, isAll);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filtering);
        adapter.addAll(filtering);
        showAnimationNoResult(filtering);
    }
    private void filterFromDetail(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        list.setAdapter(adapter);
        String query = queryList.toString().replace("[", "").replace("]", "").toLowerCase();
        filtering = BaseUtils.filterOnlyCategories(dataMovies, query);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filtering);
        adapter.addAll(filtering);
        showAnimationNoResult(filtering);
    }
    private void setReceivedCategoriesData(List<ChipsReceived> chips, List<String> receivedChips, RecyclerView receivedCategoriesList){
        for (int i = 0; i<receivedChips.size(); i++){
            ChipsReceived data = new ChipsReceived();
            if (receivedChips.get(i).equals("1")) {
                data.setCategories("Status: Complete");
            } else if (receivedChips.get(i).equals("0")) {
                data.setCategories("Status: On going");
            } else {
                data.setCategories(receivedChips.get(i));
            }
            data.setPosition(i);
            chips.add(data);
        }
        setReceivedCategoriesList(chips, receivedCategoriesList);
    }
    private void setReceivedCategoriesList(List<ChipsReceived> receivedChips, RecyclerView receivedCategoriesList){
        ChipsAdapter adapter = new ChipsAdapter(this, receivedChips, true, false);
        receivedCategoriesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        receivedCategoriesList.setAdapter(adapter);
    }
    private void showAnimationNoResult(List<ChildData> dataList){
        if (dataList.isEmpty()) {
            noResultParent.setVisibility(View.VISIBLE);
            noResultAnim.setAnimation(R.raw.emoji_140);
            noResultAnim.playAnimation();
        } else {
            noResultParent.setVisibility(View.GONE);
            noResultAnim.pauseAnimation();
        }
    }
}