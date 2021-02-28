package com.rafslab.movie.dl.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.adapter.TabAdapter;
import com.rafslab.movie.dl.model.RootData;
import com.rafslab.movie.dl.ui.activity.HomeActivity;
import com.rafslab.movie.dl.utils.BaseUtils;

import net.idik.lib.cipher.so.CipherClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class HomeFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static AppBarLayout appBarLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = rootView.findViewById(R.id.root_item);
        tabLayout = rootView.findViewById(R.id.tab_layout);
        appBarLayout = rootView.findViewById(R.id.app_bar);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseUtils.getActionBar(requireContext()).show();
        BaseUtils.getActionBar(requireContext()).setTitle(R.string.app_name);
        String URL
                = CipherClient.BASE_URL()
                + CipherClient.END();
        setRootData(URL);
        if (HomeActivity.checkMenuItem(requireContext())) {
            return;
        }
        HomeActivity.disableMenuItem(requireContext(), R.id.filter);
        HomeActivity.enableMenuItem(requireContext(), R.id.search);
    }
    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }
    private void setRootData(String URL){
        AndroidNetworking
                .get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<RootData> rootData = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                RootData data = new RootData();
                                data.setTitle(object.getString("title"));
                                List<RootData.RootChild> rootChildren = new ArrayList<>();
                                JSONArray rootArray = object.getJSONArray("child");
                                for (int rootId = 0; rootId<rootArray.length(); rootId++){
                                    JSONObject rootObject = rootArray.getJSONObject(rootId);
                                    RootData.RootChild root = new RootData.RootChild();
                                    root.setTitle(rootObject.getString("title"));
                                    rootChildren.add(root);
                                }
                                data.setRootChildren(rootChildren);
                                rootData.add(data);
                                getDataTabLayout(rootData, tabLayout, viewPager, data);
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    private void getDataTabLayout(List<RootData> rootDataList, TabLayout tabLayout, ViewPager viewPager, RootData data) {
        try {
            TabAdapter adapter = new TabAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            ChildHomeFragment fragment = new ChildHomeFragment();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    LinearLayout tabViewGroup = (LinearLayout) ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                    TextView tabText = (TextView) tabViewGroup.getChildAt(1);
                    tabText.setTypeface(tabText.getTypeface(), Typeface.BOLD);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    LinearLayout tabViewGroup = (LinearLayout) ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                    TextView tabText = (TextView) tabViewGroup.getChildAt(1);
                    tabText.setTypeface(null, Typeface.NORMAL);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            for (int i = 0; i < rootDataList.size(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(rootDataList.get(i).getTitle()));
                adapter.addFragment(fragment.newInstance(data, 0), rootDataList.get(i).getTitle());
            }
            if (rootDataList.size() > 5) {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            }
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}

