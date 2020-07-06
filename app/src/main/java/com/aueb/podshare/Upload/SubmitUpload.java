package com.aueb.podshare.Upload;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aueb.podshare.R;
import com.aueb.podshare.classes.Episode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import javax.annotation.Nullable;

public class SubmitUpload extends AppCompatActivity {

    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference eStorageRef;
    StorageTask eUploadTask;
    DatabaseReference referenceEpisodes;
    MediaMetadataRetriever metadataRetriever;
    byte [] cover;
    String str_title, str_description, str_podcast, str_publisher, str_duration;
    TextView title, description, podcast, publisher, duration;
    ImageView podcastCover;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
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
            String filename = getFileName(audioUri);
            textViewImage.setText(filename);
            metadataRetriever.setDataSource(this, audioUri);

            cover = metadataRetriever.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            podcastCover.setImageBitmap(bitmap);
            title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            description.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.M));
            podcast.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            publisher.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            duration.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

            str_title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            str_description = metadataRetriever.extractMetadata(MediaMetadataRetriever.M);
            str_podcast = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            str_publisher = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            str_duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int out = result.lastIndexOf('/');
            if (out != -1) {
                result = result.substring(out + 1);

            }
        }
        return result;

    }

    public void uploadFileToFirebase(View v) {
        if (textViewImage.equals("No file selected")) {
            Toast.makeText(this,"please select an image", Toast.LENGTH_SHORT).show();
        } else {
            if (pUploadTask != null && pUploadTask.isInProgress()) {
                Toast.makeText(this, "episode upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFiles();
            }
        }
    }

    private void uploadFiles() {
        if (audioUri != null) {
            Toast.makeText(this, "uploading, please wait", Toast.LENGTH_SHORT).show();
            final StorageReference storageReference = pStorageRef.child(System.currentTimeMillis()+'.'+getFileExtension(audioUri));
            pUploadTask = storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Episode episode = new Episode(str_title, str_description);
                            String uploadID = referenceEpisodes.push().getKey();
                            referenceEpisodes.child(uploadID).setValue(episode);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(this, "No file selected to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri audioUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
}
