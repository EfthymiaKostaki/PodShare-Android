package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class PrivacySharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "privacy";
    String PRIVACY_KEY = "privacy";

    public PrivacySharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String privacy) {
        editor.putString(PRIVACY_KEY, privacy).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(PRIVACY_KEY, null);
    }

    public void terminateSession() {
        editor.putString(PRIVACY_KEY, null).commit();
    }
}
