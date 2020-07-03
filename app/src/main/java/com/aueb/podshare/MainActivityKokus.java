package com.aueb.podshare;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import javax.annotation.Nullable;

public class MainActivityKokus extends AppCompatActivity {

    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference pStorageRef;
    StorageTask pUploadTask;
    DatabaseReference referenceEpisodes;
    MediaMetadataRetriever metadataRetriever;
    byte [] cover;
    String str_title, str_description, str_podcast, str_publisher, str_duration;
    TextView title, description, podcast, publisher, duration;
    ImageView podcastCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) implements AdapterView.OnItemSelectedListener {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewImage = findViewById(R.id.textViewPodcastsSelected);
        progressBar = findViewById(R.id.progressBar);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        publisher = findViewById(R.id.publisher);
        duration = findViewById(R.id.duration);
        podcast = findViewById(R.id.podcast);
        podcastCover = findViewById(R.id.imageview);

        metadataRetriever = new MediaMetadataRetriever();
        referenceEpisodes = FirebaseDatabase.getInstance().getReference().child("episodes");
        pStorageRef = FirebaseStorage.getInstance().getReference().child("episodes");



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openAudioFiles(View v) {
        Intent i =new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            audioUri = data.getData();
            metadataRetriever.setDataSource(this, audioUri);

            cover = metadataRetriever.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            podcastCover.setImageBitmap(bitmap);
            title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            
            podcast.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            publisher.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            duration.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        }
    }

}
