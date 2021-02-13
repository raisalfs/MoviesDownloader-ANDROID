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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.ui.activity.DetailsActivity;
import com.rafslab.movie.dl.view.ThumbPreviewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private final Context mContext;
    private List<ChildData> childDataList;
    private SortedList<ChildData> sortedList;
    private final LayoutInflater inflater;
    private boolean sorted, isOnRecommendation;
    public ChildAdapter(Context mContext, List<ChildData> childDataList) {
        this.mContext = mContext;
        this.childDataList = childDataList;
        inflater = LayoutInflater.from(mContext);
    }
    public ChildAdapter(Context mContext, boolean sorted) {
        this.mContext = mContext;
        this.sorted = sorted;
        inflater = LayoutInflater.from(mContext);
    }
    public ChildAdapter(Context mContext, boolean sorted, boolean isOnRecommendation) {
        this.mContext = mContext;
        this.sorted = sorted;
        this.isOnRecommendation = isOnRecommendation;
        inflater = LayoutInflater.from(mContext);
    }
    public void order(final String properties){
        sortedList = new SortedList<>(ChildData.class, new SortedList.Callback<ChildData>() {
            @Override
            public int compare(ChildData o1, ChildData o2) {
                if (properties.equalsIgnoreCase("Ascending")) {
                    return o1.getTitle().compareTo(o2.getTitle());
                } else if (properties.equalsIgnoreCase("Descending")) {
                    return o2.getTitle().compareTo(o1.getTitle());
                } else if (properties.equalsIgnoreCase("By Highest")){
                    return String.valueOf(o2.getRating()).compareTo(String.valueOf(o1.getRating()));
                } else if (properties.equalsIgnoreCase("By Lowest")){
                    return String.valueOf(o1.getRating()).compareTo(String.valueOf(o2.getRating()));
                } else if (properties.equalsIgnoreCase("By Newest")){
                    return String.valueOf(o2.getRelease()).compareTo(String.valueOf(o1.getRelease()));
                } else if (properties.equalsIgnoreCase("By Oldest")){
                    return String.valueOf(o1.getRelease()).compareTo(String.valueOf(o2.getRelease()));
                }
                return String.valueOf(o1.getTitle()).compareTo(String.valueOf(o2.getTitle()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(ChildData oldItem, ChildData newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @Override
            public boolean areItemsTheSame(ChildData item1, ChildData item2) {
                return item1.getTitle().equals(item2.getTitle());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }
    public void sort(final String properties){
        sortedList = new SortedList<>(ChildData.class, new SortedList.Callback<ChildData>() {
            @Override
            public int compare(ChildData o1, ChildData o2) {
                if (properties.equalsIgnoreCase("By Title")) {
                    return o1.getTitle().compareTo(o2.getTitle());
                } else if (properties.equalsIgnoreCase("By Rating")) {
                    return String.valueOf(o2.getRating()).compareTo(String.valueOf(o1.getRating()));
                } else if (properties.equalsIgnoreCase("By Year")){
                    return String.valueOf(o2.getRelease()).compareTo(String.valueOf(o1.getRelease()));
                }
                return String.valueOf(o1.getTitle()).compareTo(String.valueOf(o2.getTitle()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(ChildData oldItem, ChildData newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @Override
            public boolean areItemsTheSame(ChildData item1, ChildData item2) {
                return item1.getTitle().equals(item2.getTitle());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }
    public void addAll(List<ChildData> starList) {
        sortedList.beginBatchedUpdates();
        for (int i = 0; i < starList.size(); i++) {
            sortedList.add(starList.get(i));
        }
        sortedList.endBatchedUpdates();
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isOnRecommendation) {
            return new ChildViewHolder(inflater.inflate(R.layout.item_row_list, parent, false));
        } else {
            return new ChildViewHolder(inflater.inflate(R.layout.item_row_grid, parent, false));
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ChildData data;
        if (sorted) {
            data = sortedList.get(position);
        } else {
            data = childDataList.get(position);
        }
        int identifyDownloadable = data.getDownloadable();
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra("childData", data);
            intent.putExtra("position_db", data.getId());
            intent.putExtra("position", position);
            intent.putExtra("downloadable", identifyDownloadable);

//            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, holder.thumbnail, mContext.getResources().getString(R.string.image_transition));
//            mContext.startActivity(intent, compat.toBundle());
//            Pair<View, String> pair = Pair.create(holder.thumbnail, mContext.getResources().getString(R.string.image_transition));
//            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pair);
            mContext.startActivity(intent);
        });
        holder.thumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra("childData", data);
            intent.putExtra("position_db", data.getId());
            intent.putExtra("position", position);
            intent.putExtra("downloadable", identifyDownloadable);
//            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, holder.thumbnail, mContext.getResources().getString(R.string.image_transition));
//            mContext.startActivity(intent, compat.toBundle());
            mContext.startActivity(intent);
        });
        String information = data.getRelease() + " | " + data.getContentRating() + "+" + " | " + data.getSeasonCount() + " Season";
        String episode = data.getEpsCount() + " Episode";
        holder.epsCount.setText(episode);
        holder.informationMovies.setText(information);
        if (!isOnRecommendation) {
            Glide.with(mContext).load(data.getPoster()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).thumbnail(0.1f).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.thumbnail.setOnLongClickListener(v -> {
                        new ThumbPreviewer().show(mContext, holder.thumbnail, data.getTitle(), data.getRating());
                        return true;
                    });
                    return false;
                }
            }).into(holder.thumbnail);
            String season;
            if (data.getSeasonCount() == 1) {
                season = "S0" + data.getSeasonCount();
            } else {
                if (data.getSeasonCount() < 10) {
                    season = "S01 " + "S0" + data.getSeasonCount();
                } else {
                    season = "S01 " + "S" + data.getSeasonCount();
                }
            }
            holder.seasonCount.setText(season);
        } else {
            Glide.with(mContext).load(data.getCoverArrays().get(0).getImage()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).thumbnail(0.1f).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.thumbnail);
            holder.epsCount.setTextSize(12);
            holder.seasonCount.setVisibility(View.GONE);
        }
        holder.title.setText(data.getTitle());
    }

    @Override
    public int getItemCount() {
        if (sorted) {
            return sortedList.size();
        } else {
            return childDataList.size();
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final TextView title;
        private final TextView informationMovies;
        private final TextView epsCount;
        private final TextView seasonCount;
        private final ProgressBar progressBar;
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            progressBar = itemView.findViewById(R.id.progress_bar);
            informationMovies = itemView.findViewById(R.id.information_movies);
            epsCount = itemView.findViewById(R.id.eps_count);
            seasonCount = itemView.findViewById(R.id.season_count);
        }
    }
    public void setFilter(List<ChildData> models){
        childDataList = new ArrayList<>();
        childDataList.addAll(models);
        notifyDataSetChanged();
    }
}
