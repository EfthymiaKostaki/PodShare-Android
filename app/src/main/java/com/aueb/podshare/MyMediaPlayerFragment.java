package com.aueb.podshare;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.adapter.EpisodeAdapter;
import com.aueb.podshare.classes.Episode;
import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.Sessions.PodsharerNameSharedPreference;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;
import com.aueb.podshare.utils.BitmapUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

public class MyMediaPlayerFragment extends Fragment implements Playable {

    List<Episode> episodes;
    int position = 0;
    boolean isPlaying = false;
    TextView elapsedTime;
    TextView remainingTime;
    private Button playButton;
    private ListView episodesList;
    private ArrayList<Episode> podcastEpisodes = new ArrayList<>();
    private ArrayList<String> episodeTitles = new ArrayList<>();
    SeekBar seekBar;
    Runnable runnable;
    MediaPlayer mediaPlayer;
    NotificationManager notificationManager;
    private User user;
    private byte[] podcastImage;
    public static String TAG = "MEDIA PLAYER";
    private ArrayList<Uri> episodesAudioUri;
    private Bitmap audioPlay;
    private int indexCurrentAudioPlaying;


    public MyMediaPlayerFragment(User user, byte[] podcastImage, ArrayList<Uri> uris, int index ) {
        this.user = user;

        this.podcastImage = podcastImage;
        this.episodesAudioUri = uris;
        this.indexCurrentAudioPlaying = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final PodsharerNameSharedPreference podsharer = new PodsharerNameSharedPreference(getContext());
        final EpisodeNameSharedPreference episode = new EpisodeNameSharedPreference(getContext());
        final EpisodeDescriptionSharedPreference episodeDescription = new EpisodeDescriptionSharedPreference(getContext());
        final PodcastNameSharedPreference podcast = new PodcastNameSharedPreference(getContext());
        final ImageSharedPreference image = new ImageSharedPreference(getContext());
        View view = inflater.inflate(R.layout.play_episode_fragment, container, false);
        
        ImageView podsharerPlay = view.findViewById(R.id.podsharer_play);
        podsharerPlay.setImageBitmap(BitmapUtil.decodeBase64(image.getSession()));
        TextView episodeText = view.findViewById(R.id.episode_name_play);
        episodeText.setText(episode.getSession());
        TextView posharerText = view.findViewById(R.id.podsharer_play_text);
        posharerText.setText(podsharer.getSession());
        TextView descriptionEpisode = (TextView) view.findViewById(R.id.description_play);
        descriptionEpisode.setText(episodeDescription.getSession());
        Bitmap bitmap = BitmapFactory.decodeByteArray(podcastImage, 0, podcastImage.length);
        LinearLayout lin = view.findViewById(R.id.play_inside);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        lin.setBackground(bitmapDrawable);

        seekBar = (SeekBar) view.findViewById(R.id.progressBar);
        playButton = (Button) view.findViewById(R.id.playButton);
        elapsedTime = (TextView) view.findViewById(R.id.elapsedTime);
        remainingTime = (TextView) view.findViewById(R.id.remainingTime);
        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), episodesAudioUri.get(indexCurrentAudioPlaying));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        TextView durationView = (TextView) view.findViewById(R.id.duration);
        durationView.setText(createTimeLabel(mediaPlayer.getDuration()));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                playCycle();
                mediaPlayer.start();
                MyNotification.createNotification(getActivity(), "koko", R.drawable.pause, position, 1);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(v);
                MyNotification.createNotification(getActivity(), "loko", R.drawable.pause, position, 1);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(getTag(), "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    loadFragment(new PodsharerProfileFragment());
                    return true;
                } else {
                    return false;
                }
            }
        });

        String podcastName = podcast.getSession();
        ArrayList<Podcast> podcasts = user.getPodcasts();
        Podcast myPodcast = null;
        for (Podcast pod : podcasts) {
            if (pod.getName().equals(podcastName)) {
                myPodcast = pod;
            }
        }

        podcastEpisodes = myPodcast.getEpisodes();

        for (int i = 0; i < podcastEpisodes.size(); i++) {
            episodeTitles.add(podcastEpisodes.get(i).get_name());
        }

        EpisodeAdapter episodeAdapter = new EpisodeAdapter(episodeTitles, podcastEpisodes, getActivity());

        episodesList = view.findViewById(R.id.other_episodes_list);
        episodesList.setAdapter(episodeAdapter);

        episodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The selected episode will start playing
            }
        });

        return view;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MyNotification.CHANNEL_ID,
                    "PODSHARE", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
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
        public void handleMessage(Message msg) {
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
    public void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");

            switch (action) {
                case MyNotification.ACTION_PREVIOUS:
                    onEpisodePrevious();
                    break;
                case MyNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()) {
                        onEpisodePause();
                    } else {
                        onEpisodePlay();
                    }
                    break;
                case MyNotification.ACTION_NEXT:
                    onEpisodeNext();
                    break;
            }
        }
    };


    @Override
    public void onEpisodePrevious() {
        position--;
        MyNotification.createNotification(getActivity(), "Nico", R.drawable.pause, position, episodes.size() - 1);
    }

    @Override
    public void onEpisodePlay() {

    }

    @Override
    public void onEpisodePause() {

    }

    @Override
    public void onEpisodeNext() {
        position++;
        MyNotification.createNotification(getActivity(), "Rico", R.drawable.play, position, 2);
        playButton.setBackgroundResource(R.drawable.replay);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
