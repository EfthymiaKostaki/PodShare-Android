package com.aueb.podshare;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aueb.podshare.view.InputLayoutWithEditTextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.BreakIterator;

public class UploadEpisodeNewPodcastActivity extends AppCompatActivity {
    private Button backButton;
    private Button addImage;
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
        addImage = findViewById(R.id.add_image);
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
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(UploadEpisodeNewPodcastActivity.this,
                        "Camera Permission Denied",
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

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    //FINE
                    Bitmap image = BitmapFactory.decodeFile(filePath);
                    ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    imageView.setImageBitmap(image);
                } else {
                    //NOT IN REQUIRED FORMAT
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
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            assert selectedImage != null;
            Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (Build.VERSION.SDK_INT >= 29) {
                    // You can replace '0' by 'cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)'
                    // Note that now, you read the column '_ID' and not the column 'DATA'
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(column_index));
                    // now that you have the media URI, you can decode it to a bitmap
                    try (ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(imageUri, "r")) {
                        if (pfd != null) {
                            Bitmap image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                            ImageView imageView = (ImageView) findViewById(R.id.imgView);
                            imageView.setImageBitmap(image);
                        }
                    } catch (IOException ex) {
                        Log.println(Log.ERROR, "URI", String.valueOf(imageUri));
                        ex.printStackTrace();
                    }
                } else {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }
            }
            cursor.close();
        }
    }
    
 */
}
