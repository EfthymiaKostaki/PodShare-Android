package com.aueb.podshare;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aueb.podshare.Sessions.PodcastDescriptionSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    Activity context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
        ImageView play = (ImageView) view.findViewById(R.id.pl);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayEpisode();
            }
        });


        return view;
    }

    private void goToPlayEpisode() {
        Intent intent = new Intent(context, MyMediaPlayer.class);
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new HomeSubscriptionsFragment1(), "PodSharers");
        adapter.addFragment(new HomeSubscriptionsFragment2(), "Podcasts");
        viewPager.setAdapter(adapter);
    }

}
