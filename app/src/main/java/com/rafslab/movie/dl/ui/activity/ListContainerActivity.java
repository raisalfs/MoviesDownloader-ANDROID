package com.rafslab.movie.dl.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.CastAdapter;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.ui.fragment.sheet.ViewCastSorting;
import com.rafslab.movie.dl.ui.fragment.sheet.ViewSorting;
import com.rafslab.movie.dl.utils.BaseUtils;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;
import static com.rafslab.movie.dl.ui.fragment.sheet.ViewCastSorting.ORDER_CAST_KEY;
import static com.rafslab.movie.dl.ui.fragment.sheet.ViewCastSorting.SHARED_CAST_KEY;
import static com.rafslab.movie.dl.ui.fragment.sheet.ViewCastSorting.SORT_CAST_KEY;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ListContainerActivity extends AppCompatActivity {
    private RecyclerView itemList;
    private Toolbar toolbar;
    private ImageButton sortOrder;
    private final List<ChildData> childDataList = new ArrayList<>();
    private final List<Cast> castList = new ArrayList<>();
    private String sort, order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_list_container);
        initView();
        if (sortOrder.getVisibility() == View.GONE) {
            sortOrder.setVisibility(View.VISIBLE);
        }
        SharedPreferences myPrefs = getSharedPreferences(SHARED_CAST_KEY, MODE_PRIVATE);
        order = myPrefs.getString(ORDER_CAST_KEY, "Ascending");
        sort = myPrefs.getString(SORT_CAST_KEY, "All");
        setSupportActionBar(toolbar);
        String path = getIntent().getStringExtra("path");
        if (getIntent().hasExtra("type")) {
            String type = getIntent().getStringExtra("type");
            if (type != null) {
                sortOrderFunction();
                sortOrder.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list));
                toolbar.setTitle(path);
                getItemCategoriesList(path);
            }
        } else {
            if (path != null) {
                if (path.contains("Cast")){
                    sortOrder.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list));
                    toolbar.setTitle(path);
                    getCastData(path);
                    sortCastFunction();
                } else {
                    sortOrderFunction();
                    sortOrder.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list));
                    toolbar.setTitle(path);
                    getItemList(path);
                }
            }
        }
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        toolbar.setNavigationOnClickListener(v-> onBackPressed());
    }
    private void sortOrderFunction(){
        sortOrder.setOnClickListener(v->{
            ViewSorting viewSorting = new ViewSorting();
            Bundle args = new Bundle();
            args.putSerializable("data", (Serializable) childDataList);
            args.putString("context", "ListContainerActivity");
            viewSorting.setArguments(args);
            viewSorting.show(getSupportFragmentManager(), viewSorting.getTag());
        });
    }
    private void sortCastFunction(){
        sortOrder.setOnClickListener(v->{
            ViewCastSorting viewSorting = new ViewCastSorting();
            Bundle args = new Bundle();
            args.putSerializable("data", (Serializable) castList);
            viewSorting.setArguments(args);
            viewSorting.show(getSupportFragmentManager(), viewSorting.getTag());
        });
    }
    private void initView(){
        itemList = findViewById(R.id.container_list);
        toolbar = findViewById(R.id.toolbar);
        sortOrder = findViewById(R.id.sort_order);
    }
    public static void refreshItems(Context context, String sort, String order, List<ChildData> dataList){
        ((ListContainerActivity)context).sort(sort, order, dataList);
    }
    public static void refreshCastItems(Context context, String sort, String order, List<Cast> castList){
        ((ListContainerActivity)context).sortByGender(castList, sort, order);
    }
    private List<Cast> filteredCastByGender(List<Cast> model, String query){
        final List<Cast> filteredList = new ArrayList<>();
        for (Cast cast : model){
            final String castGender = cast.getSex();
            if (query.contains(castGender)) {
                filteredList.add(cast);
            } else if (query.contains("All")) {
                filteredList.add(cast);
            }
        }
        return filteredList;
    }
    private void sortByGender(List<Cast> castList, String query, String order){
        CastAdapter adapter = new CastAdapter(this, true, true);
        if (query.contains("All")) {
            if (order.equals("Ascending")) {
                itemList.swapAdapter(adapter, false);
                adapter.sort("Ascending");
                adapter.addAll(castList);
                adapter.notifyDataSetChanged();
            } else if (order.equals("Descending")) {
                itemList.swapAdapter(adapter, false);
                adapter.sort("Descending");
                adapter.addAll(castList);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (order.equals("Ascending")) {
                itemList.swapAdapter(adapter, false);
                final List<Cast> filteredList = filteredCastByGender(castList, query);
                adapter.setFilter(filteredList);
                adapter.sort("Ascending");
                adapter.addAll(filteredList);
                adapter.notifyDataSetChanged();
            } else if (order.equals("Descending")) {
                itemList.swapAdapter(adapter, false);
                final List<Cast> filteredList = filteredCastByGender(castList, query);
                adapter.setFilter(filteredList);
                adapter.sort("Descending");
                adapter.addAll(filteredList);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void sort(String sort, String order, List<ChildData> data){
        ChildAdapter adapter = new ChildAdapter(this, true);
        if (sort.equals("Name")) {
            if (order.equals("Ascending")) {
                itemList.swapAdapter(adapter, false);
                adapter.sort("By Title");
                adapter.order("Ascending");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                itemList.swapAdapter(adapter, false);
                adapter.sort("By Title");
                adapter.order("Descending");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (order.equals("Ascending")) {
                itemList.swapAdapter(adapter, false);
                adapter.sort("By Rating");
                adapter.order("By Lowest");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                itemList.swapAdapter(adapter, false);
                adapter.sort("By Rating");
                adapter.order("By Highest");
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void getCastData(String path){
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + path.toLowerCase().replace(" ", "-")
                + CipherClient.END();
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i< response.length(); i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Cast cast = new Cast();
                                cast.setReal_name(object.getString("castName"));
                                cast.setCover(object.getString("cover"));
                                cast.setBorn(object.getString("born"));
                                List<Cast.SocialMedia> socialMedia = new ArrayList<>();
                                JSONArray socialArray = object.getJSONArray("socialMedia");
                                for (int i1 = 0; i1 < socialArray.length(); i1++){
                                    JSONObject object1 = socialArray.getJSONObject(i1);
                                    Cast.SocialMedia socialData = new Cast.SocialMedia();
                                    socialData.setType(object1.getString("type"));
                                    socialData.setName(object1.getString("name"));
                                    socialMedia.add(socialData);
                                }
                                cast.setSocialMedia(socialMedia);
                                cast.setSex(object.getString("gender"));
                                castList.add(cast);
                                setCastData(castList, itemList);
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
    private void setCastData(List<Cast> castList, RecyclerView recyclerView){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        CastAdapter adapter = new CastAdapter(this, true, true);
        recyclerView.setAdapter(adapter);
        final List<Cast> filteredItems = filteredCastByGender(castList, sort);
        adapter.sort(order);
        adapter.addAll(filteredItems);
        adapter.notifyDataSetChanged();
    }
    private void getItemList(String path){
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + path.toLowerCase().replace(" ", "-")
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
                                childDataList.add(data);
                                setDataList(itemList);
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
    private void setDataList(RecyclerView recyclerView){
        ChildAdapter adapter = new ChildAdapter(this, childDataList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }
    private void getItemCategoriesList(String categories){
        if (sortOrder.getVisibility() == View.VISIBLE) {
            sortOrder.setVisibility(View.GONE);
        }
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
                                childDataList.add(data);
                                setDataCategoriesList(itemList, categories);
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
    private void setDataCategoriesList(RecyclerView recyclerView, String categories){
        ChildAdapter adapter = new ChildAdapter(this, true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        List<ChildData> filteredCategories = BaseUtils.filterOnlyCategories(childDataList, categories);
        adapter.sort("By Title");
        adapter.addAll(filteredCategories);
    }
}