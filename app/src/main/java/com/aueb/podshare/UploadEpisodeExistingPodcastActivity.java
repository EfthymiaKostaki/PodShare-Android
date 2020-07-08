package com.aueb.podshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aueb.podshare.Sessions.AudioSharedPreference;
import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastDescriptionSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.Sessions.PrivacySharedPreference;
import com.aueb.podshare.classes.Episode;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.utils.BitmapUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UploadEpisodeExistingPodcastActivity extends AppCompatActivity {
    private Button backButton;
    private Button next;
    private String podcast_name_chosen = "";
    private Button cancel;
    private FirebaseAuth mAuth;
    RadioGroup rgb;
    private static final String TAG = "GET_PODCASTS" ;
    private static ArrayList<String> podcasts = new ArrayList<>();
    
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
        rgb = findViewById(R.id.podcast_choice);
        getPodcasts();
        for (String podcast : podcasts){
            RadioButton rb = new RadioButton(this);
            rb.setId(podcasts.indexOf(podcast));
            rb.setText(podcast);
            rb.setChecked(false);

            rgb.addView(rb);
        }

    }

    private void alertUser() {
        final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        final AudioSharedPreference audioSharedPreference = new AudioSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        final PrivacySharedPreference privacySharedPreference = new PrivacySharedPreference(UploadEpisodeExistingPodcastActivity.this);
        new AlertDialog.Builder(UploadEpisodeExistingPodcastActivity.this)
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?"+podcasts.get(0))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // DROP ALL SESSION OBJECTS UNTIL NOW FOR THE UPLOAD
                        episodeNameSharedPreference.terminateSession();
                        episodeDescriptionSharedPreference.terminateSession();
                        podcastNameSharedPreference.terminateSession();
                        if (podcastDescriptionSharedPreference != null) {
                            podcastDescriptionSharedPreference.terminateSession();
                            imageSharedPreference.terminateSession();
                        }
                        audioSharedPreference.terminateSession();
                        privacySharedPreference.terminateSession();
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
        PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeExistingPodcastActivity.this);
        if (podcastDescriptionSharedPreference != null) {
            podcastDescriptionSharedPreference.terminateSession();
        }
        podcastNameSharedPreference.saveSession(podcast_name_chosen);
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

    public void getPodcasts(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        // save to firebase at the correct folder and redirect to main activity.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", firebaseuser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().collection("podcasts")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot podcastDocument : task.getResult()) {
                                                        String name = (String) podcastDocument.get("name");
                                                        podcasts.add(name);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting podcasts: ", task.getException());
                                                    Toast.makeText(UploadEpisodeExistingPodcastActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(UploadEpisodeExistingPodcastActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
