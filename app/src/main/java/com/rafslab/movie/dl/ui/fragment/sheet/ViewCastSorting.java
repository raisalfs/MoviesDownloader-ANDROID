package com.rafslab.movie.dl.ui.fragment.sheet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.ui.activity.ListContainerActivity;
import com.rafslab.movie.dl.utils.BaseUtils;
import com.rafslab.movie.dl.view.FilterSeekBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ViewCastSorting extends SuperBottomSheetFragment {
    private Toolbar toolbar;
    private MaterialRadioButton sortMale, sortFemale, restoreAllCast, orderAsc, orderDesc;
    private ConstraintLayout cancel, done;
    private BottomSheetBehavior behavior;
    private FilterSeekBar seekBar;
    private TextView min, max;
    private SharedPreferences.Editor sortPrefsEdit;
    public static final String SHARED_CAST_KEY = "sortCastPrefs";
    public static final String SORT_CAST_KEY = "sortCastKey";
    public static final String ORDER_CAST_KEY = "orderCastKey";
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_cast_sorting, container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        sortMale = rootView.findViewById(R.id.sort_male);
        sortFemale = rootView.findViewById(R.id.sort_female);
        restoreAllCast = rootView.findViewById(R.id.sort_all);
        orderAsc = rootView.findViewById(R.id.order_asc);
        orderDesc = rootView.findViewById(R.id.order_desc);
        cancel = rootView.findViewById(R.id.cancel);
        done = rootView.findViewById(R.id.applyChanges);
        seekBar = rootView.findViewById(R.id.seek_bar);
        min = rootView.findViewById(R.id.min_seek);
        max = rootView.findViewById(R.id.max_seek);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sortPrefs = requireActivity().getSharedPreferences(SHARED_CAST_KEY, Context.MODE_PRIVATE);
        toolbar.setTitle("Sort by");
        sortPrefsEdit = sortPrefs.edit();
        onChecked();
        List<Cast> data =(List<Cast>) getArguments().getSerializable("data");
        done.setOnClickListener(v->{
            sortPrefsEdit.apply();
            ListContainerActivity.refreshCastItems(requireContext(), sortPrefs.getString(SORT_CAST_KEY, ""), sortPrefs.getString(ORDER_CAST_KEY, ""), data);
            dismiss();
        });
        sortMale.setChecked(sortPrefs.getString(SORT_CAST_KEY, "All").equals("Male"));
        sortFemale.setChecked(sortPrefs.getString(SORT_CAST_KEY, "All").equals("Female"));
        restoreAllCast.setChecked(sortPrefs.getString(SORT_CAST_KEY, "All").equals("All"));
        orderAsc.setChecked(sortPrefs.getString(ORDER_CAST_KEY, "Ascending").equals("Ascending"));
        orderDesc.setChecked(sortPrefs.getString(ORDER_CAST_KEY, "Ascending").equals("Descending"));
        cancel.setOnClickListener(v -> dismiss());
        seekBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            min.setText(BaseUtils.formatSeekBar(minValue.intValue()));
            max.setText(BaseUtils.formatSeekBar(maxValue.intValue()));
        });

    }
    private void onChecked(){
        sortMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sortFemale.setChecked(false);
                restoreAllCast.setChecked(false);
                sortPrefsEdit.putString(SORT_CAST_KEY, "Male");
            }
        });
        sortFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sortMale.setChecked(false);
                restoreAllCast.setChecked(false);
                sortPrefsEdit.putString(SORT_CAST_KEY, "Female");
            }
        });
        restoreAllCast.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sortMale.setChecked(false);
                sortFemale.setChecked(false);
                sortPrefsEdit.putString(SORT_CAST_KEY, "All");
            }
        });
        orderAsc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (orderDesc.isChecked()) {
                    orderDesc.setChecked(false);
                }
                sortPrefsEdit.putString(ORDER_CAST_KEY, "Ascending");
            }
        });
        orderDesc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (orderAsc.isChecked()) {
                    orderAsc.setChecked(false);
                }
                sortPrefsEdit.putString(ORDER_CAST_KEY, "Descending");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (getView() != null) {
                        behavior = BottomSheetBehavior.from((View)getView().getParent());
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
