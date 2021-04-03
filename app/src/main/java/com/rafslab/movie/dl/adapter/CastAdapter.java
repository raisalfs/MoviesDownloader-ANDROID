package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.ui.activity.CastActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private final Context mContext;
    private final LayoutInflater inflater;
    private final RequestOptions options;
    private List<Cast> castList;
    private SortedList<Cast> castSortedList;
    private boolean isCastInHome;
    private boolean isCastSorted;

    public CastAdapter(Context mContext, List<Cast> castList) {
        this.mContext = mContext;
        this.castList = castList;
        inflater = LayoutInflater.from(mContext);
        options = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher);
    }
    public CastAdapter(Context mContext, List<Cast> castList, boolean isCastInHome) {
        this.mContext = mContext;
        this.castList = castList;
        this.isCastInHome = isCastInHome;
        inflater = LayoutInflater.from(mContext);
        options = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher);
    }
    public CastAdapter(Context mContext, boolean isCastInHome, boolean isCastSorted) {
        this.mContext = mContext;
        this.isCastInHome = isCastInHome;
        this.isCastSorted = isCastSorted;
        inflater = LayoutInflater.from(mContext);
        options = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher);
    }
    public void sort(final String properties){
        castSortedList = new SortedList<>(Cast.class, new SortedList.Callback<Cast>() {
            @Override
            public int compare(Cast o1, Cast o2) {
                if (properties.equalsIgnoreCase("Ascending")) {
                    return o1.getReal_name().compareTo(o2.getReal_name());
                } else if (properties.equalsIgnoreCase("Descending")) {
                    return o2.getReal_name().compareTo(o1.getReal_name());
                }
                return String.valueOf(o1.getReal_name()).compareTo(String.valueOf(o2.getReal_name()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Cast oldItem, Cast newItem) {
                return oldItem.getReal_name().equals(newItem.getReal_name());
            }

            @Override
            public boolean areItemsTheSame(Cast item1, Cast item2) {
                return item1.getReal_name().equals(item2.getReal_name());
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

    public void addAll(List<Cast> starList) {
        castSortedList.beginBatchedUpdates();
        for (int i = 0; i < starList.size(); i++) {
            castSortedList.add(starList.get(i));
        }
        castSortedList.endBatchedUpdates();
    }
    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CastViewHolder(inflater.inflate(R.layout.cast_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast;
        if (isCastSorted) {
            cast = castSortedList.get(position);
        } else {
            cast = castList.get(position);
        }
        if (isCastInHome) {
            holder.nameRealCasting.setTypeface(holder.nameRealCasting.getTypeface(), Typeface.BOLD);
            holder.nameInMovieCasting.setTypeface(holder.nameInMovieCasting.getTypeface(), Typeface.NORMAL);
            holder.nameRealCasting.setText(cast.getReal_name());
            String nameText;
            if (cast.getGender().equals("Male")) {
                nameText = "Actor";
            } else {
                nameText = "Actress";
            }
            holder.nameInMovieCasting.setText(nameText);
            Glide.with(mContext).load(cast.getCover()).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ((AppCompatActivity)mContext).startPostponedEnterTransition();
                    return false;
                }
            }).into(holder.imageActor);
            holder.itemView.setOnClickListener(v-> {
                Intent intent = new Intent(mContext, CastActivity.class);
                intent.putExtra("data", cast);
                intent.putExtra("casting", cast.getReal_name());
                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("actor_list", (Serializable) castList);
                intent.putExtra("type", "fromHome");
                mContext.startActivity(intent);
            });
        } else {
            holder.nameRealCasting.setTypeface(holder.nameRealCasting.getTypeface(), Typeface.NORMAL);
            holder.nameInMovieCasting.setTypeface(holder.nameInMovieCasting.getTypeface(), Typeface.BOLD);
            holder.nameRealCasting.setText(cast.getReal_name());
            String name = "( " + cast.getName() + " )";
            holder.nameInMovieCasting.setText(name);
            Glide.with(mContext).load(cast.getProfile()).apply(options).into(holder.imageActor);
            holder.itemView.setOnClickListener(v-> {
                Intent intent = new Intent(mContext, CastActivity.class);
                intent.putExtra("data", cast);
                intent.putExtra("casting", cast.getReal_name());
                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("actor_list", (Serializable) castList);
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isCastSorted) {
            return castSortedList.size();
        } else return castList.size();
    }
    static class CastViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageActor;
        private final TextView nameRealCasting;
        private final TextView nameInMovieCasting;
        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imageActor = itemView.findViewById(R.id.cast_profile);
            nameRealCasting = itemView.findViewById(R.id.cast_real_name);
            nameInMovieCasting = itemView.findViewById(R.id.cast_name_movie);
        }
    }
    public void setFilter(List<Cast> models){
        castList = new ArrayList<>();
        castList.addAll(models);
        notifyDataSetChanged();
    }
}
