package com.rafslab.movie.dl.ui.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rafslab.movie.dl.BuildConfig;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.ui.activity.HomeActivity;
import com.rafslab.movie.dl.ui.activity.OpenSourceLibraries;
import com.rafslab.movie.dl.utils.BaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.mauker.materialsearchview.MaterialSearchView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.rafslab.movie.dl.controller.AppController.NIGHT_MODE;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int PERMISSION_STORAGE_CODE = 1000;
    private String title, description, versionValue, downloadValue;
    private final String currentVersion = BuildConfig.VERSION_NAME;
    private int status;
    private boolean isUpdate;
    private boolean isSwitch = true;
    private MaterialSearchView searchView;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        searchView = new MaterialSearchView(requireContext());
        Preference appVersion = findPreference("check_updates");
        ListPreference changeTheme = findPreference(NIGHT_MODE);
        Preference findBug = findPreference("find_bug");
        Preference openSourceLicense = findPreference("open_source_license");
        Preference clearHistory = findPreference("clear_search_history");
        if (changeTheme != null) {
            changeTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                dialog();
                return isSwitch;
            });
        }
        if (findBug != null) {
            findBug.setOnPreferenceClickListener(preference -> {
                showDialogUnderDevelopment();
                return true;
            });
        }
        if (openSourceLicense != null) {
            openSourceLicense.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireContext(), OpenSourceLibraries.class));
                return true;
            });
        }
        if (appVersion != null) {
            appVersion.setSummary(currentVersion);
            appVersion.setOnPreferenceClickListener(preference -> {
                String path = "response";
                getLatestVersion(path);
                BaseUtils.showMessage(requireContext(), "Checking newest version", Toast.LENGTH_SHORT);
                new Handler().postDelayed(()->{
                    if (currentVersion.equals(versionValue)) {
                        showDialogUpToDate();
                    }
                },1000);
                return true;
            });
        }
        if (clearHistory != null) {
            clearHistory.setOnPreferenceClickListener(preference -> {
                deleteHistory(searchView);
                return true;
            });
        }
    }
    private void dialog(){
        AlertDialog.Builder dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog);
        dialog.setTitle("Theme");
        dialog.setMessage("After switching, the app will restart automatically. \nSwitch now?");

        dialog.setPositiveButton("Switch", (dialog1, which) -> {
            isSwitch = true;
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        dialog.setNegativeButton("Cancel", (dialog1, which) -> {
            isSwitch = false;
            dialog1.dismiss();
        });
        AlertDialog dialogs = dialog.create();
        dialogs.show();
        Button positive = dialogs.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative = dialogs.getButton(DialogInterface.BUTTON_NEGATIVE);
        positive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
        positive.setAllCaps(false);
        negative.setAllCaps(false);
        negative.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
    }
    private void showDialogUpToDate(){
        SweetAlertDialog dialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitle("Checking latest version");
        dialog.setContentText("App is up to date.");
        dialog.setConfirmText("OK");
        dialog.showCancelButton(false);
        dialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        dialog.show();
    }
    private void showDialogUnderDevelopment(){
        SweetAlertDialog dialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE);
        dialog.setTitle("Under Development");
        dialog.setContentText("Will be available soon.");
        dialog.setConfirmText("OK");
        dialog.showCancelButton(false);
        dialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        dialog.show();
    }
    private void getLatestVersion(String path){
        String URL = BaseUtils.getURLMaintenance(path);
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //get status
                            status = response.optInt("status");
                            JSONObject message = response.getJSONObject("message");
                            //get message body
                            JSONObject messageBody = message.getJSONObject("body");
                            title = messageBody.getString("title");
//                            changeLog = messageBody.getString("changeLog");
                            description = messageBody.getString("description");
//                            cover = messageBody.getString("cover");
                            //message header
//                            header = message.getString("header");
                            //get update
                            isUpdate = response.getBoolean("update");
                            //get function
                            JSONObject functionObject = response.getJSONObject("function");
                            JSONObject versionObject = functionObject.getJSONObject("version");
                            versionValue = versionObject.getString("value");
//                            versionCode = versionObject.getInt("code");
                            JSONObject downloadObject = functionObject.getJSONObject("download");
                            downloadValue = downloadObject.getString("value");

//                            isCountDown = functionObject.getBoolean("countDown");
//                            countDownValue = functionObject.getInt("value");
                            if (status == 1) {
                                showDialogUpdate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String fileName = "RAFSLAB-" + versionValue + ".apk";
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                if (downloadFileRef != 0) {
                    BaseUtils.showMessage(requireContext(), "Memulai mengunduh...", Toast.LENGTH_SHORT);
                } else {
                    BaseUtils.showMessage(requireContext(), "Mengunduh gagal!", Toast.LENGTH_SHORT);
                }
            }
        }
    }
    private void showDialogUpdate(){
        boolean versionShame = currentVersion.equals(versionValue);
        if (!versionShame){
            String fileName = "RAFSLAB-" + versionValue + ".apk";
            SweetAlertDialog dialogUpdate = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
            dialogUpdate.setTitle(title);
            dialogUpdate.setContentText(description);
            if (isUpdate) {
                dialogUpdate.setCancelable(false);
                dialogUpdate.setConfirmText("Download");
                dialogUpdate.setCancelText("Exit");
                dialogUpdate.setConfirmClickListener(sweetAlertDialog1 -> {
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                        if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, PERMISSION_STORAGE_CODE);
                        } else {
                            long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                            if (downloadFileRef != 0) {
                                BaseUtils.showMessage(requireContext(), "Memulai download...", Toast.LENGTH_SHORT);
                            } else {
                                BaseUtils.showMessage(requireContext(), "Download failed!", Toast.LENGTH_SHORT);
                            }
                            dialogUpdate.dismissWithAnimation();
                        }

                    } else {
                        long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                        if (downloadFileRef != 0) {
                            BaseUtils.showMessage(requireContext(), "Memulai download...", Toast.LENGTH_SHORT);
                        } else {
                            BaseUtils.showMessage(requireContext(), "Download failed!", Toast.LENGTH_SHORT);
                        }
                        dialogUpdate.dismissWithAnimation();
                    }

                });
                dialogUpdate.setCancelClickListener(sweetAlertDialog1 -> requireActivity().finishAffinity());
            } else {
                dialogUpdate.setCancelable(true);
                dialogUpdate.setConfirmText("Download");
                dialogUpdate.setCancelText("Cancel");
                dialogUpdate.setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
                dialogUpdate.setConfirmClickListener(sweetAlertDialog1 -> {
                    long downloadFileRef = downloadFile(Uri.parse(downloadValue), fileName, title, description);
                    if (downloadFileRef != 0) {
                        BaseUtils.showMessage(requireContext(), "Memulai download...", Toast.LENGTH_SHORT);
                    } else {
                        BaseUtils.showMessage(requireContext(), "Download failed!", Toast.LENGTH_SHORT);
                    }
                    dialogUpdate.dismissWithAnimation();
                });
            }
            dialogUpdate.show();
        }
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
            e.printStackTrace();

        }
        return downloadReference;
    }
    private void deleteHistory(MaterialSearchView searchView){
        SweetAlertDialog dialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
        dialog.setTitle("Delete History");
        dialog.setContentText("Delete all search history ?");
        dialog.setConfirmText("Delete");
        dialog.setCancelText("Cancel");
        dialog.setConfirmClickListener(sweetAlertDialog -> {
           searchView.clearHistory();
            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
           new Handler().postDelayed(()-> sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE),500);
           sweetAlertDialog.setContentText("History Deleted");
           sweetAlertDialog.setConfirmText("OK");
            sweetAlertDialog.showCancelButton(false);
            sweetAlertDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        });
        dialog.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
