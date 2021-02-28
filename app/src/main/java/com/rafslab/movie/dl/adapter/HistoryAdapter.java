package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.database.DownloadedHistoryHelper;
import com.rafslab.movie.dl.model.child.ChildData;
import com.vickykdv.circlerectimageview.CircleRectImage;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final Context mContext;
    private final List<ChildData> childDataList;
    private final LayoutInflater inflater;
    private DownloadedHistoryHelper helper;
    private final Callback callback;

    public HistoryAdapter(Context mContext, List<ChildData> childDataList, Callback callback) {
        this.mContext = mContext;
        this.childDataList = childDataList;
        this.callback = callback;
        inflater = LayoutInflater.from(mContext);
    }
    public interface Callback{
        void getHistoryDownloaded(boolean isChecked, int position, int selectedItem);
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(inflater.inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ChildData data = childDataList.get(position);
        holder.title.setText(data.getTitle());
        holder.size.setText("%Size");
        helper = new DownloadedHistoryHelper(mContext);
        Glide.with(mContext).load(data.getCoverArrays().get(0).getImage()).into(holder.thumbnail);
        Gson gson = new Gson();
        String downloaded = gson.toJson(data.getDownloadedDatabases());
        holder.itemView.setOnClickListener(v->{
            Log.d("History Downloaded", downloaded);

        });
    }

    @Override
    public int getItemCount() {
        return childDataList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView size;
        private final CircleRectImage thumbnail;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            size = itemView.findViewById(R.id.size);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}