package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.Categories;
import com.rafslab.movie.dl.model.child.ChipsReceived;
import com.rafslab.movie.dl.model.child.Tag;
import com.rafslab.movie.dl.ui.activity.ResultActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ChipsAdapter extends RecyclerView.Adapter<ChipsAdapter.ChipViewHolder> {
    private final Context mContext;
    ArrayList<String> selectedCategories;
    ArrayList<String> selectedTags;
    ArrayList<String> selectedRegion;
    private List<Categories> chips;
    private List<Tag> tagsList;
    private final LayoutInflater inflater;
    private Callback callback;
    private boolean isResult, isTag, isDetail;
    private List<ChipsReceived> chipReceived;

    public ChipsAdapter(Context mContext, List<Categories> chips, Callback callback) {
        this.mContext = mContext;
        this.chips = chips;
        this.inflater = LayoutInflater.from(mContext);
        this.callback = callback;
    }
    public ChipsAdapter(Context mContext, List<Tag> chips, Callback callback, boolean isTag) {
        this.mContext = mContext;
        this.tagsList = chips;
        this.inflater = LayoutInflater.from(mContext);
        this.callback = callback;
        this.isTag = isTag;
    }

    public ChipsAdapter(Context mContext, List<ChipsReceived> chips, boolean isResult, boolean isDetail) {
        this.mContext = mContext;
        this.chipReceived = chips;
        this.isResult = isResult;
        this.isDetail = isDetail;
        this.inflater = LayoutInflater.from(mContext);
    }
    public interface Callback {
        void onChipReceived(List<String> categoriesList, List<String> tagList, int position, boolean isChecked);
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChipViewHolder(inflater.inflate(R.layout.chip_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        if (isResult) {
            selectedCategories = new ArrayList<>();
            ChipsReceived chipsReceived = chipReceived.get(position);
            holder.chip.setText(chipsReceived.getCategories());
            holder.chip.setTextSize(14);
            if (isDetail) {
                holder.chip.setChecked(false);
                holder.chip.setCheckable(false);
                holder.chip.setOnClickListener(v->{
                    List<String> categories = new ArrayList<>();
                    categories.add(chipsReceived.getCategories());
                    Intent intent = new Intent(mContext, ResultActivity.class);
                    intent.putExtra("queryCategories", (Serializable) categories);
                    intent.putExtra("size", categories.size());
                    intent.putExtra("identity", "fromDetail");
                    mContext.startActivity(intent);
                });
            } else {
                holder.chip.setChecked(true);
                holder.chip.setCheckable(false);
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedCategories.add(buttonView.getText().toString());
                    } else {
                        selectedCategories.remove(buttonView.getText().toString());
                    }
                };
                holder.chip.setOnCheckedChangeListener(onCheckedChangeListener);
            }
        }
        else {
            if (isTag){
                Tag tag = tagsList.get(position);
                holder.chip.setText(tag.getTag());
                selectedTags = new ArrayList<>();
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedTags.add(buttonView.getText().toString());

                    } else {
                        selectedTags.remove(buttonView.getText().toString());
                    }
                };
                holder.chip.setOnCheckedChangeListener(onCheckedChangeListener);
            } else {
                Categories chip = chips.get(position);
                holder.chip.setText(chip.getName());
                selectedCategories = new ArrayList<>();
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedCategories.add(buttonView.getText().toString());

                    } else {
                        selectedCategories.remove(buttonView.getText().toString());
                    }
                };
                holder.chip.setOnCheckedChangeListener(onCheckedChangeListener);
            }
            callback.onChipReceived(selectedCategories, selectedTags, position, holder.chip.isChecked());
        }
    }

    @Override
    public int getItemCount() {
        if (isResult){
            return chipReceived.size();
        } else {
            if (isTag) {
                return tagsList.size();
            } else {
                return chips.size();
            }
        }
    }

    static class ChipViewHolder extends RecyclerView.ViewHolder {
        Chip chip;
        ChipGroup chipGroup;
        public ChipViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chips);
            chipGroup = itemView.findViewById(R.id.chip_group);
        }
    }
}
