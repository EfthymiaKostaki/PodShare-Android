package com.aueb.podshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

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
        mAuth = FirebaseAuth.getInstance();
    }
}
