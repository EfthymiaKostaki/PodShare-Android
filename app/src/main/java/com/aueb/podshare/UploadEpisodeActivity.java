package com.aueb.podshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
        /* Alert dialog is on the making
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setMessage("Are you sure you want to disregard your changes?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();*/
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToPodcastActivity() {
        if (existingPodcastChecked) {
            startActivity(new Intent(this, UploadEpisodeExistingPodcastActivity.class));
        } else {
            startActivity(new Intent(this, UploadEpisodeNewPodcastActivity.class));
        }
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
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
