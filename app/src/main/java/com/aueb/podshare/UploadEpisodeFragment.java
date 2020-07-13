package com.aueb.podshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aueb.podshare.Sessions.AudioSharedPreference;
import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastDescriptionSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.Sessions.PrivacySharedPreference;
import com.aueb.podshare.view.InputLayoutWithEditTextView;

public class UploadEpisodeFragment extends Fragment {

    private Button next;
    private Button cancel;
    private boolean existingPodcastChecked = true;
    private InputLayoutWithEditTextView episodeName;
    private InputLayoutWithEditTextView episodeDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.upload_episode_fragment, container, false);
        next = view.findViewById(R.id.next);
        episodeName = view.findViewById(R.id.episode_name);
        episodeDescription = view.findViewById(R.id.episode_description);
        cancel = view.findViewById(R.id.cancel_button);
        EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(getActivity());
        EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(getActivity());
        if (episodeNameSharedPreference.getSession() != null) {
            episodeName.setEditTextValue(episodeNameSharedPreference.getSession());
            episodeDescription.setEditTextValue(episodeDescriptionSharedPreference.getSession());
        }

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.accuracy_choice);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId) {
                    case R.id.new_podcast_choice_button:
                        existingPodcastChecked = false;
                        break;
                    case R.id.existing_podcast_choice_button:
                        existingPodcastChecked = true;
                        break;
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertUser();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (episodeName.getEditTextValue().equals("") || episodeDescription.getEditTextValue().equals("")) {
                    alertEmptyFields();
                } else {
                    EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(getActivity());
                    EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(getActivity());
                    episodeNameSharedPreference.saveSession(episodeName.getEditTextValue());
                    episodeDescriptionSharedPreference.saveSession(episodeDescription.getEditTextValue());
                    if (existingPodcastChecked) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, new UploadEpisodeExistingPodcastFragment());
                        fragmentTransaction.commit();
                    } else {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, new UploadEpisodeNewPodcastFragment());
                        fragmentTransaction.commit();
                    }
                }
            }
        });

        return view;
    }

    private void alertUser() {
        final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(getActivity());
        final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(getActivity());
        final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(getActivity());
        final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(getActivity());
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(getActivity());
        final AudioSharedPreference audioSharedPreference = new AudioSharedPreference(getActivity());
        final PrivacySharedPreference privacySharedPreference = new PrivacySharedPreference(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        episodeNameSharedPreference.terminateSession();
                        episodeDescriptionSharedPreference.terminateSession();
                        podcastNameSharedPreference.terminateSession();
                        podcastDescriptionSharedPreference.terminateSession();
                        imageSharedPreference.terminateSession();
                        audioSharedPreference.terminateSession();
                        privacySharedPreference.terminateSession();
                        startActivity(new Intent((getActivity()), MainActivity.class));
                        getActivity().finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void alertEmptyFields() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Empty fields")
                .setMessage("Please add values to all the fields")
                .setNegativeButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
