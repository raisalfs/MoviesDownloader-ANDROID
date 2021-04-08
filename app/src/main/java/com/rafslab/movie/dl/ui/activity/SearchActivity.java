package com.rafslab.movie.dl.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
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
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.adapter.ResultTestAdapter;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.Categories;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.utils.BaseUtils;
import com.rafslab.movie.dl.view.RecyclerItemDecoration;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchGrid;
    private ProgressBar progressBar;
    private ConstraintLayout noResult;
    private LottieAnimationView noResultAnimation;
    private Toolbar toolbar;
    private final List<Categories> categoriesList = new ArrayList<>();
    private String query, type;
    private double min, max;
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
        setContentView(R.layout.activity_search);
        initViews();
        query = getIntent().getStringExtra("query");
        type = getIntent().getStringExtra("type");
        min = getIntent().getDoubleExtra("min", 0);
        max = getIntent().getDoubleExtra("max", 0);
        List<String> queryList = getIntent().getStringArrayListExtra("queryList");
        if (queryList != null) {
            for (int i = 0; i< queryList.size(); i++){
                Categories categories = new Categories();
                categories.setGenre(queryList.get(i));
                categoriesList.add(categories);
            }
        }
        setSupportActionBar(toolbar);
        if (query != null) {
            String title;
            if (query.contains("-")) {
                title = "Rating: " + BaseUtils.formatSeekBar2(min) + " - " + BaseUtils.formatSeekBar2(max);
            } else {
                title = "[" + query + "]";
            }
            toolbar.setTitle(title);
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + CipherClient.DEFAULT()
                + CipherClient.Extension();
        getSearchData(URL);
    }
    private void getSearchData(String path){
        AndroidNetworking.get(path)
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
                                childDataList.add(data);
                                if (getIntent().hasExtra("queryList")) {
                                    setMultipleQuerySearchData(searchGrid);
                                } else if (type != null && type.equals("isRating")) {
                                    setSearchOnlyRating(searchGrid, childDataList);
                                } else {
                                    setSearchDataList(searchGrid, childDataList);
                                }
                                progressBar.setVisibility(View.GONE);
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
    private void setSearchDataList(RecyclerView recyclerView, List<ChildData> childData){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        ChildAdapter adapter = new ChildAdapter(this, true);
        adapter.sort("By Title");
        List<ChildData> filter = BaseUtils.setFilterSingleQuery(childData, query);
        adapter.addAll(filter);
        if (filter.isEmpty()) {
            noResult.setVisibility(View.VISIBLE);
            noResultAnimation.setAnimation(R.raw.emoji_140);
            noResultAnimation.playAnimation();
        } else {
            noResult.setVisibility(View.GONE);
            noResultAnimation.pauseAnimation();
        }
        recyclerView.setAdapter(adapter);
    }
    private void setSearchOnlyRating(RecyclerView recyclerView, List<ChildData> childData){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        ChildAdapter adapter = new ChildAdapter(this, true);
        List<ChildData> filter = BaseUtils.filterOnlyRating(childData, min, max);
        adapter.sort("By Rating");
        adapter.order("Descending");
        recyclerView.setAdapter(adapter);
        adapter.addAll(filter);
        adapter.notifyDataSetChanged();
        if (filter.isEmpty()) {
            noResult.setVisibility(View.VISIBLE);
            noResultAnimation.setAnimation(R.raw.emoji_140);
            noResultAnimation.playAnimation();
        } else {
            noResult.setVisibility(View.GONE);
            noResultAnimation.pauseAnimation();
        }
    }
    private void setMultipleQuerySearchData(RecyclerView searchGrid){
        searchGrid.setLayoutManager(new LinearLayoutManager(this));
        ResultTestAdapter adapterList = new ResultTestAdapter(this, categoriesList);
        searchGrid.setAdapter(adapterList);
        RecyclerItemDecoration decoration = new RecyclerItemDecoration(this, 2, true, getSectionCallback(categoriesList));
        searchGrid.addItemDecoration(decoration);
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
    private void initViews(){
        searchGrid = findViewById(R.id.search_grid);
        progressBar = findViewById(R.id.progress_bar);
        noResult = findViewById(R.id.no_result);
        noResultAnimation = findViewById(R.id.no_result_animation);
        toolbar = findViewById(R.id.toolbar);
    }
}