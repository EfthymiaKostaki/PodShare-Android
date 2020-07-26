package com.aueb.podshare.database;

public class UserDAO {
    public String username;
    public String email;
    private String uid;
    private String description;

    public UserDAO() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserDAO(String email, String username) {

        this.email = email;
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
