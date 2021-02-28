package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.ui.activity.DetailsActivity;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderViewHolder> {
    private final Context mContext;
    private List<ChildData> rootList;
    private List<CoverArray> coverList;
    private final LayoutInflater inflater;
    private Callback callback;
    private boolean isCoverOnDetails;

    public SliderAdapter(Context mContext, List<ChildData> rootList, Callback callback) {
        this.mContext = mContext;
        this.rootList = rootList;
        this.callback = callback;
        inflater = LayoutInflater.from(mContext);
    }
    public SliderAdapter(Context mContext, List<CoverArray> coverList, boolean isCoverOnDetails) {
        this.mContext = mContext;
        this.coverList = coverList;
        this.isCoverOnDetails = isCoverOnDetails;
        inflater = LayoutInflater.from(mContext);
    }
    public interface Callback {
        void setBackgroundGradient(ChildData data, int position);
    }
    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
        if (isCoverOnDetails) {
            return new SliderViewHolder(inflater.inflate(R.layout.cover_slider_item, parent, false));
        } else return new SliderViewHolder(inflater.inflate(R.layout.slider_root_list, parent, false));
    }

    @Override
    public void onBindViewHolder(SliderViewHolder viewHolder, int position) {
        if (isCoverOnDetails) {
            CoverArray cover = coverList.get(position);
            Glide.with(mContext).load(cover.getImage()).thumbnail(0.1f).into(viewHolder.cover);
        } else {
            ChildData data = rootList.get(position);
            ChildData data1 = rootList.get(position);
            callback.setBackgroundGradient(data1, position);
            viewHolder.release.setText(data.getRelease());
            viewHolder.type.setText(data.getCountry());
            viewHolder.title.setText(data.getTitle());
            String episode = data.getEpsCount() + " Episode";
            viewHolder.episode.setText(episode);
            Glide.with(mContext).load(data.getCoverArrays().get(0).getImage()).thumbnail(0.1f).addListener(new RequestListener<Drawable>() {
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
            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("childList", (Serializable) rootList);
                intent.putExtra("childData", data);
                intent.putExtra("castList", (Serializable) data.getCastList());
                intent.putExtra("position_db", data.getId());
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getCount() {
        if (isCoverOnDetails) {
            return coverList.size();
        } else return rootList.size();
    }

    static class SliderViewHolder extends SliderViewAdapter.ViewHolder {
        private final ImageView cover;
        private final TextView release;
        private final TextView type;
        private final TextView episode;
        private final TextView title;
        private final ProgressBar progressBar;
        public SliderViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            release = itemView.findViewById(R.id.release);
            type = itemView.findViewById(R.id.type);
            episode = itemView.findViewById(R.id.episode);
            title = itemView.findViewById(R.id.title);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
