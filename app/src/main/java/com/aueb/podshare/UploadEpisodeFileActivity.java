package com.aueb.podshare;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastDescriptionSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class UploadEpisodeFileActivity<StorageReference> extends  AppCompatActivity {
    private static final String TAG = "UPLOAD_FILE" ;
    private Button submit;
    private Button cancel;
    private Button backButton;
    private Button addAudio;
    private boolean privacy_private = true;
    private Bitmap audio;
    private FirebaseAuth mAuth;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static int RESULT_LOAD_AUDIO = 1;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private com.google.firebase.storage.StorageReference usersRef = storage.getReference();
    private InputStream stream;
    private String filePath;
    private String file_extn;


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
        addAudio = findViewById(R.id.add_audio);
        addAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_AUDIO);
            }
        });
    }

    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                UploadEpisodeFileActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            UploadEpisodeFileActivity.this,
                            new String[] { permission },
                            requestCode);
        }
        else {
            Toast
                    .makeText(UploadEpisodeFileActivity.this,
                            "Permission already granted",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                filePath = getPath(selectedImage);
                file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (file_extn.equals("3gp") || file_extn.equals("mp4") || file_extn.equals("m4a") || file_extn.equals("mp3") || file_extn.equals("ogg")) {
                    //FINE
                    try {
                        stream = new FileInputStream(new File(filePath));
                        Toast
                                .makeText(UploadEpisodeFileActivity.this,
                                        "Audio was successfully uploaded.",
                                        Toast.LENGTH_SHORT)
                                .show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.d("audio log",filePath);
                }  //NOT IN REQUIRED FORMAT

            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(UploadEpisodeFileActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(UploadEpisodeFileActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        assert cursor != null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
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
        if (filePath.equals("")) {
            alertEmptyFields();
        } else {
            final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeFileActivity.this);
            final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeFileActivity.this);
            final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeFileActivity.this);
            final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeFileActivity.this);
            final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeFileActivity.this);

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
                                    try {
                                        if (getCallingActivity() != null) {
                                            String shortClassName = getCallingActivity().getClassName();
                                            final Episode episode = new Episode(episodeNameSharedPreference.getSession(), episodeDescriptionSharedPreference.getSession());
                                            episode.set_privacy(privacy_private);
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            assert firebaseUser != null;
                                            String userId = firebaseUser.getUid();
                                            if (shortClassName.equals("com.aueb.podshare.UploadEpisodeNewPodcastActivity")) {
                                                Podcast podcast = new Podcast(podcastNameSharedPreference.getSession(), podcastDescriptionSharedPreference.getSession(), Calendar.getInstance().getTime());
                                                document.getReference().collection("podcasts").add(podcast);
                                                Bitmap image = BitmapUtil.decodeBase64(imageSharedPreference.getSession());
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                                byte[] data = baos.toByteArray();
                                                com.google.firebase.storage.StorageReference podcastRef = usersRef.child("users/" + userId + "/podcasts/" + podcastNameSharedPreference.getSession()+ "/"+ podcastNameSharedPreference.getSession()+".png");
                                                UploadTask uploadTask = podcastRef.putBytes(data);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Log.w(TAG, "Error uploading to storage default user image.");
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                        Log.w(TAG, "Successfully uploaded to storage default user image.");
                                                    }
                                                });
                                            }
                                            document.getReference().collection("podcasts").whereEqualTo("name", podcastNameSharedPreference.getSession())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot podcastDocument : task.getResult()) {
                                                                    podcastDocument.getReference().collection("episodes").add(episode);
                                                                }
                                                            }
                                                        }
                                                    });
                                            UploadTask uploadTask = usersRef.child("users/"+ userId + "/podcasts/" + podcastNameSharedPreference.getSession()+ "/episodes/"+ episodeNameSharedPreference.getSession() + "/" + episodeNameSharedPreference.getSession() + "." +file_extn).putStream(stream);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    Toast
                                                            .makeText(UploadEpisodeFileActivity.this,
                                                                    "Something went wrong. Please try again later.",
                                                                    Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                    Toast
                                                            .makeText(UploadEpisodeFileActivity.this,
                                                                    "Episode was successfully added.",
                                                                    Toast.LENGTH_SHORT)
                                                            .show();}
                                            });

                                            Log.d("audio log",filePath);
                                            episodeNameSharedPreference.terminateSession();
                                            episodeDescriptionSharedPreference.terminateSession();
                                            podcastNameSharedPreference.terminateSession();
                                            podcastDescriptionSharedPreference.terminateSession();
                                            imageSharedPreference.terminateSession();
                                            goToMainActivity();
                                        } else {
                                            Log.e("class was not found", "calling activity does not exist");
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                Toast.makeText(UploadEpisodeFileActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void goToMainActivity() {
        startActivity(new Intent(UploadEpisodeFileActivity.this, MainActivity.class));
        finish();
    }


    private void alertEmptyFields() {
        new AlertDialog.Builder(UploadEpisodeFileActivity.this)
                .setTitle("Empty fields")
                .setMessage("Please add values to all the fields")
                .setNegativeButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void alertUser() {
        final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeFileActivity.this);
        final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeFileActivity.this);
        final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeFileActivity.this);
        final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeFileActivity.this);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeFileActivity.this);
        new AlertDialog.Builder(UploadEpisodeFileActivity.this)
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?"+episodeNameSharedPreference.getSession())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // DROP ALL SESSION OBJECTS UNTIL NOW FOR THE UPLOAD
                        episodeNameSharedPreference.terminateSession();
                        episodeDescriptionSharedPreference.terminateSession();
                        podcastNameSharedPreference.terminateSession();
                        podcastDescriptionSharedPreference.terminateSession();
                        imageSharedPreference.terminateSession();
                        goToMainActivity();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.privacy_private:
                if (checked)
                    privacy_private = true;
                break;
            case R.id.privacy_public:
                if (checked)
                    privacy_private = false;
                break;
        }
    }
}
