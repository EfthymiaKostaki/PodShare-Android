package com.aueb.podshare;

public interface Playable {
    void onEpisodePrevious(int i);

    void onEpisodePlay();

    void onEpisodePause();

    void onEpisodeNext(int i);
}
