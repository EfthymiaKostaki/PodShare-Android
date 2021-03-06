package com.aueb.podshare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.adapter.EpisodeAdapter;
import com.aueb.podshare.classes.Episode;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;
import com.aueb.podshare.utils.BitmapUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PodcastProfileFragment extends Fragment {

    private static final String TAG = "PODCAST PROFILE FRAG";
    private ProgressDialog progressDialog;
    private User user;
    private View view;
    private byte[] podcastImage;
    private ListView episodesList;
    private ArrayList<Episode> podcastEpisodes = new ArrayList<>();
    private ArrayList<String> episodeTitles = new ArrayList<>();

    public PodcastProfileFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.podcast_profile_fragment, container, false);
        showLoading();
        getPodcastDetails();
        episodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String selected = ((TextView) view.findViewById(R.id.episode_name_txt)).getText().toString();

                Toast toast = Toast.makeText(getContext(), selected, Toast.LENGTH_SHORT);
                toast.show();
                EpisodeNameSharedPreference episode = new EpisodeNameSharedPreference(getContext());
                EpisodeDescriptionSharedPreference description = new EpisodeDescriptionSharedPreference(getContext());
                String descriptionEp = ((TextView) view.findViewById(R.id.episode_description)).getText().toString();
                description.saveSession(descriptionEp);
                episode.saveSession(selected);
                showLoading();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                final PodcastNameSharedPreference podcast = new PodcastNameSharedPreference(getContext());
                int index = 0;
                final int[] countDownloadedUris = {0};
                ArrayList<Episode> episodes = getPodcast().getEpisodes();
                ArrayList<Uri> uris = new ArrayList<>();
                for (final int[] i = {0}; i[0] < episodes.size(); i[0]++) {
                    Log.d(TAG, "Inside for: " + i[0]);
                    if (episode.getSession().equals(episodes.get(i[0]).get_name())) {
                        index = i[0];
                        Log.d(TAG, "Found episode selected index: " + i[0]);
                    }

                    StorageReference episodesRef = storageRef.child("users/" + user.getUid() + "/podcasts/" + podcast.getSession() + "/episodes/" + episodes.get(i[0]).get_name() + "/" + episodes.get(i[0]).get_name() + ".mp3");
                    int finalIndex = index;
                    episodesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            uris.add(uri);
                            Log.d(TAG, "Adding uri in position: " + finalIndex);
                            if (countDownloadedUris[0] == episodes.size() - 1) {
                                dismissLoading();
                                loadFragment(new MyMediaPlayerFragment(user, podcastImage, uris, finalIndex));
                            }
                            countDownloadedUris[0]++;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.d(TAG, "Failure in index: " + finalIndex);
                        }
                    });
                }

            }
        });
        return view;
    }

    private Podcast getPodcast() {
        PodcastNameSharedPreference podcastSession = new PodcastNameSharedPreference(getContext());
        String podcastName = podcastSession.getSession();
        ArrayList<Podcast> podcasts = user.getPodcasts();
        Podcast podcast = null;
        for (Podcast pod : podcasts) {
            if (pod.getName().equals(podcastName)) {
                podcast = pod;
            }
        }
        return podcast;
    }

    private void getPodcastDetails() {
        PodcastNameSharedPreference podcastSession = new PodcastNameSharedPreference(getContext());
        String podcastName = podcastSession.getSession();
        Podcast podcast = getPodcast();
        final RelativeLayout relativeImage = view.findViewById(R.id.relative_image);
        TextView podcastNameText = view.findViewById(R.id.podcast_name);
        podcastNameText.setText(podcastName);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(getContext());
        Bitmap bitmap = BitmapUtil.decodeBase64(imageSharedPreference.getSession());
        ImageView imagePodsharer = view.findViewById(R.id.podcast_creator_picture);
        imagePodsharer.setImageBitmap(bitmap);
        TextView podsharerName = view.findViewById(R.id.podcast_creator_name);
        podsharerName.setText(user.getUsername());
        TextView numberEpisodes = view.findViewById(R.id.number_of_episodes);
        numberEpisodes.setText(String.valueOf(podcast.get_number_of_episodes()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("users/" + user.getUid() + "/podcasts/" + podcastName + "/" + podcastName + ".png");
        final long ONE_MEGABYTE = 1024 * 1024;
        userImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                podcastImage = bytes;
                Bitmap bitmapPodcast = BitmapFactory.decodeByteArray(podcastImage, 0, podcastImage.length);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapPodcast);
                relativeImage.setBackground(bitmapDrawable);
                dismissLoading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Could not retrieve Podsharer image.", Toast.LENGTH_SHORT).show();
            }
        });

        podcastEpisodes = podcast.getEpisodes();

        for (int i = 0; i < podcastEpisodes.size(); i++) {
            episodeTitles.add(podcastEpisodes.get(i).get_name());
        }

        EpisodeAdapter episodeAdapter = new EpisodeAdapter(episodeTitles, podcastEpisodes, getActivity());

        episodesList = view.findViewById(R.id.episodes_list);
        episodesList.setAdapter(episodeAdapter);

        dismissLoading();
    }

    private void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage(getString(R.string.fetching_episodes));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void dismissLoading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        podcastEpisodes = new ArrayList<>();
        episodeTitles = new ArrayList<>();
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
