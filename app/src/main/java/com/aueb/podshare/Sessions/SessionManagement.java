package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

import com.aueb.podshare.classes.User;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME= "session";
    String SESSION_KEY = "session_user";

    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String email) {
        editor.putString(SESSION_KEY, email).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(SESSION_KEY, null);
    }

    public void terminateSession() {
        editor.putString(SESSION_KEY, null).commit();
    }
}
