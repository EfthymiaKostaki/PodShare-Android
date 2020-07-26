package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class PodcastNameSharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "podcast_name";
    String SESSION_KEY = "podcast_name";

    public PodcastNameSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String podcast_name) {
        editor.putString(SESSION_KEY, podcast_name).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(SESSION_KEY, null);
    }

    public void terminateSession() {
        editor.putString(SESSION_KEY, null).commit();
    }
}
