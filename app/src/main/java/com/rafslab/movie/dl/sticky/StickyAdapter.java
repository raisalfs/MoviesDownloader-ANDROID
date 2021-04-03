package com.rafslab.movie.dl.sticky;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.ChildAdapter;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.Categories;
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

public class StickyAdapter extends ListAdapter<Categories, RecyclerView.ViewHolder> implements StickyHeaderDecoration.StickyListener {
    private final boolean isHeader;
    public StickyAdapter(boolean isHeader){
        super(ModelDiffUtilCallback);
        this.isHeader = isHeader;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root;
        if (isHeader) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
            return new StickyHeaderViewHolder(root);
        } else {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new StickyPostViewHolder(root);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isHeader) {
            ((StickyHeaderViewHolder) holder).bind(getItem(position));
        } else {
            ((StickyPostViewHolder) holder).bind(getItem(position));
        }
    }
    static class StickyHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public StickyHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
        public void bind(Categories model){
            title.setText(model.getGenre());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    static class StickyPostViewHolder extends RecyclerView.ViewHolder {
        RecyclerView itemList;
        ProgressBar progressBar;
        public StickyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            itemList = itemView.findViewById(R.id.root_list);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
        public void bind(Categories model){
            String path = model.getGenre();
            String URL = CipherClient.BASE_URL() + CipherClient.API_DIR() + "latest-updated" + CipherClient.END();
            Categories dataType = new Categories();
            dataType.setType(ItemType.Post);
            setResultList(itemList, progressBar, URL, path);
        }
        private void setResultList(RecyclerView resultList, ProgressBar progressBar, String URL, String categories){
            AndroidNetworking.get(URL)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            List<ChildData> dataList = new ArrayList<>();
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
                                    dataList.add(data);
                                    setSortingWithCategories(resultList, dataList, categories);
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
        private void setSortingWithCategories(RecyclerView resultList, List<ChildData> dataList, String categories){
            ChildAdapter adapter = new ChildAdapter(resultList.getContext(), true);
            resultList.setLayoutManager(new LinearLayoutManager(resultList.getContext(), LinearLayoutManager.HORIZONTAL, false));
            resultList.setAdapter(adapter);
            adapter.sort("By Title");
            adapter.order("Ascending");
            final List<ChildData> filteredItem = BaseUtils.setFilterSingleQuery(dataList, categories);
            adapter.addAll(filteredItem);
        }
    }
    @Override
    public int getHeaderPositionItem(int itemPosition) {
        int headerPosition = 0;
        for (int i = itemPosition; i > 0; i--){
            if (isHeader(i)) {
                headerPosition = i;
                return headerPosition;
            }
        }
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.header_item;
    }

    @Override
    public void bindHeaderData(View view, int headerPosition) {
        TextView title = view.findViewById(R.id.title);
        title.setText(getItem(headerPosition).getGenre());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItem(itemPosition).getType() == ItemType.Header;
    }
    public static final DiffUtil.ItemCallback<Categories> ModelDiffUtilCallback =
            new DiffUtil.ItemCallback<Categories>() {
                @Override
                public boolean areItemsTheSame(@NonNull Categories model, @NonNull Categories t1) {
                    return model.getGenre().equals(t1.getGenre());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull Categories model, @NonNull Categories t1) {
                    return model.equals(t1);
                }
            };
}