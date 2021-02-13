package com.rafslab.movie.dl.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.SimpleAdapter;
import com.rafslab.movie.dl.model.Account;
import com.rafslab.movie.dl.ui.activity.HomeActivity;
import com.rafslab.movie.dl.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {
    private TextView sign;
    private RecyclerView accountList;
    private ImageView backgroundGradient;
    private AppBarLayout contextAppBar;
    Toolbar contextToolbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        sign = rootView.findViewById(R.id.login_register);
        accountList = rootView.findViewById(R.id.account_list);
        backgroundGradient = requireActivity().findViewById(R.id.background_gradient);
        contextAppBar = requireActivity().findViewById(R.id.app_bar);
        contextToolbar = requireActivity().findViewById(R.id.toolbar);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backgroundGradient.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.background));
        contextAppBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        BaseUtils.getStatusBar(requireContext()).setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        BaseUtils.getActionBar(requireContext()).hide();
        List<Account> data = prepareList();
        accountList.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountList.setAdapter(new SimpleAdapter(requireContext(), data, true));
        sign.setOnClickListener(v-> HomeActivity.showDialogUnderDevelopment(requireContext()));

    }
    private List<Account> prepareList(){
        List<Account> accounts = new ArrayList<>();
        Account data = new Account(R.drawable.ic_twotone_settings, "Settings");
        accounts.add(data);
        data = new Account(R.drawable.ic_request, "Request Movies");
        accounts.add(data);
        data = new Account(R.drawable.ic_baseline_bookmarks, "Bookmark");
        accounts.add(data);
        data = new Account(R.drawable.ic_download, "Downloaded");
        accounts.add(data);
        return accounts;
    }
    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(requireActivity(), view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(requireContext(), HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(HomeActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(requireContext(), intent, options.toBundle());
    }
}
