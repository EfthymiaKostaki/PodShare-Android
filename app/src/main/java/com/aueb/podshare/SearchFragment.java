package com.aueb.podshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aueb.podshare.adapter.Adapter;
import com.google.android.material.tabs.TabLayout;

public class SearchFragment extends Fragment {

    private TabLayout tabs;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_fragment, container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.searchviewPager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        tabs = (TabLayout) view.findViewById(R.id.searchtabLayout);
        tabs.setupWithViewPager(viewPager);
        setupTabIcons();

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new SearchPodsharersFragment(), "PodSharers");
        adapter.addFragment(new SearchPodcastsFragment(), "Podcasts");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(R.drawable.search_podsharers);
        tabs.getTabAt(1).setIcon(R.drawable.search_podcasts);
    }

}
