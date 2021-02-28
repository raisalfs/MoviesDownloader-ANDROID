package com.rafslab.movie.dl.services;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class MySingleton {
    @SuppressLint("StaticFieldLeak")
    private  static MySingleton instance;
    private RequestQueue requestQueue;
    private final Context context;

    private MySingleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
