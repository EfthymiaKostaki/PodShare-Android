package com.aueb.podshare.classes;

public class Podcast {

    public String title;
    public String noOfEpisodes;

    public Podcast() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Podcast(String title, String noOfEpisodes) {

        this.title = title;
        this.noOfEpisodes = noOfEpisodes;
    }
}
