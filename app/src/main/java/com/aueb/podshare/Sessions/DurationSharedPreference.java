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

    public void saveSession(String duration) {
        editor.putString(DURATION_KEY, duration).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(DURATION_KEY, null);
    }

    public void terminateSession() {
        editor.putString(DURATION_KEY, null).commit();
    }
}
