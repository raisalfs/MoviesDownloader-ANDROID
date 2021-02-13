package com.rafslab.movie.dl.ui.fragment.sheet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.ui.activity.ListContainerActivity;
import com.rafslab.movie.dl.ui.activity.ResultActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ViewSorting extends SuperBottomSheetFragment {
    private Toolbar toolbar;
    private MaterialRadioButton sortName, sortRating, orderAsc, orderDesc;
    private ConstraintLayout cancel, done;
    private BottomSheetBehavior behavior;
    private RecyclerView parentList;
    private SharedPreferences.Editor sortPrefsEdit;
    public static final String SHARED_KEY = "sortPrefs";
    public static final String SORT_KEY = "sortKey";
    public static final String ORDER_KEY = "orderKey";
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sorting, container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        sortName = rootView.findViewById(R.id.sort_name);
        sortRating = rootView.findViewById(R.id.sort_rating);
        orderAsc = rootView.findViewById(R.id.order_asc);
        orderDesc = rootView.findViewById(R.id.order_desc);
        cancel = rootView.findViewById(R.id.cancel);
        done = rootView.findViewById(R.id.applyChanges);
        parentList = requireActivity().findViewById(R.id.result_list);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sortPrefs = requireActivity().getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
        toolbar.setTitle("Sort by");
        sortPrefsEdit = sortPrefs.edit();
        onChecked();
        List<ChildData> data =(List<ChildData>) getArguments().getSerializable("data");
        String parentContext = getArguments().getString("context");
        done.setOnClickListener(v->{
            sortPrefsEdit.apply();
            if (parentContext != null) {
                if (parentContext.equals("ResultActivity")) {
                    ResultActivity.refreshItems(requireContext(), sortPrefs.getString(SORT_KEY, ""), sortPrefs.getString(ORDER_KEY, ""), data);
                } else {
                    ListContainerActivity.refreshItems(requireContext(), sortPrefs.getString(SORT_KEY, ""), sortPrefs.getString(ORDER_KEY, ""), data);
                }
            }
            dismiss();
        });
        sortName.setChecked(sortPrefs.getString(SORT_KEY, "Name").equals("Name"));
        sortRating.setChecked(sortPrefs.getString(SORT_KEY, "Name").equals("Rating"));
        orderAsc.setChecked(sortPrefs.getString(ORDER_KEY, "Ascending").equals("Ascending"));
        orderDesc.setChecked(sortPrefs.getString(ORDER_KEY, "Ascending").equals("Descending"));
        cancel.setOnClickListener(v -> dismiss());
    }
    private void onChecked(){
        sortName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (sortRating.isChecked()) {
                    sortRating.setChecked(false);
                }
                sortPrefsEdit.putString(SORT_KEY, "Name");
            }
        });
        sortRating.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (sortName.isChecked()) {
                    sortName.setChecked(false);
                }
                sortPrefsEdit.putString(SORT_KEY, "Rating");
            }
        });
        orderAsc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (orderDesc.isChecked()) {
                    orderDesc.setChecked(false);
                }
                sortPrefsEdit.putString(ORDER_KEY, "Ascending");
            }
        });
        orderDesc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (orderAsc.isChecked()) {
                    orderAsc.setChecked(false);
                }
                sortPrefsEdit.putString(ORDER_KEY, "Descending");
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
