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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UploadEpisodeFileActivity extends  AppCompatActivity {
    private Button submit;
    private Button cancel;
    private Button backButton;
    private Button addAudio;
    private boolean privacy_private = true;
    private Bitmap audio;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static int RESULT_LOAD_AUDIO = 1;
    
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
                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (file_extn.equals("3gp") || file_extn.equals("mp4") || file_extn.equals("m4a") || file_extn.equals("mp3") || file_extn.equals("ogg")) {
                    //FINE
                    audio = BitmapFactory.decodeFile(filePath);
                    Toast
                            .makeText(UploadEpisodeFileActivity.this,
                                    "Episode was successfully added.",
                                    Toast.LENGTH_SHORT)
                            .show();
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
        if (audio == null) {
            alertEmptyFields();
        } else {
            // save to firebase at the correct folder and redirect to main activity.
        }
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
        new AlertDialog.Builder(UploadEpisodeFileActivity.this)
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // DROP ALL SESSION OBJECTS UNTIL NOW FOR THE UPLOAD
                        startActivity(new Intent(UploadEpisodeFileActivity.this, MainActivity.class));
                        finish();
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
                    privacy_private = false;
                break;
            case R.id.privacy_public:
                if (checked)
                    privacy_private = true;
                break;
        }
    }
}
