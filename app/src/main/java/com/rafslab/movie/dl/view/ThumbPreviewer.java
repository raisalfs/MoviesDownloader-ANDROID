package com.rafslab.movie.dl.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;


import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.utils.ThumbPreviewerUtils;

import java.util.Objects;

public class ThumbPreviewer {
    @SuppressLint("ClickableViewAccessibility")

    public void show(Context context, ImageView source, String title, double rating) {
        BitmapDrawable background = ThumbPreviewerUtils.getBlurredScreenDrawable(context, source.getRootView());
        View dialogView = LayoutInflater.from(context).inflate(R.layout.view_thumbnail_previewer, null);
        ImageView imageView = dialogView.findViewById(R.id.previewer_thumbnail);
        TextView textView = dialogView.findViewById(R.id.title_movies);
        AppCompatRatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        if (source.getDrawable().getConstantState() != null) {
            Drawable copy = source.getDrawable().getConstantState().newDrawable();
            textView.setText(title);
            ratingBar.setRating((float) (rating * 0.5));
            imageView.setImageDrawable(copy);
            final Dialog dialog = new Dialog(context, R.style.ThumbnailPreviewerTheme);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(background);
            dialog.setContentView(dialogView);
            dialog.show();
            source.setOnTouchListener((v, event) -> {
                if (dialog.isShowing()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    int action = event.getActionMasked();
                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        dialog.dismiss();
                        return true;
                    }
                }
                return false;
            });
        }
    }
}
