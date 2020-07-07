package com.aueb.podshare;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.aueb.podshare.utils.BitmapUtil;
import com.aueb.podshare.view.InputLayoutWithEditTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadEpisodeNewPodcastActivity extends AppCompatActivity {
    private Button backButton;
    private Button addImage;
    private Button next;
    private Button cancel;
    private Bitmap image;
    private String str_image;
    private ImageView imageView;
    private InputLayoutWithEditTextView podcastName;
    private InputLayoutWithEditTextView podcastDescription;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_episode_new_podcast);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadEpisodeActivity();
            }
        });
        podcastName = findViewById(R.id.podcast_name);
        podcastDescription = findViewById(R.id.podcast_description);
        PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeNewPodcastActivity.this);
        PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeNewPodcastActivity.this);
        if (podcastNameSharedPreference.getSession() != null) {
            podcastName.setEditTextValue(podcastNameSharedPreference.getSession());
            podcastDescription.setEditTextValue(podcastDescriptionSharedPreference.getSession());
        }
        addImage = findViewById(R.id.add_image);
        imageView = findViewById(R.id.imgView);
        ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeNewPodcastActivity.this);
        if (imageSharedPreference.getSession() != null) {
            image = BitmapUtil.decodeBase64(imageSharedPreference.getSession());
            imageView.setImageBitmap(image);
        }
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
        final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(UploadEpisodeNewPodcastActivity.this);
        final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(UploadEpisodeNewPodcastActivity.this);
        final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeNewPodcastActivity.this);
        final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeNewPodcastActivity.this);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeNewPodcastActivity.this);
        new AlertDialog.Builder(UploadEpisodeNewPodcastActivity.this)
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?")
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
                        startActivity(new Intent(UploadEpisodeNewPodcastActivity.this, MainActivity.class));
                        finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToUploadEpisodeFileActivity() {
        if (podcastName.getEditTextValue().equals("") || podcastDescription.getEditTextValue().equals("") || image == null) {
            alertEmptyFields();
        } else {
            PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(UploadEpisodeNewPodcastActivity.this);
            PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(UploadEpisodeNewPodcastActivity.this);
            ImageSharedPreference imageSharedPreference = new ImageSharedPreference(UploadEpisodeNewPodcastActivity.this);
            podcastNameSharedPreference.saveSession(podcastName.getEditTextValue());
            podcastDescriptionSharedPreference.saveSession(podcastDescription.getEditTextValue());
            imageSharedPreference.saveSession(str_image);
            startActivityForResult(new Intent(this, UploadEpisodeFileActivity.class), 100);
            finish();
        }
    }

    private void alertEmptyFields() {
        new AlertDialog.Builder(UploadEpisodeNewPodcastActivity.this)
                .setTitle("Empty fields")
                .setMessage("Please add values to all the fields")
                .setNegativeButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToUploadEpisodeActivity() {
        startActivity(new Intent(this, UploadEpisodeActivity.class));
        finish();
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
                Toast.makeText(UploadEpisodeNewPodcastActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(UploadEpisodeNewPodcastActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                UploadEpisodeNewPodcastActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            UploadEpisodeNewPodcastActivity.this,
                            new String[] { permission },
                            requestCode);
        }
        else {
            Toast
                    .makeText(UploadEpisodeNewPodcastActivity.this,
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
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    str_image = encodeToBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    //FINE
                    image = BitmapFactory.decodeFile(filePath);
                    ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    imageView.setImageBitmap(image);
                }  //NOT IN REQUIRED FORMAT

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

    public static String encodeToBase64(Bitmap image) {
        Bitmap bitmap = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }
}
