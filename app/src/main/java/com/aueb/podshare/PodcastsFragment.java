package com.aueb.podshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.adapter.PodcastAdapter;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;

import java.util.ArrayList;

public class PodcastsFragment extends Fragment {

    private User user;
    private ListView podcastsList;
    private ArrayList<Podcast> userPodcasts = new ArrayList<>();
    private ArrayList<String> podcastTitles = new ArrayList<>();

    public PodcastsFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.podsharer_profile_podcasts_fragment, container, false);

        userPodcasts = user.getPodcasts();

        for (int i = 0; i < userPodcasts.size(); i++) {
            podcastTitles.add(userPodcasts.get(i).getName());
        }

        PodcastAdapter podcastAdapter = new PodcastAdapter(userPodcasts, podcastTitles, getActivity());

        podcastsList = view.findViewById(R.id.podcasts_list);
        podcastsList.setAdapter(podcastAdapter);

        podcastsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String selected = ((TextView) view.findViewById(R.id.podcast_name_text)).getText().toString();

                Toast toast = Toast.makeText(getContext(), selected, Toast.LENGTH_SHORT);
                toast.show();
                PodcastNameSharedPreference podcast = new PodcastNameSharedPreference(getContext());
                podcast.saveSession(selected);
                loadFragment(new PodcastProfileFragment(user));
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        userPodcasts = new ArrayList<>();
        podcastTitles = new ArrayList<>();
    }
}
