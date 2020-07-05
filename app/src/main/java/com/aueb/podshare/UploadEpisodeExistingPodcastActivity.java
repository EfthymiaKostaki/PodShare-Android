package com.aueb.podshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
        // TO DO ADD ALERT FOR USER TO CONFIRM
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
