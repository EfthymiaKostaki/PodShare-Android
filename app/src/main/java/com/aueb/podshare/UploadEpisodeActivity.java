package com.aueb.podshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aueb.podshare.Sessions.AudioSharedPreference;
import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastDescriptionSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.Sessions.PrivacySharedPreference;
import com.aueb.podshare.view.InputLayoutWithEditTextView;

public class UploadEpisodeActivity extends AppCompatActivity {

    private Button next;
    private Button cancel;
    private boolean existingPodcastChecked = true;
    private InputLayoutWithEditTextView episodeName;
    private InputLayoutWithEditTextView episodeDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_episode);
        next = findViewById(R.id.next);
        episodeName = findViewById(R.id.episode_name);
        episodeDescription = findViewById(R.id.episode_description);
        cancel = findViewById(R.id.cancel_button);
        EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeActivity.this);
        EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeActivity.this);
        if (episodeNameSharedPreference.getSession() != null) {
            episodeName.setEditTextValue(episodeNameSharedPreference.getSession());
            episodeDescription.setEditTextValue(episodeDescriptionSharedPreference.getSession());
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertUser();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPodcastActivity();
            }
        });

    }

    private void alertUser() {
        final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeActivity.this);
        final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeActivity.this);
        final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeActivity.this);
        final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeActivity.this);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeActivity.this);
        final AudioSharedPreference audioSharedPreference = new AudioSharedPreference(UploadEpisodeActivity.this);
        final PrivacySharedPreference privacySharedPreference = new PrivacySharedPreference(UploadEpisodeActivity.this);
        new AlertDialog.Builder(UploadEpisodeActivity.this)
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
                        startActivity(new Intent(UploadEpisodeActivity.this, MainActivity.class));
                        finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToPodcastActivity() {
        if (episodeName.getEditTextValue().equals("") || episodeDescription.getEditTextValue().equals("")) {
            alertEmptyFields();
        } else {
            EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeActivity.this);
            EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeActivity.this);
            episodeNameSharedPreference.saveSession(episodeName.getEditTextValue());
            episodeDescriptionSharedPreference.saveSession(episodeDescription.getEditTextValue());
            if (existingPodcastChecked) {
                startActivity(new Intent(this, UploadEpisodeExistingPodcastActivity.class));
            } else {
                startActivity(new Intent(this, UploadEpisodeNewPodcastActivity.class));
            }
            finish();
        }
    }

    private void alertEmptyFields() {
        new AlertDialog.Builder(UploadEpisodeActivity.this)
                .setTitle("Empty fields")
                .setMessage("Please add values to all the fields")
                .setNegativeButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.new_podcast_choice_button:
                if (checked)
                    existingPodcastChecked = false;
                break;
            case R.id.existing_podcast_choice_button:
                if (checked)
                    existingPodcastChecked = true;
                break;
        }
    }

}
