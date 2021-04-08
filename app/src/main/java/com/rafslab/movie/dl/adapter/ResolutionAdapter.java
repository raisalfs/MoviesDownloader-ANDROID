package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.ResolutionValue;

import java.util.List;

public class ResolutionAdapter extends RecyclerView.Adapter<ResolutionAdapter.ResolutionViewHolder> {
    private final Context mContext;
    private final List<ResolutionValue> resolutionValues;
    private final LayoutInflater inflater;
    private ResolutionCallBack callBack;
    private static int lastCheckedPosition = -1;
    private MotionLayout scene;
    private String getResolutionParent;
    public ResolutionAdapter(Context mContext, List<ResolutionValue> resolutionValues) {
        this.mContext = mContext;
        this.resolutionValues = resolutionValues;
        inflater = LayoutInflater.from(mContext);
    }
    public ResolutionAdapter(Context mContext, List<ResolutionValue> resolutionValues, ResolutionCallBack callBack, MotionLayout scene, String getResolutionParent) {
        this.mContext = mContext;
        this.resolutionValues = resolutionValues;
        this.callBack = callBack;
        this.scene = scene;
        this.getResolutionParent = getResolutionParent;
        inflater = LayoutInflater.from(mContext);
    }
    public interface ResolutionCallBack{
        void getSelectedResolution(ResolutionValue data, int position);
    }

    @NonNull
    @Override
    public ResolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResolutionViewHolder(inflater.inflate(R.layout.resolution_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResolutionViewHolder holder, int position) {
        ResolutionValue data = resolutionValues.get(position);
        holder.resolution.setText(data.getName());
//        holder.resolution.setChecked(position == lastCheckedPosition);
        holder.resolution.setChecked(data.getName().equals(getResolutionParent));
        holder.resolution.setOnClickListener(v->{
            callBack.getSelectedResolution(data, position);
            int copyOfLastPositionSelected = lastCheckedPosition;
            lastCheckedPosition = position;
            notifyItemChanged(copyOfLastPositionSelected);
            notifyItemChanged(lastCheckedPosition);
            new Handler().postDelayed(()-> scene.transitionToStart(), 500);
        });
    }
    @Override
    public int getItemCount() {
        return resolutionValues.size();
    }

    static class ResolutionViewHolder extends RecyclerView.ViewHolder {
        private final Chip resolution;
        ChipGroup resolutionGroup;
        public ResolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            resolutionGroup = itemView.findViewById(R.id.chip_group);
            resolution = resolutionGroup.findViewById(R.id.chip);

        }
    }
}
