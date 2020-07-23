package com.aueb.podshare;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyMediaPlayer extends AppCompatActivity {

    TextView elapsedTime;
    TextView remainingTime;
    Button playButton;
    SeekBar seekBar;
    Runnable runnable;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_play_episode);

        seekBar = (SeekBar) findViewById(R.id.progressBar);
        playButton = (Button) findViewById(R.id.playButton);
        elapsedTime = (TextView) findViewById(R.id.elapsedTime);
        remainingTime = (TextView) findViewById(R.id.remainingTime);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.podcast);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                playCycle();
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setBackgroundResource(R.drawable.replay);
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void playCycle() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Message message = new Message();
                        message.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(message);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            int currentPosition = msg.what;
            seekBar.setProgress(currentPosition);

            String elapsedTimeLabel = createTimeLabel(currentPosition);
            elapsedTime.setText(elapsedTimeLabel);
            String remainingTimeLabel = createTimeLabel(mediaPlayer.getDuration() - currentPosition);
            remainingTime.setText("- " + remainingTimeLabel);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

    public void playAudio(View view) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.play_button);
        } else {
            mediaPlayer.pause();
            playButton.setBackgroundResource(R.drawable.play);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }
}