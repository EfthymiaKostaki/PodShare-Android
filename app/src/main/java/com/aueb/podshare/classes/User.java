package com.aueb.podshare.classes;

import java.util.ArrayList;

public class User {

    public String username;
    public String email;
    public String description;
    public ArrayList<Podcast> podcasts;
    private String uid;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username, String description, String uid) {
        this.description = description;
        this.email = email;
        this.username = username;
        this.uid = uid;
        podcasts = new ArrayList<>();
    }


    public User(String email, String username) {

        this.email = email;
        this.username = username;
        podcasts = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

}
