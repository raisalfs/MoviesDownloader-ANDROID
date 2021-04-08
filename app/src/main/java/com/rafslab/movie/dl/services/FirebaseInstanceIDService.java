package com.rafslab.movie.dl.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "mFirebaseIDService";
    private static final String SUBSCRIBE_TO = "users";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
        Log.i(TAG, "Token: " + token);
    }
}
