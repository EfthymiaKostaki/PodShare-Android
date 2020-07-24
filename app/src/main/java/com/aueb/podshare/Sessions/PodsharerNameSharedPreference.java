package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class PodsharerNameSharedPreference {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "podsharer_name";
    String PODSHARER_NAME_KEY = "podsharer_name";

    public PodsharerNameSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String podsharer_name) {
        editor.putString(PODSHARER_NAME_KEY, podsharer_name).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(PODSHARER_NAME_KEY, null);
    }

    public void terminateSession() {
        editor.putString(PODSHARER_NAME_KEY, null).commit();
    }


}
