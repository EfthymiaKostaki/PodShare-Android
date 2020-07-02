package com.aueb.podshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aueb.podshare.mediaPlayer.MediaPlayerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import jp.wasabeef.blurry.Blurry;

/* DOESN'T WORK! Is not integrated to main activity */
public class MediaPlayerActivity extends AppCompatActivity {
    private String TAG = "TAG_Media_Player_Activity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_episode);
        /*
        ViewGroup play_image = (ViewGroup) findViewById(R.id.play_image);
        Blurry.with(getApplicationContext())
                .radius(10)
                .sampling(8)
                .color(Color.argb(66, 255, 255, 0))
                .async()
                .onto(play_image);*/

        Button mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
        MediaPlayerService mediaPlayerService = new MediaPlayerService();

    }

    private void updateUI() {
        FirebaseUser u = mAuth.getCurrentUser();
    }
    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
