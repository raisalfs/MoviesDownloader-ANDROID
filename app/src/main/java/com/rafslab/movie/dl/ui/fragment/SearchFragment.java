package com.rafslab.movie.dl.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.appbar.AppBarLayout;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.utils.BaseUtils;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView searchList;
    private ProgressBar progressBar;
    private ImageView backgroundGradient;
    private AppBarLayout contextAppBar;
    private ConstraintLayout noResult;
    private LottieAnimationView noResultAnimation;
    private final List<ChildData> childDataList = new ArrayList<>();

    public SearchFragment newInstance(String query){
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchList = rootView.findViewById(R.id.search_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        noResult = rootView.findViewById(R.id.no_result);
        noResultAnimation = rootView.findViewById(R.id.no_result_animation);
        backgroundGradient = requireActivity().findViewById(R.id.background_gradient);
        contextAppBar = requireActivity().findViewById(R.id.app_bar);
        return rootView;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backgroundGradient.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.background));
        contextAppBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        BaseUtils.getStatusBar(requireContext()).setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        BaseUtils.getActionBar(requireContext()).show();
        if (getArguments() != null) {
            String query = getArguments().getString("query");
            String customTitle = "[ " + query + " ]";
            BaseUtils.getActionBar(requireContext()).setTitle(customTitle);
        }
        String URL = CipherClient.BASE_URL()
                + CipherClient.API_DIR()
                + CipherClient.END();
        getSearchData(URL, searchList);
    }
    private void getSearchData(String path, RecyclerView recyclerView){
        AndroidNetworking.get(path)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                                setSearchDataList(recyclerView, childDataList);
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
        if (getArguments() != null) {
            try {
                String query = getArguments().getString("query");
                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                ChildAdapter adapter = new ChildAdapter(requireContext(), true);
                adapter.sort("By Title");
                List<ChildData> filter = BaseUtils.filterAll(childData, query);
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
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
