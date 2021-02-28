package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.jsibbold.zoomage.ZoomageView;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.Cover;
import com.rafslab.movie.dl.ui.activity.ThumbnailDetailsActivity;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class CastImageAdapter extends SliderViewAdapter<CastImageAdapter.CastImageViewHolder> {
    private final Context mContext;
    private final List<Cover> coverList;
    private final LayoutInflater inflater;
    private boolean isDetails;
    private Cast cast;

    public CastImageAdapter(Context mContext, List<Cover> coverList) {
        this.mContext = mContext;
        this.coverList = coverList;
        inflater = LayoutInflater.from(mContext);
    }
    public CastImageAdapter(Context mContext, List<Cover> coverList, Cast cast, boolean isDetails) {
        this.mContext = mContext;
        this.coverList = coverList;
        this.isDetails = isDetails;
        this.cast = cast;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public CastImageViewHolder onCreateViewHolder(ViewGroup parent) {
        if (!isDetails) {
            return new CastImageViewHolder(inflater.inflate(R.layout.image_slider_item_zoomable, parent, false));
        } else {
            return new CastImageViewHolder(inflater.inflate(R.layout.image_slider_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(CastImageViewHolder viewHolder, int position) {
        viewHolder.isDetails = isDetails;
        if (!isDetails) {
            Glide.with(mContext).load(coverList.get(position).getImage()).thumbnail(0.1f).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(viewHolder.coverZoomAble);
        } else {
            Glide.with(mContext).load(coverList.get(position).getImage()).thumbnail(0.1f).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(viewHolder.cover);
            viewHolder.cover.setOnClickListener(v->{
                Intent intent = new Intent(mContext, ThumbnailDetailsActivity.class);
                intent.putExtra("cast", cast);
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getCount() {
        return coverList.size();
    }

    static class CastImageViewHolder extends SliderViewAdapter.ViewHolder {
        private boolean isDetails;
        private final KenBurnsView cover;
        private ZoomageView coverZoomAble;
        private final ProgressBar progressBar;
        public CastImageViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            progressBar = itemView.findViewById(R.id.progress_bar);
            if (!isDetails) {
                coverZoomAble = itemView.findViewById(R.id.cover_zoomable);
            }
        }
    }
}
