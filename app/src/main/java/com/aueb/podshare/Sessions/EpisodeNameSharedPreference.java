package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

import com.aueb.podshare.classes.User;

public class EpisodeNameSharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "episode_name";
    String EPISODE_NAME_KEY = "episode_name";

    public EpisodeNameSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String episode_name) {
        editor.putString(EPISODE_NAME_KEY, episode_name).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(EPISODE_NAME_KEY, null);
    }

    public void terminateSession() {
        editor.putString(EPISODE_NAME_KEY, null).commit();
    }
}
