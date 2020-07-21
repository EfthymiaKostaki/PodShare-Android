package com.aueb.podshare.classes;

import java.util.Date;
import java.time.Duration;
import java.util.*;

public class Episode {
    private String name;
    private String description;
    private String episodeLink;
    private Podcast podcast;
    /* When privacy is true it means that episode is private */
    private boolean privacy;
    private int favourites;
    private int likes;
    private int views;
    private Date pub_date;
    private String duration;

    public Episode(String name, String description) {
        if (name.trim().equals("")) {
            this.name = "No name";
        }
        this.name = name;
        this.description = description;
        favourites = 0;
        likes = 0;
        views = 0;
        pub_date = Calendar.getInstance().getTime();
    }

    public String get_name() {
        return name;
    }

    public String get_description() {
        return description;
    }

    public void belongsTo(Podcast podcast) {
        this.podcast = podcast;
    }

    public Podcast get_podcast() {
        return podcast;
    }

    public void gain_view() {
        views += 1;
        podcast.set_views(views);
    }

    public int get_views() {
        return views;
    }

    public void set_privacy(boolean privacy) {
        this.privacy = privacy;
    }

    public boolean get_privacy() {
        return privacy;
    }

    public void set_duration(String dur) {
        duration = dur;
    }

    public void increase_favourites() {
        favourites += 1;
        podcast.set_favourites(favourites);
    }

    public void decrease_favourites() {
        favourites -= 1;
        podcast.set_favourites(favourites);
    }

    public int get_favourites() {
        return favourites;
    }

    public void increase_likes() {
        likes += 1;
        podcast.set_likes(likes);
    }

    public void decrease_likes() {
        likes -= 1;
        podcast.set_likes(likes);
    }

    public int get_likes() {
        return likes;
    }

    public Date get_pubDate() {
        return pub_date;
    }

    public String get_duration() {
        return duration;
    }

    public void setEpisodeLink(String link) {
        episodeLink = link;
    }

}
