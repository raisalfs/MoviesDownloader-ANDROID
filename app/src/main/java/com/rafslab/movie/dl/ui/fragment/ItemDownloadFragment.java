package com.rafslab.movie.dl.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.EpisodeAdapter;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.model.child.ValueDownload;
import com.rafslab.movie.dl.utils.BaseUtils;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ItemDownloadFragment extends Fragment implements EpisodeAdapter.EpisodeListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    Toolbar toolbar;
    private View shadow;
    private ConstraintLayout downloadBatch, downloaded;

    public ItemDownloadFragment newInstance(ChildData childData, Download download, String path, int downloadPosition, int resolutionPosition){
        ItemDownloadFragment fragment = new ItemDownloadFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", childData);
        args.putSerializable("download", download);
        args.putString("path", path);
        args.putInt("download_position", downloadPosition);
        args.putInt("resolution_position", resolutionPosition);
        fragment.setArguments(args);
        return fragment;
    }
    public void getToolbar(Toolbar toolbar){
        this.toolbar = toolbar;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download_item, container, false);
        recyclerView = rootView.findViewById(R.id.batch_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        downloadBatch = rootView.findViewById(R.id.download_batch);
        downloaded = rootView.findViewById(R.id.downloaded);
        shadow = rootView.findViewById(R.id.shadow);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            ChildData childData = (ChildData) getArguments().getSerializable("data");
            String path = getArguments().getString("path");
            int resolutionPosition = getArguments().getInt("resolution_position");
            int downloadPosition = getArguments().getInt("download_position");
            if (childData != null) {
                Download download = (Download) getArguments().getSerializable("download");
                ResolutionValue resolutions = childData.getDownloads().get(downloadPosition).getResolution().getResolutionValues().get(resolutionPosition);
                ResolutionValue.Value value2 = resolutions.getValues();
                String URL = CipherClient.BASE_URL()
                        + resolutions.getName()
                        + path.replace(" ", "-")
                        + CipherClient.END();
                getEpisodeData(childData, URL, resolutions, download, value2);
            }
        }
        downloaded.setOnClickListener(v-> BaseUtils.showMessage(requireContext(), "Coming soon!", Toast.LENGTH_SHORT));
        downloadBatch.setOnClickListener(v-> BaseUtils.showMessage(requireContext(), "Coming soon!", Toast.LENGTH_SHORT));
    }
    private void getEpisodeData(ChildData childData, String URL, ResolutionValue resolutions, Download download, ResolutionValue.Value value2){
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ValueDownload> valueDownloads = new ArrayList<>();
                        for (int i = 0; i<response.length(); i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ValueDownload data = new ValueDownload();
                                data.setName(object.getInt("name"));
                                data.setValue(object.getString("value"));
                                valueDownloads.add(data);
                                progressBar.setVisibility(View.GONE);
                                setEpisodeData(childData, download, valueDownloads, recyclerView, resolutions, value2);
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
    private void setEpisodeData(ChildData childData, Download download, List<ValueDownload> downloads, RecyclerView recyclerView, ResolutionValue resolutions, ResolutionValue.Value value2){
        try {
            EpisodeAdapter adapter = new EpisodeAdapter(requireContext(), childData, download, downloads, resolutions, value2, this);
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 5));
            recyclerView.setAdapter(adapter);
            int listTop = recyclerView.getTop();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (recyclerView.getChildAt(0).getTop() < listTop) {
                        shadow.setVisibility(View.VISIBLE);
                    } else {
                        shadow.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setDownloadBatch(ChildData data, Download download, List<ValueDownload> downloads, ResolutionValue values, ResolutionValue.Value value2) {
    }
}
