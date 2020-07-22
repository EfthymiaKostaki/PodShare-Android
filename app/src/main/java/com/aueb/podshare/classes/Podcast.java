package com.aueb.podshare.classes;

import java.util.Date;
import java.util.ArrayList;

public class Podcast {

    private String name;
    private String description;
    public String url;
    private String imagePath;
    private int subscriptions;
    private int likes;
    private int favourites;
    private int views;
    private Date pub_date;
    private ArrayList<Episode> episodes;

    public Podcast(String name, String description, Date pub_date) {
        this.name = name;
        this.description = description;
        episodes = new ArrayList();
        subscriptions = 0;
        likes = 0;
        favourites = 0;
        views = 0;
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

    public void add_subscription() {
        subscriptions += 1;
    }

    public void remove_subscription() {
        subscriptions -= 1;
    }

    public int get_subscriptions() {
        return subscriptions;
    }

    public void set_likes(int likes) {
        this.likes = likes;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public int get_likes() {
        return likes;
    }

    public void set_favourites(int favs) {
        favourites = favs;
    }

    public int get_favourites() {
        return favourites;
    }

    public void set_views(int views) {
        this.views = views;
    }

    public int get_views() {
        return views;
    }

    public Date getPub_date() {
        return pub_date;
    }

}