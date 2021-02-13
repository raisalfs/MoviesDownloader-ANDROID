package com.rafslab.movie.dl.utils;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.rafslab.movie.dl.ui.activity.HomeActivity;

import net.idik.lib.cipher.so.CipherClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AntiReverseUtils {

	public Context mContext;
	private static final int PERMISSION_STORAGE_CODE = 1000;
	
	public AntiReverseUtils(Context ctx){
		mContext = ctx;
	}
	
	public void checkSign(final HomeActivity context, String URL, String Version){
		if (isNotSign()){
			isReversed(context,URL,Version);
		}
	}
	
	public void checkHookApp(final HomeActivity context){
		if(isExist()){
			isDanger(context);
		}
	}
	
	public boolean isNotSign(){
		String md5 = getSignInfo(mContext, mContext.getPackageName(), "MD5");
        String sha1 = getSignInfo(mContext, mContext.getPackageName(), "SHA1");
		if (!hexAppendChar(md5.toUpperCase()).equals(CipherClient.MD5())){
			return true;
		}
		return !hexAppendChar(sha1.toUpperCase()).equals(CipherClient.SHA1());
	}
	
	public boolean isExist(){
		if (packageExists("com.gmail.heagoo.apkeditor")){
			return true;
		}
		if (packageExists("com.gmail.heagoo.apkeditor.pro")){
			return true;
		}
		if (packageExists("per.pqy.apktool")) {
			return true;
		}
		if (packageExists("bin.mt.pro")) {
			return true;
		}
		if (packageExists("bin.mt.provip")) {
			return true;
		}
		if (packageExists("com.applisto.appcloner")) {
			return true;
		}
		if (packageExists("com.luckypatchers.luckypatcherinstaller")) {
			return true;
		}
		return packageExists("id.nami.apktool");
	}
	
	public boolean packageExists(final String packageName) {
		try {
			ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName, 0);
			return info != null;
		}
		catch (Exception ex) {
		}
		return false;
	}
	
	public String getSignInfo(Context context, String packageName, String type) {
        Signature[] signs = getSignatures(context, packageName);
        if (signs != null && 0 < signs.length) {
            return getSignatureString(signs[0], type);
        }
        return null;
    }

	public static Signature[] getSignatures(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 64).signatures;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSignatureString(Signature sig, String type) {
        byte[] hexBytes = sig.toByteArray();
        String fingerprint = "error!";
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            if (digest != null) {
                byte[] digestBytes = digest.digest(hexBytes);
                StringBuilder sb = new StringBuilder();
                for (byte digestByte : digestBytes) {
                    sb.append(Integer.toHexString((digestByte & 255) | 256).substring(1, 3));
                }
                fingerprint = sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return fingerprint;
    }
	
	public String hexAppendChar(String value) {
		if (value == null || value.equals("")) {
			return value;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			sb.append(value.charAt(i));
			if ((i - 1) % 2 == 0) {
				sb.append(":");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public void isReversed(final HomeActivity mMain, String URL_UPDATE, String Version){
		SweetAlertDialog dialog = new SweetAlertDialog(mMain, SweetAlertDialog.ERROR_TYPE);
		dialog.setTitle("Application is Reversed!");
		dialog.setContentText("Sorry, rebuild this Application is not allowed.\nPlease install the original Application");
		dialog.setCancelable(false);
		dialog.setConfirmText("DOWNLOAD");
		dialog.setConfirmClickListener(sweetAlertDialog -> {
			if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
				if (mMain.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
						PackageManager.PERMISSION_DENIED) {
					String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
					mMain.requestPermissions(permission, PERMISSION_STORAGE_CODE);
				} else {
					setDownloadApp(mMain, URL_UPDATE,Version);
				}

			} else {
				setDownloadApp(mMain,URL_UPDATE,Version);
			}
		}).setCancelText("EXIT").setCancelClickListener(dialog12 -> System.exit(2));
		dialog.show();
	}
	private void setDownloadApp(HomeActivity mMain, String URL, String Version){
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
		request.setTitle("korenime-"+Version);
		request.setDescription("Downloading Latest Version");
		request.allowScanningByMediaScanner();
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,Version + System.currentTimeMillis() + ".apk");
		} else {
			request.setDestinationInExternalPublicDir("Korenime","korenime-" +Version + "-" + System.currentTimeMillis() + ".apk");

		}
		//get download service
		DownloadManager downloadManager = (DownloadManager) mMain.getSystemService(Context.DOWNLOAD_SERVICE);
		assert downloadManager != null;
		downloadManager.enqueue(request);
	}
	public void isDanger(final HomeActivity mMain){
		Builder builder = new Builder(mMain);
		builder.setTitle("Hook Application Detected!!!");
		builder.setMessage("Lucky Patcher Found, \n\nPlease uninstall first to run this application");
		builder.setCancelable(false);
		builder.setPositiveButton("EXIT", (dialogInterface, i) -> System.exit(2));
		builder.show();
	}
}
