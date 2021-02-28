package com.rafslab.movie.dl.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.ChipsReceived;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.ui.fragment.sheet.ViewSorting;
import com.rafslab.movie.dl.utils.BaseUtils;

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

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ResultActivity extends AppCompatActivity {

    private final List<ChildData> dataMovies = new ArrayList<>();
    private final List<ChipsReceived> receiveList = new ArrayList<>();
    private List<String> receiveCategories;
    private List<String> receiveTags;
    private List<ChildData> filterRatingANDCategories = new ArrayList<>();
    private List<ChildData> filterOnlyCategories = new ArrayList<>();
    private List<ChildData> filterOnlyRating = new ArrayList<>();
    private List<ChildData> filterOnlyStatus = new ArrayList<>();

    private RecyclerView recyclerView, receivedCategoriesList, receivedTagList;
    private Toolbar toolbar;
    private String newValue;
    private String tagValue;
    private String sort, order, identity;
    private double max;
    private double min;
    private FloatingActionButton fabSorting;
    private LottieAnimationView noResultAnim;
    private ConstraintLayout noResultParent;
    int chipSize;
    int status;

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
        identity = getIntent().getStringExtra("identity");
        min = getIntent().getDoubleExtra("min_rating", 0);
        max = getIntent().getDoubleExtra("max_rating", 0);
        status = getIntent().getIntExtra("status", 0);
        receiveCategories = getIntent().getStringArrayListExtra("queryCategories");
        receiveTags = getIntent().getStringArrayListExtra("queryTags");
        chipSize = getIntent().getIntExtra("size", 0);
        SharedPreferences preferences = getSharedPreferences(SHARED_KEY, MODE_PRIVATE);
        sort = preferences.getString(SORT_KEY, "Name");
        order = preferences.getString(ORDER_KEY, "Ascending");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
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
        if (receiveCategories != null) {
            newValue = receiveCategories.toString().toLowerCase()
                    .replace("[", "")
                    .replace("]", "");
            setReceivedCategoriesData(receiveList, receiveCategories, receivedCategoriesList);
        }
        if (receiveTags != null && identity == null) {
            if (receiveTags.toString().contains("[]")) {
                receivedTagList.setVisibility(View.VISIBLE);
                tagValue = receiveTags.toString().toLowerCase()
                        .replace("[", "")
                        .replace("]", "");
                setTagListData(receivedTagList, receiveTags);
            } else {
                receivedTagList.setVisibility(View.GONE);
            }
        }
        setMovieDataList();
        fabSorting.setOnClickListener(v->{
            ViewSorting viewSorting = new ViewSorting();
            Bundle args = new Bundle();
            if (receiveCategories == null) {
                args.putSerializable("data", (Serializable) filterOnlyRating);
            } else {
                if (getIntent().hasExtra("min_rating") && getIntent().hasExtra("max_rating")) {
                    args.putSerializable("data", (Serializable) filterRatingANDCategories);
                } else {
                    args.putSerializable("data", (Serializable) filterOnlyCategories);
                }
            }
            if (getIntent().hasExtra("status")) {
                args.putSerializable("data", (Serializable) filterOnlyStatus);
            }
            args.putString("context", "ResultActivity");
            viewSorting.setArguments(args);
            viewSorting.show(getSupportFragmentManager(), viewSorting.getTag());
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences viewPrefs = getSharedPreferences("keyViewPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor viewEditor = viewPrefs.edit();
        String viewKey = "keyView";
        if (item.isChecked()) {
            return false;
        }
        if (item.getItemId() == R.id.add_more) {
            onBackPressed();
        }
        if (item.getGroupId() == R.id.group_view) {
            item.setChecked(false);
            if (item.getItemId() == R.id.grid_view) {
                setContentView(R.layout.activity_result);
                initViews();
                firstView();
                viewEditor.putString(viewKey, "Grid");
                setSupportActionBar(toolbar);
                fabSorting.setVisibility(View.VISIBLE);
                toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
                toolbar.setNavigationOnClickListener(v-> onBackPressed());
                ChildAdapter adapter = new ChildAdapter(this, dataMovies);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                recyclerView.swapAdapter(adapter, false);
                adapter.notifyDataSetChanged();
            } else if (item.getItemId() == R.id.list_view) {
                BaseUtils.showMessage(this, "Under development", Toast.LENGTH_SHORT);
            }
        }
        viewEditor.apply();
        return true;
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
        receivedTagList = findViewById(R.id.received_tags_list);
        toolbar = findViewById(R.id.toolbar);
        receivedCategoriesList = findViewById(R.id.received_categories_list);
        fabSorting = findViewById(R.id.sorting);
        noResultAnim = findViewById(R.id.no_result_animation);
        noResultParent = findViewById(R.id.no_result);
    }
    private void setMovieDataList(){
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + CipherClient.END();
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
                                dataMovies.add(data);
                                if (identity.equals("fromSheet")) {
                                    boolean onlyCategories = receiveCategories != null && min == 0.0 && max == 10.0;
                                    boolean onlyStatus = getIntent().hasExtra("status");
                                    boolean onlyTags = !(receiveTags.toString().contains("[]")) && !(onlyCategories && onlyStatus) && !identity.equals("fromDetail") && receiveTags != null;
                                    boolean onlyRating = receiveCategories.toString().contains("[]");
                                    boolean ratingCategories = receiveCategories != null && min >= 0.0 && max <= 10.0;
                                    boolean isAll = ratingCategories && getIntent().hasExtra("status");
                                    boolean isCategoriesTags = !(receiveTags.toString().contains("[]")) && onlyCategories;
                                    if (isAll) {
                                        setAll(recyclerView);
                                    } else {
                                        if (ratingCategories) {
                                            setRatingANDCategories(recyclerView);
                                        } else if (onlyCategories) {
                                            setOnlyCategories(recyclerView);
                                        } else if (onlyRating) {
                                            setOnlyRating(recyclerView, min, max);
                                        } else if (onlyStatus) {
                                            setOnlyStatus(recyclerView);
                                        } else if (onlyTags) {
                                            setOnlyTag(recyclerView);
                                        } else if (isCategoriesTags) {
                                            setTagsAndCategories(recyclerView);
                                        }
                                    }
                                } else {
                                    setOnlyCategories(recyclerView);
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
    private void setRatingANDCategories(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        list.setAdapter(adapter);
        filterRatingANDCategories = BaseUtils.filterRatingANDCategories(dataMovies, newValue, min, max);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterRatingANDCategories);
        adapter.addAll(filterRatingANDCategories);
        showAnimationNoResult(filterRatingANDCategories);
    }
    private void setAll(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        list.setAdapter(adapter);
        List<ChildData> filterAll = BaseUtils.filterAll(dataMovies, newValue, min, max, String.valueOf(status));
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterAll);
        adapter.addAll(filterAll);
        showAnimationNoResult(filterAll);
    }
    private void setOnlyTag(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        List<ChildData> filterOnlyTags = BaseUtils.filterOnlyTags(dataMovies, tagValue);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterOnlyTags);
        adapter.addAll(filterOnlyTags);
        adapter.notifyDataSetChanged();
        showAnimationNoResult(filterOnlyTags);
    }
    private void setOnlyRating(RecyclerView list, double min, double max){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        filterOnlyRating = BaseUtils.filterOnlyRating(dataMovies, min, max);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterOnlyRating);
        adapter.addAll(filterOnlyRating);
        adapter.notifyDataSetChanged();
        showAnimationNoResult(filterOnlyRating);
    }
    private void setOnlyCategories(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        filterOnlyCategories = BaseUtils.filterOnlyCategories(dataMovies, newValue);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterOnlyCategories);
        adapter.addAll(filterOnlyCategories);
        adapter.notifyDataSetChanged();
        showAnimationNoResult(filterOnlyCategories);
    }
    private void setOnlyStatus(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        filterOnlyStatus = BaseUtils.filterOnlyStatus(dataMovies, String.valueOf(status));
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterOnlyStatus);
        adapter.addAll(filterOnlyStatus);
        adapter.notifyDataSetChanged();
        showAnimationNoResult(filterOnlyStatus);
    }
    private void setTagsAndCategories(RecyclerView list){
        ChildAdapter adapter = new ChildAdapter(this, true);
        list.setLayoutManager(new GridLayoutManager(this,3));
        List<ChildData> filterTagsAndCategories = BaseUtils.filterTagsANDCategories(dataMovies, tagValue, newValue);
        adapter.sort(sort);
        adapter.order(order);
        list.setAdapter(adapter);
        sort(sort, order, filterTagsAndCategories);
        adapter.addAll(filterTagsAndCategories);
        adapter.notifyDataSetChanged();
    }
    private void setReceivedCategoriesData(List<ChipsReceived> chips, List<String> receivedChips, RecyclerView receivedCategoriesList){
        for (int i = 0; i< receivedChips.size(); i++){
            ChipsReceived data = new ChipsReceived();
            data.setCategories(receivedChips.get(i));
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
    private void setTagListData(RecyclerView tagList, List<String> receivedChips){
        List<ChipsReceived> chips = new ArrayList<>();
        for (int i = 0; i< receivedChips.size(); i++){
            ChipsReceived data = new ChipsReceived();
            data.setCategories(receivedChips.get(i));
            data.setPosition(i);
            chips.add(data);
        }
        setReceivedTagList(chips, tagList);
    }
    private void setReceivedTagList(List<ChipsReceived> receivedChips, RecyclerView receivedCategoriesList){
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