package com.aueb.podshare;

import android.media.MediaPlayer;

import com.aueb.podshare.classes.Episode;

import java.io.IOException;
import java.util.List;

public class ChoosePodcastToPlayActivity {

    public void playPodcast(List<Episode> arrayListEpisodes, int adapterPosition) throws IOException {
        Episode episode = arrayListEpisodes.get(adapterPosition);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(episode.getEpisodeLink());
        mediaPLayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer.prepareAsync();
    }
}
