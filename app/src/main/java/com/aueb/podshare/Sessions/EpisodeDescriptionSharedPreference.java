package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class EpisodeDescriptionSharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME= "session";
    String SESSION_KEY = "episode_name";

    public EpisodeDescriptionSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String episode_description) {
        editor.putString(SESSION_KEY, episode_description).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(SESSION_KEY, null);
    }

    public void terminateSession() {
        editor.putString(SESSION_KEY, null).commit();
    }
}
