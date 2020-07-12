package com.aueb.podshare.classes;

public class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username) {

        this.email = email;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
