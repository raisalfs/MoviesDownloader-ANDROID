package com.rafslab.movie.dl.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.RootAdapter;
import com.rafslab.movie.dl.adapter.SliderAdapter;
import com.rafslab.movie.dl.model.RootData;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.Resolution;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.utils.BaseUtils;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChildHomeFragment extends Fragment {
    private SliderView sliderView;
    private RecyclerView recyclerView;
    private ImageView backgroundGradient;
    private NestedScrollView scrollable;
    private AppBarLayout appBarLayout;
    private ConstraintLayout rootView;
    private FrameLayout rootFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppBarLayout parentFragmentAppBar = null;

    public ChildHomeFragment newInstance(RootData data, int position){
        ChildHomeFragment fragment = new ChildHomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", data);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_home, container, false);
        sliderView = rootView.findViewById(R.id.child_slider);
        this.rootView = requireActivity().findViewById(R.id.rootView);
        recyclerView = rootView.findViewById(R.id.child_list);
        scrollable = rootView.findViewById(R.id.scrollable);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_to_refresh);
        appBarLayout = requireActivity().findViewById(R.id.app_bar);
        rootFragment = requireActivity().findViewById(R.id.fragment_home);
        backgroundGradient = requireActivity().findViewById(R.id.background_gradient);
        return rootView;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getParentFragment() != null) {
            parentFragmentAppBar = ((HomeFragment)getParentFragment()).getAppBarLayout();
        }
        Rect scrollBounds = new Rect();
        scrollable.getHitRect(scrollBounds);
        appBarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
        BaseUtils.getStatusBar(requireContext()).setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.transparent));
        rootView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
        rootFragment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
        parentFragmentAppBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
        if (getArguments() != null) {
            RootData data = (RootData) getArguments().getSerializable("data");
            if (data != null) {
                String URL = CipherClient.BASE_URL()
                        + CipherClient.ROOT_DIR()
                        + data.getTitle().toLowerCase()
                        + CipherClient.Extension();
                getDataSlider(URL);
                setDataList(data.getRootChildren());
                swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(()-> {
                    swipeRefreshLayout.setRefreshing(false);
                    getDataSlider(URL);
                    setDataList(data.getRootChildren());
                }, 1000));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollable.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (getParentFragment() != null) {
                    parentFragmentAppBar = ((HomeFragment)getParentFragment()).getAppBarLayout();
                }
                int color = ContextCompat.getColor(requireContext(), R.color.colorPrimary);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color) & 0xFF;

                int colorBackground = ContextCompat.getColor(requireContext(), R.color.background);
                int r2 = (colorBackground >> 16) & 0xFF;
                int g2 = (colorBackground >> 8) & 0xFF;
                int b2 = (colorBackground) & 0xFF;
                double scrollYPercent = ((((float) scrollY) / ((float) (sliderView.getHeight() - scrollBounds.height() + getStatusBarHeight(requireContext())))));
                if (scrollYPercent >= 0 && scrollYPercent <= 1) {
                    rootView.setBackgroundColor(Color.argb((int) (255 * scrollYPercent), r, g, b));
                    rootFragment.setBackgroundColor(Color.argb((int) (255 * scrollYPercent), r2, g2, b2));
                    appBarLayout.setBackgroundColor(Color.argb((int) (255 * scrollYPercent), r, g, b));
                    BaseUtils.getStatusBar(requireContext()).setStatusBarColor(Color.argb((int) (255 * scrollYPercent), r, g, b));
                    if (parentFragmentAppBar != null) {
                        parentFragmentAppBar.setBackgroundColor(Color.argb((int) (255 * scrollYPercent), r, g, b));
                    }
                }
//                if (sliderView != null) {
//                    if (!(sliderView.getLocalVisibleRect(scrollBounds))) {
//                        rootView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
//                        rootFragment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background));
//                        BaseUtils.getStatusBar(requireContext()).setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
//                        if (tab != null) {
//                            tab.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
//                        }
//                        backgroundGradient.setVisibility(View.GONE);
//                    } else {
//                        if (sliderView.getLocalVisibleRect(scrollBounds) || scrollBounds.height() < sliderView.getHeight()) {
//                            rootView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background));
//                            rootFragment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
//                            BaseUtils.getStatusBar(requireContext()).setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.transparent));
//                            if (tab != null) {
//                                tab.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
//                            }
//                            backgroundGradient.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        scrollable.fullScroll(View.FOCUS_UP);
//        scrollable.smoothScrollTo(0, 0);
        if (sliderView.getSliderAdapter() != null) {
            sliderView.dataSetChanged();
        }
    }

    public static int getStatusBarHeight(final Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
    }
    private void getDataSlider(String URL){
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ChildData> childDataList = new ArrayList<>();
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
                                childDataList.add(data);
                                getImageSlider(sliderView, childDataList);
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
    private void getImageSlider(SliderView sliderView, List<ChildData> rootList){
        try {
            sliderView.setSliderAdapter(new SliderAdapter(requireContext(), rootList, (data, position) -> Glide.with(requireActivity()).load(rootList.get(sliderView.getCurrentPagePosition()).getCoverArrays().get(0).getImage()).into(backgroundGradient)));
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setDataList(List<RootData.RootChild> rootChildList){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new RootAdapter(requireContext(), rootChildList));
    }
}
