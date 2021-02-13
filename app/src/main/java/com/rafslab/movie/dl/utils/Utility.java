package com.rafslab.movie.dl.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utility {
    public static int calculateNumberOfColumnGrid(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }
}
