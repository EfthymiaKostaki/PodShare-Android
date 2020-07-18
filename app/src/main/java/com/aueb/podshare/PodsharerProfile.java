package com.aueb.podshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class PodsharerProfile extends Fragment  {
    private TabLayout tabs;

    public PodsharerProfile() {
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
        // to do: create correct fragments for podcasts and recents. maybe we should only keep podcasts and
        // then when the user clicks a podcast we replace the podcasts with the episodes but the user can
        // also go back.
        adapter.addFragment(new SearchPodcastsFragment(), "Recents");
        adapter.addFragment(new SearchPodsharersFragment(), "Podcasts");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(R.drawable.search_podcasts);
        tabs.getTabAt(1).setIcon(R.drawable.search_podsharers);
    }

}
