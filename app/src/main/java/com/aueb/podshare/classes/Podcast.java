package com.aueb.podshare.classes;

import java.util.Date;
import java.util.ArrayList;

public class Podcast {

    private String name;
    private String description;
    private String imagePath;
    private int subscriptions;
    private User publisher;
    private Date pub_date;
    private ArrayList<Episode> episodes;

    public Podcast() {

    }

    public Podcast(String name, String description, User publisher, Date pub_date) {
        this.name = name;
        this.description = description;
        episodes = new ArrayList();
        subscriptions = 0;
        this.publisher = publisher;
        this.pub_date = pub_date;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

    public void removeEpisode() {

    }

    public int get_number_of_episodes() {
        return episodes.size();
    }

    public int get_likes() {
        int likes = 0;
        for (Episode episode : episodes) {
            likes += episode.get_likes();
        }
        return likes;
    }

    public int get_favourites() {
        int favourites = 0;
        for (Episode episode : episodes) {
            favourites += episode.get_favourites();
        }
        return favourites;
    }

    public int get_totalViews(){
        int views = 0;
        for (Episode episode : episodes) {
            views += episode.get_views();
        }
        return views;
    }

    public void add_subscription() {
        subscriptions += 1;
    }

    public int get_subscriptions() {
        return subscriptions;
    }

    public User getPublisher() {
        return publisher;
    }

    public Date getPub_date() {
        return pub_date;
    }

}
