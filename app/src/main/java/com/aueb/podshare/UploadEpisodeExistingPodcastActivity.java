package com.aueb.podshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UploadEpisodeExistingPodcastActivity extends AppCompatActivity {
    private Button backButton;
    private Button next;
    private String podcast_name_chosen = "";
    private Button cancel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_episode_existing_podcast);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadEpisodeActivity();
            }
        });
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadEpisodeFileActivity();
            }
        });
        cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertUser();
            }
        });
    }

    private void alertUser() {
        new AlertDialog.Builder(UploadEpisodeExistingPodcastActivity.this)
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // DROP ALL SESSION OBJECTS UNTIL NOW FOR THE UPLOAD
                        startActivity(new Intent(UploadEpisodeExistingPodcastActivity.this, MainActivity.class));
                        finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToUploadEpisodeFileActivity() {
        startActivityForResult(new Intent(this, UploadEpisodeFileActivity.class), 101);
        finish();
    }

    private void goToUploadEpisodeActivity() {
        startActivity(new Intent(this, UploadEpisodeActivity.class));
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        RadioGroup radioGroup = findViewById(R.id.podcast_choice);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        podcast_name_chosen = radioButton.getText().toString();
    }
}
