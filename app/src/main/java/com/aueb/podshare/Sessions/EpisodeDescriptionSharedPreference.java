package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class EpisodeDescriptionSharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "episode_description";
    String EPISODE_DESCRIPTION_KEY = "episode_description";

    public EpisodeDescriptionSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String episode_description) {
        editor.putString(EPISODE_DESCRIPTION_KEY, episode_description).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(EPISODE_DESCRIPTION_KEY, null);
    }

    public void terminateSession() {
        editor.putString(EPISODE_DESCRIPTION_KEY, null).commit();
    }
}
