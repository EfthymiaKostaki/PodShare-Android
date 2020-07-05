package com.aueb.podshare;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class UploadEpisodeFileActivity extends  AppCompatActivity {
    private Button submit;
    private Button cancel;
    private Button backButton;
    private Button addAudio;
    private boolean privacy_private = true;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_episode_file);
        cancel = findViewById(R.id.cancel_button);
        submit = findViewById(R.id.submit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertUser();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase();
            }
        });
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadPreviousActivity();
            }
        });
        addAudio = findViewById(R.id.add_image);
        /*addAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {;
            }
        });*/
    }

    private void goToUploadPreviousActivity() {
        if (getCallingActivity() != null) {
            String shortClassName = getCallingActivity().getClassName();
            if (shortClassName.equals("com.aueb.podshare.UploadEpisodeNewPodcastActivity")) {
                startActivity(new Intent(this, UploadEpisodeNewPodcastActivity.class));
                finish();
            } else if (shortClassName.equals("com.aueb.podshare.UploadEpisodeExistingPodcastActivity")){
                startActivity(new Intent(this, UploadEpisodeExistingPodcastActivity.class));
                finish();
            } else {
                Log.e("class was not found", shortClassName);
            }
        } else {
            Log.e("class was not found", "calling activity does not exist");
        }
    }

    private void saveToFirebase() {
        // save to firebase at the correct folder and redirect to main activity.
    }

    private void alertUser() {
        // TO DO: ADD ALERT DIALOG. ALSO,  WHEN THIS IS CONFIRMED ALL SESSION OBJECTS SHOULD BE DROPPED.
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.privacy_private:
                if (checked)
                    privacy_private = false;
                break;
            case R.id.privacy_public:
                if (checked)
                    privacy_private = true;
                break;
        }
    }
}
