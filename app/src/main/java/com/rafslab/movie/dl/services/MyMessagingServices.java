package com.rafslab.movie.dl.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.ui.activity.HomeActivity;

import java.util.Map;
import java.util.Random;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class MyMessagingServices extends FirebaseMessagingService {
    String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        final Intent intent = new Intent(this, HomeActivity.class);
        if (data.containsKey("click_action")) {
            intent.putExtra("click_action", data.get("click_action"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager, ADMIN_CHANNEL_ID);
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_notifications_active);
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        assert notificationManager != null;
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void setupChannels(NotificationManager notificationManager, String ADMIN_CHANNEL_ID){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
