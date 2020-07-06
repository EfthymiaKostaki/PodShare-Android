package com.aueb.podshare.Upload;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aueb.podshare.MainActivity;
import com.aueb.podshare.R;
import com.aueb.podshare.classes.Episode;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class UploadEpisodeStep1 extends AppCompatActivity {

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
        Episode episode = new Episode(str_name, str_description);
    }

    private void goToUploadEpisodeStep2ExistingPodcastActivity() {
        startActivity(new Intent(this, UploadEpisodeStep2ExistingPodcast.class));
        finish();
    }

    private void goToUploadEpisodeStep2NewPodcastActivity() {
        startActivity(new Intent(this, UploadEpisodeStep2NewPodcast.class));
        finish();
    }
}
