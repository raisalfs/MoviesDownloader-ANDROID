package com.rafslab.movie.dl.ui.fragment;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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

import com.android.volley.toolbox.JsonObjectRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.EpisodeAdapter;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.model.child.ValueDownload;
import com.rafslab.movie.dl.model.user.Report;
import com.rafslab.movie.dl.services.MySingleton;
import com.rafslab.movie.dl.utils.BaseUtils;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ItemDownloadFragment extends Fragment implements EpisodeAdapter.EpisodeListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    Toolbar toolbar;
    private View shadow;
    private ConstraintLayout downloadBatch, downloaded;

    private String reasonDownload;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAUluU7WA:APA91bGbkNWr19S0eTCQrjppxNdV2iCWzhRCfRLz71A-9B8_olNLqlS4BCbwIr91VSLYxlif2fR2yvtGi3JPCrgwH58l1bqa2wxezCzXoncEs6yaNHGZq1ZrOldFsINcr5iQQhqbqld4";
    final private String contentType = "application/json";
    String TOPIC;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;

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
                        + "Series/Download/"
                        + resolutions.getName()
                        + "/"
                        + path.replace(" ", "-")
                        + CipherClient.Extension();
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
//     downloadBatch.setOnClickListener(v-> setDownloadBatch(data, download, values, value2));
    }
    private long downloadFile(Uri uri, String fileName, String title, String description) {
        long downloadReference = 0;

        DownloadManager downloadManager = (DownloadManager)requireContext().getSystemService(DOWNLOAD_SERVICE);
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
            reasonDownload = e.getMessage();

        }
        return downloadReference;
    }
    private void setDownloadBatch(ChildData childData, Download download, ResolutionValue value, ResolutionValue.Value value2){
        String fileName = childData.getTitle();
        String downloadTitle = childData.getTitle() + "-" + download.getName() + "-" + value.getName();
        String downloadDesc = "Downloading " + childData.getTitle() + "-" + download.getName() + "-" + value.getName();
        long downloadFileRef = downloadFile(Uri.parse(value2.getBatch()), fileName, downloadTitle, downloadDesc);
        if (downloadFileRef != 0) {
            BaseUtils.showMessage(requireContext(), "Memulai download...", Toast.LENGTH_SHORT);
        } else {
            String title = childData.getTitle();
            String episode1 = "Batch";
            String season = download.getName();
            String resolution = value.getName();
            String reason = reasonDownload;
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date date = new Date();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference getReference = database.getReference();
            getReference.child("Report").child(dateFormat.format(date).replace("/", "-")).push().setValue(new Report(title, episode1, season, resolution, reason)).addOnSuccessListener(aVoid -> sendNotification("Broken Link!", title));
            showDialog();
        }
    }
    private void sendNotification(String title, String message){
        TOPIC = "/topics/superUSER";
        NOTIFICATION_TITLE = title;
        NOTIFICATION_MESSAGE = message;
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e("ServerAdapter", "sendNotification: " + e.getMessage() );
        }
        Notification(notification);
    }
    private void Notification(JSONObject notification) {
        JsonObjectRequest request = new JsonObjectRequest(FCM_API, notification, response -> {
            Log.i("ServerAdapter", "onResponse: " + response.toString());
            NOTIFICATION_TITLE = "";
            NOTIFICATION_MESSAGE = "";
        }, error -> BaseUtils.showMessage(requireContext(), error.getMessage(), Toast.LENGTH_SHORT)){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
    private void showDialog(){
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Broken link")
                .setContentText("Laporkan link ke Admin.")
                .setConfirmText("Laporkan")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog
                            .setTitleText("Loading...")
                            .setContentText("Sedang mengirim laporan ke Admin.")
//                            .hideConfirmButton()
                            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    if (sweetAlertDialog.isShowing() && sweetAlertDialog.getProgressHelper().isSpinning()) {
                        new Handler().postDelayed(()->{
                            sweetAlertDialog.setTitle("Berhasil!");
                            sweetAlertDialog.setContentText("Berhasil mengirim laporan.");
                            sweetAlertDialog.setConfirmText("OK");
                            sweetAlertDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            BaseUtils.showMessage(requireContext(), "Terimakasih atas laporannya, link akan segera diperbaiki :)", Toast.LENGTH_SHORT);
                        },1500);
                    }
                })
                .show();
    }
}
