package com.aueb.podshare.classes;

import java.util.ArrayList;

public class User {

    public String username;
    public String email;
    public ArrayList<Podcast> podcasts;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username) {

        this.email = email;
        this.username = username;
        podcasts = new ArrayList<>();
    }

    public void addPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }
}
