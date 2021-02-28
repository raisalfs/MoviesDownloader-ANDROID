package com.rafslab.movie.dl.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.Download;
import com.rafslab.movie.dl.model.child.ResolutionValue;
import com.rafslab.movie.dl.model.child.ValueDownload;
import com.rafslab.movie.dl.model.user.Report;
import com.rafslab.movie.dl.services.MySingleton;
import com.rafslab.movie.dl.utils.BaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {
    private final Context mContext;
    private final List<ValueDownload> downloads;
    private final ResolutionValue values;
    private final ResolutionValue.Value value2;
    private final LayoutInflater inflater;
    private final Download download;
    ChildData childData;
    private String reasonDownload;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "your key";
    final private String contentType = "application/json";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    private final EpisodeListener listener;

    public EpisodeAdapter(Context mContext, ChildData childData, Download download, List<ValueDownload> downloads, ResolutionValue values, ResolutionValue.Value value2, EpisodeListener listener) {
        this.childData = childData;
        this.mContext = mContext;
        this.downloads = downloads;
        this.values = values;
        this.download = download;
        this.value2 = value2;
        this.listener = listener;
        inflater = LayoutInflater.from(mContext);
    }
    public interface EpisodeListener{
        void setDownloadBatch(ChildData data, Download download, List<ValueDownload> downloads, ResolutionValue values, ResolutionValue.Value value2);
    }
    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodeViewHolder(inflater.inflate(R.layout.episode_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        ValueDownload data = downloads.get(position);
        String eps = data.getName() + "";
        holder.episode_container.setText(eps);
        listener.setDownloadBatch(childData, download, downloads, values, value2);
        holder.episode_container.setOnClickListener(v-> setDownload(childData, download, values, data));
    }

    @Override
    public int getItemCount() {
        return downloads.size();
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton episode_container;
        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            episode_container = itemView.findViewById(R.id.episode_container);
        }
    }
    private long downloadFile(Uri uri, String fileName, String title, String description) {
        long downloadReference = 0;
        DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(DOWNLOAD_SERVICE);
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
    private void setDownload(ChildData childData, Download download, ResolutionValue value, ValueDownload valueDownload){
        String fileName = childData.getTitle();
        String downloadTitle = childData.getTitle() + "-" + "Episode "+valueDownload.getName() + "-" + value.getName();
        String downloadDesc = "Downloading " + childData.getTitle() + "-" + valueDownload.getName();
        long downloadFileRef = downloadFile(Uri.parse(valueDownload.getValue()), fileName, downloadTitle, downloadDesc);
        if (downloadFileRef != 0) {
            BaseUtils.showMessage(mContext, "Memulai download...", Toast.LENGTH_SHORT);
        } else {
            String title = childData.getTitle();
            String episode1 = valueDownload.getName() + "";
            String season = download.getName();
            String resolution = value.getName();
            String reason = reasonDownload;
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date date = new Date();
            FirebaseApp.initializeApp(mContext);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference getReference = database.getReference();
            new Handler().postDelayed(()-> getReference.child("Report").child(dateFormat.format(date).replace("/", "-")).push().setValue(new Report(title, episode1, season, resolution, reason)).addOnSuccessListener(aVoid -> sendNotification(title)), 1000);
            showDialog();
        }
    }
    private void sendNotification(String message){
        String TOPIC = "/topics/superUSER";
        NOTIFICATION_TITLE = "Broken Link!";
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
        }, error -> BaseUtils.showMessage(mContext, error.getMessage(), Toast.LENGTH_SHORT)){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }
    private void showDialog(){
        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
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
                            BaseUtils.showMessage(mContext, "Terimakasih atas laporannya, link akan segera diperbaiki :)", Toast.LENGTH_SHORT);
                        },1500);
                    }
                })
                .show();
    }
}
