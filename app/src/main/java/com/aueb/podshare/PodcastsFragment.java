package com.aueb.podshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;

import java.util.ArrayList;

public class PodcastsFragment extends Fragment {
    private User user;

    public PodcastsFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.podsharer_profile_podcasts_fragment, container, false);
        addPodcasts();
        return view;
    }

    private void addPodcasts() {
        //LinearLayout podcasts = getView().findViewById(R.id.podcasts_frame);
        //ArrayList<Podcast> userPodcasts = user.getPodcasts();
        /*for (Podcast podcast: userPodcasts) {
             This is not working because these ids not exist in the view we just created
            LinearLayout linearLayout = getView().findViewById(R.id.podcast_card);
            TextView podcastName = getView().findViewById(R.id.podcast_name_text);
            podcastName.setText(podcast.getName());
            TextView podcastDescription = getView().findViewById(R.id.podcast_description_text);
            podcastDescription.setText(podcast.getDescription());
            podcasts.addView(linearLayout);
        }*/
    }
}
