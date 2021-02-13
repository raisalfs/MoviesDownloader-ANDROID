package com.rafslab.movie.dl.ui.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.HistoryAdapter;
import com.rafslab.movie.dl.database.DownloadedHistoryHelper;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.ui.activity.HomeActivity;
import com.rafslab.movie.dl.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class DownloadedFragment extends Fragment implements HistoryAdapter.Callback {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    DownloadedHistoryHelper downloadedHistoryHelper;
    private List<ChildData> childDataList = new ArrayList<>();
    private HistoryAdapter adapter2;
    private ProgressBar progressBar;
    private ImageView backgroundGradient;
    private AppBarLayout contextAppBar;
    Toolbar contextToolbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download, container, false);
        recyclerView = rootView.findViewById(R.id.history_list);
        refreshLayout = rootView.findViewById(R.id.refresh);
        progressBar = rootView.findViewById(R.id.progress_bar);
        backgroundGradient = requireActivity().findViewById(R.id.background_gradient);
        contextAppBar = requireActivity().findViewById(R.id.app_bar);
        contextToolbar = requireActivity().findViewById(R.id.toolbar);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backgroundGradient.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.background));
        contextAppBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        BaseUtils.getStatusBar(requireContext()).setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        BaseUtils.getActionBar(requireContext()).show();
        BaseUtils.getActionBar(requireContext()).setTitle("Downloaded");
        contextToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        contextToolbar.setNavigationOnClickListener(v-> {
            HomeActivity.getBackPopStack(requireContext());
            contextToolbar.setNavigationIcon(null);
        });
        downloadedHistoryHelper = new DownloadedHistoryHelper(requireContext());
        loadBookmark(childDataList);

        refreshLayout.setOnRefreshListener(() -> {
            loadBookmark(childDataList);
            new Handler().postDelayed(()->{
                refreshLayout.setRefreshing(false);
                if (childDataList.isEmpty()){
                    BaseUtils.showMessage(requireContext(), "No data found!", Toast.LENGTH_SHORT);
                }
            },300);
        });
        if (childDataList.isEmpty()) {
            checkFavorites();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        loadBookmark(childDataList);
    }

    private void checkFavorites(){
        loadBookmark(childDataList);
    }
    private void loadBookmark(List<ChildData> dataList){
        adapter2 = new HistoryAdapter(requireContext(), dataList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
        downloadedHistoryHelper = new DownloadedHistoryHelper(requireContext());
        getAllHistory();
        progressBar.setVisibility(View.GONE);
    }
    @SuppressLint("StaticFieldLeak")
    private void getAllHistory(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                childDataList.clear();
                childDataList.addAll(downloadedHistoryHelper.getAllHistory());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter2.notifyDataSetChanged();

            }
        }.execute();
    }

    @Override
    public void getHistoryDownloaded(boolean isChecked, int position, int selectedItem) {
        String getSelected = selectedItem + " selected";
        downloadedHistoryHelper = new DownloadedHistoryHelper(requireContext());
    }
}
