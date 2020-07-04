package com.aueb.podshare.mediaPlayer;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class EpisodePlayerActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Boolean check = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
    }
}
