package com.aueb.podshare.Upload;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aueb.podshare.MainActivity;
import com.aueb.podshare.R;
import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.SessionManagement;
import com.aueb.podshare.classes.Podcast;

import java.util.Calendar;

public class UploadEpisodeStep2NewPodcast extends AppCompatActivity {

    TextView name, description;
    String str_name, str_description;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.episode_name_text);
        description = findViewById(R.id.episode_description_text);
        str_name = name.toString();
        str_description = description.toString();
        SessionManagement session = new SessionManagement(this);
        String username = session.getSession();
        EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(this);
        EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(this);
        episodeNameSharedPreference.saveSession(str_name);
        episodeDescriptionSharedPreference.saveSession(str_description);

    }

    private void goToSubmitUploadActivity() {
        startActivity(new Intent(this, SubmitUpload.class));
        finish();
    }
}
