package com.aueb.podshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aueb.podshare.R;
import com.aueb.podshare.classes.Podcast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class MainActivityKoko extends AppCompatActivity{

    AppCompatEditText editTextTitle;
    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorageRef;
    StorageTask mUploadTask;

    private void playPodcast(String path) {
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openAudioFile(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            audioUri = data.getData();
            String fileName = getFileName(audioUri);

        }
    }

    private String getFileName(Uri uri){
        String result = null;
        if (uri.get)
    }

    private List<String> podcastFiles;

    private void addPodcastFilesFrom(String dirPath) {
        final File podcastDir = new File(dirPath);
        if (!podcastDir.exists()) {
            podcastDir.mkdir();
            return;
        }
        final File[] files = podcastDir.listFiles();
        for (File file : files){
            final String path = file.getAbsolutePath();
            if (path.endsWith(".mp3")) {
                podcastFiles.add(path);
            }
        }
    }

    public void uploadAudioToFirebase(View v) {
        if (textViewImage.getText().toString().equals("No file selected")){
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show());
        } else {
            uploadPodcast();
        }
    }

    private void uploadPodcast() {
        if (audioUri != null) {
            String durationTxt;
            Toast.makeText(getApplicationContext(), "Uploading, please wait...", Toast.LENGTH_LONG).show();

            progressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "."+getFileExtension(audioUri));

            int durationInMillis = findSongDuration(audioUri);
            if (durationInMillis == 0){
                durationTxt = "Empty";
            }
            durationTxt = getDurationFromMillis(durationInMillis);
            mUploadTask = storageReference.putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Podcast podcast = new Podcast(editTextTitle.getText().toString(), );
                        }
                    })
        }
    }

    private String getDurationFromMillis(int durationInMillis){
        Date date = new Date(durationInMillis);
        SimpleDateFormat simple_date = new SimpleDateFormat("mm:ss", Locale.getDefault());
        String time = simple_date.format(date);
        return time;

    }

    private int findSongDuration(Uri audioUri){
        int timeInMillis = 0;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(String.valueOf(this.audioUri));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillis = Integer.parseInt(time);

            retriever.release();
            return timeInMillis;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getFileExtension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
}
