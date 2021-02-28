package com.rafslab.movie.dl.sticky;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class StickyRecyclerView extends RecyclerView {
    public StickyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public StickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (getAdapter() instanceof StickyHeaderDecoration.StickyListener) {
            setStickyItemDecoration();
        }
    }
    private void setStickyItemDecoration() {
        StickyHeaderDecoration itemDecoration = new StickyHeaderDecoration((StickyHeaderDecoration.StickyListener) getAdapter());
        this.addItemDecoration(itemDecoration);
    }
}
