package com.aueb.podshare.database;

import com.aueb.podshare.classes.Podcast;

import java.util.ArrayList;

public class UserDAO {
    public String username;
    public String email;
    private String uid;

    public UserDAO() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserDAO(String email, String username) {

        this.email = email;
        this.username = username;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
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
}
