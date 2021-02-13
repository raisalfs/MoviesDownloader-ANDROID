package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.ui.activity.DetailsActivity;
import com.vickykdv.circlerectimageview.CircleRectImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context mContext;
    private List<ChildData> childDataList;
    private SortedList<ChildData> sortedList;
    private LayoutInflater inflater;
    private boolean sorting;

    public BookmarkAdapter(Context mContext, List<ChildData> childDataList) {
        this.mContext = mContext;
        this.childDataList = childDataList;
        this.inflater = LayoutInflater.from(mContext);
    }

    public BookmarkAdapter(Context mContext, boolean sorting) {
        this.mContext = mContext;
        this.sorting = sorting;
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
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookmarkViewHolder(inflater.inflate(R.layout.item_row_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        ChildData data;
        if (sorting) {
            data = sortedList.get(position);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("childList", (Serializable) childDataList);
                intent.putExtra("childData", data);
                intent.putExtra("castList", (Serializable) data.getCastList());
//                intent.putExtra("episode_data", data.getEpisode());
                intent.putExtra("position", holder.getAdapterPosition());
                mContext.startActivity(intent);
            });
        } else {
            data = childDataList.get(position);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("childList", (Serializable) childDataList);
                intent.putExtra("childData", data);
                intent.putExtra("castList", (Serializable) data.getCastList());
//                intent.putExtra("episode_data", data.getEpisode());
                intent.putExtra("position", holder.getAdapterPosition());
                mContext.startActivity(intent);
            });
        }
        holder.title.setText(data.getTitle());
        Glide.with(mContext).load(data.getPoster()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if (sorting) {
            return sortedList.size();
        } else {
            return childDataList.size();
        }
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private CircleRectImage thumbnail;
        private TextView title;
        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
        }
    }
    public void setFilter(List<ChildData> models){
        childDataList = new ArrayList<>();
        childDataList.addAll(models);
        notifyDataSetChanged();
    }
}
