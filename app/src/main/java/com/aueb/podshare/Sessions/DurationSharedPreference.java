package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class DurationSharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "duration";
    String DURATION_KEY = "duration";

    public DurationSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(int duration) {
        editor.putInt(DURATION_KEY, duration).commit();
    }

    public int getSession() {
        return sharedPreferences.getInt(DURATION_KEY, -1);
    }

    public void terminateSession() {
        editor.putInt(DURATION_KEY, -1).commit();
    }
}
