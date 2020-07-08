package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class AudioSharedPreference {
    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor1;
    SharedPreferences sharedPreferences2;
    SharedPreferences.Editor editor2;
    String SHARED_PREF_NAME= "audio";
    String SESSION_KEY = "audio";
    String SHARED_PREF_AUD = "audio_extension";
    String AUDIO_KEY = "audio_extension";

    public AudioSharedPreference(Context context) {
        sharedPreferences1 = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();
        sharedPreferences2 = context.getSharedPreferences(SHARED_PREF_AUD, Context.MODE_PRIVATE);
        editor2 = sharedPreferences2.edit();
    }

    public void saveSession(String audio, String audio_extension) {
        editor1.putString(SESSION_KEY, audio).commit();
        editor2.putString(AUDIO_KEY, audio).commit();
    }

    public String getSession() {
        return sharedPreferences1.getString(SESSION_KEY, null);
    }

    public String getAudioExtension(){ return sharedPreferences2.getString(AUDIO_KEY, null); }

    public void terminateSession() {
        editor1.putString(SESSION_KEY, null).commit();
        editor2.putString(AUDIO_KEY, null).commit();
    }
}
