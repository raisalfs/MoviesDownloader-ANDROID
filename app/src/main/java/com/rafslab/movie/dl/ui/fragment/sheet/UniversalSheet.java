package com.rafslab.movie.dl.ui.fragment.sheet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.view.SeekBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UniversalSheet extends SuperBottomSheetFragment {
    private SeekBar seekBar;
    private TextView valueText, previewTextSize;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.settings_home_view, container, false);
        seekBar = rootView.findViewById(R.id.bar_text_size);
        valueText = rootView.findViewById(R.id.value_bar);
        previewTextSize = rootView.findViewById(R.id.preview_text_size);
        toolbar = rootView.findViewById(R.id.toolbar);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        previewTextSize.setTextSize(seekBar.getMinValue());
        seekBar.setOnSeekbarChangeListener(value -> {
            String aa = value.intValue() + "";
            valueText.setText(aa);
            previewTextSize.setTextSize(value.intValue());
            Log.d("Filter", "" + value.intValue());
        });
        toolbar.setTitle("Home Style");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (getView() != null) {
                        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)getView().getParent());
                        behavior.setPeekHeight(getView().getHeight());
                        getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    @Override
    public float getCornerRadius() {
        return 24;
    }

    @Override
    public int getExpandedHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public boolean isSheetAlwaysExpanded() {
        return true;
    }

    @Override
    public boolean animateCornerRadius() {
        return true;
    }

    @Override
    public boolean animateStatusBar() {
        return true;
    }

    @Override
    public boolean isSheetCancelable() {
        return false;
    }

    @Override
    public boolean isSheetCancelableOnTouchOutside() {
        return false;
    }
}
