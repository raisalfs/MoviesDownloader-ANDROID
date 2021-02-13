package com.rafslab.movie.dl.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import org.jetbrains.annotations.NotNull;

public class CrashReporter implements Thread.UncaughtExceptionHandler {
	private final Context myContext;
	private final String LINE_SEPARATOR = "\n";

	public CrashReporter(Context context) {
		myContext = context;
	}



	public void uncaughtException(@NotNull Thread thread, @NotNull Throwable exception) {
		try{
			StringWriter stackTrace = new StringWriter();
			exception.printStackTrace(new PrintWriter(stackTrace));
			//putString(Log, errorReport.toString());
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			String errorReport = "==== CAUSE OF ERROR ====" +
					LINE_SEPARATOR +
					stackTrace.toString() +
					LINE_SEPARATOR +
					"==== DEVICE INFORMATION ====" +
					LINE_SEPARATOR +
					"Brand : " +
					Build.BRAND +
					LINE_SEPARATOR +
					"Device : " +
					Build.DEVICE +
					LINE_SEPARATOR +
					"Model : " +
					Build.MODEL +
					LINE_SEPARATOR +
					"Id : " +
					Build.ID +
					LINE_SEPARATOR +
					"Product : " +
					Build.PRODUCT +
					LINE_SEPARATOR +
					"SDK : " +
					Build.VERSION.SDK_INT +
					LINE_SEPARATOR +
					"Release : " +
					Build.VERSION.RELEASE +
					LINE_SEPARATOR +
					"Incremental : " +
					Build.VERSION.INCREMENTAL +
					LINE_SEPARATOR;
			shareIntent.putExtra(Intent.EXTRA_TEXT, errorReport);
			Intent intent = Intent.createChooser(shareIntent,"Share Error");
			intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
			myContext.startActivity(intent);

			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(2);

		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
