package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class ImageSharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME= "image";
    String SESSION_KEY = "image";

    public ImageSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String image) {
        editor.putString(SESSION_KEY, image).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(SESSION_KEY, null);
    }

    public void terminateSession() {
        editor.putString(SESSION_KEY, null).commit();
    }
}
