package com.rafslab.movie.dl.utils;

import android.content.Context;
import android.content.Intent;

import com.rafslab.movie.dl.ui.activity.HomeActivity;

public class NotificationActionHelper {
    public static void setActionNotification(Context context, String extras, int position_item){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("click_action", extras);
        intent.putExtra("position", position_item);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
