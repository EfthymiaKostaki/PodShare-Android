package com.aueb.podshare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.Sessions.PodsharerNameSharedPreference;
import com.aueb.podshare.adapter.EpisodeAdapter;
import com.aueb.podshare.classes.Episode;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;
import com.aueb.podshare.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class MyMediaPlayerFragment extends Fragment implements Playable {

    public static String TAG = "MEDIA PLAYER";
    int position = 0;
    boolean isPlaying = false;
    TextView elapsedTime;
    TextView remainingTime;
    SeekBar seekBar;
    Runnable runnable;
    MediaPlayer mediaPlayer;
    Button next;
    Button previous;

    /*BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             String action = intent.getExtras().getString("action_name");

             switch (action) {
                 case MyNotification.ACTION_PREVIOUS:
                     onEpisodePrevious(indexCurrentAudioPlaying - 1);
                     break;
                 case MyNotification.ACTION_PLAY:
                     if (mediaPlayer.isPlaying()) {
                         onEpisodePause();
                     } else {
                         onEpisodePlay();
                     }
                     break;
                 case MyNotification.ACTION_NEXT:
                     onEpisodeNext(indexCurrentAudioPlaying + 1);
                     break;
             }
         }
     };
     */
    private ProgressDialog progressDialog;
    //NotificationManager notificationManager;
    private Button playButton;
    private ListView episodesList;
    private ArrayList<Episode> podcastEpisodes = new ArrayList<>();
    private ArrayList<String> episodeTitles = new ArrayList<>();
    private ArrayList<String> episodeDescriptions = new ArrayList<>();
    private User user;
    private byte[] podcastImage;
    private ArrayList<Uri> episodesAudioUri;
    private Bitmap audioPlay;
    private int indexCurrentAudioPlaying;
    private String episode_title;

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

    public MyMediaPlayerFragment(User user, byte[] podcastImage, ArrayList<Uri> uris, int index) {
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
        TextView podsharerText = view.findViewById(R.id.podsharer_play_text);
        podsharerText.setText(podsharer.getSession());
        TextView podcastText = view.findViewById(R.id.podcast_name_play);
        podcastText.setText(podcast.getSession());
        TextView descriptionEpisode = view.findViewById(R.id.description_play);
        descriptionEpisode.setText(episodeDescription.getSession());
        Bitmap bitmap = BitmapFactory.decodeByteArray(podcastImage, 0, podcastImage.length);
        LinearLayout lin = view.findViewById(R.id.play_inside);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        lin.setBackground(bitmapDrawable);

        next = view.findViewById(R.id.next);
        previous = view.findViewById(R.id.previous);
        seekBar = view.findViewById(R.id.progressBar);
        playButton = view.findViewById(R.id.playButton);
        elapsedTime = view.findViewById(R.id.elapsedTime);
        remainingTime = view.findViewById(R.id.remainingTime);
        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), episodesAudioUri.get(indexCurrentAudioPlaying));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        TextView durationView = view.findViewById(R.id.duration);
        durationView.setText(createTimeLabel(mediaPlayer.getDuration()));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                playCycle();
                mediaPlayer.start();
                //MyNotification.createNotification(getActivity(), "koko", R.drawable.pause, position, 1);
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
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("EPISODES_EPISODES"));
            getActivity().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));
        }*/

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (episodeDescriptions.size() > 0) {
                    if (indexCurrentAudioPlaying == 0) {
                        EpisodeNameSharedPreference name = new EpisodeNameSharedPreference(getContext());
                        name.saveSession(episodeTitles.get(episodeTitles.size() - 1));
                        EpisodeDescriptionSharedPreference description = new EpisodeDescriptionSharedPreference(getContext());
                        description.saveSession(episodeDescriptions.get(episodeDescriptions.size() - 1));
                        onEpisodePrevious(podcastEpisodes.size() - 1);
                    } else {
                        EpisodeNameSharedPreference name = new EpisodeNameSharedPreference(getContext());
                        name.saveSession(episodeTitles.get(indexCurrentAudioPlaying - 1));
                        EpisodeDescriptionSharedPreference description = new EpisodeDescriptionSharedPreference(getContext());
                        description.saveSession(episodeDescriptions.get(indexCurrentAudioPlaying - 1));
                        onEpisodePrevious(indexCurrentAudioPlaying - 1);
                    }
                } else {
                    onEpisodePrevious(indexCurrentAudioPlaying);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
                //MyNotification.createNotification(getActivity(), "loko", R.drawable.pause, position, 1);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (episodeDescriptions.size() > 0) {
                    if (indexCurrentAudioPlaying == podcastEpisodes.size() - 1) {
                        EpisodeNameSharedPreference name = new EpisodeNameSharedPreference(getContext());
                        name.saveSession(episodeTitles.get(0));
                        EpisodeDescriptionSharedPreference description = new EpisodeDescriptionSharedPreference(getContext());
                        description.saveSession(episodeDescriptions.get(0));
                        onEpisodeNext(0);
                    } else {
                        EpisodeNameSharedPreference name = new EpisodeNameSharedPreference(getContext());
                        name.saveSession(episodeTitles.get(indexCurrentAudioPlaying));
                        EpisodeDescriptionSharedPreference description = new EpisodeDescriptionSharedPreference(getContext());
                        description.saveSession(episodeDescriptions.get(indexCurrentAudioPlaying));
                        onEpisodeNext(indexCurrentAudioPlaying + 1);
                    }
                } else {
                    onEpisodeNext(indexCurrentAudioPlaying);
                }
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
            if (!podcastEpisodes.get(i).get_name().equals(episode.getSession())) {
                episodeTitles.add(podcastEpisodes.get(i).get_name());
                episodeDescriptions.add(podcastEpisodes.get(i).get_description());
            }
        }

        EpisodeAdapter episodeAdapter = new EpisodeAdapter(episodeTitles, podcastEpisodes, getActivity());

        episodesList = view.findViewById(R.id.other_episodes_list);
        episodesList.setAdapter(episodeAdapter);

        episodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.episode_name_txt)).getText().toString();

                Toast toast = Toast.makeText(getContext(), selected, Toast.LENGTH_SHORT);
                toast.show();
                EpisodeNameSharedPreference episode = new EpisodeNameSharedPreference(getContext());
                EpisodeDescriptionSharedPreference description = new EpisodeDescriptionSharedPreference(getContext());
                String descriptionEp = ((TextView) view.findViewById(R.id.episode_description)).getText().toString();
                description.saveSession(descriptionEp);
                episode.saveSession(selected);
                int index = 0;
                ArrayList<Episode> episodes = getPodcast().getEpisodes();
                for (int i = 0; i < episodes.size(); i++) {
                    Log.d(TAG, "Inside for: " + i);
                    if (episode.getSession().equals(episodes.get(i).get_name())) {
                        index = i;
                        Log.d(TAG, "Found episode selected index: " + i);
                    }
                    if (i == episodes.size() - 1) {
                        loadFragment(new MyMediaPlayerFragment(user, podcastImage, episodesAudioUri, index));
                    }
                }

            }
        });

        return view;
    }

    private Podcast getPodcast() {
        PodcastNameSharedPreference podcastSession = new PodcastNameSharedPreference(getContext());
        String podcastName = podcastSession.getSession();
        ArrayList<Podcast> podcasts = user.getPodcasts();
        Podcast podcast = null;
        for (Podcast pod : podcasts) {
            if (pod.getName().equals(podcastName)) {
                podcast = pod;
            }
        }
        return podcast;
    }
/*
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

 */

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

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

    public void playAudio() {
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
        playAudio();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        handler.removeCallbacks(runnable);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);*/
    }

    @Override
    public void onEpisodePrevious(int i) {
        position--;
        /*MyNotification.createNotification(getActivity(), "Nico", R.drawable.pause, position, episodes.size() - 1);*/
        loadFragment(new MyMediaPlayerFragment(user, podcastImage, episodesAudioUri, i));
    }

    @Override
    public void onEpisodePlay() {
        onResume();
    }

    @Override
    public void onEpisodePause() {
        onPause();
    }

    @Override
    public void onEpisodeNext(int i) {
        position++;
        /*MyNotification.createNotification(getActivity(), "Rico", R.drawable.play, position, 2);*/
        loadFragment(new MyMediaPlayerFragment(user, podcastImage, episodesAudioUri, i));
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
